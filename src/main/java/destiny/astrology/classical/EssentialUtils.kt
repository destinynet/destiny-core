/**
 * @author smallufo
 * Created on 2008/1/2 at 下午 4:08:25
 */
package destiny.astrology.classical

import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import org.slf4j.LoggerFactory

/** EssentialIF 的工具箱  */
class EssentialUtils(
  /** 內定採用 Swiss Ephemeris 的 DayNightDifferentiatorImpl  */
  private val dayNightDifferentiatorImpl: DayNightDifferentiator// = new DayNightDifferentiatorImpl(new RiseTransImpl());
) {
  private var essentialImpl: EssentialIF = EssentialDefaultImpl()

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * receiver 是否 接納 receivee by Essential Dignities (Ruler/Exaltation/Triplicity/Term/Face) <br></br>
   * 老闆是 receiver , 客人是 receivee , 如果客人進入了老闆的地盤 ( 旺 / 廟 / 三分 / Terms / Faces ) , 則「老闆接納外人」
   */
  fun isReceivingFromDignities(receiver: Point, receivee: Point, h: Horoscope): Boolean {

    return h.getZodiacSign(receivee)?.let { receiveeSign ->
      return when (receiver) {
        essentialImpl.getPoint(receiveeSign, Dignity.RULER) -> {
          logger.debug("{} 透過 {} 接納 {}", receiver, Dignity.RULER, receivee)
          true
        }
        essentialImpl.getPoint(receiveeSign, Dignity.EXALTATION) -> {
          logger.debug("{} 透過 {} 接納 {}", receiver, Dignity.EXALTATION, receivee)
          true
        }
        essentialImpl.getTriplicityPoint(receiveeSign, dayNightDifferentiatorImpl.getDayNight(h.lmt, h.location)) -> {
          logger.debug("{} 透過 Triplicity 接納 {}", receiver, receivee)
          true
        }
        else -> {
          return h.getPosition(receivee)?.lng?.let { lngDegree ->
            return when (receiver) {
              essentialImpl.getTermsPoint(lngDegree) -> {
                logger.debug("{} 透過 TERMS 接納 {}", receiver, receivee)
                true
              }
              essentialImpl.getFacePoint(lngDegree) -> {
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

  /** receiver 是否 接納 receivee by Essential Debilities (Detriment/Fall)  */
  fun isReceivingFromDebilities(receiver: Point, receivee: Point, h: Horoscope): Boolean {
    return h.getZodiacSign(receivee)?.let { receiveeSign ->
      receiver === essentialImpl.getPoint(receiveeSign, Dignity.DETRIMENT) ||
      receiver === essentialImpl.getPoint(receiveeSign, Dignity.FALL)
    } ?: false
  }

  /** Ruler 互訪 , 還沒確認是 優質互容  */
  fun isBothRulerVisit(planet: Point, h: Horoscope): Boolean {
    return h.getZodiacSign(planet)?.let { sign1 ->
      essentialImpl.getPoint(sign1, Dignity.RULER)?.let { signRuler ->
        h.getZodiacSign(signRuler)?.takeIf { sign2 ->
          planet === essentialImpl.getPoint(sign2, Dignity.RULER)
        }?.let {
          true
        }
      }
    } ?: false
  }

  /** 如果其中一顆星處於 [Dignity.DETRIMENT] 或是 [Dignity.FALL] , 則為 true  */
  fun isOneInBadSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean {
    return p1 === essentialImpl.getPoint(sign1, Dignity.DETRIMENT) || p1 === essentialImpl.getPoint(sign1, Dignity.FALL) || p2 === essentialImpl.getPoint(sign2, Dignity.DETRIMENT) || p2 === essentialImpl.getPoint(sign2, Dignity.FALL)
  }

  /** 如果 兩顆星都處於 [Dignity.DETRIMENT] 或是  [Dignity.FALL] , 則為 true  */
  fun isBothInBadSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign): Boolean {
    return (p1 === essentialImpl.getPoint(sign1, Dignity.DETRIMENT) || p1 === essentialImpl.getPoint(sign1, Dignity.FALL)) && (p2 === essentialImpl.getPoint(sign2, Dignity.DETRIMENT) || p2 === essentialImpl.getPoint(sign2, Dignity.FALL))
  }

  fun setEssentialImpl(essentialImpl: EssentialIF) {
    this.essentialImpl = essentialImpl
  }

}
