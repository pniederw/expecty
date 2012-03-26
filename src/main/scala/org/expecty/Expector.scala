package org.expecty

import scala.collection.mutable.ListBuffer
import reflect.runtime.Mirror

class Expector(opts: Options) {
  private[this] var captures: List[Capture[_]] = List.empty
  private[this] var failures: List[Failure] = List.empty

  def reset() {
    captures = List.empty
  }

  def record[T](value: T, col: Int) : T = {
    captures = Capture(value, col) :: captures
    value
  }

  def verify(cond: Boolean, text: String, ast: String): Result = {
    lazy val output = render(text)
    if (opts.isTrace) println(output)
    if (!cond) {
      if (opts.isFail && opts.isFailEarly) throw new AssertionError(output)
      failures = Failure(captures, output) :: failures
    }
    Result(failures)
  }

  private[this] def render(text: String): String = {
    val offset = text.prefixLength(_.isWhitespace)
    val intro = new StringBuilder().append("\n\n").append(text.trim())
    val lines = ListBuffer(new StringBuilder)

    val rightToLeft = captures.sortWith(_.col > _.col)
    for (value <- rightToLeft) placeValue(lines, value.value, value.col - offset)

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
    if (opts.isShowTypes) str + " (" + Mirror.typeOfInstance(value).typeSymbol.fullName + ")"
    else str
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

case class Capture[T](value: T, col: Int)

case class Failure(captures: List[Capture[_]], output: String)

case class Result(failures: List[Failure])