/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.tools.formatInvariant
import destiny.tools.Lang
import destiny.tools.LocalizedDecorator
import destiny.core.News
import destiny.tools.Decorator
import destiny.tools.getOutputString

object LngDecorator : LocalizedDecorator<Lng> {
  private val implMap = mapOf(Lang.ZH_TW to LngDecoratorTaiwan(),
                              Lang.ZH_CN to LngDecoratorChina(),
                              Lang.EN to LngDecoratorEnglish())

  override fun getOutputString(value: Lng, lang: Lang): String {
    return implMap.getOutputString(value, lang)
  }
}

class LngDecoratorTaiwan : Decorator<Lng> {
  /**
   * 東經：121度30分00.00秒
   * 總共 22 char 寬度
   */
  override fun getOutputString(value: Lng): String {
    return with(StringBuilder()) {
      append(if (value.eastWest == News.EastWest.EAST) "東經" else "西經")
      zh.invoke(this , value)
    }.toString()
  }
}

private val zh = {sb : StringBuilder , lng : Lng ->
  sb.append("：")
  sb.append("%03d".formatInvariant(lng.deg)).append("度")
  sb.append("%02d".formatInvariant(lng.min)).append("分")

  val secString = "%02.2f".formatInvariant(lng.sec).let {
    // 可能是 "0.00"
    if (it.length == 4) "0$it"
    else it
  }
  sb.append(secString)
  sb.append("秒")
  sb
}

class LngDecoratorChina : Decorator<Lng> {
  /**
   * 东经：121度30分00.00秒
   * 總共 22 char 寬度
   */
  override fun getOutputString(value: Lng): String {

    return with(StringBuilder()) {
      append(if (value.eastWest == News.EastWest.EAST) "东经" else "西经")
      zh.invoke(this , value)
    }.toString()
  }
}

class LngDecoratorEnglish : Decorator<Lng> {

  /**
   * ISO 6709 國際標準
   * 50°40′46,461″N
   * 50°03′46.461″S
   * 注意，「分」這裡，有 left padding '0'
   */
  override fun getOutputString(value: Lng): String {

    return with(StringBuilder()) {
      append(value.deg).append("°")
      append("%02d".formatInvariant(value.min)).append("'")
      val secString = "%02.2f".formatInvariant(value.sec).let {
        // 可能是 "0.00"
        if (it.length == 4) "0$it"
        else it
      }
      append(secString).append("\"")
      append(if (value.eastWest == News.EastWest.EAST) "E" else "W")
    }.toString()
  }

}
