package io.scalac.lagom.rates

import com.google.inject.AbstractModule
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport
import io.scalac.lagom.rates.api.ExchangeRatesService

class ExchangeRatesModule extends AbstractModule with ServiceGuiceSupport {
  def configure(): Unit = {
    bindServices(serviceBinding(classOf[ExchangeRatesService], classOf[ExchangeRatesServiceImpl]))
  }
}
