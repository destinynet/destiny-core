/**
 * Created by kevin.huang on 2019-09-26.
 */
package destiny.astrology.classical.rules

import destiny.astrology.Planet
import destiny.core.IPattern

interface IClassicalPattern : IPattern

interface IPlanetPattern : IClassicalPattern {
  val type: RuleType
  val planet: Planet
}

interface IMutualPattern : IClassicalPattern {
  val p1 : Planet
  val p2 : Planet
}