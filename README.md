# Expecty

Expecty brings power assertions known from Groovy and Spock to Scala. It is a micro library that aims to do one thing well. Expecty is licensed under the Apache 2 license.

## Getting Started

Currently, Expecty is based on the Scala Macros fork of Scala (https://github.com/scalamacros/kepler). The goal is to get it to work with the upcoming Scala 2.10 proper (possibly already with a milestone). Because there are no binaries published for the Scala Macros fork, its GitHub repo needs to be added as a Git submodule, and the Expecty build will take care of building the Scala distribution to be used.

    $ git checkout git@github.com:pniederw/expecty.git
    $ git submodule init
    $ git submodule update
    $ ./gradlew build
    
The third and fourth command will take a while because they clone and build https://github.com/scalamacros/kepler. Subsequent builds will be smart enough not to rebuild Kepler unless it is manually updated to a newer version.

To get an idea how to use Expecty, have a look at src/test/scala/org/epecty/ExpectySpec and other specs in the same directory.

Note: Development of Expecty is in a very early stage.

 