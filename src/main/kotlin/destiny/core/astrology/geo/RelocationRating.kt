/**
 * Created by smallufo on 2026-11-20.
 */
package destiny.core.astrology.geo

import destiny.tools.Score

/**
 * 換置／回歸換置的五級評等：只看吉凶軸（[ReturnRelocationResult.score]，0.5 ＝ 中性），給使用者看的不是小數。
 *
 * 門檻是**暫定值**：實際的分數擠在 0.5 附近（同一張盤、不同城市常落在 0.50–0.52），
 * 所以門檻窄；之後以一批出生時間精確的命例 × 精選城市跑出分布，再依分位數校正（只改 [THRESHOLDS]）。
 */
enum class RelocationRating {
  STRONGLY_FAVORABLE,
  FAVORABLE,
  NEUTRAL,
  UNFAVORABLE,
  STRONGLY_UNFAVORABLE;

  companion object {
    /** 與 0.5 的差：≥ 第一個 ⇒ 明顯，≥ 第二個 ⇒ 略（負的一側對稱） */
    val THRESHOLDS: Pair<Double, Double> = 0.03 to 0.008

    fun of(score: Score): RelocationRating {
      val d = score.value - 0.5
      val (strong, slight) = THRESHOLDS
      return when {
        d >= strong   -> STRONGLY_FAVORABLE
        d >= slight   -> FAVORABLE
        d > -slight   -> NEUTRAL
        d > -strong   -> UNFAVORABLE
        else          -> STRONGLY_UNFAVORABLE
      }
    }
  }
}

/**
 * 兩座城市「占星上幾乎相同」：換置本命盤與回歸盤的上升、天頂都相差 < [angleDeg]，而且分數相差 < [scoreDelta]。
 * 鄰近的城市（相距一兩百公里）四角只差一兩度，排名的差距是雜訊，不該讀成「A 比 B 好」。
 *
 * 換置盤缺一張（[RelocationCandidate.relocatedChart] 為 null）就無從比較，回 false。
 * 2° 與 0.01 是初值，與 [RelocationRating.THRESHOLDS] 同一批資料校正。
 */
fun ReturnRelocationResult.nearlySameAs(other: ReturnRelocationResult, angleDeg: Double = 2.0, scoreDelta: Double = 0.01): Boolean {
  if (kotlin.math.abs(score.value - other.score.value) >= scoreDelta) return false
  fun close(a: RelocationCandidate, b: RelocationCandidate): Boolean {
    val x = a.relocatedChart ?: return false
    val y = b.relocatedChart ?: return false
    return listOf(1, 10).all { cusp -> x.getCuspDegree(cusp).getAngle(y.getCuspDegree(cusp)) < angleDeg }
  }
  return close(natal, other.natal) && close(returnChart, other.returnChart)
}

/**
 * 依「幾乎相同」把排好的結果分組：每組的第一個是組裡分數最高的，組內保持原順序。
 * 只和組裡的**第一個**比（不做遞移：A≈B、B≈C 不代表 A≈C）。
 */
fun List<ReturnRelocationResult>.groupNearlySame(angleDeg: Double = 2.0, scoreDelta: Double = 0.01): List<List<ReturnRelocationResult>> {
  val groups = mutableListOf<MutableList<ReturnRelocationResult>>()
  forEach { r ->
    groups.firstOrNull { it.first().nearlySameAs(r, angleDeg, scoreDelta) }?.add(r) ?: groups.add(mutableListOf(r))
  }
  return groups
}
