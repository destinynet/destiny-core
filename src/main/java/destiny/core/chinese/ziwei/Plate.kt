/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.StemBranch
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime


/** 排盤結果 , 作為 DTO  */
open class Plate (

  /** 名稱  */
  override val name: String?,

  /** 出生資料 , 陰曆  */
  override val chineseDate: ChineseDate,

  /** 出生資料 , 陽曆 , 精確到「分、秒」  */
  override val localDateTime: ChronoLocalDateTime<*>?,

  /** 出生年的干支 (可能是節氣、也可能是陰曆) */
  override val year: StemBranch,

  /** 出生月份(陰曆 or 節氣) 所轉換的數字  */
  override val finalMonthNumForMonthStars: Int,

  /** 時辰 */
  override val hour: Branch,

  /** 出生地點  */
  override val location: ILocation?,

  /** 地點名稱  */
  override val place: String?,

  /** 日、夜？ */
  override val dayNight: DayNight,

  /** 性別  */
  override val gender: Gender,

  /** 身宮  */
  override val bodyHouse: StemBranch,

  /** 命主  */
  override val mainStar: ZStar,

  /** 身主  */
  override val bodyStar: ZStar,

  /** 五行  */
  override val fiveElement: FiveElement,

  /** 五行第幾局  */
  override val state: Int,

  /** 12個宮位，每個宮位內的資料  */
  override val houseDataSet: Set<HouseData>,

  /**
   * 四化星 的列表
   * 存放著「這顆星」在 [本命、大限、流年、...] 的四化 結果為何
   */
  override val transFours: Map<ZStar, Map<FlowType, ITransFour.Value>>,

  /** 取得此命盤，包含哪些流運資訊  */
  override val flowBranchMap: Map<FlowType, StemBranch>,

  /** 星體強弱表  */
  override val starStrengthMap: Map<ZStar, Int>,

  /** 註解列表  */
  override val notes: List<String>,

  /** 虛歲，每歲的起訖時分 (fromGmt , toGmt)  */
  override val vageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>?,

  /** 實歲，每歲的起訖時分 (fromGmt , toGmt)  */
  override val rageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>?,

  override val summaries: List<String>
) : IPlate, Serializable {




//  /** 大運 */
//  override fun withFlowSection(flowSection: StemBranch, appendBranchHouseMap: Map<Branch, House>): IPlate {
//    val newFlowBranchMap: Map<FlowType, StemBranch> = flowBranchMap.toMutableMap().apply {
//      put(FlowType.SECTION, flowSection)
//    }.toMap()
//
//    val newBranchFlowHouseMap: Map<Branch, Map<FlowType, House>> = branchFlowHouseMap.map { (branch: Branch, m: Map<FlowType, House>) ->
//      val newHouse: House = appendBranchHouseMap[branch]!!
//      val newMap = m.toMutableMap().apply {
//        put(FlowType.SECTION, newHouse)
//      }.toMap()
//      branch to newMap
//    }.toMap()
//
//
//    val newHouseDataSet = houseDataSet.map { hd: HouseData ->
//      hd.copy(flowHouseMap = newBranchFlowHouseMap.getValue(hd.stemBranch.branch))
//    }.toSet()
//
//    return copy(flowBranchMap = newFlowBranchMap, branchFlowHouseMap = newBranchFlowHouseMap, houseDataSet = newHouseDataSet)
//  }

//  /** 流年 */
//  override fun withFlowYear(flowYear: StemBranch, branchHouseMap: Map<Branch, House>): IPlate {
//    val newFlowBranchMap: Map<FlowType, StemBranch> = flowBranchMap.toMutableMap().apply {
//      put(FlowType.YEAR, flowYear)
//    }.toMap()
//
//    val newBranchFlowHouseMap = branchFlowHouseMap.map { (branch: Branch, m: Map<FlowType, House>) ->
//      val newHouse: House = branchHouseMap[branch]!!
//      val newMap = m.toMutableMap().apply {
//        put(FlowType.YEAR, newHouse)
//      }.toMap()
//      branch to newMap
//    }.toMap()
//
//    // 以流年的 將前12星 取代本命盤中的位置
//    // 先檢查，本命盤中，是否已經存在任何 將前12星 , 若有，代表設定要計算
//    val showGeneralFront: Boolean = houseDataSet
//      .flatMap { houseData -> houseData.stars }
//      .any { it is StarGeneralFront }
//
//    if (showGeneralFront) {
//      // 若有的話，就清除掉現有紀錄
//      houseDataSet.forEach { houseData -> houseData.stars.removeIf { it is StarGeneralFront } }
//
//      // 接著，以「流年」的將前12星，塞入
//      StarGeneralFront.values.map { star ->
//        val b = StarGeneralFront.starFuncMap.getValue(star).invoke(flowYear.branch)
//        Pair(star, b)
//      }.forEach { (star, branch) ->
//        houseDataSet
//          .first { it.stemBranch.branch == branch }
//          .also { houseData -> houseData.stars.add(star) }
//      }
//    }
//
//    // 以流年的 歲前12星 取代本命盤中的位置
//    // 先檢查，本命盤中，是否已經存在任何 歲前12星。 若有，代表設定要計算
//    val showYearFront = houseDataSet
//      .flatMap { houseData -> houseData.stars }
//      .any { it is StarYearFront }
//
//    if (showYearFront) {
//      // 若有的話，就清除掉現有的紀錄
//      houseDataSet.forEach { houseData -> houseData.stars.removeIf { star -> star is StarYearFront } }
//
//      // 接著，以「流年」的 歲前12星，塞入
//      StarYearFront.values.map { star ->
//        val b = StarYearFront.starFuncMap.getValue(star).invoke(flowYear.branch)
//        Pair(star, b)
//      }.forEach { (star, branch) ->
//        houseDataSet
//          .first { it.stemBranch.branch == branch }
//          .also { houseData -> houseData.stars.add(star) }
//      }
//    }
//
//    val newHouseDataSet = houseDataSet.map { hd: HouseData ->
//      hd.copy(flowHouseMap = newBranchFlowHouseMap.getValue(hd.stemBranch.branch))
//    }.toSet()
//
//    return copy(flowBranchMap = newFlowBranchMap, branchFlowHouseMap = newBranchFlowHouseMap, houseDataSet = newHouseDataSet)
//  }
//
//  /** 流月 */
//  override fun withFlowMonth(flowMonth: StemBranch, branchHouseMap: Map<Branch, House>): IPlate {
//    val newFlowBranchMap: Map<FlowType, StemBranch> = flowBranchMap.toMutableMap().apply {
//      put(FlowType.MONTH, flowMonth)
//    }.toMap()
//
//    val newBranchFlowHouseMap = branchFlowHouseMap.map { (branch: Branch, m: Map<FlowType, House>) ->
//      val newHouse: House = branchHouseMap[branch]!!
//      val newMap = m.toMutableMap().apply {
//        put(FlowType.MONTH, newHouse)
//      }.toMap()
//      branch to newMap
//    }.toMap()
//
//    val newHouseDataSet = houseDataSet.map { hd: HouseData ->
//      hd.copy(flowHouseMap = newBranchFlowHouseMap.getValue(hd.stemBranch.branch))
//    }.toSet()
//
//    return copy(flowBranchMap = newFlowBranchMap, branchFlowHouseMap = newBranchFlowHouseMap, houseDataSet = newHouseDataSet)
//  }
//
//  /** 流日 */
//  override fun withFlowDay(flowDay: StemBranch, branchHouseMap: Map<Branch, House>): IPlate {
//    val newFlowBranchMap: Map<FlowType, StemBranch> = flowBranchMap.toMutableMap().apply {
//      put(FlowType.DAY, flowDay)
//    }.toMap()
//
//    val newBranchFlowHouseMap = branchFlowHouseMap.map { (branch: Branch, m: Map<FlowType, House>) ->
//      val newHouse: House = branchHouseMap[branch]!!
//      val newMap = m.toMutableMap().apply {
//        put(FlowType.DAY, newHouse)
//      }.toMap()
//      branch to newMap
//    }.toMap()
//
//    val newHouseDataSet = houseDataSet.map { hd: HouseData ->
//      hd.copy(flowHouseMap = newBranchFlowHouseMap.getValue(hd.stemBranch.branch))
//    }.toSet()
//
//    return copy(flowBranchMap = newFlowBranchMap, branchFlowHouseMap = newBranchFlowHouseMap, houseDataSet = newHouseDataSet)
//  }
//
//  /** 流時 */
//  override fun withFlowHour(flowHour: StemBranch, branchHouseMap: Map<Branch, House>): IPlate {
//    val newFlowBranchMap: Map<FlowType, StemBranch> = flowBranchMap.toMutableMap().apply {
//      put(FlowType.HOUR, flowHour)
//    }.toMap()
//
//    val newBranchFlowHouseMap = branchFlowHouseMap.map { (branch: Branch, m: Map<FlowType, House>) ->
//      val newHouse: House = branchHouseMap[branch]!!
//      val newMap = m.toMutableMap().apply {
//        put(FlowType.HOUR, newHouse)
//      }.toMap()
//      branch to newMap
//    }.toMap()
//
//    val newHouseDataSet = houseDataSet.map { hd: HouseData ->
//      hd.copy(flowHouseMap = newBranchFlowHouseMap.getValue(hd.stemBranch.branch))
//    }.toSet()
//
//    return copy(flowBranchMap = newFlowBranchMap, branchFlowHouseMap = newBranchFlowHouseMap, houseDataSet = newHouseDataSet)
//  }
//
//  /** 流運四化 */
//  override fun appendTrans4Map(trans4Map: Map<Pair<ZStar, FlowType>, ITransFour.Value>): IPlate {
//
//    val newTransFours: Map<ZStar, Map<FlowType, ITransFour.Value>> = transFours.toMutableMap().apply {
//      trans4Map.forEach { (starFlowType: Pair<ZStar, FlowType>, value: ITransFour.Value) ->
//        val (star, flowType) = starFlowType
//
//        val starMap: Map<FlowType, ITransFour.Value>? = this[star]
//        if (starMap != null) {
//          this[star] = starMap.toMutableMap().apply {
//            put(flowType, value)
//          }
//        } else {
//          put(star, mapOf(flowType to value))
//        }
//      }
//    }.toMap()
//
//    return copy(transFours = newTransFours)
//  }
}

/** 大運盤 */
open class PlateWithSection(p : IPlate ,
                       override val flowSection: StemBranch,
                       override val branchHouseMap: Map<Branch, House>,
                       houseDataSet: Set<HouseData>,
                       transFours: Map<ZStar, Map<FlowType, ITransFour.Value>>
                       ) : Plate(p.name , p.chineseDate, p.localDateTime, p.year, p.finalMonthNumForMonthStars, p.hour, p.location, p.place, p.dayNight, p.gender,
                                 p.bodyHouse , p.mainStar, p.bodyStar, p.fiveElement, p.state, houseDataSet, transFours,
                                 p.flowBranchMap, p.starStrengthMap, p.notes, p.vageMap, p.rageMap,p.summaries), IPlateSection, Serializable

/** 流年盤 */
open class PlateWithYear(p: IPlateSection,
                    override val flowYear: StemBranch,
                    branchHouseMap: Map<Branch, House>,
                    houseDataSet: Set<HouseData>,
                    transFours: Map<ZStar, Map<FlowType, ITransFour.Value>>
                    ) : PlateWithSection(p, p.flowSection, branchHouseMap, houseDataSet, transFours) , IPlateYear, Serializable

/** 流月盤 */
open class PlateWithMonth(p : IPlateYear,
                          override val flowMonth: StemBranch,
                          branchHouseMap: Map<Branch, House>,
                          houseDataSet: Set<HouseData>,
                          transFours: Map<ZStar, Map<FlowType, ITransFour.Value>>
                          ) : PlateWithYear(p, p.flowYear, branchHouseMap, houseDataSet, transFours), IPlateMonth, Serializable

/** 流日盤 */
open class PlateWithDay(p: IPlateMonth,
                        override val flowDay: StemBranch,
                        branchHouseMap: Map<Branch, House>,
                        houseDataSet: Set<HouseData>,
                        transFours: Map<ZStar, Map<FlowType, ITransFour.Value>>) : PlateWithMonth(p, p.flowMonth, branchHouseMap, houseDataSet, transFours), IPlateDay, Serializable

/** 流時盤 */
class PlateWithHour(p : IPlateDay,
                    override val flowHour: StemBranch,
                    branchHouseMap: Map<Branch, House>,
                    houseDataSet: Set<HouseData>,
                    transFours: Map<ZStar, Map<FlowType, ITransFour.Value>>) : PlateWithDay(p, p.flowDay, branchHouseMap, houseDataSet, transFours) , IPlateHour, Serializable
