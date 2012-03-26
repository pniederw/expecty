package org.expecty

import org.expecty.Expecty._

import org.junit.Test
import org.junit.Ignore

class ExpectyRenderingSpec {
  implicit def result2TestResult(result: Result) = TestResult(result)
  implicit var opts = Options().noFail

  @Test
  def literals() {
    expect {
      "abc".length() == 2
    } outputs """
"abc".length() == 2
      |        |
      3        false
    """
  }

  // TODO
  // zero-arg is broken
  // would be nice if we didn’t show list 'literals' at all
  @Test
  def object_apply() {
    expect {
      List() == List(1, 2)
    } outputs """
List() == List(1, 2)
    |  |  |
    |  |  List(1, 2)
    |  false
    List()
    """
  }

  @Test
  def infix_operators() {
    val str = "abc"

    expect {
      str + "def" == "other"
    } outputs """
str + "def" == "other"
|   |       |
abc abcdef  false
    """
  }

  @Test
  def null_value() {
    val x = null

    expect {
      x == "null"
    } outputs """
x == "null"
| |
| false
null
    """
  }

  @Test
  def value_with_type_hint() {
    opts = opts.showTypes
    val x = "123"

    expect {
      x == 123
    } outputs  """
x == 123
| |
| false (java.lang.Boolean)
123 (java.lang.String)
    """
  }

  @Test
  def arithmetic_expressions() {
    val one = 1

    expect {
      one + 2 == 4
    } outputs """
one + 2 == 4
|   |   |
1   3   false
    """
  }

  @Test
  def property_read() {
    val person = Person()

    expect {
      person.age == 43
    } outputs """
person.age == 43
|      |   |
|      42  false
Person(Fred,42)
    """
  }

  @Test
  def method_call_zero_args() {
    val person = Person()

    expect {
      person.doIt() == "pending"
    } outputs """
person.doIt() == "pending"
|      |      |
|      done   false
Person(Fred,42)
    """
  }

  @Test
  def method_call_one_arg() {
    val person = Person()
    val word = "hey"

    expect {
      person.sayTwice(word) == "hoho"
    } outputs """
person.sayTwice(word) == "hoho"
|      |        |     |
|      heyhey   hey   false
Person(Fred,42)
    """
  }

  @Test
  def method_call_multiple_args() {
    val person = Person()
    val word1 = "hey"
    val word2 = "ho"

    expect {
      person.sayTwo(word1, word2) == "hoho"
    } outputs """
person.sayTwo(word1, word2) == "hoho"
|      |      |      |      |
|      heyho  hey    ho     false
Person(Fred,42)
    """
  }

  @Test
  def method_call_var_args() {
    val person = Person()
    val word1 = "foo"
    val word2 = "bar"
    val word3 = "baz"

    expect {
      person.sayAll(word1, word2, word3) == "hoho"
    } outputs """
person.sayAll(word1, word2, word3) == "hoho"
|      |      |      |      |      |
|      |      foo    bar    baz    false
|      foobarbaz
Person(Fred,42)
    """
  }

  @Test
  def nested_property_reads_and_method_calls() {
    val person = Person()

    expect {
      person.sayTwo(person.sayTwice(person.name), "bar") == "hoho"
    } outputs """
person.sayTwo(person.sayTwice(person.name), "bar") == "hoho"
|      |      |      |        |      |             |
|      |      |      FredFred |      Fred          false
|      |      Person(Fred,42) Person(Fred,42)
|      FredFredbar
Person(Fred,42)

    """
  }

  // TODO
  @Test
  @Ignore
  def implicit_conversion() {
    expect {
      "fred".slice(1, 2) == "frog"
    } outputs """
"fred".slice(1, 2) == "frog"
|      |           |
|red   r           false
scala.Predef$@5bcdbf6
      """
  }

  case class Person(name: String = "Fred", age: Int = 42) {
    def doIt() = "done"
    def sayTwice(word: String) = word * 2
    def sayTwo(word1: String,  word2: String) = word1 + word2
    def sayAll(words: String*) = words.mkString("")
  }
}
