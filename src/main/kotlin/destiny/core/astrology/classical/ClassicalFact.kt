/**
 * Created by smallufo on 2026-10-30.
 */
package destiny.core.astrology.classical

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
