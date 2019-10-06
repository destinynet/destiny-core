/**
 * Created by kevin.huang on 2019-09-26.
 */
package destiny.astrology.classical.rules

import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.IMutualData
import destiny.core.IPattern

interface IClassicalPattern : IPattern

enum class RuleType {
  ESSENTIAL,
  ACCIDENTAL,
  DEBILITY
}

interface IPlanetPattern : IClassicalPattern {
  val type: RuleType
  val planet: Planet
}


interface IPlanetPatternFactory {

  fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern>

}

interface IMutualPattern : IClassicalPattern, IMutualData
