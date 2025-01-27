/**
 * Created by smallufo on 2017-07-10.
 */
package destiny.core.astrology

import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.astrology.classical.rules.Misc
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

fun Map<Int, ZodiacDegree>.getCuspDegree(cusp: Int): ZodiacDegree {
  if (cusp > 12)
    return this.getCuspDegree(cusp - 12)
  else if (cusp < 1)
    return this.getCuspDegree(cusp + 12)
  return this.getValue(cusp)
}


data class HoroscopeModel(

  /** GMT's Julian Day */
  override val gmtJulDay: GmtJulDay,

  override val location: ILocation,

  val config: IHoroscopeConfig,


  /** 星體位置表 */
  override val positionMap: Map<AstroPoint, IPosWithAzimuth>,

  /** 地盤 12宮 (1~12) , 每宮宮首在黃道幾度*/
  override val cuspDegreeMap: Map<Int, ZodiacDegree>,

  /** 行星空亡表 */
  override val vocMap: Map<Planet, Misc.VoidCourseSpan>,

  /** 行星時 , Planetary Hour */
  override val planetaryHour: PlanetaryHour?,

  /** 星體逆行狀態 */
  override val retrogradePhaseMap: Map<Star, RetrogradePhase>,

  /** ruler */
  override val rulingHouseMap: Map<Planet, Set<RulingHouse>>
) : IHoroscopeModel, Serializable {

  override val time: ChronoLocalDateTime<*>
    get() = TimeTools.getLmtFromGmt(gmtJulDay, location, JulDayResolver1582CutoverImpl())

  /** 地名 */
  override val place: String? = config.place

  /** 分宮法  */
  override val houseSystem: HouseSystem = config.houseSystem

  /** 座標系統  */
  override val coordinate: Coordinate = config.coordinate

  /** 中心系統  */
  override val centric: Centric = config.centric

  /** 溫度  */
  override val temperature: Double = config.temperature

  /** 壓力  */
  override val pressure: Double = config.pressure

}

/**
 * 與「人」綁定的星盤資料
 */
interface IPersonHoroscopeModel : IHoroscopeModel, IBirthDataNamePlace {
  override val gender: Gender
  override val name: String?
}

/**
 * 與「人」綁定的星盤資料
 */
data class PersonHoroscopeModel(
  private val horoscopeModel: IHoroscopeModel,
  override val gender: Gender,
  override val name: String?
) : IPersonHoroscopeModel, IHoroscopeModel by horoscopeModel
