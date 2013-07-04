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

import language.experimental.macros

// should have two type parameters; one for recorded type, another for returned type
// so far failed to implement the macro side of this
abstract class Recorder {
  val listener: RecorderListener[Boolean]
  def apply(recording: Boolean): Boolean = macro RecorderMacro.apply
  def logged(recording: Boolean): Boolean = macro RecorderMacro.logged
}

trait CompileTimeLogging extends Recorder {
  override def apply(recording: Boolean): Boolean = macro RecorderMacro.logged
}
