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
