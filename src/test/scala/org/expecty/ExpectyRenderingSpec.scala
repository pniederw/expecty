package org.expecty

import org.junit.Assert._
import org.junit.{Ignore, Test}
import junit.framework.ComparisonFailure

class ExpectyRenderingSpec {
  val expect = new Expecty()

  @Test
  def literals() {
    outputs("""
"abc".length() == 2
      |        |
      3        false
    """) {
      expect {
        "abc".length() == 2
      }
    }
  }

  // TODO
  // zero-arg is broken
  // would be nice if we didn’t show list 'literals' at all
  @Test
  def object_apply() {
    outputs("""
List() == List(1, 2)
    |  |  |
    |  |  List(1, 2)
    |  false
    List()
    """) {
      expect {
        List() == List(1, 2)
      }
    }
  }

  @Test
  def infix_operators() {
    val str = "abc"

    outputs("""
str + "def" == "other"
|   |       |
abc abcdef  false
    """) {
      expect {
        str + "def" == "other"
      }
    }
  }

  @Test
  def null_value() {
    val x = null

    outputs("""
x == "null"
| |
| false
null
    """) {
      expect {
        x == "null"
      }
    }
  }

  @Test
  def value_with_type_hint() {
    val expect = new Expecty(showTypes = true)
    val x = "123"

    outputs("""
x == 123
| |
| false (java.lang.Boolean)
123 (java.lang.String)
    """) {
      expect {
        x == 123
      }
    }
  }

  @Test
  def arithmetic_expressions() {
    val one = 1

    outputs("""
one + 2 == 4
|   |   |
1   3   false
    """) {
      expect {
        one + 2 == 4
      }
    }
  }

  @Test
  def property_read() {
    val person = Person()

    outputs("""
person.age == 43
|      |   |
|      42  false
Person(Fred,42)
    """) {
      expect {
        person.age == 43
      }
    }
  }

  @Test
  def method_call_zero_args() {
    val person = Person()

    outputs("""
person.doIt() == "pending"
|      |      |
|      done   false
Person(Fred,42)
    """) {
      expect {
        person.doIt() == "pending"
      }
    }
  }

  @Test
  def method_call_one_arg() {
    val person = Person()
    val word = "hey"

    outputs("""
person.sayTwice(word) == "hoho"
|      |        |     |
|      heyhey   hey   false
Person(Fred,42)
    """) {
      expect {
        person.sayTwice(word) == "hoho"
      }
    }
  }

  @Test
  def method_call_multiple_args() {
    val person = Person()
    val word1 = "hey"
    val word2 = "ho"

    outputs("""
person.sayTwo(word1, word2) == "hoho"
|      |      |      |      |
|      heyho  hey    ho     false
Person(Fred,42)
    """) {
      expect {
        person.sayTwo(word1, word2) == "hoho"
      }
    }
  }

  @Test
  def method_call_var_args() {
    val person = Person()
    val word1 = "foo"
    val word2 = "bar"
    val word3 = "baz"

    outputs("""
person.sayAll(word1, word2, word3) == "hoho"
|      |      |      |      |      |
|      |      foo    bar    baz    false
|      foobarbaz
Person(Fred,42)
    """) {
      expect {
        person.sayAll(word1, word2, word3) == "hoho"
      }
    }
  }

  @Test
  def nested_property_reads_and_method_calls() {
    val person = Person()

    outputs("""
person.sayTwo(person.sayTwice(person.name), "bar") == "hoho"
|      |      |      |        |      |             |
|      |      |      FredFred |      Fred          false
|      |      Person(Fred,42) Person(Fred,42)
|      FredFredbar
Person(Fred,42)

    """) {
      expect {
        person.sayTwo(person.sayTwice(person.name), "bar") == "hoho"
      }
    }
  }

  // TODO
  @Test
  @Ignore
  def implicit_conversion() {
    outputs("""
"fred".slice(1, 2) == "frog"
|      |           |
|red   r           false
scala.Predef$@5bcdbf6
      """) {
      expect {
        "fred".slice(1, 2) == "frog"
      }
    }
  }

  def outputs(rendering: String)(expectation: => Boolean) {
    try {
      expectation
      fail("Expectation should have failed but didn't")
    }
    catch  {
      case e: AssertionError => {
        val expected = rendering.trim()
        val actual = e.getMessage.trim()
        if (actual != expected) {
          throw new ComparisonFailure("Expectation output doesn't match", expected, actual)
        }
      }
    }
  }

  case class Person(name: String = "Fred", age: Int = 42) {
    def doIt() = "done"
    def sayTwice(word: String) = word * 2
    def sayTwo(word1: String,  word2: String) = word1 + word2
    def sayAll(words: String*) = words.mkString("")
  }
}
