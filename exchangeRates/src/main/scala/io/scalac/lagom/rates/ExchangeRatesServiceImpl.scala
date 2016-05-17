package io.scalac.lagom.rates

import javax.inject.Inject

import akka.NotUsed
import com.lightbend.lagom.javadsl.api.ServiceCall
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry
import io.scalac.lagom.rates.api.{ExchangeRatesService, ExchangeRatio, Rate}
import io.scalac.lagom.utils.Implicits.{asJavaBiFunction, requestToServiceCall}

class ExchangeRatesServiceImpl @Inject()(persistentEntityRegistry: PersistentEntityRegistry) extends ExchangeRatesService {

  persistentEntityRegistry.register(classOf[ExchangeStorage])

  override def getExchangeRate(fromUnit: String, toUnit: String): ServiceCall[NotUsed, ExchangeRatio] =
    (_: NotUsed) => {
      val ref = persistentEntityRegistry.refFor(classOf[ExchangeStorage], s"$fromUnit-$toUnit")

      ref.ask[ExchangeRatio, GetRatio](GetRatio(fromUnit, toUnit))
    }

  override def setExchangeRate(fromUnit: String, toUnit: String): ServiceCall[Rate, NotUsed] =
    requestToServiceCall((rate: Rate) => {
      val fromToStorageRef = persistentEntityRegistry.refFor(classOf[ExchangeStorage], s"$fromUnit-$toUnit")
      val toFromStorageRef = persistentEntityRegistry.refFor(classOf[ExchangeStorage], s"$toUnit-$fromUnit")

      fromToStorageRef.ask[NotUsed, SaveRatio](SaveRatio(fromUnit, toUnit, rate.rate)).thenCombine(
        toFromStorageRef.ask[NotUsed, SaveRatio](SaveRatio(toUnit, fromUnit, 1 / rate.rate)),
        (_: NotUsed, _: NotUsed) => NotUsed
      )
    })
}
