package org.expecty

case class Recording[T](value: T, recordedExprs: List[RecordedExpression[T]]) {

}
