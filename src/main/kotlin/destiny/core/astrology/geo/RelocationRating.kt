/**
 * Created by smallufo on 2026-11-20.
 */
package destiny.core.astrology.geo

import destiny.tools.Score

/**
 * 換置／回歸換置的五級評等：只看吉凶軸（0.5 ＝ 中性），給使用者看的不是小數。
 *
 * 門檻依出生時間精確的命例 × 精選城市的分數分布校正（只改 [THRESHOLDS]／[SINGLE_LAYER_THRESHOLDS]）。
 * 門檻對 0.5 對稱，所以「什麼線都沒有」永遠是中性；吉星的線推得比凶星遠，有利的一側本來就比較多。
 * 以兩層加權的分數為例，各級大約占：明顯有利 7%、略有利 27%、中性 39%、略不利 25%、明顯不利 2%。
 */
enum class RelocationRating {
  STRONGLY_FAVORABLE,
  FAVORABLE,
  NEUTRAL,
  UNFAVORABLE,
  STRONGLY_UNFAVORABLE;

  companion object {
    /** 兩層加權（換置本命盤＋回歸盤，[ReturnRelocationResult.score]）：與 0.5 的差 ≥ 第一個 ⇒ 明顯，≥ 第二個 ⇒ 略（負的一側對稱） */
    val THRESHOLDS: Pair<Double, Double> = 0.03 to 0.008

    /**
     * 只有一層（換置本命盤的適合度，[IRelocationScorer.suitability]）：單層的分數沒有被另一層平均掉，分布較寬，
     * 用同樣的門檻會有兩倍多的城市落在「明顯」；這組門檻讓各級的比例與兩層加權相當
     */
    val SINGLE_LAYER_THRESHOLDS: Pair<Double, Double> = 0.04 to 0.01

    /** 兩層加權的評等 */
    fun of(score: Score): RelocationRating = of(score, THRESHOLDS)

    fun of(score: Score, thresholds: Pair<Double, Double>): RelocationRating {
      val d = score.value - 0.5
      val (strong, slight) = thresholds
      return when {
        d >= strong   -> STRONGLY_FAVORABLE
        d >= slight   -> FAVORABLE
        d > -slight   -> NEUTRAL
        d > -strong   -> UNFAVORABLE
        else          -> STRONGLY_UNFAVORABLE
      }
    }

    /** 單層（換置本命盤）的評等 */
    fun ofSingleLayer(suitability: Score): RelocationRating = of(suitability, SINGLE_LAYER_THRESHOLDS)
  }
}

/**
 * 兩座城市「占星上幾乎相同」：換置本命盤與回歸盤的上升、天頂都相差 < [angleDeg]，而且分數相差 < [scoreDelta]。
 * 鄰近的城市（相距一兩百公里）四角只差一兩度，排名的差距是雜訊，不該讀成「A 比 B 好」。
 *
 * 換置盤缺一張（[RelocationCandidate.relocatedChart] 為 null）就無從比較，回 false。
 * 2° 與 0.01 經同一批資料驗證：相距 100 km 內的城市幾乎全部併成一組、100–200 km 約八成、800 km 以上幾乎不會；
 * 起決定作用的是 2°（分數差改成 0.02 結果幾乎不變）。
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
