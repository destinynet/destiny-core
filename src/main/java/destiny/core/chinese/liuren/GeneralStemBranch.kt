/**
 * Created by smallufo on 2021-09-22.
 */
package destiny.core.chinese.liuren

import destiny.core.Descriptive
import java.util.*


/**
 * 「天將」的所屬干支設定
 */
enum class GeneralStemBranch {
  /** 大六壬：壬子天后 癸亥玄武 */
  Liuren,

  /** 金口訣：壬子玄武 癸亥天后 */
  Pithy
}

fun GeneralStemBranch.asDescriptive() = object : Descriptive {
  override fun toString(locale: Locale): String {
    return when (this@asDescriptive) {
      GeneralStemBranch.Liuren -> "《大六壬》"
      GeneralStemBranch.Pithy  -> "《金口訣》"
    }
  }

  override fun getDescription(locale: Locale): String {
    return when (this@asDescriptive) {
      GeneralStemBranch.Liuren -> "壬子天后 癸亥玄武。"
      GeneralStemBranch.Pithy  -> "壬子玄武 癸亥天后。"
    }
  }
}

