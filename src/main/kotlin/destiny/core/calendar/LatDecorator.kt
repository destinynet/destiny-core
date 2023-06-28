/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.core.News
import destiny.core.News.NorthSouth.NORTH
import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.util.*
import kotlin.math.absoluteValue

object LatDecorator {
  private val implMap = mapOf(Locale.TAIWAN to LatDecoratorTaiwan(),
                              Locale.SIMPLIFIED_CHINESE to LatDecoratorChina(),
                              Locale.ENGLISH to LatDecoratorEnglish())

  fun getOutputString(value: Double, locale: Locale): String {
    return implMap.getOutputString(value, locale)
  }
}

data class Lat(val northSouth: News.NorthSouth, val deg: Int, val min: Int, val sec: Double) {
  companion object {
    fun of(value: Double): Lat {
      val northSouth = if (value >= 0) NORTH else News.NorthSouth.SOUTH

      val deg = value.absoluteValue.toInt()
      val min = ((value.absoluteValue - deg) * 60).toInt()
      val sec = value.absoluteValue * 3600 - deg * 3600 - min * 60
      return Lat(northSouth, deg, min, sec)
    }
  }
}


private val zh = {sb : StringBuilder , lat : Lat ->
  sb.append("：")
  sb.append("%02d".format(lat.deg)).append("度")
  sb.append("%02d".format(lat.min)).append("分")

  val secString = "%02.2f".format(lat.sec).let {
    // 可能是 "0.00"
    if (it.length == 4) "0$it"
    else it
  }
  sb.append(secString)
  sb.append("秒")
  sb
}

class LatDecoratorTaiwan : Decorator<Double> {
  /**
   * 北緯：01度00分00.00秒
   * 共 21 chars width
   */
  override fun getOutputString(value: Double): String {
    val lat = Lat.of(value)

    return with(StringBuilder()) {
      append(if (lat.northSouth == NORTH) "北緯" else "南緯")
      zh.invoke(this , lat)
    }.toString()
  }
}

class LatDecoratorChina : Decorator<Double> {
  /**
   * 北纬：01度00分00.00秒
   * 共 21 chars width
   */
  override fun getOutputString(value: Double): String {
    val lat = Lat.of(value)

    return with(StringBuilder()) {
      append(if (lat.northSouth == NORTH) "北纬" else "南纬")
      zh.invoke(this , lat)
    }.toString()
  }
}

class LatDecoratorEnglish : Decorator<Double> {

  /**
   * ISO 6709 國際標準
   * 50°40′46,461″N
   * 50°03′46.461″S
   * 注意，「分」這裡，有 left padding '0'
   * 共 14 bytes
   */
  override fun getOutputString(value: Double): String {
    val lat = Lat.of(value)
    return with(StringBuilder()) {
      append("%02d".format(lat.deg)).append("°")
      append("%02d".format(lat.min)).append("'")
      val secString = "%02.3f".format(lat.sec).let {
        // 可能是 "0.000"
        if (it.length == 5) "0$it"
        else it
      }
      append(secString).append("\"")
      append(if (lat.northSouth == NORTH) "N" else "S")
    }.toString()
  }

}
