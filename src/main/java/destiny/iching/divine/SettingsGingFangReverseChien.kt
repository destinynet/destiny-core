/**
 * @author smallufo
 * @date 2002/8/20
 * @time 下午 02:42:09
 */
package destiny.iching.divine

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.iching.Symbol
import destiny.iching.Symbol.*
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Divine.KEY_DIVINE_HEXSETTINGS
import java.util.*

@Impl([Domain(KEY_DIVINE_HEXSETTINGS, SettingsGingFangReverseChien.VALUE)])
class SettingsGingFangReverseChien : AbstractSettings() {

  override val symbolStemMap: Map<Symbol, List<Stem>>
    get() = mapOf(
      乾 to listOf(壬, 壬, 壬, 甲, 甲, 甲),
      兌 to listOf(丁, 丁, 丁, 丁, 丁, 丁),
      離 to listOf(己, 己, 己, 己, 己, 己),
      震 to listOf(庚, 庚, 庚, 庚, 庚, 庚),
      巽 to listOf(辛, 辛, 辛, 辛, 辛, 辛),
      坎 to listOf(戊, 戊, 戊, 戊, 戊, 戊),
      艮 to listOf(丙, 丙, 丙, 丙, 丙, 丙),
      坤 to listOf(乙, 乙, 乙, 癸, 癸, 癸))

  override val symbolBranchMap: Map<Symbol, List<Branch>>
    get() = mapOf(
      乾 to listOf(午, 申, 戌, 子, 寅, 辰),
      兌 to listOf(巳, 卯, 丑, 亥, 酉, 未),
      離 to listOf(卯, 丑, 亥, 酉, 未, 巳),
      震 to listOf(子, 寅, 辰, 午, 申, 戌),
      巽 to listOf(丑, 亥, 酉, 未, 巳, 卯),
      坎 to listOf(寅, 辰, 午, 申, 戌, 子),
      艮 to listOf(辰, 午, 申, 戌, 子, 寅),
      坤 to listOf(未, 巳, 卯, 丑, 亥, 酉))

  override fun toString(locale: Locale): String {
    return "京房易之乾卦自午起"
  }

  override fun getDescription(locale: Locale): String {
    return "京房易納甲之另派說法，乾卦納支自午起：午申戌子寅辰"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    const val VALUE = "gfr"
  }

}
