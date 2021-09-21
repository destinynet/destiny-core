package destiny.core.chinese.liuren

import destiny.core.Descriptive
import java.util.*

enum class GeneralSeq {
  Default,
  Zhao
}

fun GeneralSeq.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return when (this@asDescriptive) {
      GeneralSeq.Default -> "內定順序"
      GeneralSeq.Zhao    -> "趙氏六壬"
    }
  }

  override fun getDescription(locale: Locale): String {
    return when (this@asDescriptive) {
      GeneralSeq.Default -> "貴蛇朱合勾青空，白常玄陰后。"
      GeneralSeq.Zhao    -> "貴青合勾 蛇朱常白 陰空玄后。"
    }
  }
}
