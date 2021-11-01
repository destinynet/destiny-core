/**
 * Created by smallufo on 2018-05-12.
 */
package destiny.core.astrology.classical.rules

import destiny.core.Gender
import destiny.core.astrology.Centric
import destiny.core.astrology.Coordinate
import destiny.core.astrology.HouseSystem
import destiny.core.astrology.Planet
import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime
import java.util.*

interface IClassicalContext {

  fun getPatternAndComments(lmt: ChronoLocalDateTime<*>,
                            loc: ILocation,
                            place: String?,
                            gender: Gender,
                            name: String?,
                            locale: Locale = Locale.getDefault(),
                            houseSystem: HouseSystem? = HouseSystem.PLACIDUS,
                            centric: Centric? = Centric.GEO,
                            coordinate: Coordinate? = Coordinate.ECLIPTIC): Map<Planet, List<Pair<IPlanetPattern, String>>>

}

