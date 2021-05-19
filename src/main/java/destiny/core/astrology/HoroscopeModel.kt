/**
 * Created by smallufo on 2017-07-10.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.ITimeLoc
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import kotlin.math.abs

/**
 * 與「人」無關的星盤資料
 * 沒有性別 [destiny.core.Gender]
 */
interface IHoroscopeModel : ITimeLoc {

  /** 地名 */
  val place: String?

  /** 分宮法  */
  val houseSystem: HouseSystem

  /** 座標系統  */
  val coordinate: Coordinate

  /** 中心系統  */
  val centric: Centric

  /** 溫度  */
  val temperature: Double?

  /** 壓力  */
  val pressure: Double?

  /** 星體位置表 */
  val positionMap: Map<Point, IPosWithAzimuth>

  /** 地盤 12宮 (1~12) , 每宮宮首在黃道幾度*/
  val cuspDegreeMap: Map<Int, ZodiacDegree>

  // ==================================== 以下為 推導值 ====================================

  /**
   * 星體於黃經的度數
   */
  val pointDegreeMap: Map<Point, ZodiacDegree>
    get() = positionMap.mapValues { (_, posWithAzimuth) -> posWithAzimuth.lngDeg }

  /**
   * 星體於黃道上的星座
   */
  val pointSignMap: Map<Point, ZodiacSign>
    get() = pointDegreeMap.mapValues { (_, lngDeg) -> lngDeg.sign }

  /**
   * @return 取得 GMT 時刻
   */
  val gmt: ChronoLocalDateTime<*>
    get() = julDayResolver.getLocalDateTime(gmtJulDay)

  /**
   * 承上 , 取得 GMT , 但帶入自訂的 reverse Julian Day converter
   */
  fun getGmt(revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    return revJulDayFunc.invoke(gmtJulDay)
  }


  /**
   * @return 取得 LMT 時刻
   */
  val lmt: ChronoLocalDateTime<*>
    get() = TimeTools.getLmtFromGmt(gmt, location)

  /**
   * 承上 , 取得 LMT , 但帶入自訂的 reverse Julian Day converter
   */
  fun getLmt(revJulDayFunc: Function1<Double, ChronoLocalDateTime<*>>): ChronoLocalDateTime<*> {
    return TimeTools.getGmtFromLmt(getGmt(revJulDayFunc), location)
  }

  val points: Set<Point>
    get() = positionMap.keys

  /**
   * 取得所有行星 [Planet] 的位置
   */
  val planetPositionWithAzimuth: Map<Planet, IPosWithAzimuth>
    get() = positionMap
      .filter { it.key is Planet }
      .mapKeys { it.key as Planet }
      .toMap()

  /**
   * 取得所有小行星 [Asteroid] 的位置
   */
  val asteroidPositionWithAzimuth: Map<Asteroid, IPosWithAzimuth>
    get() = positionMap
      .filter { it.key is Asteroid }
      .mapKeys { it.key as Asteroid }
      .toMap()

  /**
   * 取得八個漢堡學派虛星 [Hamburger] 的位置
   */
  val hamburgerPositionWithAzimuth: Map<Hamburger, IPosWithAzimuth>
    get() = positionMap
      .filter { it.key is Hamburger }
      .mapKeys { it.key as Hamburger }
      .toMap()


  /**
   * 黃道幾度，落於第幾宮 ( 1 <= house <= 12 )
   */
  fun getHouse(zodiacDegree: ZodiacDegree): Int {
    return Companion.getHouse(zodiacDegree, cuspDegreeMap)
  }

  /**
   * 取得第幾宮內的星星列表 , 1 <= index <=12 , 並且按照黃道度數「由小到大」排序
   */
  fun getHousePoints(index: Int): List<Point> {
    if (index < 1)
      return getHousePoints(index + 12)
    return if (index > 12) getHousePoints(index - 12)
    else positionMap.filter { (_, posWithAzimuth) -> getHouse(posWithAzimuth.lngDeg) == index }
      .map { it.key }
  }

  /**
   * 所有宮位內，的星體列表 , 並且按照黃道度數「由小到大」排序
   */
  val houseMap: Map<Int, List<Point>>
    get() {
      return (1..12).associateWith { houseIndex ->
        positionMap.filter { (_, posWithAzimuth) -> getHouse(posWithAzimuth.lngDeg) == houseIndex }
          .map { it.key }
      }
    }

  /**
   * 取得第幾宮的宮首落於黃道幾度。
   *
   * @param cusp 1 <= cusp <= 12
   */
  fun getCuspDegree(cusp: Int): ZodiacDegree {
    if (cusp > 12)
      return getCuspDegree(cusp - 12)
    else if (cusp < 1)
      return getCuspDegree(cusp + 12)
    return cuspDegreeMap.getValue(cusp)
  }


  /**
   * 取得單一 Horoscope 中 , 任兩顆星的交角
   */
  fun getAngle(fromPoint: Point, toPoint: Point): Double {
    return positionMap.getValue(fromPoint).lngDeg.getAngle(positionMap.getValue(toPoint).lngDeg)
  }


  /** 取得一顆星體 Point / Star 在星盤上的角度  */
  fun getPositionWithAzimuth(point: Point): IPosWithAzimuth {
    return positionMap.getValue(point)
  }


  /**
   * 取得一連串星體的位置（含地平方位角）
   */
  fun getPositionWithAzimuth(stars: List<Star>): Map<Star, IPosWithAzimuth> {
    return positionMap
      .filter { (k, _) -> stars.contains(k) }
      .mapKeys { it as Star }
      .toMap()
  }


  fun getPositionWithAzimuth(clazz: Class<out Point>): Map<Point, IPosWithAzimuth> {
    return positionMap
      .filter { (k, _) -> k.javaClass == clazz }
      .toMap()
  }

  /**
   * @param point 取得此星體在第幾宮
   */
  fun getHouse(point: Point): Int? {
    return positionMap[point]?.let { pos -> getHouse(pos.lngDeg) }
  }

  /** 取得星體的位置以及地平方位角  */
  fun getPosition(point: Point): IPosWithAzimuth? {
    return positionMap[point]
  }

  fun getStarPosition(star : Star) : IStarPositionWithAzimuth? {
    return positionMap[star]?.takeIf { it is IStarPositionWithAzimuth }?.let { it as IStarPositionWithAzimuth }
  }

  /** 取得某星 位於什麼星座  */
  fun getZodiacSign(point: Point): ZodiacSign? {
    return getPosition(point)?.lngDeg?.sign
  }

  companion object {

    fun getHouse(degree: ZodiacDegree , cuspDegreeMap: Map<Int, ZodiacDegree>) : Int {
      return (1..11).firstOrNull { house ->
        if ((abs(cuspDegreeMap.getValue(house + 1).value - cuspDegreeMap.getValue(house).value) < 180)) {
          //沒有切換360度的問題
          cuspDegreeMap.getValue(house).value <= degree.value && degree.value < cuspDegreeMap.getValue(house + 1).value
        } else {
          //切換360度
          cuspDegreeMap.getValue(house).value <= degree.value && degree.value < cuspDegreeMap.getValue(house + 1).value + 360 ||
            cuspDegreeMap.getValue(house).value <= degree.value + 360 && degree.value < cuspDegreeMap.getValue(house + 1).value
        }
      } ?:12
    }

    /**
     * 取得此兩顆星，對於此交角 Aspect 的誤差是幾度
     * 例如兩星交角 175 度 , Aspect = 沖 (180) , 則 誤差 5 度
     */
    fun getAspectError(positionMap: Map<Point, IPos> , p1:Point , p2:Point , aspect: Aspect) : Double? {
      return positionMap[p1]?.let { p1Pos ->
        positionMap[p2]?.let { p2Pos ->
          val angle = p1Pos.lngDeg.getAngle(p2Pos.lngDeg)
          abs(aspect.degree - angle)
        }
      }
    }

    private val julDayResolver = JulDayResolver1582CutoverImpl()
  } // companion

} // interface

data class HoroscopeModel(

  /** GMT's Julian Day */
  override val gmtJulDay: Double,

  override val location: ILocation,

  /** 地名 */
  override val place: String?,

  /** 分宮法  */
  override val houseSystem: HouseSystem,

  /** 座標系統  */
  override val coordinate: Coordinate,

  /** 中心系統  */
  override val centric: Centric,

  /** 溫度  */
  override val temperature: Double?,

  /** 壓力  */
  override val pressure: Double?,

  /** 星體位置表 */
  override val positionMap: Map<Point, IPosWithAzimuth>,

  /** 地盤 12宮 (1~12) , 每宮宮首在黃道幾度*/
  override val cuspDegreeMap: Map<Int, ZodiacDegree>) : IHoroscopeModel, Serializable {

  override val time: ChronoLocalDateTime<*>
    get() = TimeTools.getLmtFromGmt(gmtJulDay, location, JulDayResolver1582CutoverImpl())
}

/**
 * 與「人」綁定的星盤資料
 */
interface IPersonHoroscopeModel : IHoroscopeModel {
  val gender: Gender
  val name: String?
}

/**
 * 與「人」綁定的星盤資料
 */
data class PersonHoroscopeModel(
  private val horoscopeModel: IHoroscopeModel,
  override val gender: Gender,
  override val name: String?) : IPersonHoroscopeModel, IHoroscopeModel by horoscopeModel
