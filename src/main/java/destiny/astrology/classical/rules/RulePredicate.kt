/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.*

interface RulePredicate<out T : Rule> {
  fun getRule(p: Planet, h: Horoscope): T?
}

class RulerPredicate(private val rulerImpl: IRuler) : RulePredicate<Rule.Ruler> {
  override fun getRule(p: Planet, h: Horoscope): Rule.Ruler? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === rulerImpl.getPoint(sign)
    }?.let { sign ->
      Rule.Ruler(p, sign)
    }
  }
}

class ExaltPredicate(private val exaltImpl : IExaltation) : RulePredicate<Rule.Exalt> {
  override fun getRule(p: Planet, h: Horoscope): Rule.Exalt? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === exaltImpl.getPoint(sign)
    }?.let { sign ->
      Rule.Exalt(p, sign)
    }
  }
}

class TermPredicate(private val termImpl : ITerm) : RulePredicate<Rule.Term> {
  override fun getRule(p: Planet, h: Horoscope): Rule.Term? {
    return h.getPosition(p)?.lng?.takeIf { lngDeg ->
      p === termImpl.getPoint(lngDeg)
    }?.let { lngDeg ->
      Rule.Term(p, lngDeg)
    }
  }
}

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities).  */
class TriplicityPredicate(private val triplicityImpl : ITriplicity ,
                          private val dayNightImpl: DayNightDifferentiator) : RulePredicate<Rule.Triplicity> {
  override fun getRule(p: Planet, h: Horoscope): Rule.Triplicity? {
    return h.getZodiacSign(p)?.let { sign ->
      dayNightImpl.getDayNight(h.lmt, h.location).takeIf { dayNight ->
        (dayNight == DayNight.DAY && p === triplicityImpl.getPoint(sign, DayNight.DAY) ||
          dayNight == DayNight.NIGHT && p === triplicityImpl.getPoint(sign, DayNight.NIGHT))
      }?.let { dayNight ->
        Rule.Triplicity(p, sign, dayNight)
      }
    }
  }
}


class MutualPredicate(private val essentialImpl: IEssential ,
                      private val dayNightImpl: DayNightDifferentiator,
                      private val dignities: Collection<Dignity>) : RulePredicate<Mutual.Reception> {
  override fun getRule(p: Planet, h: Horoscope): Mutual.Reception ? {
    val dayNight = dayNightImpl.getDayNight(h.gmtJulDay , h.location)
    essentialImpl.getMutualData(p , h.pointDegreeMap , dayNight , dignities)
    TODO()
  }

}
