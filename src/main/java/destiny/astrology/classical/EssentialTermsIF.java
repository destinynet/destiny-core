/**
 * @author smallufo 
 * Created on 2007/11/28 at 下午 2:55:05
 */ 
package destiny.astrology.classical;

import destiny.astrology.Point;
import destiny.astrology.ZodiacSign;

/**
 * 取得黃道帶上某點，其 Terms 是哪顆星，目前參考資料只會回傳行星 Planet <br/>
 * 內定實作為 EssentialTermsDefaultImpl
 */
public interface EssentialTermsIF
{
  /** 取得黃道帶上的某點，其 Terms 是哪顆星 , 0<=degree<360 */
  public Point getTermsStar(double degree);
  
  /** 取得某星座某度，其 Terms 是哪顆星 , 0<=degree<30 */
  public Point getTermsStar(ZodiacSign sign , double degree);
}
