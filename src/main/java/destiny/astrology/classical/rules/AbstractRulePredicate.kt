/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.astrology.Horoscope
import destiny.astrology.Planet
import destiny.astrology.classical.*

abstract class AbstractRulePredicate<out T : Rule>(internal val essentialImpl: IEssential) {
  abstract fun getRule(p: Planet, h: Horoscope): T?
}


val triplicityImpl: ITriplicity = TriplicityWilliamImpl()
val termImpl: ITerm = TermPtolomyImpl()

val rulerImpl: IRuler = RulerPtolemyImpl()
val detrimentImpl: IDetriment = DetrimentPtolemyImpl()
val exaltImpl: IExaltation = ExaltationPtolemyImpl()
val fallImpl: IFall = FallPtolemyImpl()
val faceImpl = FacePtolomyImpl()

//var essentialImpl: IEssential = EssentialImpl(rulerImpl, exaltImpl, fallImpl, detrimentImpl, triplicityImpl, termImpl, faceImpl)

class RulerPredicate(essentialImpl: IEssential) : AbstractRulePredicate<Rule.Ruler>(essentialImpl) {
  override fun getRule(p: Planet, h: Horoscope): Rule.Ruler? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === rulerImpl.getPoint(sign)
    }?.let { sign ->
      Rule.Ruler(p, sign)
    }
  }
}

class ExaltPredicate(essentialImpl: IEssential) : AbstractRulePredicate<Rule.Exalt>(essentialImpl) {
  override fun getRule(p: Planet, h: Horoscope): Rule.Exalt? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === exaltImpl.getPoint(sign)
    }?.let { sign ->
      Rule.Exalt(p, sign)
    }
  }
}

class TermPredicate(essentialImpl: IEssential) : AbstractRulePredicate<Rule.Term>(essentialImpl) {
  override fun getRule(p: Planet, h: Horoscope): Rule.Term? {
    return h.getPosition(p)?.lng?.takeIf { lngDeg ->
      p === termImpl.getPoint(lngDeg)
    }?.let { lngDeg ->
      Rule.Term(p, lngDeg)
    }
  }
}

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities).  */
class TriplicityPredicate(essentialImpl: IEssential, private val dayNightImpl: DayNightDifferentiator) : AbstractRulePredicate<Rule.Triplicity>(essentialImpl) {
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


