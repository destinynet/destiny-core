/**
 * Created by smallufo on 2017-12-24.
 */
package destiny.astrology

import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.core.calendar.Location
import destiny.core.calendar.TimeTools
import java.io.Serializable
import java.time.temporal.ChronoField
import java.util.*

class DayNightSimpleImpl : DayNightDifferentiator , Serializable {

  @Transient private val revJulDayFunc = { value: Double -> JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(value) }

  override fun getDayNight(gmtJulDay: Double, location: Location): DayNight {
    val lmt = TimeTools.getLmtFromGmt(revJulDayFunc.invoke(gmtJulDay) , location)
    val hour = lmt.get(ChronoField.HOUR_OF_DAY)
    return if (hour in 6..17)
      DayNight.DAY
    else
      DayNight.NIGHT
  }

  override fun getTitle(locale: Locale): String {
    return "最基本的區分法"
  }

  override fun getDescription(locale: Locale): String {
    return "六至十八為白天，其餘為晚上。僅能作為 Test 使用，勿用於 Production 環境"
  }

}
