/*
* Copyright 2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*     http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.expecty

import reflect.makro.Context
import tools.nsc.util.RangePosition

object RecorderMacro {
  def apply(c: Context)(recording: c.Expr[Boolean]): c.Expr[Boolean] = {
    import c.mirror._

    Block(declareRuntime(c) :: recordExpressions(c)(recording), completeRecording(c))
  }

  private def declareRuntime(c: Context): c.Tree = {
    import c.mirror._

    val runtimeClass = staticClass(classOf[RecorderRuntime].getName)
    ValDef(
      Modifiers(),
      newTermName("$org_expecty_recorderRuntime"),
      TypeTree(runtimeClass.asType),
      Apply(
        Select(
          New(Ident(runtimeClass)),
          newTermName("<init>")),
        List(
          Select(
            c.prefix,
            newTermName("listener")))))
  }

  private def recordExpressions(c: Context)(recording: c.Tree): List[c.Tree] = {
    import c.mirror._

    val exprs = splitExpressions(c)(recording)
    exprs.flatMap { expr =>
      val text = getText(c)(expr)
      val ast = showRaw(expr)
      try {
        List(resetValues(c), recordExpression(c)(text, ast, expr))
      } catch {
        case e => throw new RuntimeException(
          "Expecty: Error rewriting expression.\nText: " + text + "\nAST : " + ast, e)
      }
    }
  }

  private def completeRecording(c: Context): c.Tree = {
    import c.mirror._

    Apply(
      Select(
        Ident(newTermName("$org_expecty_recorderRuntime")),
        newTermName("completeRecording")),
      List())
  }

  private def resetValues(c: Context) = {
    import c.mirror._

    Apply(
      Select(
        Ident(newTermName("$org_expecty_recorderRuntime")),
        newTermName("resetValues")),
      List())
  }

  private def recordExpression(c: Context)(text: String, ast: String, expr: c.Tree) = {
    import c.mirror._

    val buggedExpr = bugExpression(c)(expr, true)
    c.echo("Expression  : " + text.trim())
    c.echo("Original AST: " + ast)
    c.echo("Bugged AST  : " + showRaw(buggedExpr))
    c.echo("")

    Apply(
      Select(
        Ident(newTermName("$org_expecty_recorderRuntime")),
        newTermName("recordExpression")),
      List(
        c.literal(text).tree,
        c.literal(ast).tree,
        buggedExpr))
  }

  private def splitExpressions(c: Context)(recording: c.Tree): List[c.Tree] = {
    import c.mirror._

    recording match {
      case Block(xs, y) => xs ::: List(y)
      case _ => List(recording)
    }
  }

  private def bugExpression(c: Context)(expr: c.Tree, record: Boolean) : c.Tree = {
    import c.mirror._

    expr match {
      case Apply(x, ys) => recordValue(c)(Apply(bugExpression(c)(x, true), bugExpressions(c)(ys, true)), expr.tpe, getAnchor(c)(x), record)
      case TypeApply(x, ys) => recordValue(c)(TypeApply(bugExpression(c)(x, false), ys), expr.tpe, getAnchor(c)(x), record)
      // don't record value of implicit "this" added by compiler; couldn't find a better way to detect implicit "this" than via point
      case Select(x@This(_), y) if getPosition(c)(expr).point == getPosition(c)(x).point => recordValue(c)(expr, expr.tpe, getAnchor(c)(expr), record)
      case Select(x, y) => recordValue(c)(Select(bugExpression(c)(x, true), y), expr.tpe, getAnchor(c)(expr), record)
      case New(_) => expr // only record after ctor call
      case Literal(_) => expr // don't record
      case _ => recordValue(c)(expr, expr.tpe, getAnchor(c)(expr), record)
    }
  }

  private def bugExpressions(c: Context)(exprs: List[c.Tree], record: Boolean) : List[c.Tree] = exprs.map(bugExpression(c)(_, record))

  private def recordValue(c: Context)(expr: c.Tree, tpe: c.Type, anchor: Int, record: Boolean) : c.Tree = {
    import c.mirror._

    if (record && tpe.typeSymbol.isType)
      Apply(
        Select(
          Ident(newTermName("$org_expecty_recorderRuntime")),
          newTermName("recordValue")),
        List(expr, Literal(Constant(anchor))))
    else expr
  }

  private def getText(c: Context)(expr: c.Tree): String = expr.pos match {
    case p: RangePosition => c.echo("RangePosition found!"); p.lineContent.slice(p.start, p.end)
    case p: scala.tools.nsc.util.Position => p.lineContent
  }

  private def getAnchor(c: Context)(expr: c.Tree): Int = {
    val pos = getPosition(c)(expr)
    pos.point - pos.source.lineToOffset(pos.line - 1)
  }

  private def getPosition(c: Context)(expr: c.Tree) = expr.pos.asInstanceOf[scala.tools.nsc.util.Position]
}
