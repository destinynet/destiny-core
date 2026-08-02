/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.tools.JSerializable
import java.time.temporal.ChronoField

class IntAgeNoteWestYearImpl(val julDayResolver: JulDayResolver) : IIntAgeNote, JSerializable {

  override val intAgeNote: IntAgeNote = IntAgeNote.WestYear

  override fun getAgeNote(gmtJulDay: GmtJulDay): String {
    val start = julDayResolver.getLocalDateTime(gmtJulDay)
    return start.get(ChronoField.YEAR_OF_ERA).toString()
  }

  override fun getAgeNote(startAndEnd: Pair<GmtJulDay, GmtJulDay>): String {
    return getAgeNote(startAndEnd.first)
  }
}
