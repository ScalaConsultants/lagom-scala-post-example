package io.scalac.lagom.rates

import java.util.Optional

import akka.NotUsed
import com.lightbend.lagom.javadsl.persistence.PersistentEntity
import com.lightbend.lagom.serialization.Jsonable
import io.scalac.lagom.rates.api.ExchangeRatio
import io.scalac.lagom.utils.Implicits.{asJavaBiFunction, asJavaConsumer, asJavaFunction, asJavaBiConsumer}

sealed trait ExchangeCommand

case class SaveRatio(fromUnit: String, toUnit: String, ratio: BigDecimal)
  extends ExchangeCommand with PersistentEntity.ReplyType[NotUsed]

case class GetRatio(fromUnit: String, toUnit: String)
  extends ExchangeCommand with PersistentEntity.ReplyType[ExchangeRatio]

sealed trait ExchangeEvent extends Jsonable

case class RatioChanged(fromUnit: String, toUnit: String, ratio: BigDecimal) extends ExchangeEvent

case class ExchangeState(fromUnit: String,
                         toUnit: String,
                         ratio: BigDecimal)

object ExchangeStorage {
  implicit def saveRatioToRatioChanged(saveRatio: SaveRatio): RatioChanged =
    RatioChanged(
      fromUnit = saveRatio.fromUnit,
      toUnit = saveRatio.toUnit,
      ratio = saveRatio.ratio
    )

  implicit def ratioChangedToExchangeState(ratioChanged: RatioChanged): ExchangeState =
    ExchangeState(
      fromUnit = ratioChanged.fromUnit,
      toUnit = ratioChanged.toUnit,
      ratio = ratioChanged.ratio
    )
}

class ExchangeStorage extends PersistentEntity[ExchangeCommand, ExchangeEvent, Option[ExchangeState]] {

  import ExchangeStorage.{saveRatioToRatioChanged, ratioChangedToExchangeState}
  type ExchangeStorageSavedState = Option[ExchangeState]

  def initialBehavior(snapshotState: Optional[ExchangeStorageSavedState]): Behavior = {
    val builder = newBehaviorBuilder(snapshotState.orElse(None))

    builder.setCommandHandler(
      classOf[SaveRatio],
      asJavaBiFunction[SaveRatio, CommandContext[NotUsed], Persist[_ <: ExchangeEvent]](
        (cmd: SaveRatio, ctx: CommandContext[NotUsed]) => {
          ctx.thenPersist(
            saveRatioToRatioChanged(cmd),
            (t: ExchangeEvent) => ctx.reply(NotUsed)
          )
        }
      )
    )

    builder.setEventHandler(
      classOf[RatioChanged],
      (evt: RatioChanged) => Some(ratioChangedToExchangeState(evt))
    )

    builder.setReadOnlyCommandHandler(
      classOf[GetRatio],
      (cmd: GetRatio, ctx: ReadOnlyCommandContext[ExchangeRatio]) => ctx.reply(ExchangeRatio(state.map(_.ratio)))
    )

    builder.build()
  }
}
