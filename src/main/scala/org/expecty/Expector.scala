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