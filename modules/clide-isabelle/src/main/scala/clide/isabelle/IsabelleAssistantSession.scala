 /*            _ _     _                                                      *\
 **           | (_)   | |                                                     **
 **        ___| |_  __| | ___      clide 2                                    **
 **       / __| | |/ _` |/ _ \     (c) 2012-2013 Martin Ring                  **
 **      | (__| | | (_| |  __/     http://clide.flatmap.net                   **
 **       \___|_|_|\__,_|\___|                                                **
 **                                                                           **
 **  This file is part of Clide.                                              **
 **                                                                           **
 **  Clide is free software: you can redistribute it and/or modify            **
 **  it under the terms of the GNU General Public License as published by     **
 **  the Free Software Foundation, either version 3 of the License, or        **
 **  (at your option) any later version.                                      **
 **                                                                           **
 **  Clide is distributed in the hope that it will be useful,                 **
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of           **
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            **
 **  GNU General Public License for more details.                             **
 **                                                                           **
 **  You should have received a copy of the GNU General Public License        **
 **  along with Clide.  If not, see <http://www.gnu.org/licenses/>.           **
 \*                                                                           */

//package clide.isabelle
//
//import akka.actor.Props
//import clide.assistants._
//import clide.collaboration.Operation
//import clide.models._
//import isabelle._
//import isabelle.Options
//
//class IsabelleAssistantSession(project: ProjectInfo) extends AssistantSession(project: ProjectInfo) {
//  var session: Session = null
//
//  var thys = Map[Document.Node.Name,OpenedFile]()
//
//  def startup() {
//    val ops = isabelle.Options.init
//    log.info("building session content")
//    val content = Build.session_content(ops, false, Nil, "HOL")
//    session = new Session(new isabelle.Thy_Load(content.loaded_theories, content.syntax) {
//      override def append(dir: String, source_path: Path): String = {
//        log.info("thy_load.append({}, {})", dir, source_path)
//        val path = source_path.expand
//        if (path.is_absolute) Isabelle_System.platform_path(path)
//        else {
//          (Path.explode(dir) + source_path).expand.implode
//        }
//      }
//      override def with_thy_text[A](name: Document.Node.Name, f: CharSequence => A): A = {
//        log.info("thy_load.with_thy_text({},{})", name, f)
//        thys.get(name).map(file => f(file.state)).getOrElse {
//          f("")
//        }
//      }
//      override def text_edits(reparse_limit: Int, previous: Document.Version, edits: List[Document.Edit_Text]) = {
//        val result = super.text_edits(reparse_limit, previous, edits)
//        log.info("text_edits = {}", result)
//        result
//      }
//    })
//    session.phase_changed += { p => p match {
//      case Session.Startup  => chat("I'm starting up, please wait a second!")
//      case Session.Shutdown => chat("I'm shutting down")
//      case Session.Inactive => // TODO: Set inactive
//      case Session.Failed   => chat("Sorry, something failed")
//      case Session.Ready    =>
//        chat("I'm ready to go!")
//        session.update_options(ops)
//        self ! AssistantSession.Activate
//    } }
//    session.syslog_messages += { msg => chat(XML.content(msg.body)) }
//    session.raw_output_messages += { msg =>
//      log.info("OUTPUT: {}", XML.content(msg.body))
//    }
//    session.start(List("-S","HOL"))
//  }
//
//  def fileAdded(file: OpenedFile) {
//    log.info("{}: {}", file.info.path, file.info.mimeType)
//    if (file.info.mimeType == Some("text/x-isabelle")) {
//      val model = context.actorOf(Props(classOf[IsabelleDocumentModel], this.peer, project, session), "file" + file.info.id)
//      registerDocumentModel(file.info.id, model)
//    }
//  }
//
//  def fileChanged(file: OpenedFile, change: Operation, after: OpenedFile) = {
//
//  }
//
//  def fileClosed(file: OpenedFile) = {
//
//  }
//
//  def shutdown() {
//    session.stop()
//  }
//}