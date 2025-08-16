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
        } // 過濾恆星
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

    val angularConsideringPoints = points.filter { it is Planet || it is FixedStar || it is LunarPoint }

    // 交角之內 , 都是 0.6 分起跳
    val startScore = 0.6
    // 從 8度之內起算
    val toleranceOrb = 8.0

    val axisStars: Map<Axis, List<AxisStar>> = when(grain) {
      BirthDataGrain.DAY -> emptyMap()
      BirthDataGrain.MINUTE -> {
        mapOf(
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
            .filter { (p: AstroPoint, orb) ->
              if(p is FixedStar) {
                // TODO 理想容許度：1.5度 以內
                //  如果一顆恆星距離軸點在 1.5度 之內，其影響力被認為是明確且強大的，絕對需要納入解盤的考慮。
                //  可接受容許度：最多 2.5度
                //  對於特別明亮的一等星，或是像軒轅十四、畢宿五、心宿二、北落師門這四顆「王室之星」(Royal Stars)，容許度可以稍微放寬到 2度，甚至 2.5度。
                //  超過 2.5度：絕大多數的占星師會認為其影響力已經微乎其微，甚至可以忽略不計。
                orb < 2.0
              } else {
                // 左右 8 度
                orb < toleranceOrb
              }
            }
            .map { (p, orb) ->
              // if 交角 = 8 => 0.6 + 0.4 * ( 8 - 8 ) / 8 = 0.6
              // if 交角 = 0 => 0.6 + 0.4 * ( 8 - 0 ) / 8 = 1.0
              val score = (startScore + (1 - startScore) * (toleranceOrb - orb) / toleranceOrb).toScore()
              AxisStar(p, orb, score)
            }
            .sortedByDescending { it.score }
            .toList()
        }
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

    val tightestAspects = getTightAspects(aspectCalculator, threshold).let { aspects ->
      when(grain) {
        BirthDataGrain.MINUTE -> aspects
        BirthDataGrain.DAY -> aspects.filterNot { asp -> asp.points.any { it is Axis} }
      }
    }.filterNot { pattern -> pattern.points.all { it is FixedStar } }

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
          midPoints.filterNot { midPoint -> midPoint.points.any { it is Axis } }
        }
      }
    }



    return HoroscopeDto(
      time as LocalDateTime, location, place,
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
