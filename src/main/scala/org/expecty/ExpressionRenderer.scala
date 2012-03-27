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

import collection.mutable.ListBuffer
import reflect.runtime.Mirror

class ExpressionRenderer(showTypes: Boolean) {
  def render(recordedExpr: RecordedExpression[_]): String = {
    val offset = recordedExpr.text.prefixLength(_.isWhitespace)
    val intro = new StringBuilder().append("\n\n").append(recordedExpr.text.trim())
    val lines = ListBuffer(new StringBuilder)

    val rightToLeft = recordedExpr.recordedValues.sortWith(_.anchor > _.anchor)
    for (recordedValue <- rightToLeft) placeValue(lines, recordedValue.value, recordedValue.anchor - offset)

    lines.prepend(intro)
    lines.append(new StringBuilder)
    lines.mkString("\n")
  }

  private[this] def placeValue(lines: ListBuffer[StringBuilder], value: Any, col: Int) {
    val str = renderValue(value)

    placeString(lines(0), "|", col)

    for (line <- lines.drop(1)) {
      if (fits(line, str, col)) {
        placeString(line, str, col)
        return
      }
      placeString(line, "|", col)
    }

    val newLine = new StringBuilder()
    placeString(newLine, str, col)
    lines.append(newLine)
  }

  private[this] def renderValue(value: Any): String = {
    val str = if (value == null) "null" else value.toString
    if (showTypes) str + " (" + Mirror.typeOfInstance(value).typeSymbol.fullName + ")"
    else str
  }

  private[this] def placeString(line: StringBuilder, str: String, anchor: Int) {
    val diff = anchor - line.length
    for (i <- 1 to diff) line.append(' ')
    line.replace(anchor, anchor + str.length(), str)
  }

  private[this] def fits(line: StringBuilder, str: String, anchor: Int): Boolean = {
    line.slice(anchor, anchor + str.length() + 1).forall(_.isWhitespace)
  }
}
