/**
 * Created by smallufo on 2018-06-07.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.GmtJulDay
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
 * 實作方法： 包含 [YearMonthSolarTermsStarPositionImpl] , 並覆寫其 getMonth 之值
 */
class YearMonthSunSignImpl(private val ymSolarTermsStarPositionImpl: YearMonthSolarTermsStarPositionImpl) :
  IYearMonth, IYear by ymSolarTermsStarPositionImpl {

  override val southernHemisphereOpposition: Boolean = ymSolarTermsStarPositionImpl.southernHemisphereOpposition

  override val hemisphereBy: HemisphereBy = ymSolarTermsStarPositionImpl.hemisphereBy

  override val impl: MonthConfig.Impl
    get() = MonthConfig.Impl.SunSign

  val solarTermsImpl: ISolarTerms by lazy {
    ymSolarTermsStarPositionImpl.solarTermsImpl
  }

  override fun toString(locale: Locale): String {
    return name
  }

  override fun getDescription(locale: Locale): String {
    return "以節氣加星座 劃分月令：節氣的「節」與「氣」之間，屬於上一個星座，天干不變，地支退一位"
  }

  override fun getMonth(gmtJulDay: GmtJulDay, location: ILocation): IStemBranch {
    // 原始 月干支
    val originalMonth = ymSolarTermsStarPositionImpl.getMonth(gmtJulDay, location)

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

    if (ymSolarTermsStarPositionImpl != other.ymSolarTermsStarPositionImpl) return false

    return true
  }

  override fun hashCode(): Int {
    return ymSolarTermsStarPositionImpl.hashCode()
  }


  companion object {
    const val name = "120柱月令"
    const val VALUE = "sign"
  }
}
