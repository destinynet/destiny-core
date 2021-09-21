/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.liuren

import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranch.*
import destiny.core.chinese.liuren.General.*
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Pithy.KEY_GENERAL_STEM_BRANCH
import java.io.Serializable
import java.util.*

/**
 * 大六壬：壬子天后 癸亥玄武
 */
@Impl([Domain(KEY_GENERAL_STEM_BRANCH, GeneralStemBranchLiuren.VALUE)])
class GeneralStemBranchLiuren : IGeneralStemBranch, Serializable {

  private val map = mapOf(
    貴人 to 己丑,
    螣蛇 to 丁巳,
    朱雀 to 丙午,
    六合 to 乙卯,
    勾陳 to 戊辰,
    青龍 to 甲寅,
    天空 to 戊戌,
    白虎 to 庚申,
    太常 to 己未,
    玄武 to 癸亥,
    太陰 to 辛酉,
    天后 to 壬子
  )

  override fun getStemBranch(general: General): StemBranch {
    return map.getValue(general)
  }

  /**
   * 從地支，找尋「天將」
   * 因為 map value 為「干支」，因此需要 filter
   */
  override fun get(branch: Branch): General {
    return map.filter { it.value.branch == branch }
      .keys.first()
  }

  override fun toString(locale: Locale): String {
    return GeneralStemBranch.Liuren.asDescriptive().toString(locale)
  }

  override fun getDescription(locale: Locale): String {
    return GeneralStemBranch.Liuren.asDescriptive().getDescription(locale)
  }

  companion object {
    const val VALUE = "liuren"
  }
}
