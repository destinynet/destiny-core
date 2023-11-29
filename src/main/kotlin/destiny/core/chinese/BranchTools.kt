/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*

object BranchTools {

  val trilogies: Set<Pair<Set<Branch>, FiveElement>>
    get() {
      return setOf(
        setOf(申, 子, 辰) to 水,
        setOf(巳, 酉, 丑) to 金,
        setOf(亥, 卯, 未) to 木,
        setOf(寅, 午, 戌) to 火,
      )
    }

  /** 地支三合  */
  fun trilogy(branch: Branch): FiveElement {
    return trilogies.first {
      it.first.contains(branch)
    }.second
  }

  /** 地支三會  */
  fun direction(branch: Branch): FiveElement {
    return when (branch) {
      亥, 子, 丑 -> 水
      寅, 卯, 辰 -> 木
      巳, 午, 未 -> 火
      申, 酉, 戌 -> 金
    }
  }

}
