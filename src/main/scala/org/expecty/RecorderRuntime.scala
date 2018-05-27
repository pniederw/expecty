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
package org.expecty

// one instance per recording
class RecorderRuntime(listener: RecorderListener[Boolean]) {
  var recordedValues: List[RecordedValue] = _
  var recordedExprs: List[RecordedExpression[Boolean]] = List.empty

  def resetValues(): Unit = {
    recordedValues = List.empty
  }

  def recordValue[U](value: U, anchor: Int): U = {
    val recordedValue = RecordedValue(value, anchor)
    listener.valueRecorded(recordedValue)
    recordedValues = recordedValue :: recordedValues
    value
  }

  def recordExpression(text: String, ast: String, value: Boolean): Unit = {
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
