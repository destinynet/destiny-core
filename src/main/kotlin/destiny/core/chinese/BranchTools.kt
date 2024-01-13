/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese

import destiny.core.chinese.Branch.*
import destiny.core.chinese.FiveElement.*

object BranchTools {

  private val trilogies: Set<Pair<Set<Branch>, FiveElement>>
    get() {
      return setOf(
        setOf(申, 子, 辰) to 水,
        setOf(巳, 酉, 丑) to 金,
        setOf(亥, 卯, 未) to 木,
        setOf(寅, 午, 戌) to 火,
      )
    }

  /** 地支三合  */
  fun Branch.trilogy() : FiveElement {
    return trilogies.first {
      it.first.contains(this)
    }.second
  }

  fun trilogy(branch1: Branch, branch2: Branch, branch3: Branch): FiveElement? {
    return setOf(branch1, branch2, branch3).takeIf { it.size == 3 }
      ?.let { all3 ->
        trilogies.firstOrNull { (set, _) ->
          set.containsAll(all3)
        }?.second
      }
  }

  fun Branch.trilogyCount(vararg branches: Branch) : Int {
    return branches.map { b ->
      if (this.trilogy() == b.trilogy()) 1 else 0
    }.sum()
  }


  val directions : Set<Pair<Set<Branch> , FiveElement>>
    get() {
      return setOf(
        setOf(亥, 子, 丑) to 水,
        setOf(寅, 卯, 辰) to 木,
        setOf(巳, 午, 未) to 火,
        setOf(申, 酉, 戌) to 金,
      )
    }

  /** 地支三會  */
  fun direction(branch: Branch): FiveElement {
    return directions.first {
      it.first.contains(branch)
    }.second
  }

  fun direction(branch1: Branch, branch2: Branch, branch3: Branch) : FiveElement? {
    return setOf(branch1, branch2, branch3).takeIf { it.size == 3 }
      ?.let { all3 ->
        directions.firstOrNull { (set, _) ->
          set.containsAll(all3)
        }?.second
      }
  }

}
