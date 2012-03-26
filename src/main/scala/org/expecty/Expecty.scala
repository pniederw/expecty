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

object Expecty {
  implicit val opts = Options()
  def expect(cond: Boolean)(implicit opts: Options): Result = macro ExpectyMacro.expect
}

case class Options(isFail: Boolean = true, isFailEarly: Boolean = true, isTrace: Boolean = false, isShowTypes: Boolean = false) {
  def fail = copy(isFail = true)
  def noFail = copy(isFail = false)

  def failEarly = copy(isFailEarly = true)
  def failLate = copy(isFailEarly = false)

  def trace = copy(isTrace = true)
  def noTrace = copy(isTrace = false)

  def showTypes = copy(isShowTypes = true)
  def noShowTypes = copy(isShowTypes = false)
}

