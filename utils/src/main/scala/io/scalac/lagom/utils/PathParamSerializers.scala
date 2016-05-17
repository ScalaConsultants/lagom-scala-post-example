package io.scalac.lagom.utils

import com.lightbend.lagom.javadsl.api.deser.PathParamSerializer
import Implicits.asJavaFunction

object PathParamSerializers {

  lazy val BigDecimalSerializer: PathParamSerializer[BigDecimal] =
    com.lightbend.lagom.javadsl.api.deser.PathParamSerializers.required(
      "BigDecimal",
      (pathValue: String) => BigDecimal(pathValue),
      (v: BigDecimal) => v.toString
    )
}
