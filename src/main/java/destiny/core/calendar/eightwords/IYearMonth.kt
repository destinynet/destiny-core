/**
 * @author smallufo
 * Created on 2006/5/22 at 上午 11:57:40
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.chinese.StemBranch
import java.time.chrono.ChronoLocalDateTime

/** 取得年干支的介面  */
interface IYear {

  fun getYear(gmtJulDay: Double, loc: ILocation): StemBranch

  /**
   *
   * @param lmt 傳入當地手錶時間
   * @param loc 傳入當地經緯度等資料
   * @return 年干支（天干地支皆傳回）
   */
  fun getYear(lmt: ChronoLocalDateTime<*>, loc: ILocation): StemBranch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getYear(gmtJulDay, loc)
  }

}

/**
 * 取得月干支的介面
 */
interface IMonth {

  /** 南半球月令是否對沖  */
  val southernHemisphereOpposition: Boolean

  /**
   * 南半球的判定方法
   * 依據 赤道 [HemisphereBy.EQUATOR] , 還是 赤緯 [HemisphereBy.DECLINATION] 來界定南北半球
   * 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   */
  val hemisphereBy: HemisphereBy

  fun getMonth(gmtJulDay: Double, location: ILocation): StemBranch

  fun getMonth(lmt: ChronoLocalDateTime<*>, loc: ILocation): StemBranch {
    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    return getMonth(gmtJulDay, loc)
  }

}


/**
 * 年月應該要一起考慮，所以設計這個 Interface
 */
interface IYearMonth : IYear, IMonth
