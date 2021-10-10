/**
 * Created by smallufo on 2019-05-02.
 */
package destiny.core.calendar.eightwords

import destiny.core.calendar.eightwords.DayHourConfigBuilder.Companion.dayHour

/**
 * 整合「日」與「時」的實作
 */
interface IDayHour : IDay, IHour {

  val hourImpl: IHour

  val config: DayHourConfig
    get() {
      return dayHour {
        day {
          changeDayAfterZi = this@IDayHour.changeDayAfterZi
          midnight = when (this@IDayHour.midnightImpl) {
            is MidnightSolarTransImpl -> DayConfig.MidnightImpl.NADIR
            is MidnightLmtImpl        -> DayConfig.MidnightImpl.CLOCK0
            else                      -> error("no midnight")
          }
        }
        hourBranch {
          hourImpl = when (this@IDayHour.hourImpl) {
            is HourSolarTransImpl -> HourImpl.TST
            is HourLmtImpl        -> HourImpl.LMT
            else                  -> error("no hour")
          }
        }
      }
    }
}
