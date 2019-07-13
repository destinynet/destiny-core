/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import java.io.Serializable

/**
 * 命盤中，一個宮位所包含的所有資訊
 */
data class HouseData(

  /** 宮位名稱  */
  val house: House,

  /** 宮位干支  */
  val stemBranch: StemBranch,

  /** 宮位裡面 有哪些星體  */
  val stars: MutableSet<ZStar>,

  /** 此宮位，在各個流運，叫什麼宮位  */
  private val flowHouseMap: Map<FlowType, House>,

  /** 宮干四化，此宮位，因為什麼星，各飛入哪個宮位(地支)  */
  private val transFourFlyMap: Set<Triple<ITransFour.Value, ZStar, Branch>>,

  /** 大限，從「虛歲」幾歲，到幾歲 */
  val rangeFromVage: Int,
  val rangeToVage: Int,

  /** 六條小限  */
  val smallRanges: List<Int>) : Serializable, Comparable<HouseData> {

  /** 宮干自化 列表 , 長度 0 , 1 or 2  */
  val selfTransFours: List<ITransFour.Value>
    get() = transFourFlyMap
      .filter { (_, _, third) -> third == stemBranch.branch }
      .map { it.first }
      .toList()

  /** 宮干 化入對宮  */
  val oppositeTransFours: List<ITransFour.Value>
    get() = transFourFlyMap
      .filter { (_, _, third) -> third == stemBranch.branch.opposite }
      .map { it.first }
      .toList()

  /** 傳回各個流運的宮位名稱對照 , 不傳回本命  */
  val flowHouseMapWithoutBirth: Map<FlowType, House>
    get() = flowHouseMap
      .filter { it.key != FlowType.本命 }
      .toMap().toSortedMap()

  val vageRanges: Pair<Int, Int>
    get() = Pair(rangeFromVage, rangeToVage)

  override fun toString(): String {
    return "[宮位 名稱=$house, 干支=$stemBranch, 星體=$stars]"
  }

  override fun compareTo(other: HouseData): Int {
    return if (this == other) 0 else this.house.compareTo(other.house)
  }

}
