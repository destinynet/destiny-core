/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.*
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.EssentialDefaultImpl
import destiny.astrology.classical.IEssential

abstract class AbstractRulePredicate<out T : Rule> {
  abstract fun getRule(p: Planet, h: Horoscope): T?
}

var essentialImpl: IEssential = EssentialDefaultImpl()

class RulerPredicate : AbstractRulePredicate<Rule.Ruler>() {
  override fun getRule(p: Planet, h: Horoscope): Rule.Ruler? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === essentialImpl.getPoint(sign, Dignity.RULER)
    }?.let { sign ->
      Rule.Ruler(p, sign)
    }
  }
}

class ExaltPredicate : AbstractRulePredicate<Rule.Exalt>() {
  override fun getRule(p: Planet, h: Horoscope): Rule.Exalt? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === essentialImpl.getPoint(sign, Dignity.EXALTATION)
    }?.let { sign ->
      Rule.Exalt(p, sign)
    }
  }
}

class TermPredicate : AbstractRulePredicate<Rule.Term>() {
  override fun getRule(p: Planet, h: Horoscope): Rule.Term? {
    return h.getPosition(p)?.lng?.takeIf { lngDeg ->
      p === essentialImpl.getTermsPoint(lngDeg)
    }?.let { lngDeg ->
      Rule.Term(p, lngDeg)
    }
  }
}

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities).  */
class TriplicityPredicate(private val dayNightImpl: DayNightDifferentiator) : AbstractRulePredicate<Rule.Triplicity>() {
  override fun getRule(p: Planet, h: Horoscope): Rule.Triplicity? {
    return h.getZodiacSign(p)?.let { sign ->
      dayNightImpl.getDayNight(h.lmt, h.location).takeIf { dayNight ->
        (dayNight == DayNight.DAY && p === essentialImpl.getTriplicityPoint(sign, DayNight.DAY) ||
          dayNight == DayNight.NIGHT && p === essentialImpl.getTriplicityPoint(sign, DayNight.NIGHT))
      }?.let { dayNight ->
        Rule.Triplicity(p, sign, dayNight)
      }
    }
  }
}

class BeneficialMutualReceptionPredicate : AbstractRulePredicate<Rule.BeneficialMutualReception>() {

  override fun getRule(p: Planet, h: Horoscope): Rule.BeneficialMutualReception? {
    return mutualReception(p, h, Dignity.RULER, Dignity.RULER)
      ?: mutualReception(p, h, Dignity.EXALTATION, Dignity.EXALTATION)
      ?: mutualReception(p, h, Dignity.RULER, Dignity.EXALTATION)
      ?: mutualReception(p, h, Dignity.EXALTATION, Dignity.RULER)
  }

  private fun mutualReception(p: Planet, h: Horoscope, dig1: Dignity, dig2: Dignity): Rule.BeneficialMutualReception? {
    return h.getZodiacSign(p)
      ?.let { sign1 ->
        essentialImpl.getPoint(sign1, dig1)
          ?.takeIf { planet2 -> p != planet2 }
          ?.let { planet2 ->
            h.getZodiacSign(planet2)
              ?.takeIf { sign2 -> p === essentialImpl.getPoint(sign2, dig2) }
              ?.takeIf { sign2 -> !essentialImpl.isBothInBadSituation(p, sign1, planet2, sign2) }
              ?.let { sign2 ->
                Rule.BeneficialMutualReception(p, sign1, dig1, planet2 as Planet, sign2, dig2)
              }
          }
      }
  }
}


class MutualReceptionPredicateOld : AbstractRulePredicate<Mutual>() {

  /**
   * planet1 位於 sign1 ,
   * sign1 的 RULER (planet2) 飛到 sign2
   * sign2 的 RULER 就是 planet1
   * 形成「兩家互訪」的情形 : 我在你家、你在我家
   */
  data class MutualReception(val planet1: Planet, val sign1: ZodiacSign, val planet2: Planet, val sign2: ZodiacSign)

  override fun getRule(p: Planet, h: Horoscope): Mutual? {
    return getMutualReception(p, h)?.let { mr ->
      val dig1 = essentialImpl.getDignity(mr.planet1, mr.sign1)
      val dig2 = essentialImpl.getDignity(mr.planet2, mr.sign2)
      when (dig1 to dig2) {
        (Dignity.EXALTATION to Dignity.EXALTATION) -> Mutual.MutualExalt(mr.planet1, mr.sign1, mr.planet2, mr.sign2)
        (Dignity.FALL to Dignity.FALL) -> Mutual.MutualFall(mr.planet1, mr.sign1, mr.planet2, mr.sign2)
        (Dignity.DETRIMENT to Dignity.DETRIMENT) -> Mutual.MutualDetriment(mr.planet1, mr.sign1, mr.planet2, mr.sign2)
        else -> Mutual.MutualGeneral(mr.planet1, mr.sign1, mr.planet2, mr.sign2)
      }
    }
  }

  /**
   * 檢查此 planet 是否在此星盤中，發生 RULER 互訪
   */
  private fun getMutualReception(p: Planet, h: Horoscope): MutualReception? {
    return h.getZodiacSign(p)
      ?.let { sign1 ->
        essentialImpl.getPoint(sign1, Dignity.RULER)
          ?.takeIf { it !== p }
          ?.let { point -> point as Planet }
          ?.let { planet2 ->
            h.getZodiacSign(planet2)
              ?.takeIf { sign2 -> p === essentialImpl.getPoint(sign2, Dignity.RULER) }
              ?.let { sign2 -> MutualReception(p, sign1, planet2, sign2) }
          }
      }
  }


} // class MutualReceptionPredicate

/**
 * 互相接納
 * https://skywriter.wordpress.com/2016/11/01/new-insights-into-mutual-reception/
 */
class MutualReceptionPredicate : AbstractRulePredicate<MutualReception>() {
  override fun getRule(p: Planet, h: Horoscope): MutualReception? {
    return getMutualReception(p , h , Dignity.RULER , Dignity.RULER)?.let { MutualReception.BySign(it.planet1 , it.sign1 , it.planet2 , it.sign2) }
      ?:getMutualReception(p,h,Dignity.EXALTATION , Dignity.EXALTATION)?.let { MutualReception.ByExalt(it.planet1 , it.sign1 , it.planet2 , it.sign2) }
  }

  data class MutReception(val planet1: Planet, val sign1: ZodiacSign, val planet2: Planet, val sign2: ZodiacSign)

  private fun getMutualReception(p: Planet, h: Horoscope, dig1: Dignity, dig2: Dignity): MutReception? {
    return h.getZodiacSign(p)
      ?.let { sign1 ->
        essentialImpl.getPoint(sign1, dig1)
          ?.takeIf { it !== p }
          ?.let { point -> point as Planet }
          ?.let { planet2 ->
            h.getZodiacSign(planet2)
              ?.takeIf { sign2 -> p === essentialImpl.getPoint(sign2, dig2) }
              ?.let { sign2 -> MutReception(p , sign1 , planet2 , sign2) }
          }
      }
  }

}

