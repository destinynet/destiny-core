package destiny.core.astrology.prediction

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.TimeTools
import java.time.chrono.ChronoLocalDateTime

/**
 * 推運，其實是 『可對應（收斂 , Converge）到某個日期』的推運法。<BR></BR>
 * 例如 ProgressionSecondary , ProgressionTertiary , ProgressionMinor , Solar Return , Lunar Return 皆屬此類 <BR></BR>
 * 而太陽弧 (Solar Arc) 則不屬於此類。因為其星盤並沒有可對應的日期
 */
interface Mappable {

  /**
   * 取得對應的時間 , 通常是收斂到某日期
   *
   * @param natalGmtJulDay 出生時刻
   * @param nowGmtJulDay 欲查閱的時刻 (generally now)
   * @return 「收斂」到的時間
   */
  fun getConvergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay

  /**
   * 承上 [ChronoLocalDateTime] 版本
   */
  fun getConvergentTime(natalTime: ChronoLocalDateTime<*>, nowTime: ChronoLocalDateTime<*>,
                        julDayResolver: JulDayResolver = JulDayResolver1582CutoverImpl()): ChronoLocalDateTime<*> {
    val natalGmtJulDay = TimeTools.getGmtJulDay(natalTime)
    val nowGmtJulDay = TimeTools.getGmtJulDay(nowTime)

    return julDayResolver.getLocalDateTime(getConvergentTime(natalGmtJulDay, nowGmtJulDay))
  }

}
