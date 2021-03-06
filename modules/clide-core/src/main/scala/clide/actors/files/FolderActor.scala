/*             _ _     _                                                      *\
**            | (_)   | |                                                     **
**         ___| |_  __| | ___      clide 2                                    **
**        / __| | |/ _` |/ _ \     (c) 2012-2013 Martin Ring                  **
**       | (__| | | (_| |  __/     http://clide.flatmap.net                   **
**        \___|_|_|\__,_|\___|                                                **
**                                                                            **
**   This file is part of Clide.                                              **
**                                                                            **
**   Clide is free software: you can redistribute it and/or modify            **
**   it under the terms of the GNU Lesser General Public License as           **
**   published by the Free Software Foundation, either version 3 of           **
**   the License, or (at your option) any later version.                      **
**                                                                            **
**   Clide is distributed in the hope that it will be useful,                 **
**   but WITHOUT ANY WARRANTY; without even the implied warranty of           **
**   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            **
**   GNU General Public License for more details.                             **
**                                                                            **
**   You should have received a copy of the GNU Lesser General Public         **
**   License along with Clide.                                                **
**   If not, see <http://www.gnu.org/licenses/>.                              **
\*                                                                            */
package clide.actors.files

import akka.actor._
import clide.models.ProjectInfo
import java.io.File
import akka.actor.ActorLogging
import clide.models._
import clide.actors._
import clide.persistence.DBAccess
import scala.slick.session.Session

/**
 * @author Martin Ring <martin.ring@dfki.de>
 */
private[actors] object FolderActor {
  def props(project: ProjectInfo, parent: Option[FileInfo], name: String)(implicit dbAccess: DBAccess) =
    Props(classOf[FolderActor], project, parent, name, dbAccess)
}

/**
 * Watches and manages a Folder
 *
 * @author Martin Ring <martin.ring@dfki.de>
 **/
private[actors] class FolderActor(project: ProjectInfo, parent: Option[FileInfo], name: String)(implicit val dbAccess: DBAccess) extends Actor with ActorLogging with FileEventSource {
  import dbAccess.schema._
  import dbAccess.{db => DB}
  import Messages._
  import Events._

  val fullPath = parent.map(_.path).getOrElse("") + File.pathSeparator + name

  var info:     FileInfo      = null
  var children: Map[Long,FileInfo] = Map()
  def file:     File          = new File(project.root + info.path.mkString(File.pathSeparator)) // TODO

  def getFolder(name: String) = context.child(name).getOrElse{
    context.actorOf(FolderActor.props(project, Some(info), name),name) }

  def getFile(name: String) = context.child(name).getOrElse{
    context.actorOf(FileActor.props(project, info, name),name) }

  def getExisting(name: String) = context.child(name).orElse {
    children.values.find(_.path.last == name).map { i =>
      if (i.isDirectory) getFolder(name)
      else getFile(name) }
  }

  def receiveMessages: Receive = {
    case e @ FileCreated(file) =>
      triggerFileEvent(e)
      if (file.parent.map(_ == info.id).getOrElse(false)) {
        log.info("adding to children")
        children += file.id -> file
      }
    case e @ FileDeleted(file) =>
      triggerFileEvent(e)
      if (file.parent.map(_ == info.id).getOrElse(false)) {
        log.info("removing child")
        children -= file.id
      }
    case WithPath(Seq(), msg) => receiveMessages(msg)
    case WithPath(Seq(name), Delete) =>
      getExisting(name).map(_.forward(Delete))
    case WithPath(Seq(name), msg@Messages.internal.OpenFile(_)) =>
      getFile(name).forward(msg)
    case WithPath(Seq(name), TouchFile) =>
      getFile(name).forward(TouchFile)
    case WithPath(Seq(name), msg@Edit(_,_,_)) =>
      getFile(name).forward(msg)
    case WithPath(Seq(name), msg@Annotate(_,_,_,_)) =>
      getFile(name).forward(msg)
    case WithPath(Seq(name,tail@_*), ExplorePath) =>
      getExisting(name).fold{
        receiveMessages(BrowseFolder)
      }( _.forward(WithPath(tail,ExplorePath) ))
    case WithPath(Seq(head,tail@_*), msg) =>
      getFolder(head).forward(WithPath(tail, msg))

    case NewFile =>
      def findName(n: Int = 1): String = {
        val name = if (n > 1) "unnamed" + n else "unnamed"
        if (children.values.exists(_.path.last == name))
          findName(n+1)
        else name
      }
      val name = findName()
      log.info(s"creating new file: $name")
      getFile(findName()) ! NewFile
    case ExplorePath => receiveMessages(BrowseFolder)
	case BrowseFolder =>
	  log.info(children.toString)
      sender ! FolderContent(info,children.values.toList)
	case TouchFolder =>
	  // Touched
    case Delete =>
      context.children.foreach { child =>
        // We unregister from our children's events in order to
        // get just one event triggered for the deletion of this
        // folder
        child ! Unregister
        // Now we can propagate the deletion to all of our children
        // which still means, that their event listeners receive
        // a delete event.
        context.children.foreach(_ ! Delete)
      }
      // In many cases we will not be able to delete the file on
      // the disk right now. For those cases we have to mark the
      // file as deleted and postpone the deletion. Otherwise we
      // can remove all evidences right away.
      info = info.copy(deleted = true)      
      if (!file.delete()) {
        DB.withSession { implicit session: Session => FileInfos.update(info) }
        file.deleteOnExit() // HACK: Schedule deletion instead
      } else {
        DB.withSession { implicit session: Session => FileInfos.delete(info) }
      }
      triggerFileEvent(FileDeleted(info))
      context.stop(self)
  }

  def receive = receiveMessages orElse receiveFileEvents

  override def preRestart(reason:Throwable, message:Option[Any]){
    log.error(reason, "Unhandled exception for message: {}", message)
  }

  override def preStart = {
    initFileEventSource()    
    DB.withSession { implicit session: Session =>
      FileInfos.get(project, parent.map(_.path :+ name).getOrElse(Seq.empty)) match {
        case None => // The file did not previously exist
          info = FileInfos.create(
            project = project.id,
            path    = parent.map(_.path :+ name).getOrElse(Seq.empty),
            mimeType = None,
            deleted = false,
            exists  = false,
            isDirectory = true,
            parent = parent.map(_.id)
          )
          log.info("created file " + info)
          triggerFileEvent(FileCreated(info))
        case Some(info) =>
          if (info.deleted) { // The file was in a deleted state
            this.info = info.copy(deleted = false)
            FileInfos.update(this.info)
            log.info("created file " + info)
            triggerFileEvent(FileCreated(info))
          } else {
            this.info = info
          }
          this.children = FileInfos.getChildren(info.id).map(i => i.id -> i).toMap
          log.info(this.children.toString)
      }
    }
  }
}
