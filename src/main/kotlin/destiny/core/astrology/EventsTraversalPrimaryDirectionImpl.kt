/**
 * Created by smallufo on 2025-06-29.
 */
package destiny.core.astrology

import destiny.core.asLocaleString
import destiny.core.astrology.prediction.IPrimaryDirection
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.electional.Impact
import destiny.core.electional.Span
import destiny.tools.KotlinLogging
import destiny.tools.getTitle
import jakarta.inject.Named
import java.util.Locale

/**
 * 主限法 (Primary Direction) 的推運事件遍歷。
 *
 * 委派 [IPrimaryDirection] 算出一生的主限事件 (arc→年→約略日期)，再篩選落在
 * [fromGmtJulDay]..[toGmtJulDay] 區間者，轉為 [AstroEventDto]。
 *
 * 主示星 / 承諾星 / 相位 / 方法 / 順逆 / 時間鑰匙 皆取自
 * [AstrologyTraversalConfig.primaryDirectionConfig] (仿 SolarArc 取自 solarArcConfig)。
 */
@Named
class EventsTraversalPrimaryDirectionImpl(
  private val primaryDirection: IPrimaryDirection,
) : IEventsTraversal {

  override fun traverse(
    model: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    grain: BirthDataGrain,
    config: AstrologyTraversalConfig,
    transitingPoints: Set<AstroPoint>,
    natalTargetPoints: Set<AstroPoint>,
  ): Sequence<AstroEventDto> {

    if (toGmtJulDay < model.gmtJulDay) {
      logger.warn { "Primary Direction time range invalid: toGmtJulDay=$toGmtJulDay before birth ${model.gmtJulDay}" }
      return emptySequence()
    }

    val pd = config.primaryDirectionConfig

    val events = primaryDirection.getDirectionEvents(
      natalChart = model,
      significators = pd.significators,
      promissors = pd.promissors,
      aspects = pd.aspects,
      method = pd.method,
      forward = pd.forward,
      timeKey = pd.timeKey,
    )

    return events.asSequence()
      .filter { it.eventGmt in fromGmtJulDay..toGmtJulDay }
      .map { ev ->
        val promissorName = ev.promissor.asLocaleString().getTitle(Locale.ENGLISH)
        val significatorName = ev.significator.asLocaleString().getTitle(Locale.ENGLISH)
        val description = "[PD $promissorName] ${ev.aspect} [natal $significatorName]" +
          if (ev.forward) " (direct)" else " (converse)"

        val pattern = PointAspectPattern(listOf(ev.promissor, ev.significator), ev.aspect.degree, null, 0.0)
        val aspectData = AspectData(pattern, null, 0.0, null, ev.eventGmt)

        AstroEventDto(AstroEvent.AspectEvent(description, aspectData), ev.eventGmt, null, Span.INSTANT, Impact.PERSONAL)
      }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
