package clide.plugins

import clide.models._
import isabelle._
import clide.collaboration.{Operation,Retain,Delete,Insert}
import clide.collaboration.Annotations
import akka.actor.ActorRef

class IsabelleDocumentModel(server: ActorRef, project: ProjectInfo, session: Session) extends DocumentModel(server, project) {  
  def path     = file.path.mkString("/")
    
  def nodeName = Document.Node.Name(
    path, project.root,
    file.path.lastOption.getOrElse("<unknown>"))
      
  def nodeHeader = 
    Exn.capture {
      session.thy_load.check_thy_text(nodeName, state)
    } match {
      case Exn.Res(header) => header
      case Exn.Exn(exn) => Document.Node.bad_header(Exn.message(exn))
    }
    
  def perspective = // TODO
    Text.Perspective.full
    
  def initEdits: List[(Document.Node.Name,Document.Node.Edit[Text.Edit,Text.Perspective])] = {
    val name = nodeName
    List(session.header_edit(name, nodeHeader),
         name -> Document.Node.Clear(),
         name -> Document.Node.Edits(List(Text.Edit.insert(0,state))),
         name -> Document.Node.Perspective(perspective))
  }
    
  def opToEdits(operation: Operation): List[Document.Edit_Text] = {
    val name = nodeName
    val (_,edits) = operation.actions.foldLeft((0,Nil : List[Text.Edit])) { 
      case ((i,edits),Retain(n)) => (i+n,edits)
      case ((i,edits),Delete(n)) => (i+n,Text.Edit.remove(i,Seq.fill(n)('-').mkString) :: edits)
      case ((i,edits),Insert(s)) => (i+s.length,Text.Edit.insert(i,s) :: edits)
    }
    List(session.header_edit(name, nodeHeader),
      name -> Document.Node.Edits[Text.Edit,Text.Perspective](edits),
      name -> Document.Node.Perspective(perspective))
  }
  
  def annotate: List[(String,Annotations)] = List(
    "isabelle" -> IsabelleMarkup.annotations(session.snapshot(nodeName,Nil)))  
  
  def changed(op: Operation) { // TODO
    session.update(opToEdits(op))
  }
  
  def initialize() {
    session.update(initEdits)
    session.commands_changed += { change =>
      if (change.nodes.contains(nodeName))
        self ! DocumentModel.Refresh
    }
  }
}