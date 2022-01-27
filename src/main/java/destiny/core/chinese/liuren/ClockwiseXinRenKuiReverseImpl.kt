/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren

import destiny.core.Clockwise
import destiny.core.DayNight
import destiny.core.Descriptive
import destiny.core.astrology.IDayNight
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IDayHour
import destiny.core.chinese.IClockwise
import destiny.core.chinese.Stem.*
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 推算貴神
 * 甲乙丙丁戊己庚 皆為晝順夜逆； 辛壬癸 為晝逆夜順
 * XinRenKui Reverse
 */
class ClockwiseXinRenKuiReverseImpl(private val dayHourImpl: IDayHour,
                                    private val differentiator: IDayNight) : IClockwise,
                                                                             Descriptive by destiny.core.chinese.liuren.Clockwise.XinRenKuiReverse.asDescriptive(),
                                                                             Serializable {

  override fun getClockwise(gmtJulDay: GmtJulDay, loc: ILocation): Clockwise {
    val day = dayHourImpl.getDay(gmtJulDay, loc)
    val dayNight = differentiator.getDayNight(gmtJulDay, loc)

    return when (day.stem) {
      甲, 乙, 丙, 丁, 戊, 己, 庚 -> if (dayNight == DayNight.DAY) Clockwise.CLOCKWISE else Clockwise.COUNTER
      辛, 壬, 癸 -> if (dayNight == DayNight.DAY) Clockwise.COUNTER else Clockwise.CLOCKWISE
    }
  }

  companion object {
    const val VALUE = "xrkR"
  }
}
