package org.expecty

object Expecty {
  implicit val opts = Options()
  def expect(cond: Boolean)(implicit opts: Options): Result = macro ExpectyMacro.expect
}

case class Options(isFail: Boolean = true, isFailEarly: Boolean = true, isTrace: Boolean = false, isShowTypes: Boolean = false) {
  def fail = copy(isFail = true)
  def noFail = copy(isFail = false)

  def failEarly = copy(isFailEarly = true)
  def failLate = copy(isFailEarly = false)

  def trace = copy(isTrace = true)
  def noTrace = copy(isTrace = false)

  def showTypes = copy(isShowTypes = true)
  def noShowTypes = copy(isShowTypes = false)
}

