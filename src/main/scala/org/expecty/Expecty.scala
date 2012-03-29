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

class Expecty(failEarly: Boolean = true, showTypes: Boolean = false, printAsts: Boolean = false) extends Recorder {
  class ExpectyListener extends RecorderListener[Boolean] {
    override def expressionRecorded(recordedExpr: RecordedExpression[Boolean]) {
      if (printAsts) println(recordedExpr.ast)

      if (!recordedExpr.value && failEarly) {
        val renderer = new ExpressionRenderer(showTypes)
        val rendering = renderer.render(recordedExpr)
        throw new AssertionError(rendering)
      }
    }

    override def recordingCompleted(recording: Recording[Boolean]) {
      if (!failEarly) {
        val failedExprs = recording.recordedExprs.filter(!_.value)
        if (!failedExprs.isEmpty) {
          val renderer = new ExpressionRenderer(showTypes)
          val renderings = failedExprs.reverse.map(renderer.render(_))
          throw new AssertionError(renderings.mkString("\n\n"))
        }
      }
    }
  }

  val listener = new ExpectyListener
}


