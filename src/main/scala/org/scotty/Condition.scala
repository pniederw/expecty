package org.scotty

import collection.mutable.ListBuffer

class Condition {
  var values: List[ConditionValue[_]] = List()

  def verify(cond: Boolean, text: String, ast: String) {
    if (!cond) {
      val intro = new StringBuilder().append("Condition not satisfied:\n\n").append(text)
      val lines = ListBuffer(new StringBuilder)

      val sorted = values.sortWith(_.col > _.col)
      for (value <- sorted) place(value, lines)

      lines.prepend(intro)
      lines.append(new StringBuilder)
      throw new AssertionError(lines.mkString("\n"))
    }
  }

  private[this] def place(value: ConditionValue[_], lines: ListBuffer[StringBuilder]) {
    val str = value.value.toString()

    insert(lines(0), value.col, "|")

    for (line <- lines.drop(1)) {
      if (fits(value, str, line)) {
        insert(line, value.col, str)
        return
      }
      insert(line, value.col, "|")
    }

    val last = new StringBuilder()
    insert(last, value.col, str)
    lines.append(last)
  }

  private[this] def insert(builder: StringBuilder, col: Int,  str: String) {
    val diff = col - builder.length
    for (i <- 1 to diff) builder.append(' ')
    builder.replace(col, col + str.length(), str)
  }

  private[this] def fits(value: ConditionValue[_], str: String, line: StringBuilder): Boolean = {
    val firstCol = value.col
    val lastCol = value.col + str.length() + 1

    line.slice(firstCol, lastCol).forall(_.isWhitespace)
  }

  def record[T](col: Int, value: T) : T = {
    values = ConditionValue(col, value) :: values
    value
  }
}

case class ConditionValue[T](col: Int, value: T)