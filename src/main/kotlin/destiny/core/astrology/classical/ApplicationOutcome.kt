/**
 * Created by smallufo on 2014-07-15.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.Planet
import destiny.core.astrology.Stationary

/** 入相位軌跡的一個取樣點：盤面時刻後 [days] 日，[ApplicationOutcome.applying] 與 [ApplicationOutcome.applied] 的黃經 */
data class OutcomeSample(
  val days: Double,
  val applyingLng: Double,
  val appliedLng: Double,
)

/**
 * 入相位的結局：盤面時刻 [applying] 正以 [pattern] 入相位 [applied]，兩星成形之前**最先**發生的事。
 * 最先的事件決定結局，所以同一對星不會同時是兩種結局。
 *
 * - [applying]：兩者中較快的那顆（盤面時刻的黃經速度絕對值較大）；[applied]：被追的那顆
 * - [eventDays]：決定結局的事件離盤面時刻幾天（成形、最接近後轉開、或被插隊的那一刻）
 * - [ingress]：事件發生之前換星座的那顆（兩顆都換時記先換的）；不影響判定，只標記 —— 成形時已換了星座，事情的本質可能變調
 * - [track]：自盤面時刻起的黃經等距取樣，給動畫重演用
 */
sealed interface ApplicationOutcome : ClassicalFact {
  val applying: Planet
  val applied: Planet
  val pattern: IPointAspectPattern
  val eventDays: Double
  val ingress: Planet?
  val track: List<OutcomeSample>

  val ingressBeforePerfection: Boolean get() = ingress != null

  /** 兩星成形，途中沒有任何阻礙；[eventDays] ＝ 成形時刻 */
  data class Perfected(
    override val applying: Planet,
    override val applied: Planet,
    override val pattern: IPointAspectPattern,
    override val eventDays: Double,
    override val ingress: Planet?,
    override val track: List<OutcomeSample>,
  ) : ApplicationOutcome

  /**
   * 臨陣退縮（refranation）：成形之前 [refranator] 停滯轉向（[stationary]，盤面時刻後 [stationDays] 日），
   * 兩星在 [closestDays] 日後最接近、仍差 [closestOrb] 度，之後漸行漸遠。[eventDays] ＝ [closestDays]。
   *
   * 兩種情形：快星自己停滯轉向（自我退縮），或被追的那顆停滯、走得比快星還遠（對方退縮）。
   * 對方停滯轉向、反而迎上來的（例：月亮入相位土星，土星轉逆往月亮走），相位照樣成形，是 [Perfected]。
   * 快星只是減速、被慢星甩開，期間沒有任何停滯的，不算退縮 —— 古典的 refranation 要有轉向（此時沒有結局）。
   */
  data class Refranation(
    override val applying: Planet,
    override val applied: Planet,
    override val pattern: IPointAspectPattern,
    override val ingress: Planet?,
    override val track: List<OutcomeSample>,
    val refranator: Planet,
    val stationary: Stationary,
    val stationDays: Double,
    val closestDays: Double,
    val closestOrb: Double,
  ) : ApplicationOutcome {
    override val eventDays: Double get() = closestDays
    val other: Planet get() = if (refranator === applying) applied else applying
  }

  /**
   * 插隊（prohibition）：成形之前，比 [applied] 快的第三顆星 [prohibitor] 先追上 [applied]、與它成形（[interruption] 是盤面時刻兩者的入相位）。
   * 第三顆星先與 [applying] 成形不算插隊（快星一天內就會與容許度內的星成形，那樣幾乎每個慢星對都被插隊）。
   * [byBody]：以合相插隊 —— Lilly 最嚴格的 prohibition，實體上的攔截；其餘是透過光芒（by rays），交角在 [interruption] 裡。
   *
   * Cutting off light（abscission）幾何上是它的變體，不另立結局，由翻譯層依幾何改用那個說法。
   */
  data class Prohibition(
    override val applying: Planet,
    override val applied: Planet,
    override val pattern: IPointAspectPattern,
    override val eventDays: Double,
    override val ingress: Planet?,
    override val track: List<OutcomeSample>,
    val prohibitor: Planet,
    val interruption: IPointAspectPattern,
  ) : ApplicationOutcome {
    val byBody: Boolean get() = interruption.aspect == Aspect.CONJUNCTION
  }

  /**
   * 撲空（frustration）：成形之前，[applied] 先與比它更慢的 [frustrator] 成形然後離開，[applying] 追了個空。
   * [interruption] 是 [applied] 與 [frustrator] 在盤面時刻的相位。
   */
  data class Frustration(
    override val applying: Planet,
    override val applied: Planet,
    override val pattern: IPointAspectPattern,
    override val eventDays: Double,
    override val ingress: Planet?,
    override val track: List<OutcomeSample>,
    val frustrator: Planet,
    val interruption: IPointAspectPattern,
  ) : ApplicationOutcome
}

/**
 * 入相位的結局。定義見 [ApplicationOutcome]；六種 Denials of Perfection 見 http://www.skyscript.co.uk/tobyn2.html#ref
 *
 * TODO : antiscia / antiscion (映點) 的入相位
 */
interface IApplicationOutcome {

  /** [p1] 與 [p2]（不分順序）若正在容許度內入相位，回傳其結局；不在入相位、或無從判定（例：快星減速被甩開）為 null */
  fun getOutcome(h: IHoroscopeModel, p1: Planet, p2: Planet): ApplicationOutcome?

  /** 全盤（七顆古典行星兩兩之間）入相位的結局 */
  fun getOutcomes(h: IHoroscopeModel): List<ApplicationOutcome> =
    Planet.classicalList.flatMapIndexed { i, p1 ->
      Planet.classicalList.drop(i + 1).mapNotNull { p2 -> getOutcome(h, p1, p2) }
    }
}
