/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 12:35:27
 */
package destiny.astrology.classical.rules.essentialDignities

import destiny.astrology.classical.*
import destiny.astrology.classical.rules.AbstractRule

abstract class Rule : AbstractRule(resource), Applicable {

  val termImpl: ITerm = TermPtolomyImpl()
  val triplicityImpl : ITriplicity = TriplicityWilliamImpl()

  val rulerImpl : IRuler = RulerPtolemyImpl()
//  val fallImpl : IFall = FallPtolemyImpl()
  //val detrimentImpl : IDetriment = DetrimentPtolemyImpl()
  val faceImpl: IFace = FacePtolomyImpl()

  companion object {
    private const val resource = "destiny.astrology.classical.rules.essentialDignities.EssentialDignities"
  }


}
