/**
 * @author smallufo
 * Created on 2006/5/22 at 下午 12:09:24
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IStarPosition
import destiny.core.astrology.IStarTransit
import destiny.core.calendar.*
import destiny.core.chinese.IStemBranch
import destiny.tools.getDescription
import destiny.tools.getTitle
import java.util.*

/**
 * (default)
 * 年：具備設定換年點的功能
 *
 * 月：「定氣法」計算地支 , 計算太陽在黃道帶 0 , 15 , 30 ... 345 度的時刻
 *
 * 具備設定 南北半球月令是否對沖﹑界定南北半球的方法（赤道/赤緯度數）
 */
class YearMonthSolarTermsStarPositionImpl(
  private val yearImpl: IYear,
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  private val julDayResolver: JulDayResolver,
  override val southernHemisphereOpposition: Boolean = false,
  override val hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR
) : IYearMonth, IYear by yearImpl {

  override fun getTitle(locale: Locale): String {
    return MonthImpl.SolarTerms.getTitle(Locale.TAIWAN)
  }

  override fun getDescription(locale: Locale): String {
    return MonthImpl.SolarTerms.getDescription(locale)
  }

  val solarTermsImpl: ISolarTerms by lazy {
    SolarTermsImpl(starTransitImpl, starPositionImpl)
  }

  /**
   * @return 取得月干支
   */
  override fun getMonth(gmtJulDay: GmtJulDay, location: ILocation): IStemBranch {
    return getMonth(gmtJulDay, location, solarTermsImpl, starPositionImpl, southernHemisphereOpposition, hemisphereBy, changeYearDegree, julDayResolver)
  }

  companion object {
    const val VALUE = "default"
  }

}
