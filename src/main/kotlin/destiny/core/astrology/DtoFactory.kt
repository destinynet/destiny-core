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
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.fixError
import destiny.core.calendar.toLmt
import destiny.tools.KotlinLogging
import destiny.tools.Score.Companion.toScore
import jakarta.inject.Named
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Named
class DtoFactory(
  private val classicalFeature: ClassicalFeature,
  private val classicalFactories: List<IPlanetPatternFactory>,
) {

  fun IHoroscopeModel.toHoroscopeDto(
    grain: BirthDataGrain,
    rulerImpl: IRuler,
    aspectAffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    horoConfig: IHoroscopeConfig,
    includeClassical: Boolean
  ): IHoroscopeDto {
    val bySign: Map<ZodiacSign, List<AstroPoint>> =
      signPointsMap
        .mapValues { (_ , points) ->
          points.filterNot { it is FixedStar } // 過濾恆星
            .filter { astroPoint ->
              when(grain) {
                BirthDataGrain.MINUTE -> true
                BirthDataGrain.DAY -> astroPoint !is Axis // 過濾四軸
              }
            }
        }
        .filter { (_, points) ->
          points.isNotEmpty()
        }

    val byHouse: List<HouseDto> = when(grain) {
      BirthDataGrain.DAY -> emptyList()
      BirthDataGrain.MINUTE -> houses.map { h ->
        HouseDto(
          h.index,
          h.cusp,
          with(rulerImpl) {
            h.cusp.sign.getRulerPoint()!!
          }, getHousePoints(h.index)
            .filterNot { it is FixedStar } // 過濾恆星
        )
      }
    }

    val threshold = 0.9

    val byStar: Map<AstroPoint, Natal.StarPosInfo> = getByStarMap(threshold, aspectCalculator, rulerImpl, grain).filter { (p, _) ->
      when (grain) {
        BirthDataGrain.MINUTE -> true
        BirthDataGrain.DAY    -> p !is Axis
      }
    }
      .filterKeys { it !is FixedStar } // 過濾恆星

    // 交角之內 , 都是 0.6 分起跳
    val startScore = 0.6
    // 從 8度之內起算
    val toleranceOrb = 8.0

    val axisStars: Map<Axis, List<AxisStar>> = when(grain) {
      BirthDataGrain.DAY -> emptyMap()
      BirthDataGrain.MINUTE -> {
        getAxisStars(8.0).map { (axis , starAndOrbs) ->
          val newStarAndOrbs = starAndOrbs.filter { (star , orb) ->
            if (star is FixedStar) {
              orb < 2.0
            } else {
              true
            }
          }

          axis to newStarAndOrbs.map { (star, orb: Double) ->
            // if 交角 = 8 => 0.6 + 0.4 * ( 8 - 8 ) / 8 = 0.6
            // if 交角 = 0 => 0.6 + 0.4 * ( 8 - 0 ) / 8 = 1.0
            val score = (startScore + (1 - startScore) * (toleranceOrb - orb) / toleranceOrb).toScore()
            AxisStar(star, orb, score)
          }.sortedByDescending { it.score }
        }.toMap()
      }
    }

    val allPlanets = points.filterIsInstance<Planet>().size

    val houseStarDistribution: Map<HouseType, Natal.HouseStarDistribution> = when(grain) {
      BirthDataGrain.DAY -> emptyMap()
      BirthDataGrain.MINUTE -> {
        HouseType.entries.associateWith { houseType ->
          val starCount = getHousePoints(houseType).filterIsInstance<Planet>().size
          Natal.HouseStarDistribution(starCount, (starCount.toDouble() * 100.0 / allPlanets))
        }
      }
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

    val tightestAspects = getTightAspects(aspectCalculator, threshold, grain)
      .filterNot { pattern -> pattern.points.all { it is FixedStar } }

    val astroPatterns = getPatterns(PatternContext(aspectAffective, aspectCalculator), threshold).let { patterns ->
      when (grain) {
        BirthDataGrain.MINUTE -> patterns
        BirthDataGrain.DAY -> {
          patterns.filterNot { pattern -> pattern.points.any { it is Axis } }
        }
      }
    }

    val midPoints = getMidPointsWithFocal().filter { it.orb <= 1.0 }.let { midPoints ->
      when(grain) {
        BirthDataGrain.MINUTE -> midPoints
        BirthDataGrain.DAY -> {
          midPoints.filterNot {
            midPoint ->
            midPoint.points.any { it is Axis } || midPoint.focal is Axis
          }
        }
      }
    }



    return HoroscopeDto(
      time as LocalDateTime,
      (
        gmtJulDay.toLmt(
          ZoneId.of("UTC"),
          JulDayResolver1582CutoverImpl()
        ) as LocalDateTime).fixError() as LocalDateTime,
      location, place,
      bySign, byHouse, byStar,
      axisStars,
      houseStarDistribution,
      elementDistribution(),
      qualityDistribution(),
      tightestAspects,
      astroPatterns,
      classicalPatterns,
      getGraph(rulerImpl),
      midPoints,
    )
  }

  private fun IPersonHoroscopeModel.getAge(viewGmt: GmtJulDay): Int {
    return ((viewGmt - this.gmtJulDay) / 365).toInt()
  }

  fun IPersonHoroscopeModel.toPersonHoroscopeDto(
    grain: BirthDataGrain,
    viewGmt: GmtJulDay,
    rulerImpl: IRuler,
    aspectAffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    horoConfig: IHoroscopeConfig
  ): IPersonHoroscopeDto {
    val horoscopeDto: IHoroscopeDto = this.toHoroscopeDto(grain, rulerImpl, aspectAffective, aspectCalculator, horoConfig, true)
    val age = this.getAge(viewGmt)

    return Natal(gender, age, name, horoscopeDto)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
