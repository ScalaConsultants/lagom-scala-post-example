package io.scalac.lagom.rates

import com.lightbend.lagom.serialization.Jsonable

package object api {

  case class ExchangeRatio(ratio: Option[BigDecimal]) extends Jsonable

  case class Rate(rate: BigDecimal) extends Jsonable

}
