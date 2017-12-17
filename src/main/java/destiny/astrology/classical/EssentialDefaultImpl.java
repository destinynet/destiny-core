/**
 * @author smallufo 
 * Created on 2007/12/12 at 下午 8:35:16
 */ 
package destiny.astrology.classical;

import destiny.astrology.DayNight;
import destiny.astrology.Point;
import destiny.astrology.ZodiacSign;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/** Facade Class of Ptolemy's Table of Essential Dignities and Debilities */
public class EssentialDefaultImpl implements IEssential, Serializable
{
  private IEssentialRedf essentialRedfImpl = new EssentialRedfDefaultImpl();
  private IEssentialTriplicity triplicityImpl = new EssentialTriplicityDefaultImpl();
  private IEssentialTerms termsImpl = new EssentialTermsDefaultImpl();
  private IEssentialFace faceImpl = new EssentialFaceDefaultImpl();
  
  public EssentialDefaultImpl()
  {
  }

  /**
   * @param dignity {@link Dignity#RULER} 與 {@link Dignity#DETRIMENT} 不會傳回 empty ,
   *                                     但 {@link Dignity#EXALTATION} 與 {@link Dignity#FALL} 就有可能為 empty
   * */
  @Override
  public Point getPoint(@NotNull ZodiacSign sign, @NotNull Dignity dignity) {
    return essentialRedfImpl.getPoint(sign, dignity).orElse(null);
  }

  @Override
  /** Triplicity of DAY/NIGHT */
  @NotNull
  public Point getTriplicityPoint(ZodiacSign sign, DayNight dayNight) {
    return triplicityImpl.getTriplicityPoint(sign, dayNight);
  }

  /* Terms */
  @NotNull
  @Override
  public Point getTermsPoint(double degree) {
    return termsImpl.getTermsStar(degree);
  }

  @Override
  /** Terms */
  public Point getTermsPoint(ZodiacSign sign, double degree)
  {
    return termsImpl.getTermsStar(sign, degree);
  }

  
  /** Face */
  @NotNull
  @Override
  public Point getFacePoint(double degree) {
    return faceImpl.getFaceStar(degree);
  }

  @Override
  /** Face */
  public Point getFacePoint(ZodiacSign sign, double degree)
  {
    return faceImpl.getFaceStar(sign, degree);
  }

  public void setEssentialRedfImpl(IEssentialRedf essentialRedfImpl)
  {
    this.essentialRedfImpl = essentialRedfImpl;
  }

  public void setTriplicityImpl(IEssentialTriplicity triplicityImpl)
  {
    this.triplicityImpl = triplicityImpl;
  }

  public void setTermsImpl(IEssentialTerms termsImpl)
  {
    this.termsImpl = termsImpl;
  }

  public void setFaceImpl(IEssentialFace faceImpl)
  {
    this.faceImpl = faceImpl;
  }


}
