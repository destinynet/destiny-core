/**
 * Range Peaks — 多鏡頭峰谷模型與組裝(純函式,無 ephemeris 相依)。
 *
 * Yearly Peaks B1 的輸出模型(root docs/plans/2026-08-24-yearly-peaks-algorithm.md §四)。
 * **命名刻意不含 Yearly**:from/to 任意 —— 年度商品給 365 天、季度商品給 90 天,
 * 同一個模型與組裝邏輯,無任何「一年」假設。
 */
package destiny.core.astrology.prediction

import java.time.YearMonth

/** 一個 lens 掃出的完整曲線(id 對應 [SearchProfile.id])。 */
data class LensCurve(
  val lensId: String,
  val curve: YearMonthCurve,
)

/**
 * 跨鏡頭合併後的峰:重疊的各 lens 峰窗併成一座,範圍取聯集。
 *
 * @param strength 貢獻窗中的最大值。⚠️ 跨 lens 僅**粗可比**:各 lens 共用同一
 *   scoring 家族、僅 aspectWeights 相異 —— 排序夠用,差值不宜過度解讀。
 * @param contributions lensId → 該 lens 落在本峰內的最強峰窗(原始特徵的入口:
 *   instantHits / periodHits 都在窗上,逐峰 LLM 素材由此取)。
 */
data class RangePeak(
  val from: YearMonth,
  val to: YearMonth,
  val strength: Double,
  val contributions: Map<String, YearMonthWindow>,
) {
  /** 多鏡頭共振:≥2 個 lens 在此峰重疊(古典多重證言的跨主題版;不改分數,交判讀端當特徵)。 */
  val confluent: Boolean get() = contributions.size >= 2
}

/**
 * 多鏡頭掃描的組裝結果。
 *
 * @param peaks 依 strength 降冪的 top-K 峰。
 * @param troughs 安靜期(所有有分化的 lens 皆低於自身相對水位),**已扣除被 peaks 覆蓋的月份**
 *   ——「全年地圖」上峰與谷不重疊;谷的誠實邊界(已掃詞彙 ≠ 人生全部)由呼叫端隨模型附 coverage。
 */
data class RangePeaksModel(
  val from: YearMonth,
  val to: YearMonth,
  val lenses: List<LensCurve>,
  val peaks: List<RangePeak>,
  val troughs: List<TroughSpan>,
  /** 個人化校準(B2):各 lens 對此人過往事件的實測可信度;由 service 於組裝後填入。 */
  val calibrations: List<LensCalibration> = emptyList(),
)

object RangePeaks {

  /**
   * 把各 lens 的曲線與峰窗組裝成跨鏡頭模型:
   *  1. 收集所有 lens 的峰窗,依月份**重疊**(非相鄰)遞移合併成峰群 —— 相鄰而不重疊的
   *     窗來自谷值切峰,是兩座峰,不得黏回去。
   *  2. 每群 → [RangePeak](範圍聯集、strength 取最大、每 lens 留最強貢獻窗)。
   *  3. 依 strength 降冪取 top-K。
   *  4. 谷:[YearMonthTroughs.extract] 後,扣除被選中 peaks 覆蓋的月份。
   */
  fun assemble(
    lensCurves: List<LensCurve>,
    from: YearMonth,
    to: YearMonth,
    topK: Int = 5,
    quietFraction: Double = 0.25,
  ): RangePeaksModel {
    require(!from.isAfter(to)) { "from $from must not be after to $to" }

    // 1. 收集 + 依 from 排序,sweep-line 依重疊遞移分群
    val tagged: List<Pair<String, YearMonthWindow>> = lensCurves
      .flatMap { lc -> lc.curve.windows.map { lc.lensId to it } }
      .sortedBy { it.second.from }

    val groups = mutableListOf<MutableList<Pair<String, YearMonthWindow>>>()
    var groupMaxTo: YearMonth? = null
    for (tw in tagged) {
      if (groupMaxTo != null && !tw.second.from.isAfter(groupMaxTo)) {
        groups.last() += tw
        if (tw.second.to.isAfter(groupMaxTo)) groupMaxTo = tw.second.to
      } else {
        groups += mutableListOf(tw)
        groupMaxTo = tw.second.to
      }
    }

    // 2.~3. 峰群 → RangePeak → top-K
    val peaks = groups.map { group ->
      RangePeak(
        from = group.minOf { it.second.from },
        to = group.maxOf { it.second.to },
        strength = group.maxOf { it.second.strength },
        contributions = group.groupBy({ it.first }, { it.second })
          .mapValues { (_, ws) -> ws.maxBy { it.strength } },
      )
    }.sortedByDescending { it.strength }.take(topK)

    // 4. 谷,扣除峰覆蓋月
    val rawTroughs = YearMonthTroughs.extract(lensCurves.map { it.curve.strengths }, from, to, quietFraction)
    val peakMonths: Set<YearMonth> = peaks.flatMap { p ->
      generateSequence(p.from) { it.plusMonths(1) }.takeWhile { !it.isAfter(p.to) }
    }.toSet()
    val troughs = rawTroughs.flatMap { span ->
      generateSequence(span.from) { it.plusMonths(1) }.takeWhile { !it.isAfter(span.to) }
        .filter { it !in peakMonths }
        .fold(mutableListOf<TroughSpan>()) { acc, m ->
          val last = acc.lastOrNull()
          if (last != null && last.to.plusMonths(1) == m) acc[acc.lastIndex] = last.copy(to = m)
          else acc += TroughSpan(m, m)
          acc
        }
    }

    return RangePeaksModel(from, to, lensCurves, peaks, troughs)
  }
}
