/*
* Copyright 2012 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*     http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package foo

import org.junit.Test
import org.expecty.Expecty

class ExpectySpec {
  val expect = new Expecty()
  val name = "Hi from Expecty!"

  @Test
  def passingExpectation(): Unit = {
    expect(name.length == 16)
  }

  @Test(expected = classOf[AssertionError])
  def failingExpectation(): Unit = {
    expect(name.length() == 10)
  }

  @Test
  def multiplePassingExpectations(): Unit = {
    expect(name.length == 16)
    expect(name.startsWith("Hi"))
    expect(name.endsWith("Expecty!"))
  }

  @Test(expected = classOf[AssertionError])
  def mixedPassingAndFailingExpectations(): Unit = {
    expect(name.length == 16)
    expect(name.startsWith("Ho"))
    expect(name.endsWith("Expecty!"))
  }

  @Test
  def passingMultiExpectation(): Unit = {
    expect {
      name.length == 16
      name.startsWith("Hi")
      name.endsWith("Expecty!")
    }
  }

  @Test(expected = classOf[AssertionError])
  def failingMultiExpectation(): Unit = {
    expect {
      name.length == 16
      name.startsWith("Ho")
      name.endsWith("Expecty!")
    }
  }

  //TODO: needs assertion
  // @Test(expected = classOf[AssertionError])
  // def lateFailingExpectation(): Unit = {
  //   def expect = new Expecty(failEarly = false)

  //   expect {
  //     name.length == 13
  //     name.startsWith("Ho")
  //     name.endsWith("Expcty!")
  //   }
  // }
}
