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

  /** 宮位名稱 (本命盤就本命盤的宮位 , 大限盤就大限盤的宮位... 以下類推) */
  val house: House,

  /** 宮位干支  */
  val stemBranch: StemBranch,

  /** 宮位裡面 有哪些星體  */
  val stars: Set<ZStar>,

  /** 此宮位，在各個流運(含本命)，叫什麼宮位  */
  val flowHouseMap: Map<FlowType, House>,

  /** 宮干四化，此宮位，因為什麼星，各飛入哪個宮位(地支)  */
  private val transFourFlyMap: Set<Triple<ITransFour.Value, ZStar, Branch>>,

  /** 大限，從幾歲開始 (inclusive , 不考慮 虛歲 or 實歲）*/
  val rangeFromAge: Int,
  /** 大限，到幾歲截止 (inclusive , 不考慮 虛歲 or 實歲）*/
  val rangeToAge: Int,

  /** 六條小限  */
  val smallRanges: List<Int>) : Serializable, Comparable<HouseData> {

  fun getHouse(flowType: FlowType = FlowType.MAIN) : House {
    return flowHouseMap[flowType] ?: house
  }


  /** 宮干自化 列表 , 0 <= 長度 <= 3 */
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
      .filter { it.key != FlowType.MAIN }
      .toMap().toSortedMap()

  val ageRanges: Pair<Int, Int>
    get() = Pair(rangeFromAge, rangeToAge)

  override fun toString(): String {
    return "[宮位 名稱=$house, 干支=$stemBranch, 星體=$stars]"
  }

  override fun compareTo(other: HouseData): Int {
    return if (this == other) 0 else this.house.compareTo(other.house)
  }

}
