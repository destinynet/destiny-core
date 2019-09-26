/**
 * Created by kevin.huang on 2019-09-26.
 */
package destiny.astrology.classical.rules

import destiny.core.IPattern

interface IClassicalPattern : IPattern {
  val type : RuleType
}