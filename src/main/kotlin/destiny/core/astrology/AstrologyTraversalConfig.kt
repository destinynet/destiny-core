package destiny.core.astrology

import destiny.core.astrology.Aspect.Importance
import destiny.core.astrology.Planet.*
import kotlinx.serialization.Serializable


/**
 * 相位過濾規則：first-match-wins。
 * 當某個 transiting planet 匹配到規則時，由該規則決定是否接受此相位。
 * 未匹配任何規則的行星，fallback 到 [AstrologyTraversalConfig.aspectTypes]。
 */
@Serializable
data class AspectFilterRule(
  val transitingPlanets: Set<Planet>,
  val natalPlanets: Set<Planet>,
  val aspectTypes: Set<Aspect>,
  val accepting: Boolean
)


/**
 * Progression（推運）專用的遍歷設定。
 * 與 Transit 不同，SP/TP 的 convergent time window 很短，
 * 需要包含所有行星（尤其 Moon/Sun）才能偵測到有意義的事件。
 *
 * @param planets 用於計算推運相位的行星集合（outer ring）。預設包含所有行星。
 * @param stationaryPlanets 計算哪些推運行星的滯留事件。
 *   SP 窗口約 1-2 天，Mercury/Venus 可能在此期間轉向（station）。
 * @param eclipse 是否偵測推運窗口內的日蝕/月蝕。
 *   出生後數十天內若有蝕相，反推至真實年齡是極為特殊的事件。
 * @param signIngress 是否偵測推運行星換座。
 *   SP Moon 換座約每 2.5 年一次，SP Sun 換座約每 30 年一次 — 都是重大定時指標。
 * @param houseIngress 是否偵測推運行星換宮。
 *   SP Moon 換宮標誌生活重心的轉移。需要精確出生時間（[BirthDataGrain.MINUTE]）。
 */
data class ProgressionConfig(
  val planets: Set<Planet> = Planet.values.toSet(),
  val stationaryPlanets: Set<Planet> = setOf(MERCURY, VENUS, MARS),
  val eclipse: Boolean = true,
  val signIngress: Boolean = true,
  val houseIngress: Boolean = true,
)

data class SolarArcConfig(
  val transitingPoints: Set<AstroPoint> = Planet.values.toSet() + LunarNode.values.toSet() + Axis.MERIDIAN + Axis.RISING,
  val applyingOrb: Double = 2.0,
  val separatingOrb: Double = 1.0,
)

data class AstrologyTraversalConfig(
  /** 占星盤的設定 */
  val horoscopeConfig: IHoroscopeConfig = HoroscopeConfig(),
  /** 計算全球星體(outer ring)交角 */
  val globalAspect: Boolean = true,
  /** 計算外圈對內圈星體(outer to inner)交角 */
  val personalAspect: Boolean = true,
  /**
   * 相位過濾規則（first-match-wins）。
   * 對 personalAspect 的 transiting planet，依序匹配規則；
   * 匹配到時由規則決定此相位是否計算。未匹配任何規則時 fallback 到 [aspectTypes]。
   */
  val aspectFilterRules: List<AspectFilterRule> = emptyList(),
  /** personalAspect 的預設相位類型（當 aspectFilterRules 未匹配時使用） */
  val aspectTypes: Set<Aspect> = majorAspects,
  /** 月亮空亡 */
  val voc: Boolean = true,
  /** 計算哪些行星的滯留事件。空集合 = 不計算。Sun/Moon 無滯留，會自動排除。 */
  val stationaryPlanets: Set<Planet> = allStationaryCapable,
  /** 星體逆行 */
  val retrograde: Boolean = true,
  /** 日食、月食 */
  val eclipse: Boolean = true,
  /** 月相 */
  val lunarPhase: Boolean = true,
  /**
   * 是否在 DTO 中包含「過運星體 to 本命星體」的詳細相位資料。預設為 false
   */
  val includeTransitToNatalAspects: Boolean = false,
  /** 星體換星座 */
  val signIngress: Boolean = true,
  /** 星體換宮位 */
  val houseIngress: Boolean = true,
  /** 星體進入/離開 Out of Bounds (赤緯超過 ±23.44°) */
  val oobIngress: Boolean = false,
  /** 計算 OOB 進出的行星集合。空集合 = 不計算（即使 oobIngress=true）。
   *  OOB 常見於內行星（Mercury, Venus, Mars），獨立於 [transitingPlanets]。 */
  val oobPlanets: Set<Planet> = allOobCapable,
  /** 計算 transit aspect 的行星集合（外圈行星）。用於指定哪些行星作為過運星體。 */
  val transitingPlanets: Set<Planet> = outerPlanets,
  /** Secondary Progression 遍歷設定 */
  val secondaryProgressionConfig: ProgressionConfig = ProgressionConfig(),
  /** Tertiary Progression 遍歷設定 */
  val tertiaryProgressionConfig: ProgressionConfig = ProgressionConfig(),
  /** SolarArc 遍歷設定 */
  val solarArcConfig: SolarArcConfig = SolarArcConfig(),
) {

  /**
   * 取得某個 transiting planet 對某個 natal planet 實際允許的角度集合。
   * 先查 [aspectFilterRules]（first-match-wins），未匹配則 fallback 到 [aspectTypes]。
   */
  fun effectiveAngles(transitingPlanet: Planet, natalPoint: AstroPoint): Set<Double> {
    val natalPlanet = natalPoint as? Planet
    for (rule in aspectFilterRules) {
      if (transitingPlanet !in rule.transitingPlanets) continue
      if (!rule.accepting) return emptySet()
      if (natalPlanet != null && natalPlanet !in rule.natalPlanets) return emptySet()
      return rule.aspectTypes.flatMap { it.mirrorAngles }.toSet()
    }
    return aspectTypes.flatMap { it.mirrorAngles }.toSet()
  }

  /** 計算用的所有過運星體（transitingPlanets + stationaryPlanets） */
  val allTransitingPoints: Set<Planet> get() = transitingPlanets + stationaryPlanets

  companion object {
    /** 所有可能滯留的行星（排除 Sun, Moon） */
    val allStationaryCapable: Set<Planet> = Planet.planetSet - setOf(SUN, MOON)

    val outerPlanets: Set<Planet> = setOf(JUPITER, SATURN, URANUS, NEPTUNE, PLUTO)

    /** 容易 OOB 的行星（Mercury, Venus, Mars）— Sun/Moon 排除 */
    val allOobCapable: Set<Planet> = setOf(MERCURY, VENUS, MARS)

    /** 主要相位：合、衝、刑、三合、六合 */
    val majorAspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet()

    /**
     * 年度預測預設：僅外行星行運，含日食月食與滯留，不含月相、換座、換宮。
     */
    val YEARLY_FORECAST = AstrologyTraversalConfig(
      globalAspect = false,
      personalAspect = true,
      voc = false,
      stationaryPlanets = outerPlanets,
      retrograde = false,
      eclipse = true,
      lunarPhase = false,
      includeTransitToNatalAspects = true,
      signIngress = false,
      houseIngress = false,
      transitingPlanets = outerPlanets,
      oobIngress = true
    )

    /**
     * 以 [YEARLY_FORECAST] 為基底，額外加入內行星。
     * [transitExtra] 加入行運相位計算；[stationExtra] 額外加入滯留事件。
     * [rules] 允許精細的相位過濾（如 Mars 只計算硬相位）。
     */
    fun yearlyWithTransit(
      transitExtra: Set<Planet> = emptySet(),
      stationExtra: Set<Planet> = emptySet(),
      rules: List<AspectFilterRule> = emptyList()
    ): AstrologyTraversalConfig = YEARLY_FORECAST.copy(
      transitingPlanets = outerPlanets + transitExtra,
      stationaryPlanets = outerPlanets + transitExtra + stationExtra,
      aspectFilterRules = rules,
    )
  }
}
