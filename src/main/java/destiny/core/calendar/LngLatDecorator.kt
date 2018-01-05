/**
 * Created by smallufo on 2017-05-05.
 */
package destiny.core.calendar

import destiny.tools.AlignTools
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
    return implMap.getOutputString(location , locale)
  }
}


class LngLatDecoratorTaiwan : Decorator<Location> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: Location): String {
    val sb = StringBuilder()
    with(sb) {
      append(if (value.eastWest == Location.EastWest.EAST) "東經" else "西經")
      append(AlignTools.alignRight(value.lngDeg , 3)).append("度 ")
      append(AlignTools.alignRight(value.lngMin , 2)).append("分 ")
      append(formatter.format(value.lngSec)).append("秒, ")

      append(if (value.northSouth == Location.NorthSouth.NORTH) "北緯" else "南緯")
      append(AlignTools.alignRight(value.latDeg , 3)).append("度 ")
      append(AlignTools.alignRight(value.latMin , 2)).append("分 ")
      append(formatter.format(value.latSec)).append("秒")
    }
    return sb.toString()
  }
}

class LngLatDecoratorChina : Decorator<Location> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: Location): String {
    val sb = StringBuilder()
    sb.append(if (value.eastWest == Location.EastWest.EAST) "东经" else "西经").append(" ")
    sb.append(value.lngDeg).append("度 ")
    sb.append(value.lngMin).append("分 ")
    sb.append(formatter.format(value.lngSec)).append("秒, ")

    sb.append(if (value.northSouth == Location.NorthSouth.NORTH) "北纬" else "南纬").append(" ")
    sb.append(value.latDeg).append("度 ")
    sb.append(value.latMin).append("分 ")

    sb.append(formatter.format(value.latSec)).append("秒")

    return sb.toString()
  }
}


class LngLatDecoratorEnglish : Decorator<Location> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: Location): String {

    val sb = StringBuilder()
    sb.append(value.lngDeg).append("° ")
    sb.append(value.lngMin).append("' ")
    sb.append(formatter.format(value.lngSec)).append("\" ")
    sb.append(if (value.eastWest == Location.EastWest.EAST) "E" else "W")
    sb.append(" , ")


    sb.append(value.latDeg).append("° ")
    sb.append(value.latMin).append("' ")
    sb.append(formatter.format(value.latSec)).append("\" ")
    sb.append(if (value.northSouth == Location.NorthSouth.NORTH) "N" else "S")


    return sb.toString()
  }
}
