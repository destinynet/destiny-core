/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.core.News
import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.util.*

object LngDecorator {
  private val implMap = mapOf(Locale.TAIWAN to LngDecoratorTaiwan(),
                              Locale.SIMPLIFIED_CHINESE to LngDecoratorChina(),
                              Locale.ENGLISH to LngDecoratorEnglish())

  fun getOutputString(value: LngValue, locale: Locale): String {
    return implMap.getOutputString(value, locale)
  }
}

class LngDecoratorTaiwan : Decorator<LngValue> {
  /**
   * 東經：121度30分00.00秒
   * 總共 22 char 寬度
   */
  override fun getOutputString(value: LngValue): String {
    return with(StringBuilder()) {
      append(if (value.eastWest == News.EastWest.EAST) "東經" else "西經")
      zh.invoke(this , value)
    }.toString()
  }
}

private val zh = {sb : StringBuilder , lng : LngValue ->
  sb.append("：")
  sb.append("%03d".format(lng.deg)).append("度")
  sb.append("%02d".format(lng.min)).append("分")

  val secString = "%02.2f".format(lng.sec).let {
    // 可能是 "0.00"
    if (it.length == 4) "0$it"
    else it
  }
  sb.append(secString)
  sb.append("秒")
  sb
}

class LngDecoratorChina : Decorator<LngValue> {
  /**
   * 东经：121度30分00.00秒
   * 總共 22 char 寬度
   */
  override fun getOutputString(value: LngValue): String {

    return with(StringBuilder()) {
      append(if (value.eastWest == News.EastWest.EAST) "东经" else "西经")
      zh.invoke(this , value)
    }.toString()
  }
}

class LngDecoratorEnglish : Decorator<LngValue> {

  /**
   * ISO 6709 國際標準
   * 50°40′46,461″N
   * 50°03′46.461″S
   * 注意，「分」這裡，有 left padding '0'
   */
  override fun getOutputString(value: LngValue): String {

    return with(StringBuilder()) {
      append(value.deg).append("°")
      append("%02d".format(value.min)).append("'")
      val secString = "%02.2f".format(value.sec).let {
        // 可能是 "0.00"
        if (it.length == 4) "0$it"
        else it
      }
      append(secString).append("\"")
      append(if (value.eastWest == News.EastWest.EAST) "E" else "W")
    }.toString()
  }

}
