package io.scalac.lagom.calculator

import java.util.concurrent.CompletableFuture
import javax.inject.Inject

import akka.NotUsed
import com.lightbend.lagom.javadsl.api.ServiceCall
import io.scalac.lagom.calculator.api.{CalculatedValue, CalculatorService}
import io.scalac.lagom.rates.api.{ExchangeRatesService, ExchangeRatio}
import io.scalac.lagom.utils.Implicits._
import io.scalac.lagom.utils.ServerError

class CalculatorServiceImpl @Inject()(exchangeRatesService: ExchangeRatesService) extends CalculatorService {
  def calculate(fromValue: BigDecimal, fromUnit: String, toUnit: String): ServiceCall[NotUsed, CalculatedValue] =
    (_: NotUsed) => {
      exchangeRatesService.getExchangeRate(fromUnit, toUnit).invoke().thenCompose(
        (v1: ExchangeRatio) => v1.ratio match {
          case Some(ratio) => CompletableFuture.completedFuture[CalculatedValue](CalculatedValue(ratio, toUnit))
          case _ => {
            val v = new CompletableFuture[CalculatedValue]()
            v.completeExceptionally(ServerError(s"Could not retrive a ratio for $fromUnit-$toUnit"))
            v
          }
        }
      )
    }
}
