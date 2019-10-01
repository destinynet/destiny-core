/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.IDayNight
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.IExaltation
import destiny.astrology.classical.IRuler
import destiny.astrology.classical.ITerm
import destiny.astrology.classical.ITriplicity
import destiny.core.DayNight

interface RulePredicate<out T : EssentialDignity> {
  fun getRules(p: Planet, h: IHoroscopeModel): List<T>?
}

class RulerPredicate(private val rulerImpl: IRuler) : RulePredicate<EssentialDignity.Ruler> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<EssentialDignity.Ruler>? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === with(rulerImpl) { sign.getRulerPoint() }
    }?.let { sign ->
      listOf(EssentialDignity.Ruler(p, sign))
    }
  }
}

class ExaltPredicate(private val exaltImpl : IExaltation) : RulePredicate<EssentialDignity.Exaltation> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<EssentialDignity.Exaltation>? {
    return h.getZodiacSign(p)?.takeIf { sign ->
      p === with(exaltImpl) { sign.getExaltPoint() }
    }?.let { sign ->
      listOf(EssentialDignity.Exaltation(p, sign))
    }
  }
}

class TermPredicate(private val termImpl : ITerm) : RulePredicate<EssentialDignity.Term> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<EssentialDignity.Term>? {
    return h.getPosition(p)?.lng?.takeIf { lngDeg ->
      p === termImpl.getPoint(lngDeg)
    }?.let { lngDeg ->
      listOf(EssentialDignity.Term(p, lngDeg))
    }
  }
}

/** A planet in its own day or night triplicity (not to be confused with the modern triplicities).  */
class TriplicityPredicate(private val triplicityImpl : ITriplicity ,
                          private val dayNightImpl: IDayNight) : RulePredicate<EssentialDignity.Triplicity> {
  override fun getRules(p: Planet, h: IHoroscopeModel): List<EssentialDignity.Triplicity>? {
    return h.getZodiacSign(p)?.let { sign ->
      dayNightImpl.getDayNight(h.lmt, h.location).takeIf { dayNight ->
        with(triplicityImpl) {
          (dayNight == DayNight.DAY && p === sign.getTriplicityPoint(DayNight.DAY) ||
            dayNight == DayNight.NIGHT && p === sign.getTriplicityPoint(DayNight.NIGHT))
        }
      }?.let { dayNight ->
        listOf(EssentialDignity.Triplicity(p, sign, dayNight))
      }
    }
  }
}


//class MutualPredicate(private val essentialImpl: IEssential,
//                      private val dayNightImpl: IDayNight,
//                      private val dignities: Collection<Dignity>) : RulePredicate<Mutual.Reception> {
//  override fun getRules(p: Planet, h: IHoroscopeModel): List<Mutual.Reception> ? {
//    val dayNight = dayNightImpl.getDayNight(h.gmtJulDay , h.location)
//    essentialImpl.getMutualData(p , h.pointDegreeMap , dayNight , dignities)
//    TODO()
//  }
//
//}
