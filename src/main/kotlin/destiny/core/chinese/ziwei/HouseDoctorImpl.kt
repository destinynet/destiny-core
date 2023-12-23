/** Created by smallufo on 2017-12-11. */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.chinese.Branch
import destiny.core.chinese.Stem

/**
 * 年干 + 性別
 * 被用於 [StarDoctor] : 博士12神煞
 * 必須判別 [IZiweiConfig.yearType]
 * 因為 博士12神煞 depend on [StarLucky.祿存] , 而 祿存 又是 年干 系星 ,
 * 祿存的 實作繼承 [HouseYearStemImpl] , 與 [IZiweiConfig.yearType] 相關
 * 故此博士十二神煞，也必須與祿存的年干設定相同
 *
 */
class HouseDoctorImpl(star: StarDoctor) : HouseAbstractImpl<Pair<Stem, Gender>>(star) {

  override fun getBranch(t: Pair<Stem, Gender>): Branch {
    return StarDoctor.starFuncMap[star]!!.invoke(t.first, t.second)
  }

  override fun getBranch(context: HouseCalContext): Branch {
    val yearStem = context.year.stem
    return getBranch(Pair(yearStem, context.gender))
  }
}
