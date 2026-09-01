package destiny.core.astrology

import destiny.core.astrology.Aspect.Importance
import destiny.core.astrology.Planet.*
import destiny.core.astrology.prediction.EventSource
import destiny.core.astrology.prediction.ITimeKey
import destiny.core.astrology.prediction.PrimaryDirectionMethod
import destiny.core.astrology.prediction.PtolemyKey
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

/**
 * 太陽弧 (Solar Arc) 遍歷設定。
 *
 * 注意：太陽弧年速約 0.99°，因此**容許度即時間**：
 *   0.10° ≈ ±37 天、0.15° ≈ ±55 天、0.50° ≈ ±半年、1.00° ≈ ±1 年、2.00° ≈ ±2 年。
 *
 * @param applyingOrb   相位在區間**結束之後**才會精準時，於區間尾端回報的容許度上限（2.0° ≈ 未來 2 年內）。
 * @param separatingOrb 相位在區間**開始之前**已經精準時，於區間開頭回報的容許度上限（1.0° ≈ 過去 1 年內）。
 */
data class SolarArcConfig(
  val transitingPoints: Set<AstroPoint> = Planet.values.toSet() + LunarNode.values.toSet() + Axis.MERIDIAN + Axis.RISING,
  val applyingOrb: Double = 2.0,
  val separatingOrb: Double = 1.0,
)

/**
 * 主限法 (Primary Direction) 遍歷設定。
 *
 * @param significators 主示星 (固定的本命點，事件的「當事人」)，通常是 Asc / MC / 日 / 月。
 * @param promissors    承諾星 (被推進、象徵事件的星體)，通常是傳統七政。
 * @param aspects       計算哪些相位。
 * @param method        量法 (預設黃道-赤經法)。
 * @param forward       順推 (true) 或逆推 (false)。
 * @param timeKey       時間鑰匙 (arc↔年)。
 */
data class PrimaryDirectionConfig(
  val significators: Set<AstroPoint> = setOf(Axis.RISING, Axis.MERIDIAN, SUN, MOON),
  val promissors: Set<AstroPoint> = Planet.classicalSet,
  val aspects: Set<Aspect> = Aspect.getAspects(Importance.HIGH).toSet(),
  val method: PrimaryDirectionMethod = PrimaryDirectionMethod.Zodiacal(),
  val forward: Boolean = true,
  val timeKey: ITimeKey = PtolemyKey,
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
  /** Primary Direction 遍歷設定 */
  val primaryDirectionConfig: PrimaryDirectionConfig = PrimaryDirectionConfig(),
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

/**
 * 某個 [source] 在這一次掃描裡**實際會移動的點** —— 掃描端與宣告端共用的唯一推導。
 *
 * ## 為什麼要有這個函式
 *
 * 「移動端有哪些」原本在三個地方各自算：`ReportFactory.scanTimeLineEvents` 的 `fetchEvents`
 * （行運與各推運）、[EventsTraversalSolarArcImpl]（它**刻意忽略**呼叫端傳入的集合，
 * 所以必須自己算一次），以及素材的 coverage 行（一份手寫字串）。
 * 三份推導只要有一份落後，素材就會宣告一件母體做不到、或做得到卻沒說的事 ——
 * 而「宣告與資料不符」是本專案付過最多次代價的缺陷族。
 *
 * 實際發生過的一次：長期層補進火星之後，母體有了 592 筆火星條目，
 * 而讀素材的一方仍把火星當成「不在任何計數母體中」，回頭手推分母
 * （「恆星週期 687 天…估約 45 個日曆月」）—— 資料是對的，可是沒有人知道。
 *
 * @param callerTransitingPoints 呼叫端指定的行運星集合（TRANSIT 用它）。
 * @param progressionConfig 推運類 source 的設定；`planets` 取代呼叫端的集合。與 `fetchEvents` 同款。
 * @param natalPoints 本命盤實際有的點 —— 只有 SOLAR_ARC 需要（它的移動端由本命點推出來）。
 */
fun AstrologyTraversalConfig.movingPointsOf(
  source: EventSource,
  grain: BirthDataGrain,
  callerTransitingPoints: Set<AstroPoint>,
  progressionConfig: ProgressionConfig? = null,
  natalPoints: Set<AstroPoint>? = null,
): Set<AstroPoint> {
  val candidates: Collection<AstroPoint> = when {
    // 太陽弧把所有點推進相同度數，不受呼叫端的外行星集合限制 —— 判準與 [EventsTraversalSolarArcImpl] 同源。
    source == EventSource.SOLAR_ARC && natalPoints != null -> solarArcConfig.transitingPoints
      .filter { it is Planet || it is LunarNode || it is Axis }
      .filter { it in natalPoints }
      .filter { grain.includeAxis || it !in Axis.values }

    else                                                   -> progressionConfig?.planets ?: callerTransitingPoints
  }
  // ⚠️ 移動端的 grain 閘門 —— 推運／太陽弧的移動端由本命位置推出來，繼承同一個未知量。
  return candidates.filter { grain.allowsMovingPoint(source, it) }.toSet()
}

/**
 * 移動端涵蓋範圍的**兩側**：某個 source 底下掃了哪些點、以及**哪些點在這個 source 底下沒有分母**。
 *
 * @param covered 這個 source 實際移動的點（來自 [movingPointsOf]）
 * @param uncovered 全集扣掉 [covered] —— 「以這個技法問，沒有母體可算」的那些
 */
data class MovingPointCoverage(
  val source: EventSource,
  val covered: List<AstroPoint>,
  val uncovered: List<AstroPoint>,
)

/**
 * ⭐ 逐 source 算出涵蓋與其補集 —— **素材的 coverage 行與工具的自陳行共用這一份**。
 *
 * ## 為什麼補集要與正面清單一起走
 *
 * [BirthDataGrain.gatedNatalTargets] 的 KDoc 寫過同一條教訓：述詞只答得出「這個點行不行」，
 * 答不出「有哪些點不見了」，而後者才是下游必須知道的。
 * 只給正面清單的實測結果是：讀者**正確複述了清單**，卻仍在規則層寫
 * 「這個計數母體不涵蓋行運火星」並手推分母 —— 因為它的推論不是查表，
 * 而是**從行星速度推涵蓋範圍**（火星是快星 → 應該屬於快層 → 快層沒有母體）。
 * 補集直接堵住那條推論。
 *
 * ⚠️ **補集必須逐 source 算，不可取聯集。** 太陽當行運沒有分母、當太陽弧有；
 * 取聯集只會得到「只有月亮沒分母」這種對任何一條規則都無用的答案。
 *
 * 全集取行星＋月交點（軸點不會當移動端出現在計數母體裡）。
 */
fun movingPointCoverage(bySource: Map<EventSource, Collection<AstroPoint>>): List<MovingPointCoverage> {
  val universe: List<AstroPoint> = Planet.values.toList() + LunarNode.values.toList()
  return bySource.entries.sortedBy { it.key.ordinal }.map { (source, points) ->
    MovingPointCoverage(source, points.toList(), universe.filterNot { it in points })
  }
}

/**
 * 一種**現象**（而非一顆星）。
 *
 * [movingPointCoverage] 回答的是「哪些**點**沒有分母」；本列舉是它的另一半：
 * 「哪些**現象**沒有分母」。兩者的缺失方式相同 —— 某一層掃了、另一層沒掃 ——
 * 而先前只有前者被自陳出來。
 *
 * @param label 對外的措辭。**與素材的 coverage 行、工具的自陳行共用同一份**，
 *   所以改這裡就等於同時改兩處；先前那兩處是各自手寫的字面清單。
 */
enum class ScanPhenomenon(val label: String) {
  TRANSIT_TO_NATAL_ASPECT("transit-to-natal aspects"),
  TRANSIT_TO_TRANSIT_ASPECT("transit-transit aspects"),
  STATION("stations"),
  RETROGRADE_SPAN("retrograde phases"),
  ECLIPSE("eclipses"),
  SIGN_INGRESS("sign ingresses"),
  HOUSE_INGRESS("house ingresses"),
  LUNAR_PHASE("lunar phases"),
  VOID_OF_COURSE("void-of-course moon"),
  ;
}

/**
 * 這個設定掃不掃某個現象 —— **由旗標導出，不另立清單**。
 *
 * 這正是 [movingPointCoverage] 的 KDoc 裡那條「由設定導出、不從結果反推」的同一條規矩：
 * 反推會把「掃了但這段期間剛好沒發生」誤報成「不在母體裡」。
 */
fun AstrologyTraversalConfig.scans(phenomenon: ScanPhenomenon): Boolean = when (phenomenon) {
  ScanPhenomenon.TRANSIT_TO_NATAL_ASPECT   -> personalAspect
  ScanPhenomenon.TRANSIT_TO_TRANSIT_ASPECT -> globalAspect
  ScanPhenomenon.STATION                   -> stationaryPlanets.isNotEmpty()
  ScanPhenomenon.RETROGRADE_SPAN           -> retrograde
  ScanPhenomenon.ECLIPSE                   -> eclipse
  ScanPhenomenon.SIGN_INGRESS              -> signIngress
  ScanPhenomenon.HOUSE_INGRESS             -> houseIngress
  ScanPhenomenon.LUNAR_PHASE               -> lunarPhase
  ScanPhenomenon.VOID_OF_COURSE            -> voc
}

/**
 * 現象層面的三分：**可以問分母 / 有資料但問不到 / 連資料都只在事件窗裡**。
 *
 * @param counted 全段掃到，而且有計數工具 —— 可以要分母
 * @param fullSpanNoTool 全段掃到，但沒有計數工具 —— 資料在，分母得自己導（而導出來的通常是錯的）
 * @param windowOnly 只在事件窗裡掃 —— **永遠沒有分母**，讀者只看得到「出事的月份長什麼樣」
 */
data class PhenomenonCoverage(
  val counted: List<ScanPhenomenon>,
  val fullSpanNoTool: List<ScanPhenomenon>,
  val windowOnly: List<ScanPhenomenon>,
)

/**
 * ⭐ 現象層面的涵蓋自陳 —— [movingPointCoverage] 的另一半。
 *
 * ## 為什麼要有這個
 *
 * 先前的自陳只列出「哪些**星體**沒有全段分母」。而實際發生過的是另一種：
 * 某個**現象**在事件窗掃得到、全段掃不到，於是讀者在事件月裡看見它、
 * 拿它寫成規則，再手推一個分母 —— 而那個分母被實測為錯了三倍、
 * 分子甚至根本是零。整條錯誤鏈上沒有任何一處會報錯。
 *
 * 那個現象後來補上了計數工具；但**同一類缺口當時還有三個潛伏著**
 * （互相位、換座、月相），只是還沒有人踩到。這個函式把那三個講出來。
 *
 * ## 三分而非二分
 *
 * 「有資料但沒工具」與「連資料都沒有」對讀者是不同的處境：
 * 前者可以要求加工具，後者得改掃描設定。混成一句「沒有分母」會讓兩者都無法行動。
 *
 * @param windowLayers 事件窗那幾層（讀者在事件月看得見的東西）
 * @param fullSpanLayers 全段那幾層
 * @param fullSpanExtras 不由 [AstrologyTraversalConfig] 決定、但確實掃了全段的現象
 *   （例如順逆三相與留另有自己的全段掃描，不吃 traversal 的旗標）。
 *   ⛔ 呼叫端必須跟著實際的掃描走 —— 這是本函式唯一無法自行導出的一格。
 * @param countable 有計數工具的現象。⛔ 增減計數工具時要跟著改。
 */
fun phenomenonCoverage(
  windowLayers: List<AstrologyTraversalConfig>,
  fullSpanLayers: List<AstrologyTraversalConfig>,
  fullSpanExtras: Set<ScanPhenomenon> = emptySet(),
  countable: Set<ScanPhenomenon> = emptySet(),
): PhenomenonCoverage {
  fun scannedBy(layers: List<AstrologyTraversalConfig>, p: ScanPhenomenon) = layers.any { it.scans(p) }
  val counted = mutableListOf<ScanPhenomenon>()
  val noTool = mutableListOf<ScanPhenomenon>()
  val windowOnly = mutableListOf<ScanPhenomenon>()
  ScanPhenomenon.entries.forEach { p ->
    val inFullSpan = scannedBy(fullSpanLayers, p) || p in fullSpanExtras
    val inWindow = scannedBy(windowLayers, p)
    when {
      inFullSpan && p in countable -> counted += p
      inFullSpan                   -> noTool += p
      inWindow                     -> windowOnly += p
      // 兩邊都沒有 ＝ 這份素材根本不含這個現象，不必宣告（宣告了反而像在暗示它存在）
    }
  }
  return PhenomenonCoverage(counted, noTool, windowOnly)
}
