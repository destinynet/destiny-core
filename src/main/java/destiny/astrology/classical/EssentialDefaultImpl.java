/**
 * @author smallufo 
 * Created on 2007/12/12 at 下午 8:35:16
 */ 
package destiny.astrology.classical;

import destiny.astrology.DayNight;
import destiny.astrology.Point;
import destiny.astrology.ZodiacSign;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/** Facade Class of Ptolemy's Table of Essential Dignities and Debilities */
public class EssentialDefaultImpl implements EssentialIF , Serializable 
{
  private EssentialRedfIF essentialRedfImpl = new EssentialRedfDefaultImpl();
  private EssentialTriplicityIF triplicityImpl = new EssentialTriplicityDefaultImpl();
  private EssentialTermsIF termsImpl = new EssentialTermsDefaultImpl();
  private EssentialFaceIF faceImpl = new EssentialFaceDefaultImpl();
  
  public EssentialDefaultImpl()
  {
  }
  
  @Nullable
  @Override
  /** Rulership / Exaltation / Detriment / Fall */
  public Point getPoint(ZodiacSign sign, Dignity dignity)
  {
    return essentialRedfImpl.getPoint(sign, dignity);
  }

  @Override
  /** Tripilicity of DAY/NIGHT */
  public Point getTriplicityPoint(ZodiacSign sign, DayNight dayNight)
  {
    return triplicityImpl.getTriplicityPoint(sign, dayNight);
  }

  @Override
  /** Terms */
  public Point getTermsPoint(double degree)
  {
    return termsImpl.getTermsStar(degree);
  }

  @Override
  /** Terms */
  public Point getTermsPoint(ZodiacSign sign, double degree)
  {
    return termsImpl.getTermsStar(sign, degree);
  }

  
  @Override
  /** Face */
  public Point getFacePoint(double degree)
  {
    return faceImpl.getFaceStar(degree);
  }

  @Override
  /** Face */
  public Point getFacePoint(ZodiacSign sign, double degree)
  {
    return faceImpl.getFaceStar(sign, degree);
  }

  public void setEssentialRedfImpl(EssentialRedfIF essentialRedfImpl)
  {
    this.essentialRedfImpl = essentialRedfImpl;
  }

  public void setTriplicityImpl(EssentialTriplicityIF triplicityImpl)
  {
    this.triplicityImpl = triplicityImpl;
  }

  public void setTermsImpl(EssentialTermsIF termsImpl)
  {
    this.termsImpl = termsImpl;
  }

  public void setFaceImpl(EssentialFaceIF faceImpl)
  {
    this.faceImpl = faceImpl;
  }


}
