/**
 * @author smallufo
 * Created on 2008/5/29 at 上午 3:15:13
 */
package destiny.astrology.prediction

import destiny.astrology.*
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools

import java.io.Serializable
import java.time.Duration
import java.time.chrono.ChronoLocalDateTime

/**
 * 返照法演算法 , 可以計算 Planet 的返照
 */
class ReturnContext(
  /** 最完整的 constructor , 連是否逆推 , 是否考慮歲差，都要帶入  */
  private val horoscopeImpl: IHoroscope,
  /** 計算星體的介面  */
  private var starPositionWithAzimuthImpl: IStarPositionWithAzimuth?,
  /** 計算星體到黃道幾度的時刻，的介面  */
  private var starTransitImpl: IStarTransit,
  private val houseCuspImpl: IHouseCusp,
  private val apsisWithAzimuthImpl: IApsisWithAzimuth,
  /** 出生時間 , LMT  */
  private val natalLmt: ChronoLocalDateTime<*>,
  /** 出生地點  */
  private val natalLoc: Location,
  /** 欲計算的目標時間，通常是當下，now，以LMT型態  */
  private val nowLmt: ChronoLocalDateTime<*>,
  /** 現在所處的地點  */
  private val nowLoc: Location, planet: Planet, orb: Double, converse: Boolean, precession: Boolean) : DiscreteIF, Conversable, Serializable {

  /** 返照法所採用的行星 , 太陽/太陰 , 或是其他  */
  private val planet = Planet.SUN

  /** 是否逆推，內定是順推  */
  private var converse = false

  /** 是否消除歲差，內定是不計算歲差  */
  /** 是否消除歲差  */
  /** 設定是否消除歲差  */
  var isPrecession = false

  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  var orb = 0.0


  /** 對外主要的 method , 取得 return 盤  */
  val returnHoroscope: Horoscope
    get() {
      val natalGmt = TimeTools.getGmtFromLmt(natalLmt, natalLoc)
      val nowGmt = TimeTools.getGmtFromLmt(nowLmt, nowLoc)

      val convergentGmt = getConvergentTime(natalGmt, nowGmt)
      val convergentLmt = TimeTools.getLmtFromGmt(convergentGmt, nowLoc)

      val houseSystem = HouseSystem.PLACIDUS
      val coordinate = Coordinate.ECLIPTIC
      val centric = Centric.GEO
      val temperature = 20.0
      val pressure = 1013.25
      val nodeType = NodeType.MEAN

      return horoscopeImpl.getHoroscope(convergentLmt, nowLoc, houseSystem, centric, coordinate)
    }

  init {
    this.orb = orb
    this.converse = converse
    this.isPrecession = precession
  }


  /**
   * 實作 [Mappable], 注意，在 [AbstractProgression]的實作中，並未要求是GMT；但在這裡，必須**要求是GMT** ！
   * 傳回值也是GMT！
   */
  override fun getConvergentTime(natalGmtTime: ChronoLocalDateTime<*>, nowGmtTime: ChronoLocalDateTime<*>): ChronoLocalDateTime<*> {
    val nowGmtJulDay = TimeTools.getGmtJulDay(nowGmtTime)

    val coordinate = if (isPrecession) Coordinate.SIDEREAL else Coordinate.ECLIPTIC
    //先計算出生盤中，該星體的黃道位置
    val natalPlanetDegree = starPositionWithAzimuthImpl!!.getPosition(planet, natalGmtTime, Centric.GEO, coordinate).lng

    //再從現在的時刻，往前(prior , before) 推 , 取得 planet 與 natal planet 呈現 orb 的時刻
    return if (!converse) {
      //順推
      starTransitImpl.getNextTransitGmtDateTime(planet, Utils.getNormalizeDegree(natalPlanetDegree + orb), coordinate, nowGmtJulDay, false) //false 代表逆推，往before算
    } else {
      // converse == true , 逆推
      //從出生時間往前(before)推
      val d = Duration.between(nowGmtTime, natalGmtTime).abs()
      val beforeNatalGmtJulDay = TimeTools.getGmtJulDay(nowGmtTime.minus(d))
      //要確認最後一個參數，到底是要用 true , 還是 false , 要找相關定義 , 我覺得這裡應該是順推
      starTransitImpl.getNextTransitGmtDateTime(planet, Utils.getNormalizeDegree(natalPlanetDegree + orb), coordinate, beforeNatalGmtJulDay, true) //true 代表順推 , 往 after 算
    }
  }


  override fun setConverse(value: Boolean) {
    this.converse = value
  }

  /** 是否逆推 , true 代表「是」，逆推！  */
  override fun isConverse(): Boolean {
    return converse
  }

  fun setStarPositionWithAzimuthImpl(starPositionWithAzimuthImpl: IStarPositionWithAzimuth) {
    this.starPositionWithAzimuthImpl = starPositionWithAzimuthImpl
  }

  fun setStarTransitImpl(starTransitImpl: IStarTransit) {
    this.starTransitImpl = starTransitImpl
  }


}
