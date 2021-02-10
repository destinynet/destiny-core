/**
 * @author smallufo
 * @date 2002/8/21
 * @time 下午 11:13:57
 */
package destiny.core.iching.divine

import destiny.core.chinese.StemBranch
import destiny.core.iching.Hexagram
import destiny.core.iching.IHexagram
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains.Divine.KEY_DIVINE_HIDDEN_ENERGY
import java.io.Serializable
import java.util.*

/**
 * 伏神系統，京房之《京房易卦》 , 一定不會傳回 null
 */
@Impl([Domain(KEY_DIVINE_HIDDEN_ENERGY, HiddenEnergyGingFangImpl.VALUE)])
class HiddenEnergyGingFangImpl : IHiddenEnergy, Serializable {

  override fun toString(locale: Locale): String {
    return NAME
  }

  override fun getDescription(locale: Locale): String {
    return NAME
  }

  override fun getStemBranch(hexagram: IHexagram, settings: ISettingsOfStemBranch, lineIndex: Int): StemBranch? {
    val comparator = HexagramDivinationComparator()

    /* 1 <= 卦序 <= 64 */
    // 京房易卦卦序
    val hexagramIndex = comparator.getIndex(hexagram)

    /* 0乾 , 1兌 , 2離 , 3震 , 4巽 , 5坎 , 6艮 , 7坤 */
    val 宮位 = (hexagramIndex - 1) / 8

    /* 1:本宮卦             (乾為天)
     * 2:初爻變    ，一世卦 (天風姤)
     * 3:二爻變    ，二世卦 (天山遯)
     * 4:三爻變    ，三世卦 (天地否)
     * 5:四爻變    ，四世卦 (風地觀)
     * 6:五爻變    ，五世卦 (山地剝)
     * 7:四爻再變  ，遊魂卦 (火地晉)
     * 0:下三爻再變，歸魂卦 (火天大有)
     */
    val 宮序 = hexagramIndex - 宮位 * 8
    //System.out.println("宮序:" + 宮序);

    val 首宮卦 = Hexagram.of(宮位 * 8 + 1, comparator)

    val sequence = HexagramDivinationComparator()

    val 對宮首卦 = Hexagram.of((7 - 宮位) * 8 + 1, sequence)
    //System.out.println("首宮卦:" + 首宮卦 + " , 對宮首卦為:" + 對宮首卦);

    when {
      hexagram == 首宮卦 ->
        /**
         * 如果是八純卦，則往(先天八卦的)對宮尋找伏神
         * 乾 <-> 坤
         * 兌 <-> 艮
         * 離 <-> 坎
         * 震 <-> 巽
         */

        return settings.getStemBranch(對宮首卦, lineIndex)
      (2..4).contains(宮序) ->
        /**
         * 如果是初爻變 , 二爻變 , 或是三爻變
         */
        return if ((1..3).contains(lineIndex)) {
          settings.getStemBranch(首宮卦, lineIndex)
        } else {
          settings.getStemBranch(對宮首卦, lineIndex)
        }
      (5..7).contains(宮序) ->
        /**
         * 如果四爻變 , 五爻變 , 四爻又變(遊魂卦)
         */
        return settings.getStemBranch(首宮卦, lineIndex)
      宮序 == 8 ->
        /**
         * 如果下三爻再變 (歸魂卦)
         */
        return if ((1..3).contains(lineIndex)) {
          settings.getStemBranch(對宮首卦, lineIndex)
        } else {
          settings.getStemBranch(首宮卦, lineIndex)
        }
      else -> throw IllegalStateException("impossible")
    }

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
    const val VALUE = "gf"
    private const val NAME = "京房之《京房易卦》"
  }
}
