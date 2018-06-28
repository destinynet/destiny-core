/**
 * @author smallufo
 * Created on 2007/12/30 at 上午 4:48:47
 */
package destiny.astrology.classical.rules.debilities

import destiny.astrology.classical.*

abstract class EssentialRule : DebilityRule() {

  val triplicityImpl : ITriplicity = TriplicityWilliamImpl()

  val rulerImpl : IRuler = RulerPtolemyImpl()
  val detrimentImpl : IDetriment = DetrimentPtolemyImpl()
  val exaltImpl : IExaltation = ExaltationPtolemyImpl()
  val fallImpl : IFall = FallPtolemyImpl()

  val termImpl: ITerm = TermPtolomyImpl()
  val faceImpl : IFace = FacePtolomyImpl()

}
