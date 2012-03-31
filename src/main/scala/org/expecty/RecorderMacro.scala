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

  private[this] def declareRuntime(c: Context): c.Tree = {
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

  private[this] def recordExpressions(c: Context)(recording: c.Tree): List[c.Tree] = {
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

  private[this] def completeRecording(c: Context): c.Tree = {
    import c.mirror._

    Apply(
      Select(
        Ident(newTermName("$org_expecty_recorderRuntime")),
        newTermName("completeRecording")),
      List())
  }

  private[this] def resetValues(c: Context) = {
    import c.mirror._

    Apply(
      Select(
        Ident(newTermName("$org_expecty_recorderRuntime")),
        newTermName("resetValues")),
      List())
  }

  private[this] def recordExpression(c: Context)(text: String, ast: String, expr: c.Tree) = {
    import c.mirror._

    val buggedExpr = recordAllValues(c)(expr)
    log(c)(expr, "Expression  : " + text.trim())
    log(c)(expr, "Original AST: " + ast)
    log(c)(expr, "Bugged AST  : " + showRaw(buggedExpr))
    log(c)(expr, "")

    Apply(
      Select(
        Ident(newTermName("$org_expecty_recorderRuntime")),
        newTermName("recordExpression")),
      List(
        c.literal(text).tree,
        c.literal(ast).tree,
        buggedExpr))
  }

  private[this] def splitExpressions(c: Context)(recording: c.Tree): List[c.Tree] = {
    import c.mirror._

    recording match {
      case Block(xs, y) => xs ::: List(y)
      case _ => List(recording)
    }
  }

  private[this] def recordAllValues(c: Context)(expr: c.Tree) : c.Tree = {
    import c.mirror._

    expr match {
      case New(_) => expr // only record after ctor call
      case Literal(_) => expr // don't record
      // don't record value of implicit "this" added by compiler; couldn't find a better way to detect implicit "this" than via point
      case Select(x@This(_), y) if getPosition(c)(expr).point == getPosition(c)(x).point => expr
      case _ => recordValue(c)(recordSubValues(c)(expr), expr)
    }
  }

  private[this] def recordSubValues(c: Context)(expr: c.Tree) : c.Tree = {
    import c.mirror._

    expr match {
      case Apply(x, ys) => Apply(recordAllValues(c)(x), ys.map(recordAllValues(c)(_)))
      case TypeApply(x, ys) => recordValue(c)(TypeApply(recordSubValues(c)(x), ys), expr)
      case Select(x, y) => Select(recordAllValues(c)(x), y)
      case _ => expr
    }
  }

  private[this] def recordValue(c: Context)(expr: c.Tree, origExpr: c.Tree) : c.Tree = {
    import c.mirror._

    if (origExpr.tpe.typeSymbol.isType)
      Apply(
        Select(
          Ident(newTermName("$org_expecty_recorderRuntime")),
          newTermName("recordValue")),
        List(expr, Literal(Constant(getAnchor(c)(origExpr)))))
    else expr
  }

  private[this] def getText(c: Context)(expr: c.Tree): String = expr.pos match {
    case p: RangePosition => c.echo("RangePosition found!"); p.lineContent.slice(p.start, p.end)
    case p: scala.tools.nsc.util.Position => p.lineContent
  }

  private[this] def getAnchor(c: Context)(expr: c.Tree): Int = {
    import c.mirror._

    expr match {
      case Apply(x, ys) => getAnchor(c)(x) + 0
      case TypeApply(x, ys) => getAnchor(c)(x) + 0
      case _ => {
        val pos = getPosition(c)(expr)
        pos.point - pos.source.lineToOffset(pos.line - 1)
      }
    }
  }

  private[this] def getPosition(c: Context)(expr: c.Tree) = expr.pos.asInstanceOf[scala.tools.nsc.util.Position]

  private[this] def log(c: Context)(expr: c.Tree, msg: String) {
    c.info(expr.pos, msg, false)
  }
}
