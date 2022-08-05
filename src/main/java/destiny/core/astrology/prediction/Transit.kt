/**
 * Created by smallufo on 2022-08-06.
 */
package destiny.core.astrology.prediction

import destiny.core.calendar.GmtJulDay
import java.io.Serializable


class Transit(override val converse: Boolean = false) : ILinear, Conversable, Serializable {

  override fun getDivergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay {
    return if (!converse) {
      nowGmtJulDay
    } else {
      val diff = nowGmtJulDay - natalGmtJulDay
      (natalGmtJulDay - diff)
    }
  }

  override fun getConvergentTime(natalGmtJulDay: GmtJulDay, nowGmtJulDay: GmtJulDay): GmtJulDay {
    return if (!converse) {
      nowGmtJulDay
    } else {
      val diff = nowGmtJulDay - natalGmtJulDay
      (natalGmtJulDay - diff)
    }
  }
}
