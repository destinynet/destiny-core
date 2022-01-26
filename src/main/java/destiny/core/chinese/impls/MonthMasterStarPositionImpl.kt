/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.Descriptive
import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.IStarPosition
import destiny.core.astrology.Planet
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import destiny.core.chinese.IMonthMaster
import destiny.core.chinese.MonthMaster
import destiny.tools.asDescriptive
import java.io.Serializable

class MonthMasterStarPositionImpl(private val starPositionImpl: IStarPosition<*>) : IMonthMaster,
                                                                                    Descriptive by MonthMaster.StarPosition.asDescriptive(),
                                                                                    Serializable {

  override fun getBranch(gmtJulDay: GmtJulDay, loc: ILocation): Branch {
    val pos = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, loc, Centric.GEO, Coordinate.ECLIPTIC)
    return pos.sign.branch
  }
}
