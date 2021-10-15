/**
 * Created by smallufo on 2021-10-15.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.chinese.Tianyi
import destiny.core.chinese.YearType
import destiny.tools.AbstractCachedPersonFeature
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

@Serializable
data class ZiweiConfig(val stars: Set<@Contextual ZStar> = setOf(*StarMain.values, *StarLucky.values, *StarUnlucky.values),
                       val mainBodyHouse: MainBodyHouse = MainBodyHouse.Trad,
                       val purpleStarBranch: PurpleStarBranch = PurpleStarBranch.Default,
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
) : java.io.Serializable

/**
 * to replace [IZiweiContext]
 */
class ZiweiFeature : AbstractCachedPersonFeature<ZiweiConfig, IPlate>() {

  override val defaultConfig: ZiweiConfig = ZiweiConfig()

  override fun calculate(gmtJulDay: GmtJulDay, loc: ILocation, gender: Gender, name: String?, place: String?, config: ZiweiConfig): IPlate {
    TODO("Not yet implemented")
  }
}
