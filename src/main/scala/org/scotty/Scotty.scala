package org.scotty

object Scotty {
  def expect(cond: Boolean) = macro ScottyMacro.expect
}
