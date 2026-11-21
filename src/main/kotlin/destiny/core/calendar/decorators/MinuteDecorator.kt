/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 11:56:28
 */
package destiny.core.calendar.decorators

import destiny.tools.Lang
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.getOutputString
import destiny.tools.JSerializable

object MinuteDecorator : LocalizedDecorator<Int> {
  private val implMap = mapOf<Lang, Decorator<Int>>(
    Lang.ZH to MinuteDecoratorChinese(),
    Lang.EN to MinuteDecoratorEnglish()
  )

  override fun getOutputString(value: Int, lang: Lang): String {
    return implMap.getOutputString(value , lang)
  }
}

class MinuteDecoratorChinese : Decorator<Int>, JSerializable {
  override fun getOutputString(value: Int): String {
    return value.toString() + "分"
  }
}


class MinuteDecoratorEnglish : Decorator<Int>, JSerializable {
  override fun getOutputString(value: Int): String {
    return value.toString()
  }
}
