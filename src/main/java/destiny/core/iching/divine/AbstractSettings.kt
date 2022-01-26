/**
 * Created by smallufo on 2016-09-03.
 */
package destiny.core.iching.divine

import destiny.core.Descriptive
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.core.iching.IHexagram
import destiny.core.iching.Symbol
import destiny.tools.asDescriptive
import java.io.Serializable

abstract class AbstractSettings(override val settings: SettingsOfStemBranch) : ISettingsOfStemBranch,
                                                                               Descriptive by settings.asDescriptive(),
                                                                               Serializable {

  protected abstract val symbolStemMap: Map<Symbol, List<Stem>>
  protected abstract val symbolBranchMap: Map<Symbol, List<Branch>>

  override fun getStemBranch(hexagram: IHexagram, lineIndex: Int): StemBranch {
    val stem: Stem
    val branch: Branch

    if ((1..3).contains(lineIndex)) {
      //下卦天干
      stem = symbolStemMap.getValue(hexagram.lowerSymbol)[lineIndex - 1]
      branch = symbolBranchMap.getValue(hexagram.lowerSymbol)[lineIndex - 1]
    } else {
      //上卦天干
      stem = symbolStemMap.getValue(hexagram.upperSymbol)[lineIndex - 1]
      branch = symbolBranchMap.getValue(hexagram.upperSymbol)[lineIndex - 1]
    } //上卦天干

    return StemBranch[stem, branch]
  }
}
