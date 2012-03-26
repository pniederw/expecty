package org.expecty

import org.expecty.Expecty._

import org.junit.Test

class ExpectySpec {
  implicit val opts = Options()
  val name = "Hi from Expecty!"

  @Test
  def passingExpectation() {
    expect(name.length == 16)
  }

  @Test(expected = classOf[AssertionError])
  def failingExpectation() {
    expect(name.length() == 10)
  }

  @Test
  def multiplePassingExpectations() {
    expect(name.length == 16)
    expect(name.startsWith("Hi"))
    expect(name.endsWith("Expecty!"))
  }

  @Test(expected = classOf[AssertionError])
  def mixedPassingAndFailingExpectations() {
    expect(name.length == 16)
    expect(name.startsWith("Ho"))
    expect(name.endsWith("Expecty!"))
  }

  @Test
  def passingMultiExpectation() {
    expect {
      name.length == 16
      name.startsWith("Hi")
      name.endsWith("Expecty!")
    }
  }

  @Test(expected = classOf[AssertionError])
  def failingMultiExpectation() {
    expect {
      name.length == 16
      name.startsWith("Ho")
      name.endsWith("Expecty!")
    }
  }
}
