/**
 * @author smallufo
 * @date 2002/8/19
 * @time 下午 10:40:38
 */
package destiny.core.iching.divine

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.core.iching.Symbol
import destiny.core.iching.Symbol.*

class SettingsGingFang : AbstractSettings(SettingsOfStemBranch.GingFang) {

  override val symbolStemMap: Map<Symbol, List<Stem>>
    get() = mapOf(
      乾 to listOf(甲, 甲, 甲, 壬, 壬, 壬),
      兌 to listOf(丁, 丁, 丁, 丁, 丁, 丁),
      離 to listOf(己, 己, 己, 己, 己, 己),
      震 to listOf(庚, 庚, 庚, 庚, 庚, 庚),
      巽 to listOf(辛, 辛, 辛, 辛, 辛, 辛),
      坎 to listOf(戊, 戊, 戊, 戊, 戊, 戊),
      艮 to listOf(丙, 丙, 丙, 丙, 丙, 丙),
      坤 to listOf(乙, 乙, 乙, 癸, 癸, 癸))

  override val symbolBranchMap: Map<Symbol, List<Branch>>
    get() = mapOf(
      乾 to listOf(子, 寅, 辰, 午, 申, 戌),
      兌 to listOf(巳, 卯, 丑, 亥, 酉, 未),
      離 to listOf(卯, 丑, 亥, 酉, 未, 巳),
      震 to listOf(子, 寅, 辰, 午, 申, 戌),
      巽 to listOf(丑, 亥, 酉, 未, 巳, 卯),
      坎 to listOf(寅, 辰, 午, 申, 戌, 子),
      艮 to listOf(辰, 午, 申, 戌, 子, 寅),
      坤 to listOf(未, 巳, 卯, 丑, 亥, 酉))

  companion object {
    const val VALUE = "gf"
  }

}
