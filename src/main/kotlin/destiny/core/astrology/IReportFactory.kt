package destiny.core.astrology

import destiny.core.astrology.prediction.EventSource
import destiny.core.calendar.GmtJulDay
import java.time.Duration
import java.time.LocalDate

interface IReportFactory {

  /** 針對某個時間點分析 */
  fun getTransitSolarArcModel(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    localDate: LocalDate,
    threshold: Double?,
    config: IPersonHoroscopeConfig
  ): TransitSolarArcModel

  /** 某段範圍時間內的事件 */
  fun getTimeLineEvents(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    viewGmtJulDay: GmtJulDay,
    fromTime: GmtJulDay,
    toTime: GmtJulDay,
    eventSources: Set<EventSource>,
    traversalConfig: AstrologyTraversalConfig,
    includeLunarReturn: Boolean,
    pastExtDays: Int,
    futureExtDays: Int,
    /** 內定以 natal points , 可以額外指定 */
    outerPoints: Set<AstroPoint> = personModel.points,
    innerPoints: Set<AstroPoint> = personModel.points,
  ): ITimeLineEventsModel

  /** 事件自動分群(依據相鄰事件) */
  fun getMergedUserEventsModel(extractedEvents: ExtractedEvents, viewDay: LocalDate, futureDuration: Duration? = null) : MergedUserEventsModel
}
