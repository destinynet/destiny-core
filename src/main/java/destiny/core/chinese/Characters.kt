/**
 * Created by smallufo on 2018-02-01.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*
import destiny.core.chinese.Stem.*

/** 各式神煞演算法 */
object Characters {

  /** 驛馬 */
  fun getHorse(branch : Branch) : Branch {
    return when (BranchTools.trilogy(branch)) {
      水 -> 寅
      木 -> 巳
      金 -> 亥
      火 -> 申
      else -> throw AssertionError(branch)
    }
  }

  /** 桃花、咸池 : Peach */
  fun getPeach(branch: Branch) : Branch {
    return when (BranchTools.trilogy(branch)) {
      水 -> 酉
      木 -> 子
      金 -> 午
      火 -> 卯
      else -> throw AssertionError(branch)
    }
  }

  /** 祿 , Bliss */
  fun getBliss(stem: Stem) : Branch {
    return when(stem) {
      甲 -> 寅
      乙 -> 卯
      丙 , 戊 -> 巳
      丁 , 己 -> 午
      庚 -> 申
      辛 -> 酉
      壬 -> 亥
      癸 -> 子
    }
  }

}