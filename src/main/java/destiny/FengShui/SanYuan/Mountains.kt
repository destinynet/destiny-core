/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.FengShui.SanYuan

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.iching.Symbol

sealed class SealedMnt {
  data class MntBranch(val branch: Branch) : SealedMnt()
  data class MntStem(val stem: Stem) : SealedMnt()
  data class MntSymbol(val symbol: destiny.iching.Symbol) : SealedMnt()
}

enum class Mountain(val mnt: SealedMnt) {
  子(SealedMnt.MntBranch(Branch.子)),
  癸(SealedMnt.MntStem(Stem.癸)),
  丑(SealedMnt.MntBranch(Branch.丑)),
  艮(SealedMnt.MntSymbol(Symbol.艮)),
  寅(SealedMnt.MntBranch(Branch.寅)),
  甲(SealedMnt.MntStem(Stem.甲)),
  卯(SealedMnt.MntBranch(Branch.卯)),
  乙(SealedMnt.MntStem(Stem.乙)),
  辰(SealedMnt.MntBranch(Branch.辰)),
  巽(SealedMnt.MntSymbol(Symbol.巽)),
  巳(SealedMnt.MntBranch(Branch.巳)),
  丙(SealedMnt.MntStem(Stem.丙)),
  午(SealedMnt.MntBranch(Branch.午)),
  丁(SealedMnt.MntStem(Stem.丁)),
  未(SealedMnt.MntBranch(Branch.未)),
  坤(SealedMnt.MntSymbol(Symbol.坤)),
  申(SealedMnt.MntBranch(Branch.申)),
  庚(SealedMnt.MntStem(Stem.庚)),
  酉(SealedMnt.MntBranch(Branch.酉)),
  辛(SealedMnt.MntStem(Stem.辛)),
  戌(SealedMnt.MntBranch(Branch.戌)),
  乾(SealedMnt.MntSymbol(Symbol.乾)),
  亥(SealedMnt.MntBranch(Branch.亥)),
  壬(SealedMnt.MntStem(Stem.壬));

  /** 0 ~ 23 */
  val index: Int
    get() = values().indexOf(this)


  val opposite: Mountain
    get() {
      val newIndex = normalize(this.index + 12)
      return values()[newIndex]
    }

  companion object {
    fun normalize(value: Int): Int {
      return when {
        value > 23 -> normalize(value - 24)
        value < 0 -> normalize(value + 24)
        else -> value
      }
    }
  }
}
