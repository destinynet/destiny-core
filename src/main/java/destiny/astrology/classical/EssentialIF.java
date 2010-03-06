/**
 * @author smallufo 
 * Created on 2007/12/12 at 下午 8:29:27
 */ 
package destiny.astrology.classical;

import destiny.astrology.DayNight;
import destiny.astrology.Point;
import destiny.astrology.ZodiacSign;

/** 
 * Facade Interface of Essential Dignities and Deblitities <br>
 * 具備計算 Ptolemy's Table of Essential Dignities and Deblities 的所有介面 
 */
public interface EssentialIF
{
  /** 取得黃道帶上某星座，其 Dignity 之 廟旺陷落 各是何星 */
  public Point getPoint(ZodiacSign sign , Dignity dignity);
  
  /** 取得黃道帶上某星座，其 Triplicity 是什麼星  */
  public Point getTriplicityPoint(ZodiacSign sign  , DayNight dayNight);
  
  /** 取得黃道帶上的某點，其 Terms 是哪顆星 , 0<=degree<360 */
  public Point getTermsPoint(double degree);
  
  /** 取得某星座某度，其 Terms 是哪顆星 , 0<=degree<30 */
  public Point getTermsPoint(ZodiacSign sign , double degree);
  
  /** 取得黃道帶上的某點，其 Face 是哪顆星 , 0<=degree<360 */
  public Point getFacePoint(double degree);
  
  /** 取得某星座某度，其 Face 是哪顆星 , 0<=degree<30 */
  public Point getFacePoint(ZodiacSign sign , double degree);
  
}
