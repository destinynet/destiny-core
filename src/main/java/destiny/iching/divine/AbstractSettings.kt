/**
 * Created by smallufo on 2016-09-03.
 */
package destiny.iching.divine

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.iching.IHexagram
import destiny.iching.Symbol

import java.io.Serializable

abstract class AbstractSettings : ISettingsOfStemBranch, Serializable {

  protected abstract val symbolStemMap: Map<Symbol, List<Stem>>
  protected abstract val symbolBranchMap: Map<Symbol, List<Branch>>

  override fun getStemBranch(hexagram: IHexagram, lineIndex: Int): StemBranch {
    val 天干: Stem
    val 地支: Branch

    if ((1..3).contains(lineIndex)) {
      //下卦天干
      天干 = symbolStemMap[hexagram.lowerSymbol]!![lineIndex-1]
      地支 = symbolBranchMap[hexagram.lowerSymbol]!![lineIndex-1]
    } else {
      //上卦天干
      天干 = symbolStemMap[hexagram.upperSymbol]!![lineIndex-1]
      地支 = symbolBranchMap[hexagram.upperSymbol]!![lineIndex-1]
    } //上卦天干

    return StemBranch.get(天干, 地支)
  }

}
