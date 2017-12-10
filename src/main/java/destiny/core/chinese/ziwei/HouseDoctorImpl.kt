/** Created by smallufo on 2017-12-11. */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem
import destiny.core.chinese.StemBranch

/**
 * 年干 + 性別
 * 被用於 [StarDoctor] : 博士12神煞
 * 必須判別 [ZContext.YearType]
 * 因為 博士12神煞 depend on [StarLucky.祿存] , 而 祿存 又是 年干 系星 ,
 * 祿存的 實作繼承 [HouseYearStemImpl] , 與 [ZContext.YearType] 相關
 * 故此博士十二神煞，也必須與祿存的年干設定相同
 *
 */
class HouseDoctorImpl(star: StarDoctor) : HouseAbstractImpl<Pair<Stem, Gender>>(star) {

  override fun getBranch(t: Pair<Stem, Gender>): Branch {
    return StarDoctor.starFuncMap[star]!!.invoke(t.first , t.second)
  }

  override fun getBranch(lunarYear: StemBranch, solarYear: StemBranch, monthBranch: Branch, finalMonthNumForMonthStars: Int, solarTerms: SolarTerms, days: Int, hour: Branch, state: Int, gender: Gender, leap: Boolean, prevMonthDays: Int, predefinedMainHouse: Branch?, context: ZContext): Branch {
    val yearStem = if (context.yearType == ZContext.YearType.YEAR_LUNAR) lunarYear.stem else solarYear.stem
    return getBranch(Pair(yearStem, gender))
  }

}