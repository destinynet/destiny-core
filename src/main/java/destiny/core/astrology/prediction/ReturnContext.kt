/**
 * @author smallufo
 * Created on 2008/5/29 at 上午 3:15:13
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.*
import destiny.core.astrology.Planet
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.TimeTools
import destiny.tools.circleUtils
import mu.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import kotlin.math.absoluteValue

interface IReturnContext : Conversable, IDiscrete {

  val planet: Planet

  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  val orb: Double

  /** 是否消除歲差， false = 不計算歲差  */
  val precession: Boolean

  /** 對外主要的 method , 取得 return 盤  */
  fun getReturnHoroscope(natalGmtJulDay: Double, natalLoc: ILocation, nowGmtJulDay: Double, nowLoc: ILocation): IHoroscopeModel

  fun getReturnHoroscope(natalLmt: ChronoLocalDateTime<*>, natalLoc: ILocation, nowLmt: ChronoLocalDateTime<*>, nowLoc: ILocation): IHoroscopeModel {
    val natalGmtJulDay = TimeTools.getGmtJulDay(natalLmt , natalLoc)
    val nowGmtJulDay = TimeTools.getGmtJulDay(nowLmt , nowLoc)
    return getReturnHoroscope(natalGmtJulDay , natalLoc ,  nowGmtJulDay , nowLoc)
  }
}


/**
 * 返照法演算法 , 可以計算 Planet 的返照
 */
class ReturnContext(
  /** 返照法所採用的行星 , 太陽/太陰 , 或是其他  */
  override val planet: Planet = Planet.SUN,
  /** 是否逆推 , true 代表「是」，逆推！ */
  override val converse: Boolean = false,
  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  override val orb: Double = 0.0,
  /** 是否消除歲差，內定是不計算歲差  */
  override val precession: Boolean = false,

  /** 計算星體的介面  */
  private val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator,
  /** 計算星體到黃道幾度的時刻，的介面  */
  private var starTransitImpl: IStarTransit,
  private val houseCuspImpl: IHouseCusp,
  private val pointPosFuncMap: Map<Point, IPosition<*>> ,
  private val julDayResolver: JulDayResolver) : IReturnContext, Serializable {


  override fun getReturnHoroscope(natalGmtJulDay: Double, natalLoc: ILocation, nowGmtJulDay: Double, nowLoc: ILocation): IHoroscopeModel {
    val convergentGmtJulDay: Double = getConvergentTime(natalGmtJulDay, nowGmtJulDay)
    val convergentGmt = julDayResolver.getLocalDateTime(convergentGmtJulDay)

    val convergentLmt = TimeTools.getLmtFromGmt(convergentGmt, nowLoc)

    val horoscopeContext = HoroscopeContext(starPositionWithAzimuthImpl, houseCuspImpl, pointPosFuncMap, IHoroscopeContext.defaultPoints)
    return horoscopeContext.getHoroscope(convergentLmt, nowLoc)

  }


  override fun getConvergentTime(natalGmtJulDay: Double, nowGmtJulDay: Double): Double {
    val coordinate = if (precession) Coordinate.SIDEREAL else Coordinate.ECLIPTIC
    //先計算出生盤中，該星體的黃道位置
    val natalPlanetDegree = starPositionWithAzimuthImpl.getPosition(planet, natalGmtJulDay, Centric.GEO, coordinate).lng

    //再從現在的時刻，往前(prior , before) 推 , 取得 planet 與 natal planet 呈現 orb 的時刻
    return if (!converse) {
      //順推
      starTransitImpl.getNextTransitGmt(planet, circleUtils.getNormalizeDegree(natalPlanetDegree + orb), coordinate, nowGmtJulDay, false) //false 代表逆推，往before算
    } else {
      // converse == true , 逆推
      //從出生時間往前(before)推
      val d = (natalGmtJulDay - nowGmtJulDay).absoluteValue
      val beforeNatalGmtJulDay = natalGmtJulDay - d // TimeTools.getGmtJulDay(natalTime.minus(d))
      //要確認最後一個參數，到底是要用 true , 還是 false , 要找相關定義 , 我覺得這裡應該是順推
      starTransitImpl.getNextTransitGmt(planet, circleUtils.getNormalizeDegree(natalPlanetDegree + orb), coordinate, beforeNatalGmtJulDay, true) //true 代表順推 , 往 after 算
    }
  }


  companion object {
    val logger = KotlinLogging.logger { }
  }

}
