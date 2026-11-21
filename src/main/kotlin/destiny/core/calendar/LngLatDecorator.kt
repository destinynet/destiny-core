/**
 * Created by smallufo on 2017-05-05.
 */
package destiny.core.calendar

import destiny.tools.Lang
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.text.DecimalFormat

/**
 * 純粹只有經緯度，以及時區
 */
object LngLatDecorator : LocalizedDecorator<ILocation> {

  private val implMap = mapOf<Lang, Decorator<ILocation>>(
    Lang.ZH_TW to LngLatDecoratorTaiwan(),
    Lang.ZH_CN to LngLatDecoratorChina(),
    Lang.EN to LngLatDecoratorEnglish()
  )

  override fun getOutputString(location: ILocation, lang: Lang): String {
    return implMap.getOutputString(location, lang)
  }
}


class LngLatDecoratorTaiwan : Decorator<ILocation> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: ILocation): String {
    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.lng, Lang.ZH_TW))
      append(", ")
      append(LatDecorator.getOutputString(value.lat, Lang.ZH_TW))
    }.toString()
  }
}

class LngLatDecoratorChina : Decorator<ILocation> {

  internal var formatter = DecimalFormat("00.00")

  override fun getOutputString(value: ILocation): String {
    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.lng, Lang.ZH_CN))
      append(", ")
      append(LatDecorator.getOutputString(value.lat, Lang.ZH_CN))
    }.toString()
  }
}


class LngLatDecoratorEnglish : Decorator<ILocation> {

  override fun getOutputString(value: ILocation): String {

    return with(StringBuilder()) {
      append(LngDecorator.getOutputString(value.lng, Lang.EN))
      append(", ")
      append(LatDecorator.getOutputString(value.lat, Lang.EN))
    }.toString()
  }
}
