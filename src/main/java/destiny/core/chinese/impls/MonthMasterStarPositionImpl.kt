/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IStarPosition
import destiny.core.astrology.Planet
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import destiny.core.chinese.IMonthMaster
import destiny.core.chinese.MonthMaster
import destiny.core.chinese.asDescriptive
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

class MonthMasterStarPositionImpl(private val starPositionImpl: IStarPosition<*>) : IMonthMaster, Serializable {

  override fun toString(locale: Locale): String {
    return MonthMaster.StarPosition.asDescriptive().toString(locale)
  }

  override fun getDescription(locale: Locale): String {
    return MonthMaster.StarPosition.asDescriptive().getDescription(locale)
  }

  override fun getBranch(lmt: ChronoLocalDateTime<*>, location: ILocation): Branch {
    val pos = starPositionImpl.getPosition(Planet.SUN, lmt, location, Centric.GEO, Coordinate.ECLIPTIC)
    return pos.sign.branch
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is MonthMasterStarPositionImpl) return false
    return true
  }

  override fun hashCode(): Int {
    return MonthMasterStarPositionImpl::class.java.hashCode()
  }


}
