/**
 * Created by smallufo on 2019-10-02.
 */
package destiny.astrology.classical.rules

import destiny.astrology.IDayNight
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.classical.*
import mu.KotlinLogging
import java.io.Serializable


class ClassicalPatternContext(private val rulerImpl: IRuler,
                              private val exaltImpl: IExaltation,
                              private val fallImpl: IFall,
                              private val detrimentImpl: IDetriment,
                              private val triplicityImpl: ITriplicity,
                              private val termImpl: ITerm,
                              private val faceImpl: IFace,
                              private val dayNightDifferentiator: IDayNight) : Serializable {

  private val essentialImpl: IEssential = EssentialImpl(rulerImpl, exaltImpl, fallImpl, detrimentImpl, triplicityImpl, termImpl, faceImpl, dayNightDifferentiator)

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
          with(essentialImpl) {
            planet.getMutualData(h.pointDegreeMap , null , setOf(Dignity.RULER)).firstOrNull()?.let { mutualData ->
              val sign1 = h.getZodiacSign(planet)!!
              val p2 = mutualData.getAnotherPoint(planet)
              val sign2 = h.getZodiacSign(p2)!!
              EssentialDignity.Ruler(planet, sign)
            }
          }
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

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
