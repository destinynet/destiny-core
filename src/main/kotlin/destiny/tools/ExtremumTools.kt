/**
 * Created by smallufo on 2026-10-30.
 */
package destiny.tools

import kotlin.math.sqrt

/** 一維單峰函數的極值搜尋（黃金分割） */
object ExtremumTools {

  private val INV_PHI = (sqrt(5.0) - 1) / 2

  /** 區間寬已逼近浮點精度、收不下去時的保險 */
  private const val MAX_ITERATIONS = 200

  /**
   * [lo, hi] 間單峰函數 [f] 的最小值，收斂到區間寬 ≤ [tolerance]；傳回 (x, f(x))。
   * 每一輪只多算一次 [f]（三分法要兩次）
   */
  fun minimize(lo: Double, hi: Double, tolerance: Double, f: (Double) -> Double): Pair<Double, Double> {
    require(lo <= hi) { "lo ($lo) > hi ($hi)" }
    var a = lo
    var b = hi
    var c = b - (b - a) * INV_PHI
    var d = a + (b - a) * INV_PHI
    var fc = f(c)
    var fd = f(d)
    var i = 0
    while (b - a > tolerance && i++ < MAX_ITERATIONS) {
      if (fc <= fd) {
        b = d; d = c; fd = fc
        c = b - (b - a) * INV_PHI; fc = f(c)
      } else {
        a = c; c = d; fc = fd
        d = a + (b - a) * INV_PHI; fd = f(d)
      }
    }
    val x = (a + b) / 2
    return x to f(x)
  }

  /** [lo, hi] 間單峰函數 [f] 的最大值；傳回 (x, f(x)) */
  fun maximize(lo: Double, hi: Double, tolerance: Double, f: (Double) -> Double): Pair<Double, Double> =
    minimize(lo, hi, tolerance) { -f(it) }.let { (x, v) -> x to -v }
}
