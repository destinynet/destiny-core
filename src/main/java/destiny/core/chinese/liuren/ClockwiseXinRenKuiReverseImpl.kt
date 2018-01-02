/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.core.calendar.Location
import destiny.core.calendar.eightwords.IDay
import destiny.core.calendar.eightwords.IHour
import destiny.core.calendar.eightwords.IMidnight
import destiny.core.chinese.Clockwise
import destiny.core.chinese.ClockwiseIF
import destiny.core.chinese.Stem.*
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/**
 * 推算貴神
 * 甲乙丙丁戊己庚 皆為晝順夜逆； 辛壬癸 為晝逆夜順
 * XinRenKui Reverse
 */
class ClockwiseXinRenKuiReverseImpl(private val dayImpl: IDay,
                                    private val midnightImpl: IMidnight,
                                    private val hourImpl: IHour,
                                    private val changeDayAfterZi: Boolean,
                                    private val differentiator: DayNightDifferentiator) : ClockwiseIF, Serializable {

  override fun getClockwise(lmt: ChronoLocalDateTime<*>, loc: Location): Clockwise {
    val day = dayImpl.getDay(lmt, loc, midnightImpl, hourImpl, changeDayAfterZi)
    val dayNight = differentiator.getDayNight(lmt, loc)

    return when (day.stem) {
      甲, 乙, 丙, 丁, 戊, 己, 庚 -> if (dayNight == DayNight.DAY) Clockwise.CLOCKWISE else Clockwise.COUNTER
      辛, 壬, 癸 -> if (dayNight == DayNight.DAY) Clockwise.COUNTER else Clockwise.CLOCKWISE
    }
  }

  override fun getTitle(locale: Locale): String {
    return "辛壬癸 晝逆夜順"
  }

  override fun getDescription(locale: Locale): String {
    return "甲乙丙丁戊己庚 皆為晝順夜逆； 辛壬癸 為晝逆夜順"
  }
}
