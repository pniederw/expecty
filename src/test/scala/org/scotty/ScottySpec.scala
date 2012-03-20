package org.scotty

import org.scotty.Scotty.expect
import org.junit.Test

class ScottySpec {
  @Test
  def validCondition() {
    val name = "Hi from Scotty!"
    expect(name.length == 15)
  }

  @Test
  def invalidCondition() {
    val name = "Hi from Scotty!"
    expect(name.length == 10)
  }
}
