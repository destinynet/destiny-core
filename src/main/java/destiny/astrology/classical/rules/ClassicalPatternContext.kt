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
                              private val dayNightImpl: IDayNight,
                              private val besiegedImpl: IBesieged,
                              private val translationOfLightImpl: ITranslationOfLight,
                              private val collectionOfLightImpl: ICollectionOfLight,
                              private val refranationImpl: IRefranation) : Serializable {

  private val essentialImpl: IEssential =
    EssentialImpl(rulerImpl, exaltImpl, fallImpl, detrimentImpl, triplicityImpl, termImpl, faceImpl,
      dayNightImpl)

  /**
   * ====================================== for [EssentialDignity] ======================================
   */

  /**
   * A planet in its own sign
   */
  val ruler = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return h.getZodiacSign(planet)?.takeIf { sign ->
        (planet === with(rulerImpl) { sign.getRulerPoint() })
      }?.let { sign ->
        logger.debug("{} 位於 {} , 為其 {}", planet, sign, Dignity.RULER)
        EssentialDignity.Ruler(planet, sign)
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /** A planet in its exaltation */
  val exaltation = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return h.getZodiacSign(planet)?.takeIf { sign ->
        (planet === with(exaltImpl) { sign.getExaltPoint() })
      }?.let { sign ->
        logger.debug("{} 位於其 {} 的星座 {}", planet, Dignity.EXALTATION, sign)
        EssentialDignity.Exaltation(planet, sign)
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * A planet in its own day or night triplicity (not to be confused with the modern triplicities).
   */
  val triplicity = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)
      return h.getZodiacSign(planet)?.takeIf { sign ->
        with(triplicityImpl) {
          (dayNight == DAY && planet === sign.getTriplicityPoint(DAY) ||
            dayNight == NIGHT && planet === sign.getTriplicityPoint(NIGHT))
        }
      }?.let { sign ->
        logger.debug("{} 位於 {} 為其 {} 之 Triplicity", planet, sign, dayNight)
        EssentialDignity.Triplicity(planet, sign, dayNight)
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()

    }
  }

  /**
   * A planet in itw own term.
   * */
  val term = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getPosition(planet)?.lng?.takeIf { lngDeg ->
        (planet === termImpl.getPoint(lngDeg))
      }?.let { lngDeg ->
        EssentialDignity.Term(planet, lngDeg)
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * A planet in its own Chaldean decanate or face.
   * */
  val face = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getPosition(planet)?.lng?.takeIf { lngDeg ->
        (planet === faceImpl.getPoint(lngDeg))
      }?.let { lngDeg ->
        EssentialDignity.Face(planet, lngDeg)
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * RR / EE / RE 互容
   */
  val beneficialMutualReception = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      val rulerMutual = with((essentialImpl)) {
        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.RULER)).firstOrNull()?.let { mutualData ->
          val p2 = mutualData.getAnotherPoint(planet)
          val sign1 = h.getZodiacSign(planet)!!
          val sign2 = h.getZodiacSign(p2)!!
          EssentialDignity.BeneficialMutualReception(planet, sign1, mutualData.getDignityOf(planet),
            p2, sign2, mutualData.getDignityOf(p2))
        }
      }

      val exaltMutual = with(essentialImpl) {
        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.EXALTATION)).firstOrNull()?.let { mutualData ->
          val sign1 = h.getZodiacSign(planet)!!
          val p2 = mutualData.getAnotherPoint(planet)
          val sign2 = h.getZodiacSign(p2)!!
          EssentialDignity.BeneficialMutualReception(planet, sign1, mutualData.getDignityOf(planet),
            p2, sign2, mutualData.getDignityOf(p2))
        }
      }

      val mixedMutual = with(essentialImpl) {
        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.RULER, Dignity.EXALTATION)).firstOrNull()
          ?.takeIf { mutualData ->
            val p2 = mutualData.getAnotherPoint(planet)
            val dig1 = mutualData.getDignityOf(planet)
            val dig2 = mutualData.getDignityOf(p2)
            dig1 !== dig2
          }
          ?.let { mutualData ->
            val sign1 = h.getZodiacSign(planet)!!
            val p2 = mutualData.getAnotherPoint(planet)
            val sign2 = h.getZodiacSign(p2)!!
            logger.debug("mutualData = {}", mutualData)
            logger.debug("{} 位於 {} , 與其 {}({}) 飛至 {} . 而 {} 的 {}({}) 飛至 {} , 形成 RULER/EXALT 互容",
              planet, sign1, mutualData.getDignityOf(p2), p2, sign2, sign2, mutualData.getDignityOf(planet),
              planet, sign1)
            EssentialDignity.BeneficialMutualReception(planet, sign1, mutualData.getDignityOf(planet),
              p2, sign2, mutualData.getDignityOf(p2))
          }
      }

      return listOfNotNull(rulerMutual, exaltMutual, mixedMutual)
    }
  } // beneficialMutualReception

  /**
   * ====================================== for [AccidentalDignity] ======================================
   */

  val house_1_10 = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getHouse(planet)
        ?.takeIf { it == 1 || it == 10 }
        ?.let { house ->
          AccidentalDignity.House_1_10(planet, house)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val house_4_7_11 = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getHouse(planet)
        ?.takeIf { intArrayOf(4, 7, 11).contains(it) }
        ?.let { house ->
          AccidentalDignity.House_4_7_11(planet, house)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val house_2_5 = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getHouse(planet)
        ?.takeIf { it == 2 || it == 5 }
        ?.let { house ->
          AccidentalDignity.House_2_5(planet, house)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val house_9 = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getHouse(planet)
        ?.takeIf { it == 9 }
        ?.let { _ ->
          AccidentalDignity.House_9(planet)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }


  val house_3 = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getHouse(planet)
        ?.takeIf { it == 3 }
        ?.let { _ ->
          AccidentalDignity.House_3(planet)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Direct in motion (does not apply to Sun and Moon).
   */
  val direct = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return planet.takeIf { it !== SUN && it !== MOON }
        ?.let { h.getStarPosition(it) }
        ?.speedLng
        ?.takeIf { it > 0 }
        ?.let {
          AccidentalDignity.Direct(planet)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Swift in motion (faster than average).
   * */
  val swift = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return AverageDailyMotionMap.getAvgDailySpeed(planet)?.takeIf { dailyDeg ->
        h.getStarPosition(planet)
          ?.speedLng?.let { speedLng ->
          speedLng > dailyDeg
        } ?: false
      }?.let {
        AccidentalDignity.Swift(planet)
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Mars, Jupiter, or Saturn oriental of (rising before) the Sun.
   * 火星、木星、土星 是否 東出 於 太陽
   */
  val orientalGood = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return arrayOf(MARS, JUPITER, SATURN)
        .takeIf { it.contains(planet) }
        ?.let { h.getPosition(planet) }?.lng
        ?.let { planetDegree ->
          h.getPosition(SUN)?.lng?.takeIf { sunDegree ->
            IHoroscopeModel.isOriental(planetDegree, sunDegree)
          }
        }?.let {
          AccidentalDignity.Oriental(planet)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Mercury, or Venus occidental of (rising after) the Sun.
   * */
  val occidentalGood = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return planet.takeIf { it === MERCURY || it === VENUS }
        ?.let { it -> h.getPosition(it) }?.lng?.let { planetDeg ->
        h.getPosition(SUN)?.lng?.takeIf { sunDeg ->
          IHoroscopeModel.isOccidental(planetDeg, sunDeg)
        }?.let {
          AccidentalDignity.Occidental(planet)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Moon increasing in light (月增光/上弦月) , or occidental of the Sun.
   * */
  val moonIncreaseLight = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return planet.takeIf { it === MOON }
        ?.let { h.getPosition(it) }?.lng?.let { moonDeg ->
        h.getPosition(SUN)?.lng?.takeIf { sunDeg ->
          IHoroscopeModel.isOccidental(moonDeg, sunDeg)
        }?.let {
          AccidentalDignity.Moon_Increase_Light
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()

    }
  }

  /**
   * Free from combustion and the Sun's rays. 只要脫離了太陽左右 17度，就算 Free Combustion !?
   * TODO : refine the definition , it is too broad
   */
  val freeCombustion = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return planet.takeIf { it !== SUN }
        ?.takeIf { h.getAngle(it, SUN) > 17 }
        ?.let {
          AccidentalDignity.Free_Combustion(planet)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Cazimi (within 17 minutes of the Sun).
   * */
  val cazimi = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return planet.takeIf { it !== SUN }
        ?.takeIf { h.getAngle(it, SUN) < 17.0 / 60.0 }
        ?.let { AccidentalDignity.Cazimi(planet) }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Partile conjunction with Jupiter or Venus.
   * 和金星或木星合相，交角 1 度內
   * */
  val partileConjJupiterVenus = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
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
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Partile aspect with Dragon's Head (Moon's North Node).
   */
  val partileConjNorthNode = object : IPlanetPatternFactory {

    /** 內定採用 NodeType.MEAN  */
    var nodeType = NodeType.MEAN

    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      val north: LunarNode = LunarNode.of(NorthSouth.NORTH, nodeType)
      return h.getPosition(planet)?.lng?.let { planetDeg ->
        h.getPosition(north)?.lng?.takeIf { northDeg ->
          IHoroscopeModel.getAngle(planetDeg, northDeg) <= 1
        }?.let {
          logger.debug("{} 與 {} 形成 {}", planet, north, CONJUNCTION)
          AccidentalDignity.Partile_Conj_North_Node(planet, north)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()

    }
  }

  /**
   * Partile trine Jupiter or Venus.
   */
  val partileTrineJupiterVenus = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      val planetDeg = h.getPosition(planet)?.lng
      val jupiterDeg = h.getPosition(JUPITER)?.lng
      val venusDeg = h.getPosition(VENUS)?.lng


      return planetDeg?.let {

        val trineJupiter = jupiterDeg?.takeIf {
          planet !== JUPITER && AspectEffectiveModern.isEffective(planetDeg, jupiterDeg, TRINE, 1.0)
        }?.let {
          AccidentalDignity.Partile_Trine_Jupiter_Venus(planet, JUPITER)
        }

        val trineVenus = venusDeg?.takeIf {
          planet !== VENUS && AspectEffectiveModern.isEffective(planetDeg, venusDeg, TRINE, 1.0)
        }?.let {
          AccidentalDignity.Partile_Trine_Jupiter_Venus(planet, VENUS)
        }

        listOfNotNull(trineJupiter, trineVenus)

      } ?: emptyList()
    }
  }

  /**
   * Partile aspect Jupiter or Venus.
   */
  val partileSextileJupiterVenus = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      val planetDeg = h.getPosition(planet)?.lng
      val jupiterDeg = h.getPosition(JUPITER)?.lng
      val venusDeg = h.getPosition(VENUS)?.lng

      return planetDeg?.let {

        val sextileJupiter = jupiterDeg
          ?.takeIf { planet !== JUPITER && AspectEffectiveModern.isEffective(planetDeg, jupiterDeg, SEXTILE, 1.0) }
          ?.let { AccidentalDignity.Partile_Sextile_Jupiter_Venus(planet, JUPITER) }

        val sextileVenus = venusDeg
          ?.takeIf { planet !== VENUS && AspectEffectiveModern.isEffective(planetDeg, venusDeg, SEXTILE, 1.0) }
          ?.let { AccidentalDignity.Partile_Sextile_Jupiter_Venus(planet, VENUS) }

        listOfNotNull(sextileJupiter, sextileVenus)
      } ?: emptyList()
    }
  }

  /**
   * Partile conjunct Cor Leonis (Regulus) at 29deg50' Leo in January 2000.
   * */
  val partileConjRegulus = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return h.getPosition(planet)?.lng?.let { planetDeg ->
        h.getPosition(FixedStar.REGULUS)?.lng?.takeIf { regulusDeg ->
          AspectEffectiveModern.isEffective(planetDeg, regulusDeg, CONJUNCTION, 1.0)
        }?.let {
          AccidentalDignity.Partile_Conj_Regulus(planet)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Partile conjunct Spica at 23deg50' Libra in January 2000.
   * */
  val partileConjSpica = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return h.getPosition(planet)?.lng?.let { planetDeg ->
        h.getPosition(FixedStar.SPICA)?.lng?.takeIf { spicaDeg ->
          AspectEffectiveModern.isEffective(planetDeg, spicaDeg, CONJUNCTION, 1.0)
        }?.let {
          logger.debug("{} 與 {} 形成 {}", planet, FixedStar.SPICA, CONJUNCTION)
          AccidentalDignity.Partile_Conj_Spica(planet)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
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
   */
  val joyHouse = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return h.getHouse(planet)?.takeIf { house ->
        (planet === MERCURY && house == 1 ||
          planet === MOON && house == 3 ||
          planet === VENUS && house == 5 ||
          planet === MARS && house == 6 ||
          planet === SUN && house == 9 ||
          planet === JUPITER && house == 11 ||
          planet === SATURN && house == 12)
      }?.let { house ->
        AccidentalDignity.JoyHouse(planet, house)
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()

    }
  }

  /**
   * 判斷得時 (Hayz) : 白天 , 晝星位於地平面上，落入陽性星座；或是晚上，夜星在地平面上，落入陰性星座
   * 晝星 : 日 , 木 , 土
   * 夜星 : 月 , 金 , 火
   *
   * 相對於得時的，是「不得時」 [outOfSect]
   *
   */
  val hayz = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

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
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * 夾輔 : 被金星木星包夾 , 是很幸運的情形
   * 角度考量 0/60/90/120/180
   * 中間不能與其他行星形成角度
   */
  val besiegedJupiterVenus = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return planet.takeIf { arrayOf(SUN, MOON, MERCURY, MARS, SATURN).contains(it) }?.takeIf {
        val gmt = TimeTools.getGmtFromLmt(h.lmt, h.location)
        besiegedImpl.isBesieged(it, VENUS, JUPITER, gmt, classical = true, isOnlyHardAspects = false)
      }?.let {
        AccidentalDignity.Besieged_Jupiter_Venus(planet)
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val translationOfLight = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return translationOfLightImpl.getResult(planet, h)
        ?.let { (from, to, aspectType) ->
          val deg = h.getAngle(from, to)
          // {0} 從 {1} 傳遞光線到 {2} ，{1} 與 {2} 交角 {3} 度，相位為 {4} 。 (入相位 or 出相位)
          // {0} 從 {1} 傳遞光線到 {2} ，{1} 與 {2} 交角 {3} 度，未形成相位
          AccidentalDignity.Translation_of_Light(planet, from, to, deg, aspectType)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * 目前只將「收集好光 (DIGNITIES) 」視為 Collection of Light ，而「蒐集穢光 (DEBILITIES) 」不納入考慮
   */
  val collectionOfLight = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return collectionOfLightImpl.getResult(planet, h, ICollectionOfLight.CollectType.DIGNITIES)?.let { twoPlanets ->
        // {0} 從 {1} 與 {2} 收集光線。 {1} 與 {2} 交角 {3} 度。
        AccidentalDignity.Collection_of_Light(planet, twoPlanets, h.getAngle(twoPlanets[0], twoPlanets[1]))
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * 在與火星或土星形成交角之前，臨陣退縮，代表避免厄運
   */
  val refrainFromMarsSaturn = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
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
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }

  }

  /**
   * ====================================== for [Debility] ======================================
   * */

  /** In Detriment */
  val detriment = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getZodiacSign(planet)?.let { sign ->
        sign.takeIf { planet === with(detrimentImpl) { sign.getDetrimentPoint() } }
          ?.let { Debility.Detriment(planet, sign) }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /** In Fall */
  val fall = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getZodiacSign(planet)?.let { sign ->
        sign.takeIf { planet === with(fallImpl) { sign.getFallPoint() } }
          ?.let { Debility.Fall(planet, sign) }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Peregrine : 漂泊、茫游、外出狀態
   */
  val peregrine = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return h.getPosition(planet)?.lng?.let { planetDeg ->
        val dayNight = dayNightImpl.getDayNight(h.lmt, h.location)
        h.getZodiacSign(planet)?.takeIf { sign ->

          planet !== with(rulerImpl) { sign.getRulerPoint() } &&
            planet !== with(exaltImpl) { sign.getExaltPoint() } &&
            planet !== with(detrimentImpl) { sign.getDetrimentPoint() } &&
            planet !== with(fallImpl) { sign.getFallPoint() } &&
            planet !== with(termImpl) { sign.getTermPoint(planetDeg) } &&
            planet !== faceImpl.getPoint(planetDeg)
        }?.takeIf { sign ->
          with(triplicityImpl) {
            (!(dayNight == DAY && planet === sign.getTriplicityPoint(DAY))
              && !(dayNight == NIGHT && planet === sign.getTriplicityPoint(NIGHT)))
          }
        }?.let {
          Debility.Peregrine(planet)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()


    }
  }

  val house_12 = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getHouse(planet)
        ?.takeIf { it == 12 }
        ?.let { Debility.House_12(planet) }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val house_6_8 = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getHouse(planet)
        ?.takeIf { it == 6 || it == 8 }
        ?.let { Debility.House_6_8(planet, it) }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val retrograde = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getStarPosition(planet)
        ?.speedLng
        ?.takeIf { it < 0 }
        ?.let { Debility.Retrograde(planet) }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val slower = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return AverageDailyMotionMap.getAvgDailySpeed(planet)
        ?.takeIf { dailyDeg ->
          h.getStarPosition(planet)
            ?.speedLng?.let { speedLng ->
            speedLng < dailyDeg
          } ?: false
        }?.let { Debility.Slower(planet) }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * 火星、木星、或土星，在太陽西方
   */
  val occidentalBad = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return arrayOf(MARS, JUPITER, SATURN)
        .takeIf { it.contains(planet) }
        ?.let { h.getPosition(planet) }?.lng?.let { planetDegree ->
        h.getPosition(SUN)?.lng?.takeIf { sunDegree ->
          IHoroscopeModel.isOccidental(planetDegree, sunDegree)
        }?.let {
          Debility.Occidental(planet)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Mercury, or Venus oriental to the Sun.
   * 金星、水星，是否 東出 於 太陽
   *
   */
  val orientalBad = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return arrayOf(MERCURY, VENUS)
        .takeIf { it.contains(planet) }
        ?.let { h.getPosition(planet) }?.lng?.let { planetDegree ->
        h.getPosition(SUN)?.lng?.takeIf { sunDegree ->
          IHoroscopeModel.isOriental(planetDegree, sunDegree)
        }?.let {
          Debility.Oriental(planet)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Moon decreasing in light.
   * */
  val moonDecreaseLight = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return planet
        .takeIf { it === MOON }
        ?.let { h.getPosition(it) }?.lng?.let { moonDegree ->
        h.getPosition(SUN)?.lng?.takeIf { sunDegree ->
          IHoroscopeModel.isOriental(moonDegree, sunDegree)
        }?.let {
          Debility.Moon_Decrease_Light
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Combust the Sun (between 17' and 8.5 from Sol).
   */
  val combustion = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return planet.takeIf { it !== SUN }
        ?.takeIf { h.getAngle(planet, SUN) > 17.0 / 60.0 && h.getAngle(planet, SUN) <= 8.5 }
        ?.let { Debility.Combustion(planet) }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Under the Sunbeams (between 8.5 and 17 from Sol).
   */
  val sunbeam = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return planet.takeIf { it !== SUN }
        ?.takeIf { h.getAngle(it, SUN) > 8.5 && h.getAngle(it, SUN) <= 17.0 }
        ?.let { Debility.Sunbeam(planet) }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val partileConjMarsSaturn = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return h.getPosition(planet)?.lng?.let { planetDeg ->
        val marsDeg = h.getPosition(MARS)?.lng
        val saturnDeg = h.getPosition(SATURN)?.lng

        val conjMars = marsDeg?.takeIf {
          planet !== MARS && IHoroscopeModel.getAngle(planetDeg, marsDeg) <= 1
        }?.let { Debility.Partile_Conj_Mars_Saturn(planet, MARS) }

        val conjSat = saturnDeg?.takeIf {
          planet != SATURN && IHoroscopeModel.getAngle(planetDeg, saturnDeg) <= 1
        }?.let { Debility.Partile_Conj_Mars_Saturn(planet, SATURN) }

        listOfNotNull(conjMars, conjSat)

      } ?: emptyList()
    }
  }


  /**
   * Partile conjunction with Dragon's Tail (Moon's South Node).
   * */
  val partileConjSouthNode = object : IPlanetPatternFactory {
    /** 內定採用 NodeType.MEAN  */
    var nodeType = NodeType.MEAN

    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return h.getPosition(planet)?.lng?.let { planetDeg ->
        val south = LunarNode.of(NorthSouth.SOUTH, nodeType)
        h.getPosition(south)?.lng?.takeIf { southDeg ->
          IHoroscopeModel.getAngle(planetDeg, southDeg) <= 1
        }?.let {
          Debility.Partile_Conj_South_Node(planet)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Besieged between Mars and Saturn.
   * 被火土夾制，只有日月水金，這四星有可能發生
   * 前一個角度與火土之一形成 0/90/180 , 後一個角度又與火土另一顆形成 0/90/180
   * 中間不能與其他行星形成角度
   */
  val besiegedMarsSaturn = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

      return planet.takeIf { arrayOf(SUN, MOON, MERCURY, VENUS).contains(it) }
        ?.takeIf {
          val gmt = TimeTools.getGmtFromLmt(h.lmt, h.location)
          //火土夾制，只考量「硬」角度 , 所以最後一個參數設成 true
          besiegedImpl.isBesieged(it, MARS, SATURN, gmt, classical = true, isOnlyHardAspects = true)
        }?.let {
          Debility.Besieged_Mars_Saturn(planet)
        }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * Partile opposite Mars or Saturn.
   */
  val partileOppoMarsSaturn = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getPosition(planet)?.lng?.let { planetDeg ->

        val oppoMars = h.getPosition(MARS)?.lng?.takeIf { marsDeg ->
          planet !== MARS && AspectEffectiveModern.isEffective(planetDeg, marsDeg, OPPOSITION, 1.0)
        }?.let {
          Debility.Partile_Oppo_Mars_Saturn(planet, MARS)
        }

        val oppoSat = h.getPosition(SATURN)?.lng?.takeIf { saturnDeg ->
          planet != SATURN && AspectEffectiveModern.isEffective(planetDeg, saturnDeg, OPPOSITION, 1.0)
        }?.let {
          Debility.Partile_Oppo_Mars_Saturn(planet, SATURN)
        }

        listOfNotNull(oppoMars, oppoSat)
      } ?: emptyList()
    }
  }

  /**
   * Partile square Mars or Saturn.
   * */
  val partileSquareMarsSaturn = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getPosition(planet)?.lng?.let { planetDeg ->

        val squareMars = h.getPosition(MARS)?.lng?.takeIf { marsDeg ->
          planet !== MARS && AspectEffectiveModern.isEffective(planetDeg, marsDeg, SQUARE, 1.0)
        }?.let {
          Debility.Partile_Square_Mars_Saturn(planet, MARS)
        }

        val squareSat = h.getPosition(SATURN)?.lng?.takeIf { saturnDeg ->
          planet != SATURN && AspectEffectiveModern.isEffective(planetDeg, saturnDeg, SQUARE, 1.0)
        }?.let {
          Debility.Partile_Square_Mars_Saturn(planet, SATURN)
        }

        listOfNotNull(squareMars, squareSat)
      } ?: emptyList()
    }
  }

  /**
   * Within 5 deg of Caput Algol at 26 deg 10' Taurus in January 2000.
   * */
  val conjAlgol = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      return h.getPosition(planet)?.lng?.let { planetDeg ->
        h.getPosition(FixedStar.ALGOL)?.lng?.takeIf { algolDeg ->
          AspectEffectiveModern.isEffective(planetDeg, algolDeg, CONJUNCTION, 5.0)
        }?.let { _ ->
          logger.debug("{} 與 {} 形成 {}", planet, FixedStar.ALGOL, CONJUNCTION)
          Debility.Conj_Algol(planet)
        }
      }
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  /**
   * 判斷不得時 (Out of sect) : 白天 , 夜星位於地平面上，落入陽性星座；或是晚上，晝星在地平面上，落入陰性星座
   * 晝星 : 日 , 木 , 土
   * 夜星 : 月 , 金 , 火
   *
   * 相對於不得時的，是「得時」 [hayz]
   */
  val outOfSect = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {

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
        ?.let { pattern -> listOf(pattern) } ?: emptyList()
    }
  }

  val refrainFromVenusJupiter = object : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: IHoroscopeModel): List<IPlanetPattern> {
      // 太陽 / 月亮不會逆行
      return planet.takeIf { it !== SUN && it !== MOON }?.let {

        val refrainFromVenus = planet.takeIf { it !== VENUS }?.let {
          refranationImpl.getImportantResult(h, planet, VENUS)
        }?.let { (_, aspect) ->
          logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, VENUS, aspect)
          Debility.Refrain_from_Venus_Jupiter(planet, VENUS, aspect)
        }

        val refrainFromJupiter = planet.takeIf { it !== JUPITER }?.let {
          refranationImpl.getImportantResult(h, planet, JUPITER)
        }?.let { (_, aspect) ->
          logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, JUPITER, aspect)
          Debility.Refrain_from_Venus_Jupiter(planet, JUPITER, aspect)
        }

        listOfNotNull(refrainFromVenus, refrainFromJupiter)
      } ?: emptyList()
    }
  }

  val essentialDignities: List<IPlanetPatternFactory> = listOf(ruler, exaltation, triplicity, term, face, beneficialMutualReception)

  val accidentalDignities: List<IPlanetPatternFactory> = listOf(house_1_10, house_4_7_11, house_2_5, house_9, house_3, direct, swift
    , orientalGood, occidentalGood, moonIncreaseLight, freeCombustion, cazimi, partileConjJupiterVenus
    , partileConjNorthNode, partileTrineJupiterVenus, partileSextileJupiterVenus, partileConjRegulus
    , partileConjSpica, joyHouse, hayz, besiegedJupiterVenus, translationOfLight, collectionOfLight, refrainFromMarsSaturn)

  val debilities: List<IPlanetPatternFactory> = listOf(detriment, fall, peregrine, house_12, house_6_8, retrograde, slower
    , occidentalBad, orientalBad, moonDecreaseLight, combustion, sunbeam, partileConjMarsSaturn, partileConjSouthNode
    , besiegedMarsSaturn, partileOppoMarsSaturn, partileSquareMarsSaturn, conjAlgol, outOfSect, refrainFromVenusJupiter)


  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
