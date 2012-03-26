package org.expecty

import org.junit.ComparisonFailure

case class TestResult(result: Result) {
  def outputs(output: String) {
    assert(result.failures.length == 1)
    val expected = output.trim()
    val actual = result.failures(0).output.trim()
    if (actual != expected) throw new ComparisonFailure("Expectation output doesn't match", expected, actual)
  }
}
