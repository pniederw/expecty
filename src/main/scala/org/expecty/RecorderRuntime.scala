package org.expecty

// one instance per recording
class RecorderRuntime(listener: RecorderListener[Boolean]) {
  var recordedValues: List[RecordedValue] = _
  var recordedExprs: List[RecordedExpression[Boolean]] = List.empty

  def resetValues() {
    recordedValues = List.empty
  }

  def recordValue[U](value: U, anchor: Int): U = {
    val recordedValue = RecordedValue(value, anchor)
    listener.valueRecorded(recordedValue)
    recordedValues = recordedValue :: recordedValues
    value
  }

  def recordExpression(text: String, ast: String, value: Boolean) {
    val recordedExpr = RecordedExpression(text, ast, value, recordedValues)
    listener.expressionRecorded(recordedExpr)
    recordedExprs = recordedExpr :: recordedExprs
  }

  def completeRecording(): Boolean = {
    val lastRecorded = recordedExprs.head
    val recording = Recording(lastRecorded.value, recordedExprs)
    listener.recordingCompleted(recording)
    recording.value
  }
}
