package destiny.core.chinese.lunarStation

import destiny.core.Descriptive
import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.Planet.Companion.aheadOf
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/**
 * 時禽
 */
interface ILunarStationHourly : Descriptive {

  fun getHourly(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation
}

/**
 * 以「元」之首為定義
 *
 * 每「元」的最後一個時辰，與次元第一個時辰 是不連續的！
 *
 * 《禽星易見》：
 * 七曜禽星會者稀，
 * 日虛月鬼火從箕，
 * 水畢木氐金奎位，
 * 土宿還從翼上推，
 * 常將日禽尋時禽，
 * 但向禽中索取時。
 * 會者一元倒一指，
 * 不會七元七首詩。
 *
 * 一元甲子的星期天的子時是 [虛] 日鼠，
 * 二元甲子的星期天的子時是 [鬼] 金羊，
 * 三元甲子的星期天的子時是 [箕] 水豹，
 * 四元甲子的星期天的子時是 [畢] 月烏，
 * 五元甲子的星期天的子時是 [氐] 土貉，
 * 六元甲子的星期天的子時是 [奎] 木狼，
 * 七元甲子的星期天的子時是 [翼] 火蛇。
 */
@Impl(
  [
    Domain(Domains.LunarStation.HourImpl.KEY_GENERAL, LunarStationHourlyYuanImpl.VALUE, true),
    Domain(Domains.LunarStation.HourImpl.KEY_SELECT, LunarStationHourlyYuanImpl.VALUE)
  ]
)
class LunarStationHourlyYuanImpl(private val dailyImpl: ILunarStationDaily,
                                 private val dayHourImpl: IDayHour) : ILunarStationHourly, Serializable {

  override fun getHourly(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {

    val (dayStation, dayYuan) = dailyImpl.getDailyIndex(lmt, loc).let { it.station() to it.yuan() }
    val dayPlanet = dayStation.planet

    val hourBranch: Branch = dayHourImpl.getHour(lmt, loc)

    // 該元 週日子時
    val sundayHourStart = yuanSundayHourStartMap[dayYuan]!!
    val hourSteps = dayPlanet.aheadOf(SUN) * 12 + hourBranch.getAheadOf(Branch.子)

    sundayHourStart.next(hourSteps)

    return sundayHourStart.next(hourSteps)
  }

  override fun toString(locale: Locale): String {
    return NAME
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarStationHourlyYuanImpl) return false

    if (dailyImpl != other.dailyImpl) return false
    if (dayHourImpl != other.dayHourImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = dailyImpl.hashCode()
    result = 31 * result + dayHourImpl.hashCode()
    return result
  }


  companion object {
    const val NAME = "《禽星易見》"
    const val VALUE = "YUAN"

    val yuanSundayHourStartMap = mapOf(
      1 to 虛,
      2 to 鬼,
      3 to 箕,
      4 to 畢,
      5 to 氐,
      6 to 奎,
      7 to 翼,
    )
  }
}


/**
 * 時禽 ， 固定排列 , 不分七元。 此法簡便易排，民間禽書常用此法。
 *
 * (日) 日宿子時起 [虛] 日鼠，
 * (一) 月宿子時起 [鬼] 金羊，
 * (二) 火宿子時起 [箕] 水豹，
 * (三) 水宿子時起 [畢] 月烏，
 * (四) 木宿子時起 [氐] 土貉，
 * (五) 金宿子時起 [奎] 木狼，
 * (六) 土宿子時起 [翼] 火蛇。
 */
@Impl(
  [
    Domain(Domains.LunarStation.HourImpl.KEY_GENERAL, LunarStationHourlyFixedImpl.VALUE),
    Domain(Domains.LunarStation.HourImpl.KEY_SELECT, LunarStationHourlyFixedImpl.VALUE, true)
  ]
)
class LunarStationHourlyFixedImpl(private val dailyImpl: ILunarStationDaily,
                                  private val dayHourImpl: IDayHour) : ILunarStationHourly, Serializable {

  override fun getHourly(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {

    val dayStation = dailyImpl.getDaily(lmt, loc)

    val start = when (dayStation.planet) {
      SUN -> 虛
      MOON -> 鬼
      MARS -> 箕
      MERCURY -> 畢
      JUPITER -> 氐
      VENUS -> 奎
      SATURN -> 翼
      else -> throw IllegalArgumentException("Impossible")
    }

    val hourSteps = dayHourImpl.getHour(lmt, loc).getAheadOf(Branch.子)
    return start.next(hourSteps)
  }

  override fun toString(locale: Locale): String {
    return NAME
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is LunarStationHourlyFixedImpl) return false

    if (dailyImpl != other.dailyImpl) return false
    if (dayHourImpl != other.dayHourImpl) return false

    return true
  }

  override fun hashCode(): Int {
    var result = dailyImpl.hashCode()
    result = 31 * result + dayHourImpl.hashCode()
    return result
  }


  companion object {
    const val NAME = "《剋擇講義》"
    const val VALUE = "FIXED"
  }
}
