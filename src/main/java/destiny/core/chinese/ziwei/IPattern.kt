/**
 * Created by smallufo on 2018-07-01.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.ziwei.ITransFour.Value.*
import destiny.core.chinese.ziwei.StarLucky.*
import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.chinese.ziwei.StarUnlucky.*
import java.io.Serializable

/**
 * https://medium.com/@JorgeCastilloPr/kotlin-purity-and-function-memoization-b12ab35d70a5
 */
class Memoize<in T, out R>(val f: (T) -> R) : (T) -> R {
  private val values = mutableMapOf<T, R>()
  override fun invoke(x: T): R {
    return values.getOrPut(x) { f(x) }
  }
}

fun <T, R> ((T) -> R).memoize(): (T) -> R = Memoize(this)

fun IPlate.拱(branch: Branch = this.mainHouse.branch): Set<Branch> = branch.let { setOf(it.prev(4), it.next(4)) }
fun IPlate.三方(branch: Branch = this.mainHouse.branch) = 拱(branch).plus(branch)
fun IPlate.三方四正(branch: Branch = this.mainHouse.branch): Set<Branch> = 三方(branch).plus(branch.opposite)

fun IPlate.neighbors(branch: Branch = this.mainHouse.branch): Set<Branch> = branch.let { setOf(it.previous, it.next) }


fun IPlate.日月(): List<Branch> = this.getBranches(太陽, 太陰)
fun IPlate.紫府(): List<Branch> = this.getBranches(紫微, 天府)
fun IPlate.昌曲(): List<Branch> = this.getBranches(文昌, 文曲)
fun IPlate.輔弼(): List<Branch> = this.getBranches(左輔, 右弼)
fun IPlate.魁鉞(): List<Branch> = this.getBranches(天魁, 天鉞)

// 六惡星
fun IPlate.羊陀(): List<Branch> = this.getBranches(擎羊, 陀羅)

fun IPlate.火鈴(): List<Branch> = this.getBranches(火星, 鈴星)
fun IPlate.空劫(): List<Branch> = this.getBranches(地空, 地劫)

fun IPlate.六惡星(): Set<Branch> = this.羊陀().plus(this.火鈴()).plus(this.空劫()).toSet()

fun IPlate.三方四正無六惡星(branch: Branch = this.mainHouse.branch) : Boolean {
  return this
    .takeIf { it.三方四正(branch).intersect(it.六惡星()).isEmpty() }
    ?.let { true }
    ?: false
}

fun IPlate.三方四正有輔弼(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(輔弼())
fun IPlate.鄰宮有輔弼(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(輔弼())

fun IPlate.三方四正有昌曲(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(昌曲())
fun IPlate.鄰宮有昌曲(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(昌曲())

fun IPlate.三方四正有魁鉞(branch: Branch = this.mainHouse.branch) = this.三方四正(branch).containsAll(魁鉞())
fun IPlate.鄰宮有魁鉞(branch: Branch = this.mainHouse.branch) = neighbors(branch).containsAll(魁鉞())

fun IPlate.三方四正有祿存(branch: Branch = this.mainHouse.branch) =
  this.三方四正(branch).contains(this.starMap[祿存]?.stemBranch?.branch)

fun IPlate.三方四正有祿權科星(branch: Branch = this.mainHouse.branch): Boolean =
  this.三方四正(branch).flatMap { b ->
    this.getHouseDataOf(b).stars.map { star: ZStar ->
      this.tranFours[star]?.get(FlowType.本命)
    }
  }.any { value: ITransFour.Value? -> setOf(祿, 權, 科).contains(value) }


fun IPlate.化祿入命宮() = this.getTransFourHouseOf(祿).stemBranch.branch == this.mainHouse.branch
fun IPlate.化權入命宮() = this.getTransFourHouseOf(權).stemBranch.branch == this.mainHouse.branch
fun IPlate.化科入命宮() = this.getTransFourHouseOf(科).stemBranch.branch == this.mainHouse.branch
fun IPlate.化忌入命宮() = this.getTransFourHouseOf(忌).stemBranch.branch == this.mainHouse.branch

enum class GoodCombo {
  輔弼,
  昌曲,
  魁鉞,
  祿存,
  祿權科星
}

enum class EvilCombo {
  空劫,
  火鈴,
  羊陀
}

enum class Route {
  入,
  夾,
  拱,
}


interface IPattern {

  val name: String

  val notes: String?
}


/** ============================================================================= */

/**
 * 某星 在 某宮位
 */
interface IStarHousePattern : IPattern {
  val orStars : Set<ZStar>
  val house : House

  override val name: String
    get() = (orStars.joinToString("或") { it.toString() }+"在"+house).let {
      if (!it.endsWith("宮"))
        it+"宮"
      else
        it
    }
}

data class StarHousePattern(
  override val orStars: Set<ZStar>,
  override val house: House) : IStarHousePattern , Serializable {
  override val notes: String?
    get() = null
}


/** ============================================================================= */

/**
 * A星(與B星）坐X或Y地支 於 Z宮位
 */
interface IStarsBranchesHousePattern : IPattern {
  val stars : Set<ZStar>
  val branches : Set<Branch>
  val house : House

  override val name: String
    get() = {
      val sb = StringBuilder()
      sb.append(stars.joinToString("與"))

      if (stars.size == 1)
        sb.append(" 獨坐 ")
      else
        sb.append(" 坐 ")

      sb.append(branches.joinToString("或"))
      sb.append(" 於 ")
      sb.append(house.toString().let {
        if (!it.endsWith("宮"))
          it+"宮"
        else
          it
      })
      sb.toString()
    }.invoke()
}

data class StarBranchHousePattern (
  val andStars: Set<ZStar>,
  val orBranches: Set<Branch>,
  override val house: House
  ) : IStarsBranchesHousePattern , Serializable {
  override val stars: Set<ZStar>
    get() = andStars
  override val branches: Set<Branch>
    get() = orBranches
  override val notes: String?
    get() = null
}
