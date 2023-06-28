/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese

import destiny.core.chinese.FiveElement.*

object BranchTools {

  /** 地支三合  */
  fun trilogy(branch: Branch): FiveElement {
    return when (branch) {
      Branch.申, Branch.子, Branch.辰 -> 水
      Branch.巳, Branch.酉, Branch.丑 -> 金
      Branch.亥, Branch.卯, Branch.未 -> 木
      Branch.寅, Branch.午, Branch.戌 -> 火
    }
  }

  /** 地支三會  */
  fun direction(branch: Branch): FiveElement {
    return when (branch) {
      Branch.亥, Branch.子, Branch.丑 -> 水
      Branch.寅, Branch.卯, Branch.辰 -> 木
      Branch.巳, Branch.午, Branch.未 -> 火
      Branch.申, Branch.酉, Branch.戌 -> 金
    }
  }

}
