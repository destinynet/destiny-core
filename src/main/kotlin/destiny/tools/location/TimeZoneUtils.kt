/** 2009/10/31 上午5:19:43 by smallufo  */
package destiny.tools.location

import java.util.*

/** 一些 TimeZone 的工具箱  */
object TimeZoneUtils {

  /**
   * 從時差（分鐘）找出 TimeZone , 要找最短的
   * @param minuteOffset
   */
  fun getTimeZone(minuteOffset: Int): TimeZone {
    var shortest = "GMT                            "
    TimeZone.getAvailableIDs(minuteOffset * 60 * 1000)
      .asSequence()
      .filter { it.length < shortest.length }
      .forEach { shortest = it }
    return TimeZone.getTimeZone(shortest.trim { it <= ' ' })
  }
}

