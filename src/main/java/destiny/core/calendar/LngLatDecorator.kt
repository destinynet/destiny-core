/**
 * Created by smallufo on 2017-05-05.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.LocaleTools
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
    val bestMatchingLocale = LocaleTools.getBestMatchingLocale(locale, implMap.keys) ?: implMap.keys.first()
    return implMap[bestMatchingLocale]!!.getOutputString(location)
  }
}


class LngLatDecoratorTaiwan : Decorator<Location> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(location: Location): String {
    val sb = StringBuilder()
    sb.append(if (location.eastWest == Location.EastWest.EAST) "東經" else "西經").append(" ")
    sb.append(location.lngDeg).append("度 ")
    sb.append(location.lngMin).append("分 ")
    sb.append(formatter.format(location.lngSec)).append("秒, ")

    sb.append(if (location.northSouth == Location.NorthSouth.NORTH) "北緯" else "南緯").append(" ")
    sb.append(location.latDeg).append("度 ")
    sb.append(location.latMin).append("分 ")

    sb.append(formatter.format(location.latSec)).append("秒.")

    return sb.toString()
  }
}

class LngLatDecoratorChina : Decorator<Location> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(location: Location): String {
    val sb = StringBuilder()
    sb.append(if (location.eastWest == Location.EastWest.EAST) "东经" else "西经").append(" ")
    sb.append(location.lngDeg).append("度")
    sb.append(location.lngMin).append("分")
    sb.append(formatter.format(location.lngSec)).append("秒, ")

    sb.append(if (location.northSouth == Location.NorthSouth.NORTH) "北纬" else "南纬").append(" ")
    sb.append(location.latDeg).append("度")
    sb.append(location.latMin).append("分")

    sb.append(formatter.format(location.latSec)).append("秒.")

    return sb.toString()
  }
}


class LngLatDecoratorEnglish : Decorator<Location> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(location: Location): String {

    val sb = StringBuilder()
    sb.append(if (location.eastWest == Location.EastWest.EAST) "East " else "West ")
    sb.append(location.lngDeg).append("° ")
    sb.append(location.lngMin).append("' ")
    sb.append(formatter.format(location.lngSec)).append("\" , ")

    sb.append(if (location.northSouth == Location.NorthSouth.NORTH) "North " else "South ")
    sb.append(location.latDeg).append("° ")
    sb.append(location.latMin).append("' ")
    sb.append(formatter.format(location.latSec)).append("\".")

    return sb.toString()
  }
}
