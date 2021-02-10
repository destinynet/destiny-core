/**
 * @author smallufo
 * @date 2002/8/20
 * @time 下午 02:52:11
 */
package destiny.core.iching.divine

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.core.iching.Symbol
import destiny.core.iching.Symbol.*
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Divine.KEY_DIVINE_HEXSETTINGS
import java.util.*


@Impl([Domain(KEY_DIVINE_HEXSETTINGS, SettingsTsangShan.VALUE)])
class SettingsTsangShan : AbstractSettings() {

  override val symbolStemMap: Map<Symbol, List<Stem>>
    get() = mapOf(
      乾 to listOf(庚, 壬, 甲, 戊, 丙, 庚),
      兌 to listOf(丁, 乙, 癸, 辛, 己, 丁),
      離 to listOf(辛, 己, 丁, 乙, 癸, 辛),
      震 to listOf(乙, 丁, 己, 辛, 癸, 乙),
      巽 to listOf(癸, 辛, 己, 丁, 乙, 癸),
      坎 to listOf(丙, 庚, 壬, 甲, 戊, 丙),
      艮 to listOf(戊, 丙, 庚, 壬, 甲, 戊),
      坤 to listOf(己, 丁, 乙, 癸, 辛, 己))

  override val symbolBranchMap: Map<Symbol, List<Branch>>
    get() = mapOf(
      乾 to listOf(戌, 申, 午, 辰, 寅, 子),
      兌 to listOf(酉, 亥, 丑, 卯, 巳, 未),
      離 to listOf(未, 酉, 亥, 丑, 卯, 巳),
      震 to listOf(卯, 丑, 亥, 酉, 未, 巳),
      巽 to listOf(巳, 未, 酉, 亥, 丑, 卯),
      坎 to listOf(子, 戌, 申, 午, 辰, 寅),
      艮 to listOf(寅, 子, 戌, 申, 午, 辰),
      坤 to listOf(未, 酉, 亥, 丑, 卯, 巳))

  override fun toString(locale: Locale): String {
    return "藏山雷學之藏山卜"
  }

  override fun getDescription(locale: Locale): String {
    return "藏山雷學是大陸易學家 石銘 及 孫則鳴 等人所提出來的納甲系統，有著另外一套嚴謹的學理在背後支持。詳情可參照《藏山雷學》一書"
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
    const val VALUE = "ts"
  }

}
