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
import destiny.tools.JSerializable

class MonthMasterStarPositionImpl(private val starPositionImpl: IStarPosition<*>) : IMonthMaster,
                                                                                    Descriptive by MonthMaster.StarPosition.asDescriptive(),
                                                                                    JSerializable {

  override fun getBranch(gmtJulDay: GmtJulDay, loc: ILocation): Branch {
    val pos = starPositionImpl.calculate(Planet.SUN, gmtJulDay, Centric.GEO, Coordinate.ECLIPTIC)
    return pos.sign.branch
  }
}
