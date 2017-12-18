/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.EssentialDefaultImpl
import destiny.astrology.classical.EssentialUtils
import destiny.astrology.classical.IEssential

abstract class AbstractRulePredicate<out T : Rule> {
  abstract fun getRule(p: Planet, h: Horoscope): T?
}

var essentialImpl: IEssential = EssentialDefaultImpl()

class RulerRredicate : AbstractRulePredicate<Rule.Ruler>() {

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

class BeneficialMutlalReceptionPredicate(private val utils: EssentialUtils) : AbstractRulePredicate<Rule.BeneficialMutualReception>() {

  override fun getRule(p: Planet, h: Horoscope): Rule.BeneficialMutualReception? {
    return mutualReception(p, h, Dignity.RULER, Dignity.RULER)
      ?: mutualReception(p, h, Dignity.EXALTATION, Dignity.EXALTATION)
      ?: mutualReception(p, h, Dignity.RULER, Dignity.EXALTATION)
      ?: mutualReception(p, h, Dignity.EXALTATION, Dignity.RULER)
  }

  private fun mutualReception(p: Planet, h: Horoscope, d1: Dignity, d2: Dignity): Rule.BeneficialMutualReception? {
    return h.getZodiacSign(p)
      ?.let { sign1 ->
        essentialImpl.getPoint(sign1, d1)
          ?.takeIf { planet2 -> p != planet2 }
          ?.let { planet2 ->
            h.getZodiacSign(planet2)
              ?.takeIf { sign2 -> p === essentialImpl.getPoint(sign2, d2) }
              ?.takeIf { sign2 -> !utils.isBothInBadSituation(p, sign1, planet2, sign2) }
              ?.let { sign2 ->
                Rule.BeneficialMutualReception(p, sign1, planet2 as Planet, sign2)
              }
          }
      }
  }
}