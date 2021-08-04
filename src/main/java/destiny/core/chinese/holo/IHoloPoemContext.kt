package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime

interface IHoloPoemContext : IHoloContext {

  /** 取得 先天卦、後天卦 , 元氣、化工 等資訊 */
  override fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?): IPoemHolo

  /** 除了傳回 本命先後天卦，另外傳回 以及此 gmt 時刻 的大運、流年、流月 等資訊 . 另外加上每個卦象的詩文 */
  fun getHoloWithTimeFull(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, gmt: GmtJulDay, name: String? = null, place: String? = null): Pair<IPoemHolo, List<HoloFullHexagram>>
}
