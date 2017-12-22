/**
 * Created by smallufo on 2017-12-21.
 */

package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Planet
import destiny.astrology.Point
import destiny.astrology.ZodiacSign


data class MutReception(val planet1: Planet, val sign1: ZodiacSign, val planet2: Planet, val sign2: ZodiacSign)


class EssentialTools {

  companion object {

    /**
     * @param p1 是否接受 p2 的 RULER 招待 , +5
     * 譯作： p2 透過 RULER 接納了 p1
     */
    fun isReceivingFromRuler(p1: Point, p2: Point, map: Map<Point, ZodiacSign>, rulerImpl: IRuler): Boolean {
      return map[p1]?.takeIf { sign1 ->
        p2 === rulerImpl.getPoint(sign1)
      }?.let { true } ?: false
    }

    /**
     * @param p1 是否接受 p2 的 EXALT 招待 , +4
     * 譯作 : p2 透過 EXALT 接納了 p1
     */
    fun isReceivingFromExalt(p1: Point, p2: Point, map: Map<Point, ZodiacSign>, exaltImpl: IExaltation): Boolean {
      return map[p1]?.takeIf { sign1 ->
        p2 === exaltImpl.getPoint(sign1)
      }?.let { true } ?: false
    }

    /**
     * @param p1 是否接受 p2 的 TRIPLICITY 招待 , +3
     * 譯作 : p2 透過 TRIPLICITY 接納了 p1
     */
    fun isReceivingFromTriplicity(p1: Point, p2: Point, map: Map<Point, ZodiacSign>, dayNight: DayNight, triplicityImpl: ITriplicity): Boolean {
      return map[p1]?.takeIf { sign1 ->
        p2 === triplicityImpl.getPoint(sign1, dayNight)
      }?.let { true } ?: false
    }

    /**
     * @param p1 位於 sign1 的 degree 度 , 是否接受 p2 的 TERMS 招待 , +2
     * 譯作 : p2 透過 TERMS 接納了 p1
     */
    fun isReceivingFromTerms(p1: Point, sign1: ZodiacSign, degree: Double, p2: Point, termsImpl: ITerms): Boolean {
      return (p2 === termsImpl.getPoint(sign1, degree))
    }

    /**
     * @param p1 位於 sign1 的 degree 度 , 是否接受 p2 的 FACE 招待 , +1
     * 譯作 : p2 透過 FACE 接納了 p1
     */
    fun isReceivingFromFace(p1: Point, sign1: ZodiacSign, degree: Double, p2: Point, faceImpl: IFace): Boolean {
      return (p2 === faceImpl.getPoint(sign1 , degree))
    }


    /**
     * 製作出類似這樣的表格 : http://www.skyscript.co.uk/dig6.html
     */
    fun getReceptionMap(map: Map<Planet, ZodiacSign>): Map<Pair<Planet, Reception>, Set<Planet>> {
      TODO()
    }

    /** 用以判斷 [Dignity] 的互容 */
    fun getMutualReception(p: Planet, pointSignMap: Map<Point, ZodiacSign>, dig1: Dignity, dig2: Dignity, essentialImpl: IEssential, rulerImpl: IRuler): MutReception? {
      return pointSignMap[p]
        ?.let { sign1 ->
          essentialImpl.getPoint(sign1, dig1)
            ?.takeIf { it !== p }
            ?.let { point -> point as Planet }
            ?.let { planet2 ->
              pointSignMap[planet2]
                ?.takeIf { sign2 -> p === essentialImpl.getPoint(sign2, dig2) }
                ?.let { sign2 -> MutReception(p, sign1, planet2, sign2) }
            }
        }

    }

    /** 用以判斷 Triplicity 的互容 */
    fun getTriplicityMutualReception(p: Planet, pointSignMap: Map<Point, ZodiacSign>, dayNight: DayNight, essentialImpl: IEssential): MutReception? {
      return pointSignMap[p]
        ?.let { sign1 ->
          essentialImpl.getTriplicityPoint(sign1, dayNight)
            .takeIf { it != p }
            ?.let { point -> point as Planet }
            ?.let { planet2 ->
              pointSignMap[planet2]
                ?.takeIf { sign2 -> p === essentialImpl.getTriplicityPoint(sign2, dayNight) }
                ?.let { sign2 -> MutReception(p, sign1, planet2, sign2) }
            }
        }
    }

    /** 如果 兩顆星都處於 [Dignity.DETRIMENT] 或是  [Dignity.FALL] , 則為 true  */
    fun isBothInBadSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign, detrimentImpl: IDetriment, fallImpl: IFall): Boolean {
      return (p1 === detrimentImpl.getPoint(sign1) || p1 === fallImpl.getPoint(sign1))
        && (p2 === detrimentImpl.getPoint(sign2) || p2 === fallImpl.getPoint(sign2))
    }

    /** 如果 兩顆星都處於 [Dignity.RULER] 或是  [Dignity.EXALTATION] , 則為 true  */
    fun isBothInGoodSituation(p1: Point, sign1: ZodiacSign, p2: Point, sign2: ZodiacSign, rulerImpl: IRuler, exaltImpl: IExaltation): Boolean {
      return (p1 === rulerImpl.getPoint(sign1) || p1 === exaltImpl.getPoint(sign1))
        && (p2 === rulerImpl.getPoint(sign2) || p2 === exaltImpl.getPoint(sign2))
    }

  }


}

