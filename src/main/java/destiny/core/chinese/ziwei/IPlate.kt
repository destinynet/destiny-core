package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.chinese.*
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*


interface IPlate : Serializable {
  /** 名稱  */
  val name: String?

  /** 出生資料 , 陰曆  */
  val chineseDate: ChineseDate

  /** 出生資料 , 陽曆 , 精確到「分、秒」  */
  val localDateTime: ChronoLocalDateTime<*>?

  /** 出生年的干支 (可能是節氣、也可能是陰曆) */
  val year: StemBranch

  /** 出生月份(陰曆 or 節氣) 所轉換的數字  */
  val finalMonthNumForMonthStars: Int

  /** 時辰 */
  val hour: Branch

  /** 出生地點  */
  val location: ILocation?

  /** 地點名稱  */
  val place: String?

  /** 日、夜？ */
  val dayNight: DayNight

  /** 性別  */
  val gender: Gender

  /** 身宮  */
  val bodyHouse: StemBranch

  /** 命主  */
  val mainStar: ZStar

  /** 身主  */
  val bodyStar: ZStar

  /** 五行  */
  val fiveElement: FiveElement

  /** 五行第幾局  */
  val state: Int

  /** 12個宮位，每個宮位內的資料  */
  val houseDataSet: Set<HouseData>

  /**
   * 四化星 的列表
   * 存放著「這顆星」在 [本命、大限、流年、...] 的四化 結果為何
   */
  val transFours: Map<ZStar, Map<FlowType, T4Value>>

  /** 取得此命盤，包含哪些流運資訊  */
  val flowBranchMap: Map<FlowType, StemBranch>

  /** 星體強弱表  */
  val starStrengthMap: Map<ZStar, Int>

  /** 註解列表  */
  val notes: List<String>

  /** 虛歲，每歲的起訖時分 (fromGmt , toGmt)  */
  val vageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>?

  /** 實歲，每歲的起訖時分 (fromGmt , toGmt)  */
  val rageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>?

  val summaries: List<String>


  // =========== 以上 ↑↑ fields for overridden ↑↑ ===========

  /** 命宮  */
  val mainHouse: StemBranch
    get() {
      return houseMap[House.命宮]!!.stemBranch
    }

  /** 宮位名稱 -> 宮位資料  */
  val houseMap: Map<House, HouseData>
    get() {
      return houseDataSet.toList().associateBy { hd -> hd.house }
    }

  /** 星體 -> 宮位資料  */
  val starMap: Map<ZStar, HouseData>
    get() {
      return houseDataSet.flatMap { hd -> hd.stars.map { star -> star to hd } }.toMap()
    }

  /** 宮位地支 -> 星體s  */
  val branchStarMap: Map<Branch, Collection<ZStar>>
    get() {
      return houseDataSet.groupBy { it.stemBranch.branch }.mapValues { it.value.flatMap { hData -> hData.stars } }
    }

  /** 宮位名稱 -> 星體s  */
  val houseStarMap: Map<House, Set<ZStar>>
    get() {
      return houseDataSet.associate { it.house to it.stars }
    }

  /** 命盤中，此地支的宮位名稱是什麼  */
  val branchHouseMap: Map<Branch, House>
    get() {
      return houseDataSet.associate { it.stemBranch.branch to it.getHouse(FlowType.MAIN) }
    }

  /** 取得此地支，在各個流運類型， 宮位名稱 是什麼  */
  val branchFlowHouseMap: Map<Branch, Map<FlowType, House>>
    get() {
      return houseDataSet.associate { houseData ->
        houseData.stemBranch.branch to houseData.flowHouseMap
      }
    }

  /** 每個地支宮位，所代表的大限，「歲數」從何時、到何時 (實歲、虛歲不討論) */
  val flowSectionAgeMap: Map<StemBranch, Pair<Int, Int>>
    get() {
      return houseDataSet.map { hd ->
        hd.stemBranch to hd.ageRanges
      }.sortedBy { (_, pair) -> pair.first }
        .toMap()
    }



  // =========== 以上 ↑↑ functions ↑↑ ===========

  /** 納音 */
  fun getNayin(locale: Locale): String {
    return NaYin.getDesc(getHouseDataOf(House.命宮, FlowType.MAIN)!!.stemBranch, locale)
  }

  /** 傳回虛歲 or 實歲 age map */
  fun getAgeMap(ageType: AgeType) : Map<Int, Pair<GmtJulDay, GmtJulDay>>? {
    return when(ageType) {
      AgeType.VIRTUAL -> vageMap
      AgeType.REAL    -> rageMap
    }
  }

  /** 取得這些星體所在宮位的地支 */
  fun getBranches(vararg stars: ZStar): List<Branch> {
    return stars.mapNotNull { star -> starMap[star]?.stemBranch?.branch }
  }

  /** 取得每個宮位、詳細資料 , 按照 [命宮 , 兄弟 , 夫妻...] 排序下來  */
  fun getSortedHouseDataSet(): Set<HouseData> {
    return TreeSet(houseDataSet)
  }

  /** 取得此地支的宮位資訊  */
  fun getHouseDataOf(branch: Branch): HouseData {
    return houseDataSet.first { houseData -> houseData.stemBranch.branch == branch }
  }

  /** 取得此流運的此宮位 */
  fun getHouseDataOf(house: House, type: FlowType) : HouseData? {
    return houseDataSet.firstOrNull { houseData -> houseData.flowHouseMap[type] == house }
  }

  /**
   * 取得此宮位的內容 , 未指定流運。
   *  [IPlate] 本命盤就查找本命盤的宮位
   *  [IPlateSection] 大限盤就查找大限盤的宮位
   *  ... 以下類推
   *  */
  fun getHouseDataOf(house: House): HouseData? {
    return houseDataSet.firstOrNull { hd -> hd.house == house }
  }

  /** 這顆星在哪個宮位 */
  fun getHouseDataOf(star: ZStar): HouseData? {
    return houseDataSet.firstOrNull { it.stars.contains(star) }
  }

  /** 這顆星在此流運，位於哪個宮位 */
  fun getHouseOf(star: ZStar, flowType: FlowType = FlowType.MAIN): House? {
    return getHouseDataOf(star)?.let { houseData ->
      houseData.flowHouseMap[flowType]
    }
  }

  /** 取得此顆星，的四化列表 */
  fun getTransFourOf(star: ZStar): List<Pair<FlowType, T4Value>> {
    return transFours[star]?.map { (key, value) -> key to value }?.toList() ?: emptyList()
  }

  /** 取得此星，的四化值 (maybe null) */
  fun getTransFourValue(star: ZStar, type: FlowType = FlowType.MAIN): T4Value? {
    return transFours[star]?.let { m -> m[type] }
  }

  /**
   * 取得此四化星，在哪一宮位
   * */
  fun getTransFourHouseOf(value: T4Value, type: FlowType = FlowType.MAIN): HouseData {
    val star = transFours.entries.first { (_, map) ->
      map.any { (t, v) -> t == type && v == value }
    }.key
    return getHouseDataOf(star)!!
  }

  /** 取得在此地支宮位的主星 */
  fun getMainStarsIn(branch: Branch): List<ZStar> {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .filterIsInstance<StarMain>()
  }

  /** 吉星 */
  fun getLuckyStarsIn(branch: Branch): List<ZStar> {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .filterIsInstance<StarLucky>()
  }

  /** 凶星 */
  fun getUnluckyStarsIn(branch: Branch): List<ZStar> {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .filterIsInstance<StarUnlucky>()
  }

  /** 雜曜 */
  fun getMinorStarsIn(branch: Branch): List<ZStar> {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .filterIsInstance<StarMinor>()
  }

  /** 博士12神煞 */
  fun getDoctorStarIn(branch: Branch): ZStar? {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .firstOrNull { it is StarDoctor }
  }

  /** 長生12神煞 */
  fun getLongevityStarIn(branch: Branch): ZStar? {
    return houseDataSet
      .filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .firstOrNull { it is StarLongevity }
  }

  /** 將前 12星 */
  fun getGeneralFrontStarIn(branch: Branch): ZStar? {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .firstOrNull { it is StarGeneralFront }
  }

  /** 歲前 12星 */
  fun getYearFrontStarIn(branch: Branch): ZStar? {
    return houseDataSet.filter { it.stemBranch.branch == branch }
      .flatMap { it.stars }
      .firstOrNull { it is StarYearFront }
  }

}


/** 大運盤 */
interface IPlateSection : IPlate {
  val flowSection: StemBranch
}

/** 流年盤 */
interface IPlateYear : IPlateSection{
  val flowYear: StemBranch
}

/** 流月盤 */
interface IPlateMonth : IPlateYear {
  val flowMonth: StemBranch
}

/** 流日盤 */
interface IPlateDay : IPlateMonth {
  val flowDay: StemBranch
}

/** 流時盤 */
interface IPlateHour : IPlateDay {
  val flowHour: StemBranch
}
