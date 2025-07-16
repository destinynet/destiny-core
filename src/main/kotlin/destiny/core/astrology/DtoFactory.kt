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
import destiny.tools.KotlinLogging
import destiny.tools.Score.Companion.toScore
import jakarta.inject.Named
import java.time.LocalDateTime
import java.util.*

@Named
class DtoFactory(
  private val classicalFeature: ClassicalFeature,
  private val classicalFactories: List<IPlanetPatternFactory>,
) {

  fun IHoroscopeModel.toHoroscopeDto(
    rulerImpl: IRuler,
    aspectAffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    horoConfig: IHoroscopeConfig,
    includeClassical: Boolean
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
    val byStar = getByStarMap(threshold, aspectCalculator, rulerImpl)

    val angularConsideringPoints = points.filter { it is Planet || it is FixedStar || it is LunarPoint }

    // 交角之內 , 都是 0.6 分起跳
    val startScore = 0.6
    // 從 8度之內起算
    val toleranceOrb = 8.0
    val axisStars = mapOf(
      Axis.RISING to 1,
      Axis.MERIDIAN to 10,
      Axis.SETTING to 7,
      Axis.NADIR to 4
    ).map { (axis, houseNum) ->
      axis to this.getCuspDegree(houseNum)
    }.associate { (axis, zDeg) ->
      axis to angularConsideringPoints
        .asSequence()
        .map { p -> p to getZodiacDegree(p) }
        .filter { (_, pDeg) -> pDeg != null }
        .map { (p, zDeg) -> p to zDeg!! }
        .map { (p, pDeg) -> p to pDeg.getAngle(zDeg) }
        .filter { (_, orb) -> orb < toleranceOrb } // 左右 8 度
        .map { (p, orb) ->
          // if 交角 = 8 => 0.6 + 0.4 * ( 8 - 8 ) / 8 = 0.6
          // if 交角 = 0 => 0.6 + 0.4 * ( 8 - 0 ) / 8 = 1.0
          val score = (startScore + (1 - startScore) * (toleranceOrb - orb) / toleranceOrb).toScore()
          AxisStar(p, orb, score)
        }
        .sortedByDescending { it.score }
        .toList()
    }

    val allPlanets = points.filterIsInstance<Planet>().size
    val houseStarDistribution: Map<HouseType, Natal.HouseStarDistribution> = HouseType.entries.associateWith { houseType ->
      val starCount = getHousePoints(houseType).filterIsInstance<Planet>().size
      Natal.HouseStarDistribution(starCount, (starCount.toDouble() * 100.0 / allPlanets))
    }

    val classicalConfig: IClassicalConfig = ClassicalConfig(classicalFactories)
    val cfg = classicalConfig to horoConfig

    val classicalPatterns  = if (includeClassical) {
      classicalFeature.getModel(gmtJulDay, location, cfg).flatMap { (_, list: List<IPlanetPattern>) ->
        list.map {
          PatternTranslator.getDescriptor(it).getDescription(Locale.ENGLISH)
        }
      }
    } else {
      emptyList()
    }

    return HoroscopeDto(
      time as LocalDateTime, location, place,
      signPointsMap.filter { (_, points) -> points.isNotEmpty() },
      byHouse, byStar,
      axisStars,
      houseStarDistribution,
      elementDistribution(),
      qualityDistribution(),
      getTightAspects(aspectCalculator, threshold),
      getPatterns(PatternContext(aspectAffective, aspectCalculator), threshold),
      classicalPatterns,
      getGraph(rulerImpl),
      getMidPointsWithFocal().filter { it.orb <= 1.0 }
    )
  }

  private fun IPersonHoroscopeModel.getAge(viewGmt: GmtJulDay): Int {
    return ((viewGmt - this.gmtJulDay) / 365).toInt()
  }

  fun IPersonHoroscopeModel.toPersonHoroscopeDto(
    viewGmt: GmtJulDay,
    rulerImpl: IRuler,
    aspectAffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    includeHouse: Boolean,
    horoConfig: IHoroscopeConfig
  ): IPersonHoroscopeDto {
    val horoscopeDto: IHoroscopeDto = this.toHoroscopeDto(rulerImpl, aspectAffective, aspectCalculator, horoConfig, true)
    val age = this.getAge(viewGmt)

    return Natal(gender, age, name, horoscopeDto).let {
      if (includeHouse) {
        it
      } else {
        // 只有日期，必須把 house/axis 相關資訊抹除
        it.copy(
          stars = it.stars.filterNot { (k, _) -> Axis.array.contains(k) },
          houses = emptyList(),
          houseStarDistribution = emptyMap(),
          axisStars = emptyMap(),
          tightestAspects = it.tightestAspects.filterNot { pattern -> pattern.points.any { p -> Axis.array.contains(p) } },
          astroPatterns = it.astroPatterns.filterNot { pattern -> pattern.points.any { p -> Axis.array.contains(p) } },
          midPoints =  it.midPoints.filterNot { pattern -> pattern.points.any { p -> Axis.array.contains(p) } },
        )
      }
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
