# Expecty

Expecty brings power assertions known from [Groovy](http://groovy.codehaus.org) and [Spock](http://spockframework.org) to Scala. It is a micro library that aims to do one thing well. Expecty is licensed under the Apache 2 license.

## Examples

```scala
case class Person(name: String = "Fred", age: Int = 42) {
  def say(words: String*) = words.mkString(" ")
}

val person = Person()

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
```

## Getting Started

Currently, Expecty is based on the [Scala Macros](https://github.com/scalamacros/kepler) fork of the Scala language. The goal is to get it to work with the upcoming Scala 2.10 (possibly already with a milestone). Because there are no binaries published for the Scala Macros fork, its GitHub repo needs to be added as a Git submodule, and the Expecty build will take care of building a Scala distribution from it.

    $ git checkout git@github.com:pniederw/expecty.git
    $ git submodule init
    $ git submodule update
    $ ./gradlew build
    
The third and fourth command will take a while because they clone and build https://github.com/scalamacros/kepler. Subsequent builds will be smart enough not to rebuild Kepler unless it is manually updated to a newer version.

To get an idea how to use Expecty, have a look at `src/test/scala/org/epecty/ExpectySpec.scala` and other specs in the same directory.

Note: Development of Expecty is in a very early stage.

 