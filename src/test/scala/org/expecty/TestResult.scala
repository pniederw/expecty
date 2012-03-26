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

import org.junit.ComparisonFailure
import org.junit.Assert._

case class TestResult(result: Result) {
  def outputs(output: String) {
    assertEquals(1, result.failures.length)
    val expected = output.trim()
    val actual = result.failures(0).output.trim()
    if (actual != expected) throw new ComparisonFailure("Expectation output doesn't match", expected, actual)
  }
}
