/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.util.*

class IntAgeNoteWestYearImpl : IntAgeNote, Serializable {

  override fun getAgeNote(gmtJulDay: Double): String {
    val start = revJulDayFunc.invoke(gmtJulDay)
    return start.get(ChronoField.YEAR_OF_ERA).toString()
  }

  override fun getAgeNote(startAndEnd: Pair<Double, Double>): String? {
    return getAgeNote(startAndEnd.first)
  }

  override fun toString(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(javaClass.name, locale).getString("name")
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }


  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is IntAgeNoteWestYearImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }


  companion object {
    private val revJulDayFunc: (Double) -> ChronoLocalDateTime<*> = { it:Double  -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
