package clide.ghc

import clide.collaboration.Annotate
import clide.collaboration.Annotation
import clide.collaboration.Annotations
import clide.collaboration.Plain
import scala.util.parsing.combinator.RegexParsers
import clide.collaboration.AnnotationType

object HaskellMarkup {
  object LineParser extends RegexParsers {
    def line: Parser[Int] = "[0-9]+".r ^^ { _.toInt }
	def ch:   Parser[Int] = "[0-9]+".r ^^ { _.toInt }
	def t: Parser[String] = opt("Warning" <~ ":") ^^ { _.getOrElse("Error") }
	def position: Parser[(Int,Int)] = line ~ ":" ~ ch ~ ":" ^^ {
	  case line ~ _ ~ ch ~ _ => (line,ch)
	}
	def output: Parser[String] = ".*".r ^^ { _.split("    +").mkString("\n")}
	def error: Parser[((Int,Int),String,String)] = position ~ t ~ output ^^ {
	  case p ~ t ~ c => (p,t,c)
	}	 
  }
  
  def parseLine(s: String) = {
    LineParser.parse(LineParser.error,s).map(Some(_)).getOrElse(None)
  }
  
  trait GHCMessage
  case class Error extends GHCMessage
  case class Lint  extends GHCMessage  
  
  val donts = Seq(
    "\\p{Lu}\\p{L}*\\.",
    "\"[^\"]*\"",
    "'[^']*'",
    "\\{-(?:[^-]|(?:\\-+[^-\\}]))*-+\\}",
    "--.*","\\w(?:sqrt|forall|alpha|beta|gamma|pi)","(?:sqrt|forall|alpha|beta|gamma|pi)\\w")
  
  val substs = Map(      
    "."  -> "∘",
    "->" -> "→",
    "<-" -> "←",
    "!!" -> "‼",
    "/=" -> "≠",
    "<=" -> "≤",
    ">=" -> "≥",
    "\\" -> "λ",
    "&&" -> "∧",
    "||" -> "∨",
    "::" -> "∷",
    "forall" -> "∀",
    "alpha" -> "α",
    "beta" -> "β",
    "gamma" -> "γ",
    "pi" -> "π",
    "sqrt" -> "√")
 
  def prettify(s: String): String = (substs - ".").foldLeft(s) {
    case (s,(a,b)) => s.replace(a,b)
  }
    
  val r = ("(?:" + (donts ++ substs.keys.map(_.replace("\\","\\\\").replace(".", "\\.").replace("|","\\|"))).reduce(_ + "|" + _) + ")").r
  
  def substitutions(state: String, as: Annotations = new Annotations): Annotations = state match {    
    case "" => as
    case _  => r.findFirstMatchIn(state) match {
      case Some(m) =>
        if (substs.isDefinedAt(m.matched))
          substitutions(state.drop(m.end), as.plain(m.start).annotate(m.end - m.start, Set(AnnotationType.Class -> "symbol", AnnotationType.Substitution ->substs(m.matched))))
        else
          substitutions(state.drop(m.end), as.plain(m.end))
      case None => as.plain(state.length())
    }
  }
  
  def toAnnotations(errors: List[((Int,Int),String,String)], state: String): Annotations = {
    var result = new Annotations
    val lines = state.split("\n").map(_.length() + 1).toList
    
    def offset(line: Int, ch: Int): Int = 
      lines.take(line-1).reduceOption(_ + _).getOrElse(0) + ch
    
    var position = 0
      
    errors.map({
      case ((l,c),t,e) => (offset(l,c),t,e)
    }).sortBy(_._1).foreach {
      case (o,t,e) =>
        if (o > position) {
          result = result.plain(o - position)
          position = o
        }
        t match {
          case "Error" => result = result.annotate(0, Set(AnnotationType.ErrorMessage -> prettify(e)))
          case "Warning" => result = result.annotate(0, Set(AnnotationType.WarningMessage -> prettify(e)))
        }
        
    }                  
    result.plain(state.length - position)
  }
}