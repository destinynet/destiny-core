/**
 * Created by smallufo on 2026-11-12.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.BirthDataGrain
import destiny.tools.serializers.DoubleTwoDecimalSerializer
import kotlinx.serialization.Serializable

/**
 * 雙環盤：本命（內）、太陽弧（外）。即 [TriWheelDto] 拿掉行運那一環 —— 只看「這一兩年的主題」，不看哪一天落地。
 * **給 LLM 分析用，不是畫盤用**。相位依本命標的分組（[targets]），每條都是 [TriWheelLayer.SOLAR_ARC] → [TriWheelLayer.NATAL]。
 */
@Serializable
data class BiWheelDto(
  val natalGrain: BirthDataGrain,
  /** 太陽弧推進的度數（本命到觀測時刻） */
  @Serializable(with = DoubleTwoDecimalSerializer::class)
  val degreeMoved: Double,
  /** 內 → 外兩環的各點位置（[TriWheelLayer.NATAL]、[TriWheelLayer.SOLAR_ARC]），只作脈絡，判讀看 [targets] */
  val rings: List<TriWheelRing>,
  /** 有被打中的本命點，依非世代相位條數、再依最小 orb 排序 */
  val targets: List<BiWheelTarget>,
  /** 因 [natalGrain] 不足而**不能**作為標的、也不推進的本命點（例：DAY 的月亮、軸點）。讀者要知道它們是沒算，不是沒事 */
  val excludedPoints: List<AstroPoint>,
  /** 實際有推進的點。沒出現在 [targets] 的，代表算了但沒在門檻內，不是沒算 */
  val consideredMovers: List<AstroPoint>,
  val filter: ArcFilter,
)

@Serializable
data class BiWheelTarget(
  val natal: AstroPoint,
  val hits: List<TriWheelHit>,
)

/**
 * 太陽弧 → 本命的篩選規則。隨 DTO 一起輸出，讓讀者知道「沒列出」的門檻。
 */
@Serializable
data class ArcFilter(
  val orb: Double = 1.0,
  /** 判定 [TriWheelHit.generational] 的出生日前後天數 */
  val cohortDays: Int = 30,
  val aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH),
)
