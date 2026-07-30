/**
 * Created by smallufo on 2025-07-23.
 */
package destiny.core.calendar.chinese

import java.time.LocalDateTime
import java.time.YearMonth


data class YearMonthRange(val start: YearMonth, val endInclusive: YearMonth) {
  val fromTime: LocalDateTime = start.atDay(1).atStartOfDay()
  val toTime: LocalDateTime = endInclusive.plusMonths(1).atDay(1).atStartOfDay()

  fun overlaps(other: YearMonthRange): Boolean =
    !start.isAfter(other.endInclusive) && !endInclusive.isBefore(other.start)

  operator fun plus(other: YearMonthRange): YearMonthRange =
    YearMonthRange(minOf(start, other.start), maxOf(endInclusive, other.endInclusive))
}

/**
 * 各自向外擴張 [extMonth] 個月後，把相鄰／重疊者合併。
 *
 * 這是「有延時的事件」版本；點事件請用同名的 `List<YearMonth>` 多載，
 * 它只是把每個點視為 `start == endInclusive` 的退化區間後轉呼叫此處。
 */
@JvmName("groupMergedRangesOfRanges")
fun List<YearMonthRange>.groupMergedRanges(extMonth: Int = 1): List<YearMonthRange> =
  this
    .map { YearMonthRange(it.start.minusMonths(extMonth.toLong()), it.endInclusive.plusMonths(extMonth.toLong())) }
    .sortedBy { it.start }
    .fold(mutableListOf()) { acc, range ->
      if (acc.isEmpty()) {
        acc.add(range)
      } else {
        val last = acc.last()
        if (!range.start.isAfter(last.endInclusive.plusMonths(1))) {
          acc[acc.lastIndex] = YearMonthRange(
            last.start,
            maxOf(last.endInclusive, range.endInclusive)
          )
        } else {
          acc.add(range)
        }
      }
      acc
    }

fun List<YearMonth>.groupMergedRanges(extMonth: Int = 1): List<YearMonthRange> =
  this.map { YearMonthRange(it, it) }.groupMergedRanges(extMonth)
