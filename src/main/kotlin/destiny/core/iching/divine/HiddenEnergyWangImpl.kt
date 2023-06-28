/**
 * @author smallufo
 * @date 2002/8/20
 * @time 下午 04:20:44
 */
package destiny.core.iching.divine

import destiny.core.Descriptive
import destiny.core.chinese.FiveElement
import destiny.core.chinese.SimpleBranch
import destiny.core.chinese.StemBranch
import destiny.core.iching.Hexagram
import destiny.core.iching.IHexagram
import destiny.tools.asDescriptive
import java.io.Serializable


/** 伏神系統，王洪緒之《卜筮正宗》 , 大多數會是 null  */
class HiddenEnergyWangImpl : IHiddenEnergy,
                             Descriptive by HiddenEnergy.Wang.asDescriptive(),
                             Serializable {

  override fun getStemBranch(hexagram: IHexagram, settings: ISettingsOfStemBranch, lineIndex: Int): StemBranch? {
    val comparator = HexagramDivinationComparator()

    /* 首先查詢目前這個 hexagram 的首宮是哪個卦 */
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

    //int 宮序 = 京房易卦卦序 - 宮位*8;

    val 首宮卦 = Hexagram.of(宮位 * 8 + 1, comparator)


    // 此卦包含的五行
    val containingFiveElements: List<FiveElement> =
      (1..6).map { SimpleBranch.getFiveElement(settings.getStemBranch(hexagram, it).branch) }.distinct()

    // 此卦所缺少的五行
    val lackingFiveElement: List<FiveElement> = listOf(*FiveElement.values())
      .filterNot { containingFiveElements.contains(it) }

    return settings.getStemBranch(首宮卦, lineIndex)
      .takeIf { sb -> lackingFiveElement.contains(SimpleBranch.getFiveElement(sb.branch)) }

  }


  companion object {
    const val VALUE = "wang"
  }
}
