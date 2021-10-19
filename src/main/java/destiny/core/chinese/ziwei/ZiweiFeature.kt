/**
 * Created by smallufo on 2021-10-15.
 */
package destiny.core.chinese.ziwei

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import destiny.core.DayNight
import destiny.core.Gender
import destiny.core.IntAgeNote
import destiny.core.astrology.*
import destiny.core.calendar.*
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.chinese.ChineseDateFeature
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.calendar.chinese.IFinalMonthNumber.MonthAlgo
import destiny.core.calendar.eightwords.DayHourFeature
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.IRisingSign
import destiny.core.calendar.eightwords.YearMonthFeature
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.DestinyMarker
import destiny.tools.PersonFeature
import destiny.tools.serializers.ZStarSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import java.time.chrono.ChronoLocalDateTime
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

@Serializable
data class ZiweiConfig(val stars: Set<@Serializable(with = ZStarSerializer::class) ZStar> = setOf(*StarMain.values, *StarLucky.values, *StarUnlucky.values),
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
                       val fireBell: FireBell = FireBell.FIREBELL_BOOK,
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
                       val flowDay: FlowDay = FlowDay.Branch,
                       /** 流時 */
                       val flowHour: FlowHour = FlowHour.MainHouseDep,
                       /** 大限計算方式 */
                       val bigRange: BigRange = BigRange.FromMain,
                       /** 歲運註記 */
                       val ageNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo),
                       /** 八字設定 設定 */
                       val ewConfig: EightWordsConfig = EightWordsConfig()
) : java.io.Serializable


@DestinyMarker
class ZiweiConfigBuilder : destiny.tools.Builder<ZiweiConfig> {

  var stars: Set<@Contextual ZStar> = setOf(*StarMain.values, *StarLucky.values, *StarUnlucky.values)

  /** 命宮、身宮 演算法  */
  var mainBodyHouse: MainBodyHouse = MainBodyHouse.Trad

  /** 紫微星，在閏月時，該如何處理  */
  var purpleStarBranch: PurpleStarBranch = PurpleStarBranch.Default

  /**
   * 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [mainBodyHouse] 為占星實作 [MainBodyHouse.Astro] , 此值會被忽略
   * 注意，此值可能為 null , 因為若是 '命宮、身宮 演算法' 是占星實作的話 , client 端會把此值填為 null
   * */
  var mainStarsAlgo: MonthAlgo? = MonthAlgo.MONTH_FIXED_THIS

  /** 月系星，如何計算月令  */
  var monthStarsAlgo: MonthAlgo = MonthAlgo.MONTH_FIXED_THIS

  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  var yearType: YearType = YearType.YEAR_LUNAR

  /** 宮位名字  */
  var houseSeq: HouseSeq = HouseSeq.Default

  /** [StarLucky.天魁] , [StarLucky.天鉞] (貴人) 算法  */
  var tianyi: Tianyi = Tianyi.ZiweiBook

  /** 火鈴 */
  var fireBell: FireBell = FireBell.FIREBELL_BOOK

  /** 天馬 */
  var skyHorse: SkyHorse = SkyHorse.YEAR

  /** 天使天傷 */
  var hurtAngel: HurtAngel = HurtAngel.HURT_ANGEL_FIXED

  /** 紅豔 */
  var redBeauty: RedBeauty = RedBeauty.RED_BEAUTY_DIFF

  /** 四化設定 */
  var transFour: TransFour = TransFour.FullBook

  /** 廟旺弱陷 */
  var strength: Strength = Strength.FullBook

  /** 流年 */
  var flowYear: FlowYear = FlowYear.Anchor

  /** 流月 */
  var flowMonth: FlowMonth = FlowMonth.Default

  /** 流日 */
  var flowDay: FlowDay = FlowDay.Branch

  /** 流時 */
  var flowHour: FlowHour = FlowHour.MainHouseDep

  /** 大限計算方式 */
  var bigRange: BigRange = BigRange.FromMain

  /** 歲運註記 */
  var ageNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo)

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
      ageNotes
    )
  }

  companion object {
    fun ziweiConfig(block: ZiweiConfigBuilder.() -> Unit = {}): ZiweiConfig {
      return ZiweiConfigBuilder().apply(block).build()
    }
  }
}

/**
 * to replace [IZiweiContext]
 */

interface IZiweiFeature : PersonFeature<ZiweiConfig, IPlate> {

  /** 取命宮、身宮地支  */
  fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: ILocation, config: ZiweiConfig = ZiweiConfig()): Triple<Branch, Branch, Int?>

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
    dayNight: DayNight = (if (listOf(卯, 辰, 巳, 午, 未, 申).contains(hour)) DayNight.DAY else DayNight.NIGHT),
    gender: Gender,
    optionalVageMap: Map<Int, Pair<GmtJulDay, GmtJulDay>>? = null,
    config: ZiweiConfig
  ): Builder
}

@Named
class ZiweiFeature(
  private val julDayResolver: JulDayResolver,
  @Named("houseCuspImpl")
  private val risingSignImpl: IRisingSign,
  private val starPositionImpl: IStarPosition<*>,
  private val chineseDateFeature: ChineseDateFeature,
  private val yearMonthFeature: YearMonthFeature,
  private val dayHourFeature: DayHourFeature,
  private val houseSeqImplMap: Map<HouseSeq, IHouseSeq>,
  private val prevMonthDaysImpl: IPrevMonthDays,
  private val purpleStarBranchImplMap : Map<PurpleStarBranch, IPurpleStarBranch>,
  private val tianyiImplMap: Map<Tianyi, ITianyi>
) : AbstractCachedPersonFeature<ZiweiConfig, IPlate>(), IZiweiFeature {

  override val defaultConfig: ZiweiConfig = ZiweiConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: ZiweiConfig): IPlate {
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

    TODO("Not yet implemented")
  }

  override fun calculate(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?, config: ZiweiConfig): IPlate {

    // 排盤之中所產生的註解 , Pair<KEY , parameters>
    val notesBuilders = mutableListOf<Pair<String, Array<Any>>>()



    TODO("Not yet implemented")
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
