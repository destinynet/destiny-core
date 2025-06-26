/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.astrology.prediction.ProgressionSecondary
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.electional.DayHourService
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
  private val starTransitImpl: IStarTransit,
  private val dayHourService: DayHourService
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
      model.toPersonHoroscopeDto(viewGmt, RulerPtolemyImpl(), aspectEffectiveModern, modernAspectCalculator, config)
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
    val convergentFrom = progressionSecondary.getConvergentTime(bdnp.gmtJulDay, fromTime)
    val convergentTo = progressionSecondary.getConvergentTime(bdnp.gmtJulDay, toTime)

    val traverseConfig = destiny.core.electional.Config(
      ewConfig = null,
      astrologyConfig = destiny.core.electional.Config.AstrologyConfig(
        aspect = true,
        voc = true,
        retrograde = true,
        eclipse = true,
        lunarPhase = true,
        includeTransitToNatalAspects = false,
        starChangeSign = true
      )
    )

    val includeHour = when (grain) {
      BirthDataGrain.DAY    -> false
      BirthDataGrain.MINUTE -> true
    }

    dayHourService.traverse(bdnp, bdnp.location, convergentFrom, convergentTo, traverseConfig, includeHour).map { eventDto ->
      //val divergentTime = progressionSecondary.getDivergentTime(bdnp.gmtJulDay, eventDto.begin.toGmtJulDay(bdnp.location))
      //ProgressionEvent(eventDto as AstroEventDto, divergentTime)
    }


    val model: IPersonHoroscopeModel = personHoroscopeFeature.getPersonModel(bdnp, config)
    val natal: IPersonHoroscopeDto = with(dtoFactory) {
      model.toPersonHoroscopeDto(convergentFrom, RulerPtolemyImpl(), aspectEffectiveModern, modernAspectCalculator, config)
    }

    TODO("Not yet implemented")
  }
}
