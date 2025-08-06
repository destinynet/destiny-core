/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology

import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.astrology.prediction.EventSource
import destiny.core.astrology.prediction.ProgressionSecondary
import destiny.core.astrology.prediction.ProgressionTertiary
import destiny.core.astrology.prediction.ReturnContext
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.toLmt
import destiny.core.electional.DayHourService
import destiny.tools.KotlinLogging
import jakarta.inject.Named
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
    config: IPersonHoroscopeConfig,
    includeLunarReturn: Boolean,
    extDays: Int,
  ): ITimeLineEventsModel

  /** 事件自動分群(依據相鄰事件) */
  fun getMergedUserEventsModel(extractedEvents: ExtractedEvents, viewDay: LocalDate, futureDuration: Duration?) : MergedUserEventsModel
}

@Named
class ReportFactory(
  private val horoscopeFeature: IHoroscopeFeature,
  private val aspectEffectiveModern: IAspectEffective,
  private val modernAspectCalculator: IAspectCalculator,
  private val dtoFactory: DtoFactory,
  private val dayHourService: DayHourService,
  private val julDayResolver: JulDayResolver,
  private val eventsTraversalSolarArcImpl: IEventsTraversal,
  private val eventsTraversalTransitImpl: IEventsTraversal,
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val personHoroscopeFeature: IPersonHoroscopeFeature,
) : IReportFactory {
  override fun getTransitSolarArcModel(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    localDate: LocalDate,
    threshold: Double?,
    config: IPersonHoroscopeConfig
  ): TransitSolarArcModel {
    val viewGmt = localDate.atTime(12, 0).toGmtJulDay(personModel.location)

    val innerConsiderHour = when (grain) {
      BirthDataGrain.DAY    -> false
      BirthDataGrain.MINUTE -> true
    }

    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      personModel.toPersonHoroscopeDto(grain, viewGmt, RulerPtolemyImpl, aspectEffectiveModern, modernAspectCalculator, config)
    }

    val solarArcModel = horoscopeFeature.getSolarArc(personModel, viewGmt, innerConsiderHour, modernAspectCalculator, threshold, config)

    val transitModel = horoscopeFeature.getModel(viewGmt, personModel.location, config)

    val laterViewGmt = viewGmt.plus(0.01) // 大約 15 分鐘後
    val laterTransitModel = horoscopeFeature.getModel(laterViewGmt, personModel.location, config)

    // 準備 lambda 函式
    val laterForTransit: ((AstroPoint) -> IZodiacDegree?) = { p -> laterTransitModel.positionMap[p] }
    // Solar Arc 盤在一天內是固定的，所以它的 "later" 位置就是它自己
    val laterForSA: ((AstroPoint) -> IZodiacDegree?) = { p -> solarArcModel.positionMap[p] }

    // SA 的 house 沒有意義，因此只能用 coarse 版本
    val transitToSolarArcAspects: List<SynastryAspect> = horoscopeFeature.synastryAspectsCoarse(
      transitModel.positionMap, solarArcModel.positionMap,
      laterForTransit, laterForSA,
      modernAspectCalculator, threshold, Aspect.getAspects(Aspect.Importance.HIGH).toSet()
    )

    return TransitSolarArcModel(natal, grain, localDate, solarArcModel, transitToSolarArcAspects)
  }

  override fun getTimeLineEvents(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    viewGmtJulDay: GmtJulDay,
    fromTime: GmtJulDay,
    toTime: GmtJulDay,
    eventSources: Set<EventSource>,
    config: IPersonHoroscopeConfig,
    includeLunarReturn: Boolean,
    extDays: Int
  ): ITimeLineEventsModel {
    val progressionSecondary = ProgressionSecondary()
    val progressionTertiary = ProgressionTertiary()

    val secondaryProgressionConvergentFrom = progressionSecondary.getConvergentTime(personModel.gmtJulDay, fromTime - extDays)
    val secondaryProgressionConvergentTo = progressionSecondary.getConvergentTime(personModel.gmtJulDay, toTime + extDays)

    val tertiaryProgressionConvergentFrom = progressionTertiary.getConvergentTime(personModel.gmtJulDay, fromTime - extDays)
    val tertiaryProgressionConvergentTo = progressionTertiary.getConvergentTime(personModel.gmtJulDay, toTime + extDays)

    val loc = personModel.location
    logger.debug { "secondaryProgression convergentFrom = ${secondaryProgressionConvergentFrom.toLmt(loc, julDayResolver)}" }
    logger.debug { "secondaryProgression convergentTo   = ${secondaryProgressionConvergentTo.toLmt(loc, julDayResolver)}" }
    logger.trace { "tertiaryProgression  convergentFrom = ${tertiaryProgressionConvergentFrom.toLmt(loc, julDayResolver)}" }
    logger.trace { "tertiaryProgression  convergentTo   = ${tertiaryProgressionConvergentTo.toLmt(loc, julDayResolver)}" }

    val astrologyConfig = AstrologyTraversalConfig(
      aspect = true,
      voc = false,
      stationary = true,
      retrograde = false,
      eclipse = true,
      lunarPhase = true,
      includeTransitToNatalAspects = false,
      signIngress = true,
      houseIngress = true,
    )

    val includeHour = when (grain) {
      BirthDataGrain.DAY    -> false
      BirthDataGrain.MINUTE -> true
    }

    val events = buildSet {
      if (EventSource.SECONDARY in eventSources) {
        addAll(dayHourService.traverseAstrologyEvents(
          personModel,
          secondaryProgressionConvergentFrom,
          secondaryProgressionConvergentTo,
          personModel.location,
          includeHour,
          astrologyConfig,
          eventsTraversalTransitImpl
        ).map { eventDto ->
          val divergentTime = progressionSecondary.getDivergentTime(personModel.gmtJulDay, eventDto.begin)
          TimeLineEvent(EventSource.SECONDARY, eventDto as AstroEventDto, divergentTime)
        })
      }
      if (EventSource.TERTIARY in eventSources) {
        addAll(dayHourService.traverseAstrologyEvents(
          personModel,
          tertiaryProgressionConvergentFrom,
          tertiaryProgressionConvergentTo,
          personModel.location,
          includeHour,
          astrologyConfig,
          eventsTraversalTransitImpl
        ).map { eventDto ->
          val divergentTime = progressionTertiary.getDivergentTime(personModel.gmtJulDay, eventDto.begin)
          TimeLineEvent(EventSource.TERTIARY, eventDto as AstroEventDto, divergentTime)
        })
      }
      if (EventSource.SOLAR_ARC in eventSources) {
        addAll(dayHourService.traverseAstrologyEvents(
          personModel,
          fromTime,
          toTime,
          personModel.location,
          includeHour,
          astrologyConfig,
          eventsTraversalSolarArcImpl
        ).map { eventDto ->
          TimeLineEvent(EventSource.SOLAR_ARC, eventDto as AstroEventDto, eventDto.begin)
        })
      }
    }.sortedBy { it.divergentTime }.toList()

    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      personModel.toPersonHoroscopeDto(grain, viewGmtJulDay, RulerPtolemyImpl, aspectEffectiveModern, modernAspectCalculator, config)
    }

    val threshold = 0.9
    val returnChartIncludeClassical = false

    val lunarReturns = if (includeLunarReturn) {
      val lunarReturnContext = ReturnContext(Planet.MOON, starPositionImpl, starTransitImpl, horoscopeFeature, dtoFactory, true, 0.0, returnChartIncludeClassical)
      lunarReturnContext.getRangedReturns(personModel, grain, fromTime, toTime, aspectEffectiveModern, modernAspectCalculator, config, threshold, returnChartIncludeClassical)
    } else {
      emptySequence()
    }

    val returnCharts = lunarReturns.sortedBy { it.validFrom }.toList()

    return TimeLineEventsModel(natal, grain, fromTime, toTime, events, returnCharts)
  }

  /** 事件自動分群(依據相鄰事件) */
  override fun getMergedUserEventsModel(extractedEvents: ExtractedEvents, viewDay: LocalDate, futureDuration: Duration?): MergedUserEventsModel {
    val loc = extractedEvents.location

    val grain = if (extractedEvents.hourMinute != null) BirthDataGrain.MINUTE else BirthDataGrain.DAY

    val viewGmtJulDay = viewDay.atStartOfDay().toGmtJulDay(loc)

    val config = PersonHoroscopeConfig()
    val model: IPersonHoroscopeModel = personHoroscopeFeature.getPersonModel(extractedEvents, config)

    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      model.toPersonHoroscopeDto(grain, viewGmtJulDay, RulerPtolemyImpl, aspectEffectiveModern, modernAspectCalculator, config)
    }

    val eventSources = setOf(
      EventSource.SECONDARY,
      EventSource.SOLAR_ARC
    )

    val eventGroups: List<EventGroup> = extractedEvents.events.groupAdjacentEvents(extMonth = 1).map { groupedEvent: List<YearMonthEvent> ->
      val (from, to) = groupedEvent.sortedBy { it.yearMonth }
        .let { (it.first().yearMonth.atDay(1).atStartOfDay().toGmtJulDay(loc) to it.last().yearMonth.plusMonths(1).atDay(1).atStartOfDay().toGmtJulDay(loc)) }
      val timeLineEvents: ITimeLineEventsModel = getTimeLineEvents(
        model, grain, viewGmtJulDay,
        from, to,
        eventSources, config,
        includeLunarReturn = true,
        extDays = 30 // 前後延伸一個月
      )
      EventGroup(from, to, groupedEvent, timeLineEvents.events, timeLineEvents.lunarReturns)
    }

    val threshold = 0.9
    val returnChartIncludeClassical = false

    val solarReturnContext = ReturnContext(Planet.SUN, starPositionImpl, starTransitImpl, horoscopeFeature, dtoFactory, true, 0.0, false)

    val fromTime = eventGroups.first().fromTime
    val toTime = eventGroups.last().toTime

    val solarReturns = solarReturnContext.getRangedReturns(model, grain, fromTime, toTime, aspectEffectiveModern, modernAspectCalculator, config, threshold, returnChartIncludeClassical).toList()

    val past = Past(eventGroups, solarReturns)

    if (futureDuration != null) {
      val futureFromTime = viewDay.atStartOfDay().toGmtJulDay(loc)
      val futureToTime = viewDay.plus(futureDuration).plusDays(1).atStartOfDay().toGmtJulDay(loc)
      val futureTimeLineEvents = getTimeLineEvents(model, grain, viewGmtJulDay, futureFromTime, futureToTime, eventSources, config, true, 30)

      val futureSolarReturns = solarReturnContext.getRangedReturns(model, grain, futureToTime, futureToTime, aspectEffectiveModern, modernAspectCalculator, config, threshold, returnChartIncludeClassical).toList()
      Future(futureToTime, futureToTime, futureTimeLineEvents.events, futureTimeLineEvents.lunarReturns, futureSolarReturns)
    }


    return MergedUserEventsModel(natal, grain, extractedEvents.intro, past, viewDay)
  }



  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
