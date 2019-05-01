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

/** 民國紀年  */
class IntAgeNoteMinguoYearImpl : IntAgeNote, Serializable {

  override fun getAgeNote(gmtJulDay: Double): String? {
    val start = revJulDayFunc.apply(gmtJulDay)
    val westYear = start.get(ChronoField.YEAR_OF_ERA)
    return if (westYear >= 1912) {
      "民" + (westYear - 1911)
    } else {
      null
    }
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

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is IntAgeNoteMinguoYearImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }


  companion object {

    private val revJulDayFunc = Function<Double, ChronoLocalDateTime<*>> { JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
