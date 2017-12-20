/**
 * @author smallufo
 * Created on 2007/12/12 at 下午 8:29:27
 */
package destiny.astrology.classical

import destiny.astrology.*
import org.slf4j.LoggerFactory

/**
 * Facade Interface of Essential Dignities and Deblitities <br></br>
 * 具備計算 Ptolemy's Table of Essential Dignities and Deblities 的所有介面
 */
interface IEssential {

  companion object {
    val logger = LoggerFactory.getLogger(IEssential::class.java)
  }



  /**
   * 取得黃道帶上某星座，其 Dignity 之 廟旺陷落 各是何星
   * @param dignity [Dignity.RULER] 與 [Dignity.DETRIMENT] 不會傳回 null ,
   * 但 [Dignity.EXALTATION] 與 [Dignity.FALL] 就有可能為 null
   */
  fun getPoint(sign: ZodiacSign, dignity: Dignity): Point?

  fun getPoints(sign: ZodiacSign , vararg dignities: Dignity): List<Pair<Dignity , Point>>  {
    return dignities.map { dignity ->
      dignity to getPoint(sign , dignity)
    }.filter { it.second != null }
      .map { it -> it.first to it.second!! }
      .toList()
  }

  /** 取得此行星，在此星座的 旺 廟 陷 落 */
  fun getDignity(planet: Planet , sign: ZodiacSign) : Dignity ? {
    return Dignity.values().firstOrNull{ dig -> planet === getPoint(sign , dig) }
  }

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

  /** 如果 兩顆星都處於 [Dignity.DETRIMENT] 或是  [Dignity.FALL] , 則為 true  */
  fun isBothInBadSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean {
    return (p1 === getPoint(sign1, Dignity.DETRIMENT) || p1 === getPoint(sign1, Dignity.FALL))
      &&   (p2 === getPoint(sign2, Dignity.DETRIMENT) || p2 === getPoint(sign2, Dignity.FALL))
  }

  /** 如果 兩顆星都處於 [Dignity.RULER] 或是  [Dignity.EXALTATION] , 則為 true  */
  fun isBothInGoodSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign):Boolean {
    return (p1 === getPoint(sign1, Dignity.RULER) || p1 === getPoint(sign1, Dignity.EXALTATION))
      &&   (p2 === getPoint(sign2, Dignity.RULER) || p2 === getPoint(sign2, Dignity.EXALTATION))
  }

  
   /**
    * 是否有一顆星在糟糕狀況? (只要有一顆就算) 
    * 如果其中一顆星處於 [Dignity.DETRIMENT] 或是 [Dignity.FALL] , 則為 true  */
  fun isOneInBadSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean {
    return p1 === getPoint(sign1, Dignity.DETRIMENT)
      || p1 === getPoint(sign1, Dignity.FALL)
      || p2 === getPoint(sign2, Dignity.DETRIMENT) 
      || p2 === getPoint(sign2, Dignity.FALL)
  }


  /** receiver 是否 接納 receivee by Essential Debilities (Detriment/Fall)  */
  fun isReceivingFromDebilities(receiver: Point, receivee: Point, h: Horoscope): Boolean {
    return h.getZodiacSign(receivee)?.let { receiveeSign ->
      receiver === getPoint(receiveeSign, Dignity.DETRIMENT) ||
      receiver === getPoint(receiveeSign, Dignity.FALL)
    } ?: false
  }
  
  /**
   * receiver 是否 接納 receivee by Essential Dignities (Ruler/Exaltation/Triplicity/Term/Face) <br></br>
   * 老闆是 receiver , 客人是 receivee , 如果客人進入了老闆的地盤 ( 旺 / 廟 / 三分 / Terms / Faces ) , 則「老闆接納外人」
   */
  fun isReceivingFromDignities(receiver: Point, receivee: Point, h: Horoscope , dayNightImpl: DayNightDifferentiator): Boolean {

    return h.getZodiacSign(receivee)?.let { receiveeSign ->
      return when (receiver) {
        getPoint(receiveeSign, Dignity.RULER) -> {
          logger.debug("{} 透過 {} 接納 {}", receiver, Dignity.RULER, receivee)
          true
        }
        getPoint(receiveeSign, Dignity.EXALTATION) -> {
          logger.debug("{} 透過 {} 接納 {}", receiver, Dignity.EXALTATION, receivee)
          true
        }
        getTriplicityPoint(receiveeSign, dayNightImpl.getDayNight(h.lmt, h.location)) -> {
          logger.debug("{} 透過 Triplicity 接納 {}", receiver, receivee)
          true
        }
        else -> {
          return h.getPosition(receivee)?.lng?.let { lngDegree ->
            return when (receiver) {
              getTermsPoint(lngDegree) -> {
                logger.debug("{} 透過 TERMS 接納 {}", receiver, receivee)
                true
              }
              getFacePoint(lngDegree) -> {
                logger.debug("{} 透過 FACE 接納 {}", receiver, receivee)
                true
              }
              else -> false
            }
          } ?: false
        } // else
      }
    } ?: false
  }

}
