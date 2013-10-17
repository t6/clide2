package clide.isabelle

import akka.actor.ActorRef
import akka.actor.actorRef2Scala
import clide.assistants.DocumentModel
import clide.collaboration.Annotations
import clide.collaboration.Delete
import clide.collaboration.Insert
import clide.collaboration.Operation
import clide.collaboration.Retain
import clide.models.ProjectInfo
import isabelle.Document
import isabelle.Exn
import isabelle.Session
import isabelle.Text
import isabelle.Thy_Header
import isabelle.Thy_Load

class IsabelleDocumentModel(server: ActorRef, project: ProjectInfo, session: Session, thy_load: Thy_Load) extends DocumentModel(server, project) {  
  def nodeName = {
    val name = file.path.mkString("/")
    Thy_Header.thy_name(name).map { theory =>
      Document.Node.Name(name, project.root, theory)
    }
  }.get
  
  def nodeHeader: isabelle.Document.Node_Header = 
    Exn.capture {      
      val name = nodeName
      thy_load.check_header(name, thy_load.read_header(name))
    }
  
  val perspective = Text.Perspective.full
  
  def opToEdits(operation: Operation): List[Text.Edit] = {        
    val (_,edits) = operation.actions.foldLeft((0,Nil : List[Text.Edit])) { 
      case ((i,edits),Retain(n)) => (i+n,edits)
      case ((i,edits),Delete(n)) => (i+n,Text.Edit.remove(i,Seq.fill(n)('-').mkString) :: edits)
      case ((i,edits),Insert(s)) => (i+s.length,Text.Edit.insert(i,s) :: edits)
    }    
    edits
  }
  
  def annotate: List[(String,Annotations)] = {
    List("highlighting"  -> IsabelleMarkup.highlighting(session.snapshot(nodeName,Nil)),
         "substitutions" -> IsabelleMarkup.substitutions(state))
  }
    
  
  def changed(op: Operation) {
    val edits = opToEdits(op)
    log.info("sending edits: {}", edits)
    session.edit_node(nodeName, nodeHeader, perspective, edits)    
  }
  
  def initialize() {
    log.info("name: {}, header: {}", nodeName, nodeHeader)
    session.init_node(nodeName, nodeHeader, perspective, state)
    session.commands_changed += { change =>
      log.info("commands changed: {}", change)
      self ! DocumentModel.Refresh
    }
  }
}