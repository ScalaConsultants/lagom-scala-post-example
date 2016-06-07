package io.scalac.lagom.calculator

import com.google.inject.AbstractModule
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport
import io.scalac.lagom.calculator.api.CalculatorService
import io.scalac.lagom.rates.api.ExchangeRatesService

class CalculatorModule extends AbstractModule with ServiceGuiceSupport {
  override def configure(): Unit = {
    bindServices(serviceBinding(classOf[CalculatorService], classOf[CalculatorServiceImpl]))
    bindClient(classOf[ExchangeRatesService])
  }
}
