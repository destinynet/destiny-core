/**
 * @author smallufo
 * Created on 2007/12/31 at 下午 10:42:30
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.IDayNight
import destiny.astrology.IBesieged
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.IDebilities
import destiny.astrology.classical.IRefranation
import destiny.astrology.classical.rules.IRule
import java.io.Serializable
import java.util.*

class DebilitiesBean(private val dayNightImpl: IDayNight,
                     private val besiegedImpl: IBesieged,
                     private val refranationImpl: IRefranation) : IDebilities, Serializable {


  override val rules: List<IRule> by lazy {
    listOf(
      Detriment()
      , Fall()
      , Peregrine(dayNightImpl)
      , House_12()
      , House_6_8()
      , Retrograde()
      , Slower()
      , Occidental()
      , Oriental()
      , Moon_Decrease_Light()
      , Combustion()
      , Sunbeam()
      , Partile_Conj_Mars_Saturn()
      , Partile_Conj_South_Node()
      , Besieged_Mars_Saturn(besiegedImpl)
      , Partile_Oppo_Mars_Saturn()
      , Partile_Square_Mars_Saturn()
      , Conj_Algol()
      , Out_of_Sect(dayNightImpl)
      //, MutualDeception(essentialImpl)
      , Refrain_from_Venus_Jupiter(refranationImpl)
    )
  }


  override fun getComments(planet: Planet, h: IHoroscopeModel, locale: Locale): List<String> {
    return rules
      .map { it.getComment(planet, h, locale) }
      .filter { it != null }
      .map { it -> it!! }
      .toList()
  }


}
