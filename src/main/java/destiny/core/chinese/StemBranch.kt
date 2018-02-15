package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.甲子
import destiny.core.chinese.StemBranch.癸亥
import destiny.tools.ArrayTools

/**
 * 中國干支組合表示法，0[甲子] ~ 59[癸亥]
 */
enum class StemBranch(override val stem: Stem, override val branch: Branch) : IStemBranchOptional , Comparable<StemBranch> {
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
  fun next(n: Int): StemBranch {
    return get(getIndex(this) + n)
  }


  /**
   * 取得前 n 組干支組合
   * n = 0 : 傳回自己
   */
  fun prev(n: Int): StemBranch {
    return next(0 - n)
  }

  /** 取得下一組干支 , 甲子 傳回 乙丑  */
  val next: StemBranch
    get() = next(1)

  /** 取得上一組干支 , 甲子 傳回 癸亥  */
  val previous: StemBranch
    get() = prev(1)


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
    return ARRAY.indexOf(sb)
  } //getIndex()

    /** 取得「空亡」的兩個地支  */
  val empties: Collection<Branch>
    get() = StemBranch.getEmpties(this)


  override fun toString(): String {
    return stem.toString() + branch.toString()
  }



  companion object {
    private val ARRAY = arrayOf(甲子, 乙丑, 丙寅, 丁卯, 戊辰, 己巳, 庚午, 辛未, 壬申, 癸酉, 甲戌, 乙亥, 丙子, 丁丑, 戊寅, 己卯, 庚辰, 辛巳, 壬午, 癸未, 甲申, 乙酉, 丙戌, 丁亥, 戊子, 己丑,
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
  } // companion
}