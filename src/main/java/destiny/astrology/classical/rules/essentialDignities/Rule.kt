/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:35:27
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.classical.*
import destiny.astrology.classical.rules.AbstractRule

abstract class Rule : AbstractRule(resource), Applicable {

  /** 具備計算 Ptolemy's Table of Essential Dignities and Debilities 的所有介面  */
  var essentialImpl: IEssential = EssentialDefaultImpl()

  val termsImpl : ITerms = TermsPtolomyImpl()
  val triplicityImpl : ITriplicity = TriplicityWilliamImpl()

  val rulerImpl : IRuler = RulerPtolemyImpl()
  val exaltImpl : IExaltation = ExaltationPtolemyImpl()

  companion object {
    private const val resource = "destiny.astrology.classical.rules.essentialDignities.EssentialDignities"
  }


}
