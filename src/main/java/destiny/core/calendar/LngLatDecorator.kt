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

  private val implMap = mapOf<Locale, Decorator<ILocation>>(
    Locale.TAIWAN to LngLatDecoratorTaiwan(),
    Locale.CHINA to LngLatDecoratorChina(),
    Locale.ENGLISH to LngLatDecoratorEnglish()
  )

  fun getOutputString(location: ILocation, locale: Locale): String {
    return implMap.getOutputString(location, locale)
  }
}


class LngLatDecoratorTaiwan : Decorator<ILocation> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: ILocation): String {
    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.lng, Locale.TAIWAN))
      append(", ")
      append(LatDecorator.getOutputString(value.lat, Locale.TAIWAN))
    }.toString()
  }
}

class LngLatDecoratorChina : Decorator<ILocation> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: ILocation): String {
    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.lng, Locale.SIMPLIFIED_CHINESE))
      append(", ")
      append(LatDecorator.getOutputString(value.lat, Locale.SIMPLIFIED_CHINESE))
    }.toString()
  }
}


class LngLatDecoratorEnglish : Decorator<ILocation> {

  override fun getOutputString(value: ILocation): String {

    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.lng, Locale.ENGLISH))
      append(", ")
      append(LatDecorator.getOutputString(value.lat, Locale.ENGLISH))
    }.toString()
  }
}
