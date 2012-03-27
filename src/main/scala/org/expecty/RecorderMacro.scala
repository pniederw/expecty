package org.expecty

import reflect.makro.Context
import tools.nsc.util.RangePosition

object RecorderMacro {
  def apply(c: Context)(recording: c.Expr[Boolean]): c.Expr[Boolean] = {
    import c.mirror._

    val exprs = splitExpressions(c)(recording)
    val recordedExprs = exprs.flatMap { expr =>
      val buggedExpr = bugExpression(c)(expr)
      val text = getText(c)(expr)
      val ast = showRaw(expr)

      val resetCall = Apply(Select(Ident(newTermName("$org_expecty_recorderRuntime")), newTermName("resetValues")), List())
      val recordCall = Apply(Select(Ident(newTermName("$org_expecty_recorderRuntime")), newTermName("recordExpression")), List(c.literal(text).tree, c.literal(ast).tree, buggedExpr))

      // why can't use tuple here?
      List(resetCall, recordCall)
    }

    val runtimeClass = staticClass(classOf[RecorderRuntime].getName)
    val runtimeDecl = ValDef(
      Modifiers(),
      newTermName("$org_expecty_recorderRuntime"),
      TypeTree(runtimeClass.asType),
      Apply(Select(New(Ident(runtimeClass)), newTermName("<init>")), List(Select(c.prefix, newTermName("listener")))))

    val completeCall = Apply(Select(Ident(newTermName("$org_expecty_recorderRuntime")), newTermName("completeRecording")), List())

    Block(runtimeDecl :: recordedExprs, completeCall)
  }

  private[this] def splitExpressions(c: Context)(recording: c.Tree): List[c.Tree] = {
    import c.mirror._

    recording match {
      case Block(xs, y) => xs ::: List(y)
      case _ => List(recording)
    }
  }

  private[this] def bugExpression(c: Context)(expr: c.Tree) : c.Tree = {
    import c.mirror._

    expr match {
      case Apply(x, ys) => recordValue(c)(Apply(bugExpression(c)(x), bugExpressions(c)(ys)), expr.tpe, getAnchor(c)(x))
      // don't record value of implicit "this" added by compiler; couldn't find a better way to detect implicit "this" than via point
      case Select(x@This(_), y) if getPosition(c)(expr).point == getPosition(c)(x).point => recordValue(c)(expr, expr.tpe, getAnchor(c)(expr))
      case Select(x, y) => recordValue(c)(Select(bugExpression(c)(x), y), expr.tpe, getAnchor(c)(expr))
      case Literal(_) => expr // don't record
      case _ => recordValue(c)(expr, expr.tpe, getAnchor(c)(expr))
    }
  }

  private[this] def bugExpressions(c: Context)(exprs: List[c.Tree]) : List[c.Tree] = exprs.map(bugExpression(c)(_))

  private[this] def recordValue(c: Context)(expr: c.Tree, tpe: c.Type, anchor: Int) : c.Tree = {
    import c.mirror._

    if (tpe.typeSymbol.isType)
      Apply(Select(Ident(newTermName("$org_expecty_recorderRuntime")), newTermName("recordValue")), List(expr, Literal(Constant(anchor))))
    else expr
  }

  private[this] def getText(c: Context)(expr: c.Tree): String = expr.pos match {
    case p: RangePosition => c.echo("RangePosition found!"); p.lineContent.slice(p.start, p.end)
    case p: scala.tools.nsc.util.Position => p.lineContent
  }

  private[this] def getAnchor(c: Context)(expr: c.Tree): Int = {
    val pos = getPosition(c)(expr)
    pos.point - pos.source.lineToOffset(pos.line - 1)
  }

  private[this] def getPosition(c: Context)(expr: c.Tree) = expr.pos.asInstanceOf[scala.tools.nsc.util.Position]
}
