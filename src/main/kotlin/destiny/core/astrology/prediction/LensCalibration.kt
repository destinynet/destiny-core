/**
 * Range Peaks — 個人化校準(Yearly Peaks B2,
 * root docs/plans/2026-08-24-yearly-peaks-algorithm.md §3.4)。
 *
 * 引擎權重「無公認真值、刻意可調參」([YearMonthScoringConfig] KDoc);通用校準靠名人
 * fixture,但本產品有更好的東西 —— **使用者自己的過往事件**。對每個 lens 回測
 * 「此人的過往事件是否落在該 lens 的高分窗內」,得到**此 lens 對此人**的實測可信度。
 *
 * 這是負對照哲學的量化版:r3 盲測裡負對照窗讓 LLM 自己把規則標「作廢」;
 * 校準表給的是「這個 pattern 對這個人過去**沒有**用」的證據,如實交給判讀端。
 */
package destiny.core.astrology.prediction

/**
 * 校準分層。**規則保守**:典型校準集 ~12 筆事件攤到 6 個 lens,單 lens 常常只有 1–2 筆
 * —— 假裝那是統計就是自欺,故樣本不足一律 [UNTESTED]。
 * [UNTESTED] 的 lens 峰仍出現在報告,但素材裡的校準行如實寫「無足夠歷史樣本」。
 */
enum class CalibrationTier {
  /** 樣本足夠且命中率顯著高於隨機(見 [CalibrationConfig.strongTopNHitRate] 的推導)。 */
  TESTED_STRONG,

  /** 樣本足夠但命中率平平 —— 「這個 pattern 對這個人過去沒有用」本身就是可交付的證據。 */
  TESTED_WEAK,

  /** 樣本 < [CalibrationConfig.minSamples],不做任何宣稱。 */
  UNTESTED,
}

/**
 * @param minSamples 低於此樣本數一律 [CalibrationTier.UNTESTED]。
 * @param topN 回測的「命中」名次門檻(與名人基準同一把尺:top-3)。
 * @param strongTopNHitRate STRONG 門檻。推導:回測協定為事件月 ±窗共 36 個月、
 *   引擎預設 topN=12 個窗、命中門檻 top-3 —— 隨機基線 ≈ 3/12 = 0.25,
 *   取其兩倍 0.5 當「顯著高於隨機」。可調參,無公認真值。
 */
data class CalibrationConfig(
  val minSamples: Int = 3,
  val topN: Int = 3,
  val strongTopNHitRate: Double = 0.5,
)

/**
 * 單一 lens 對單一使用者的校準結果。
 *
 * @param results 逐事件的回測明細 —— 保留可追溯性:素材端引用「5 筆中 3 筆入 top-3」時,
 *   哪 5 筆、各排第幾,都指得出來。
 */
data class LensCalibration(
  val lensId: String,
  val results: List<BacktestResult>,
  val topNHitRate: Double,
  val meanPercentile: Double,
  val tier: CalibrationTier,
) {
  val sampleCount: Int get() = results.size
}

object LensCalibrator {

  fun calibrate(
    lensId: String,
    results: List<BacktestResult>,
    config: CalibrationConfig = CalibrationConfig(),
  ): LensCalibration {
    val summary = YearMonthBacktest.summarize(results)
    val tier = when {
      results.size < config.minSamples                    -> CalibrationTier.UNTESTED
      summary.topNHitRate >= config.strongTopNHitRate     -> CalibrationTier.TESTED_STRONG
      else                                                -> CalibrationTier.TESTED_WEAK
    }
    return LensCalibration(lensId, results, summary.topNHitRate, summary.meanPercentile, tier)
  }
}
