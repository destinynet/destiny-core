/**
 * @author smallufo
 * Created on 2008/4/9 at 上午 12:31:36
 */
package destiny.core.calendar.decorators

import destiny.tools.Lang
import destiny.tools.LocalizedDecorator
import destiny.tools.Decorator
import destiny.tools.getOutputString
import destiny.tools.JSerializable

object YearDecorator : LocalizedDecorator<Int> {
  private val implMap = mapOf<Lang, Decorator<Int>>(
    Lang.ZH_TW to YearDecoratorTaiwan(),
    Lang.ZH_CN to YearDecoratorChina(),
    Lang.EN to YearDecoratorEnglish(),
    Lang.JA_JP to YearDecoratorJapanese()
  )

  override fun getOutputString(value: Int, lang: Lang): String {
    return implMap.getOutputString(value, lang)
  }
}


class YearDecoratorTaiwan : Decorator<Int>, JSerializable {
  override fun getOutputString(value: Int): String {
    return if (value - 1911 >= 1)
      "西元" + value + "年 (民國" + (value - 1911) + "年)"
    else
    //民國前
      "西元" + value + "年 (民國前" + (1912 - value) + "年)"
  }
}


class YearDecoratorChina : Decorator<Int>, JSerializable {

  override fun getOutputString(value: Int): String {
    return "西元" + value.toString() + "年"
  }

}

class YearDecoratorJapanese : Decorator<Int>, JSerializable {
  override fun getOutputString(value: Int): String {
    return "西暦年" + value + "年"
  }
}


class YearDecoratorEnglish : Decorator<Int>, JSerializable {
  override fun getOutputString(value: Int): String {
    return value.toString()
  }

}
