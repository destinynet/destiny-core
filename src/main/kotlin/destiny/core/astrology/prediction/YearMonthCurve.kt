/**
 * YearMonth Search — 逐月強度曲線與谷(trough)抽取。
 *
 * Yearly Peaks 的 delta 1 / delta 3(見 root repo 的
 * docs/plans/2026-08-24-yearly-peaks-algorithm.md §3.2/§3.3):
 * [YearMonthScorer.buildWindows] 在合併切峰與 topN 之前,其實已算出完整的逐桶強度,
 * 只是隨即丟棄 —— [YearMonthCurve] 把它曝露出來;[YearMonthTroughs] 據以抽「安靜期」。
 *
 * 谷的語意務必精確(比照 `Past.negativeControlGroups` 的教訓):
 * trough ≠「沒事」,而是「**已掃詞彙內**無能量集中」—— 掃了哪些層由呼叫端隨模型
 * 附上 `ScanCoverage`,本檔不越權宣稱。
 */
package destiny.core.astrology.prediction

import java.time.YearMonth
import kotlin.math.abs

/**
 * 一次搜尋的完整輸出:逐桶強度曲線 + 峰窗。
 *
 * @param strengths 合併切峰與 topN **之前**、AND 過濾與段層乘數/confluence **之後**的
 *   逐桶強度。無命中的桶不在 map(語意 = 0)。[SearchGrain.YEAR] 的鍵為該年 1 月
 *   (與 [YearMonthWindow.from] 同一慣例)。
 * @param windows 與 [YearMonthScorer.buildWindows] 完全相同的輸出(谷值切峰 + top-N),
 *   兩者由同一次計算產生,不會分岔。
 */
data class YearMonthCurve(
  val strengths: Map<YearMonth, Double>,
  val windows: List<YearMonthWindow>,
)

/** 安靜期:一段連續月份,期間內**每條**有分化的曲線都低於其自身的相對水位。 */
data class TroughSpan(
  val from: YearMonth,
  val to: YearMonth,
)

/**
 * 谷抽取 —— 純函式,無 ephemeris 相依。
 *
 * 三個刻意的決定(docs/plans/2026-08-24-yearly-peaks-algorithm.md §3.3):
 *  1. **相對水位,不是絕對常數**:每條曲線各取自身幅度的下段當門檻 ——
 *     `θ = min + quietFraction × (max − min)`(預設 0.25)。天生行運熱鬧的盤才有安靜期
 *     可言、冷清的盤不會全年皆谷;曲線乘上任意正常數,峰谷結構不變(scale-invariant)。
 *     不用分位數(rank-based):質量集中的分布(如 11 個月同值 + 1 個月低谷)會讓
 *     P25 落在高原值上,把整年誤標為谷 —— 幅度分數不會。
 *  2. **全平曲線不參與**(不貢獻谷、也不否決谷):平 = 無相對結構,從中讀出「何時安靜」
 *     與從中讀出「何時有事」同樣是無中生有。全部曲線皆平 → 回空(不宣稱任何結構)。
 *  3. **跨曲線取交集**:某月要「每條」有分化的曲線都安靜才算安靜 ——
 *     谷是給「全年地圖」用的強宣稱,寬鬆版(任一曲線安靜)交給呼叫端自行對單條曲線呼叫。
 */
object YearMonthTroughs {

  /**
   * @param curves 每條曲線一個 `Map<YearMonth, Double>`(缺月視為 0;
   *   [YearMonthCurve.strengths] 直接可用)。
   * @param from 評估範圍(含)。範圍外的鍵不參與。
   * @param quietFraction 相對水位:門檻取曲線幅度的此一分數(`min + quietFraction × range`),
   *   預設 0.25。
   * @return 依時間排序的安靜期;無分化曲線或無共同安靜月 → 空。
   */
  fun extract(
    curves: Collection<Map<YearMonth, Double>>,
    from: YearMonth,
    to: YearMonth,
    quietFraction: Double = 0.25,
  ): List<TroughSpan> {
    require(!from.isAfter(to)) { "from $from must not be after to $to" }
    require(quietFraction in 0.0..1.0) { "quietFraction must be in [0,1], got $quietFraction" }

    val months: List<YearMonth> = generateSequence(from) { it.plusMonths(1) }
      .takeWhile { !it.isAfter(to) }
      .toList()

    // 每條曲線:補 0 → 判分化 → 算自身門檻。tolerance 隨曲線幅度縮放(維持 scale-invariance)。
    data class Gauged(val filled: Map<YearMonth, Double>, val threshold: Double, val tolerance: Double)

    val gauged: List<Gauged> = curves.mapNotNull { curve ->
      val filled = months.associateWith { curve[it] ?: 0.0 }
      val values = filled.values
      val range = values.max() - values.min()
      val magnitude = values.maxOf { abs(it) }
      if (range <= magnitude * REL_EPS) return@mapNotNull null  // 全平(含全 0):無相對結構
      Gauged(filled, values.min() + quietFraction * range, range * REL_EPS)
    }
    if (gauged.isEmpty()) return emptyList()

    val quietMonths: List<YearMonth> = months.filter { m ->
      gauged.all { g -> g.filled.getValue(m) <= g.threshold + g.tolerance }
    }

    // 連續安靜月合併成段
    return quietMonths.fold(mutableListOf<TroughSpan>()) { acc, m ->
      val last = acc.lastOrNull()
      if (last != null && last.to.plusMonths(1) == m) acc[acc.lastIndex] = last.copy(to = m)
      else acc += TroughSpan(m, m)
      acc
    }
  }

  private const val REL_EPS = 1e-9
}
