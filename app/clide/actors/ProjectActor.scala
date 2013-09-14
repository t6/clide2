package clide.actors

import akka.actor._
import clide.models._
import play.api.Play.current
import play.api.db.slick._
import scala.slick.driver.H2Driver.simple._
import clide.actors.files._

class ProjectActor(var info: ProjectInfo) extends Actor with ActorLogging {
  import clide.actors.Messages._
  import clide.actors.Events._
    
  var root: ActorRef    = context.system.deadLetters  
  
  def admin: Receive = {
    case DeleteProject =>
      DB.withSession { implicit session: Session =>
        ProjectInfos.delete(info.id)
      }
      sender         ! DeletedProject(info)
      context.parent ! DeletedProject(info)
      context.stop(self)
  }
  
  def write: Receive = {
    case StartFileBrowser =>
      val browser = context.actorOf(Props(classOf[FileBrowser],true,root))
      browser.forward(StartFileBrowser)
  }
  
  def read: Receive = {
    case StartFileBrowser =>
      val browser = context.actorOf(Props(classOf[FileBrowser],false,root))
      browser.forward(StartFileBrowser)
  }
  
  def none: Receive = {
    case _ => sender ! NotAllowed
  }
  
  def receive = {
    case WrappedProjectMessage(level,StartFileBrowser) =>
      context.actorOf(Props(classOf[FileBrowser],level,root)).forward(StartFileBrowser)
    case WrappedProjectMessage(level,msg) => level match {
      case ProjectAccessLevel.Admin =>
        (admin orElse write orElse read orElse none)(msg)
      case ProjectAccessLevel.Write =>
        (write orElse read orElse none)(msg)
      case ProjectAccessLevel.Read =>
        (read orElse none)(msg)
      case _ =>
        none(msg)
    }
  }
  
  override def preStart() {
    root = context.actorOf(Props(classOf[FolderActor], info, None, "files"),"files")
    log.info(s"project ${info.owner}/${info.name}")
  }
}