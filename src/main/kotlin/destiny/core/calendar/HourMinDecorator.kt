/**
 * Created by smallufo on 2022-03-10.
 */
package destiny.core.calendar

import destiny.tools.Lang
import destiny.tools.Decorator
import destiny.tools.JSerializable
import destiny.tools.LocalizedDecorator
import destiny.tools.getOutputString

object HourMinDecorator : LocalizedDecorator<Pair<Int, Int>> {
  private val implMap = mapOf<Lang, Decorator<Pair<Int, Int>>>(
    Lang.ZH_TW to HourMinDecoratorTradChinese,
    Lang.ZH_CN to HourMinDecoratorSimplifiedChinese,
    Lang.EN to HourMinDecoratorEnglish
  )

  override fun getOutputString(value: Pair<Int, Int>, lang: Lang): String {
    return implMap.getOutputString(value, lang)
  }
}

object HourMinDecoratorTradChinese : Decorator<Pair<Int, Int>>, JSerializable {
  private fun readResolve(): Any = HourMinDecoratorTradChinese
  override fun getOutputString(value: Pair<Int, Int>): String {
    return buildString {
      append(if (value.first < 10) "0" else "").append(value.first).append("時")
      append(if (value.second < 10) "0" else "").append(value.second).append("分")
    }
  }
}

object HourMinDecoratorSimplifiedChinese : Decorator<Pair<Int, Int>>, JSerializable {
  private fun readResolve(): Any = HourMinDecoratorSimplifiedChinese
  override fun getOutputString(value: Pair<Int, Int>): String {
    return buildString {
      append(if (value.first < 10) "0" else "").append(value.first).append("时")
      append(if (value.second < 10) "0" else "").append(value.second).append("分")
    }
  }
}

object HourMinDecoratorEnglish : Decorator<Pair<Int, Int>>, JSerializable {
  private fun readResolve(): Any = HourMinDecoratorEnglish
  override fun getOutputString(value: Pair<Int, Int>): String {
    return buildString {
      append(if (value.first < 10) "0" else "").append(value.first)
      append(":")
      append(if (value.second < 10) "0" else "").append(value.second)
    }
  }
}
