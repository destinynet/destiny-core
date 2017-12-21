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

  private val essentialRedfImpl = EssentialRedfDefaultImpl(RulerPtolemyImpl() , DetrimentPtolemyImpl())

  private val triplicityImpl = EssentialTriplicityDefaultImpl()

  private val termsImpl = EssentialTermsDefaultImpl()

  private val faceImpl = EssentialFaceDefaultImpl()

  /**
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 empty ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 empty
   */
  override fun getPoint(sign: ZodiacSign, dignity: Dignity): Point? {
    return essentialRedfImpl.getPoint(sign, dignity)
  }

  /** Triplicity of DAY/NIGHT  */
  override fun getTriplicityPoint(sign: ZodiacSign, dayNight: DayNight): Point {
    return triplicityImpl.getTriplicityPoint(sign, dayNight)
  }

  /* Terms */
  override fun getTermsPoint(degree: Double): Point {
    return termsImpl.getTermsStar(degree)
  }

  /** Terms  */
  override fun getTermsPoint(sign: ZodiacSign, degree: Double): Point {
    return termsImpl.getTermsStar(sign, degree)
  }


  /** Face  */
  override fun getFacePoint(degree: Double): Point {
    return faceImpl.getFaceStar(degree)
  }

  /** Face  */
  override fun getFacePoint(sign: ZodiacSign, degree: Double): Point {
    return faceImpl.getFaceStar(sign, degree)
  }


}
