/**
 * Created by smallufo on 2019-10-02.
 */
package destiny.astrology.classical.rules

import destiny.astrology.IDayNight
import destiny.astrology.IHoroscopeModel
import destiny.astrology.Planet
import destiny.astrology.Planet.*
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
        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.RULER, Dignity.EXALTATION)).firstOrNull()
          ?.let { mutualData ->
            val sign1 = h.getZodiacSign(planet)!!
            val p2 = mutualData.getAnotherPoint(planet)
            val sign2 = h.getZodiacSign(p2)!!
            logger.debug("mutualData = {}", mutualData)
            logger.debug("{} 位於 {} , 與其 {}({}) 飛至 {} . 而 {} 的 {}({}) 飛至 {} , 形成 RULER/EXALT 互容",
                         planet, sign1, mutualData.getDignityOf(p2), p2, sign2, sign2, mutualData.getDignityOf(planet),
                         planet, sign1)
            EssentialDignity.BeneficialMutualReception(planet, mutualData.getDignityOf(planet), p2,
                                                       mutualData.getDignityOf(p2))
          }
      }

      return rulerMutual ?: exaltMutual ?: mixedMutual
    }
  } // beneficialMutualReception

  // ================================================================================================================

  /**
   * to replace [destiny.astrology.classical.rules.accidentalDignities.House_1_10]
   */
  val house_1_10 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getHouse(planet)
        ?.takeIf { it == 1 || it == 10 }
        ?.let { house ->
          Pair("comment", arrayOf(planet, house))
          AccidentalDignity.House_1_10(planet, house)
        }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.accidentalDignities.House_4_7_11]
   */
  val house_4_7_11 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getHouse(planet)
        ?.takeIf { it == 2 || it == 5 }
        ?.let { house ->
          Pair("comment", arrayOf(planet, house))
          AccidentalDignity.House_4_7_11(planet, house)
        }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.accidentalDignities.House_2_5]
   */
  val house_2_5 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getHouse(planet)
        ?.takeIf { it == 2 || it == 5 }
        ?.let { house ->
          AccidentalDignity.House_2_5(planet, house)
        }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.accidentalDignities.House_9]
   */
  val house_9 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getHouse(planet)
        ?.takeIf { it == 9 }
        ?.let { _ ->
          AccidentalDignity.House_9(planet)
        }
    }
  }


  /**
   * to replace [destiny.astrology.classical.rules.accidentalDignities.House_3]
   */
  val house_3 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getHouse(planet)
        ?.takeIf { it == 3 }
        ?.let { _ ->
          AccidentalDignity.House_3(planet)
        }
    }
  }

  /**
   * Direct in motion (does not apply to Sun and Moon).
   * to replace  [destiny.astrology.classical.rules.accidentalDignities.Direct]
   */
  val direct = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return planet.takeIf { it !== SUN && it !== MOON }
        ?.let { h.getStarPosition(it) }
        ?.speedLng
        ?.takeIf { it > 0 }
        ?.let {
          AccidentalDignity.Direct(planet)
        }
    }
  }

  /**
   * Swift in motion (faster than average).
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Swift]
   * */
  val swift = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return AverageDailyMotionMap.getAvgDailySpeed(planet)?.takeIf { dailyDeg ->
        h.getStarPosition(planet)
          ?.speedLng?.let { speedLng ->
          speedLng > dailyDeg
        } ?: false
      }?.let { AccidentalDignity.Swift(planet) }
    }
  }

  /**
   * Mars, Jupiter, or Saturn oriental of (rising before) the Sun.
   * 火星、木星、土星 是否 東出 於 太陽
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Oriental]
   */
  val oriental = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      val planetDegree: Double? = arrayOf(MARS, JUPITER, SATURN)
        .takeIf { it.contains(planet) }
        ?.let { h.getPosition(planet) }?.lng

      val sunDegree: Double? = h.getPosition(SUN)?.lng

      return if (sunDegree != null && planetDegree != null && IHoroscopeModel.isOriental(planetDegree, sunDegree)) {
        AccidentalDignity.Oriental(planet)
      } else {
        null
      }
    }
  }

  /**
   * Mercury, or Venus occidental of (rising after) the Sun.
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Occidental]
   * */
  val occidental = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      val planetDeg: Double? = planet.takeIf { it === MERCURY || it === VENUS }
        ?.let { h.getPosition(it) }?.lng
      val sunDeg: Double? = h.getPosition(SUN)?.lng
      return if (planetDeg != null && sunDeg != null && IHoroscopeModel.isOccidental(planetDeg, sunDeg)) {
        AccidentalDignity.Occidental(planet)
      } else {
        null
      }
    }
  }

  /**
   * Moon increasing in light (月增光/上弦月) , or occidental of the Sun.
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Moon_Increase_Light]
   * */
  val moonIncreaseLight = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      val moonDeg = planet.takeIf { it === MOON }
        ?.let { h.getPosition(it) }?.lng
      val sunDeg = h.getPosition(SUN)?.lng
      return if (moonDeg != null && sunDeg != null && IHoroscopeModel.isOccidental(moonDeg, sunDeg)) {
        AccidentalDignity.Moon_Increase_Light
      } else {
        null
      }
    }
  }

  /**
   * Free from combustion and the Sun's rays. 只要脫離了太陽左右 17度，就算 Free Combustion !?
   * TODO : refine the definition , it is too broad
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Free_Combustion]
   */
  val freeCombustion = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return planet.takeIf { it !== SUN }
        ?.takeIf { h.getAngle(it, SUN) > 17 }
        ?.let {
          AccidentalDignity.Free_Combustion(planet)
        }
    }
  }

  /**
   * Cazimi (within 17 minutes of the Sun).
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Cazimi]
   * */
  val cazimi = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return planet.takeIf { it !== SUN }
        ?.takeIf { h.getAngle(it, SUN) < 17.0 / 60.0 }
        ?.let { AccidentalDignity.Cazimi(planet) }
    }
  }

  /**
   * Partile conjunction with Jupiter or Venus.
   * 和金星或木星合相，交角 1 度內
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Partile_Conj_Jupiter_Venus]
   * */
  val partileConjJupiterVenus = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      val planetDeg = h.getPosition(planet)?.lng
      val jupiterDeg = h.getPosition(JUPITER)?.lng
      val venusDeg = h.getPosition(VENUS)?.lng

      return planetDeg?.let {
        val jupResult = jupiterDeg?.takeIf {
          planet !== JUPITER && IHoroscopeModel.getAngle(planetDeg, jupiterDeg) <= 1
        }?.let { AccidentalDignity.Partile_Conj_Jupiter_Venus(planet, JUPITER) }

        val venResult = venusDeg?.takeIf {
          planet !== VENUS && IHoroscopeModel.getAngle(planetDeg, venusDeg) <= 1
        }?.let { AccidentalDignity.Partile_Conj_Jupiter_Venus(planet, VENUS) }

        jupResult?:venResult
      }
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
