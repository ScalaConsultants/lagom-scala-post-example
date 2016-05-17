package io.scalac.lagom.calculator.api

import akka.NotUsed
import com.lightbend.lagom.javadsl.api.ScalaService._
import com.lightbend.lagom.javadsl.api.transport.Method
import com.lightbend.lagom.javadsl.api.{Descriptor, Service, ServiceCall}
import io.scalac.lagom.utils.{MicroserviceExceptionSerializer, PathParamSerializers}

trait CalculatorService extends Service {

  def calculate(fromValue: BigDecimal,
                fromUnit: String,
                toUnit: String
               ): ServiceCall[NotUsed, CalculatedValue]

  def descriptor(): Descriptor =
    named("calculatorservice")
      .`with`(
        restCall(Method.GET, "/api/calculator/exchange?fromValue&fromUnit&toUnit", calculate _)
      )
      .`with`(classOf[BigDecimal], PathParamSerializers.BigDecimalSerializer)
      .`with`(new MicroserviceExceptionSerializer)
      .withAutoAcl(true)
}
