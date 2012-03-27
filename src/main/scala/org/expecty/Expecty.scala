package org.expecty

class Expecty(failEarly: Boolean = true, showTypes: Boolean = false) extends Recorder {
  class ExpectyListener extends RecorderListener[Boolean] {
    override def expressionRecorded(recordedExpr: RecordedExpression[Boolean]) {
      if (!recordedExpr.value && failEarly) {
        val renderer = new ExpressionRenderer(showTypes)
        val rendering = renderer.render(recordedExpr)
        throw new AssertionError(rendering)
      }
    }

    override def recordingCompleted(recording: Recording[Boolean]) {
      if (!failEarly) {
        val failedExprs = recording.recordedExprs.filter(!_.value)
        if (!failedExprs.isEmpty) {
          val renderer = new ExpressionRenderer(showTypes)
          val renderings = failedExprs.reverse.map(renderer.render(_))
          throw new AssertionError(renderings.mkString(""))
        }
      }
    }
  }

  val listener = new ExpectyListener
}


