/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.Location
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.StemBranch
import org.apache.commons.lang3.StringUtils
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/** 排盤結果 , 作為 DTO  */
/** 命盤 */
open class Plate(
  /** 名稱  */
  val name: String?,
  /** 出生資料 , 陰曆  */
  val chineseDate: ChineseDate,
  /** 出生資料 , 陽曆 , 精確到「分、秒」  */
  val localDateTime: ChronoLocalDateTime<*>?,
  /** 出生地點  */
  val location: Location?,
  /** 地點名稱  */
  val place: String?,
  /** 性別  */
  val gender: Gender,
  /** 命宮  */
  val mainHouse: StemBranch,
  /** 身宮  */
  val bodyHouse: StemBranch,
  /** 命主  */
  val mainStar: ZStar,
  /** 身主  */
  val bodyStar: ZStar,
  /** 五行  */
  val fiveElement: FiveElement,
  /** 五行第幾局  */
  val set: Int,

  /** 12個宮位，每個宮位內的資料  */
  private val houseDataSet: Set<HouseData>,

  /** 四化星 的列表
   * 存放著「這顆星」在 [本命、大限、流年、...] 的四化 結果為何
   */
  /** 取得 星體的四化列表  */
  val tranFours: Map<ZStar, Map<FlowType, ITransFour.Value>>// = new HashMap<>();
  ,
  /** 取得此地支，在各個流運類型， 宮位名稱 是什麼  */
  val branchFlowHouseMap: Map<Branch, Map<FlowType, House>>,

  /** 取得此命盤，包含哪些流運資訊  */
  val flowBranchMap: Map<FlowType, StemBranch>,

  /** 星體強弱表  */
  val starStrengthMap: Map<ZStar, Int>,

  /** 註解列表  */
  val notes: List<String>,

  /** 虛歲，每歲的起訖時分 (fromGmt , toGmt)  */
  val vageMap: Map<Int, Pair<Double, Double>>) : Serializable {

  val nameOpt: Optional<String>
    get() = Optional.ofNullable(name)

  val localDateTimeOpt: Optional<ChronoLocalDateTime<*>>
    get() = Optional.ofNullable(localDateTime)

  val placeOpt: Optional<String>
    get() = if (StringUtils.isBlank(place)) {
      Optional.empty()
    } else {
      Optional.of(place!!)
    }

  /** 宮位名稱 -> 宮位資料  */
  val houseMap: Map<House, HouseData>
    get() = houseDataSet.toList().map { hd -> hd.house to hd }.toMap()
  //get() = houseDataSet.stream().collect<Map<House, HouseData>, Any>(Collectors.toMap(Function<HouseData, House> { it.getHouse() }) { hd -> hd })

  /** 星體 -> 宮位資料  */
  val starMap: Map<ZStar, HouseData>
    get() = houseDataSet
      .flatMap { hd -> hd.stars.map { star -> star to hd } }
      .toMap()

  /** 宮位地支 -> 星體s  */
  val branchStarMap: Map<Branch, Collection<ZStar>>
    get() = houseDataSet.groupBy { it.stemBranch.branch }.mapValues { it.value.flatMap { it.stars } }

  /** 宮位名稱 -> 星體s  */
  val houseStarMap: Map<House, Set<ZStar>>
    get() = houseDataSet.map { it -> it.house to it.stars }.toMap()

  /** 本命盤中，此地支的宮位名稱是什麼  */
  val branchHouseMap: Map<Branch, House>
    get() = branchFlowHouseMap.map { it -> it.key to it.value[FlowType.本命]!! }.toMap()

  /** 取得每個宮位、詳細資料 , 按照 [命宮 , 兄弟 , 夫妻...] 排序下來  */
  fun getSortedHouseDataSet(): Set<HouseData> {
    return TreeSet(houseDataSet)
  }

  /** 取得此地支的宮位資訊  */
  fun getHouseDataOf(branch: Branch): HouseData {
    return houseDataSet.first { houseData -> houseData.stemBranch.branch == branch }
  }

  fun getHouseDataOf(house: House): HouseData? {
    return houseDataSet.firstOrNull { it.house == house }
  }

  /** 這顆星在哪個宮位 */
  fun getHouseDataOf(star: ZStar): HouseData? {
    return houseDataSet.firstOrNull { it.stars.contains(star) }
  }

  /** 取得此顆星，的四化列表  */
  fun getTransFourOf(star: ZStar): List<Pair<FlowType, ITransFour.Value>> {
    return tranFours[star]?.map { (key, value) -> key to value }?.toList() ?: emptyList()
  }

  /** 取得在此地支宮位的主星  */
  fun getMainStarsIn(branch: Branch): List<ZStar> {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .filter { it is StarMain }
  }

  /** 吉星  */
  fun getLuckyStarsIn(branch: Branch): List<ZStar> {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .filter { it is StarLucky }
  }

  /** 凶星  */
  fun getUnluckyStarsIn(branch: Branch): List<ZStar> {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .filter { it is StarUnlucky }
  }

  /** 雜曜  */
  fun getMinorStarsIn(branch: Branch): List<ZStar> {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .filter { it is StarMinor }
  }

  /** 博士12神煞  */
  fun getDoctorStarIn(branch: Branch): ZStar? {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .firstOrNull { it is StarDoctor }
  }

  /** 長生12神煞  */
  fun getLongevityStarIn(branch: Branch): ZStar? {
    return houseDataSet
      .filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .firstOrNull { it is StarLongevity }
  }

  /** 將前 12星  */
  fun getGeneralFrontStarIn(branch: Branch): ZStar? {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .firstOrNull { it is StarGeneralFront }
  }

  /** 歲前 12星  */
  fun getYearFrontStarIn(branch: Branch): ZStar? {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .firstOrNull { it is StarYearFront }
    //.let { zStar -> zStar as StarYearFront }
  }
}
