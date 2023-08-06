/**
 * Created by smallufo on 2023-08-06.
 */
package destiny.core.calendar

import java.time.ZoneId


data class SolarTermsEvent(override val begin: GmtJulDay,
                           /** 節氣  */
                           val solarTerms: SolarTerms) : IEvent {
  override val zoneId: ZoneId = GMT
}
