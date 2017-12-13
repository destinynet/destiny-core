/**
 * @author smallufo
 * Created on 2008/4/2 at 上午 1:26:30
 */
package destiny.core

import destiny.tools.Decorator
import destiny.tools.getOutputString
import java.util.*

object GenderDecorator {
  private val implMap = mapOf(
    Locale.TAIWAN to GenderDecoratorChinese(),
    Locale.ENGLISH to GenderDecoratorEnglish()
  )

  fun getOutputString(gender: Gender, locale: Locale): String {
    return implMap.getOutputString(gender , locale)
  }
}


class GenderDecoratorChinese : Decorator<Gender> {
  override fun getOutputString(value: Gender): String {
    return if (value === Gender.男) "男" else "女"
  }
}

class GenderDecoratorEnglish : Decorator<Gender> {
  override fun getOutputString(value: Gender): String {
    return if (value === Gender.男) "Male" else "Female"
  }
}
