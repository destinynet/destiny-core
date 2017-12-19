/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 8:29:27
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Point
import destiny.astrology.ZodiacSign

/**
 * Facade Interface of Essential Dignities and Deblitities <br></br>
 * 具備計算 Ptolemy's Table of Essential Dignities and Deblities 的所有介面
 */
interface IEssential {

  /**
   * 取得黃道帶上某星座，其 Dignity 之 廟旺陷落 各是何星
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 null ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 null
   */
  fun getPoint(sign: ZodiacSign, dignity: Dignity): Point?

//  fun getPoints(sign: ZodiacSign , vararg dignities: Dignity): List<Pair<Dignity , Point>>  {
//    return dignities.map { dignity ->
//      dignity to getPoint(sign , dignity)
//    }.filter { it.second != null }
//      .map { it -> it.first to it.second!! }
//      .toList()
//  }

  /** 取得黃道帶上某星座，其 Triplicity 是什麼星   */
  fun getTriplicityPoint(sign: ZodiacSign, dayNight: DayNight): Point

  /** 取得黃道帶上的某點，其 Terms 是哪顆星 , 0<=degree<360  */
  fun getTermsPoint(degree: Double): Point

  /** 取得某星座某度，其 Terms 是哪顆星 , 0<=degree<30  */
  fun getTermsPoint(sign: ZodiacSign, degree: Double): Point

  /** 取得黃道帶上的某點，其 Face 是哪顆星 , 0<=degree<360  */
  fun getFacePoint(degree: Double): Point

  /** 取得某星座某度，其 Face 是哪顆星 , 0<=degree<30  */
  fun getFacePoint(sign: ZodiacSign, degree: Double): Point

}
