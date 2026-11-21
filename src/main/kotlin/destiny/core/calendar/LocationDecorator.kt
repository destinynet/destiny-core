/**
 * @author smallufo
 * Created on 2008/1/17 at 上午 1:35:01
 */
package destiny.core.calendar

import destiny.tools.Lang
import destiny.tools.LangTools
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.LocaleTools
import java.time.Instant

object LocationDecorator : LocalizedDecorator<ILocation> {
  private val implMap = mapOf<Lang, Decorator<ILocation>>(
    Lang.ZH_TW to LocationDecoratorTaiwan(),
    Lang.ZH_CN to LocationDecoratorChina(),
    Lang.EN to LocationDecoratorEnglish()
  )

  override fun getOutputString(location: ILocation, lang: Lang): String {

    val bestMatchingLocale = LangTools.getBestMatchingLangOrFirst(lang, implMap.keys)
    return implMap.getValue(bestMatchingLocale).getOutputString(location)
  }

}


class LocationDecoratorTaiwan : Decorator<ILocation> {

  override fun getOutputString(value: ILocation): String {

    val sb = StringBuilder()
    sb.append(LngLatDecorator.getOutputString(value, Lang.ZH_TW))

    value.altitudeMeter?.also {
      sb.append("高度 ").append(it).append(" 公尺.")
    }
    sb.append(" 時區 ").append(value.zoneId.id)
    if (value.hasMinuteOffset)
      sb.append(" 時差 ").append(value.finalMinuteOffset).append(" 分鐘.")

    return sb.toString()
  }

}


class LocationDecoratorChina : Decorator<ILocation> {

  override fun getOutputString(value: ILocation): String {
    val sb = StringBuilder()
    sb.append(LngLatDecorator.getOutputString(value, Lang.ZH_CN))

    value.altitudeMeter?.also {
      sb.append("高度 ").append(it).append(" 米")
    }
    sb.append(" 时区 ").append(value.zoneId.id)
    if (value.hasMinuteOffset)
      sb.append(" 时差 ").append(value.finalMinuteOffset).append(" 分钟.")

    return sb.toString()
  }

}


class LocationDecoratorEnglish : Decorator<ILocation> {

  override fun getOutputString(value: ILocation): String {
    val sb = StringBuilder()
    sb.append(LngLatDecorator.getOutputString(value, Lang.EN))


    sb.append(" GMT offset ")
      .append(value.zoneId.rules.getStandardOffset(Instant.now()).totalSeconds/ (60 * 60))
      .append(" hours")
    value.altitudeMeter?.also {
      sb.append(", Alt ").append(it).append(" m.")
    }
    return sb.toString()
  }

}
