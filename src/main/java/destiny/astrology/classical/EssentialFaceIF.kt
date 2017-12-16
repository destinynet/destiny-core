/**
 * @author smallufo
 * Created on 2007/11/28 at 下午 9:06:28
 */
package destiny.astrology.classical

import destiny.astrology.Star
import destiny.astrology.ZodiacSign

/**
 * 取得黃道帶上某點，其 Face 是哪顆星，目前參考資料只會回傳行星 Planet <br></br>
 * 內定實作為 EssentialFaceDefaultImpl
 */
interface EssentialFaceIF {

  /** 取得黃道帶上的某點，其 Face 是哪顆星 , 0<=degree<360  */
  fun getFaceStar(degree: Double): Star

  /** 取得某星座某度，其 Face 是哪顆星 , 0<=degree<30  */
  fun getFaceStar(sign: ZodiacSign, degree: Double): Star
}
