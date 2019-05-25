/**
 * @author smallufo
 * Created on 2008/5/29 at 上午 3:15:13
 */
package destiny.astrology.prediction

import destiny.astrology.*
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import mu.KotlinLogging

import java.io.Serializable
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime

interface IReturnContext : Conversable , IDiscrete {

  val planet: Planet

  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  val orb: Double

  /** 是否逆推 , true 代表「是」，逆推！ */
  override val converse: Boolean

  /** 對外主要的 method , 取得 return 盤  */
  fun getReturnHoroscope(natalLmt: ChronoLocalDateTime<*> , natalLoc: ILocation , nowLmt: ChronoLocalDateTime<*> , nowLoc: ILocation) : IHoroscopeModel
}


/**
 * 返照法演算法 , 可以計算 Planet 的返照
 */
class ReturnContext(
  /** 返照法所採用的行星 , 太陽/太陰 , 或是其他  */
  override val planet: Planet = Planet.SUN,
  /** 是否逆推 , true 代表「是」，逆推！ */
  override val converse: Boolean,
  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  override val orb: Double = 0.0,

  /** 計算星體的介面  */
  private val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator,
  /** 計算星體到黃道幾度的時刻，的介面  */
  private var starTransitImpl: IStarTransit,
  private val houseCuspImpl: IHouseCusp,
  /** 是否消除歲差，內定是不計算歲差  */
  private val precession: Boolean = false,
  private val pointPosFuncMap: Map<Point, IPosition<*>>) : IReturnContext, Serializable {


  override fun getReturnHoroscope(natalLmt: ChronoLocalDateTime<*>, natalLoc: ILocation, nowLmt: ChronoLocalDateTime<*>, nowLoc: ILocation): IHoroscopeModel {
    val natalGmt = TimeTools.getGmtFromLmt(natalLmt, natalLoc)
    val nowGmt = TimeTools.getGmtFromLmt(nowLmt, nowLoc)

    val convergentGmt = getConvergentTime(natalGmt, nowGmt)
    val convergentLmt = TimeTools.getLmtFromGmt(convergentGmt, nowLoc)

    val horoscopeContext = HoroscopeContext(starPositionWithAzimuthImpl, houseCuspImpl, pointPosFuncMap, IHoroscopeContext.defaultPoints)
    return horoscopeContext.getHoroscope(convergentLmt, nowLoc)
  }

  /**
   * 實作 [Mappable], 注意，在 [AbstractProgression]的實作中，並未要求是GMT；但在這裡，必須**要求是GMT** ！
   * 傳回值也是GMT！
   */
  override fun getConvergentTime(natalTime: ChronoLocalDateTime<*>, nowTime: ChronoLocalDateTime<*>): ChronoLocalDateTime<*> {
    val nowGmtJulDay = TimeTools.getGmtJulDay(nowTime)

    val coordinate = if (precession) Coordinate.SIDEREAL else Coordinate.ECLIPTIC
    //先計算出生盤中，該星體的黃道位置
    val natalPlanetDegree = starPositionWithAzimuthImpl.getPosition(planet, natalTime, Centric.GEO, coordinate).lng

    //再從現在的時刻，往前(prior , before) 推 , 取得 planet 與 natal planet 呈現 orb 的時刻
    return if (!converse) {
      //順推
      starTransitImpl.getNextTransitGmtDateTime(planet, Utils.getNormalizeDegree(natalPlanetDegree + orb), coordinate, nowGmtJulDay, false) //false 代表逆推，往before算
    } else {
      // converse == true , 逆推
      //從出生時間往前(before)推
      val d = Duration.between(nowTime, natalTime).abs()
      val beforeNatalGmtJulDay = TimeTools.getGmtJulDay(natalTime.minus(d))
      //要確認最後一個參數，到底是要用 true , 還是 false , 要找相關定義 , 我覺得這裡應該是順推
      starTransitImpl.getNextTransitGmtDateTime(planet, Utils.getNormalizeDegree(natalPlanetDegree + orb), coordinate, beforeNatalGmtJulDay, true) //true 代表順推 , 往 after 算
    }
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }

}
