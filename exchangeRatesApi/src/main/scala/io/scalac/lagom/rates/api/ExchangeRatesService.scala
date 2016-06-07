package io.scalac.lagom.rates.api

import akka.NotUsed
import com.lightbend.lagom.javadsl.api.ScalaService._
import com.lightbend.lagom.javadsl.api.transport.Method
import com.lightbend.lagom.javadsl.api.{Descriptor, Service, ServiceCall}
import io.scalac.lagom.utils.MicroserviceExceptionSerializer

trait ExchangeRatesService extends Service {

  def getExchangeRate(fromUnit: String,
                      toUnit: String
                     ): ServiceCall[NotUsed, ExchangeRatio]

  def setExchangeRate(fromUnit: String,
                      toUnit: String
                     ): ServiceCall[Rate, NotUsed]

  def descriptor(): Descriptor =
    named("exchangerates")
      .`with`(
        restCall(Method.GET, "/api/exchangerates/:fromUnit/:toUnit", getExchangeRate _),
        restCall(Method.PUT, "/api/exchangerates/:fromUnit/:toUnit", setExchangeRate _)
      )
      .`with`(new MicroserviceExceptionSerializer)
      .withAutoAcl(true)
}
