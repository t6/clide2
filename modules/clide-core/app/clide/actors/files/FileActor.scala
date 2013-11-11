 /*            _ _     _                                                      *\
 **           | (_)   | |                                                     **
 **        ___| |_  __| | ___      clide 2                                    **
 **       / __| | |/ _` |/ _ \     (c) 2012-2013 Martin Ring                  **
 **      | (__| | | (_| |  __/     http://clide.flatmap.net                   **
 **       \___|_|_|\__,_|\___|                                                **
 \*                                                                           */

package clide.actors.files

import akka.actor._
import clide.collaboration._
import clide.models._
import java.io.File
import clide.actors._
import scala.collection.mutable.{Buffer,Map}
import scala.util.{Failure,Success}
import clide.Core.DB
import clide.Core.DAL._
import clide.Core.DAL.profile.simple._ // TODO: MOVE ALL TO SCHEMA

private[actors] object FileActor {
  def apply(project: ProjectInfo, parent: FileInfo, name: String) = 
    Props(classOf[FileActor], project, parent, name)
}

/**
 * @author Martin Ring <martin.ring@dfki.de>
 */
private[actors] class FileActor(project: ProjectInfo, parent: FileInfo, name: String) extends Actor 
                                                                         with ActorLogging
                                                                         with FileEventSource {
  import Messages._
  import Events._  
    
  var info: FileInfo = null
  def file = new File(project.root + info.path.mkString(File.pathSeparator)) // TODO    
  
  var otActive = false
  val clients = Map[ActorRef,SessionInfo]()
  var server: Server = null
  
  val annotations = Map[(Long,String),(Long,Annotations)]()
  
  def initOt() {
    log.info("initializing ot")
    DB.withSession { implicit session: Session => 
      val revs = Revisions.get(info.id).toSeq
      server = new Server(Document(""))
      log.info(f"reapplying ${revs.length} operations")
      for ((op,rev) <- revs.zipWithIndex) {
        server.applyOperation(op,rev) match {
          case Success(o) => // <- good            
          case Failure(e) => // <- bad ;) TODO
            log.error(s"unable to apply revision $op (${e.getMessage}")
        }
      }
      log.info(server.text)
      otActive = true
    }    
  }    
  
  def resendAnnotations(receiver: ActorRef) = {
    annotations.foreach {
      case ((u,n),(r,a)) =>
        receiver ! Annotated(info.id, u, server.transformAnnotation(r.toInt, a).get, n)
    }
  }
  
  def receiveMessages: Receive = {
    case WithPath(Seq(),any) => receiveMessages(any)
    
    case ExplorePath =>
      context.parent.forward(BrowseFolder)
      
    case Messages.internal.OpenFile(user) =>
      log.info(s"opening for ${user.user}")
      if (!otActive) {
        try {
          initOt()
          clients += sender -> user
          context.watch(sender)
          sender ! Events.internal.OTState(this.info, server.text, server.revision)
          resendAnnotations(sender)
        }
        catch {
          case e: Throwable =>
            log.error(e, "init failed")            
            sender ! FileInitFailed(this.info.id)
        }
      } else {
        clients += sender -> user
        context.watch(sender)
        sender ! Events.internal.OTState(this.info, server.text, server.revision)
        resendAnnotations(sender)
      }
      
    case Annotate(_,rev,as,name) =>      
      if (!otActive) initOt()
      server.transformAnnotation(rev.toInt, as) match { // TODO: Ugly: rev.toInt
        case Failure(e) =>
          // TODO
          log.warning("{}'s annotation could not be transformed", clients(sender).user)
        case Success(as) =>
          annotations.get((clients(sender).id,name)) match {
            case None =>              
            case Some((oldRev,oldA)) =>
              server.transformAnnotation(oldRev.toInt, oldA).toOption match {
                case None      => 
                  log.warning("couldnt transform old annotations")                  
                  // TODO: Handle failure here
                case Some(old) =>
                  //val diff = AnnotationDiff.diff(old.annotations, a.annotations)
                  //log.info("diff for ({},{}): {}", clients(sender).id, name, diff)
                  //if (old.annotations != a.annotations)                    
                    //clients.keys.filter(_ != sender).foreach(_ ! AnnotationChanged(info.id,clients(sender).id,diff,name))                  
              }                                         
          }          
          clients.keys.filter(_ != sender).foreach(_ ! Annotated(info.id,clients(sender).id,as,name))
          annotations((clients(sender).id,name)) = (server.revision,as)
      }
      
    case Edit(_,rev,ops) =>
      if (!otActive) initOt()         
      server.applyOperation(ops,rev) match {
        case Failure(e) =>
          // TODO
          log.warning("edit couldnt be applied")
        case Success(o) =>          
          DB.withSession { implicit session: Session => Revisions.insert(Revision(info.id,server.revision,o)) }
          clients.keys.filter(_ != sender).foreach(_ ! Edited(info.id, o))
          sender ! AcknowledgeEdit(info.id)
      }      
      
    case TouchFile =>
      //
      
    case Delete => // TODO !!      
      info = info.copy(deleted = true)
      val q = FileInfos.get(info.id)
      if (info.exists && !file.delete()) {                  
        DB.withSession { implicit session: Session => q.update(info) }
        file.deleteOnExit() // HACK: Schedule deletion instead
      } else {
        DB.withSession { implicit session: Session => q.delete }
      }
      triggerFileEvent(FileDeleted(info))      
      context.stop(self)
      
    case Terminated(ref) =>
      clients -= ref
  }
  
  def receive = receiveMessages orElse receiveFileEvents
  
  override def preRestart(reason:Throwable, message:Option[Any]){
    log.error(reason, "Unhandled exception for message: {}", message)
  }
  
  override def preStart() {
    initFileEventSource()
    val q = FileInfos.get(project, parent.path :+ name) 
    DB.withSession { implicit session: Session =>
      q.firstOption match {
        case None => // The file did not previously exist
          info = FileInfos.create(
            project = project.id,
            path    = parent.path :+ name,
            mimeType = name.split('.').toList.last match { // TODO: Move to config
              case "thy"    => Some("text/x-isabelle")
              case "scala"  => Some("text/x-scala")
              case "hs"     => Some("text/x-haskell")
              case "java"   => Some("text/x-java")
              case "js"     => Some("text/javascript")
              case "coffee" => Some("text/x-coffeescript")
              case "css"    => Some("text/css")
              case "less"   => Some("text/x-less")
              case "md"     => Some("text/x-markdown")
              case "sql"    => Some("text/x-sql")
              case "txt"    => Some("text/plain")
              case _        => None
            }, 
            deleted = false,
            exists  = false,
            isDirectory = false,
            parent = Some(parent.id)
          )
          log.info("file created: " + info)
          triggerFileEvent(FileCreated(info))
        case Some(info) =>
          if (info.deleted) { // The file was in a deleted state
            this.info = info.copy(deleted = false)
            q.update(this.info)
            log.info("file created: " + info)
            triggerFileEvent(FileCreated(this.info))
          } else {
            this.info = info
          }
      }
    } 
  }
}
