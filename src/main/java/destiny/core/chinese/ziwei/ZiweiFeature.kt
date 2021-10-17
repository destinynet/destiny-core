/**
 * Created by smallufo on 2021-10-15.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.IntAgeNote
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YearType
import destiny.tools.AbstractCachedPersonFeature
import destiny.tools.DestinyMarker
import destiny.tools.serializers.ZStarSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

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
                       val mainStarsAlgo: IFinalMonthNumber.MonthAlgo? = IFinalMonthNumber.MonthAlgo.MONTH_FIXED_THIS,
                       /** 月系星，如何計算月令  */
                       val monthStarsAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_FIXED_THIS,
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
                       val ageNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo)

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
  var mainStarsAlgo: IFinalMonthNumber.MonthAlgo? = IFinalMonthNumber.MonthAlgo.MONTH_FIXED_THIS
  /** 月系星，如何計算月令  */
  var monthStarsAlgo: IFinalMonthNumber.MonthAlgo = IFinalMonthNumber.MonthAlgo.MONTH_FIXED_THIS
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
  /** 歲運註記 */
  var ageNotes: List<IntAgeNote> = listOf(IntAgeNote.WestYear, IntAgeNote.Minguo)
  /** 大限計算方式 */
  var bigRange: BigRange = BigRange.FromMain

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
    fun ziweiConfig(block: ZiweiConfigBuilder.() -> Unit = {}) : ZiweiConfig {
      return ZiweiConfigBuilder().apply(block).build()
    }
  }
}

/**
 * to replace [IZiweiContext]
 */
class ZiweiFeature : AbstractCachedPersonFeature<ZiweiConfig, IPlate>() {

  override val defaultConfig: ZiweiConfig = ZiweiConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: ZiweiConfig): IPlate {
    TODO("Not yet implemented")
  }
}
