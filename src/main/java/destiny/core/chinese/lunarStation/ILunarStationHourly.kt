package destiny.core.chinese.lunarStation

import destiny.core.astrology.LunarStation
import destiny.core.astrology.LunarStation.*
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.Companion.aheadOf
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.chinese.Branch
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * 時禽
 */
interface ILunarStationHourly {

  fun getHourlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation
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
    Domain(Domains.LunarStation.KEY_HOUR, LunarStationHourlyYuanImpl.VALUE, true),
    Domain(Domains.LunarStation.KEY_HOUR_SELECTION, LunarStationHourlyYuanImpl.VALUE)
  ]
)
class LunarStationHourlyYuanImpl(private val dailyImpl: ILunarStationDaily,
                                 private val dayHourImpl: IDayHour) : ILunarStationHourly, Serializable {

  override fun getHourlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {

    val (dayStation, dayYuan) = dailyImpl.getDailyStation(lmt, loc)
    val dayPlanet = dayStation.planet

    val hourBranch: Branch = dayHourImpl.getHour(lmt, loc)

    // 該元 週日子時
    val sundayHourStart = yuanSundayHourStartMap[dayYuan]!!
    val hourSteps = dayPlanet.aheadOf(Planet.SUN) * 12 + hourBranch.getAheadOf(Branch.子)

    sundayHourStart.next(hourSteps)

    return sundayHourStart.next(hourSteps)
  }

  companion object {
    const val VALUE = "YUAN"

    private val yuanSundayHourStartMap = mapOf(
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
    Domain(Domains.LunarStation.KEY_HOUR, LunarStationHourlyFixedImpl.VALUE),
    Domain(Domains.LunarStation.KEY_HOUR_SELECTION, LunarStationHourlyFixedImpl.VALUE, true)
  ]
)
class LunarStationHourlyFixedImpl(private val dailyImpl: ILunarStationDaily,
                                  private val dayHourImpl: IDayHour) : ILunarStationHourly, Serializable {

  override fun getHourlyStation(lmt: ChronoLocalDateTime<*>, loc: ILocation): LunarStation {

    val (dayStation, _) = dailyImpl.getDailyStation(lmt, loc)

    val start = when (dayStation.planet) {
      Planet.SUN -> 虛
      Planet.MOON -> 鬼
      Planet.MARS -> 箕
      Planet.MERCURY -> 畢
      Planet.JUPITER -> 氐
      Planet.VENUS -> 奎
      Planet.SATURN -> 翼
      else -> throw IllegalArgumentException("Impossible")
    }

    val hourSteps = dayHourImpl.getHour(lmt, loc).getAheadOf(Branch.子)
    return start.next(hourSteps)
  }


  companion object {
    const val VALUE = "FIXED"
  }
}
