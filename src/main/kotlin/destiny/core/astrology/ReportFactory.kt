/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.astrology.prediction.ProgressionSecondary
import destiny.core.astrology.prediction.ProgressionTertiary
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.toLmt
import destiny.core.electional.AstroEventDto
import destiny.core.electional.DayHourService
import destiny.tools.KotlinLogging
import jakarta.inject.Named
import java.time.LocalDate


interface IReportFactory {
  fun getTransitSolarArcModel(
    bdnp: IBirthDataNamePlace,
    grain: BirthDataGrain,
    localDate: LocalDate,
    threshold: Double?,
    config: IPersonHoroscopeConfig
  ): TransitSolarArcModel

  fun getProgressionAstroEventsModel(
    bdnp: IBirthDataNamePlace,
    grain: BirthDataGrain,
    fromTime: GmtJulDay,
    toTime: GmtJulDay,
    config: IPersonHoroscopeConfig
  ): ProgressionAstroEventsModel
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
    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      model.toPersonHoroscopeDto(viewGmt, RulerPtolemyImpl, aspectEffectiveModern, modernAspectCalculator, config)
    }

    val innerConsiderHour = when (grain) {
      BirthDataGrain.DAY    -> false
      BirthDataGrain.MINUTE -> true
    }

    val solarArcModel = horoscopeFeature.getSolarArc(model, viewGmt, innerConsiderHour, modernAspectCalculator, threshold, config)

    val transitModel = horoscopeFeature.getModel(viewGmt, bdnp.location, config)

    val laterViewGmt = viewGmt.plus(0.01) // 大約 15 分鐘後
    val laterTransitModel = horoscopeFeature.getModel(laterViewGmt, bdnp.location, config)

    // 準備 lambda 函式
    val laterForTransit: ((AstroPoint) -> IZodiacDegree?) = { p -> laterTransitModel.positionMap[p] }
    // Solar Arc 盤在一天內是固定的，所以它的 "later" 位置就是它自己
    val laterForSA: ((AstroPoint) -> IZodiacDegree?) = { p -> solarArcModel.positionMap[p] }

    val transitToSolarArcAspects: List<SynastryAspect> = horoscopeFeature.synastryAspects(
      transitModel.positionMap, solarArcModel.positionMap,
      laterForTransit, laterForSA,
      modernAspectCalculator, threshold, Aspect.getAspects(Aspect.Importance.HIGH).toSet()
    )

    return TransitSolarArcModel(natal, grain, localDate, solarArcModel, transitToSolarArcAspects)
  }

  override fun getProgressionAstroEventsModel(
    bdnp: IBirthDataNamePlace,
    grain: BirthDataGrain,
    fromTime: GmtJulDay,
    toTime: GmtJulDay,
    config: IPersonHoroscopeConfig
  ): ProgressionAstroEventsModel {
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

    val traverseConfig = destiny.core.electional.Config(
      ewConfig = null,
      astrologyConfig = destiny.core.electional.Config.AstrologyConfig(
        aspect = true,
        voc = false,
        stationary = true,
        retrograde = false,
        eclipse = true,
        lunarPhase = true,
        includeTransitToNatalAspects = false,
        ingress = true
      )
    )

    val includeHour = when (grain) {
      BirthDataGrain.DAY    -> false
      BirthDataGrain.MINUTE -> true
    }

    val secondaryProgressionEvents: List<ProgressionEvent> = dayHourService.traverse(bdnp, bdnp.location, secondaryProgressionConvergentFrom, secondaryProgressionConvergentTo, traverseConfig, includeHour).map { eventDto ->
      val divergentTime = progressionSecondary.getDivergentTime(bdnp.gmtJulDay, eventDto.begin)
      ProgressionEvent(eventDto as AstroEventDto, divergentTime)
    }.sortedBy { it.divergentTime }.toList()

    val tertiaryProgressionEvents: List<ProgressionEvent> = dayHourService.traverse(bdnp, bdnp.location, tertiaryProgressionConvergentFrom, tertiaryProgressionConvergentTo, traverseConfig, includeHour).map { eventDto ->
      val divergentTime = progressionTertiary.getDivergentTime(bdnp.gmtJulDay, eventDto.begin)
      ProgressionEvent(eventDto as AstroEventDto, divergentTime)
    }.sortedBy { it.divergentTime }.toList()


    val model: IPersonHoroscopeModel = personHoroscopeFeature.getPersonModel(bdnp, config)
    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      model.toPersonHoroscopeDto(secondaryProgressionConvergentFrom, RulerPtolemyImpl, aspectEffectiveModern, modernAspectCalculator, config)
    }

    return ProgressionAstroEventsModel(natal, grain, fromTime, toTime, secondaryProgressionEvents, tertiaryProgressionEvents)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
