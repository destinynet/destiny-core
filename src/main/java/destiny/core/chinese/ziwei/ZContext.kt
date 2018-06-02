/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import destiny.core.Descriptive
import destiny.core.Gender
import destiny.core.IntAgeNote
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo
import destiny.core.chinese.Branch
import destiny.core.chinese.ITianyi
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch
import org.slf4j.LoggerFactory
import java.io.Serializable
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
  val mainStarsAlgo: IFinalMonthNumber.MonthAlgo?

  /** 月系星，如何計算月令  */
  val monthStarsAlgo: IFinalMonthNumber.MonthAlgo

  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  val yearType: YearType

  /** 宮位名字  */
  val houseSeqImpl: IHouseSeq

  /** 上一個月有幾日 */
  val prevMonthDaysImpl : IPrevMonthDays

  /** [StarLucky.天魁] , [StarLucky.天鉞] (貴人) 算法  */
  val tianyiImpl: ITianyi

  val fireBell: FireBell

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
                    stars: Collection<ZStar>,
                    gender: Gender,
                    optionalVageMap: Map<Int, Pair<Double, Double>>?): Builder

}

/** 年系星系  */
enum class YearType : Descriptive {
  YEAR_LUNAR, // 初一為界
  YEAR_SOLAR; // 立春為界

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IZiweiContext::class.java.name, locale).getString(name)
    } catch (e: MissingResourceException) {
      name
    }
  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
}


/** [StarUnlucky.火星] ,  [StarUnlucky.鈴星] 設定  */
enum class FireBell : Descriptive {
  /** [StarUnlucky.fun火星_全集] , [StarUnlucky.fun鈴星_全集] : (年支、時支) -> 地支  */
  FIREBELL_COLLECT,

  /** [StarUnlucky.fun火星_全書] , [StarUnlucky.fun鈴星_全書] : 年支 -> 地支 . 中州派 : 火鈴的排法按中州派僅以生年支算落宮，不按生時算落宮  */
  FIREBELL_BOOK;

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IZiweiContext::class.java.name, locale).getString(name)
    } catch (e: MissingResourceException) {
      name
    }
  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
}


/** [StarMinor.天傷]、 [StarMinor.天使] 計算方式  */
enum class HurtAngel : Descriptive {
  /** 天傷固定於交友宮 [StarMinor.fun天傷_fixed交友] 、 天使固定疾厄宮 [StarMinor.fun天使_fixed疾厄]  */
  HURT_ANGEL_FIXED,

  /** 陽順陰逆 [StarMinor.fun天傷_陽順陰逆] 、 [StarMinor.fun天使_陽順陰逆]  */
  HURT_ANGEL_YINYANG;

  override fun getTitle(locale: Locale): String {
    return ResourceBundle.getBundle(IZiweiContext::class.java.name, locale)
      .getString(name)
  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
}


/** 紅艷  */
enum class RedBeauty : Descriptive {
  /** [StarMinor.fun紅艷_甲乙相同]  */
  RED_BEAUTY_DIFF,

  /** [StarMinor.fun紅艷_甲乙相異]  */
  RED_BEAUTY_SAME;

  override fun getTitle(locale: Locale): String {
    return ResourceBundle.getBundle(IZiweiContext::class.java.name, locale)
      .getString(name)
  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
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
  override val monthStarsAlgo: MonthAlgo,

  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  override val yearType: YearType,

  /** 宮位名字  */
  override val houseSeqImpl: IHouseSeq,

  /** 上一個月有幾日 */
  override val prevMonthDaysImpl: IPrevMonthDays,

  /** 天乙貴人 算法 , 影響 [StarLucky.天魁] , [StarLucky.天鉞]  */
  override val tianyiImpl: ITianyi,

  /** [StarUnlucky.火星] ,  [StarUnlucky.鈴星] 設定  */
  override val fireBell: FireBell,

  /** [StarMinor.天傷]、 [StarMinor.天使] 計算方式  */
  override val hurtAngel: HurtAngel,

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
  override val redBeauty: RedBeauty) : IZiweiContext, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)!!

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
                             stars: Collection<ZStar>,
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
      logger.warn("命身宮設定為 : {} , 造成原本月份為 {}{}月，以月數 {} 取代之", mainStarsAlgo, if (leapMonth) "閏" else "", lunarMonth, finalMonthNumForMainStars)
      when {
        IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS === mainStarsAlgo -> // 命身宮用節氣計算，故用 {0}月={1} 而非 {2}{3}月
          notesBuilders.add(Pair("mainStarsAlgo_month_solar_terms", arrayOf(monthBranch, finalMonthNumForMainStars, if (leapMonth) "閏" else "", lunarMonth)))
        IFinalMonthNumber.MonthAlgo.MONTH_LEAP_NEXT === mainStarsAlgo -> // 命身宮於閏{0}月視為下月={1}
          notesBuilders.add(Pair("mainStarsAlgo_month_leap_next", arrayOf<Any>(lunarMonth, finalMonthNumForMainStars)))
        IFinalMonthNumber.MonthAlgo.MONTH_LEAP_SPLIT15 === mainStarsAlgo -> // 命身宮於閏月月中切割,故用{0}月
          notesBuilders.add(Pair("mainStarsAlgo_month_leap_split15", arrayOf<Any>(finalMonthNumForMainStars)))
      }
    }

    if (lunarMonth != finalMonthNumForMonthStars) {
      logger.warn("月系星的設定為 : {} , 造成原本月份為 {}{}月，以月數 {} 取代之", monthStarsAlgo, if (leapMonth) "閏" else "", lunarMonth, finalMonthNumForMonthStars)
      when {
        IFinalMonthNumber.MonthAlgo.MONTH_SOLAR_TERMS === monthStarsAlgo -> // 月系星以節氣計算，故月用 {0}={1} 而非 {2}
          notesBuilders.add(Pair("monthStarsAlgo_solar_month", arrayOf(monthBranch, finalMonthNumForMonthStars, if (leapMonth) "閏" else "", lunarMonth)))
        IFinalMonthNumber.MonthAlgo.MONTH_LEAP_NEXT === monthStarsAlgo -> // 月系星於閏{0}月視為下月{1}
          notesBuilders.add(Pair("monthStarsAlgo_month_leap_next", arrayOf<Any>(lunarMonth, finalMonthNumForMonthStars)))
        IFinalMonthNumber.MonthAlgo.MONTH_LEAP_SPLIT15 === monthStarsAlgo -> // 月系星於閏月月中切割,故用{0}月
          notesBuilders.add(Pair("monthStarsAlgo_month_leap_split15", arrayOf<Any>(finalMonthNumForMonthStars)))
      }
    }

    // 寅 的天干
    val stemOf寅 = IZiwei.getStemOf寅(lunarYear.stem)

    // 命宮所參考的「年干」，同時依據「年系星」的類型來決定
    val year: StemBranch = if (yearType == YearType.YEAR_LUNAR) lunarYear else solarYear

    // 命宮地支
    val mainBranch = mainAndBody?.first ?: IZiwei.getMainHouseBranch(finalMonthNumForMainStars, hour)
    val mainHouse = IZiwei.getStemBranchOf(mainBranch, stemOf寅)
    logger.trace("命宮在 : {}", mainHouse)

    // 身宮地支
    val bodyBranch = mainAndBody?.second ?: IZiwei.getBodyHouseBranch(finalMonthNumForMainStars, hour)
    val bodyHouse = IZiwei.getStemBranchOf(bodyBranch, stemOf寅)
    logger.trace("身宮在 : {}", bodyHouse)

    // 取命主 : 命宮所在地支安星
    val mainStar = IZiwei.getMainStar(mainHouse.branch)

    // 取身主 : 以出生年之地支安星
    val bodyStar = IZiwei.getBodyStar(lunarYear.branch)


    // 12宮 順序 以及 名稱
    //val houseSeqImpl = context.houseSeqImpl

    /** 由命宮干支的納音 [NaYin] 來決定 五行+局數(Int) , 例如 水 2 局 */
    val (五行, 五行局) = IZiwei.getMainDesc(mainHouse)

    // 干支 -> 宮位 的 mapping
    val branchHouseMap: Map<StemBranch, House> = houseSeqImpl.houses.map { house ->
      // 要計算的宮位，比命宮，超前幾步
      val steps = houseSeqImpl.getAheadOf(house, House.命宮)
      val sb = IZiwei.getStemBranchOf(mainHouse.branch.prev(steps), stemOf寅)
      sb to house
    }.toMap()

    // 地支 <-> 宮位 的 雙向 mapping
    val branchHouseBiMap: BiMap<Branch, House> = HashBiMap.create<Branch, House>()
    branchHouseMap.forEach { sb, house -> branchHouseBiMap[sb.branch] = house }

    // 為了某些流派閏月的考量 , 須在此求出「上個月」有幾天 , 才能求出紫微星
    val prevMonthDays = if (leapMonth) prevMonthDaysImpl.getPrevMonthDays(cycle, lunarYear, lunarMonth, true) else 0

    // 什麼星，在什麼地支
    val starBranchMap: Map<ZStar, Branch> = stars.map { star ->
      val branch: Branch =
        HouseFunctions.map[star]?.getBranch(lunarYear, solarYear, monthBranch, finalMonthNumForMonthStars, solarTerms,
                                            lunarDays, hour, 五行局, gender, leapMonth, prevMonthDays, mainBranch,
                                            this)!!
      star to branch
    }.toMap()

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

    // 宮干四化 : 此宮位，因為什麼星，各飛入哪個宮位(地支)
    // 參考 : http://www.fate123.com.tw/fate-teaching/fate-lesson-5.2.asp
    val flyMap: Map<StemBranch, Set<Triple<ITransFour.Value, ZStar, Branch>>> = branchHouseMap.keys.map { sb: StemBranch ->
      val m = mutableSetOf<Triple<ITransFour.Value, ZStar, Branch>>()
      ITransFour.Value.values().map { value: ITransFour.Value ->
        val flyStar: ZStar = transFourImpl.getStarOf(sb.stem, value)
        if (starBranchMap[flyStar] == null) {
          // TODO 通常不會有空宮 , 這裡就不做事
          logger.debug("cannot find flyStar[{} ({})] from starBranchMap : ", flyStar, flyStar.javaClass.name)
          starBranchMap.forEach { star, branch -> logger.debug("\t{}->{}", star, branch) }
        } else {
          m.add(Triple(value, flyStar, starBranchMap[flyStar]!!))
        }
      }
      sb to m
    }.toMap()


    // 星體強弱表
    val starStrengthMap: Map<ZStar, Int> = stars
      .map { star ->
        val strength: Int? = strengthImpl.getStrengthOf(star, starBranchMap[star]!!)
        star to strength
      }.filter { it.second != null }
      .map { it -> it.first to it.second!! }
      .toMap()

    val chineseDate = ChineseDate(cycle, lunarYear, lunarMonth, leapMonth, lunarDays)

    // 計算每個地支的 大限 起訖 「虛歲」時刻
    val flowBigVageMap = bigRangeImpl.getSortedFlowBigVageMap(branchHouseBiMap, 五行局, lunarYear, gender, houseSeqImpl)

    // 小限 mapping
    val branchSmallRangesMap: Map<Branch, List<Int>> = Branch.values().map { branch ->
      branch to ISmallRange.getRanges(branch, lunarYear.branch, gender)
    }.toMap()

    // 歲數 map
    val vageMap: Map<Int, Pair<Double, Double>>? = optionalVageMap
//    vageMap = if (optionalVageMap != null)
//      optionalVageMap
//    else {
//      val chineseDateImpl = ChineseDateCalendricaImpl() // 這裡不需太精確，不用在此套用天文曆法
//      val gmt = chineseDateImpl.getYangDate(chineseDate).atTime(LocalTime.NOON)
//      val gmtJulDay = TimeTools.getGmtJulDay(gmt)
//      intAgeZiweiImpl.getRangesMap(gender, gmtJulDay, Location.of(Locale.UK), 1, 130) // 參數沒有 loc 資訊，時間傳回 GMT , 就以 UK 作為地點
//    }

    return Builder(this, chineseDate, gender, finalMonthNumForMonthStars, hour, mainHouse, bodyHouse, mainStar,
                   bodyStar, 五行, 五行局, branchHouseMap, starBranchMap, starStrengthMap, flowBigVageMap,
                   branchSmallRangesMap, flyMap, vageMap)
      .appendNotesBuilders(notesBuilders)
      .appendTrans4Map(trans4Map)
  }

  /**
   * @param stars     取得這些星體
   * @param flowType  在[本命、大限、流年]... (之一)
   * @param flowStem  天干為
   * @return 傳回四化 (若有的話)
   */
  private fun getTrans4Map(stars: Collection<ZStar>,
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

} // ZContext
