/**
 * Created by smallufo on 2019-09-26.
 */
package destiny.core.astrology.classical.rules

import destiny.core.DayNight
import destiny.core.IPattern
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.ApplicationOutcome
import destiny.core.astrology.classical.IApplicationOutcome
import destiny.tools.JSerializable

interface IClassicalPattern : IPattern

enum class RuleType {
  ESSENTIAL,
  ACCIDENTAL,
  DEBILITY,
  MISC
}

interface IPlanetPattern : IClassicalPattern {
  val ruleType: RuleType
  val planet: Planet
}


/**
 * 一次古典判斷的輸入：被判的盤（[model]，本類別直接代理它，factory 照舊用 `h.getPosition(…)`），
 * 加上**判盤者已經知道、各 factory 不該各自重算的東西**：
 *
 * - [sect]：呼叫端依出生時刻精度判定的日夜盤（`DayNightSourceResolver.resolveSect`）。有值時三分性、外來、得時／不得時一律以它為準；
 *   null ＝ 呼叫端沒給（或判不出來），factory 退回以 [destiny.core.astrology.IDayNight] 用盤面時刻現算
 * - [outcomes]：入相位引擎對全盤 21 對星的結局，**一張盤只算一次**，插隊／撲空／退縮的幾個 factory 共用
 *   （引擎每次呼叫都要往後追星曆，原本每個 factory 對每顆星各問一輪，同一對星算到六次）
 *
 * 同一個 [IHoroscopeModel] 可以有多個 input（本命盤給 sect、卜卦盤不給）；不跨盤重用。
 */
class ClassicalInput(
  val model: IHoroscopeModel,
  val sect: DayNight? = null,
) : IHoroscopeModel by model {

  private val outcomeCache = mutableMapOf<IApplicationOutcome, List<ApplicationOutcome>>()

  /** [engine] 對這張盤全部入相位的結局，第一次呼叫才算 */
  fun outcomes(engine: IApplicationOutcome): List<ApplicationOutcome> =
    outcomeCache.getOrPut(engine) { engine.getOutcomes(model) }

  /** [p1] 與 [p2]（不分順序）的入相位結局 */
  fun outcomeOf(engine: IApplicationOutcome, p1: Planet, p2: Planet): ApplicationOutcome? =
    outcomes(engine).firstOrNull { (it.applying === p1 && it.applied === p2) || (it.applying === p2 && it.applied === p1) }
}

interface IPlanetPatternFactory : JSerializable {

  fun getPatterns(planet: Planet, h: ClassicalInput): List<IPlanetPattern>

  /** 沒有額外輸入（日夜盤由 factory 現算、引擎結果不與別的 factory 共用）；測試與單一 factory 的呼叫用 */
  fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> =
    getPatterns(planet, h as? ClassicalInput ?: ClassicalInput(h))

}
