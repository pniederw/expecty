# Expecty - Power Assertions for Scala

Expecty brings power assertions as known from [Groovy](http://groovy.codehaus.org) and [Spock](http://spockframework.org)
to the [Scala](http://scala-lang.org) language. It is a micro library that aims to do one thing well.

## License

Expecty is licensed under the Apache 2 license.

## Latest Release

Expecty 0.10 was released on November 12th, 2013. It requires Scala 2.10.0 or higher.

## Download

Get Expecty from its Maven repository at https://github.com/pniederw/expecty/tree/master/m2repo.

For sbt builds:

```scala
val expectyRepo = "Expecty Repository" at "https://raw.github.com/pniederw/expecty/master/m2repo/"
val expecty = "org.expecty" % "expecty" % "0.10"
```

For Gradle builds:

```groovy
repositories {
  // important: this repo has to come last
  maven {
    name "expecty"
    url "https://raw.github.com/pniederw/expecty/master/m2repo/"
  }
}

dependencies {
  testCompile "org.expecty:expecty:0.10"
}
```

For Maven builds:

```xml
<repositories>
  <repository>
    <id>expecty</id>
    <url>https://raw.github.com/pniederw/expecty/master/m2repo/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>org.expecty</groupId>
    <artifactId>expecty</artifactId>
    <version>0.10</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```

## Code Examples

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

## Further Examples

Have a look at [ExpectySpec.scala](https://github.com/pniederw/expecty/blob/master/src/test/scala/org/expecty/ExpectySpec.scala)
and other specs in the same directory.


 
