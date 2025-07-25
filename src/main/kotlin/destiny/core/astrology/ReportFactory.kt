/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology

import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.astrology.prediction.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.chinese.YearMonthRange
import destiny.core.calendar.chinese.groupMergedRanges
import destiny.core.calendar.toLmt
import destiny.core.electional.DayHourService
import destiny.tools.KotlinLogging
import jakarta.inject.Named
import java.time.LocalDate
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

  /** 某段範圍時間內的事件 */
  fun getTimeLineEvents(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    viewGmtJulDay: GmtJulDay,
    fromTime: GmtJulDay,
    toTime: GmtJulDay,
    eventSources: Set<EventSource>,
    config: IPersonHoroscopeConfig,
    includeLunarReturn: Boolean
  ): ITimeLineEventsModel

  /** 根據 [eventPoints] 擷取前後一個月 , 排出事件前後重要星盤 */
  fun getSmartTimelineEvents(
    personModel: IPersonHoroscopeModel,
    grain: BirthDataGrain,
    viewGmtJulDay: GmtJulDay,
    eventPoints: List<YearMonth>,
  ): ITimeLineEventsModel
}

@Named
class ReportFactory(
  private val personHoroscopeFeature: IPersonHoroscopeFeature,
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
    includeLunarReturn: Boolean
  ): ITimeLineEventsModel {
    val progressionSecondary = ProgressionSecondary()
    val progressionTertiary = ProgressionTertiary()

    val secondaryProgressionConvergentFrom = progressionSecondary.getConvergentTime(personModel.gmtJulDay, fromTime)
    val secondaryProgressionConvergentTo = progressionSecondary.getConvergentTime(personModel.gmtJulDay, toTime)

    val tertiaryProgressionConvergentFrom = progressionTertiary.getConvergentTime(personModel.gmtJulDay, fromTime)
    val tertiaryProgressionConvergentTo = progressionTertiary.getConvergentTime(personModel.gmtJulDay, toTime)

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

    val solarReturnContext = ReturnContext(Planet.SUN, starPositionImpl, starTransitImpl, horoscopeFeature, dtoFactory, true, 0.0, false)

    val threshold = 0.9

    val returnChartIncludeClassical = false

    val solarReturns = with(solarReturnContext) {
      generateSequence(personModel.getReturnDto(
        grain , fromTime,
        personModel.location,
        aspectEffectiveModern,
        modernAspectCalculator,
        config,
        personModel.place,
        threshold,
        returnChartIncludeClassical
      )) { returnDto: IReturnDto ->
        val nextFromTime = if (solarReturnContext.forward)
          returnDto.validTo + 1
        else
          returnDto.validFrom - 1
        personModel.getReturnDto(
          grain , nextFromTime,
          personModel.location,
          aspectEffectiveModern,
          modernAspectCalculator,
          config,
          personModel.place,
          threshold,
          returnChartIncludeClassical
        )
      }.takeWhile { returnDto ->
        returnDto.validFrom in fromTime..toTime || returnDto.validTo in fromTime..toTime
      }
    }


    val lunarReturns = if (includeLunarReturn) {
      val lunarReturnContext = ReturnContext(Planet.MOON, starPositionImpl, starTransitImpl, horoscopeFeature, dtoFactory, true, 0.0, returnChartIncludeClassical)

      with(lunarReturnContext) {
        generateSequence(
          personModel.getReturnDto(
            grain,
            fromTime,
            personModel.location,
            aspectEffectiveModern,
            modernAspectCalculator,
            config,
            personModel.place,
            threshold,
            returnChartIncludeClassical
          )
        ) { returnDto: IReturnDto ->
          val nextFromTime = if (lunarReturnContext.forward)
            returnDto.validTo + 1
          else
            returnDto.validFrom - 1
          personModel.getReturnDto(grain, nextFromTime, personModel.location, aspectEffectiveModern, modernAspectCalculator, config, personModel.place, threshold, false)
        }.takeWhile { returnDto ->
          returnDto.validFrom in fromTime..toTime || returnDto.validTo in fromTime..toTime
        }
      }
    } else {
      emptySequence()
    }

    val returnCharts = sequenceOf(solarReturns, lunarReturns).flatten().sortedBy { it.validFrom }.toList()

    return TimeLineEventsModel(natal, grain, fromTime, toTime, events, returnCharts)
  }

  override fun getSmartTimelineEvents(personModel: IPersonHoroscopeModel, grain: BirthDataGrain, viewGmtJulDay: GmtJulDay, eventPoints: List<YearMonth>): ITimeLineEventsModel {

    val eventSources = setOf(
      EventSource.SECONDARY,
      EventSource.SOLAR_ARC
    )
    val config = PersonHoroscopeConfig()

    val mergedRanges: List<YearMonthRange> = eventPoints.groupMergedRanges(1)
    mergedRanges.forEach {
      logger.info { "YearMonthRange : ${it.start} to ${it.endInclusive}" }
    }

    val (events: List<ITimeLineEvent>, returnCharts: List<IReturnDto>) = mergedRanges.map { ymr: YearMonthRange ->
      val fromGmt = ymr.fromTime.toGmtJulDay(personModel.location)
      val toGmt = ymr.toTime.toGmtJulDay(personModel.location)
      getTimeLineEvents(personModel, grain, viewGmtJulDay, fromGmt, toGmt, eventSources, config, includeLunarReturn = true).let {
        it.events to it.returnCharts
      }
    }.unzip().let { (eventsList, returnChartsList) ->
      eventsList.flatten() to returnChartsList.flatten()
    }

    val (from , to) = mergedRanges.let {
      it.first().fromTime.toGmtJulDay(personModel.location) to it.last().toTime.toGmtJulDay(personModel.location)
    }

    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      personModel.toPersonHoroscopeDto(grain, viewGmtJulDay, RulerPtolemyImpl, aspectEffectiveModern, modernAspectCalculator, config)
    }

    return TimeLineEventsModel(natal, grain, from, to, events, returnCharts)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
