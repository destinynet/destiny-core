/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.StemBranch
import org.jooq.lambda.tuple.Tuple
import org.jooq.lambda.tuple.Tuple5
import java.util.*

/**
 * 14 顆主星
 * (局數,生日,是否閏月,前一個月幾天,當下節氣地支)
 */
abstract class HouseMainStarImpl internal constructor(star: ZStar) : HouseAbstractImpl<Tuple5<Int, Int, Boolean, Int, IPurpleStarBranch>>(star) {

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, set: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Optional<Branch>, context: ZContext): Branch {
    return if (!leap) {
      getBranch(Tuple.tuple(set, days, false, prevMonthDays, defaultImpl))
    } else {
      // 閏月
      if (days + prevMonthDays == 30) {
        getBranch(Tuple.tuple(set, 30, true, prevMonthDays, defaultImpl))
      } else {
        // 閏月，且「日數＋前一個月的月數」超過 30天，就啟用注入進來的演算法 . 可能會累加日數
        getBranch(Tuple.tuple(set, days, true, prevMonthDays, context.getPurpleBranchImpl()))
      }
    }
  }

  companion object {

    private val defaultImpl = PurpleStarBranchDefaultImpl()
  }
}
