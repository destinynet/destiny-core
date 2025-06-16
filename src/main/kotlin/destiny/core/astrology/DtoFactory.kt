/**
 * Created by smallufo on 2025-06-16.
 */
package destiny.core.astrology

import destiny.core.astrology.classical.ClassicalConfig
import destiny.core.astrology.classical.ClassicalFeature
import destiny.core.astrology.classical.IRuler
import destiny.core.astrology.classical.rules.IPlanetPattern
import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import destiny.core.astrology.classical.rules.PatternTranslator
import destiny.core.calendar.GmtJulDay
import jakarta.inject.Named
import java.time.LocalDateTime
import java.util.*

@Named
class DtoFactory(
  private val horoscopeFeature: IHoroscopeFeature,
  private val classicalFeature: ClassicalFeature,
  private val classicalFactories: List<IPlanetPatternFactory>,
) {

  fun IHoroscopeModel.toHoroscopeDto(
    rulerImpl: IRuler,
    aspectAffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig
  ): IHoroscopeDto {

    val byHouse: List<HouseDto> = houses.map { h ->
      HouseDto(
        h.index,
        h.cusp,
        with(rulerImpl) {
          h.cusp.sign.getRulerPoint()!!
        }, getHousePoints(h.index)
      )
    }

    val threshold = 0.9
    val byStar = with(horoscopeFeature) { getByStarMap(threshold, aspectCalculator, rulerImpl) }
    val allPlanets = points.filterIsInstance<Planet>().size
    val houseStarDistribution: Map<HouseType, Natal.HouseStarDistribution> = HouseType.entries.associateWith { houseType ->
      val starCount = getHousePoints(houseType).filterIsInstance<Planet>().size
      Natal.HouseStarDistribution(starCount, (starCount.toDouble() / allPlanets))
    }

    val classicalConfig: IClassicalConfig = ClassicalConfig(classicalFactories)
    val cfg = classicalConfig to config

    val classicalPatterns = classicalFeature.getModel(gmtJulDay, location, cfg).flatMap { (_, list: List<IPlanetPattern>) ->
      list.map {
        PatternTranslator.getDescriptor(it).getDescription(Locale.ENGLISH)
      }
    }

    return with(horoscopeFeature) {
      HoroscopeDto(
        time as LocalDateTime, location, place, signPointsMap, byHouse, byStar, houseStarDistribution,
        elementDistribution(),
        qualityDistribution(),
        getTightAspects(aspectCalculator, threshold),
        getPatterns(PatternContext(aspectAffective, aspectCalculator), threshold),
        classicalPatterns,
        getGraph(rulerImpl),
        getMidPointsWithFocal().filter { it.orb <= 1.0 }
      )
    }
  }

  private fun IPersonHoroscopeModel.getAge(viewGmt: GmtJulDay): Int {
    return ((viewGmt - this.gmtJulDay) / 365).toInt()
  }

  fun IPersonHoroscopeModel.toPersonHoroscopeDto(
    viewGmt: GmtJulDay,
    rulerImpl: IRuler,
    aspectAffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig
  ): IPersonHoroscopeDto {
    val horoscopeDto = this.toHoroscopeDto(rulerImpl, aspectAffective, aspectCalculator, config)
    val age = this.getAge(viewGmt)

    return Natal(gender, age, name, horoscopeDto)
  }
}
