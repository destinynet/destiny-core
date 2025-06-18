/**
 * @author smallufo
 * Created on 2008/5/29 at 上午 3:15:13
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.*
import destiny.core.astrology.classical.IRuler
import destiny.core.astrology.classical.RulerPtolemyImpl
import destiny.core.astrology.classical.VoidCourseImpl
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.toLmt
import destiny.tools.KotlinLogging
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.math.absoluteValue

/**
 * from , to : 此 return context 有效期限
 * 若為 converse return , @from 則會 after @to
 */
data class ReturnModel(val horoscope: IHoroscopeModel, val from: GmtJulDay, val to: GmtJulDay)

interface IReturnContext : Conversable, IDiscrete {

  val planet: Planet

  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  val orb: Double

  /** 是否消除歲差， false = 不計算歲差  */
  val precession: Boolean

  /** 對外主要的 method , 取得 return 盤  */
  fun getReturnHoroscope(natalModel: IHoroscopeModel, nowGmtJulDay: GmtJulDay, nowLoc: ILocation, nowPlace: String? = null): ReturnModel

  fun IHoroscopeModel.getReturnDto(
    gmtJulDay: GmtJulDay,
    nowLoc: ILocation,
    aspectEffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig,
    nowPlace: String?,
    threshold: Double?
  ): ReturnDto
}


/**
 * 返照法演算法 , 可以計算 Planet 的返照
 */
class ReturnContext(
  /** 返照法所採用的行星 , 太陽/太陰 , 或是其他  */
  override val planet: Planet = Planet.SUN,
  /** 計算星體的介面  */
  private val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator,
  /** 計算星體到黃道幾度的時刻，的介面  */
  private val starTransitImpl: IStarTransit,
  private val horoscopeFeature: IHoroscopeFeature,
  private val dtoFactory: DtoFactory,
  private val julDayResolver: JulDayResolver,

  /** 是否順推 , true 代表順推 , false 則為逆推 */
  override val forward: Boolean = true,
  /** 交角 , 通常是 0 , 代表回歸到原始度數  */
  override val orb: Double = 0.0,
  /** 是否消除歲差，內定是不計算歲差  */
  override val precession: Boolean = false
) : IReturnContext, Serializable {

  override fun getReturnHoroscope(natalModel: IHoroscopeModel, nowGmtJulDay: GmtJulDay, nowLoc: ILocation, nowPlace: String?): ReturnModel {
    return getConvergentPeriod(natalModel.gmtJulDay, nowGmtJulDay).let { (from, to) ->

      val config = HoroscopeConfig(
        natalModel.points,
        HouseSystem.PLACIDUS,
        Coordinate.ECLIPTIC,
        Centric.GEO,
        0.0,
        1013.25,
        VoidCourseImpl.Medieval,
        nowPlace ?: natalModel.place,
      )
      val horoscope = horoscopeFeature.getModel(from, nowLoc, config)
      ReturnModel(horoscope, from, to)
    }
  }


  override fun getConvergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay {
    val coordinate = if (precession) Coordinate.SIDEREAL else Coordinate.ECLIPTIC
    // 先計算出生盤中，該星體的黃道位置
    val natalPlanetDegree: ZodiacDegree = starPositionWithAzimuthImpl.getPosition(planet, natalGmtJulDay, Centric.GEO, coordinate).lngDeg

    return if (forward) {
      // 從現在時刻 順推 , 取得 planet 回歸到 natal 度數(plus orb) 的時刻
      starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay, false, coordinate) //false 代表逆推，往before算
    } else {
      // 逆推
      // 從出生時間往前(before)推
      val d = (natalGmtJulDay - nowGmtJulDay).absoluteValue
      val beforeNatalGmtJulDay = natalGmtJulDay - d

      starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), beforeNatalGmtJulDay, false, coordinate) //false 代表逆推 , 往 before 算
    }
  }

  override fun getConvergentPeriod(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): Pair<GmtJulDay, GmtJulDay> {
    val coordinate = if (precession) Coordinate.SIDEREAL else Coordinate.ECLIPTIC
    // 先計算出生盤中，該星體的黃道位置
    val natalPlanetDegree: ZodiacDegree = starPositionWithAzimuthImpl.getPosition(planet, natalGmtJulDay, Centric.GEO, coordinate).lngDeg

    // 再從現在的時刻，往前(prior , before) 推 , 取得 planet 與 natal planet 呈現 orb 的時刻
    return if (forward) {
      // 順推
      val from = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay, false, coordinate)
      val to = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), nowGmtJulDay, true, coordinate)
      from to to
    } else {
      // 逆推
      // 從出生時間往前(before)推
      val d = (natalGmtJulDay - nowGmtJulDay).absoluteValue
      val beforeNatalGmtJulDay = natalGmtJulDay - d

      val from = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), beforeNatalGmtJulDay, true, coordinate)
      val to = starTransitImpl.getNextTransitGmt(planet, (natalPlanetDegree + orb), beforeNatalGmtJulDay, false, coordinate)
      from to to
    }
  }

  override fun IHoroscopeModel.getReturnDto(
    gmtJulDay: GmtJulDay,
    nowLoc: ILocation,
    aspectEffective: IAspectEffective,
    aspectCalculator: IAspectCalculator,
    config: IHoroscopeConfig,
    nowPlace: String?,
    threshold: Double?
  ): ReturnDto {
    val returnModel: ReturnModel = getReturnHoroscope(this, gmtJulDay, nowLoc, nowPlace)

    val horoscopeDto: IHoroscopeDto = with(dtoFactory) {
      returnModel.horoscope.toHoroscopeDto(rulerImpl, aspectEffective, aspectCalculator, config)
    }.let { it as HoroscopeDto }
      // 移除 中點資訊，畢竟這在 return chart 參考度不高
      .copy(midPoints = emptyList())

    val synastry = horoscopeFeature.synastry(returnModel.horoscope, this, aspectCalculator, threshold)

    val from = returnModel.from.toLmt(nowLoc, julDayResolver) as LocalDateTime
    val to = returnModel.to.toLmt(nowLoc, julDayResolver) as LocalDateTime
    return when (this@ReturnContext.planet) {
      Planet.SUN  -> SolarReturnDto(horoscopeDto, synastry, from, to)
      Planet.MOON -> LunarReturnDto(horoscopeDto, synastry, from, to)
      else        -> throw IllegalArgumentException("Unsupported planet: $planet")
    }
  }





  companion object {
    private val rulerImpl: IRuler = RulerPtolemyImpl()
    val logger = KotlinLogging.logger { }
  }

}
