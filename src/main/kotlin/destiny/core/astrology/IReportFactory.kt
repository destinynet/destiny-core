package destiny.core.astrology

import destiny.core.Scale
import destiny.core.astrology.prediction.EventSourceConfig
import destiny.core.calendar.GmtJulDay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

interface IReportFactory {

  /** 針對某個時間點分析 */
  fun getTransitSolarArcModel(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    localDate: LocalDate,
    threshold: Double?,
    config: IPersonHoroscopeConfig
  ): TransitSolarArcModel

  /** 某日期(or with 時間) 的時間點分析 */
  fun getEventModel(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    localDate: LocalDate,
    localTime: LocalTime?,
    threshold: Double?,
    config: IPersonHoroscopeConfig
  ): EventModel

  /** 某段範圍時間內的事件 */
  fun getTimeLineEvents(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    viewGmtJulDay: GmtJulDay,
    fromTime: GmtJulDay,
    toTime: GmtJulDay,
    eventSourceConfigs: Set<EventSourceConfig>,
    traversalConfig: AstrologyTraversalConfig,
    /** 內定以 natal points , 可以額外指定 */
    transitingPoints: Set<AstroPoint> = personModel.points,
    natalTargetPoints: Set<AstroPoint> = personModel.points,
    withLunarReturns : Boolean = true
  ): ITimeLineEventsModel

  /**
   * 事件自動分群(依據相鄰事件)
   *
   * @param negativeControlMonths 負對照窗的月份 —— 該月不屬於任何已記錄事件，以與事件群
   *   **完全相同**的掃描層產出，落在 [Past.negativeControlGroups]。選月請用預先宣告的規則
   *   而非逐月手挑：挑到星象特別乾淨的月份，等於倒過來製造倖存者偏差。
   */
  fun getMergedUserEventsModel(
    extractedEvents: ExtractedEvents,
    eventScaleConfigs: Map<Scale, Set<EventSourceConfig>>,
    viewDay: LocalDate,
    futureDuration: Duration? = null,
    longTermFromTime: GmtJulDay? = null,
    longTermToTime: GmtJulDay? = null,
    negativeControlMonths: Set<YearMonth> = emptySet(),
  ): MergedUserEventsModel
}
