/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 8:35:16
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Point
import destiny.astrology.ZodiacSign

import java.io.Serializable

/** Facade Class of Ptolemy's Table of Essential Dignities and Debilities  */
class EssentialDefaultImpl : IEssential, Serializable {

  private val essentialRedfImpl = EssentialRedfDefaultImpl(RulerPtolemyImpl() , DetrimentPtolemyImpl() , ExaltationPtolemyImpl() , FallPtolemyImpl())

  private val triplicityImpl = TriplicityWilliamImpl()

  private val termsImpl = TermsPtolomyImpl()

  private val faceImpl = FacePtolomyImpl()

  /**
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 empty ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 empty
   */
  override fun getPoint(sign: ZodiacSign, dignity: Dignity): Point? {
    return essentialRedfImpl.getPointOld(sign, dignity)
  }

  /** Triplicity of DAY/NIGHT  */
  override fun getTriplicityPoint(sign: ZodiacSign, dayNight: DayNight): Point {
    return triplicityImpl.getPoint(sign, dayNight)
  }



}
