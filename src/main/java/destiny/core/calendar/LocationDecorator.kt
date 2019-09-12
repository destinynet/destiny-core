/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:35:01
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
import java.text.DecimalFormat
import java.util.*

object LocationDecorator {
  private val implMap = mapOf<Locale, Decorator<ILocation>>(
    Locale.TAIWAN to LocationDecoratorTaiwan(),
    Locale.CHINA to LocationDecoratorChina(),
    Locale.ENGLISH to LocationDecoratorEnglish()
  )

  fun getOutputString(location: ILocation, locale: Locale): String {

    val bestMatchingLocale = LocaleTools.getBestMatchingLocale(locale, implMap.keys) ?: implMap.keys.first()
    return implMap.getValue(bestMatchingLocale).getOutputString(location)
  }

}


class LocationDecoratorTaiwan : Decorator<ILocation> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: ILocation): String {

    val sb = StringBuilder()
    sb.append(LngLatDecorator.getOutputString(value, Locale.TAIWAN))

    value.altitudeMeter?.also {
      sb.append("高度 ").append(it).append(" 公尺.")
    }
    sb.append(" 時區 ").append(value.timeZone.id)
    if (value.hasMinuteOffset)
      sb.append(" 時差 ").append(value.finalMinuteOffset).append(" 分鐘.")

    return sb.toString()
  }

}


class LocationDecoratorChina : Decorator<ILocation> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: ILocation): String {
    val sb = StringBuilder()
    sb.append(LngLatDecorator.getOutputString(value, Locale.CHINA))

    value.altitudeMeter?.also {
      sb.append("高度 ").append(it).append(" 米")
    }
    sb.append(" 时区 ").append(value.timeZone.id)
    if (value.hasMinuteOffset)
      sb.append(" 时差 ").append(value.finalMinuteOffset).append(" 分钟.")

    return sb.toString()
  }

}


class LocationDecoratorEnglish : Decorator<ILocation> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: ILocation): String {
    val sb = StringBuilder()
    sb.append(LngLatDecorator.getOutputString(value, Locale.ENGLISH))

    sb.append(" GMT offset ").append(value.timeZone.rawOffset / (60000 * 60)).append(" hours")
    value.altitudeMeter?.also {
      sb.append(", Alt ").append(it).append(" m.")
    }
    return sb.toString()
  }

}
