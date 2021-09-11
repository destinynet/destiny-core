/**
 * @author smallufo
 * Created on 2006/5/22 at 下午 12:09:24
 */
package destiny.core.calendar.eightwords

import destiny.core.astrology.IStarPosition
import destiny.core.astrology.IStarTransit
import destiny.core.calendar.*
import destiny.core.chinese.IStemBranch
import mu.KotlinLogging
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
) : IYearMonth, IYear by yearImpl{

  override fun toString(locale: Locale): String {
    return MonthConfig.MonthImpl.SolarTerms.toString(Locale.TAIWAN)
  }

  override fun getDescription(locale: Locale): String {
    return MonthConfig.MonthImpl.SolarTerms.getDescription(locale)
  }

  val solarTermsImpl: ISolarTerms by lazy {
    SolarTermsImpl(starTransitImpl, starPositionImpl, julDayResolver)
  }

  /**
   * @return 取得月干支
   */
  override fun getMonth(gmtJulDay: GmtJulDay, location: ILocation): IStemBranch {

    return getMonth(gmtJulDay, location, solarTermsImpl, starPositionImpl, southernHemisphereOpposition, hemisphereBy, changeYearDegree, julDayResolver)

  }



  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is YearMonthSolarTermsStarPositionImpl) return false

    if (southernHemisphereOpposition != other.southernHemisphereOpposition) return false
    if (hemisphereBy != other.hemisphereBy) return false
    if (changeYearDegree != other.changeYearDegree) return false

    return true
  }

  override fun hashCode(): Int {
    var result = southernHemisphereOpposition.hashCode()
    result = 31 * result + hemisphereBy.hashCode()
    result = 31 * result + changeYearDegree.hashCode()
    return result
  }


  companion object {
    const val VALUE = "default"
    private val logger = KotlinLogging.logger { }
  }

}
