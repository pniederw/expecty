# Expecty

Expecty brings power assertions as known from [Groovy](http://groovy.codehaus.org) and [Spock](http://spockframework.org) to the [Scala](http://scala-lang.org) language. It is a micro library that aims to do one thing well. Expecty is licensed under the Apache 2 license.

## Examples

```scala
import org.expecty.Expecty

case class Person(name: String = "Fred", age: Int = 42) {
  def say(words: String*) = words.mkString(" ")
}

val person = Person()
val expect = new Expecty()

// Passing expectations

expect {
  person.name == "Fred"
  person.age * 2 == 84
  person.say("Hi", "from", "Expecty!") == "Hi from Expecty!"
}

// Failing expectation

val word1 = "ping"
val word2 = "pong"

expect {
  person.say(word1, word2) == "pong pong"
}

/*
Output:

java.lang.AssertionError:

person.say(word1, word2) == "pong pong"
|      |   |      |      |
|      |   ping   pong   false
|      ping pong
Person(Fred,42)
*/

// Continue despite failing predicate

val expect2 = new Expecty(failEarly = false)

expect2 {
  person.name == "Frog"
  person.age * 2 == 73
}

/*
Output:

java.lang.AssertionError:

person.name == "Frog"
|      |    |
|      Fred false
Person(Fred,42)


person.age * 2 == 73
|      |   |   |
|      42  84  false
Person(Fred,42)
*/
```

## Getting Started

Expecty requires Scala 2.10.0-M3 or higher. To get an idea how to use Expecty, have a look at `src/test/scala/org/epecty/ExpectySpec.scala` and other specs in the same directory.

 