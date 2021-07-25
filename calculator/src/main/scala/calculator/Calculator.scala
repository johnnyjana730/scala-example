package calculator

import scala.language.postfixOps

sealed abstract class Expr
final case class Literal(v: Double) extends Expr
final case class Ref(name: String) extends Expr
final case class Plus(a: Expr, b: Expr) extends Expr
final case class Minus(a: Expr, b: Expr) extends Expr
final case class Times(a: Expr, b: Expr) extends Expr
final case class Divide(a: Expr, b: Expr) extends Expr

object Calculator extends CalculatorInterface {
  def computeValues(
      namedExpressions: Map[String, Signal[Expr]]): Map[String, Signal[Double]] = {
//    ('a' to 'z') map (ch => ch.toString -> Signal(eval(Ref(ch.toString),namedExpressions))) toMap
    namedExpressions map {
      case (name, expr) => name -> Signal(eval(expr(), namedExpressions))
    }
  }

  def eval(expr: Expr, references: Map[String, Signal[Expr]]): Double = {
    expr match {
      case Literal(v) => v
      case Ref(name) => eval(getReferenceExpr(name, references), references - name)
      case Plus(a,b) => eval(a, references) + eval(b, references)
      case Minus(a,b) => eval(a, references) - eval(b, references)
      case Times(a,b) => eval(a, references) * eval(b, references)
      case Divide(a,b) => eval(a, references)/ eval(b, references)
    }
  }

  /** Get the Expr for a referenced variables.
   *  If the variable is not known, returns a literal NaN.
   */
  private def getReferenceExpr(name: String,
      references: Map[String, Signal[Expr]]) = {
    references.get(name).fold[Expr] {
      Literal(Double.NaN)
    } { exprSignal =>
      exprSignal()
    }
  }
}
