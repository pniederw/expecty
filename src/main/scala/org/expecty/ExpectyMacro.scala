package org.expecty

import scala.reflect.makro.Context
import scala.collection.mutable.ListBuffer
import scala.tools.nsc.util.RangePosition

object ExpectyMacro {
  def expect(c: Context)(cond: c.Expr[Boolean])(opts: c.Expr[Options]): c.Expr[Result] = {
    import c.mirror._

    val conds = split(c)(cond).map { cond =>
      val transformed = transform(c)(cond)
      val source = getSource(c)(cond)
      val ast = showRaw(cond)
      (transformed, source, ast)
    }

    val exprs = ListBuffer.empty[c.Tree]
    val expectorClass = staticClass(classOf[Expector].getName)

    exprs += ValDef(
      Modifiers(),
      newTermName("_exp"),
      TypeTree(expectorClass.asType),
      Apply(Select(New(Ident(expectorClass)), newTermName("<init>")), List(opts)))

    for (cond <- conds) {
      val (transformed, source, ast) = cond
      exprs += Apply(Select(Ident(newTermName("_exp")), newTermName("reset")), List())
      exprs += Apply(Select(Ident(newTermName("_exp")), newTermName("verify")), List(transformed, c.literal(source).tree, c.literal(ast).tree))
    }

    Block(exprs.dropRight(1).toList, exprs.last)
  }

  private[this] def split(c: Context)(cond: c.Tree): List[c.Tree] = {
    import c.mirror._

    cond match {
      case Block(xs, y) => xs ::: List(y)
      case _ => List(cond)
    }
  }

  private[this] def transform(c: Context)(t: c.Tree) : c.Tree = {
    import c.mirror._

    t match {
      case Apply(x, y) => record(c)(getAnchor(c)(x), t.tpe, Apply(transform(c)(x), transformList(c)(y)))
      // don't record value of implicit "this" added by compiler; couldn't find a better way to detect implicit "this" than via point
      case Select(x@This(_), y) if getPos(c)(t).point == getPos(c)(x).point => record(c)(getAnchor(c)(t), t.tpe, t)
      case Select(x, y) => record(c)(getAnchor(c)(t), t.tpe, Select(transform(c)(x), y))
      case Literal(_) => t // don't record
      case other => record(c)(getAnchor(c)(other), t.tpe, other)
    }
  }

  private[this] def transformList(c: Context)(t: List[c.Tree]) : List[c.Tree] = t.map(transform(c)(_))

  private[this] def record(c: Context)(anchor: Int, tpe: c.Type, t: c.Tree) : c.Tree = {
    import c.mirror._

    if (tpe.typeSymbol.isType)
      Apply(Select(Ident(newTermName("_exp")), newTermName("record")), List(t, Literal(Constant(anchor))))
    else t
  }

  private[this] def getSource(c: Context)(t: c.Tree): String = t.pos match {
    case p: RangePosition => c.echo("RangePosition found!"); p.lineContent.slice(p.start,  p.end)
    case p: scala.tools.nsc.util.Position => p.lineContent
  }

  private[this] def getAnchor(c: Context)(t: c.Tree): Int = {
    val pos = getPos(c)(t)
    pos.point - pos.source.lineToOffset(pos.line - 1)
  }

  private[this] def getPos(c: Context)(t: c.Tree) = t.pos.asInstanceOf[scala.tools.nsc.util.Position]
}



