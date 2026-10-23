/**
 * Created by smallufo on 2026-10-23.
 */
package destiny.tools

object SampleTools {

  /**
   * 規則取樣點與終點的最小間距。DTO 的取樣值以兩位小數截斷輸出（DoubleTwoDecimalSerializer），
   * 間距不到 0.01 的兩點會輸出成同一個值。
   */
  const val MIN_END_GAP = 0.01

  /**
   * 區間 [0, total] 的等距取樣點：0、step、2·step…，最後一定補上 [total] 本身（含終點）。
   * 與 [total] 距離小於 [MIN_END_GAP] 的規則點捨去，終點不會與前一點重複；0 一律保留。
   * 單位由呼叫端決定（目前兩個 caller 都是分鐘）。
   */
  fun sampleSteps(total: Double, step: Double): List<Double> {
    require(step > 0) { "step must be positive : $step" }
    require(total.isFinite() && total >= 0) { "total must be finite and not negative : $total" }
    return buildList {
      add(0.0)
      var i = 1
      while (total - i * step >= MIN_END_GAP) {
        add(i * step)
        i++
      }
      if (total > 0) add(total)
    }
  }
}
