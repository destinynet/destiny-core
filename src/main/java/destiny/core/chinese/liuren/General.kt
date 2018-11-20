/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren

import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.StemBranch

/**
 * 十二天將
 */
enum class General(val shortName: Char, val positive: Boolean, val fiveElement: FiveElement) {

  貴人('貴',  true, 土),
  螣蛇('螣', false, 火),
  朱雀('朱', false, 火),
  六合('合',  true, 木),
  勾陳('勾', false, 土),
  青龍('青',  true, 木),
  天空('空', false, 土),
  白虎('白', false, 金),
  太常('常',  true, 土),
  玄武('玄', false, 水),
  太陰('陰',  true, 金),
  天后('后',  true, 水);

  fun getStemBranch(stemBranchConfig: IGeneralStemBranch): StemBranch {
    return stemBranchConfig.getStemBranch(this)
  }

  fun next(n: Int, seq: IGeneralSeq): General {
    return seq.next(this, n)
  }

  fun prev(n: Int, seq: IGeneralSeq): General {
    return seq.prev(this, n)
  }

  companion object {

    operator fun get(branch: Branch, stemBranchConfig: IGeneralStemBranch): General {
      return values().first { g -> stemBranchConfig.getStemBranch(g).branch === branch }
    }
  }
}
