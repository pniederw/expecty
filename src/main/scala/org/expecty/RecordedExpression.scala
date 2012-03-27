package org.expecty

// might hold more information in the future (for example the kind of expression),
// or might be turned into an expression tree
case class RecordedExpression[T](text: String, ast: String, value: T, recordedValues: List[RecordedValue]) {
}


