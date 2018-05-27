/*
 * Copyright (c) 2014-2016 by Alexandru Nedelcu.
 * Some rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.expecty

object compat {
  type Context = scala.reflect.macros.Context

  def freshTermName[C <: Context](c: C)(s: String) =
    c.universe.newTermName(c.fresh(s))

  def termName[C <: Context](c: C)(s: String) =
    c.universe.newTermName(s)
}
