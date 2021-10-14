/**
 * Created by smallufo on 2021-10-15.
 */
package destiny.core.chinese.ziwei

import destiny.core.calendar.chinese.IFinalMonthNumber
import destiny.core.chinese.YearType
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

@Serializable
data class ZiweiConfig(val stars: Set<@Contextual ZStar>,
                       val mainBodyHouse: MainBodyHouse,
                       val purpleStarBranch: PurpleStarBranch,
                       val mainStarsAlgo: IFinalMonthNumber.MonthAlgo?,
                       /** 月系星，如何計算月令  */
                       val monthStarsAlgo: IFinalMonthNumber.MonthAlgo,
                       /** 年系星系 , 初一為界，還是 [destiny.core.calendar.SolarTerms.立春] 為界 */
                       val yearType: YearType,
                       /** 宮位名字  */
                       val houseSeq: HouseSeq,
) : java.io.Serializable

/**
 * to replace [IZiweiContext]
 */
class ZiweiFeature {
}
