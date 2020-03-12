/**
 * @author smallufo
 * Created on 2006/5/22 at 下午 12:09:24
 */
package destiny.core.calendar.eightwords

import destiny.astrology.Centric.GEO
import destiny.astrology.Coordinate.ECLIPTIC
import destiny.astrology.Coordinate.EQUATORIAL
import destiny.astrology.IStarPosition
import destiny.astrology.IStarTransit
import destiny.astrology.Planet
import destiny.core.calendar.*
import destiny.core.chinese.*
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
  private val starPositionImpl: IStarPosition<*>,
  private val starTransitImpl: IStarTransit,
  /** 換年的度數 , 通常是立春點 (315) 換年  , 另一個值通常為 270 (冬至) */
  changeYearDegree: Double = 315.0,
  override val southernHemisphereOpposition: Boolean = false,
  override val hemisphereBy: HemisphereBy = HemisphereBy.EQUATOR) :
  YearEclipticDegreeImpl(changeYearDegree, starPositionImpl), IYearMonth {

  override fun toString(locale: Locale): String {
    return name
  }

  override fun getDescription(locale: Locale): String {
    return "以「節氣」的「節」來切割月份"
  }

  val solarTermsImpl: ISolarTerms by lazy {
    SolarTermsImpl(this.starTransitImpl, this.starPositionImpl)
  }

  /**
   * @return 取得月干支
   */
  override fun getMonth(gmtJulDay: Double, location: ILocation): IStemBranch {
    val result月支: Branch
    //先算出太陽在黃經上的度數

    // 目前的節氣
    val solarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay)

    var monthIndex = SolarTerms.getIndex(solarTerms) / 2 + 2
    if (monthIndex >= 12)
      monthIndex -= 12
    val 月支 = Branch[monthIndex]

    if (southernHemisphereOpposition) {
      /**
       * 解決南半球月支正沖的問題
       */
      if (hemisphereBy == HemisphereBy.EQUATOR) {
        //如果是依據赤道來區分南北半球
        result月支 = if (location.northSouth == NorthSouth.SOUTH)
          Branch[monthIndex + 6]
        else
          月支
      } else {
        /**
         * 如果 hemisphereBy == DECLINATION (赤緯) , 就必須計算 太陽在「赤緯」的度數
         */
        val solarEquatorialDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, GEO, EQUATORIAL).lat

        if (solarEquatorialDegree >= 0) {
          //如果太陽在赤北緯
          result月支 = if (location.northSouth == NorthSouth.NORTH) {
            //地點在北半球
            if (location.lat >= solarEquatorialDegree)
              月支
            else
              Branch[monthIndex + 6] //所在地緯度低於 太陽赤緯，取對沖月份
          } else {
            //地點在南半球 , 取正沖
            Branch[monthIndex + 6]
          }
        } else {
          //太陽在赤南緯
          result月支 = if (location.northSouth == NorthSouth.SOUTH) {
            //地點在南半球
            if (location.lat <= solarEquatorialDegree)
              Branch[monthIndex + 6] //所在地緯度高於 太陽赤南緯，真正的南半球
            else
              月支 //雖在南半球，但緯度低於太陽赤南緯，視為北半球
          } else {
            //地點在北半球，月支不變
            月支
          }
        }
      }
    } else
      result月支 = 月支

    val 年干 = getYear(gmtJulDay, location).stem
    return StemBranch[getMonthStem(gmtJulDay, 年干, result月支), result月支]
  }

  /**
   * 五虎遁月 取得月干
   *
   * 甲己之年丙作首
   * 乙庚之歲戊為頭
   * 丙辛之歲由庚上
   * 丁壬壬位順行流
   * 若言戊癸何方發
   * 甲寅之上好追求。
   *
   */
  private fun getMonthStem(gmtJulDay: Double, 年干: Stem, 月支: Branch): Stem {

    var 月干: Stem = StemBranchUtils.getMonthStem(年干, 月支)

    if (changeYearDegree != 315.0) {

      val sunDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, GEO, ECLIPTIC).lng

      if (changeYearDegree < 315) {
        logger.debug("換年點在立春前 , changeYearDegree < 315 , value = {}", changeYearDegree)
        if (sunDegree > changeYearDegree && 315 > sunDegree) {
          // t <---立春---- LMT -----換年點
          月干 = Stem[月干.index - 2]
        }
      } else if (changeYearDegree > 315) {
        //換年點在立春後 , 還沒測試
        if (sunDegree > 315 && changeYearDegree > sunDegree)
          月干 = Stem[月干.index + 2]
      }
    }
    return 月干
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is YearMonthSolarTermsStarPositionImpl) return false

    if (southernHemisphereOpposition != other.southernHemisphereOpposition) return false
    if (hemisphereBy != other.hemisphereBy) return false

    return true
  }

  override fun hashCode(): Int {
    var result = southernHemisphereOpposition.hashCode()
    result = 31 * result + hemisphereBy.hashCode()
    return result
  }


  init {
    require(changeYearDegree > 180) { "Cannot set changeYearDegree smaller than 180 " }
  }

  companion object {
    const val name = "傳統年月"
    val logger = KotlinLogging.logger { }
  }


}
