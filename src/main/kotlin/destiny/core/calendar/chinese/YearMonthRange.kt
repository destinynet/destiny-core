/**
 * Created by smallufo on 2025-07-23.
 */
package destiny.core.calendar.chinese

import java.time.LocalDateTime
import java.time.YearMonth


data class YearMonthRange(val start: YearMonth, val endInclusive: YearMonth) {
  val fromTime: LocalDateTime = start.atDay(1).atStartOfDay()
  val toTime: LocalDateTime = endInclusive.plusMonths(1).atDay(1).atStartOfDay()
}

fun List<YearMonth>.groupMergedRanges(extMonth: Int = 1): List<YearMonthRange> =
  this
    .map { YearMonthRange(it.minusMonths(extMonth.toLong()), it.plusMonths(extMonth.toLong())) }
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
