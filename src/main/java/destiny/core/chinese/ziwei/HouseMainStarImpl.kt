/**
 * Created by smallufo on 2017-04-27.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.Branch

/**
 * 14 顆主星
 * (局數,生日,是否閏月,前一個月幾天,當下節氣地支)
 */
data class MainStarData(val state: Int,
                        val days: Int,
                        val leapMonth: Boolean,
                        val prevMonthDays: Int,
                        val purpleStarBranch: IPurpleStarBranch)

open class HouseMainStarImpl internal constructor(star: StarMain) : HouseAbstractImpl<MainStarData>(star) {

  override fun getBranch(t: MainStarData): Branch {
    return StarMain.starFuncMap[star]!!.invoke(t.state, t.days, t.leapMonth, t.prevMonthDays, t.purpleStarBranch)
  }

  override fun getBranch(context: HouseCalContext): Branch {

    return context.run {
      if (!leap) {
        getBranch(MainStarData(state, days, false, prevMonthDays, defaultImpl))
      } else {
        // 閏月
        if (days + prevMonthDays == 30) {
          getBranch(MainStarData(state, 30, true, prevMonthDays, defaultImpl))
        } else {
          // 閏月，且「日數＋前一個月的月數」超過 30天，就啟用注入進來的演算法 . 可能會累加日數
          getBranch(MainStarData(state, days, true, prevMonthDays,  purpleStarBranchImplMap[config.purpleStarBranch]!!))
        }
      }
    }
  }

  companion object {

    private val defaultImpl = PurpleStarBranchDefaultImpl()
  }
}
