package destiny.core.chinese.ziwei

import destiny.core.IPresentConfig
import destiny.core.IntAgeNote
import destiny.core.astrology.DayNightConfig
import destiny.core.astrology.DayNightConfigBuilder
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.chinese.MonthAlgo
import destiny.core.calendar.eightwords.EightWordsConfig
import destiny.core.calendar.eightwords.IEightWordsConfig
import destiny.core.chinese.AgeType
import destiny.core.chinese.Branch
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YearType
import destiny.tools.Builder
import destiny.tools.DestinyMarker
import destiny.tools.serializers.LocaleSerializer
import destiny.tools.serializers.ZStarSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

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

interface IZiweiConfig : IEightWordsConfig , java.io.Serializable {
  val stars: Set<@Serializable(with = ZStarSerializer::class) ZStar>
  /** 命宮、身宮 演算法  */
  var mainBodyHouse: MainBodyHouse

  /** 紫微星，在閏月時，該如何處理  */
  var purpleStarBranch: PurpleStarBranch

  /**
   * 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [mainBodyHouse] 為占星實作 [MainBodyHouse.Astro] , 此值會被忽略
   * 注意，此值可能為 null , 因為若是 '命宮、身宮 演算法' 是占星實作的話 , client 端會把此值填為 null
   * */
  var mainStarsAlgo: MonthAlgo?

  /** 月系星，如何計算月令  */
  var monthStarsAlgo: MonthAlgo

  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  var yearType: YearType

  /** 宮位名字  */
  var houseSeq: HouseSeq

  /** [StarLucky.天魁] , [StarLucky.天鉞] (貴人) 算法  */
  var tianyi: Tianyi

  /** 火鈴 */
  var fireBell: FireBell

  /** 天馬 */
  var skyHorse: SkyHorse

  /** 天使天傷 */
  var hurtAngel: HurtAngel

  /** 紅豔 */
  var redBeauty: RedBeauty

  /** 四化設定 */
  var transFour: TransFour

  /** 廟旺弱陷 */
  var strength: Strength

  /** 流年 */
  var flowYear: FlowYear

  /** 流月 */
  var flowMonth: FlowMonth

  /** 流日 */
  var flowDay: FlowDay

  /** 流時 */
  var flowHour: FlowHour

  /** 大限計算方式 */
  var bigRange: BigRange

  /** 大限歲數 , 實歲 or 虛歲 */
  var sectionAgeType: AgeType

  /** 歲運註記 */
  val ageNotes: List<IntAgeNote>

  /** 八字設定 設定 */
  //val ewConfig: EightWordsConfig = EightWordsConfig()

  /** 晝夜區分 */
  val dayNightConfig: DayNightConfig

  /** 曆法 */
  var chineseDateImpl: ChineseDateImpl

  /** 紫微強制地支 */
  var purpleFixedBranch: Branch?

  var locale: Locale

  val ziweiConfig : ZiweiConfig
    get() = ZiweiConfig(
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
      sectionAgeType,
      ageNotes,
      ewConfig,
      dayNightConfig,
      chineseDateImpl,
      purpleFixedBranch,
      locale
    )
}

@Serializable
data class ZiweiConfig(
  override val stars: Set<@Serializable(with = ZStarSerializer::class) ZStar> = setOf(
    *StarMain.values, *StarMinor.values, *StarLucky.values, *StarUnlucky.values,
    *StarDoctor.values, *StarGeneralFront.values, *StarLongevity.values, *StarYearFront.values
),
  /** 命宮、身宮 演算法  */
  override var mainBodyHouse: MainBodyHouse = MainBodyHouse.Trad,
  /** 紫微星，在閏月時，該如何處理  */
  override var purpleStarBranch: PurpleStarBranch = PurpleStarBranch.Default,
  /**
   * 命宮、身宮、紫微等14顆主星 對於月份，如何計算 . 若 [mainBodyHouse] 為占星實作 [MainBodyHouse.Astro] , 此值會被忽略
   * 注意，此值可能為 null , 因為若是 '命宮、身宮 演算法' 是占星實作的話 , client 端會把此值填為 null
   * */
  override var mainStarsAlgo: MonthAlgo? = MonthAlgo.MONTH_FIXED_THIS,
  /** 月系星，如何計算月令  */
  override var monthStarsAlgo: MonthAlgo = MonthAlgo.MONTH_FIXED_THIS,
  /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
  override var yearType: YearType = YearType.YEAR_LUNAR,
  /** 宮位名字  */
  override var houseSeq: HouseSeq = HouseSeq.Default,
  /** [StarLucky.天魁] , [StarLucky.天鉞] (貴人) 算法  */
  override var tianyi: Tianyi = Tianyi.ZiweiBook,
  /** 火鈴 */
  override var fireBell: FireBell = FireBell.FIREBELL_COLLECT,
  /** 天馬 */
  override var skyHorse: SkyHorse = SkyHorse.YEAR,
  /** 天使天傷 */
  override var hurtAngel: HurtAngel = HurtAngel.HURT_ANGEL_FIXED,
  /** 紅豔 */
  override var redBeauty: RedBeauty = RedBeauty.RED_BEAUTY_DIFF,
  /** 四化設定 */
  override var transFour: TransFour = TransFour.FullBook,
  /** 廟旺弱陷 */
  override var strength: Strength = Strength.FullBook,
  /** 流年 */
  override var flowYear: FlowYear = FlowYear.Branch,
  /** 流月 */
  override var flowMonth: FlowMonth = FlowMonth.Default,
  /** 流日 */
  override var flowDay: FlowDay = FlowDay.FromFlowMonthMainHouse,
  /** 流時 */
  override var flowHour: FlowHour = FlowHour.MainHouseDep,
  /** 大限計算方式 */
  override var bigRange: BigRange = BigRange.FromMain,
  /** 大限歲數 , 實歲 or 虛歲 */
  override var sectionAgeType: AgeType = AgeType.VIRTUAL,
  /** 歲運註記 */
  override val ageNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo),
  /** 八字設定 設定 */
  override val ewConfig: EightWordsConfig = EightWordsConfig(),
  /** 晝夜區分 */
  override val dayNightConfig: DayNightConfig = DayNightConfig(),
  /** 曆法 */
  override var chineseDateImpl: ChineseDateImpl = ChineseDateImpl.Civil,
  /** 紫微強制地支 */
  override var purpleFixedBranch : Branch? = null,
  @Serializable(with = LocaleSerializer::class)
  override var locale: Locale = Locale.TRADITIONAL_CHINESE
) : IZiweiConfig , IEightWordsConfig by ewConfig

@DestinyMarker
class ZiweiConfigBuilder(val iEwConfig : IEightWordsConfig) : Builder<ZiweiConfig> {

  private val defaultConfig: IZiweiConfig = ZiweiConfig()

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

  /** 大限歲數 , 實歲 or 虛歲 */
  var sectionAgeType: AgeType = defaultConfig.sectionAgeType

  /** 歲運註記 */
  var ageNotes: List<IntAgeNote> = defaultConfig.ageNotes

  /** 晝夜區分 */
  var dayNightConfig: DayNightConfig = defaultConfig.dayNightConfig


  fun dayNightConfig(block: DayNightConfigBuilder.() -> Unit = {}) {
    this.dayNightConfig = with(iEwConfig) {
      DayNightConfigBuilder.dayNight(block)
    }
  }

  var chineseDateImpl: ChineseDateImpl = defaultConfig.chineseDateImpl

  var purpleFixedBranch: Branch? = defaultConfig.purpleFixedBranch

  var locale: Locale = Locale.TRADITIONAL_CHINESE

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
      sectionAgeType,
      ageNotes,
      iEwConfig.ewConfig,
      dayNightConfig,
      chineseDateImpl,
      purpleFixedBranch,
      locale
    )
  }

  companion object {
    context(ctx: IEightWordsConfig)
    fun ziweiConfig(block: ZiweiConfigBuilder.() -> Unit = {}): ZiweiConfig {
      return ZiweiConfigBuilder(ctx).apply(block).build()
    }
  }
}

interface IZiweiPresentConfig : IZiweiConfig , IPresentConfig

data class ZiweiPresentConfig(override val ziweiConfig : ZiweiConfig = ZiweiConfig(),
                              override var viewGmt: GmtJulDay = GmtJulDay.nowCeilingToNoon()) : IZiweiPresentConfig, IZiweiConfig by ziweiConfig
