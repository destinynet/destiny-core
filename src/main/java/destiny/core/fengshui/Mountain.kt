/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.core.fengshui

import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.iching.Symbol

sealed class SealedMnt {
  data class MntBranch(val branch: Branch) : SealedMnt()
  data class MntStem(val stem: Stem) : SealedMnt()
  data class MntSymbol(val symbol: Symbol) : SealedMnt()
}

enum class Mountain(val mnt: SealedMnt, val symbol: Symbol) {
  子(SealedMnt.MntBranch(Branch.子), Symbol.坎),
  癸(SealedMnt.MntStem(Stem.癸), Symbol.坎),

  丑(SealedMnt.MntBranch(Branch.丑), Symbol.艮),
  艮(SealedMnt.MntSymbol(Symbol.艮), Symbol.艮),
  寅(SealedMnt.MntBranch(Branch.寅), Symbol.艮),

  甲(SealedMnt.MntStem(Stem.甲), Symbol.震),
  卯(SealedMnt.MntBranch(Branch.卯), Symbol.震),
  乙(SealedMnt.MntStem(Stem.乙), Symbol.震),

  辰(SealedMnt.MntBranch(Branch.辰), Symbol.巽),
  巽(SealedMnt.MntSymbol(Symbol.巽), Symbol.巽),
  巳(SealedMnt.MntBranch(Branch.巳), Symbol.巽),

  丙(SealedMnt.MntStem(Stem.丙), Symbol.離),
  午(SealedMnt.MntBranch(Branch.午), Symbol.離),
  丁(SealedMnt.MntStem(Stem.丁), Symbol.離),

  未(SealedMnt.MntBranch(Branch.未), Symbol.坤),
  坤(SealedMnt.MntSymbol(Symbol.坤), Symbol.坤),
  申(SealedMnt.MntBranch(Branch.申), Symbol.坤),

  庚(SealedMnt.MntStem(Stem.庚), Symbol.兌),
  酉(SealedMnt.MntBranch(Branch.酉), Symbol.兌),
  辛(SealedMnt.MntStem(Stem.辛), Symbol.兌),

  戌(SealedMnt.MntBranch(Branch.戌), Symbol.乾),
  乾(SealedMnt.MntSymbol(Symbol.乾), Symbol.乾),
  亥(SealedMnt.MntBranch(Branch.亥), Symbol.乾),

  壬(SealedMnt.MntStem(Stem.壬), Symbol.坎);

  /** 0 ~ 23 */
  val index: Int by lazy {
     values().indexOf(this)
  }

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
