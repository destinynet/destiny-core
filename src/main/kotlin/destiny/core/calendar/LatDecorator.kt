/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.core.News.NorthSouth.NORTH
import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.util.*

object LatDecorator {
  private val implMap = mapOf(Locale.TAIWAN to LatDecoratorTaiwan(),
                              Locale.SIMPLIFIED_CHINESE to LatDecoratorChina(),
                              Locale.ENGLISH to LatDecoratorEnglish())

  fun getOutputString(value: LatValue, locale: Locale): String {
    return implMap.getOutputString(value, locale)
  }
}

private val zh = {sb : StringBuilder , lat : LatValue ->
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

class LatDecoratorTaiwan : Decorator<LatValue> {
  /**
   * 北緯：01度00分00.00秒
   * 共 21 chars width
   */
  override fun getOutputString(value: LatValue): String {

    return with(StringBuilder()) {
      append(if (value.northSouth == NORTH) "北緯" else "南緯")
      zh.invoke(this , value)
    }.toString()
  }
}

class LatDecoratorChina : Decorator<LatValue> {
  /**
   * 北纬：01度00分00.00秒
   * 共 21 chars width
   */
  override fun getOutputString(value: LatValue): String {

    return with(StringBuilder()) {
      append(if (value.northSouth == NORTH) "北纬" else "南纬")
      zh.invoke(this , value)
    }.toString()
  }
}

class LatDecoratorEnglish : Decorator<LatValue> {

  /**
   * ISO 6709 國際標準
   * 50°40′46,461″N
   * 50°03′46.461″S
   * 注意，「分」這裡，有 left padding '0'
   * 共 14 bytes
   */
  override fun getOutputString(value: LatValue): String {
    return with(StringBuilder()) {
      append("%02d".format(value.deg)).append("°")
      append("%02d".format(value.min)).append("'")
      val secString = "%02.3f".format(value.sec).let {
        // 可能是 "0.000"
        if (it.length == 5) "0$it"
        else it
      }
      append(secString).append("\"")
      append(if (value.northSouth == NORTH) "N" else "S")
    }.toString()
  }

}
