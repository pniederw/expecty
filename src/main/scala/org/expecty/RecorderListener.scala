package org.expecty

trait RecorderListener[T] {
  def valueRecorded(recordedValue: RecordedValue) {}
  def expressionRecorded(recordedExpr: RecordedExpression[T]) {}
  def recordingCompleted(recording: Recording[T]) {}
}
