/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.liuren.golden

import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import destiny.core.chinese.liuren.General
import destiny.core.chinese.liuren.General.*
import destiny.core.chinese.liuren.GeneralStemBranchIF
import java.io.Serializable
import java.util.*

/**
 * 金口訣：壬子玄武 癸亥天后
 */
class GeneralStemBranchPithy : GeneralStemBranchIF, Serializable {

  private val map = mapOf(
    貴人 to StemBranch.己丑,
    螣蛇 to StemBranch.丁巳,
    朱雀 to StemBranch.丙午,
    六合 to StemBranch.乙卯,
    勾陳 to StemBranch.戊辰,
    青龍 to StemBranch.甲寅,
    天空 to StemBranch.戊戌,
    白虎 to StemBranch.庚申,
    太常 to StemBranch.己未,
    玄武 to StemBranch.壬子,
    太陰 to StemBranch.辛酉,
    天后 to StemBranch.癸亥
  )

  override fun getStemBranch(general: General): StemBranch {
    return map[general]!!
  }

  /**
   * 從地支，找尋「天將」
   * 因為 map value 為「干支」，因此需要 filter
   */
  override fun get(branch: Branch): General {
    return map.filter { it.value.branch == branch }
      .keys.first()
  }

  override fun getTitle(locale: Locale): String {
    return "《金口訣》"
  }

  override fun getDescription(locale: Locale): String {
    return "壬子玄武 癸亥天后"
  }

  companion object
}
