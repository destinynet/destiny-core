/**
 * Created by smallufo on 2025-06-25.
 */
package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.calendar.TimeTools.toGmtJulDay
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
}

@Named
class ReportFactory(
  private val personHoroscopeFeature: IPersonHoroscopeFeature,
  private val horoscopeFeature: IHoroscopeFeature,
  private val aspectEffectiveModern: IAspectEffective,
  private val modernAspectCalculator: IAspectCalculator,
  private val dtoFactory: DtoFactory,
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
}
