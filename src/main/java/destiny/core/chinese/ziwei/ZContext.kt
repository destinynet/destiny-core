/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo
import destiny.core.calendar.locationOf
import destiny.core.chinese.Branch
import destiny.core.chinese.ITianyi
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import destiny.tools.ILocaleString
import mu.KotlinLogging
import java.io.Serializable
import java.time.LocalTime
import java.util.*

interface IZiweiContext {
  val stars: Collection<ZStar>

  /** 命宮、身宮 演算法  */
  val mainBodyHouseImpl: IMainBodyHouse

  /** 紫微星，在閏月時，該如何處理  */
  val purpleBranchImpl: IPurpleStarBranch

  /**
   * 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [mainBodyHouseImpl] 為占星實作 [MainBodyHouseAstroImpl] , 此值會被忽略
   * 注意，此值可能為 null , 因為若是 '命宮、身宮 演算法' 是占星實作的話 , client 端會把此值填為 null
   * */
  val mainStarsAlgo: MonthAlgo?

  /** 月系星，如何計算月令  */
  val monthStarsAlgo: MonthAlgo

  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  val yearType: YearType

  /** 宮位名字  */
  val houseSeqImpl: IHouseSeq

  /** 上一個月有幾日 */
  val prevMonthDaysImpl: IPrevMonthDays

  /** [StarLucky.天魁] , [StarLucky.天鉞] (貴人) 算法  */
  val tianyiImpl: ITianyi

  /** 火鈴 */
  val fireBell: FireBell

  /** 天馬 */
  val skyHorse : SkyHorse

  /** 天使天傷 */
  val hurtAngel: HurtAngel

  /** 四化設定  */
  val transFourImpl: ITransFour

  /** 廟旺弱陷  */
  val strengthImpl: IStrength

  /** 流年設定  */
  val flowYearImpl: IFlowYear

  /** 流月設定  */
  val flowMonthImpl: IFlowMonth

  /** 流日設定  */
  val flowDayImpl: IFlowDay

  /** 流時設定  */
  val flowHourImpl: IFlowHour

  /** 歲數註解 (西元？ 民國？ or others)  */
  val ageNoteImpls: List<IntAgeNote>

  /** 大限計算方式  */
  val bigRangeImpl: IBigRange

  /** 紅豔 */
  val redBeauty: RedBeauty


  /**
   * 本命盤
   *
   * @param mainAndBody 預先計算過的命宮、身宮地支
   * @param preFinalMonthNumForMainStars 預先計算好的「最終月數」 for 14顆主星 ([StarMain]])
   * @param cycle              陰曆循環 : TODO : // 很多時候是沒有 cycle 的，可能得改為 [Int?] , 但又會影響許多後續計算 (例如 vageMap)
   * @param lunarYear          陰曆的年干支
   * @param solarYear          「節氣」的年干支
   * @param monthBranch        「節氣」的月支
   * @param lunarDays          陰曆日期
   * @param optionalVageMap    預先計算好的虛歲時刻(GMT from / to)
   */
  fun getBirthPlate(mainAndBody: Pair<Branch, Branch>?,
                    preFinalMonthNumForMainStars: Int?,
                    cycle: Int,
                    lunarYear: StemBranch,
                    solarYear: StemBranch,
                    lunarMonth: Int,
                    leapMonth: Boolean,
                    monthBranch: Branch,
                    solarTerms: SolarTerms,
                    lunarDays: Int,
                    hour: Branch,
                    dayNight: DayNight = (if (listOf(Branch.卯, Branch.辰, Branch.巳,
                                                     Branch.午, Branch.未, Branch.申).contains(hour)) DayNight.DAY else DayNight.NIGHT),
                    gender: Gender,
                    optionalVageMap: Map<Int, Pair<Double, Double>>? = null): Builder

  /**
   * @param stars     取得這些星體
   * @param flowType  在[本命、大限、流年]... (之一)
   * @param flowStem  天干為
   * @return 傳回四化 (若有的話)
   */
  fun getTrans4Map(stars: Collection<ZStar>,
                   flowType: FlowType,
                   flowStem: Stem): Map<Pair<ZStar, FlowType>, ITransFour.Value> {
    return stars.map { star ->
      val key = star to flowType
      val value: ITransFour.Value? = transFourImpl.getValueOf(star, flowStem)
      key to value
    }.filter { it.second != null }
      .map { it.first to it.second!! }
      .toMap()
  }

  /** 計算 大限盤  */
  fun getFlowBig(builder: Builder, flowBig: StemBranch): Builder {
    // 在此大限中，每個地支，對應到哪個宮位

    val branchHouseMap = Branch.values().map { branch ->
      val steps = branch.getAheadOf(flowBig.branch)
      val house = houseSeqImpl.prev(House.命宮, steps)
      branch to house
    }.toMap()

    // 大限四化
    val trans4Map = getTrans4Map(stars, FlowType.大限, flowBig.stem)
    return builder
      .withFlowBig(flowBig, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }

  /** 計算 流年盤  */
  fun getFlowYear(builder: Builder, flowBig: StemBranch, flowYear: StemBranch): Builder {
    // 流年命宮
    val yearlyMain = flowYearImpl.getFlowYear(flowYear.branch, builder.birthMonthNum, builder.birthHour)

    val branchHouseMap = Branch.values().map { branch ->
      val steps = branch.getAheadOf(yearlyMain)
      val house = houseSeqImpl.prev(House.命宮, steps)
      branch to house
    }.toMap()

    // 流年四化
    val trans4Map = getTrans4Map(stars, FlowType.流年, flowYear.stem)

    return getFlowBig(builder, flowBig)
      .withFlowYear(flowYear, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }

  /** 計算 流月盤  */
  fun getFlowMonth(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch): Builder {
    // 流月命宮
    val monthlyMain = flowMonthImpl.getFlowMonth(flowYear.branch, flowMonth.branch, builder.birthMonthNum, builder.birthHour)

    val branchHouseMap = Branch.values().map { branch ->
      val steps = branch.getAheadOf(monthlyMain)
      val house = houseSeqImpl.prev(House.命宮, steps)
      branch to house
    }.toMap()

    // 流月四化
    val trans4Map = getTrans4Map(stars, FlowType.流月, flowMonth.stem)

    return getFlowYear(builder, flowBig, flowYear)
      .withFlowMonth(flowMonth, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }

  /** 計算 流日盤  */
  fun getFlowDay(builder: Builder,
                 flowBig: StemBranch,
                 flowYear: StemBranch,
                 flowMonth: StemBranch,
                 flowDay: StemBranch,
                 flowDayNum: Int): Builder {
    // 流月命宮
    val monthlyMain = flowMonthImpl.getFlowMonth(flowYear.branch, flowMonth.branch, builder.birthMonthNum, builder.birthHour)

    // 流日命宮
    val dailyMain = flowDayImpl.getFlowDay(flowDay.branch, flowDayNum, monthlyMain)
    val branchHouseMap = Branch.values().map { branch ->
      val steps = branch.getAheadOf(dailyMain)
      val house = houseSeqImpl.prev(House.命宮, steps)
      branch to house
    }.toMap()

    // 流日四化
    val trans4Map = getTrans4Map(stars, FlowType.流日, flowDay.stem)
    return getFlowMonth(builder, flowBig, flowYear, flowMonth)
      .withFlowDay(flowDay, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }

  /** 流時盤  */
  fun getFlowHour(builder: Builder,
                  flowBig: StemBranch,
                  flowYear: StemBranch,
                  flowMonth: StemBranch,
                  flowDay: StemBranch,
                  flowDayNum: Int,
                  flowHour: StemBranch): Builder {
    // 流月命宮
    val monthlyMain = flowMonthImpl.getFlowMonth(flowYear.branch, flowMonth.branch, builder.birthMonthNum, builder.birthHour)
    // 流日命宮
    val dailyMain = flowDayImpl.getFlowDay(flowDay.branch, flowDayNum, monthlyMain)
    // 流時命宮
    val hourlyMain = flowHourImpl.getFlowHour(flowHour.branch, dailyMain)

    val branchHouseMap = Branch.values().map { branch ->
      val steps = branch.getAheadOf(hourlyMain)
      val house = houseSeqImpl.prev(House.命宮, steps)
      branch to house
    }.toMap()

    // 流時四化
    val trans4Map = getTrans4Map(stars, FlowType.流時, flowHour.stem)

    return getFlowDay(builder, flowBig, flowYear, flowMonth, flowDay, flowDayNum)
      .withFlowHour(flowHour, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }
}

fun YearType.asLocaleString() = object : ILocaleString {
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(IZiweiContext::class.java.name, locale).getString(name)
  }
}

fun YearType.toString(locale: Locale) : String {
  return this.asLocaleString().toString(locale)
}

/** 年系星系  */
enum class YearType  {
  YEAR_LUNAR, // 初一為界
  YEAR_SOLAR; // 立春為界
}



/** [StarUnlucky.火星] ,  [StarUnlucky.鈴星] 設定  */

fun FireBell.asLocaleString() = object : ILocaleString {
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(IZiweiContext::class.java.name, locale).getString(name)
  }
}

fun FireBell.toString(locale : Locale) : String {
  return this.asLocaleString().toString(locale)
}

enum class FireBell {
  /** [StarUnlucky.fun火星_全集] , [StarUnlucky.fun鈴星_全集] : (年支、時支) -> 地支 (福耕老師論點) */
  FIREBELL_COLLECT,

  /** [StarUnlucky.fun火星_全書] , [StarUnlucky.fun鈴星_全書] : 年支 -> 地支 . 中州派 : 火鈴的排法按中州派僅以生年支算落宮，不按生時算落宮  */
  FIREBELL_BOOK;
}

/** 天馬，要用 年馬 還是 月馬 */

enum class SkyHorse {
  YEAR ,
  MONTH;
}

fun SkyHorse.asLocaleString() = object : ILocaleString {
  override fun toString(locale: Locale): String {
    return when(this@asLocaleString) {
      SkyHorse.YEAR -> "年馬"
      SkyHorse.MONTH -> "月馬"
    }
  }
}

fun SkyHorse.toString(locale: Locale) : String {
  return this.asLocaleString().toString(locale)
}


/** [StarMinor.天傷]、 [StarMinor.天使] 計算方式  */
fun HurtAngel.asLocaleString() = object : ILocaleString {
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(IZiweiContext::class.java.name, locale).getString(name)
  }
}

fun HurtAngel.toString(locale: Locale) : String {
  return this.asLocaleString().toString(locale)
}

enum class HurtAngel {
  /** 天傷固定於交友宮 [StarMinor.fun天傷_fixed交友] 、 天使固定疾厄宮 [StarMinor.fun天使_fixed疾厄]  */
  HURT_ANGEL_FIXED,

  /** 陽順陰逆 [StarMinor.fun天傷_陽順陰逆] 、 [StarMinor.fun天使_陽順陰逆]  */
  HURT_ANGEL_YINYANG;
}


/** 紅艷  */
fun RedBeauty.asLocaleString() = object : ILocaleString {
  override fun toString(locale: Locale): String {
    return ResourceBundle.getBundle(IZiweiContext::class.java.name, locale).getString(name)
  }
}

fun RedBeauty.toString(locale: Locale) : String {
  return this.asLocaleString().toString(locale)
}


enum class RedBeauty {
  /** [StarMinor.fun紅艷_甲乙相異]  */
  RED_BEAUTY_DIFF,

  /** [StarMinor.fun紅艷_甲乙相同]  */
  RED_BEAUTY_SAME;
}


/**
 * 純粹「設定」，並不包含 生日、性別、出生地 等資訊
 */
class ZContext(

  override val stars: Collection<ZStar>,

  /** 命宮、身宮 演算法  */
  override val mainBodyHouseImpl: IMainBodyHouse,

  /** 紫微星，在閏月時，該如何處理  */
  override val purpleBranchImpl: IPurpleStarBranch,

  /**
   * 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [mainBodyHouseImpl] 為占星實作 [MainBodyHouseAstroImpl] , 此值會被忽略
   * 注意，此值可能為 null , 因為若是 '命宮、身宮 演算法' 是占星實作的話 , client 端會把此值填為 null
   * */
  override val mainStarsAlgo: MonthAlgo?,

  /** 月系星，如何計算月令  */
  override val monthStarsAlgo: MonthAlgo = MonthAlgo.MONTH_FIXED_THIS,

  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  override val yearType: YearType = YearType.YEAR_LUNAR,

  /** 宮位名字  */
  override val houseSeqImpl: IHouseSeq,

  /** 上一個月有幾日 */
  override val prevMonthDaysImpl: IPrevMonthDays,

  /** 天乙貴人 算法 , 影響 [StarLucky.天魁] , [StarLucky.天鉞]  */
  override val tianyiImpl: ITianyi,

  /** [StarUnlucky.火星] ,  [StarUnlucky.鈴星] 設定  */
  override val fireBell: FireBell = FireBell.FIREBELL_COLLECT,

  /** 天馬 [SkyHorse] 設定 , 年馬 還是 月馬 */
  override val skyHorse: SkyHorse = SkyHorse.YEAR,

  /** [StarMinor.天傷]、 [StarMinor.天使] 計算方式  */
  override val hurtAngel: HurtAngel = HurtAngel.HURT_ANGEL_FIXED,

  /** 四化設定  */
  override val transFourImpl: ITransFour,

  /** 廟旺弱陷  */
  override val strengthImpl: IStrength,

  /** 流年設定  */
  override val flowYearImpl: IFlowYear,

  /** 流月設定  */
  override val flowMonthImpl: IFlowMonth,

  /** 流日設定  */
  override val flowDayImpl: IFlowDay,

  /** 流時設定  */
  override val flowHourImpl: IFlowHour,

  /** 歲數註解 (西元？ 民國？ or others)  */
  override val ageNoteImpls: List<IntAgeNote>,

  /** 大限計算方式  */
  override val bigRangeImpl: IBigRange,

  /** 紅豔 */
  override val redBeauty: RedBeauty = RedBeauty.RED_BEAUTY_DIFF,

  /** 計算虛歲時，需要 [IChineseDate] , 若不提供 , 則無法計算虛歲歲數 (除非有預先算好、傳入) */
  private val chineseDateImpl: IChineseDate? = null,

  /** 虛歲實作 */
  private val intAgeImpl: IIntAge? = null) : IZiweiContext, Serializable {

  /**
   * 本命盤
   *
   * @param mainAndBody 預先計算過的命宮、身宮地支
   * @param preFinalMonthNumForMainStars 預先計算好的「最終月數」 for 14顆主星 ([StarMain]])
   * @param cycle              陰曆循環 : TODO : // 很多時候是沒有 cycle 的，可能得改為 [Int?] , 但又會影響許多後續計算 (例如 vageMap)
   * @param lunarYear          陰曆的年干支
   * @param solarYear          「節氣」的年干支
   * @param monthBranch        「節氣」的月支
   * @param lunarDays          陰曆日期
   * @param optionalVageMap    預先計算好的虛歲時刻(GMT from / to)
   */
  override fun getBirthPlate(mainAndBody: Pair<Branch, Branch>?,
                             preFinalMonthNumForMainStars: Int?,
                             cycle: Int,
                             lunarYear: StemBranch,
                             solarYear: StemBranch,
                             lunarMonth: Int,
                             leapMonth: Boolean,
                             monthBranch: Branch,
                             solarTerms: SolarTerms,
                             lunarDays: Int,
                             hour: Branch,
                             dayNight: DayNight,
                             gender: Gender,
                             optionalVageMap: Map<Int, Pair<Double, Double>>?): Builder {
    // 排盤之中所產生的註解 , Pair<KEY , parameters>
    val notesBuilders = mutableListOf<Pair<String, Array<Any>>>()


    /**
     * 14主星的「月數」 [IZiweiContext.mainStarsAlgo]
     * 與「月系星」的月數 [IZiweiContext.monthStarsAlgo]
     * 可以分開設定 , 故，這裡產生兩個 finalMonthNum
     */

    /**
     * 最終要計算的「月份」數字 , for 主星 .
     * 若已經有了值（通常來自 傳統計算命宮、身宮的演算法 [MainBodyHouseTradImpl] ) , 代表也一定預先計算了命宮、身宮 [mainAndBody]
     * 若沒有預先計算的值 , 代表其命宮、身宮演算法來自占星 [MainBodyHouseAstroImpl] ,
     *    紫微星的算法，有依據「五行局數」. 而「五行局數」來自命宮. 若命宮採用上升星座，就與「月數」「節氣」無關。
     * */
    val finalMonthNumForMainStars: Int =
      preFinalMonthNumForMainStars ?: IFinalMonthNumber.getFinalMonthNumber(lunarMonth, leapMonth, monthBranch,
                                                                            lunarDays, mainStarsAlgo)

    // 最終要計算的「月份」數字 , for 月系星
    val finalMonthNumForMonthStars =
      IFinalMonthNumber.getFinalMonthNumber(lunarMonth, leapMonth, monthBranch, lunarDays, monthStarsAlgo)

    logger.trace("finalMonthNumForMainStars = {}", finalMonthNumForMainStars)


    if (mainBodyHouseImpl is MainBodyHouseTradImpl   // 如果主星是以傳統方式計算
      && lunarMonth != finalMonthNumForMainStars             // 而且最終月份數不一樣
    ) {
      logger.warn("命身宮設定為 : {} , 造成原本月份為 {}{}月，以月數 {} 取代之", mainStarsAlgo, if (leapMonth) "閏" else "", lunarMonth,
                  finalMonthNumForMainStars)
      when {
        MonthAlgo.MONTH_SOLAR_TERMS === mainStarsAlgo -> // 命身宮用節氣計算，故用 {0}月={1} 而非 {2}{3}月
          notesBuilders.add(Pair("mainStarsAlgo_month_solar_terms",
                                 arrayOf(monthBranch, finalMonthNumForMainStars, if (leapMonth) "閏" else "",
                                         lunarMonth)))
        MonthAlgo.MONTH_LEAP_NEXT === mainStarsAlgo -> // 命身宮於閏{0}月視為下月={1}
          notesBuilders.add(Pair("mainStarsAlgo_month_leap_next", arrayOf<Any>(lunarMonth, finalMonthNumForMainStars)))
        MonthAlgo.MONTH_LEAP_SPLIT15 === mainStarsAlgo -> // 命身宮於閏月月中切割,故用{0}月
          notesBuilders.add(Pair("mainStarsAlgo_month_leap_split15", arrayOf<Any>(finalMonthNumForMainStars)))
      }
    }

    if (lunarMonth != finalMonthNumForMonthStars) {
      logger.warn("月系星的設定為 : {} , 造成原本月份為 {}{}月，以月數 {} 取代之", monthStarsAlgo, if (leapMonth) "閏" else "", lunarMonth,
                  finalMonthNumForMonthStars)
      when {
        MonthAlgo.MONTH_SOLAR_TERMS === monthStarsAlgo -> // 月系星以節氣計算，故月用 {0}={1} 而非 {2}
          notesBuilders.add(Pair("monthStarsAlgo_solar_month",
                                 arrayOf(monthBranch, finalMonthNumForMonthStars, if (leapMonth) "閏" else "",
                                         lunarMonth)))
        MonthAlgo.MONTH_LEAP_NEXT === monthStarsAlgo -> // 月系星於閏{0}月視為下月{1}
          notesBuilders.add(
            Pair("monthStarsAlgo_month_leap_next", arrayOf<Any>(lunarMonth, finalMonthNumForMonthStars)))
        MonthAlgo.MONTH_LEAP_SPLIT15 === monthStarsAlgo -> // 月系星於閏月月中切割,故用{0}月
          notesBuilders.add(Pair("monthStarsAlgo_month_leap_split15", arrayOf<Any>(finalMonthNumForMonthStars)))
      }
    }

    // 寅 的天干
    val stemOf寅 = Ziwei.getStemOf寅(lunarYear.stem)

    // 命宮所參考的「年干」，同時依據「年系星」的類型來決定
    val year: StemBranch = if (yearType == YearType.YEAR_LUNAR) lunarYear else solarYear

    // 命宮地支
    val mainBranch = mainAndBody?.first ?: Ziwei.getMainHouseBranch(finalMonthNumForMainStars, hour)
    val mainHouse = Ziwei.getStemBranchOf(mainBranch, stemOf寅)
    logger.trace("命宮在 : {}", mainHouse)

    // 身宮地支
    val bodyBranch = mainAndBody?.second ?: Ziwei.getBodyHouseBranch(finalMonthNumForMainStars, hour)
    val bodyHouse = Ziwei.getStemBranchOf(bodyBranch, stemOf寅)
    logger.trace("身宮在 : {}", bodyHouse)

    // 取命主 : 命宮所在地支安星
    val mainStar = Ziwei.getMainStar(mainHouse.branch)

    // 取身主 : 以出生年之地支安星
    val bodyStar = Ziwei.getBodyStar(lunarYear.branch)


    // 12宮 順序 以及 名稱
    //val houseSeqImpl = context.houseSeqImpl

    /**
     * 由命宮干支的納音 (NaYin) 來決定 五行+局數(Int) , 例如 水 2 局
     * */
    val (五行, 五行局) = Ziwei.getMainDesc(mainHouse)

    // 干支 -> 宮位 的 mapping
    val branchHouseMap: Map<StemBranch, House> = houseSeqImpl.houses.map { house ->
      // 要計算的宮位，比命宮，超前幾步
      val steps = houseSeqImpl.getAheadOf(house, House.命宮)
      val sb = Ziwei.getStemBranchOf(mainHouse.branch.prev(steps), stemOf寅)
      sb to house
    }.toMap()

    // 地支 <-> 宮位 的 雙向 mapping
    val branchHouseBiMap: BiMap<Branch, House> = HashBiMap.create<Branch, House>()
    branchHouseMap.forEach { (sb, house) -> branchHouseBiMap[sb.branch] = house }

    // 為了某些流派閏月的考量 , 須在此求出「上個月」有幾天 , 才能求出紫微星
    val prevMonthDays = if (leapMonth) prevMonthDaysImpl.getPrevMonthDays(cycle, lunarYear, lunarMonth, true) else 0

    // 什麼星，在什麼地支
    val starBranchMap: Map<ZStar, Branch> = stars.map { star ->
      val branch: Branch? =
        HouseFunctions.map[star]?.getBranch(lunarYear, solarYear, monthBranch, finalMonthNumForMonthStars, solarTerms,
                                            lunarDays, hour, 五行局, gender, leapMonth, prevMonthDays, mainBranch,
                                            this)
      star to branch
    }.filter { (_ , branch) -> branch != null }
      .map { (star , branch) -> star to branch!! }
      .toMap()

    logger.debug("stars = {}", stars)
    logger.debug("starBranchMap = {}", starBranchMap)

    // 本命四化 : 四化要依據 陰曆初一 還是 節氣立春 劃分
    val trans4Map: Map<Pair<ZStar, FlowType>, ITransFour.Value>
    if (yearType == YearType.YEAR_LUNAR) {
      trans4Map = getTrans4Map(stars, FlowType.本命, lunarYear.stem)
    } else {
      // 立春分年
      trans4Map = getTrans4Map(stars, FlowType.本命, solarYear.stem)
      if (lunarYear !== solarYear) {
        // 如果年 與 陰曆年不同
        // solar_year=年系星立春為界，故年用 {0} 而非 {1}
        notesBuilders.add(Pair("solar_year", arrayOf<Any>(solarYear, lunarYear)))
      }
    }
    logger.debug("transFourImpl = {} , title = {}" , transFourImpl.javaClass.simpleName , transFourImpl.toString(Locale.getDefault()))
    logger.debug("trans4Map = {}" , trans4Map)

    // 宮干四化 : 此宮位，因為什麼星，各飛入哪個宮位(地支)
    // 參考 : http://www.fate123.com.tw/fate-teaching/fate-lesson-5.2.asp
    val flyMap: Map<StemBranch, Set<Triple<ITransFour.Value, ZStar, Branch>>> =
      branchHouseMap.keys.map { sb: StemBranch ->

        val set = ITransFour.Value.values().map { value ->
          val flyStar: ZStar = transFourImpl.getStarOf(sb.stem, value)
          value to flyStar
        }.filter { (_ , flyStar) ->
          starBranchMap[flyStar] != null
        }.map { (value , flyStar) ->
          Triple(value, flyStar, starBranchMap.getValue(flyStar))
        }.toSet()

        sb to set

      }.toMap()


    // 星體強弱表
    val starStrengthMap: Map<ZStar, Int> = stars
      .map { star ->
        val strength: Int? = strengthImpl.getStrengthOf(star, starBranchMap.getValue(star))
        star to strength
      }.filter { it.second != null }
      .map { it.first to it.second!! }
      .toMap()

    val chineseDate = ChineseDate(cycle, lunarYear, lunarMonth, leapMonth, lunarDays)

    // 計算每個地支的 大限 起訖 「虛歲」時刻
    val flowBigVageMap = bigRangeImpl.getSortedFlowBigVageMap(branchHouseBiMap, 五行局, lunarYear, gender, houseSeqImpl)

    // 小限 mapping
    val branchSmallRangesMap: Map<Branch, List<Int>> = Branch.values().map { branch ->
      branch to ISmallRange.getRanges(branch, lunarYear.branch, gender)
    }.toMap()

    /**
     * 歲數 map , 2018-06-03 改 optional . 因為不想在 core 內 , depend on ChineseDateCalendricaImpl
     * 不然就得開發另一套非常簡易的 [IIntAge] 在此使用
     */
    val vageMap: Map<Int, Pair<Double, Double>>? = optionalVageMap ?: {
      if (chineseDateImpl != null && intAgeImpl != null) {
        val gmt = chineseDateImpl.getYangDate(chineseDate).atTime(LocalTime.NOON)
        val gmtJulDay = TimeTools.getGmtJulDay(gmt)
        intAgeImpl.getRangesMap(gender, gmtJulDay, locationOf(Locale.UK), 1, 130) // 參數沒有 loc 資訊，時間傳回 GMT , 就以 UK 作為地點
      } else {
        null
      }
    }.invoke()

    return Builder(this, chineseDate, gender, year , finalMonthNumForMonthStars, hour, dayNight , mainHouse, bodyHouse, mainStar,
                   bodyStar, 五行, 五行局, branchHouseMap, starBranchMap, starStrengthMap, flowBigVageMap,
                   branchSmallRangesMap, flyMap, vageMap)
      .appendNotesBuilders(notesBuilders)
      .appendTrans4Map(trans4Map)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ZContext) return false

    if (stars != other.stars) return false
    if (mainBodyHouseImpl != other.mainBodyHouseImpl) return false
    if (purpleBranchImpl != other.purpleBranchImpl) return false
    if (mainStarsAlgo != other.mainStarsAlgo) return false
    if (monthStarsAlgo != other.monthStarsAlgo) return false
    if (yearType != other.yearType) return false
    if (houseSeqImpl != other.houseSeqImpl) return false
    if (prevMonthDaysImpl != other.prevMonthDaysImpl) return false
    if (tianyiImpl != other.tianyiImpl) return false
    if (fireBell != other.fireBell) return false
    if (skyHorse != other.skyHorse) return false
    if (hurtAngel != other.hurtAngel) return false
    if (transFourImpl != other.transFourImpl) return false
    if (strengthImpl != other.strengthImpl) return false
    if (flowYearImpl != other.flowYearImpl) return false
    if (flowMonthImpl != other.flowMonthImpl) return false
    if (flowDayImpl != other.flowDayImpl) return false
    if (flowHourImpl != other.flowHourImpl) return false
    if (ageNoteImpls != other.ageNoteImpls) return false
    if (bigRangeImpl != other.bigRangeImpl) return false
    if (redBeauty != other.redBeauty) return false
    if (chineseDateImpl != other.chineseDateImpl) return false
    if (intAgeImpl != other.intAgeImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = stars.hashCode()
    result = 31 * result + mainBodyHouseImpl.hashCode()
    result = 31 * result + purpleBranchImpl.hashCode()
    result = 31 * result + (mainStarsAlgo?.hashCode() ?: 0)
    result = 31 * result + monthStarsAlgo.hashCode()
    result = 31 * result + yearType.hashCode()
    result = 31 * result + houseSeqImpl.hashCode()
    result = 31 * result + prevMonthDaysImpl.hashCode()
    result = 31 * result + tianyiImpl.hashCode()
    result = 31 * result + fireBell.hashCode()
    result = 31 * result + skyHorse.hashCode()
    result = 31 * result + hurtAngel.hashCode()
    result = 31 * result + transFourImpl.hashCode()
    result = 31 * result + strengthImpl.hashCode()
    result = 31 * result + flowYearImpl.hashCode()
    result = 31 * result + flowMonthImpl.hashCode()
    result = 31 * result + flowDayImpl.hashCode()
    result = 31 * result + flowHourImpl.hashCode()
    result = 31 * result + ageNoteImpls.hashCode()
    result = 31 * result + bigRangeImpl.hashCode()
    result = 31 * result + redBeauty.hashCode()
    result = 31 * result + (chineseDateImpl?.hashCode() ?: 0)
    result = 31 * result + (intAgeImpl?.hashCode() ?: 0)
    return result
  }


  companion object {
    val logger = KotlinLogging.logger {  }
  }

} // ZContext
