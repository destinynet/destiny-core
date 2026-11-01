/**
 * Created by smallufo on 2026-11-01.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Axis
import destiny.core.astrology.BirthDataGrain
import destiny.core.astrology.Planet
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * 三環盤的一層。內環 [NATAL]、中環 [SOLAR_ARC] 或 [SECONDARY] 擇一、外環 [TRANSIT]。
 */
@Serializable
enum class TriWheelLayer { NATAL, SOLAR_ARC, SECONDARY, TRANSIT }

/**
 * 三環盤：本命（內）、推進（中）、行運（外）。**給 LLM 分析用，不是畫盤用** —— 不帶宮頭、相位線座標。
 *
 * 相位**依本命標的分組**（[targets]），不給三份平行清單 ——
 * 同一個本命點同時被背景（中→內）與觸發（外→內、外→中）打中，才是要看的訊號；
 * 平行清單會讓讀者自己找交集，且容易被條數最多的那一層牽著走。
 *
 * 「外環打中環的 SA X」歸在 SA X 以背景打中的本命 Y 底下 —— 觸發的是「SA X → 本命 Y」這條弧，
 * 不是本命 X。被打中的中環點記在 [TriWheelHit.middlePoint]。
 */
@Serializable
data class TriWheelDto(
  val natalGrain: BirthDataGrain,
  /** 事件時刻的精度；外環的軸點與宮頭看這個，與 [natalGrain] 無關 */
  val eventGrain: BirthDataGrain,
  /** 中環用哪一種推進：[TriWheelLayer.SOLAR_ARC] 或 [TriWheelLayer.SECONDARY] */
  val middle: TriWheelLayer,
  /**
   * 內 → 外三環的各點位置，只作脈絡（星座、度數、逆行），判讀看 [targets]。
   * 盲測時留空：行運位置推得回事件日期，本命外行星位置推得回出生年。
   */
  val rings: List<TriWheelRing> = emptyList(),
  /** 有被打中的本命點，依 [TriWheelTarget.routes] 多寡、再依最小 orb 排序 */
  val targets: List<TriWheelTarget>,
  /** 因 [natalGrain] 不足而**不能**作為標的的本命點（例：DAY 的月亮、軸點）。讀者要知道它們是沒算，不是沒事 */
  val excludedNatalTargets: List<AstroPoint>,
  /** 各層實際有參與計算的移動點。沒出現在 [targets] 的，代表算了但沒打中門檻內，不是沒算 */
  val consideredMovers: Map<TriWheelLayer, List<AstroPoint>> = emptyMap(),
  val filter: TriWheelFilter,
) {
  init {
    require(middle == TriWheelLayer.SOLAR_ARC || middle == TriWheelLayer.SECONDARY) { "middle must be SOLAR_ARC or SECONDARY : $middle" }
  }
}

@Serializable
data class TriWheelRing(
  val layer: TriWheelLayer,
  val points: List<TriWheelPoint>,
)

@Serializable
data class TriWheelPoint(
  val point: AstroPoint,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val lng: Double,
  val retrograde: Boolean = false,
  /** 出生時刻不確定造成的 ±度數（出生日 00:00 與 23:59 兩端之差的一半）；精確時刻為 0 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val spread: Double = 0.0,
)

/** 一條相位：[mover]（在 [from] 層）以 [aspect] 打中 [onto] 層的標的 */
@Serializable
data class TriWheelHit(
  val from: TriWheelLayer,
  val mover: AstroPoint,
  /** [TriWheelLayer.NATAL] 或中環 */
  val onto: TriWheelLayer,
  val aspect: Aspect,
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orb: Double,
  /** orb 因出生時刻不確定的 ±範圍；> [orb] 代表這條可能根本不成立 */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val orbSpread: Double = 0.0,
  /** 入相位 true、出相位 false；中環對本命一年才動約 1°，仍有方向 */
  val applying: Boolean? = null,
  /**
   * 世代相位：早 [TriWheelFilter.cohortDays] 天與晚 [TriWheelFilter.cohortDays] 天出生的人也都有這條（仍在門檻內）。
   * 多半是慢星打慢星；它說的是這一代人，不是這個人，不計入 [TriWheelTarget.routes]。
   */
  val generational: Boolean = false,
  /** [onto] 為中環時，被打中的中環點（例：SA 冥王）。它歸在哪個本命標的底下，看 [TriWheelTarget] */
  val middlePoint: AstroPoint? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class TriWheelTarget(
  val natal: AstroPoint,
  /** 中 → 內：這一兩年的主題 */
  val background: List<TriWheelHit>,
  /** 外 → 內、外 → 中：主題在這一刻落地 */
  val triggers: List<TriWheelHit>,
  /**
   * 打中這個點的路徑數（中→內、外→內、外→中），0..3；匯聚程度。世代相位不算。
   * 由前兩個欄位推得；寫成欄位（而非 getter）才會出現在 JSON 裡給 LLM 讀。
   */
  @EncodeDefault
  val routes: Int = routesOf(background + triggers),
) {
  companion object {
    fun routesOf(hits: List<TriWheelHit>): Int = hits.filterNot { it.generational }.map { it.from to it.onto }.distinct().size
  }
}

/**
 * 篩選規則 —— 資訊量由這裡控制，而不是砍掉一整層。隨 DTO 一起輸出，讓讀者知道「沒列出」的門檻。
 */
@Serializable
data class TriWheelFilter(
  /** 中 → 內 */
  val backgroundOrb: Double = 1.0,
  /** 外環慢星（木土天海冥）→ 內 */
  val slowOrb: Double = 2.0,
  /** 外環火星 → 內 */
  val marsOrb: Double = 1.5,
  /** 外環快星（日水金）只看打到本命這些點；本命月亮另受 natalGrain 閘門 */
  val fastTargets: Set<AstroPoint> = setOf(Planet.SUN, Planet.MOON),
  val fastOrb: Double = 1.0,
  /** 事件時刻精確時才納入：外環月亮、外環 ASC／MC（只看合、沖） */
  val momentOrb: Double = 3.0,
  /** 外 → 中 只在該中環點本身有背景時列出 */
  val middleTriggerRequiresBackground: Boolean = true,
  val middleTriggerOrb: Double = 1.5,
  /** 判定 [TriWheelHit.generational] 的出生日前後天數 */
  val cohortDays: Int = 30,
  val aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH),
) {
  /**
   * 外環 [mover] 打本命 [target] 時適用的容許度；null ＝ 這一對不列（快星只看打到 [fastTargets]）。
   * 依 mover 的類別分級：月亮與軸點（事件時刻級）、外行星、火星、日水金
   */
  fun triggerOrb(mover: AstroPoint, target: AstroPoint): Double? = when {
    mover == Planet.MOON || mover is Axis         -> momentOrb
    mover in Planet.outerSet                      -> slowOrb
    mover == Planet.MARS                          -> marsOrb
    mover in FAST_MOVERS && target in fastTargets -> fastOrb
    else                                          -> null
  }

  companion object {
    /** 外環的快星：日、水、金 */
    val FAST_MOVERS: Set<Planet> = setOf(Planet.SUN, Planet.MERCURY, Planet.VENUS)
  }
}
