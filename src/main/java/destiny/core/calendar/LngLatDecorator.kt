/**
 * Created by smallufo on 2017-05-05.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.text.DecimalFormat
import java.util.*

/**
 * 純粹只有經緯度，以及時區
 */
object LngLatDecorator {

  private val implMap = mapOf<Locale, Decorator<Location>>(
    Locale.TAIWAN to LngLatDecoratorTaiwan(),
    Locale.CHINA to LngLatDecoratorChina(),
    Locale.ENGLISH to LngLatDecoratorEnglish()
  )

  fun getOutputString(location: Location, locale: Locale): String {
    return implMap.getOutputString(location, locale)
  }
}


class LngLatDecoratorTaiwan : Decorator<Location> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: Location): String {
    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.longitude , Locale.TAIWAN))
      append(", ")
      append(LatDecorator.getOutputString(value.latitude , Locale.TAIWAN))
    }.toString()
  }
}

class LngLatDecoratorChina : Decorator<Location> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: Location): String {
    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.longitude , Locale.SIMPLIFIED_CHINESE))
      append(", ")
      append(LatDecorator.getOutputString(value.latitude , Locale.SIMPLIFIED_CHINESE))
    }.toString()
  }
}


class LngLatDecoratorEnglish : Decorator<Location> {

  override fun getOutputString(value: Location): String {

    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.longitude , Locale.ENGLISH))
      append(", ")
      append(LatDecorator.getOutputString(value.latitude , Locale.ENGLISH))
    }.toString()
  }
}
