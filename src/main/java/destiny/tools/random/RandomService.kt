/**
 * @author smallufo
 * Created on 2011/4/10 at 下午11:21:13
 */
package destiny.tools.random

import destiny.core.calendar.Constants
import java.time.*
import java.time.temporal.ChronoUnit

interface RandomService {

  /**
   * @param count 要傳回的整數數量
   * @param min   最小的整數值 (包含)
   * @param max   最大的整數值 (包含)
   */
  fun getIntegers(count: Int, min: Int, max: Int): IntArray

  fun getIntegerList(count: Int, min: Int, max: Int): List<Int> {
    return getIntegers(count, min, max).toList()
  }

  fun getLongs(count: Int, minInclusive: Long, maxInclusive: Long): LongArray

  fun getLong(minInclusive: Long, maxInclusive: Long): Long {
    return getLongs(1, minInclusive, maxInclusive)[0]
  }

  fun <T : Enum<*>> randomEnum(clazz: Class<T>): T {
    val length = clazz.enumConstants.size
    val r = getIntegers(length, 0, length - 1)[0]
    return clazz.enumConstants[r]
  }

  fun getYinYangs(count: Int): BooleanArray

  fun getRandomTime(fromEpochSecond: Long, toEpochSecond: Long, zoneId: ZoneId): LocalDateTime {
    return Instant.ofEpochSecond(getLong(fromEpochSecond, toEpochSecond)).atZone(zoneId).toLocalDateTime()
  }

  fun getRandomTime(from: LocalDate, to: LocalDate): LocalDateTime {
    val days = ChronoUnit.DAYS.between(from, to)

    val randomDays = getLong(0, days)
    val randomSecs = getLong(0, Constants.SECONDS_OF_DAY - 1)
    return from.plusDays(randomDays).atTime(LocalTime.MIDNIGHT).plusSeconds(randomSecs)
  }
}
