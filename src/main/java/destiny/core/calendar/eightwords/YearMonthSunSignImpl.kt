/**
 * Created by smallufo on 2018-06-07.
 */
package destiny.core.calendar.eightwords

import destiny.astrology.IStarPosition
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
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
  private val ymSolarTermsStarPositionImpl: YearMonthSolarTermsStarPositionImpl,
  /** 換年的度數 , 通常是立春點 (315) 換年 */
  changeYearDegree: Double = 315.0,

  override val southernHemisphereOpposition: Boolean = false,
  override val hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR)
  : YearEclipticDegreeImpl(changeYearDegree, starPositionImpl), IYearMonth {

  val solarTermsImpl: ISolarTerms by lazy {
    ymSolarTermsStarPositionImpl.solarTermsImpl
    //SolarTermsImpl(this.starTransitImpl, this.starPositionImpl)
  }

  override fun getTitle(locale: Locale): String {
    return name
  }

  override fun getDescription(locale: Locale): String {
    return "以節氣加星座 劃分月令：節氣的「節」與「氣」之間，屬於上一個星座，天干不變，地支退一位"
  }

  override fun getMonth(gmtJulDay: Double, location: ILocation): IStemBranch {
    // 原始 月干支
    val originalMonth = ymSolarTermsStarPositionImpl.getMonth(gmtJulDay, location)
    //val originalMonth = super.getMonth(gmtJulDay, location)

    // 目前的節氣
    val solarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

    return if (solarTerms.major) {
      // 單數 : 立春 、 驚蟄 ...
      StemBranchUnconstrained[originalMonth.stem, originalMonth.branch]!!.prev
    } else {
      StemBranchUnconstrained[originalMonth.stem, originalMonth.branch]!!
    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is YearMonthSunSignImpl) return false
    if (!super.equals(other)) return false

    if (ymSolarTermsStarPositionImpl != other.ymSolarTermsStarPositionImpl) return false
    if (southernHemisphereOpposition != other.southernHemisphereOpposition) return false
    if (hemisphereBy != other.hemisphereBy) return false

    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode()
    result = 31 * result + ymSolarTermsStarPositionImpl.hashCode()
    result = 31 * result + southernHemisphereOpposition.hashCode()
    result = 31 * result + hemisphereBy.hashCode()
    return result
  }

  companion object {
    const val name = "120柱月令"
  }


}
