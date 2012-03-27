package org.expecty

// should have two type parameters; one for recorded type, another for returned type
// so far failed to implement the macro side of this
abstract class Recorder {
  val listener: RecorderListener[Boolean]
  def apply(recording: Boolean): Boolean = macro RecorderMacro.apply
}
