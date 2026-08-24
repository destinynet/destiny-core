/**
 * Peak Slice — 逐峰交棒的素材切片(Yearly Peaks B3-a,
 * root docs/plans/2026-08-24-yearly-peaks-algorithm.md §3.5)。
 *
 * 引擎能力地圖的分工線落在這裡:引擎判得了「能量何時集中」(recall),判不了吉凶
 * (valence ⊥ intensity,timing-search-design §8 硬結論)。本檔是交棒前的最後一站 ——
 * 把單一峰窗的全部確定性素材(貢獻窗原始特徵、校準行、窗內快層/月相、候選日)
 * 打包成一個小 context,交給逐峰 LLM 判方向寫敘事。
 *
 * 純資料與純函式,無 ephemeris 相依;掃描與組裝在 destiny-core-impl 的
 * `RangePeaksService.handoff`。
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.ITimeLineEvent
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * 快層事件叢集出的候選日段:峰窗內數個快層相位在幾天內收斂 → 「±[halfWindowDays] 天
 * 精度的候選日」。
 *
 * @param peakDay 密度極大日(±half 窗內事件數最多;同密取最早)。
 * @param eventCount [peakDay] ±half 窗內的快層事件數 —— 交判讀端當權重,1 筆的孤立段是弱訊號。
 */
data class CandidateDaySpan(
  val from: LocalDate,
  val to: LocalDate,
  val peakDay: LocalDate,
  val eventCount: Int,
)

object CandidateDays {

  /**
   * 密度極大值 ＋ 非極大抑制:對每個事件日算 ±[halfWindowDays] 窗內的事件數(密度),
   * 依密度降冪(同密取早)貪婪選日,已選日的 ±2×half 鄰域內不再選(窗不重疊),
   * 取 top-[topK] 後**恢復時序**輸出。段的 from/to 是該窗內實際有事件的日界。
   *
   * ⚠️ 不用「相鄰日差 ≤ gap 遞移連鎖」:快層密度實測近 1 筆/天(Epstein 2019 峰窗
   * 111 筆/4 個月),遞移連鎖會把數週黏成一段,「±2 天」的精度語意直接蒸發 ——
   * B3-a 首測即中,密度極大值是修正後的定式。
   *
   * 誠實邊界(與 `ScanCoverage` 同精神):候選日只來自**已掃的快層詞彙**
   * (日水金對本命),段外的日子是取樣邊界不是安靜;呼叫端的素材必須原樣傳達。
   */
  fun cluster(days: List<LocalDate>, halfWindowDays: Long = 2, topK: Int = 3): List<CandidateDaySpan> {
    require(halfWindowDays >= 0) { "halfWindowDays must be >= 0" }
    require(topK >= 1) { "topK must be >= 1" }
    if (days.isEmpty()) return emptyList()

    val counts: Map<LocalDate, Int> = days.groupingBy { it }.eachCount()
    fun density(d: LocalDate): Int =
      counts.entries.sumOf { (day, c) -> if (ChronoUnit.DAYS.between(d, day).let { it >= -halfWindowDays && it <= halfWindowDays }) c else 0 }

    val ranked: List<LocalDate> = counts.keys.sortedWith(compareByDescending<LocalDate> { density(it) }.thenBy { it })
    val selected = mutableListOf<LocalDate>()
    for (d in ranked) {
      if (selected.size >= topK) break
      if (selected.none { ChronoUnit.DAYS.between(it, d).let { g -> g >= -2 * halfWindowDays && g <= 2 * halfWindowDays } }) selected += d
    }

    return selected.map { d ->
      val inWindow = counts.keys.filter { ChronoUnit.DAYS.between(d, it).let { g -> g >= -halfWindowDays && g <= halfWindowDays } }
      CandidateDaySpan(from = inWindow.min(), to = inWindow.max(), peakDay = d, eventCount = density(d))
    }.sortedBy { it.from }
  }
}

/**
 * 一座峰的完整交棒素材(確定性部分;使用者提問、natal、triggerTable 由商業層另行組裝)。
 *
 * @param calibrations 僅**本峰有貢獻**的 lens 之校準行(含 UNTESTED 的誠實行;
 *   空表示呼叫端尚未執行校準,不是「全部可信」)。
 * @param fastEvents 峰窗內快層(日水金對本命)原始事件 —— 用途是把峰定位到 ±2 天,
 *   不是自己找窗口([candidateDays] 即其叢集摘要)。
 * @param lunarPhaseEvents 峰窗內月相(朔望;僅日月度數,打到哪個本命點由判讀端複合)。
 */
data class PeakSlice(
  val peak: RangePeak,
  val calibrations: List<LensCalibration>,
  val fastEvents: List<ITimeLineEvent>,
  val lunarPhaseEvents: List<ITimeLineEvent>,
  val candidateDays: List<CandidateDaySpan>,
)
