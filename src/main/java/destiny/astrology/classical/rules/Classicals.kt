/**
 * Created by smallufo on 2018-05-12.
 */
package destiny.astrology.classical.rules

import destiny.astrology.*
import destiny.core.Gender
import destiny.core.calendar.ILocation
import java.time.chrono.ChronoLocalDateTime
import java.util.*

interface IClassicalContext : IPersonHoroscopeContext {

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

