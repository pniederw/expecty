package org.expecty

import org.expecty.Expecty._
import org.junit.Test

// TODO:
// implicit conversions (e.g. "fred".slice(1, 2)) cause undesired output
class ExpectySpec {
  implicit def result2TestResult(result: Result) = TestResult(result)

  val from = 3
  val answer = false

  def universe() = 42

  implicit val opts = Options().noFailEarly

  @Test
  def validExpectation() {
    val name = "Hi from Expecty!"
    expect(name.length == 16)
  }

  @Test
  def invalidExpectation() {
    val name = "Hi from Expecty!"
    expect(name.length() == 10)
  }

  @Test
  def multipleValidExpectations() {
    val name = "Hi from Expecty!"
    expect(name.length == 16)
    expect(name.startsWith("Hi"))
    expect(name.endsWith("Expecty!"))
  }

  @Test
  def mixedValidAndInvalidExpectations() {
    val name = "Hi from Expecty!"
    expect(name.length == 16)
    expect(name.startsWith("Ho"))
    expect(name.endsWith("Expecty!"))
  }

  @Test
  def validMultiExpectation() {
    val name = "Hi from Expecty!"
    expect {
      name.length == 16
      name.startsWith("Hi")
      name.endsWith("Expecty!")
    }
  }

  @Test
  def invalidMultiExpectation() {
    val name = "Hi from Expecty!"
    expect {
      name.length == 16
      name.startsWith("Ho")
      name.endsWith("Expecty!")
    }
  }

  @Test
  def other() {
    val name = "Hi from Expecty!"

    val to = 5
    expect(name.substring(this.from, to) == "frfr")
  }

  @Test
  def other2() {
    expect(answer)
  }

  @Test
  def other3() {
    expect(this.answer)
  }

  @Test
  def other4() {
    expect("abc".reverse == "cbx")
  }

  @Test
  def other5() {
    expect {
      "abc".reverse == "cbx"
    } outputs """
"abc".reverse == "cbx"
|     |       |
|bc   cba     false
scala.Predef$@296f25a7
    """
  }
}
