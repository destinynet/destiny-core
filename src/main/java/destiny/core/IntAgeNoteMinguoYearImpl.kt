/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import java.io.Serializable
import java.time.temporal.ChronoField

/** 民國紀年  */
class IntAgeNoteMinguoYearImpl(val julDayResolver: JulDayResolver) : IIntAgeNote, Serializable {

  override val intAgeNote: IntAgeNote = IntAgeNote.Minguo

  /**
   * 民國元年前，不輸出
   * 民國元年後，輸出 "民1" , "民109" 這樣的字串
   */
  override fun getAgeNote(gmtJulDay: GmtJulDay): String? {
    val start = julDayResolver.getLocalDateTime(gmtJulDay)
    val westYear = start.get(ChronoField.YEAR_OF_ERA)
    return if (westYear >= 1912) {
      "民" + (westYear - 1911)
    } else {
      null
    }
  }

  override fun getAgeNote(startAndEnd: Pair<GmtJulDay, GmtJulDay>): String? {
    return getAgeNote(startAndEnd.first)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is IntAgeNoteMinguoYearImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

}
