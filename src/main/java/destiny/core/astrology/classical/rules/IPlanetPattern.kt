/**
 * Created by smallufo on 2019-09-26.
 */
package destiny.core.astrology.classical.rules

import destiny.core.IPattern
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.Planet
import destiny.core.astrology.classical.IMutualData
import java.io.Serializable

interface IClassicalPattern : IPattern

enum class RuleType {
  ESSENTIAL,
  ACCIDENTAL,
  DEBILITY,
  MISC
}

interface IPlanetPattern : IClassicalPattern {
  val type: RuleType
  val planet: Planet
}


interface IPlanetPatternFactory : Serializable {

  fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern>

}

interface IMutualPattern : IClassicalPattern, IMutualData
