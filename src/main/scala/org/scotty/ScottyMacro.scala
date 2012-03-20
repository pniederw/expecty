package org.scotty

import scala.reflect.makro.Context

object ScottyMacro {
  def expect(c: Context)(cond: c.Expr[Boolean]): c.Expr[Unit] = {
    import c.mirror._

    val text = lineContent(c)(cond.tree)
    val ast = showRaw(cond.tree)

    c.echo(ast)

    val newTree = transform(c)(cond.tree)
    val newCond = c.Expr(newTree)

    c.reify {
      val _exp = new Condition()
      _exp.verify(newCond.eval, c.literal(text).eval, c.literal(ast).eval)
    }
  }

  def transform(c: Context)(t: c.Tree) : c.Tree = {
    import c.mirror._

    t match {
      case Apply(x, y) => record(c)(anchorAfter(c)(x), Apply(transform(c)(x), transformList(c)(y)))
      case Select(x, y) => Select(transform(c)(x), y)
      case x@Literal(_) => x // don't record
      case other => record(c)(anchorAt(c)(other), other)
    }
  }

  def transformList(c: Context)(t: List[c.Tree]) : List[c.Tree] = t.map(transform(c)(_))

  def record(c: Context)(anchor: Int, t: c.Tree) : c.Tree = {
    import c.mirror._

    Apply(Select(Ident(newTermName("_exp")), newTermName("record")), List(Literal(Constant(anchor)), t))
  }

  def lineContent(c: Context)(t: c.Tree): String = t.pos match {
    case p: scala.tools.nsc.util.OffsetPosition => p.lineContent
    case _ => if (t.children.isEmpty) "source code not available" else lineContent(c)(t.children(0)) + "" // appending "" works around scalac bug in tailcall phase
  }

  def anchorAt(c: Context)(t: c.Tree): Int = t.pos match {
    case p: scala.tools.nsc.util.OffsetPosition => p.point - p.source.lineToOffset(p.line - 1)
    case _ => -1
  }

  def anchorAfter(c: Context)(t: c.Tree): Int = {
    val content = lineContent(c)(t)
    val from = anchorAt(c)(t)

    if (from < 0) -1 else content.indexWhere(!_.isWhitespace, from - 1)
  }
}



