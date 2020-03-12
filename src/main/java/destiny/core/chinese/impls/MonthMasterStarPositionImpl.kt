/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.astrology.Centric
import destiny.astrology.Coordinate
import destiny.astrology.IStarPosition
import destiny.astrology.Planet
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import destiny.core.chinese.IMonthMaster
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

class MonthMasterStarPositionImpl(private val starPositionImpl: IStarPosition<*>) : IMonthMaster, Serializable {

  override fun toString(locale: Locale): String {
    return "星體觀測（過中氣）"
  }

  override fun getDescription(locale: Locale): String {
    return "真實觀測太陽在黃道的度數，判斷月將（太陽星座）"
  }

  override fun getBranch(lmt: ChronoLocalDateTime<*>, location: ILocation): Branch {
    val pos = starPositionImpl.getPosition(Planet.SUN, lmt, location, Centric.GEO, Coordinate.ECLIPTIC)
    return pos.sign.branch
  }
}
