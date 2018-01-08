/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.util.*
import kotlin.math.absoluteValue

object LngDecorator {
  private val implMap = mapOf(Locale.TAIWAN to LngDecoratorTaiwan(),
                              Locale.SIMPLIFIED_CHINESE to LngDecoratorChina(),
                              Locale.ENGLISH to LngDecoratorEnglish())

  fun getOutputString(value: Double, locale: Locale): String {
    return implMap.getOutputString(value, locale)
  }
}

data class Lng(val eastWest: Location.EastWest, val deg: Int, val min: Int, val sec: Double) {
  companion object {
    fun of(value: Double): Lng {
      val eastWest = if (value >= 0) Location.EastWest.EAST else Location.EastWest.WEST

      val deg = value.absoluteValue.toInt()
      val min = ((value.absoluteValue - deg) * 60).toInt()
      val sec = value.absoluteValue * 3600 - deg * 3600 - min * 60
      return Lng(eastWest, deg, min, sec)
    }
  }
}


class LngDecoratorTaiwan : Decorator<Double> {
  /**
   * 東經：121度30分00.00秒
   * 總共 22 char 寬度
   */
  override fun getOutputString(value: Double): String {
    val lng = Lng.of(value)

    return with(StringBuilder()) {
      append(if (lng.eastWest == Location.EastWest.EAST) "東經" else "西經")
      append("：")
      append("%03d".format(lng.deg)).append("度")
      append("%02d".format(lng.min)).append("分")

      val secString = "%02.2f".format(lng.sec).let {
        // 可能是 "0.00"
        if (it.length == 4) "0" + it
        else it
      }
      append(secString)
      append("秒")
    }.toString()
  }
}


class LngDecoratorChina : Decorator<Double> {
  /**
   * 东经：121度30分00.00秒
   * 總共 22 char 寬度
   */
  override fun getOutputString(value: Double): String {
    val lng = Lng.of(value)

    return with(StringBuilder()) {
      append(if (lng.eastWest == Location.EastWest.EAST) "东经" else "西经")
      append("：")
      append("%03d".format(lng.deg)).append("度")
      append("%02d".format(lng.min)).append("分")

      val secString = "%02.2f".format(lng.sec).let {
        // 可能是 "0.00"
        if (it.length == 4) "0" + it
        else it
      }
      append(secString)
      append("秒")
    }.toString()
  }
}

class LngDecoratorEnglish : Decorator<Double> {

  /**
   * ISO 6709 國際標準
   * 50°40′46,461″N
   * 50°03′46.461″S
   * 注意，「分」這裡，有 left padding '0'
   */
  override fun getOutputString(value: Double): String {
    val lng = Lng.of(value)

    return with(StringBuilder()) {
      append(lng.deg).append("°")
      append("%02d".format(lng.min)).append("'")
      val secString = "%02.2f".format(lng.sec).let {
        // 可能是 "0.00"
        if (it.length == 4) "0" + it
        else it
      }
      append(secString).append("\"")
      append(if (lng.eastWest == Location.EastWest.EAST) "E" else "W")
    }.toString()
  }

}