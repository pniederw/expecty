package org.scotty

import collection.mutable.ListBuffer

class Condition {
  var values: List[ConditionValue[_]] = List()

  def record[T](value: T, col: Int) : T = {
    values = ConditionValue(value, col) :: values
    value
  }

  def verify(cond: Boolean, text: String, ast: String) {
    if (!cond) {
      val intro = new StringBuilder().append("\n\n").append(text)
      val lines = ListBuffer(new StringBuilder)

      val sorted = values.sortWith(_.col > _.col)
      for (value <- sorted) placeValue(lines, value)

      lines.prepend(intro)
      lines.append(new StringBuilder)
      throw new AssertionError(lines.mkString("\n"))
    }
  }

  private[this] def placeValue(lines: ListBuffer[StringBuilder], value: ConditionValue[_]) {
    val str = value.value.toString()
    val col = value.col

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

  private[this] def placeString(line: StringBuilder, str: String, col: Int) {
    val diff = col - line.length
    for (i <- 1 to diff) line.append(' ')
    line.replace(col, col + str.length(), str)
  }

  private[this] def fits(line: StringBuilder, str: String, col: Int): Boolean = {
    line.slice(col, col + str.length() + 1).forall(_.isWhitespace)
  }
}

case class ConditionValue[T](value: T, col: Int)