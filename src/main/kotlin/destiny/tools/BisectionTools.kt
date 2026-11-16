/**
 * Created by smallufo on 2026-11-16.
 */
package destiny.tools

/** 一維的二分搜尋：把夾住分界的區間對半收窄 */
object BisectionTools {

  /**
   * [lo, hi] 之間恰有一個分界：分界以前的位置 [onLoSide] 為 true，以後為 false（呼叫端保證 lo 在 true 那一邊、hi 在 false 那一邊）。
   * 做 [iterations] 輪，每一輪取中點 `(lo + hi) / 2` 問一次 [onLoSide]，把區間收進分界所在的那一半；回傳最後的 (lo, hi)。
   *
   * 次數固定、不看容許誤差：同樣的輸入必得同樣的結果，區間寬是原來的 2^-[iterations]。
   * 要分界前、後哪一端，或兩端的中點，由呼叫端自己取
   */
  inline fun bisect(lo: Double, hi: Double, iterations: Int, onLoSide: (Double) -> Boolean): Pair<Double, Double> {
    var a = lo
    var b = hi
    repeat(iterations) {
      val mid = (a + b) / 2
      if (onLoSide(mid)) a = mid else b = mid
    }
    return a to b
  }
}
