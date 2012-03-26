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
