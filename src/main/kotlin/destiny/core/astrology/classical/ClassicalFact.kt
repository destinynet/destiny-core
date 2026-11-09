/**
 * Created by smallufo on 2026-10-30.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight
import destiny.core.astrology.IHoroscopeModel

/**
 * 古典占星的結構化事實：盤面上發生了什麼，帶著數值（容許度、日數、軌跡）。
 *
 * 與 [destiny.core.astrology.classical.rules.IPlanetPattern] 是兩層：那邊是依古典規則給的吉凶**判斷**
 * （例：`Refrain_from_Mars_Saturn` ＝ 退縮的對象是火或土的 [ApplicationOutcome.Refranation]，再評為偶然尊貴），
 * 這邊是判斷所根據的**事實**。同一個事實在不同派別下可以有不同評價，所以兩層不合併。
 *
 * 是否需要星曆（往後推算位置、停滯）是計算端的事，由各 Impl 的建構子表達，不進這個階層。
 */
sealed interface ClassicalFact

/**
 * 古典事實的來源：一種技法對一張盤列出它的事實。各技法的介面（[IAversion]、[IAntiscia]、[ICombustion]…）都是它，
 * 所以「全盤的古典事實」就是一串來源依序攤平（`ClassicalPatternContext.classicalFacts`）——
 * 新增一種技法只要實作這個介面並掛進那一串，彙整的地方不必再認得它。
 *
 * [sect]：盤的日夜，由呼叫端判定（要看出生時刻精度）；null ＝ 不明。與日夜無關的技法不看它
 */
fun interface IClassicalFactSource {
  fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact>

  /** 這個來源看不看 `sect`。全部來源都不看時，彙整端不必為了它去判日夜（那要盤面時刻與地點） */
  val usesSect: Boolean get() = false
}
