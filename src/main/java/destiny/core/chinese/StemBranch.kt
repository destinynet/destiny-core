package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.Companion.甲子
import destiny.core.chinese.StemBranch.Companion.癸亥
import destiny.tools.ArrayTools
import java.io.Serializable

/**
 * 中國干支組合表示法，0[甲子] ~ 59[癸亥]
 */
class StemBranch constructor(override val stem: Stem, override val branch: Branch) : StemBranchOptional(stem, branch),
  Comparable<StemBranch>, Serializable {

  /** 取得下一組干支 , 甲子 傳回 乙丑  */
  val next: StemBranch
    get() = next(1)

  /** 取得上一組干支 , 甲子 傳回 癸亥  */
  val previous: StemBranch
    get() = prev(1)


  /**
   * @return 0[甲子] ~ 59[癸亥]
   */
  override val index: Int
    get() = getIndex(this)

  /** 取得「空亡」的兩個地支  */
  val empties: Collection<Branch>
    get() = StemBranch.getEmpties(this)

  /**
   * 取得下 n 組干支組合
   * n = 0 : 傳回自己
   */
  override fun next(n: Int): StemBranch {
    return get(getIndex(this) + n)
  }


  /**
   * 取得前 n 組干支組合
   * n = 0 : 傳回自己
   */
  fun prev(n: Int): StemBranch {
    return next(0 - n)
  }

  /** 取得干支的差距，例如 "乙丑" 距離 "甲子" 的差距為 "1" , 通常是用於計算「虛歲」 (尚需加一)  */
  fun differs(sb: StemBranch): Int {
    return getIndex(this) - sb.index
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

  override fun toString(): String {
    return stem.toString() + branch.toString()
  }

  /**
   * 實作 Comparable 的 compareTo()
   */
  override fun compareTo(other: StemBranch): Int {
    return getIndex(this) - getIndex(other)
  } //compareTo()



  companion object {

    val 甲子 = StemBranch(甲, 子)
    val 乙丑 = StemBranch(乙, 丑)
    val 丙寅 = StemBranch(丙, 寅)
    val 丁卯 = StemBranch(丁, 卯)
    val 戊辰 = StemBranch(戊, 辰)
    val 己巳 = StemBranch(己, 巳)
    val 庚午 = StemBranch(庚, 午)
    val 辛未 = StemBranch(辛, 未)
    val 壬申 = StemBranch(壬, 申)
    val 癸酉 = StemBranch(癸, 酉)
    val 甲戌 = StemBranch(甲, 戌)
    val 乙亥 = StemBranch(乙, 亥)
    val 丙子 = StemBranch(丙, 子)
    val 丁丑 = StemBranch(丁, 丑)
    val 戊寅 = StemBranch(戊, 寅)
    val 己卯 = StemBranch(己, 卯)
    val 庚辰 = StemBranch(庚, 辰)
    val 辛巳 = StemBranch(辛, 巳)
    val 壬午 = StemBranch(壬, 午)
    val 癸未 = StemBranch(癸, 未)
    val 甲申 = StemBranch(甲, 申)
    val 乙酉 = StemBranch(乙, 酉)
    val 丙戌 = StemBranch(丙, 戌)
    val 丁亥 = StemBranch(丁, 亥)
    val 戊子 = StemBranch(戊, 子)
    val 己丑 = StemBranch(己, 丑)
    val 庚寅 = StemBranch(庚, 寅)
    val 辛卯 = StemBranch(辛, 卯)
    val 壬辰 = StemBranch(壬, 辰)
    val 癸巳 = StemBranch(癸, 巳)
    val 甲午 = StemBranch(甲, 午)
    val 乙未 = StemBranch(乙, 未)
    val 丙申 = StemBranch(丙, 申)
    val 丁酉 = StemBranch(丁, 酉)
    val 戊戌 = StemBranch(戊, 戌)
    val 己亥 = StemBranch(己, 亥)
    val 庚子 = StemBranch(庚, 子)
    val 辛丑 = StemBranch(辛, 丑)
    val 壬寅 = StemBranch(壬, 寅)
    val 癸卯 = StemBranch(癸, 卯)
    val 甲辰 = StemBranch(甲, 辰)
    val 乙巳 = StemBranch(乙, 巳)
    val 丙午 = StemBranch(丙, 午)
    val 丁未 = StemBranch(丁, 未)
    val 戊申 = StemBranch(戊, 申)
    val 己酉 = StemBranch(己, 酉)
    val 庚戌 = StemBranch(庚, 戌)
    val 辛亥 = StemBranch(辛, 亥)
    val 壬子 = StemBranch(壬, 子)
    val 癸丑 = StemBranch(癸, 丑)
    val 甲寅 = StemBranch(甲, 寅)
    val 乙卯 = StemBranch(乙, 卯)
    val 丙辰 = StemBranch(丙, 辰)
    val 丁巳 = StemBranch(丁, 巳)
    val 戊午 = StemBranch(戊, 午)
    val 己未 = StemBranch(己, 未)
    val 庚申 = StemBranch(庚, 申)
    val 辛酉 = StemBranch(辛, 酉)
    val 壬戌 = StemBranch(壬, 戌)
    val 癸亥 = StemBranch(癸, 亥)

    // 0[甲子] ~ 59[癸亥]
    private val ARRAY =
      arrayOf(甲子, 乙丑, 丙寅, 丁卯, 戊辰, 己巳, 庚午, 辛未, 壬申, 癸酉, 甲戌, 乙亥, 丙子, 丁丑, 戊寅, 己卯, 庚辰, 辛巳, 壬午, 癸未, 甲申, 乙酉, 丙戌, 丁亥, 戊子, 己丑,
              庚寅, 辛卯, 壬辰, 癸巳, 甲午, 乙未, 丙申, 丁酉, 戊戌, 己亥, 庚子, 辛丑, 壬寅, 癸卯, 甲辰, 乙巳, 丙午, 丁未, 戊申, 己酉, 庚戌, 辛亥, 壬子, 癸丑, 甲寅, 乙卯,
              丙辰, 丁巳, 戊午, 己未, 庚申, 辛酉, 壬戌, 癸亥)

    val list = listOf(*ARRAY)

    /**
     * @param index 0[甲子] ~ 59[癸亥]
     */
    operator fun get(index: Int): StemBranch {
      return ArrayTools[ARRAY, index]
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


    /**
     * 0[甲子] ~ 59[癸亥]
     * @param sb 取得某組干支的順序
     * @return 0[甲子] ~ 59[癸亥]
     */
    private fun getIndex(sb: StemBranch): Int {
      return list.indexOf(sb)
    } //getIndex()

    operator fun iterator(): Iterator<StemBranch> {
      return ARRAY.iterator()
    }
    
    fun iterable(): Iterable<StemBranch> {
      return listOf(*ARRAY)
    }

    /** 取得「空亡」的兩個地支  */
    fun getEmpties(sb: StemBranch): Collection<Branch> {
      val shift = sb.stem.index - sb.branch.index
      return when (shift) {
        0 -> listOf(戌, 亥)
        2, -10 -> listOf(申, 酉)
        4, -8 -> listOf(午, 未)
        6, -6 -> listOf(辰, 巳)
        8, -4 -> listOf(寅, 卯)
        10, -2 -> listOf(子, 丑)
        else -> throw AssertionError("Cannot find 空亡 from " + sb)
      }
    }
  }

}
