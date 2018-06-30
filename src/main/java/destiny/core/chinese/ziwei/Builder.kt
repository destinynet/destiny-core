/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.chinese.ziwei

import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.personal.IPersonContextModel
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.StemBranch
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.text.MessageFormat
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/** 本命盤  */
class Builder(

  /** 設定資料  */
  val context: IZiweiContext,
  /** 陰曆生日  */
  val chineseDate: ChineseDate,
  /** 性別  */
  val gender: Gender,
  /**
   * 出生年的干支 (可能是節氣、也可能是陰曆)
   * 這與 [chineseDate] 內的 [ChineseDate.year] 或許重複
   *  */
  val year : StemBranch,
  /** 出生月份  */
  val birthMonthNum: Int,
  /** 出生時辰  */
  val birthHour: Branch,
  /** 日、夜？ */
  private val dayNight : DayNight,
  /** 命宮  */
  private val mainHouse: StemBranch,
  /** 身宮  */
  private val bodyHouse: StemBranch,
  /** 命主  */
  private val mainStar: ZStar,
  /** 身主  */
  private val bodyStar: ZStar,
  /** 五行  */
  private val fiveElement: FiveElement,
  /** 五行第幾局  */
  val set: Int,
  /**
   *
   * 干支 -> 宮位 的對照表
   * 類似這樣資料結構 , 12組
   * [子] :
   *  本命 -> 疾厄
   *  大運 -> XX宮
   * [丑] :
   *  本命 -> 財帛
   *  大運 -> XX宮
   * */
  private val stemBranchHouseMap: Map<StemBranch, House>, starBranchMap: Map<ZStar, Branch>,

  /** 星體強弱表  */
  private val starStrengthMap: Map<ZStar, Int>,

  /** 每個地支宮位，所代表的大限，「虛歲」從何時、到何時  */
  val flowBigMap: Map<StemBranch, Pair<Int, Int>>, branchSmallRangesMap: Map<Branch, List<Int>>,

  /** 宮干四化  */
  private val flyMap: Map<StemBranch, Set<Triple<ITransFour.Value, ZStar, Branch>>>,
  /** 歲數 (暫定虛歲），每歲的起訖時分 (in GMT)  */
  private val vageMap: Map<Int, Pair<Double, Double>>?) : Serializable {

  /** 名稱  */
  private var name: String? = null

  /** 陽曆出生日期  */
  private var localDateTime: ChronoLocalDateTime<*>? = null

  /** 出生地點  */
  private var location: ILocation? = null

  /** 地點名稱  */
  private var place: String? = null


  private val houseDataSet: Set<HouseData>

  /**
   * 四化星 的列表
   * 存放著「這顆星」在 [本命、大限、流年、...] 的四化 結果為何
   */
  private val transFourMap = mutableMapOf<ZStar, MutableMap<FlowType, ITransFour.Value>>()

  /**
   * 每個地支，在每種流運，稱為什麼宮位
   */
  private val branchFlowHouseMap = mutableMapOf<Branch, MutableMap<FlowType, House>>()

  /** 計算流運資料  */
  private val flowBranchMap = TreeMap<FlowType, StemBranch>()

  /** 八字命盤  */
  private var personModel: IPersonContextModel? = null

  private val logger = LoggerFactory.getLogger(javaClass)

  /** 註解 builder  */
  private val notesBuilder = mutableListOf<Pair<String, Array<Any>>>()

  /** 註解列表  */
  private var notes: List<String> = mutableListOf()

  val stars: Set<ZStar>
    get() = starStrengthMap.keys

  /** 承上，只傳回「地支」 -> 宮位 的 mapping  */
  val branchHouseMap: Map<Branch, House>
    get() = stemBranchHouseMap.mapKeys { it.key.branch }

  init {

    // 中介 map , 記錄 '[辰] : 天相,紫微' 這樣的 mapping , 此 map 的 key 不一定包含全部地支，因為可能有空宮
    val branchStarsMap: Map<Branch, List<ZStar>> = starBranchMap.entries.groupBy { it.value }.mapValues { it.value.map { it.key } }

    // 哪個地支 裡面 有哪些星體 (可能會有空宮 , 若星體很少的話)
    val branchStarMap: Map<Branch, List<ZStar>?> = Branch.values().map { branch ->
      branch to branchStarsMap[branch] // 可能為 null (空宮) , 故，不加 !!
    }.toMap().toSortedMap()


    /**
     * [branchFlowHouseMap] 儲存類似這樣資料結構 , 12組
     * [子] :
     *  本命 -> 疾厄
     *  大運 -> XX宮
     * [丑] :
     *  本命 -> 財帛
     */
    stemBranchHouseMap.entries.map { e ->
      val m = mutableMapOf(FlowType.本命 to stemBranchHouseMap[e.key]!!)
      e.key.branch to m
    }.toMap().toSortedMap().toMap(branchFlowHouseMap)

    houseDataSet = stemBranchHouseMap.entries.map { e ->
      val sb = e.key
      val house = e.value
      val stars = branchStarMap[sb.branch]?.toSet() ?: emptySet()

      val fromTo = flowBigMap[sb]!! // 必定不為空
      val smallRanges = branchSmallRangesMap[sb.branch]!!
      HouseData(house, sb, stars.toMutableSet(), branchFlowHouseMap[sb.branch]!!, flyMap[sb]!!, fromTo.first, fromTo.second, smallRanges)
    }.toSet()

  } // builder init

  fun withName(name: String): Builder {
    this.name = name
    return this
  }

  fun withLocalDateTime(localDateTime: ChronoLocalDateTime<*>): Builder {
    this.localDateTime = localDateTime
    return this
  }

  fun withLocation(location: ILocation): Builder {
    this.location = location
    return this
  }

  fun withPlace(place: String): Builder {
    this.place = place
    return this
  }

  /** 添加 四化  */
  fun appendTrans4Map(map: Map<Pair<ZStar, FlowType>, ITransFour.Value>): Builder {
    map.forEach { (star, flowType), value ->

      this.transFourMap.computeIfPresent(star) { star1, flowTypeValueMap ->
        flowTypeValueMap.putIfAbsent(flowType, value)
        flowTypeValueMap
      }
      this.transFourMap.putIfAbsent(star, object : TreeMap<FlowType, ITransFour.Value>() {
        init {
          put(flowType, value)
        }
      })
    }
    return this
  }

  /**
   * with 大限宮位對照
   *
   * @param flowBig 哪個大限
   * @param map     地支「在該大限」與宮位的對照表
   */
  fun withFlowBig(flowBig: StemBranch, map: Map<Branch, House>): Builder {
    this.flowBranchMap[FlowType.大限] = flowBig

    map.forEach { branch, _ ->
      branchFlowHouseMap.computeIfPresent(branch) { branch1, m ->
        m[FlowType.大限] = map[branch]!!
        m
      }
    }
    return this
  }

  /**
   * with 流年宮位對照
   *
   * @param flowYear 哪個流年
   * @param map      地支「在該流年」與宮位的對照表
   */
  fun withFlowYear(flowYear: StemBranch, map: Map<Branch, House>): Builder {
    this.flowBranchMap[FlowType.流年] = flowYear
    map.forEach { branch, _ ->
      branchFlowHouseMap.computeIfPresent(branch) { branch1, m ->
        m[FlowType.流年] = map[branch]!!
        m
      }
    }

    // 以流年的 將前12星 取代本命盤中的位置
    // 先檢查，本命盤中，是否已經存在任何 將前12星 , 若有，代表設定要計算
    val showGeneralFront = houseDataSet
      .flatMap { houseData -> houseData.stars }
      .any { it is StarGeneralFront }

    if (showGeneralFront) {
      // 若有的話，就清除掉現有紀錄
      houseDataSet.forEach { houseData -> houseData.stars.removeIf { it is StarGeneralFront } }

      // 接著，以「流年」的將前12星，塞入
      StarGeneralFront.values.map { star ->
        val b = StarGeneralFront.starFuncMap[star]!!.invoke(flowYear.branch)
        Pair(star, b)
      }.forEach { (star, branch) ->
        houseDataSet.filter { it.stemBranch.branch == branch }
          .first { houseData -> houseData.stars.add(star) }
      }
    }

    // 以流年的 歲前12星 取代本命盤中的位置
    // 先檢查，本命盤中，是否已經存在任何 歲前12星。 若有，代表設定要計算
    val showYearFront = houseDataSet
      .flatMap { houseData -> houseData.stars }
      .any { it is StarYearFront }

    if (showYearFront) {
      // 若有的話，就清除掉現有的紀錄
      houseDataSet.forEach { houseData -> houseData.stars.removeIf { star -> star is StarYearFront } }

      // 接著，以「流年」的 歲前12星，塞入
      StarYearFront.values.map { star ->
        val b = StarYearFront.starFuncMap[star]!!.invoke(flowYear.branch)
        Pair(star, b)
      }.forEach { (star, branch) ->
        houseDataSet.filter { it.stemBranch.branch == branch }
          .first { houseData -> houseData.stars.add(star) }
      }

    }

    return this
  }

  /**
   * with 流月宮位對照
   *
   * @param flowMonth 哪個流月
   * @param map       地支「在該流月」與宮位的對照表
   */
  fun withFlowMonth(flowMonth: StemBranch, map: Map<Branch, House>): Builder {
    this.flowBranchMap[FlowType.流月] = flowMonth
    map.forEach { branch, _ ->
      branchFlowHouseMap.computeIfPresent(branch) { branch1, m ->
        m[FlowType.流月] = map[branch]!!
        m
      }
    }
    return this
  }

  /**
   * with 流日宮位對照
   *
   * @param flowDay 哪個流日
   * @param map     地支「在該流日」與宮位的對照表
   */
  fun withFlowDay(flowDay: StemBranch, map: Map<Branch, House>): Builder {
    this.flowBranchMap[FlowType.流日] = flowDay
    map.forEach { branch, _ ->
      branchFlowHouseMap.computeIfPresent(branch) { branch1, m ->
        m[FlowType.流日] = map[branch]!!
        m
      }
    }
    return this
  }

  /**
   * with 流時宮位對照
   *
   * @param flowHour 哪個流時
   * @param map      地支「在該流時」與宮位的對照表
   */
  fun withFlowHour(flowHour: StemBranch, map: Map<Branch, House>): Builder {
    this.flowBranchMap[FlowType.流時] = flowHour
    map.forEach { branch, _ ->
      branchFlowHouseMap.computeIfPresent(branch) { branch1, m ->
        m[FlowType.流時] = map[branch]!!
        m
      }
    }
    return this
  }

  fun withPersonModel(personModel: IPersonContextModel): Builder {
    this.personModel = personModel
    return this
  }

  fun appendNotesBuilders(notesBuilders: List<Pair<String, Array<Any>>>): Builder {
    this.notesBuilder.addAll(notesBuilders)
    return this
  }


  fun withNotes(resourceBundleClazz: Class<*>, locale: Locale): Builder {
    this.notes = buildNotes(resourceBundleClazz, locale)
    return this
  }

  fun withNotes(resourceBundleClazz: Class<*>): Builder {
    this.notes = buildNotes(resourceBundleClazz, Locale.getDefault())
    return this
  }

  private fun buildNotes(resourceBundleClazz: Class<*>, locale: Locale): List<String> {
    return notesBuilder.map { (first, second) ->
      val pattern = ResourceBundle.getBundle(resourceBundleClazz.name, locale).getString(first)
      val note = MessageFormat.format(pattern, *second)
      logger.trace("note : {}", note)
      note
    }
  }

  fun build(): IPlate {
    val plate =
      Plate(name, chineseDate, localDateTime, year , location, place, dayNight , gender, mainHouse, bodyHouse, mainStar, bodyStar,
            fiveElement, set, houseDataSet, transFourMap, branchFlowHouseMap, flowBranchMap, starStrengthMap, notes,
            vageMap)
    return if (personModel == null) {
      plate
    } else {
      PlateWithEightWords(plate , personModel!!)
    }
  }


} // class Builder
