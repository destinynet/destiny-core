/**
 * Created by smallufo on 2022-10-11.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import javax.inject.Named


/**
 * 單純以時鐘劃分 行星時
 */
@Named
class PlanetaryHourClockImpl : IPlanetaryHour {

  override fun getPlanetaryHour(gmtJulDay: GmtJulDay, loc: ILocation, transConfig: TransConfig): PlanetaryHour? {
    TODO("Not yet implemented")
  }

  override fun getPlanetaryHours(fromGmt: GmtJulDay, toGmt: GmtJulDay, loc: ILocation, transConfig: TransConfig): List<PlanetaryHour> {
    TODO("Not yet implemented")
  }
}
