/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.util.*
import java.util.function.Function

class IntAgeNoteWestYearImpl : IntAgeNote, Serializable {

  override fun getAgeNote(gmtJulDay: Double): String? {
    val start = revJulDayFunc.apply(gmtJulDay)
    return start.get(ChronoField.YEAR_OF_ERA).toString()
  }

  override fun getAgeNote(startAndEnd: Pair<Double, Double>): String? {
    return getAgeNote(startAndEnd.first)
  }

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(javaClass.name, locale).getString("name")
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }


  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }

  companion object {

    private val revJulDayFunc = Function<Double, ChronoLocalDateTime<*>> { JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
