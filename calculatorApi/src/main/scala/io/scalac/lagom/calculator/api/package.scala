package io.scalac.lagom.calculator

import com.lightbend.lagom.serialization.Jsonable

package object api {

  case class CalculatedValue(value: BigDecimal,
                             currencyUnit: String) extends Jsonable

}
