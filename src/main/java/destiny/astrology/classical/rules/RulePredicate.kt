/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.*

interface RulePredicate<out T : Rule> {
  fun getRules(p: Planet, h: IHoroscopeModel): List<T>?
}

class RulerPredicate(private val rulerImpl: IRuler) : RulePredicate<Rule.Ruler> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<Rule.Ruler>? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === rulerImpl.getPoint(sign)
    }?.let { sign ->
      listOf(Rule.Ruler(p, sign))
    }
  }
}

class ExaltPredicate(private val exaltImpl : IExaltation) : RulePredicate<Rule.Exalt> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<Rule.Exalt>? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === exaltImpl.getPoint(sign)
    }?.let { sign ->
      listOf(Rule.Exalt(p, sign))
    }
  }
}

class TermPredicate(private val termImpl : ITerm) : RulePredicate<Rule.Term> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<Rule.Term>? {
    return h.getPosition(p)?.lng?.takeIf { lngDeg ->
      p === termImpl.getPoint(lngDeg)
    }?.let { lngDeg ->
      listOf(Rule.Term(p, lngDeg))
    }
  }
}

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities).  */
class TriplicityPredicate(private val triplicityImpl : ITriplicity ,
                          private val dayNightImpl: DayNightDifferentiator) : RulePredicate<Rule.Triplicity> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<Rule.Triplicity>? {
    return h.getZodiacSign(p)?.let { sign ->
      dayNightImpl.getDayNight(h.lmt, h.location).takeIf { dayNight ->
        (dayNight == DayNight.DAY && p === triplicityImpl.getPoint(sign, DayNight.DAY) ||
          dayNight == DayNight.NIGHT && p === triplicityImpl.getPoint(sign, DayNight.NIGHT))
      }?.let { dayNight ->
        listOf(Rule.Triplicity(p, sign, dayNight))
      }
    }
  }
}


class MutualPredicate(private val essentialImpl: IEssential ,
                      private val dayNightImpl: DayNightDifferentiator,
                      private val dignities: Collection<Dignity>) : RulePredicate<Mutual.Reception> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<Mutual.Reception> ? {
    val dayNight = dayNightImpl.getDayNight(h.gmtJulDay , h.location)
    essentialImpl.getMutualData(p , h.pointDegreeMap , dayNight , dignities)
    TODO()
  }

}
