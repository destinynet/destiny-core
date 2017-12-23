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

