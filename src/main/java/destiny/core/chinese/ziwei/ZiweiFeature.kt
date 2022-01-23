/**
 * Created by smallufo on 2021-10-15.
 */
package destiny.core.chinese.ziwei

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.IIntAge
import destiny.core.IntAgeNote
import destiny.core.astrology.*
import destiny.core.calendar.*
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.IChineseDateFeature
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.*
import destiny.core.chinese.*
import destiny.core.chinese.Branch.values
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import destiny.tools.getTitle
import destiny.tools.serializers.ZStarSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.time.LocalTime
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import javax.cache.Cache
import javax.inject.Inject
import javax.inject.Named

/** 命宮、身宮 演算法  */
enum class MainBodyHouse {
  Astro,
  Trad
}

/** 紫微星，在閏月時，該如何處理  */
enum class PurpleStarBranch {
  Default,
  LeapAccumDays
}

/** 宮位名字 */
enum class HouseSeq {
  Default,
  Astro,
  Taiyi
}

/** [StarUnlucky.火星] ,  [StarUnlucky.鈴星] 設定  */
enum class FireBell {
  /** [StarUnlucky.fun火星_全集] , [StarUnlucky.fun鈴星_全集] : (年支、時支) -> 地支 (福耕老師論點) */
  FIREBELL_COLLECT,

  /** [StarUnlucky.fun火星_全書] , [StarUnlucky.fun鈴星_全書] : 年支 -> 地支 . 中州派 : 火鈴的排法按中州派僅以生年支算落宮，不按生時算落宮  */
  FIREBELL_BOOK;
}


/** 天馬，要用 年馬 還是 月馬 */
enum class SkyHorse {
  YEAR,
  MONTH;
}

/** [StarMinor.天傷]、 [StarMinor.天使] 計算方式  */
enum class HurtAngel {
  /** 天傷固定於交友宮 [StarMinor.fun天傷_fixed交友] 、 天使固定疾厄宮 [StarMinor.fun天使_fixed疾厄]  */
  HURT_ANGEL_FIXED,

  /** 陽順陰逆 [StarMinor.fun天傷_陽順陰逆] 、 [StarMinor.fun天使_陽順陰逆]  */
  HURT_ANGEL_YINYANG;
}


/** 紅艷  */
enum class RedBeauty {
  /** [StarMinor.fun紅艷_甲乙相異]  */
  RED_BEAUTY_DIFF,

  /** [StarMinor.fun紅艷_甲乙相同]  */
  RED_BEAUTY_SAME;
}

/** 四化設定 */
enum class TransFour {
  ChenBiDong,
  Divine,
  FullBook,
  FullCollect,
  Middle,
  North,
  Ziyun
}

/** 廟旺弱陷 */
enum class Strength {
  FullBook,
  Middle
}

/** 流年排法(的命宮) */
enum class FlowYear {
  Anchor,
  Branch
}

/** 流月 */
enum class FlowMonth {
  Default,
  Fixed,
  YearMainHouseDep
}

/** 流日 */
enum class FlowDay {
  Branch,
  FromFlowMonthMainHouse,
  SkipFlowMonthMainHouse
}

/** 流時 */
enum class FlowHour {
  Branch,
  MainHouseDep
}

/** 大限計算方式 */
enum class BigRange {
  FromMain,
  SkipMain
}

enum class ChineseDateImpl {
  Civil
}

@Serializable
data class ZiweiConfig(val stars: Set<@Serializable(with = ZStarSerializer::class) ZStar> = setOf(*StarMain.values, *StarMinor.values, *StarLucky.values, *StarUnlucky.values,
                                                                                                  *StarDoctor.values, *StarGeneralFront.values, *StarLongevity.values, *StarYearFront.values),
                       /** 命宮、身宮 演算法  */
                       val mainBodyHouse: MainBodyHouse = MainBodyHouse.Trad,
                       /** 紫微星，在閏月時，該如何處理  */
                       val purpleStarBranch: PurpleStarBranch = PurpleStarBranch.Default,
                       /**
                        * 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [mainBodyHouse] 為占星實作 [MainBodyHouse.Astro] , 此值會被忽略
                        * 注意，此值可能為 null , 因為若是 '命宮、身宮 演算法' 是占星實作的話 , client 端會把此值填為 null
                        * */
                       val mainStarsAlgo: MonthAlgo? = MonthAlgo.MONTH_FIXED_THIS,
                       /** 月系星，如何計算月令  */
                       val monthStarsAlgo: MonthAlgo = MonthAlgo.MONTH_FIXED_THIS,
                       /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
                       val yearType: YearType = YearType.YEAR_LUNAR,
                       /** 宮位名字  */
                       val houseSeq: HouseSeq = HouseSeq.Default,
                       /** [StarLucky.天魁] , [StarLucky.天鉞] (貴人) 算法  */
                       val tianyi: Tianyi = Tianyi.ZiweiBook,
                       /** 火鈴 */
                       val fireBell: FireBell = FireBell.FIREBELL_COLLECT,
                       /** 天馬 */
                       val skyHorse: SkyHorse = SkyHorse.YEAR,
                       /** 天使天傷 */
                       val hurtAngel: HurtAngel = HurtAngel.HURT_ANGEL_FIXED,
                       /** 紅豔 */
                       val redBeauty: RedBeauty = RedBeauty.RED_BEAUTY_DIFF,
                       /** 四化設定 */
                       val transFour: TransFour = TransFour.FullBook,
                       /** 廟旺弱陷 */
                       val strength: Strength = Strength.FullBook,
                       /** 流年 */
                       val flowYear: FlowYear = FlowYear.Anchor,
                       /** 流月 */
                       val flowMonth: FlowMonth = FlowMonth.Default,
                       /** 流日 */
                       val flowDay: FlowDay = FlowDay.FromFlowMonthMainHouse,
                       /** 流時 */
                       val flowHour: FlowHour = FlowHour.MainHouseDep,
                       /** 大限計算方式 */
                       val bigRange: BigRange = BigRange.FromMain,
                       /** 歲運註記 */
                       val ageNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo),
                       /** 八字設定 設定 */
                       val ewConfig: EightWordsConfig = EightWordsConfig(),
                       /** 晝夜區分 */
                       val dayNightConfig: DayNightConfig = DayNightConfig(),
                       /** 曆法 */
                       val chineseDateImpl: ChineseDateImpl = ChineseDateImpl.Civil
) : java.io.Serializable


@DestinyMarker
class ZiweiConfigBuilder : destiny.tools.Builder<ZiweiConfig> {

  private val defaultConfig: ZiweiConfig = ZiweiConfig()

  var stars: Set<@Contextual ZStar> = defaultConfig.stars

  /** 命宮、身宮 演算法  */
  var mainBodyHouse: MainBodyHouse = defaultConfig.mainBodyHouse

  /** 紫微星，在閏月時，該如何處理  */
  var purpleStarBranch: PurpleStarBranch = defaultConfig.purpleStarBranch

  /**
   * 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [mainBodyHouse] 為占星實作 [MainBodyHouse.Astro] , 此值會被忽略
   * 注意，此值可能為 null , 因為若是 '命宮、身宮 演算法' 是占星實作的話 , client 端會把此值填為 null
   * */
  var mainStarsAlgo: MonthAlgo? = defaultConfig.mainStarsAlgo

  /** 月系星，如何計算月令  */
  var monthStarsAlgo: MonthAlgo = defaultConfig.monthStarsAlgo

  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  var yearType: YearType = defaultConfig.yearType

  /** 宮位名字  */
  var houseSeq: HouseSeq = defaultConfig.houseSeq

  /** [StarLucky.天魁] , [StarLucky.天鉞] (貴人) 算法  */
  var tianyi: Tianyi = defaultConfig.tianyi

  /** 火鈴 */
  var fireBell: FireBell = defaultConfig.fireBell

  /** 天馬 */
  var skyHorse: SkyHorse = defaultConfig.skyHorse

  /** 天使天傷 */
  var hurtAngel: HurtAngel = defaultConfig.hurtAngel

  /** 紅豔 */
  var redBeauty: RedBeauty = defaultConfig.redBeauty

  /** 四化設定 */
  var transFour: TransFour = defaultConfig.transFour

  /** 廟旺弱陷 */
  var strength: Strength = defaultConfig.strength

  /** 流年 */
  var flowYear: FlowYear = defaultConfig.flowYear

  /** 流月 */
  var flowMonth: FlowMonth = defaultConfig.flowMonth

  /** 流日 */
  var flowDay: FlowDay = defaultConfig.flowDay

  /** 流時 */
  var flowHour: FlowHour = defaultConfig.flowHour

  /** 大限計算方式 */
  var bigRange: BigRange = defaultConfig.bigRange

  /** 歲運註記 */
  var ageNotes: List<IntAgeNote> = defaultConfig.ageNotes

  /** 八字設定 */
  var ewConfig: EightWordsConfig = defaultConfig.ewConfig
  fun ewConfig(block: EightWordsConfigBuilder.() -> Unit = {}) {
    this.ewConfig = EightWordsConfigBuilder.ewConfig(block)
  }

  /** 晝夜區分 */
  var dayNightConfig: DayNightConfig = defaultConfig.dayNightConfig
  fun dayNightConfig(block: DayNightConfigBuilder.() -> Unit = {}) {
    this.dayNightConfig = DayNightConfigBuilder.dayNight(block)
  }

  var chineseDateImpl: ChineseDateImpl = defaultConfig.chineseDateImpl

  override fun build(): ZiweiConfig {
    return ZiweiConfig(
      stars,
      mainBodyHouse,
      purpleStarBranch,
      mainStarsAlgo,
      monthStarsAlgo,
      yearType,
      houseSeq,
      tianyi,
      fireBell,
      skyHorse,
      hurtAngel,
      redBeauty,
      transFour,
      strength,
      flowYear,
      flowMonth,
      flowDay,
      flowHour,
      bigRange,
      ageNotes,
      ewConfig,
      dayNightConfig,
      chineseDateImpl
    )
  }

  companion object {
    fun ziweiConfig(block: ZiweiConfigBuilder.() -> Unit = {}): ZiweiConfig {
      return ZiweiConfigBuilder().apply(block).build()
    }
  }
}

interface IZiweiFeature : PersonFeature<ZiweiConfig, Builder> {

  /** 取命宮、身宮地支  */
  fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: ZiweiConfig): Triple<Branch, Branch, Int?>

  /**
   * @param flowType  在[本命、大限、流年]... (之一)
   * @param flowStem  天干為
   * @return 傳回四化 (若有的話)
   */
  fun getTrans4Map(flowType: FlowType, flowStem: Stem, config: ZiweiConfig): Map<Pair<ZStar, FlowType>, ITransFour.Value>

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
  fun getBirthPlate(
    mainAndBody: Pair<Branch, Branch>?,
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
    optionalVageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>? = null,
    config: ZiweiConfig
  ): Builder

  /** 計算 大限盤  */
  fun getFlowBig(builder: Builder, flowBig: StemBranch, config:ZiweiConfig): Builder

  /** 計算 流年盤 */
  fun getFlowYear(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, config: ZiweiConfig): Builder

  /** 計算 流月盤 */
  fun getFlowMonth(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch, config: ZiweiConfig): Builder

  /** 計算 流日盤 */
  fun getFlowDay(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch, flowDay: StemBranch, flowDayNum: Int, config: ZiweiConfig): Builder

  /** 計算 流時盤 */
  fun getFlowHour(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch, flowDay: StemBranch, flowDayNum: Int, flowHour: StemBranch, config: ZiweiConfig): Builder

  /**
   * @param cycle     cycle
   * @param flowYear  流年
   * @param flowMonth 流月
   * @param leap      是否閏月
   * @return 該流月的日子 (陰曆＋陽曆＋干支）
   */
  fun getDaysOfMonth(cycle: Int, flowYear: StemBranch, flowMonth: Int, leap: Boolean): List<Triple<ChineseDate, ChronoLocalDate, StemBranch>>

  /** 列出此大限中，包含哪十個流年 (陰曆 cycle + 地支干支) , 並且「虛歲」各別是幾歲 ,   */
  fun getYearsOfFlowBig(builder: Builder, flowBig: Branch, config: ZiweiConfig): List<Triple<Int, StemBranch, Int>>
}

@Named
class ZiweiFeature(
  private val julDayResolver: JulDayResolver,
  @Named("houseCuspImpl")
  private val risingSignImpl: IRisingSign,
  private val starPositionImpl: IStarPosition<*>,
  private val chineseDateFeature: IChineseDateFeature,
  private val yearFeature: YearFeature,
  private val yearMonthFeature: YearMonthFeature,
  private val dayHourFeature: DayHourFeature,
  private val hourBranchFeature: IHourBranchFeature,
  private val houseSeqImplMap: Map<HouseSeq, IHouseSeq>,
  private val prevMonthDaysImpl: IPrevMonthDays,
  private val purpleStarBranchImplMap : Map<PurpleStarBranch, IPurpleStarBranch>,
  private val tianyiImplMap: Map<Tianyi, ITianyi>,
  private val transFourImplMap: Map<TransFour, ITransFour>,
  private val strengthImplMap: Map<Strength, IStrength>,
  private val bigRangeImplMap: Map<BigRange, IBigRange>,
  private val flowYearImplMap: Map<FlowYear, IFlowYear>,
  private val flowMonthImplMap: Map<FlowMonth, IFlowMonth>,
  private val flowDayImplMap: Map<FlowDay, IFlowDay>,
  private val flowHourImplMap: Map<FlowHour, IFlowHour>,
  private val dayNightFeature: DayNightFeature,
  @Named("intAgeZiweiImpl")
  private val intAgeImpl: IIntAge
) : AbstractCachedPersonFeature<ZiweiConfig, Builder>(), IZiweiFeature {

  @Inject
  @Transient
  private lateinit var ziweiCache: Cache<LmtCacheKey<*>, Pair<*, *>>

  override val lmtPersonCache: Cache<LmtCacheKey<ZiweiConfig>, Builder>
    get() = ziweiCache as Cache<LmtCacheKey<ZiweiConfig>, Builder>

  override val defaultConfig: ZiweiConfig = ZiweiConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: ZiweiConfig): Builder {
    val lmt = TimeTools.getLmtFromGmt(gmtJulDay, loc, julDayResolver)
    return getPersonModel(lmt, loc, gender, name, place, config)
  }

  /**
   * @return 命宮、身宮 、以及「最後要給主星所使用的月數 (若為占星算法，此值為空) 」
   *  */
  override fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: ZiweiConfig): Triple<Branch, Branch, Int?> {
    return when (config.mainBodyHouse) {
      MainBodyHouse.Trad  -> {
        val cDate: ChineseDate = chineseDateFeature.getModel(lmt, loc, config.ewConfig.dayHourConfig)

        val monthBranch = yearMonthFeature.getModel(lmt, loc, config.ewConfig.yearMonthConfig).branch

        val lunarMonth = cDate.month
        val days = cDate.day

        val hour = dayHourFeature.getModel(lmt, loc, config.ewConfig.dayHourConfig).second.branch


        // 最終要計算的「月份」數字 , for 主星
        val finalMonthNumForMainStars = IFinalMonthNumber.getFinalMonthNumber(lunarMonth, cDate.leapMonth, monthBranch, days, config.mainStarsAlgo)

        val mainHouse = Ziwei.getMainHouseBranch(finalMonthNumForMainStars, hour)
        val bodyHouse = Ziwei.getBodyHouseBranch(finalMonthNumForMainStars, hour)
        logger.trace { "命 = $mainHouse , 身 = $bodyHouse" }

        Triple(mainHouse, bodyHouse, finalMonthNumForMainStars)
      }
      MainBodyHouse.Astro -> {
        /** 利用上升星座，計算命宮
         *  利用月亮星座，計算身宮 */
        val mainHouse = risingSignImpl.getRisingSign(lmt, loc, HouseSystem.PLACIDUS, Coordinate.ECLIPTIC).branch
        val moonPos = starPositionImpl.getPosition(Planet.MOON, lmt, loc, Centric.GEO, Coordinate.ECLIPTIC)

        val zodiacSign = moonPos.lngDeg.sign

        val bodyHouse = zodiacSign.branch

        Triple(mainHouse, bodyHouse, null)
      }
    }
  }


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
  override fun getBirthPlate(
    mainAndBody: Pair<Branch, Branch>?,
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
    optionalVageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>?,
    config: ZiweiConfig
  ): Builder {

    // 排盤之中所產生的註解 , Pair<KEY , parameters>
    val notesBuilders = mutableListOf<Pair<String, Array<Any>>>()

    /**
     * 14主星的「月數」 [ZiweiConfig.mainStarsAlgo]
     * 與「月系星」的月數 [ZiweiConfig.monthStarsAlgo]
     * 可以分開設定 , 故，這裡產生兩個 finalMonthNum
     */

    /**
     * 最終要計算的「月份」數字 , for 主星 .
     * 若已經有了值（通常來自 傳統計算命宮、身宮的演算法 [MainBodyHouseTradImpl] ) , 代表也一定預先計算了命宮、身宮 [mainAndBody]
     * 若沒有預先計算的值 , 代表其命宮、身宮演算法來自占星 [MainBodyHouseAstroImpl] ,
     *    紫微星的算法，有依據「五行局數」. 而「五行局數」來自命宮. 若命宮採用上升星座，就與「月數」「節氣」無關。
     * */
    val finalMonthNumForMainStars: Int =
      preFinalMonthNumForMainStars ?: IFinalMonthNumber.getFinalMonthNumber(lunarMonth, leapMonth, monthBranch, lunarDays, config.mainStarsAlgo)

    // 最終要計算的「月份」數字 , for 月系星
    val finalMonthNumForMonthStars =
      IFinalMonthNumber.getFinalMonthNumber(lunarMonth, leapMonth, monthBranch, lunarDays, config.monthStarsAlgo)

    logger.trace("finalMonthNumForMainStars = {}", finalMonthNumForMainStars)


    if (config.mainBodyHouse == MainBodyHouse.Trad  // 如果主星是以傳統方式計算
      && lunarMonth != finalMonthNumForMainStars    // 而且最終月份數不一樣
    ) {
      logger.warn(
        "命身宮設定為 : {} , 造成原本月份為 {}{}月，以月數 {} 取代之", config.mainStarsAlgo, if (leapMonth) "閏" else "", lunarMonth,
        finalMonthNumForMainStars
      )
      when (config.mainStarsAlgo) {
        MonthAlgo.MONTH_FIXED_THIS   -> {
          // do nothing
        }
        MonthAlgo.MONTH_SOLAR_TERMS  -> {
          // 命身宮用節氣計算，故用 {0}月={1} 而非 {2}{3}月
          notesBuilders.add(
            Pair(
              "mainStarsAlgo_month_solar_terms",
              arrayOf(
                monthBranch, finalMonthNumForMainStars, if (leapMonth) "閏" else "",
                lunarMonth
              )
            )
          )
        }
        MonthAlgo.MONTH_LEAP_NEXT    -> {
          // 命身宮於閏{0}月視為下月={1}
          notesBuilders.add(Pair("mainStarsAlgo_month_leap_next", arrayOf(lunarMonth, finalMonthNumForMainStars)))
        }
        MonthAlgo.MONTH_LEAP_SPLIT15 -> {
          // 命身宮於閏月月中切割,故用{0}月
          notesBuilders.add(Pair("mainStarsAlgo_month_leap_split15", arrayOf(finalMonthNumForMainStars)))
        }
      }
    }

    if (lunarMonth != finalMonthNumForMonthStars) {
      logger.warn(
        "月系星的設定為 : {} , 造成原本月份為 {}{}月，以月數 {} 取代之", config.monthStarsAlgo, if (leapMonth) "閏" else "", lunarMonth,
        finalMonthNumForMonthStars
      )

      when (config.monthStarsAlgo) {
        MonthAlgo.MONTH_FIXED_THIS   -> {
          // do nothing
        }
        MonthAlgo.MONTH_SOLAR_TERMS  -> {
          // 月系星以節氣計算，故月用 {0}={1} 而非 {2}
          notesBuilders.add(
            Pair(
              "monthStarsAlgo_solar_month",
              arrayOf(
                monthBranch, finalMonthNumForMonthStars, if (leapMonth) "閏" else "",
                lunarMonth
              )
            )
          )
        }
        MonthAlgo.MONTH_LEAP_NEXT    -> {
          // 月系星於閏{0}月視為下月{1}
          notesBuilders.add(
            Pair("monthStarsAlgo_month_leap_next", arrayOf(lunarMonth, finalMonthNumForMonthStars))
          )
        }
        MonthAlgo.MONTH_LEAP_SPLIT15 -> {
          // 月系星於閏月月中切割,故用{0}月
          notesBuilders.add(Pair("monthStarsAlgo_month_leap_split15", arrayOf(finalMonthNumForMonthStars)))
        }
      }
    }

    // 寅 的天干
    val stemOf寅 = Ziwei.getStemOf寅(lunarYear.stem)

    // 命宮所參考的「年干」，同時依據「年系星」的類型來決定
    val year: StemBranch = if (config.yearType == YearType.YEAR_LUNAR) lunarYear else solarYear

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
    val houseSeqImpl = houseSeqImplMap[config.houseSeq]!!

    /**
     * 由命宮干支的納音 (NaYin) 來決定 五行+局數(Int) , 例如 水 2 局
     * */
    val (五行, 五行局) = Ziwei.getMainDesc(mainHouse)

    // 干支 -> 宮位 的 mapping
    val branchHouseMap: Map<StemBranch, House> = // 要計算的宮位，比命宮，超前幾步
      houseSeqImpl.houses.associateBy { house ->
        // 要計算的宮位，比命宮，超前幾步
        val steps = houseSeqImpl.getAheadOf(house, House.命宮)
        val sb = Ziwei.getStemBranchOf(mainHouse.branch.prev(steps), stemOf寅)
        sb
      }

    // 地支 <-> 宮位 的 雙向 mapping
    val branchHouseBiMap: BiMap<Branch, House> = HashBiMap.create()
    branchHouseMap.forEach { (sb, house) -> branchHouseBiMap[sb.branch] = house }

    // 為了某些流派閏月的考量 , 須在此求出「上個月」有幾天 , 才能求出紫微星
    val prevMonthDays = if (leapMonth) prevMonthDaysImpl.getPrevMonthDays(cycle, lunarYear, lunarMonth, true) else 0

    // 什麼星，在什麼地支
    val starBranchMap: Map<ZStar, Branch> = config.stars.map { star ->
      val branch: Branch? = HouseFunctions.map[star]?.getBranch(
        HouseCalContext(
          lunarYear,
          solarYear,
          monthBranch,
          finalMonthNumForMonthStars,
          solarTerms,
          lunarDays,
          hour,
          五行局,
          gender,
          leapMonth,
          prevMonthDays,
          config,
          mainBranch,
          purpleStarBranchImplMap,
          tianyiImplMap
        )
      )
      star to branch
    }
      .filter { (_, branch) -> branch != null }
      .associate { (star, branch) -> star to branch!! }

    logger.debug("stars = {}", config.stars)
    logger.debug("starBranchMap = {}", starBranchMap)

    // 本命四化 : 四化要依據 陰曆初一 還是 節氣立春 劃分

    val trans4Map: Map<Pair<ZStar, FlowType>, ITransFour.Value> =
      if (config.yearType == YearType.YEAR_LUNAR) {
        getTrans4Map(FlowType.MAIN, lunarYear.stem, config)
      } else {
        // 立春分年
        getTrans4Map(FlowType.MAIN, solarYear.stem, config).also {
          if (lunarYear !== solarYear) {
            // 如果年 與 陰曆年不同
            // solar_year=年系星立春為界，故年用 {0} 而非 {1}
            notesBuilders.add(Pair("solar_year", arrayOf(solarYear, lunarYear)))
          }
        }
      }
    logger.debug("transFour = {} , title = {}", config.transFour, config.transFour.getTitle(Locale.TAIWAN))
    logger.debug("trans4Map = {}", trans4Map)

    // 宮干四化 : 此宮位，因為什麼星，各飛入哪個宮位(地支)
    // 參考 : http://www.fate123.com.tw/fate-teaching/fate-lesson-5.2.asp
    val transFourImpl = transFourImplMap[config.transFour]!!
    val flyMap: Map<StemBranch, Set<Triple<ITransFour.Value, ZStar, Branch>>> = branchHouseMap.keys.associateWith { sb: StemBranch ->

      ITransFour.Value.values()
        .map { value ->
          val flyStar: ZStar = transFourImpl.getStarOf(sb.stem, value)
          value to flyStar
        }
        .filter { (_, flyStar) ->
          starBranchMap[flyStar] != null
        }
        .map { (value, flyStar) ->
          Triple(value, flyStar, starBranchMap.getValue(flyStar))
        }
        .toSet()
    }

    // 星體強弱表
    val strengthImpl = strengthImplMap[config.strength]!!
    val starStrengthMap: Map<ZStar, Int> = config.stars.map { star ->
      val strength: Int? = strengthImpl.getStrengthOf(star, starBranchMap.getValue(star))
      star to strength
    }
      .filter { it.second != null }
      .associate { it.first to it.second!! }

    val chineseDate = ChineseDate(cycle, lunarYear, lunarMonth, leapMonth, lunarDays)

    // 計算每個地支的 大限 起訖 「虛歲」時刻
    val bigRangeImpl = bigRangeImplMap[config.bigRange]!!
    val flowBigVageMap = bigRangeImpl.getSortedFlowBigVageMap(branchHouseBiMap, 五行局, lunarYear, gender, houseSeqImpl)

    // 小限 mapping
    val branchSmallRangesMap: Map<Branch, List<Int>> = values()
      .associateWith { branch ->
        ISmallRange.getRanges(branch, lunarYear.branch, gender)
      }

    /**
     * 歲數 map , 2018-06-03 改 optional . 因為不想在 core 內 , depend on ChineseDateCalendricaImpl
     * 不然就得開發另一套非常簡易的 [IIntAge] 在此使用
     */
    val vageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>? = optionalVageMap


    return Builder(
      config, chineseDate, gender, year, finalMonthNumForMonthStars, hour, dayNight, mainHouse, bodyHouse, mainStar,
      bodyStar, 五行, 五行局, branchHouseMap, starBranchMap, starStrengthMap, flowBigVageMap,
      branchSmallRangesMap, flyMap, vageMap
    )
      .appendNotesBuilders(notesBuilders)
      .appendTrans4Map(trans4Map)

  }

  /**
   * @param flowType  在[本命、大限、流年]... (之一)
   * @param flowStem  天干為
   * @return 傳回四化 (若有的話)
   */
  override fun getTrans4Map(flowType: FlowType, flowStem: Stem, config: ZiweiConfig): Map<Pair<ZStar, FlowType>, ITransFour.Value> {
    return config.stars.map { star ->
      val key = star to flowType
      val value: ITransFour.Value? = transFourImplMap[config.transFour]!!.getValueOf(star, flowStem)
      key to value
    }
      .filter { it.second != null }
      .associate { it.first to it.second!! }
  }

  /** 計算 大限盤  */
  override fun getFlowBig(builder: Builder, flowBig: StemBranch, config: ZiweiConfig): Builder {
    // 在此大限中，每個地支，對應到哪個宮位

    val branchHouseMap = values().associateWith { branch ->
      val steps = branch.getAheadOf(flowBig.branch)

      houseSeqImplMap[config.houseSeq]!!.prev(House.命宮, steps)
    }

    // 大限四化
    val trans4Map = getTrans4Map(FlowType.SECTION, flowBig.stem, config)
    return builder
      .withFlowBig(flowBig, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }

  /** 計算 流年盤  */
  override fun getFlowYear(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, config: ZiweiConfig): Builder {
    // 流年命宮

    val yearlyMain = flowYearImplMap[config.flowYear]!!.getFlowYear(flowYear.branch, builder.birthMonthNum, builder.birthHour)

    val branchHouseMap = values().associateWith { branch ->
      val steps = branch.getAheadOf(yearlyMain)
      houseSeqImplMap[config.houseSeq]!!.prev(House.命宮, steps)
    }

    // 流年四化
    val trans4Map = getTrans4Map(FlowType.YEAR, flowYear.stem, config)

    return getFlowBig(builder, flowBig, config)
      .withFlowYear(flowYear, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }

  /** 計算 流月盤  */
  override fun getFlowMonth(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch, config: ZiweiConfig): Builder {

    // 流月命宮
    val monthlyMain = flowMonthImplMap[config.flowMonth]!!.getFlowMonth(flowYear.branch, flowMonth.branch, builder.birthMonthNum, builder.birthHour)

    val branchHouseMap = values().associateWith { branch ->
      val steps = branch.getAheadOf(monthlyMain)
      houseSeqImplMap[config.houseSeq]!!.prev(House.命宮, steps)
    }

    // 流月四化
    val trans4Map = getTrans4Map(FlowType.MONTH, flowMonth.stem, config)

    return getFlowYear(builder, flowBig, flowYear, config)
      .withFlowMonth(flowMonth, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }

  /** 計算 流日盤  */
  override fun getFlowDay(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch, flowDay: StemBranch, flowDayNum: Int, config: ZiweiConfig): Builder {
    // 流月命宮
    val monthlyMain = flowMonthImplMap[config.flowMonth]!!.getFlowMonth(flowYear.branch, flowMonth.branch, builder.birthMonthNum, builder.birthHour)

    // 流日命宮
    val dailyMain = flowDayImplMap[config.flowDay]!!.getFlowDay(flowDay.branch, flowDayNum, monthlyMain)
    val branchHouseMap = values().associateWith { branch ->
      val steps = branch.getAheadOf(dailyMain)
      houseSeqImplMap[config.houseSeq]!!.prev(House.命宮, steps)
    }

    // 流日四化
    val trans4Map = getTrans4Map(FlowType.DAY, flowDay.stem, config)
    return getFlowMonth(builder, flowBig, flowYear, flowMonth, config)
      .withFlowDay(flowDay, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }

  /** 計算 流時盤 */
  override fun getFlowHour(builder: Builder, flowBig: StemBranch, flowYear: StemBranch, flowMonth: StemBranch, flowDay: StemBranch, flowDayNum: Int, flowHour: StemBranch, config: ZiweiConfig): Builder {
    // 流月命宮
    val monthlyMain = flowMonthImplMap[config.flowMonth]!!.getFlowMonth(flowYear.branch, flowMonth.branch, builder.birthMonthNum, builder.birthHour)
    // 流日命宮
    val dailyMain = flowDayImplMap[config.flowDay]!!.getFlowDay(flowDay.branch, flowDayNum, monthlyMain)
    // 流時命宮
    val hourlyMain = flowHourImplMap[config.flowHour]!!.getFlowHour(flowHour.branch, dailyMain)

    val branchHouseMap = values().associateWith { branch ->
      val steps = branch.getAheadOf(hourlyMain)
      houseSeqImplMap[config.houseSeq]!!.prev(House.命宮, steps)
    }

    // 流時四化
    val trans4Map = getTrans4Map(FlowType.HOUR, flowHour.stem, config)

    return getFlowDay(builder, flowBig, flowYear, flowMonth, flowDay, flowDayNum, config)
      .withFlowHour(flowHour, branchHouseMap)
      .appendTrans4Map(trans4Map)
  }


  /**
   * @param cycle     cycle
   * @param flowYear  流年
   * @param flowMonth 流月
   * @param leap      是否閏月
   * @return 該流月的日子 (陰曆＋陽曆＋干支）
   */
  override fun getDaysOfMonth(cycle: Int, flowYear: StemBranch, flowMonth: Int, leap: Boolean): List<Triple<ChineseDate, ChronoLocalDate, StemBranch>> {
    val days = chineseDateFeature.getDaysOf(cycle, flowYear, flowMonth, leap)

    val list =  mutableListOf<Triple<ChineseDate, ChronoLocalDate, StemBranch>>()

    for (i in 1..days) {
      val yinDate = ChineseDate(cycle, flowYear, flowMonth, leap, i)

      val yangDate = chineseDateFeature.getYangDate(yinDate)
      val lmtJulDay = (TimeTools.getGmtJulDay(yangDate.atTime(LocalTime.MIDNIGHT)).value + 0.5).toInt()
      val index = (lmtJulDay - 11) % 60
      val sb = StemBranch[index]
      list.add(Triple(yinDate, yangDate, sb))
    }
    return list
  }

  /** 列出此大限中，包含哪十個流年 (陰曆 cycle + 地支干支) , 並且「虛歲」各別是幾歲 ,   */
  override fun getYearsOfFlowBig(builder: Builder, flowBig: Branch, config: ZiweiConfig): List<Triple<Int, StemBranch, Int>> {
    val birthYear = builder.chineseDate.year
    val birthCycle = builder.chineseDate.cycleOrZero

    val bigRangeImpl = bigRangeImplMap[config.bigRange]!!

    val (first, second) = bigRangeImpl.getVageRange(builder.branchHouseMap.getValue(flowBig), builder.state, birthYear.stem, builder.gender, houseSeqImplMap[config.houseSeq]!!)

    // 再把虛歲轉換成干支
    return (first .. second).map { vAge ->
      val sb = birthYear.next(vAge - 1) // 虛歲 (vAge) 轉換為年 , 要減一 . 虛歲
      val cycle: Int = if (sb.index >= birthYear.index) {
        birthCycle + (vAge - 1) / 60
      } else {
        birthCycle + (vAge - 1) / 60 + 1
      }
      Triple(cycle , sb , vAge)
    }.toList()
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: ZiweiConfig): Builder {

    // 排盤之中所產生的註解 , Pair<KEY , parameters>
    val notesBuilders = mutableListOf<Pair<String, Array<Any>>>()

    // 命宮地支, 身宮地支
    val (mainBranch, bodyBranch, finalMonthNumForMainStars) = getMainBodyHouse(lmt, loc, config)

    if (config.mainBodyHouse == MainBodyHouse.Astro) {
      logger.warn("命宮、身宮 採用上升、月亮星座")
      notesBuilders.add(Pair("main_body_astro", arrayOf()))
    }

    logger.debug("命宮地支 = {} , 身宮地支 = {}", mainBranch, bodyBranch)

    val t2 = TimeTools.getDstSecondOffset(lmt, loc)
    val dst = t2.first
    val minuteOffset = t2.second / 60

    val cDate = chineseDateFeature.getModel(lmt, loc)
    val cycle = cDate.cycleOrZero
    val yinYear = cDate.year

    val monthBranch = yearMonthFeature.getModel(lmt, loc, config.ewConfig.yearMonthConfig).branch

    val solarYear = yearFeature.getModel(lmt, loc, config.ewConfig.yearMonthConfig.yearConfig)

    val lunarMonth = cDate.month
    val solarTerms = yearMonthFeature.solarTermsImpl.getSolarTerms(lmt, loc)

    val lunarDays = cDate.day

    val hour = dayHourFeature.getModel(lmt, loc, config.ewConfig.dayHourConfig).second.branch

    val hourImpl = config.ewConfig.dayHourConfig.hourBranchConfig.hourImpl

    if (dst) {
      // 日光節約時間 特別註記
      notesBuilders.add(Pair("dst", arrayOf()))
      logger.info("[DST]:校正日光節約時間...")
      logger.info("lmt = {} , location = {} . location.hasMinuteOffset = {}", lmt, loc, loc.hasMinuteOffset)
      logger.info("loc tz = {} , minuteOffset = {}", loc.timeZone.id, loc.finalMinuteOffset)
      logger.info("有日光節約時間 ,  GMT 時差 : {}", minuteOffset)
      logger.info("時辰 = {} . hourImpl = {}", hour, config.ewConfig.dayHourConfig.hourBranchConfig.hourImpl)
    }

    if (hourImpl == HourImpl.TST) {
      // 如果是真太陽時

      val hour2: Branch = hourBranchFeature.getModel(lmt, loc, HourBranchConfig(hourImpl = HourImpl.LMT))

      if (hour != hour2) {
        // 如果真太陽時與LMT時間不一致，出現提醒
        notesBuilders.add(Pair("true_solar_time", arrayOf(hour, hour2)))
      }
    }

    val dayNight = dayNightFeature.getModel(lmt, loc, config.dayNightConfig)

    // 虛歲時刻 , gmt Julian Day
    val vageMap = intAgeImpl.getRangesMap(gender, TimeTools.getGmtJulDay(lmt, loc), loc, 1, 130)

    return getBirthPlate(Pair(mainBranch, bodyBranch), finalMonthNumForMainStars, cycle, yinYear, solarYear, lunarMonth
                         , cDate.leapMonth, monthBranch, solarTerms, lunarDays, hour, dayNight, gender, vageMap , config)
      .withLocalDateTime(lmt)
      .withLocation(loc)
      .apply {
        name?.also {
          withName(it)
        }
      }
      .appendNotesBuilders(notesBuilders).apply {
        place?.also { withPlace(it) }
      }//.build()

  }

  companion object {
    private val logger = KotlinLogging.logger { }
    const val CACHE_ZIWEI_FEATURE = "ziweiFeatureCache"
  }
}
