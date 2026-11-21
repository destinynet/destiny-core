/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 11:49:59
 */
package destiny.core.calendar.decorators

import destiny.tools.Lang
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.getOutputString
import destiny.tools.JSerializable

object HourDecorator : LocalizedDecorator<Int> {
  private val implMap = mapOf<Lang, Decorator<Int>>(
    Lang.ZH_TW to HourDecoratorTaiwan(),
    Lang.ZH_CN to HourDecoratorChina(),
    Lang.EN to HourDecoratorEnglish()
  )

  override fun getOutputString(value: Int, lang: Lang): String {
    return implMap.getOutputString(value, lang)
  }
}


class HourDecoratorTaiwan : Decorator<Int>, JSerializable {
  override fun getOutputString(value: Int): String {
    return value.toString() + "時"
  }
}


class HourDecoratorChina : Decorator<Int>, JSerializable {
  override fun getOutputString(value: Int): String {
    return value.toString() + "时"
  }
}

class HourDecoratorEnglish : Decorator<Int>, JSerializable {
  override fun getOutputString(value: Int): String {
    return value.toString()
  }

}
