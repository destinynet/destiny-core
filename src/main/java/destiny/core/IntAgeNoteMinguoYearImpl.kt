/**
 * Created by smallufo on 2017-10-23.
 */
package destiny.core

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.ChronoField
import java.util.*

/** 民國紀年  */
class IntAgeNoteMinguoYearImpl : IntAgeNote, Serializable {

  /**
   * 民國元年前，不輸出
   * 民國元年後，輸出 "民1" , "民109" 這樣的字串
   */
  override fun getAgeNote(gmtJulDay: Double): String? {
    val start = revJulDayFunc.invoke(gmtJulDay)
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

  companion object {
    private val revJulDayFunc: (Double) -> ChronoLocalDateTime<*> = { it:Double  -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(it) }
  }
}
