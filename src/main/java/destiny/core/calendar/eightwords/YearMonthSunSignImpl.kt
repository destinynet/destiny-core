/**
 * Created by smallufo on 2018-06-07.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.IStarPosition
import destiny.astrology.IStarTransit
import destiny.core.calendar.ILocation
import destiny.core.calendar.SolarTerms
import destiny.core.chinese.IStemBranch
import destiny.core.chinese.StemBranchUnconstrained
import java.util.*

/**
 * 120柱月令
 * 依據星座劃分月令 , coolwind 提出的理論
 * 若是出生之時，太陽位於「節、氣」的「前半段」 , 例如：「立春 到 雨水 之間」 或是 「立夏 到 小滿 之間」
 * 則，地支退一位 , 意味： 地支以星座區分（氣 與 氣之間劃分）
 *
 * 實作方法： 繼承 [YearMonthSolarTermsStarPositionImpl] , 並覆寫其 getMonth 之值
 */
class YearMonthSunSignImpl(
  starPositionImpl: IStarPosition<*>,
  starTransitImpl: IStarTransit,
  /** 換年的度數 , 通常是立春點 (315) 換年 */
  changeYearDegree: Double = 315.0,
  override val southernHemisphereOpposition: Boolean = false,
  override val hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR) :
  YearMonthSolarTermsStarPositionImpl(starPositionImpl, starTransitImpl, changeYearDegree, southernHemisphereOpposition,
                                      hemisphereBy) {

  override fun getTitle(locale: Locale): String {
    return "120柱月令"
  }

  override fun getDescription(locale: Locale): String {
    return "以節氣加星座 劃分月令：節氣的「節」與「氣」之間，屬於上一個星座，天干不變，地支退一位"
  }

  override fun getMonth(gmtJulDay: Double, location: ILocation): IStemBranch {
    // 原始 月干支
    val originalMonth = super.getMonth(gmtJulDay, location)

    // 目前的節氣
    val solarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)
    val solarTermsIndex: Int = SolarTerms.getIndex(solarTerms)

    return if (solarTermsIndex % 2 == 0) {
      // 單數 : 立春 、 驚蟄 ...
      StemBranchUnconstrained[originalMonth.stem , originalMonth.branch]!!.previous
    } else {
      StemBranchUnconstrained[originalMonth.stem , originalMonth.branch]!!
    }
  }
}