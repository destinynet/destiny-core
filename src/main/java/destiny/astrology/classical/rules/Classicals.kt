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

class ClassicalContext(
  private val classicalPatternContext: ClassicalPatternContext,
  private val personContext: IPersonHoroscopeContext
) : IClassicalContext, IPersonHoroscopeContext by personContext {



  override fun getPatternAndComments(lmt: ChronoLocalDateTime<*>, loc: ILocation, place: String?, gender: Gender, name: String?, locale: Locale, houseSystem: HouseSystem?, centric: Centric?, coordinate: Coordinate?):
    Map<Planet, List<Pair<IPlanetPattern, String>>> {
    val h: IPersonHoroscopeModel =
      personContext.getPersonHoroscope(lmt, loc, place, gender, name)


    val factories: List<IPlanetPatternFactory> = classicalPatternContext.let {
      it.essentialDignities.plus(it.accidentalDignities).plus(it.debilities)
    }

    return Planet.classicalList.map { planet ->
      val list = factories.flatMap { factory ->
        factory.getPatterns(planet , h)
      }.map { pattern ->
        pattern to patternTranslator.getDescriptor(pattern)
      }.map { (pattern , descriptor) ->
        pattern to descriptor.getDescription(locale)
      }

      planet to list
    }.toMap()
  }
}
