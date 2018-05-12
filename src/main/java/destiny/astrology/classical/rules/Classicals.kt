/**
 * Created by smallufo on 2018-05-12.
 */
package destiny.astrology.classical.rules

import destiny.astrology.*
import destiny.astrology.classical.IAccidentalDignities
import destiny.astrology.classical.IEssentialDignities
import destiny.astrology.classical.rules.debilities.DebilitiesBean
import destiny.core.Gender
import destiny.core.IBirthDataNamePlace
import destiny.core.calendar.ILocation
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

interface IClassicalModel : IPersonHoroscopeModel {

  val commentMap: Map<Planet, List<String>>

}

data class ClassicalModel(
  private val personModel: IPersonHoroscopeModel,
  override val commentMap: Map<Planet, List<String>>
                         ) : IClassicalModel, IPersonHoroscopeModel by personModel, Serializable

interface IClassicalContext : IPersonHoroscopeContext {
  fun getClassicalCommentMap(lmt: ChronoLocalDateTime<*>,
                             loc: ILocation,
                             place: String?,
                             gender: Gender,
                             name: String?,
                             locale: Locale? = Locale.getDefault(),
                             houseSystem: HouseSystem? = HouseSystem.PLACIDUS,
                             centric: Centric? = Centric.GEO,
                             coordinate: Coordinate? = Coordinate.ECLIPTIC): IClassicalModel

  fun getClassicalCommentMap(lmt: ChronoLocalDateTime<*>,
                             loc: ILocation,
                             place: String?,
                             gender: Gender,
                             name: String?): IClassicalModel {
    return getClassicalCommentMap(lmt, loc, place, gender, name, Locale.getDefault())
  }

  fun getClassicalCommentMap(data: IBirthDataNamePlace): IClassicalModel {
    return getClassicalCommentMap(data.time, data.location, data.place, data.gender, data.name)
  }

  fun getRuleAndComments(lmt: ChronoLocalDateTime<*>,
                         loc: ILocation,
                         place: String?,
                         gender: Gender,
                         name: String?,
                         locale: Locale? = Locale.getDefault(),
                         houseSystem: HouseSystem? = HouseSystem.PLACIDUS,
                         centric: Centric? = Centric.GEO,
                         coordinate: Coordinate? = Coordinate.ECLIPTIC): Map<Planet, List<Pair<IRule, String>>>

  fun getRuleAndComments(data: IBirthDataNamePlace): Map<Planet, List<Pair<IRule, String>>> {
    return getRuleAndComments(data.time, data.location, data.place, data.gender, data.name)
  }
}

class ClassicalContext(
  private val personContext: IPersonHoroscopeContext,
  private val essentialDignitiesImpl: IEssentialDignities,
  private val accidentalDignitiesImpl: IAccidentalDignities,
  private val debilitiesBean: DebilitiesBean,
  val locale: Locale) : IClassicalContext, IPersonHoroscopeContext by personContext {
  override fun getClassicalCommentMap(lmt: ChronoLocalDateTime<*>,
                                      loc: ILocation,
                                      place: String?,
                                      gender: Gender,
                                      name: String?,
                                      locale: Locale?,
                                      houseSystem: HouseSystem?,
                                      centric: Centric?,
                                      coordinate: Coordinate?): IClassicalModel {
    val finalLocale = locale ?: this.locale
    val finalHs = houseSystem ?: this.houseSystem
    val finalCentric = centric ?: this.centric
    val finalCoordinate = coordinate ?: this.coordinate

    val h: IPersonHoroscopeModel =
      personContext.getHoroscope(lmt, loc, place, gender, name, finalHs, finalCentric, finalCoordinate)
    val commentMap: Map<Planet, List<String>> = Planets.classicalList.map { planet ->
      val list1 = essentialDignitiesImpl.getComments(planet, h, finalLocale)
      val list2 = accidentalDignitiesImpl.getComments(planet, h, finalLocale)
      val list3 = debilitiesBean.getComments(planet, h, finalLocale)
      planet to (list1 + list2 + list3)
    }.toMap()

    return ClassicalModel(h, commentMap)
  }

  override fun getRuleAndComments(lmt: ChronoLocalDateTime<*>,
                                  loc: ILocation,
                                  place: String?,
                                  gender: Gender,
                                  name: String?,
                                  locale: Locale?,
                                  houseSystem: HouseSystem?,
                                  centric: Centric?,
                                  coordinate: Coordinate?): Map<Planet, List<Pair<IRule, String>>> {
    val finalLocale = locale ?: this.locale
    val finalHs = houseSystem ?: this.houseSystem
    val finalCentric = centric ?: this.centric
    val finalCoordinate = coordinate ?: this.coordinate

    val h: IPersonHoroscopeModel =
      personContext.getHoroscope(lmt, loc, place, gender, name, finalHs, finalCentric, finalCoordinate)

    val rules1 = essentialDignitiesImpl.rules
    val rules2 = accidentalDignitiesImpl.rules
    val rules3 = debilitiesBean.rules
    val rules = rules1 + rules2 + rules3

    return Planets.classicalList.map { planet ->

      val comments: List<Pair<IRule, String>> = rules.map { rule ->
        rule to rule.getComment(planet, h, finalLocale)
      }.filter { it.second != null }
        .map { pair -> Pair(pair.first, pair.second!!) }

      planet to comments
    }.toMap()
  }
}