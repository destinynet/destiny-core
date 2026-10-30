/**
 * Created by smallufo on 2025-06-16.
 */
package destiny.core.astrology

import destiny.core.astrology.classical.ClassicalConfig
import destiny.core.astrology.classical.ClassicalFeature
import destiny.core.astrology.classical.IRuler
import destiny.core.astrology.classical.rules.AccidentalDignity
import destiny.core.astrology.classical.rules.Debility
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

@Named
class DtoFactory(
  private val horoscopeFeature: IHoroscopeFeature,
  private val classicalFeature: ClassicalFeature,
  private val classicalFactories: List<IPlanetPatternFactory>,
  private val starPositionImpl: IStarPosition<*>,
) {

  fun IHoroscopeModel.toHoroscopeDto(
    grain: BirthDataGrain,
    rulerImpl: IRuler,
    aspectAffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    horoConfig: IHoroscopeConfig,
    place: String? = null
  ): IHoroscopeDto {
    val bySign: Map<ZodiacSign, List<AstroPoint>> =
      signPointsMap
        .mapValues { (_ , points) ->
          points.filterNot { it is FixedStar } // 過濾恆星
            .filter { astroPoint ->
              grain.includeAxis || astroPoint !is Axis // 無精確時刻 → 過濾四軸
            }
        }
        .filter { (_, points) ->
          points.isNotEmpty()
        }

    val byHouse: List<HouseDto> = if (!grain.includeAxis) emptyList() else houses.map { h ->
        HouseDto(
          h.index,
          h.cusp,
          with(rulerImpl) {
            h.cusp.sign.getRulerPoint()!!
          }, getHousePoints(h.index)
            .filterNot { it is FixedStar } // 過濾恆星
        )
      }

    val threshold = 0.8

    val declinationMap: Map<AstroPoint, Double> = points
      .filterIsInstance<Star>()
      .mapNotNull { star ->
        runCatching {
          star to starPositionImpl.calculate(star, gmtJulDay, centric, Coordinate.EQUATORIAL, horoConfig.starTypeOptions).lat
        }.getOrNull()
      }.toMap()

    val byStar: Map<AstroPoint, Natal.StarPosInfo> = getByStarMap(threshold, aspectCalculator, rulerImpl, grain, declinationMap).filter { (p, _) ->
      grain.includeAxis || p !is Axis
    }
      .filterKeys { it !is FixedStar } // 過濾恆星

    // 交角之內 , 都是 0.6 分起跳
    val startScore = 0.6
    // 從 8度之內起算
    val toleranceOrb = 8.0

    val axisStars: Map<Axis, List<AxisStar>> = if (!grain.includeAxis) emptyMap() else {
      run {
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

    val houseStarDistribution: Map<HouseType, Natal.HouseStarDistribution> = if (!grain.includeAxis) emptyMap() else {
      run {
        HouseType.entries.associateWith { houseType ->
          val starCount = getHousePoints(houseType).filterIsInstance<Planet>().size
          Natal.HouseStarDistribution(starCount, (starCount.toDouble() * 100.0 / allPlanets))
        }
      }
    }

    val tightestAspects = getTightAspects(aspectCalculator, threshold, grain)
      .filterNot { pattern -> pattern.points.all { it is FixedStar } }

    // grain 傳進 getPatterns：宮位在**產生時**就不算（house = null），而非事後洗掉。
    // 這裡額外濾掉以 Axis 為構成星的 pattern —— 那是「點本身不可得」，與「宮位不可得」是兩件事。
    val astroPatterns = getPatterns(PatternContext(aspectAffective, aspectCalculator), threshold, grain).let { patterns ->
      if (grain.includeAxis) patterns
      else patterns.filterNot { pattern -> pattern.points.any { it is Axis } }
    }

    val midPoints = getMidPointsWithFocal().filter { it.orb <= 1.0 }.let { midPoints ->
      if (grain.includeAxis) midPoints
      else midPoints.filterNot { midPoint ->
        midPoint.points.any { it is Axis } || midPoint.focal is Axis
      }
    }


    // 諧波盤把位置誤差放大 n 倍（正午 placeholder 的月亮誤差 ±6.6° × 9 ≈ 60°），僅 MINUTE 可信
    val harmonics: Map<Int, Harmonic> = if (grain != BirthDataGrain.MINUTE) emptyMap() else {
      listOf(5, 7, 9).associateWith { n ->
        val aspectAffectiveHarmonic = AspectEffectiveHarmonic(n, aspectAffective)

        @Suppress("UNCHECKED_CAST")
        val harmonicAspectCalculator = AspectCalculatorImpl(aspectAffectiveHarmonic, positionMap as Map<AstroPoint, IPosition<*>>)
        with(horoscopeFeature) {
          this@toHoroscopeDto.getHarmonic(n, harmonicAspectCalculator)
        }
      }
    }.filter { (_ , harmonic) -> harmonic.aspects.isNotEmpty() }


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
      getGraph(rulerImpl),
      midPoints,
      harmonics
    )
  }

  /**
   * 本命盤的古典格局（尊貴、偶然尊貴、debility…），不放進 DTO：格局是衍生資料，用的時候由 model 現算，
   * 給 LLM 時再以 [PatternTranslator] 翻成文字。
   *
   * 用哪些格局：[horoConfig] 若帶著 [IClassicalConfig]（例：[HoroscopePresentConfig]）就用它的 factories
   * （卜卦與本命的判準不同，見 `OutcomeOptions`），否則用注入的預設清單。
   *
   * 沒有精確出生時刻時，濾掉得時／不得時（[AccidentalDignity.Hayz]／[Debility.Out_of_Sect]）。
   */
  fun IHoroscopeModel.getClassicalPatterns(grain: BirthDataGrain, horoConfig: IHoroscopeConfig): List<IPlanetPattern> {
    val factories = (horoConfig as? IClassicalConfig)?.factories ?: classicalFactories
    val cfg = ClassicalConfig(factories) to horoConfig
    return classicalFeature.getModel(gmtJulDay, location, cfg).flatMap { (_, list: List<IPlanetPattern>) ->
      list.filterNot { pattern ->
        // 得時／不得時依賴晝夜「與」星體在地平面上下 —— 後者需要精確時刻，
        // 故即使知晝夜（DAY_NIGHT），以正午 placeholder 推算仍不可採信，濾除。
        !grain.includeAxis &&
          (pattern is AccidentalDignity.Hayz || pattern is Debility.Out_of_Sect)
      }
    }
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
    val horoscopeDto: IHoroscopeDto = this.toHoroscopeDto(grain, rulerImpl, aspectAffective, aspectCalculator, horoConfig, this.place)
    val age = this.getAge(viewGmt)

    return Natal(gender, age, name, horoscopeDto)
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
