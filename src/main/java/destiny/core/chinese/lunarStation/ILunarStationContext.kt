package destiny.core.chinese.lunarStation

import destiny.core.Scale
import destiny.core.astrology.LunarStation
import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime

interface ILunarStationContext {


  fun getModels(lmt: ChronoLocalDateTime<*>, loc: ILocation,
                scales: List<Scale> = listOf(Scale.YEAR, Scale.MONTH, Scale.DAY, Scale.HOUR)): Map<Scale, LunarStation>
}
