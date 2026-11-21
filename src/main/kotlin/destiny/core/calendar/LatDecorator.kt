/**
 * Created by smallufo on 2018-01-08.
 */
package destiny.core.calendar

import destiny.tools.formatInvariant
import destiny.tools.Lang
import destiny.tools.LocalizedDecorator
import destiny.core.News.NorthSouth.NORTH
import destiny.tools.Decorator
import destiny.tools.getOutputString

object LatDecorator : LocalizedDecorator<Lat> {
  private val implMap = mapOf(Lang.ZH_TW to LatDecoratorTaiwan(),
                              Lang.ZH_CN to LatDecoratorChina(),
                              Lang.EN to LatDecoratorEnglish())

  override fun getOutputString(value: Lat, lang: Lang): String {
    return implMap.getOutputString(value, lang)
  }
}

private val zh = {sb : StringBuilder , lat : Lat ->
  sb.append("：")
  sb.append("%02d".formatInvariant(lat.deg)).append("度")
  sb.append("%02d".formatInvariant(lat.min)).append("分")

  val secString = "%02.2f".formatInvariant(lat.sec).let {
    // 可能是 "0.00"
    if (it.length == 4) "0$it"
    else it
  }
  sb.append(secString)
  sb.append("秒")
  sb
}

class LatDecoratorTaiwan : Decorator<Lat> {
  /**
   * 北緯：01度00分00.00秒
   * 共 21 chars width
   */
  override fun getOutputString(value: Lat): String {

    return with(StringBuilder()) {
      append(if (value.northSouth == NORTH) "北緯" else "南緯")
      zh.invoke(this , value)
    }.toString()
  }
}

class LatDecoratorChina : Decorator<Lat> {
  /**
   * 北纬：01度00分00.00秒
   * 共 21 chars width
   */
  override fun getOutputString(value: Lat): String {

    return with(StringBuilder()) {
      append(if (value.northSouth == NORTH) "北纬" else "南纬")
      zh.invoke(this , value)
    }.toString()
  }
}

class LatDecoratorEnglish : Decorator<Lat> {

  /**
   * ISO 6709 國際標準
   * 50°40′46,461″N
   * 50°03′46.461″S
   * 注意，「分」這裡，有 left padding '0'
   * 共 14 bytes
   */
  override fun getOutputString(value: Lat): String {
    return with(StringBuilder()) {
      append("%02d".formatInvariant(value.deg)).append("°")
      append("%02d".formatInvariant(value.min)).append("'")
      val secString = "%02.3f".formatInvariant(value.sec).let {
        // 可能是 "0.000"
        if (it.length == 5) "0$it"
        else it
      }
      append(secString).append("\"")
      append(if (value.northSouth == NORTH) "N" else "S")
    }.toString()
  }

}
