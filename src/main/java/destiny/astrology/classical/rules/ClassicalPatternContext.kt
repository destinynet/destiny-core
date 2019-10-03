/**
 * Created by smallufo on 2019-10-02.
 */
package destiny.astrology.classical.rules

import destiny.astrology.*
import destiny.astrology.Aspect.*
import destiny.astrology.Planet.*
import destiny.astrology.classical.*
import destiny.core.DayNight.DAY
import destiny.core.DayNight.NIGHT
import destiny.core.calendar.TimeTools
import destiny.core.chinese.YinYang
import mu.KotlinLogging
import java.io.Serializable
import java.util.*


class ClassicalPatternContext(private val rulerImpl: IRuler,
                              private val exaltImpl: IExaltation,
                              private val fallImpl: IFall,
                              private val detrimentImpl: IDetriment,
                              private val triplicityImpl: ITriplicity,
                              private val termImpl: ITerm,
                              private val faceImpl: IFace,
                              private val dayNightDifferentiator: IDayNight,
                              private val dayNightImpl: IDayNight,
                              private val besiegedImpl: IBesieged,
                              private val translationOfLightImpl: ITranslationOfLight,
                              private val collectionOfLightImpl: ICollectionOfLight,
                              private val refranationImpl: IRefranation) : Serializable {

  private val essentialImpl: IEssential =
    EssentialImpl(rulerImpl, exaltImpl, fallImpl, detrimentImpl, triplicityImpl, termImpl, faceImpl,
      dayNightDifferentiator)

  /** ====================================== for [EssentialDignity] ====================================== */

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
          if (dayNight == DAY && planet === sign.getTriplicityPoint(DAY) ||
            dayNight == NIGHT && planet === sign.getTriplicityPoint(NIGHT)) {
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

  /** ====================================== for [AccidentalDignity] ====================================== */

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


        jupiterDeg
          ?.takeIf { planet !== JUPITER && IHoroscopeModel.getAngle(planetDeg, jupiterDeg) <= 1 }
          ?.let { AccidentalDignity.Partile_Conj_Jupiter_Venus(planet, JUPITER) }
          ?: {
            venusDeg?.takeIf { planet !== VENUS && IHoroscopeModel.getAngle(planetDeg, venusDeg) <= 1 }
              ?.let { AccidentalDignity.Partile_Conj_Jupiter_Venus(planet, VENUS) }
          }.invoke()

      }
    }
  }

  /**
   * Partile aspect with Dragon's Head (Moon's North Node).
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Partile_Conj_North_Node]
   */
  val partileConjNorthNode = object : IPlanetPatternFactory {

    /** 內定採用 NodeType.MEAN  */
    var nodeType = NodeType.MEAN

    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      val planetDeg: Double? = h.getPosition(planet)?.lng
      val north: LunarNode = LunarNode.of(NorthSouth.NORTH, nodeType)
      val northDeg: Double? = h.getPosition(north)?.lng

      return if (planetDeg != null && northDeg != null && IHoroscopeModel.getAngle(planetDeg, northDeg) <= 1) {
        logger.debug("{} 與 {} 形成 {}", planet, north, CONJUNCTION)
        AccidentalDignity.Partile_Conj_North_Node(planet , north)
      } else {
        null
      }
    }
  }

  /**
   * Partile trine Jupiter or Venus.
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Partile_Trine_Jupiter_Venus]
   */
  val partileTrineJupiterVenus = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      val planetDeg = h.getPosition(planet)?.lng
      val jupiterDeg = h.getPosition(JUPITER)?.lng
      val venusDeg = h.getPosition(VENUS)?.lng

      return planetDeg?.let {

        jupiterDeg?.takeIf {
          planet !== JUPITER && AspectEffectiveModern.isEffective(planetDeg, jupiterDeg, TRINE, 1.0)
        }?.let {
          AccidentalDignity.Partile_Trine_Jupiter_Venus(planet, JUPITER)
        }?: {
          venusDeg?.takeIf {
            planet !== VENUS && AspectEffectiveModern.isEffective(planetDeg, venusDeg, TRINE, 1.0)
          }?.let {
            AccidentalDignity.Partile_Trine_Jupiter_Venus(planet, VENUS)
          }
        }.invoke()
      }
    }
  }

  /**
   * Partile aspect Jupiter or Venus.
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Partile_Sextile_Jupiter_Venus]
   */
  val partileSextileJupiterVenus = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      val planetDeg = h.getPosition(planet)?.lng
      val jupiterDeg = h.getPosition(JUPITER)?.lng
      val venusDeg = h.getPosition(VENUS)?.lng

      return planetDeg?.let {

        jupiterDeg
          ?.takeIf { planet !== JUPITER && AspectEffectiveModern.isEffective(planetDeg, jupiterDeg, SEXTILE, 1.0) }
          ?.let { AccidentalDignity.Partile_Sextile_Jupiter_Venus(planet, JUPITER) }
          ?: {
            venusDeg
              ?.takeIf { planet !== VENUS && AspectEffectiveModern.isEffective(planetDeg, venusDeg, SEXTILE, 1.0) }
              ?.let { AccidentalDignity.Partile_Sextile_Jupiter_Venus(planet, VENUS) }
          }.invoke()
      }
    }
  }

  /**
   * Partile conjunct Cor Leonis (Regulus) at 29deg50' Leo in January 2000.
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Partile_Conj_Regulus]
   * */
  val partileConjRegulus = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      val planetDeg: Double? = h.getPosition(planet)?.lng
      val regulusDeg: Double? = h.getPosition(FixedStar.REGULUS)?.lng
      return if (planetDeg != null && regulusDeg != null && AspectEffectiveModern.isEffective(planetDeg, regulusDeg, CONJUNCTION, 1.0)) {
        logger.debug("{} 與 {} 形成 {}", planet, FixedStar.REGULUS, CONJUNCTION)
        AccidentalDignity.Partile_Conj_Regulus(planet)
      } else
        null
    }
  }

  /**
   * Partile conjunct Spica at 23deg50' Libra in January 2000.
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Partile_Conj_Spica]
   * */
  val partileConjSpica = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      val planetDeg: Double? = h.getPosition(planet)?.lng
      val spicaDeg: Double? = h.getPosition(FixedStar.SPICA)?.lng

      return if (planetDeg != null && spicaDeg != null && AspectEffectiveModern.isEffective(planetDeg, spicaDeg, CONJUNCTION, 1.0)) {
        logger.debug("{} 與 {} 形成 {}", planet, FixedStar.SPICA, CONJUNCTION)
        AccidentalDignity.Partile_Conj_Spica(planet)
      } else
        null
    }
  }

  /**
   * 喜樂宮 Joy House.
   * Mercury in 1st.
   * Moon in 3rd.
   * Venus in 5th.
   * Mars in 6th.
   * Sun in 9th.
   * Jupiter in 11th.
   * Saturn in 12th.
   * to replace [destiny.astrology.classical.rules.accidentalDignities.JoyHouse]
   */
  val joyHouse = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      return h.getHouse(planet)?.let { house ->
        if (planet === MERCURY && house == 1 ||
          planet === MOON && house == 3 ||
          planet === VENUS && house == 5 ||
          planet === MARS && house == 6 ||
          planet === SUN && house == 9 ||
          planet === JUPITER && house == 11 ||
          planet === SATURN && house == 12) {
          AccidentalDignity.JoyHouse(planet, house)
        } else {
          null
        }
      }
    }
  }

  /**
   * 判斷得時 (Hayz) : 白天 , 晝星位於地平面上，落入陽性星座；或是晚上，夜星在地平面上，落入陰性星座
   * 晝星 : 日 , 木 , 土
   * 夜星 : 月 , 金 , 火
   *
   * 相對於得時的，是「不得時」 [outOfSect]
   *
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Hayz]
   */
  val hayz = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)

      return h.getZodiacSign(planet)?.let { sign ->
        h.getHouse(planet)?.let { house ->
          when (dayNight) {
            DAY -> planet.takeIf { arrayOf(SUN, JUPITER, SATURN).contains(it) }
              ?.takeIf { house >= 7 && sign.booleanValue }
              ?.let {
                logger.debug("晝星 {} 於白天在地平面上，落入陽性星座 {} , 得時", planet, sign)
                AccidentalDignity.Hayz(planet, dayNight, YinYang.陽, sign)
              }
            NIGHT -> planet.takeIf { arrayOf(MOON, VENUS, MARS).contains(it) }
              ?.takeIf { house >= 7 && !sign.booleanValue }
              ?.let {
                logger.debug("夜星 {} 於夜晚在地平面上，落入陰性星座 {} , 得時", planet, sign)
                AccidentalDignity.Hayz(planet, dayNight, YinYang.陰, sign)
              }
          }
        }
      }
    }
  }

  /**
   * 夾輔 : 被金星木星包夾 , 是很幸運的情形
   * 角度考量 0/60/90/120/180
   * 中間不能與其他行星形成角度
   *
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Besieged_Jupiter_Venus]
   */
  val besiegedJupiterVenus = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      return planet.takeIf { arrayOf(SUN, MOON, MERCURY, MARS, SATURN).contains(it) }?.takeIf {
        val gmt = TimeTools.getGmtFromLmt(h.lmt, h.location)
        besiegedImpl.isBesieged(it, VENUS, JUPITER, gmt, classical = true, isOnlyHardAspects = false)
      }?.let { AccidentalDignity.Besieged_Jupiter_Venus(planet) }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Translation_of_Light]
   */
  val translationOfLight = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return translationOfLightImpl.getResult(planet, h)
        ?.let { (from, to, aspectType) ->
          val deg = h.getAngle(from, to)
          // {0} 從 {1} 傳遞光線到 {2} ，{1} 與 {2} 交角 {3} 度，相位為 {4} 。 (入相位 or 出相位)
          // {0} 從 {1} 傳遞光線到 {2} ，{1} 與 {2} 交角 {3} 度，未形成相位
          AccidentalDignity.Translation_of_Light(planet, from, to, deg, aspectType)
        }
    }
  }

  /**
   * 目前只將「收集好光 (DIGNITIES) 」視為 Collection of Light ，而「蒐集穢光 (DEBILITIES) 」不納入考慮
   *
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Collection_of_Light]
   */
  val collectionOfLight = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return collectionOfLightImpl.getResult(planet, h, ICollectionOfLight.CollectType.DIGNITIES)?.let { twoPlanets ->
        // {0} 從 {1} 與 {2} 收集光線。 {1} 與 {2} 交角 {3} 度。
        AccidentalDignity.Collection_of_Light(planet, twoPlanets, h.getAngle(twoPlanets[0], twoPlanets[1]))
      }
    }
  }

  /**
   * 在與火星或土星形成交角之前，臨陣退縮，代表避免厄運
   * to replace [destiny.astrology.classical.rules.accidentalDignities.Refrain_from_Mars_Saturn]
   */
  val refrainFromMarsSaturn = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return planet.takeIf { it !== MOON && it !== SUN }
        ?.let {

          planet.takeIf { it !== MARS }?.let { _ ->
            refranationImpl.getImportantResult(h, planet, MARS)?.let { pair ->
              val aspect = pair.second
              logger.debug("{} 逃過了與 {} 形成 {} (Refranation)", planet, MARS, aspect)
              AccidentalDignity.Refrain_from_Mars_Saturn(planet, MARS, aspect)
            }
          } ?: {
            planet.takeIf { it !== SATURN }?.let { _ ->
              refranationImpl.getImportantResult(h, planet, SATURN)?.let { pair ->
                val aspect = pair.second
                logger.debug("{} 逃過了與 {} 形成 {} (Refranation)", planet, SATURN, aspect)
                AccidentalDignity.Refrain_from_Mars_Saturn(planet, SATURN, aspect)
              }
            }
          }.invoke()
        }
    }

  }

  /** ====================================== for [Debility] ====================================== */

  /**
   * In Detriment
   * to replace [destiny.astrology.classical.rules.debilities.Detriment]
   */
  val detriment = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getZodiacSign(planet)?.let { sign ->
        sign.takeIf { planet === with(detrimentImpl) { sign.getDetrimentPoint() } }
          ?.let { Debility.Detriment(planet, sign) }
      }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.debilities.Fall]
   */
  val fall = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getZodiacSign(planet)?.let { sign ->
        sign.takeIf { planet === with(fallImpl) { sign.getFallPoint() } }
        ?.let { Debility.Fall(planet , sign) }
      }
    }
  }

  /**
   * Peregrine : 漂泊、茫游、外出狀態
   *
   * to replace [destiny.astrology.classical.rules.debilities.Peregrine]
   */
  val peregrine = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      return h.getPosition(planet)?.lng?.let { planetDeg ->
        h.getZodiacSign(planet)?.let { sign ->
          val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)
          if (
            planet !== with(rulerImpl) { sign.getRulerPoint() } &&
            planet !== with(exaltImpl) { sign.getExaltPoint() } &&
            planet !== with(detrimentImpl) { sign.getDetrimentPoint() } &&
            planet !== with(fallImpl) { sign.getFallPoint() } &&
            planet !== with(termImpl) { sign.getTermPoint(planetDeg) } &&
            planet !== faceImpl.getPoint(planetDeg)) {
            with(triplicityImpl) {
              // 判定日夜 Triplicity
              if (!(dayNight == DAY && planet === sign.getTriplicityPoint(DAY))
                && !(dayNight == NIGHT && planet === sign.getTriplicityPoint(NIGHT)))
                Debility.Peregrine(planet)
              else
                null
            }
          } else {
            null
          }
        }
      }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.debilities.House_12]
   */
  val house_12 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getHouse(planet)
        ?.takeIf { it == 12 }
        ?.let { Debility.House_12(planet) }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.debilities.House_6_8]
   */
  val house_6_8 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getHouse(planet)
        ?.takeIf { it == 6 || it == 8 }
        ?.let { Debility.House_6_8(planet, it) }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.debilities.Retrograde]
   */
  val retrograde = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getStarPosition(planet)
        ?.speedLng
        ?.takeIf { it < 0 }
        ?.let { Debility.Retrograde(planet) }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.debilities.Slower]
   */
  val slower = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return AverageDailyMotionMap.getAvgDailySpeed(planet)
        ?.takeIf { dailyDeg ->
          h.getStarPosition(planet)
            ?.speedLng?.let { speedLng ->
            speedLng < dailyDeg
          } ?: false
        }?.let { Debility.Slower(planet) }
    }
  }

  /**
   * 火星、木星、或土星，在太陽西方
   * to replace [destiny.astrology.classical.rules.debilities.Occidental]
   */
  val occidental2 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return arrayOf(MARS, JUPITER, SATURN)
        .takeIf { it.contains(planet) }
        ?.let { h.getPosition(planet) }?.lng?.let { planetDegree ->
        h.getPosition(SUN)?.lng?.takeIf { sunDegree ->
          IHoroscopeModel.isOccidental(planetDegree, sunDegree)
        }?.let {
          Debility.Occidental(planet)
        }
      }
    }
  }

  /**
   * Mercury, or Venus oriental to the Sun.
   * 金星、水星，是否 東出 於 太陽
   *
   * to replace [destiny.astrology.classical.rules.debilities.Oriental]
   */
  val oriental2 = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return arrayOf(MERCURY, VENUS)
        .takeIf { it.contains(planet) }
        ?.let { h.getPosition(planet) }?.lng?.let { planetDegree ->
        h.getPosition(SUN)?.lng?.takeIf { sunDegree ->
          IHoroscopeModel.isOriental(planetDegree, sunDegree)
        }?.let {
          Debility.Oriental(planet)
        }
      }
    }
  }

  /**
   * Moon decreasing in light.
   * to replace [destiny.astrology.classical.rules.debilities.Moon_Decrease_Light]
   * */
  val moonDecreaseLight = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return planet
        .takeIf { it === MOON }
        ?.let { h.getPosition(it) }?.lng?.let { moonDegree ->
        h.getPosition(SUN)?.lng?.takeIf { sunDegree ->
          IHoroscopeModel.isOriental(moonDegree, sunDegree)
        }?.let {
          Debility.Moon_Decrease_Light
        }
      }
    }
  }

  /**
   * Combust the Sun (between 17' and 8.5 from Sol).
   * to replace [destiny.astrology.classical.rules.debilities.Combustion]
   */
  val combustion = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return planet.takeIf { it !== SUN }
        ?.takeIf { h.getAngle(planet, SUN) > 17.0 / 60.0 && h.getAngle(planet, SUN) <= 8.5 }
        ?.let { Debility.Combustion(planet) }
    }
  }

  /**
   * Under the Sunbeams (between 8.5 and 17 from Sol).
   *
   * to replace [destiny.astrology.classical.rules.debilities.Sunbeam]
   * */
  val sunbeam = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return planet.takeIf { it !== SUN }
        ?.takeIf { h.getAngle(it, SUN) > 8.5 && h.getAngle(it, SUN) <= 17.0 }
        ?.let { Debility.Sunbeam(planet) }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.debilities.Partile_Conj_Mars_Saturn]
   */
  val partileConjMarsSaturn = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      return h.getPosition(planet)?.lng?.let { planetDeg ->
        val marsDeg = h.getPosition(MARS)?.lng
        val saturnDeg = h.getPosition(SATURN)?.lng

        marsDeg?.takeIf {
          planet !== MARS && IHoroscopeModel.getAngle(planetDeg, marsDeg) <= 1
        }?.let {
          Debility.Partile_Conj_Mars_Saturn(planet, MARS)
        }?: {
          saturnDeg?.takeIf {
            planet != SATURN && IHoroscopeModel.getAngle(planetDeg, saturnDeg) <= 1
          }?.let { Debility.Partile_Conj_Mars_Saturn(planet, SATURN) }
        }.invoke()

      }
    }
  }


  /**
   * Partile conjunction with Dragon's Tail (Moon's South Node).
   * to replace [destiny.astrology.classical.rules.debilities.Partile_Conj_South_Node]
   * */
  val partileConjSouthNode = object : IPlanetPatternFactory {
    /** 內定採用 NodeType.MEAN  */
    var nodeType = NodeType.MEAN

    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      return h.getPosition(planet)?.lng?.let { planetDeg ->
        val south = LunarNode.of(NorthSouth.SOUTH, nodeType)
        h.getPosition(south)?.lng?.takeIf { southDeg ->
          IHoroscopeModel.getAngle(planetDeg, southDeg) <= 1
        }?.let {
          Debility.Partile_Conj_South_Node(planet)
        }
      }
    }
  }

  /**
   * Besieged between Mars and Saturn.
   * 被火土夾制，只有日月水金，這四星有可能發生
   * 前一個角度與火土之一形成 0/90/180 , 後一個角度又與火土另一顆形成 0/90/180
   * 中間不能與其他行星形成角度
   * to replace [destiny.astrology.classical.rules.debilities.Besieged_Mars_Saturn]
   */
  val besiegedMarsSaturn = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      return planet.takeIf { arrayOf(SUN, MOON, MERCURY, VENUS).contains(it) }
        ?.takeIf {
          val gmt = TimeTools.getGmtFromLmt(h.lmt, h.location)
          //火土夾制，只考量「硬」角度 , 所以最後一個參數設成 true
          besiegedImpl.isBesieged(it, MARS, SATURN, gmt, classical = true, isOnlyHardAspects = true)
        }?.let {
          Debility.Besieged_Mars_Saturn(planet)
        }
    }
  }

  /**
   * Partile opposite Mars or Saturn.
   * to replace [destiny.astrology.classical.rules.debilities.Partile_Oppo_Mars_Saturn]
   */
  val partileOppoMarsSaturn = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getPosition(planet)?.lng?.let { planetDeg ->

        h.getPosition(MARS)?.lng?.takeIf { marsDeg ->
          planet !== MARS && AspectEffectiveModern.isEffective(planetDeg, marsDeg, OPPOSITION, 1.0)
        }?.let {
          Debility.Partile_Oppo_Mars_Saturn(planet, MARS)
        }?: {
          h.getPosition(SATURN)?.lng?.takeIf { saturnDeg ->
            planet != SATURN && AspectEffectiveModern.isEffective(planetDeg, saturnDeg, OPPOSITION, 1.0)
          }?.let {
            Debility.Partile_Oppo_Mars_Saturn(planet, SATURN)
          }
        }.invoke()
      }
    }
  }

  /**
   * Partile square Mars or Saturn.
   * to replace [destiny.astrology.classical.rules.debilities.Partile_Square_Mars_Saturn]
   * */
  val partileSquareMarsSaturn = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getPosition(planet)?.lng?.let { planetDeg ->

        h.getPosition(MARS)?.lng?.takeIf { marsDeg ->
          planet !== MARS && AspectEffectiveModern.isEffective(planetDeg, marsDeg, SQUARE, 1.0)
        }?.let {
          Debility.Partile_Square_Mars_Saturn(planet, MARS)
        }?: {
          h.getPosition(SATURN)?.lng?.takeIf { saturnDeg ->
            planet != SATURN && AspectEffectiveModern.isEffective(planetDeg, saturnDeg, SQUARE, 1.0)
          }?.let {
            Debility.Partile_Square_Mars_Saturn(planet, SATURN)
          }
        }.invoke()

      }
    }
  }

  /**
   * Within 5 deg of Caput Algol at 26 deg 10' Taurus in January 2000.
   * to replace [destiny.astrology.classical.rules.debilities.Conj_Algol]
   * */
  val conjAlgol = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return h.getPosition(planet)?.lng?.let { planetDeg ->
        h.getPosition(FixedStar.ALGOL)?.lng?.takeIf { algolDeg ->
          AspectEffectiveModern.isEffective(planetDeg, algolDeg, CONJUNCTION, 5.0)
        }?.let { _ ->
          logger.debug("{} 與 {} 形成 {}", planet, FixedStar.ALGOL, CONJUNCTION)
          Debility.Conj_Algol(planet)
        }
      }
    }
  }

  /**
   * 判斷不得時 (Out of sect) : 白天 , 夜星位於地平面上，落入陽性星座；或是晚上，晝星在地平面上，落入陰性星座
   * 晝星 : 日 , 木 , 土
   * 夜星 : 月 , 金 , 火
   *
   * 相對於不得時的，是「得時」 [hayz]
   *
   * to replace [destiny.astrology.classical.rules.debilities.Out_of_Sect]
   */
  val outOfSect = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {

      return h.getZodiacSign(planet)?.let { sign ->
        h.getHouse(planet)?.let { house ->
          when (val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)) {
            DAY -> if (arrayOf(MOON, VENUS, MARS).contains(planet) && house >= 7 && sign.booleanValue) {
              logger.debug("夜星 {} 於白天在地平面上，落入陽性星座 {} , 不得時", planet, sign.toString(Locale.TAIWAN))
              Debility.Out_of_Sect(planet, dayNight, YinYang.陽, sign)
            } else {
              null
            }
            NIGHT -> if (arrayOf(SUN, JUPITER, SATURN).contains(planet) && house >= 7 && !sign.booleanValue) {
              logger.debug("晝星 {} 於夜晚在地平面上，落入陰性星座 {} , 不得時", planet, sign.toString(Locale.TAIWAN))
              Debility.Out_of_Sect(planet, dayNight, YinYang.陰, sign)
            } else {
              null
            }
          }
        }
      }
    }
  }

  /**
   * to replace [destiny.astrology.classical.rules.debilities.Refrain_from_Venus_Jupiter]
   */
  val refrainFromVenusJupiter = object : IPlanetPatternFactory {
    override fun getPattern(planet: Planet, h: IHoroscopeModel): IPlanetPattern? {
      return planet.takeIf { it !== SUN && it !== MOON }?.let {

        planet.takeIf { it !== VENUS }?.let {
          refranationImpl.getImportantResult(h, planet, VENUS)
        }?.let { (_, aspect) ->
          logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, VENUS, aspect)
          Debility.Refrain_from_Venus_Jupiter(planet, VENUS , aspect)
        }?: {
          planet.takeIf { it !== JUPITER }?.let {
            refranationImpl.getImportantResult(h , planet , JUPITER)
          }?.let { (_ , aspect) ->
            logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, JUPITER, aspect)
            Debility.Refrain_from_Venus_Jupiter(planet, JUPITER , aspect)
          }
        }.invoke()

      }
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
