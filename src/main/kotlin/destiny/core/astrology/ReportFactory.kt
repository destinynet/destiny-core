/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.astrology.prediction.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.toLmt
import destiny.core.electional.DayHourService
import destiny.tools.KotlinLogging
import jakarta.inject.Named
import java.time.LocalDate
import java.time.YearMonth


interface IReportFactory {
  fun getTransitSolarArcModel(
    bdnp: IBirthDataNamePlace,
    grain: BirthDataGrain,
    localDate: LocalDate,
    threshold: Double?,
    config: IPersonHoroscopeConfig
  ): TransitSolarArcModel

  fun getTimeLineEvents(
    bdnp: IBirthDataNamePlace,
    grain: BirthDataGrain,
    viewGmtJulDay: GmtJulDay,
    fromTime: GmtJulDay,
    toTime: GmtJulDay,
    eventSources: Set<EventSource>,
    config: IPersonHoroscopeConfig,
    includeLunarReturn: Boolean
  ): ITimeLineEventsModel

  fun getSmartTimelineEvents(
    bdnp: IBirthDataNamePlace,
    grain: BirthDataGrain,
    viewGmtJulDay: GmtJulDay,
    eventPoints: List<YearMonth>,
  )
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
    bdnp: IBirthDataNamePlace,
    grain: BirthDataGrain,
    localDate: LocalDate,
    threshold: Double?,
    config: IPersonHoroscopeConfig
  ): TransitSolarArcModel {
    val model: IPersonHoroscopeModel = personHoroscopeFeature.getPersonModel(bdnp, config)
    val viewGmt = localDate.atTime(12, 0).toGmtJulDay(bdnp.location)

    val innerConsiderHour = when (grain) {
      BirthDataGrain.DAY    -> false
      BirthDataGrain.MINUTE -> true
    }

    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      model.toPersonHoroscopeDto(viewGmt, RulerPtolemyImpl, aspectEffectiveModern, modernAspectCalculator, innerConsiderHour, config)
    }


    val solarArcModel = horoscopeFeature.getSolarArc(model, viewGmt, innerConsiderHour, modernAspectCalculator, threshold, config)

    val transitModel = horoscopeFeature.getModel(viewGmt, bdnp.location, config)

    val laterViewGmt = viewGmt.plus(0.01) // 大約 15 分鐘後
    val laterTransitModel = horoscopeFeature.getModel(laterViewGmt, bdnp.location, config)

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
    bdnp: IBirthDataNamePlace,
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

    val secondaryProgressionConvergentFrom = progressionSecondary.getConvergentTime(bdnp.gmtJulDay, fromTime)
    val secondaryProgressionConvergentTo = progressionSecondary.getConvergentTime(bdnp.gmtJulDay, toTime)

    val tertiaryProgressionConvergentFrom = progressionTertiary.getConvergentTime(bdnp.gmtJulDay, fromTime)
    val tertiaryProgressionConvergentTo = progressionTertiary.getConvergentTime(bdnp.gmtJulDay, toTime)

    val loc = bdnp.location
    logger.debug { "secondaryProgression convergentFrom = ${secondaryProgressionConvergentFrom.toLmt(loc, julDayResolver)}" }
    logger.debug { "secondaryProgression convergentTo   = ${secondaryProgressionConvergentTo.toLmt(loc, julDayResolver)}" }
    logger.debug { "tertiaryProgression  convergentFrom = ${tertiaryProgressionConvergentFrom.toLmt(loc, julDayResolver)}" }
    logger.debug { "tertiaryProgression  convergentTo   = ${tertiaryProgressionConvergentTo.toLmt(loc, julDayResolver)}" }

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
          bdnp,
          secondaryProgressionConvergentFrom,
          secondaryProgressionConvergentTo,
          bdnp.location,
          includeHour,
          astrologyConfig,
          eventsTraversalTransitImpl
        ).map { eventDto ->
          val divergentTime = progressionSecondary.getDivergentTime(bdnp.gmtJulDay, eventDto.begin)
          TimeLineEvent(EventSource.SECONDARY, eventDto as AstroEventDto, divergentTime)
        })
      }
      if (EventSource.TERTIARY in eventSources) {
        addAll(dayHourService.traverseAstrologyEvents(
          bdnp,
          tertiaryProgressionConvergentFrom,
          tertiaryProgressionConvergentTo,
          bdnp.location,
          includeHour,
          astrologyConfig,
          eventsTraversalTransitImpl
        ).map { eventDto ->
          val divergentTime = progressionTertiary.getDivergentTime(bdnp.gmtJulDay, eventDto.begin)
          TimeLineEvent(EventSource.TERTIARY, eventDto as AstroEventDto, divergentTime)
        })
      }
      if (EventSource.SOLAR_ARC in eventSources) {
        addAll(dayHourService.traverseAstrologyEvents(
          bdnp,
          fromTime,
          toTime,
          bdnp.location,
          includeHour,
          astrologyConfig,
          eventsTraversalSolarArcImpl
        ).map { eventDto ->
          TimeLineEvent(EventSource.SOLAR_ARC, eventDto as AstroEventDto, eventDto.begin)
        })
      }
    }.sortedBy { it.divergentTime }.toList()


    val model: IPersonHoroscopeModel = personHoroscopeFeature.getPersonModel(bdnp, config)
    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      model.toPersonHoroscopeDto(viewGmtJulDay , RulerPtolemyImpl, aspectEffectiveModern, modernAspectCalculator, includeHour, config)
    }

    val solarReturnContext = ReturnContext(Planet.SUN, starPositionImpl, starTransitImpl, horoscopeFeature, dtoFactory, true, 0.0, false)

    val threshold = 0.9

    val returnChartIncludeClassical = false

    val solarReturns = with(solarReturnContext) {
      generateSequence(model.getReturnDto(fromTime, bdnp.location, aspectEffectiveModern, modernAspectCalculator, config, bdnp.place, threshold, returnChartIncludeClassical)) { returnDto: IReturnDto ->
        val nextFromTime = if (solarReturnContext.forward)
          returnDto.validTo + 1
        else
          returnDto.validFrom - 1
        model.getReturnDto(nextFromTime, bdnp.location, aspectEffectiveModern, modernAspectCalculator, config, bdnp.place, threshold, returnChartIncludeClassical)
      }.takeWhile { returnDto ->
        returnDto.validFrom in fromTime..toTime || returnDto.validTo in fromTime..toTime
      }
    }


    val lunarReturns = if (includeLunarReturn) {
      val lunarReturnContext = ReturnContext(Planet.MOON, starPositionImpl, starTransitImpl, horoscopeFeature, dtoFactory, true, 0.0, returnChartIncludeClassical)

      with(lunarReturnContext) {
        generateSequence(
          model.getReturnDto(
            fromTime,
            bdnp.location,
            aspectEffectiveModern,
            modernAspectCalculator,
            config,
            bdnp.place,
            threshold,
            returnChartIncludeClassical
          )
        ) { returnDto: IReturnDto ->
          val nextFromTime = if (lunarReturnContext.forward)
            returnDto.validTo + 1
          else
            returnDto.validFrom - 1
          model.getReturnDto(nextFromTime, bdnp.location, aspectEffectiveModern, modernAspectCalculator, config, bdnp.place, threshold, false)
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

  override fun getSmartTimelineEvents(bdnp: IBirthDataNamePlace, grain: BirthDataGrain, viewGmtJulDay: GmtJulDay, eventPoints: List<YearMonth>) {

    val monthRanges: List<YearMonth> = eventPoints.flatMap { ym ->
      listOf(ym.minusMonths(1), ym, ym.plusMonths(1))
    }.distinct()

    TODO("Not yet implemented")
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
