/**
 * Created by smallufo on 2019-10-02.
 */
package destiny.astrology.classical.rules

import destiny.astrology.IDayNight
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.*
import destiny.core.DayNight
import mu.KotlinLogging
import java.io.Serializable


class ClassicalPatternContext(private val rulerImpl: IRuler,
                              private val exaltImpl: IExaltation,
                              private val fallImpl: IFall,
                              private val detrimentImpl: IDetriment,
                              private val triplicityImpl: ITriplicity,
                              private val termImpl: ITerm,
                              private val faceImpl: IFace,
                              private val dayNightDifferentiator: IDayNight,
                              private val dayNightImpl: IDayNight) : Serializable {

  private val essentialImpl: IEssential =
    EssentialImpl(rulerImpl, exaltImpl, fallImpl, detrimentImpl, triplicityImpl, termImpl, faceImpl,
                  dayNightDifferentiator)

  /**
   * to replace [destiny.astrology.classical.rules.essentialDignities.Ruler]
   *
   * A planet in its own sign , or mutual reception with another planet by sign
   */
  val ruler = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getZodiacSign(planet)?.let { sign ->
        if (planet === with(rulerImpl) { sign.getRulerPoint() }) {
          logger.debug("{} 位於 {} , 為其 {}", planet, sign, Dignity.RULER)

          EssentialDignity.Ruler(planet, sign)
        } else {
          null
        }
      }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.essentialDignities.Exaltation]
   */
  val exaltation = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getZodiacSign(planet)?.let { sign ->
        if (planet === with(exaltImpl) { sign.getExaltPoint() }) {
          logger.debug("{} 位於其 {} 的星座 {}", planet, Dignity.EXALTATION, sign)
          EssentialDignity.Exaltation(planet, sign)
        } else
          null
      }
    }
  }

  /**
   * A planet in its own day or night triplicity (not to be confused with the modern triplicities).
   * to replace [destiny.astrology.classical.rules.essentialDignities.Triplicity]
   */
  val triplicity = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getZodiacSign(planet)?.let { sign ->
        val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)
        with(triplicityImpl) {
          if (dayNight == DayNight.DAY && planet === sign.getTriplicityPoint(DayNight.DAY) ||
            dayNight == DayNight.NIGHT && planet === sign.getTriplicityPoint(DayNight.NIGHT)) {
            logger.debug("{} 位於 {} 為其 {} 之 Triplicity", planet, sign, dayNight)
            EssentialDignity.Triplicity(planet, sign, dayNight)
          } else
            null
        }
      }
    }
  }

  /**
   * A planet in itw own term.
   * to replace [destiny.astrology.classical.rules.essentialDignities.Term]
   * */
  val term = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getPosition(planet)?.lng?.let { lngDeg ->
        if (planet === termImpl.getPoint(lngDeg))
          EssentialDignity.Term(planet, lngDeg)
        else
          null
      }
    }
  }

  /**
   * A planet in its own Chaldean decanate or face.
   * to replace [destiny.astrology.classical.rules.essentialDignities.Face]
   * */
  val face = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getPosition(planet)?.lng?.let { lngDeg ->
        if (planet === termImpl.getPoint(lngDeg))
          EssentialDignity.Face(planet, lngDeg)
        else
          null
      }
    }
  }

  /**
   * to replace
   * [destiny.astrology.classical.rules.essentialDignities.Ruler.rulerMutualReception]
   * [destiny.astrology.classical.rules.essentialDignities.Exaltation.exaltMutualReception]
   * [destiny.astrology.classical.rules.essentialDignities.MixedReception]
   */
  val beneficialMutualReception = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      val rulerMutual = with((essentialImpl)) {
        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.RULER)).firstOrNull()?.let { mutualData ->
          val p2 = mutualData.getAnotherPoint(planet)
          EssentialDignity.BeneficialMutualReception(planet, mutualData.getDignityOf(planet), p2,
                                                     mutualData.getDignityOf(p2))
        }
      }

      val exaltMutual = with(essentialImpl) {
        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.EXALTATION)).firstOrNull()?.let { mutualData ->
          val sign1 = h.getZodiacSign(planet)!!
          val p2 = mutualData.getAnotherPoint(planet)
          val sign2 = h.getZodiacSign(p2)!!
          EssentialDignity.BeneficialMutualReception(planet, mutualData.getDignityOf(planet), p2,
                                                     mutualData.getDignityOf(p2))
        }
      }

      val mixedMutual = with(essentialImpl) {
        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.RULER, Dignity.EXALTATION)).firstOrNull()?.let { mutualData ->
            val sign1 = h.getZodiacSign(planet)!!
            val p2 = mutualData.getAnotherPoint(planet)
            val sign2 = h.getZodiacSign(p2)!!
            logger.debug("mutualData = {}", mutualData)
            logger.debug("{} 位於 {} , 與其 {}({}) 飛至 {} . 而 {} 的 {}({}) 飛至 {} , 形成 RULER/EXALT 互容",
                         planet, sign1, mutualData.getDignityOf(p2), p2, sign2, sign2, mutualData.getDignityOf(planet),
                         planet, sign1)
            EssentialDignity.BeneficialMutualReception(planet , mutualData.getDignityOf(planet) , p2 , mutualData.getDignityOf(p2))
          }
      }

      return rulerMutual ?: exaltMutual ?: mixedMutual
    }
  } // beneficialMutualReception


  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
