package destiny.core.chinese

import destiny.core.ILoop
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.甲子
import destiny.core.chinese.StemBranch.癸亥
import destiny.tools.ArrayTools

/**
 * non-nullable && unConstrained
 */
interface IStemBranch : IStemBranchOptional , ILoop<IStemBranch> {
  override val stem: Stem
  override val branch: Branch
}

enum class StemBranchCycle(val sb: StemBranch) {
  甲子(StemBranch.甲子),
  甲寅(StemBranch.甲寅),
  甲辰(StemBranch.甲辰),
  甲午(StemBranch.甲午),
  甲申(StemBranch.甲申),
  甲戌(StemBranch.甲戌)
}

/**
 * 中國干支組合表示法，0[甲子] ~ 59[癸亥]
 */
enum class StemBranch(override val stem: Stem, override val branch: Branch) : IStemBranchOptional, IStemBranch,
  ILoop<IStemBranch>,
  Comparable<StemBranch> {
  甲子(甲, 子),
  乙丑(乙, 丑),
  丙寅(丙, 寅),
  丁卯(丁, 卯),
  戊辰(戊, 辰),
  己巳(己, 巳),
  庚午(庚, 午),
  辛未(辛, 未),
  壬申(壬, 申),
  癸酉(癸, 酉),
  甲戌(甲, 戌),
  乙亥(乙, 亥),
  丙子(丙, 子),
  丁丑(丁, 丑),
  戊寅(戊, 寅),
  己卯(己, 卯),
  庚辰(庚, 辰),
  辛巳(辛, 巳),
  壬午(壬, 午),
  癸未(癸, 未),
  甲申(甲, 申),
  乙酉(乙, 酉),
  丙戌(丙, 戌),
  丁亥(丁, 亥),
  戊子(戊, 子),
  己丑(己, 丑),
  庚寅(庚, 寅),
  辛卯(辛, 卯),
  壬辰(壬, 辰),
  癸巳(癸, 巳),
  甲午(甲, 午),
  乙未(乙, 未),
  丙申(丙, 申),
  丁酉(丁, 酉),
  戊戌(戊, 戌),
  己亥(己, 亥),
  庚子(庚, 子),
  辛丑(辛, 丑),
  壬寅(壬, 寅),
  癸卯(癸, 卯),
  甲辰(甲, 辰),
  乙巳(乙, 巳),
  丙午(丙, 午),
  丁未(丁, 未),
  戊申(戊, 申),
  己酉(己, 酉),
  庚戌(庚, 戌),
  辛亥(辛, 亥),
  壬子(壬, 子),
  癸丑(癸, 丑),
  甲寅(甲, 寅),
  乙卯(乙, 卯),
  丙辰(丙, 辰),
  丁巳(丁, 巳),
  戊午(戊, 午),
  己未(己, 未),
  庚申(庚, 申),
  辛酉(辛, 酉),
  壬戌(壬, 戌),
  癸亥(癸, 亥);

  /** @return 0[甲子] ~ 59[癸亥] */
  val index: Int
    get() = getIndex(this)

  /**
   * 取得下 n 組干支組合
   * n = 0 : 傳回自己
   */
  override fun next(n: Int): StemBranch {
    return get(getIndex(this) + n)
  }

  /**
   * 取得此干支，領先另一組，多少步. 其值一定為正值
   *
   * 「甲子」領先「癸亥」 1
   * 「甲子」領先「乙丑」59
   */
  fun getAheadOf(other: StemBranch): Int {
    val steps = index - other.index
    return if (steps >= 0) steps else steps + 60
  }

  /** 取得干支的差距，例如 "乙丑" 距離 "甲子" 的差距為 "1" , 通常是用於計算「虛歲」 (尚需加一)  */
  fun differs(sb: StemBranch): Int {
    return getIndex(this) - sb.index
  }

  /**
   * 0[甲子] ~ 59[癸亥]
   * @param sb 取得某組干支的順序
   * @return 0[甲子] ~ 59[癸亥]
   */
  private fun getIndex(sb: StemBranch): Int {
    return values().indexOf(sb)
  } //getIndex()

  /** 取得「空亡」的兩個地支  */
  val empties: Collection<Branch>
    get() = getEmpties(this)

  /** 哪一「旬」 */
  val cycle: StemBranchCycle
    get() = getCycle(this)


  override fun toString(): String {
    return stem.toString() + branch.toString()
  }


  companion object {

    /**
     * @param index 0[甲子] ~ 59[癸亥]
     */
    operator fun get(index: Int): StemBranch {
      return ArrayTools[values(), index]
    }

    operator fun get(stem: Stem, branch: Branch): StemBranch {
      if (Stem.getIndex(stem) % 2 != Branch.getIndex(branch) % 2)
        throw RuntimeException("Stem/Branch combination illegal ! $stem cannot be combined with $branch")

      val sIndex = Stem.getIndex(stem)
      val bIndex = Branch.getIndex(branch)
      return when (sIndex - bIndex) {
        0, -10 -> get(bIndex)
        2, -8 -> get(bIndex + 12)
        4, -6 -> get(bIndex + 24)
        6, -4 -> get(bIndex + 36)
        8, -2 -> get(bIndex + 48)
        else -> throw AssertionError("Invalid stem/branch Combination!")
      }
    }

    operator fun get(stemChar: Char, branchChar: Char): StemBranch {

      val stem = Stem[stemChar]
      val branch = Branch[branchChar]
      return if (stem != null && branch != null) {
        StemBranch[stem, branch]
      } else {
        throw RuntimeException("Cannot get StemBranch($stemChar , $branchChar)")
      }
    }

    operator fun get(stemBranch: String): StemBranch {
      return if (stemBranch.length != 2)
        throw RuntimeException("The length of $stemBranch must equal to 2 !")
      else
        get(stemBranch[0], stemBranch[1])
    }

    /** 強制將 [IStemBranchOptional] downcast 成 [StemBranch] , 可能會拋出 NPE ! */
    operator fun get(sbo: IStemBranchOptional): StemBranch {
      return get(sbo.stem!!, sbo.branch!!)
    }


    /**
     * 0[甲子] ~ 59[癸亥]
     * @param sb 取得某組干支的順序
     * @return 0[甲子] ~ 59[癸亥]
     */
    private fun getIndex(sb: StemBranch): Int {
      return values().indexOf(sb)
    } //getIndex()

    operator fun iterator(): Iterator<StemBranch> {
      return values().iterator()
    }

    fun iterable(): Iterable<StemBranch> {
      return listOf(*values())
    }

    fun getCycle(sb: StemBranch): StemBranchCycle {
      return when (sb.stem.index - sb.branch.index) {
        0 -> StemBranchCycle.甲子
        2, -10 -> StemBranchCycle.甲戌
        4, -8 -> StemBranchCycle.甲申
        6, -6 -> StemBranchCycle.甲午
        8, -4 -> StemBranchCycle.甲辰
        10, -2 -> StemBranchCycle.甲寅
        else -> throw AssertionError("Not valid $sb")
      }
    }

    /** 取得「空亡」的兩個地支  */
    fun getEmpties(sb: StemBranch): Collection<Branch> {
      return when (sb.cycle) {
        StemBranchCycle.甲子 -> listOf(戌, 亥)
        StemBranchCycle.甲戌 -> listOf(申, 酉)
        StemBranchCycle.甲申 -> listOf(午, 未)
        StemBranchCycle.甲午 -> listOf(辰, 巳)
        StemBranchCycle.甲辰 -> listOf(寅, 卯)
        StemBranchCycle.甲寅 -> listOf(子, 丑)
      }
    }
  } // companion
}