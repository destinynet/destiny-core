/**
 * Created by smallufo on 2018-06-06.
 */
package destiny.core.chinese

import destiny.core.ILoop
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.tools.ArrayTools


/**
 * 120柱干支組合，沒有 陰陽限制
 */
enum class StemBranchUnconstrained(override val stem: Stem,
                                   override val branch: Branch) : IStemBranch {
  甲子(甲, 子),
  乙子(乙, 子),
  乙丑(乙, 丑),
  丙丑(丙, 丑),
  丙寅(丙, 寅),
  丁寅(丁, 寅),
  丁卯(丁, 卯),
  戊卯(戊, 卯),
  戊辰(戊, 辰),
  己辰(己, 辰),
  己巳(己, 巳),
  庚巳(庚, 巳),
  庚午(庚, 午),
  辛午(辛, 午),
  辛未(辛, 未),
  壬未(壬, 未),
  壬申(壬, 申),
  癸申(癸, 申),
  癸酉(癸, 酉),
  甲酉(甲, 酉),
  甲戌(甲, 戌),
  乙戌(乙, 戌),
  乙亥(乙, 亥),
  丙亥(丙, 亥),
  丙子(丙, 子),
  丁子(丁, 子),
  丁丑(丁, 丑),
  戊丑(戊, 丑),
  戊寅(戊, 寅),
  己寅(己, 寅),
  己卯(己, 卯),
  庚卯(庚, 卯),
  庚辰(庚, 辰),
  辛辰(辛, 辰),
  辛巳(辛, 巳),
  壬巳(壬, 巳),
  壬午(壬, 午),
  癸午(癸, 午),
  癸未(癸, 未),
  甲未(甲, 未),
  甲申(甲, 申),
  乙申(乙, 申),
  乙酉(乙, 酉),
  丙酉(丙, 酉),
  丙戌(丙, 戌),
  丁戌(丁, 戌),
  丁亥(丁, 亥),
  戊亥(戊, 亥),
  戊子(戊, 子),
  己子(己, 子),
  己丑(己, 丑),
  庚丑(庚, 丑),
  庚寅(庚, 寅),
  辛寅(辛, 寅),
  辛卯(辛, 卯),
  壬卯(壬, 卯),
  壬辰(壬, 辰),
  癸辰(癸, 辰),
  癸巳(癸, 巳),
  甲巳(甲, 巳),
  甲午(甲, 午),
  乙午(乙, 午),
  乙未(乙, 未),
  丙未(丙, 未),
  丙申(丙, 申),
  丁申(丁, 申),
  丁酉(丁, 酉),
  戊酉(戊, 酉),
  戊戌(戊, 戌),
  己戌(己, 戌),
  己亥(己, 亥),
  庚亥(庚, 亥),
  庚子(庚, 子),
  辛子(辛, 子),
  辛丑(辛, 丑),
  壬丑(壬, 丑),
  壬寅(壬, 寅),
  癸寅(癸, 寅),
  癸卯(癸, 卯),
  甲卯(甲, 卯),
  甲辰(甲, 辰),
  乙辰(乙, 辰),
  乙巳(乙, 巳),
  丙巳(丙, 巳),
  丙午(丙, 午),
  丁午(丁, 午),
  丁未(丁, 未),
  戊未(戊, 未),
  戊申(戊, 申),
  己申(己, 申),
  己酉(己, 酉),
  庚酉(庚, 酉),
  庚戌(庚, 戌),
  辛戌(辛, 戌),
  辛亥(辛, 亥),
  壬亥(壬, 亥),
  壬子(壬, 子),
  癸子(癸, 子),
  癸丑(癸, 丑),
  甲丑(甲, 丑),
  甲寅(甲, 寅),
  乙寅(乙, 寅),
  乙卯(乙, 卯),
  丙卯(丙, 卯),
  丙辰(丙, 辰),
  丁辰(丁, 辰),
  丁巳(丁, 巳),
  戊巳(戊, 巳),
  戊午(戊, 午),
  己午(己, 午),
  己未(己, 未),
  庚未(庚, 未),
  庚申(庚, 申),
  辛申(辛, 申),
  辛酉(辛, 酉),
  壬酉(壬, 酉),
  壬戌(壬, 戌),
  癸戌(癸, 戌),
  癸亥(癸, 亥),
  甲亥(甲, 亥);

  fun toStemBranch(): StemBranch? {
    return if (index % 2 == 0) {
      StemBranch[stem, branch]
    } else {
      null
    }
  }

  /** @return 0[甲子] ~ 119[甲亥] */
  val index: Int
    get() = getIndex(this)

  /**
   * 取得下 n 組干支組合
   * n = 0 : 傳回自己
   */
  override fun next(n: Int): StemBranchUnconstrained {
    return get(getIndex(this) + n)
  }


  /**
   * 取得此干支，領先另一組，多少步. 其值一定為正值
   *
   * 「甲子」領先「甲亥」 1
   * 「甲子」領先「乙子」119
   */
  fun getAheadOf(other: StemBranchUnconstrained): Int {
    val steps = index - other.index
    return if (steps >= 0) steps else steps + 120
  }

  private fun getIndex(sbu: StemBranchUnconstrained): Int {
    return values().indexOf(sbu)
  }




  companion object {

    /** 0[甲子] ~ 119[甲亥] */
    operator fun get(index: Int): StemBranchUnconstrained {
      return ArrayTools[values(), index]
    }

    operator fun get(stem: Stem, branch: Branch): StemBranchUnconstrained? {
      return values().firstOrNull { it.stem == stem && it.branch == branch }
    }
  }
}
