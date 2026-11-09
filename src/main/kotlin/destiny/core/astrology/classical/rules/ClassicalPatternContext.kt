/**
 * Created by smallufo on 2019-10-02.
 */
package destiny.core.astrology.classical.rules

import kotlin.reflect.KClass
import destiny.core.DayNight
import destiny.core.DayNight.DAY
import destiny.core.DayNight.NIGHT
import destiny.core.News
import destiny.core.astrology.*
import destiny.core.astrology.Aspect.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.classical.*
import destiny.tools.KotlinLogging
import destiny.tools.getTitle
import java.util.*


class ClassicalPatternContext(private val rulerImpl: IRuler,
                              private val exaltImpl: IExaltation,
                              private val fallImpl: IFall,
                              private val detrimentImpl: IDetriment,
                              private val triplicityImpl: ITriplicity,
                              private val termImpl: ITerm,
                              private val faceImpl: IFace,
                              private val dayNightImpl: IDayNight,
                              private val besiegementImpl: IBesiegement,
                              private val translationOfLightImpl: ITranslationOfLight,
                              private val collectionOfLightImpl: ICollectionOfLight,
                              private val applicationOutcomeImpl: IApplicationOutcome,
                              /** 本命的居上（[overcome]）；null ＝ 這份 context 不判居上（卜卦：Lilly 一系不用這個技法） */
                              private val overcomingImpl: IOvercoming? = null,
                              /** 受剋與受扶（[maltreated]）；null ＝ 這份 context 不判（卜卦） */
                              private val maltreatmentImpl: IMaltreatment? = null,
                              /** 持矛者（[spearBearing]）；null ＝ 這份 context 不判（卜卦） */
                              private val doryphoriaImpl: IDoryphoria? = null,
                              /** 映點（[antiscion]）；null ＝ 這份 context 不判 */
                              private val antisciaImpl: IAntiscia? = null,
                              /** 不相見（[averse]）；null ＝ 這份 context 不判 */
                              private val aversionImpl: IAversion? = null,
                              /** 灼傷（[ICombustion]）：古典事實列不列由它決定（null ＝ 不列）；prompt 的日心／灼傷／光束下判斷一律用它，null 時用 [CombustionOptions.LILLY] */
                              private val combustionImpl: ICombustion? = null,
                              /** 區分與得時（[ISect]）：古典事實列不列由它決定（null ＝ 不列）；prompt 的得時／不得時（[hayz]、[outOfSect]）一律用它，null 時用 [HayzOptions.IN_SECT_ABOVE] */
                              private val sectImpl: ISect? = null,
                              /** 顯現（停滯、出入光束，[IPhasis]）：古典事實列不列由它決定（null ＝ 不列）；要星曆 */
                              private val phasisImpl: IPhasis? = null,
                              /** 野生（[IFeral]）；null ＝ 不列 */
                              private val feralImpl: IFeral? = null,
                              /** 月亮的離與合（[ILunarConnection]）；null ＝ 不列 */
                              private val lunarConnectionImpl: ILunarConnection? = null,
                              /** 燃燒路（[IViaCombusta]）；null ＝ 不列 */
                              private val viaCombustaImpl: IViaCombusta? = null,
                              /** 本命居上收哪幾種（[natalOvercomings]）；只在有 [overcomingImpl] 時有作用 */
                              private val natalOvercomingOptions: NatalOvercomingOptions = NatalOvercomingOptions.DEFAULT,
) {

  /**
   * 全盤的古典事實（判斷層之前的那一層）：光線傳遞、光線收集、受阻的入相位（臨陣退縮／插隊／撲空）、包夾、本命的居上（[natalOvercomings]）、
   * 不相見、受剋與受扶、持矛者、映點、灼傷、區分與得時、顯現、野生、月亮的離與合、燃燒路（後十種只在這份 context 有對應的 Impl 時）。
   * 判準與本 context 的格局相同（同一組 Impl、同一份 [OutcomeOptions]）。成形的入相位不列 —— 那就是一般的入相位
   */
  fun classicalFacts(h: IHoroscopeModel): List<ClassicalFact> {
    // 沒有來源看日夜時不現算（呼叫端有給就照傳）
    val sect = if (factSources.any { it.usesSect }) sectOf(h) else (h as? ClassicalInput)?.sect
    return factSources.flatMap { it.getFacts(h, sect) }
  }

  /** 本命看的居上（[natalOvercomings]）當成一個來源；沒有 [overcomingImpl] 的 context 不列 */
  private val natalOvercomingSource = object : IClassicalFactSource {
    override fun getFacts(h: IHoroscopeModel, sect: DayNight?): List<ClassicalFact> = natalOvercomings(h).map { it.overcoming }
    override val produces: Set<KClass<out ClassicalFact>> get() = setOf(Overcoming::class)
  }

  /** 這份 context 會列出的事實型別（各來源 [IClassicalFactSource.produces] 的聯集）—— 涵蓋檢查用 */
  val factTypes: Set<KClass<out ClassicalFact>> by lazy { factSources.flatMapTo(LinkedHashSet()) { it.produces } }

  /**
   * 古典事實的來源（[IClassicalFactSource]），依列出的順序攤平；這份 context 沒有的技法（null）不列。
   * 新增一種技法：實作介面、加進這一串 —— [classicalFacts] 不必改
   */
  private val factSources: List<IClassicalFactSource> = listOfNotNull(
    translationOfLightImpl,
    collectionOfLightImpl,
    applicationOutcomeImpl,
    besiegementImpl,
    natalOvercomingSource.takeIf { overcomingImpl != null },
    aversionImpl,
    maltreatmentImpl,
    doryphoriaImpl,
    antisciaImpl,
    combustionImpl,
    sectImpl,
    phasisImpl,
    feralImpl,
    lunarConnectionImpl,
    viaCombustaImpl,
  )

  /** 盤的日夜：呼叫端給的（[ClassicalInput.sect]）優先，否則以盤面時刻現算 */
  private fun sectOf(h: IHoroscopeModel): DayNight = (h as? ClassicalInput)?.sect ?: dayNightImpl.getDayNight(h.lmt, h.location)

  /** 不相見（[IAversion]）；這份 context 沒有 [aversionImpl] 時為空 */
  fun aversions(h: IHoroscopeModel): List<Aversion> = aversionImpl?.getResults(h).orEmpty()

  /**
   * 本命看的居上（基準由 [overcomingImpl] 決定，本命用星座版 [OvercomingSignImpl]）：
   * 只收 [natalOvercomingOptions] 列的那幾種（預設凶星四分、吉星六分／四分／三分居上日、月、命主星，見 [NatalOvercomingOptions]）
   */
  fun natalOvercomings(h: IHoroscopeModel): List<NatalOvercoming> {
    val impl = overcomingImpl ?: return emptyList()
    val opts = natalOvercomingOptions
    val ascRuler = ascendantRuler(h)
    val targets = listOfNotNull(
      SUN.takeIf { opts.lights }, MOON.takeIf { opts.lights }, ascRuler.takeIf { opts.ascendantRuler }
    ).distinct()
    return targets.flatMap { target ->
      val targetDeg = h.positionMap[target]?.lngDeg ?: return@flatMap emptyList()
      opts.superiors.mapNotNull { (sup, aspects) ->
        if (sup === target) return@mapNotNull null
        val supDeg = h.positionMap[sup]?.lngDeg ?: return@mapNotNull null
        impl.getOvercoming(sup, supDeg, target, targetDeg)
          ?.takeIf { it.superior === sup && it.aspect in aspects }
          ?.let { NatalOvercoming(target, it, ascRuler = target === ascRuler) }
      }
    }
  }

  /** 上升星座的主星（托勒密守護）；盤沒有宮位時為 null */
  private fun ascendantRuler(h: IHoroscopeModel): Planet? = h.ascendantRuler(rulerImpl)

  /** [natalOvercomings] 的一筆：[target] 被居上；[ascRuler] ＝ [target] 是命主星 */
  data class NatalOvercoming(val target: Planet, val overcoming: Overcoming, val ascRuler: Boolean)

  /** 本命／卜卦的 prompt 預設帶的古典格局；居上（[overcome]）只在有 [overcomingImpl] 的 context（本命）才列 */
  val defaultFactories: List<IPlanetPatternFactory> by lazy {
    listOfNotNull(
      beneficialMutualReception,
      hayz,
      translationOfLight,
      collectionOfLight,
      conjAlgol,
      outOfSect,
      // 臨陣退縮、包夾送一般化的 Misc 句子（不限金木／火土的組合，帶細節）；
      // 只看金木／火土的計分判斷（besiegedJupiterVenus、besiegedMarsSaturn、refrainFromVenusJupiter）不再列入，否則同一件事出現兩行
      refrained,
      besieged,
      prohibited,
      frustrated,
      // 離太陽多近、在太陽哪一側：不看宮位，只知日期也大多判得出來（日輪內例外，見 DtoFactory.getClassicalPatterns）
      cazimi,
      combustion,
      sunbeam,
      orientalGood,
      occidentalGood,
      orientalBad,
      occidentalBad,
      overcome.takeIf { overcomingImpl != null },
      averse.takeIf { aversionImpl != null },
      maltreated.takeIf { maltreatmentImpl != null },
      spearBearing.takeIf { doryphoriaImpl != null },
      antiscion.takeIf { antisciaImpl != null },
      phasis.takeIf { phasisImpl != null },
      feral.takeIf { feralImpl != null },
      lunarConnection.takeIf { lunarConnectionImpl != null },
      viaCombusta.takeIf { viaCombustaImpl != null },
    )
  }

  private val essentialImpl: IEssential =
    EssentialImpl(rulerImpl, exaltImpl, fallImpl, detrimentImpl, triplicityImpl, termImpl, faceImpl,
      dayNightImpl)

  /**
   * ====================================== for [EssentialDignity] ======================================
   */

  /**
   * A planet in its own sign
   */
  val ruler = IPlanetPatternFactory.single { planet, h ->
    h.getZodiacSign(planet)?.takeIf { sign ->
      (planet === with(rulerImpl) { sign.getRulerPoint() })
    }?.let { sign ->
      logger.debug("{} 位於 {} , 為其 {}", planet, sign, Dignity.RULER)
      EssentialDignity.Ruler(planet, sign)
    }
  }

  /** A planet in its exaltation */
  val exaltation = IPlanetPatternFactory.single { planet, h ->
    h.getZodiacSign(planet)?.takeIf { sign ->
      (planet === with(exaltImpl) { sign.getExaltPoint() })
    }?.let { sign ->
      logger.debug("{} 位於其 {} 的星座 {}", planet, Dignity.EXALTATION, sign)
      EssentialDignity.Exaltation(planet, sign)
    }
  }

  /**
   * A planet in its own day or night triplicity (not to be confused with the modern triplicities).
   */
  val triplicity = IPlanetPatternFactory.single { planet, h ->
    val dayNight = sectOf(h)
    h.getZodiacSign(planet)?.takeIf { sign ->
      with(triplicityImpl) {
        (dayNight == DAY && planet === sign.getTriplicityPoint(DAY) ||
          dayNight == NIGHT && planet === sign.getTriplicityPoint(NIGHT))
      }
    }?.let { sign ->
      logger.debug("{} 位於 {} 為其 {} 之 Triplicity", planet, sign, dayNight)
      EssentialDignity.Triplicity(planet, sign, dayNight)
    }
  }

  /**
   * A planet in itw own term.
   * */
  val term = IPlanetPatternFactory.single { planet, h ->
    h.getPosition(planet)?.lngDeg?.takeIf { lngDeg ->
      (planet === termImpl.getPoint(lngDeg))
    }?.let { lngDeg ->
      EssentialDignity.Term(planet, lngDeg)
    }
  }

  /**
   * A planet in its own Chaldean decanate or face.
   * */
  val face = IPlanetPatternFactory.single { planet, h ->
    h.getPosition(planet)?.lngDeg?.takeIf { lngDeg ->
      (planet === faceImpl.getPoint(lngDeg))
    }?.let { lngDeg ->
      EssentialDignity.Face(planet, lngDeg)
    }
  }

  /**
   * RR / EE / RE 互容
   */
  val beneficialMutualReception = IPlanetPatternFactory { planet, h ->
    with(essentialImpl) {
      planet.getMutualData(h.pointDegreeMap , null , setOf(Dignity.RULER , Dignity.EXALTATION))
        .map { mutualData ->
          val sign1 = h.getZodiacSign(planet)!!
          val p2 = mutualData.getAnotherPoint(planet)
          val sign2 = h.getZodiacSign(p2)!!
          logger.debug("mutualData = {}", mutualData)
          logger.debug("{} 位於 {} , 與其 {}({}) 飛至 {} . 而 {} 的 {}({}) 飛至 {} , 形成 互容",
            planet, sign1, mutualData.getDignityOf(p2), p2, sign2, sign2, mutualData.getDignityOf(planet),
            planet, sign1)
          EssentialDignity.MutualReception(
            planet, sign1, mutualData.getDignityOf(planet),
            p2, sign2, mutualData.getDignityOf(p2)
          )
        }.toList()
    }


//      val rulerMutual = with((essentialImpl)) {
//        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.RULER)).firstOrNull()?.let { mutualData ->
//          val p2 = mutualData.getAnotherPoint(planet)
//          val sign1 = h.getZodiacSign(planet)!!
//          val sign2 = h.getZodiacSign(p2)!!
//          EssentialDignity.MutualReception(planet, sign1, mutualData.getDignityOf(planet),
//            p2, sign2, mutualData.getDignityOf(p2))
//        }
//      }
//
//      val exaltMutual = with(essentialImpl) {
//        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.EXALTATION)).firstOrNull()?.let { mutualData ->
//          val sign1 = h.getZodiacSign(planet)!!
//          val p2 = mutualData.getAnotherPoint(planet)
//          val sign2 = h.getZodiacSign(p2)!!
//          EssentialDignity.MutualReception(planet, sign1, mutualData.getDignityOf(planet),
//            p2, sign2, mutualData.getDignityOf(p2))
//        }
//      }
//
//      val mixedMutual = with(essentialImpl) {
//        planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.RULER, Dignity.EXALTATION)).firstOrNull()
//          ?.takeIf { mutualData ->
//            val p2 = mutualData.getAnotherPoint(planet)
//            val dig1 = mutualData.getDignityOf(planet)
//            val dig2 = mutualData.getDignityOf(p2)
//            dig1 !== dig2
//          }
//          ?.let { mutualData ->
//            val sign1 = h.getZodiacSign(planet)!!
//            val p2 = mutualData.getAnotherPoint(planet)
//            val sign2 = h.getZodiacSign(p2)!!
//            logger.debug("mutualData = {}", mutualData)
//            logger.debug("{} 位於 {} , 與其 {}({}) 飛至 {} . 而 {} 的 {}({}) 飛至 {} , 形成 RULER/EXALT 互容",
//              planet, sign1, mutualData.getDignityOf(p2), p2, sign2, sign2, mutualData.getDignityOf(planet),
//              planet, sign1)
//            EssentialDignity.MutualReception(planet, sign1, mutualData.getDignityOf(planet),
//              p2, sign2, mutualData.getDignityOf(p2))
//          }
//      }
//
//      return listOfNotNull(rulerMutual, exaltMutual, mixedMutual)
  } // beneficialMutualReception

  /**
   * ====================================== for [AccidentalDignity] ======================================
   */

  val house_1_10 = IPlanetPatternFactory.single { planet, h ->
    h.getHouse(planet)
      ?.takeIf { it == 1 || it == 10 }
      ?.let { house ->
        AccidentalDignity.House_1_10(planet, house)
      }
  }

  val house_4_7_11 = IPlanetPatternFactory.single { planet, h ->
    h.getHouse(planet)
      ?.takeIf { intArrayOf(4, 7, 11).contains(it) }
      ?.let { house ->
        AccidentalDignity.House_4_7_11(planet, house)
      }
  }

  val house_2_5 = IPlanetPatternFactory.single { planet, h ->
    h.getHouse(planet)
      ?.takeIf { it == 2 || it == 5 }
      ?.let { house ->
        AccidentalDignity.House_2_5(planet, house)
      }
  }

  val house_9 = IPlanetPatternFactory.single { planet, h ->
    h.getHouse(planet)
      ?.takeIf { it == 9 }
      ?.let {
        AccidentalDignity.House_9(planet)
      }
  }


  val house_3 = IPlanetPatternFactory.single { planet, h ->
    h.getHouse(planet)
      ?.takeIf { it == 3 }
      ?.let {
        AccidentalDignity.House_3(planet)
      }
  }

  /**
   * Direct in motion (does not apply to Sun and Moon).
   */
  val direct = IPlanetPatternFactory.single { planet, h ->
    planet.takeIf { it !== SUN && it !== MOON }
      ?.let { h.getStarPosition(it) }
      ?.speedLng
      ?.takeIf { it > 0 }
      ?.let {
        AccidentalDignity.Direct(planet)
      }
  }

  /**
   * Swift in motion (faster than average).
   * */
  val swift = IPlanetPatternFactory.single { planet, h ->
    AverageDailyMotionMap.getAvgDailySpeed(planet)?.takeIf { dailyDeg ->
      h.getStarPosition(planet)
        ?.speedLng?.let { speedLng ->
        speedLng > dailyDeg
      } ?: false
    }?.let {
      AccidentalDignity.Swift(planet)
    }
  }

  /**
   * Mars, Jupiter, or Saturn oriental of (rising before) the Sun.
   * 火星、木星、土星 是否 東出 於 太陽
   */
  val orientalGood = IPlanetPatternFactory.single { planet, h ->
    arrayOf(MARS, JUPITER, SATURN)
      .takeIf { it.contains(planet) }
      ?.let { h.getPosition(planet) }?.lngDeg
      ?.let { planetDegree ->
        h.getPosition(SUN)?.lngDeg?.takeIf { sunDegree ->
          planetDegree.isOriental(sunDegree)
        }
      }?.let {
        AccidentalDignity.Oriental(planet)
      }
  }

  /**
   * Mercury, or Venus occidental of (rising after) the Sun.
   * */
  val occidentalGood = IPlanetPatternFactory.single { planet, h ->
    planet.takeIf { it === MERCURY || it === VENUS }
      ?.let { h.getPosition(it) }?.lngDeg?.let { planetDeg ->
      h.getPosition(SUN)?.lngDeg?.takeIf { sunDeg ->
        planetDeg.isOccidental(sunDeg)
      }?.let {
        AccidentalDignity.Occidental(planet)
      }
    }
  }

  /**
   * Moon increasing in light (月增光/上弦月) , or occidental of the Sun.
   * */
  val moonIncreaseLight = IPlanetPatternFactory.single { planet, h ->
    planet.takeIf { it === MOON }?.let { h.getPosition(it) }?.lngDeg?.let { moonDeg ->
        h.getPosition(SUN)?.lngDeg?.takeIf { sunDeg ->
          moonDeg.isOccidental(sunDeg)
        }?.let {
          AccidentalDignity.Moon_Increase_Light
        }
      }
  }

  /**
   * Free from combustion and the Sun's rays. 只要脫離了太陽左右 17度，就算 Free Combustion !?
   * TODO : refine the definition , it is too broad
   */
  val freeCombustion = IPlanetPatternFactory.single { planet, h ->
    planet.takeIf { it !== SUN }
      ?.takeIf { h.getAngle(it, SUN) > 17 }
      ?.let {
        AccidentalDignity.Free_Combustion(planet)
      }
  }

  /** 日心／灼傷／光束下的判法（prompt 的三個判斷共用；門檻只在 [CombustionOptions]） */
  private val combustionJudge: ICombustion = combustionImpl ?: CombustionImpl(CombustionOptions.LILLY)

  /**
   * Cazimi（日心，預設 17 角分內，見 [CombustionOptions]）
   * */
  val cazimi = IPlanetPatternFactory.single { planet, h ->
    combustionJudge.getCombustion(h, planet)?.takeIf { it.zone == Combustion.Zone.CAZIMI }?.let { AccidentalDignity.Cazimi(planet) }
  }

  /**
   * Partile conjunction with Jupiter or Venus.
   * 和金星或木星合相，交角 1 度內
   * */
  val partileConjJupiterVenus = IPlanetPatternFactory.single { planet, h ->
    val planetDeg = h.getPosition(planet)?.lngDeg
    val jupiterDeg = h.getPosition(JUPITER)?.lngDeg
    val venusDeg = h.getPosition(VENUS)?.lngDeg

    planetDeg?.let {

      jupiterDeg
        ?.takeIf { planet !== JUPITER && planetDeg.getAngle(jupiterDeg) <= 1 }
        ?.let { AccidentalDignity.Partile_Conj_Jupiter_Venus(planet, JUPITER) }
        ?: venusDeg?.takeIf { planet !== VENUS && planetDeg.getAngle(venusDeg) <= 1 }
          ?.let { AccidentalDignity.Partile_Conj_Jupiter_Venus(planet, VENUS) }


    }
  }

  /**
   * Partile aspect with Dragon's Head (Moon's North Node).
   */
  /** 內定採用 [NodeType.MEAN] */
  private val northNode: LunarNode = LunarNode.of(News.NorthSouth.NORTH)

  val partileConjNorthNode = IPlanetPatternFactory.single { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->
      h.getPosition(northNode)?.lngDeg?.takeIf { northDeg ->
        planetDeg.getAngle(northDeg) <= 1
      }?.let {
        logger.debug("{} 與 {} 形成 {}", planet, northNode, CONJUNCTION)
        AccidentalDignity.Partile_Conj_North_Node(planet, northNode)
      }
    }
  }

  /**
   * Partile trine Jupiter or Venus.
   */
  val partileTrineJupiterVenus = IPlanetPatternFactory { planet, h ->
    val planetDeg = h.getPosition(planet)?.lngDeg
    val jupiterDeg = h.getPosition(JUPITER)?.lngDeg
    val venusDeg = h.getPosition(VENUS)?.lngDeg


    planetDeg?.let {

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

  /**
   * Partile aspect Jupiter or Venus.
   */
  val partileSextileJupiterVenus = IPlanetPatternFactory { planet, h ->
    val planetDeg = h.getPosition(planet)?.lngDeg
    val jupiterDeg = h.getPosition(JUPITER)?.lngDeg
    val venusDeg = h.getPosition(VENUS)?.lngDeg

    planetDeg?.let {

      val sextileJupiter = jupiterDeg
        ?.takeIf { planet !== JUPITER && AspectEffectiveModern.isEffective(planetDeg, jupiterDeg, SEXTILE, 1.0) }
        ?.let { AccidentalDignity.Partile_Sextile_Jupiter_Venus(planet, JUPITER) }

      val sextileVenus = venusDeg
        ?.takeIf { planet !== VENUS && AspectEffectiveModern.isEffective(planetDeg, venusDeg, SEXTILE, 1.0) }
        ?.let { AccidentalDignity.Partile_Sextile_Jupiter_Venus(planet, VENUS) }

      listOfNotNull(sextileJupiter, sextileVenus)
    } ?: emptyList()
  }

  /**
   * Partile conjunct Cor Leonis (Regulus) at 29deg50' Leo in January 2000.
   * */
  val partileConjRegulus = IPlanetPatternFactory.single { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->
      h.getPosition(FixedStar.REGULUS)?.lngDeg?.takeIf { regulusDeg ->
        AspectEffectiveModern.isEffective(planetDeg, regulusDeg, CONJUNCTION, 1.0)
      }?.let {
        AccidentalDignity.Partile_Conj_Regulus(planet)
      }
    }
  }

  /**
   * Partile conjunct Spica at 23deg50' Libra in January 2000.
   * */
  val partileConjSpica = IPlanetPatternFactory.single { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->
      h.getPosition(FixedStar.SPICA)?.lngDeg?.takeIf { spicaDeg ->
        AspectEffectiveModern.isEffective(planetDeg, spicaDeg, CONJUNCTION, 1.0)
      }?.let {
        logger.debug("{} 與 {} 形成 {}", planet, FixedStar.SPICA, CONJUNCTION)
        AccidentalDignity.Partile_Conj_Spica(planet)
      }
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
  val joyHouse = IPlanetPatternFactory.single { planet, h ->
    h.getHouse(planet)?.takeIf { house ->
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
  }

  /** 得時／不得時的判法（prompt 的兩個判斷與古典事實共用；判準只在 [HayzOptions]）。沒有 [sectImpl] 的 context（卜卦）沿用舊判法 */
  private val sectJudge: ISect = sectImpl ?: SectImpl(HayzOptions.IN_SECT_ABOVE)

  /**
   * 得時（Hayz）：晝夜歸屬、地平線上下、星座陰陽三者都合宜，判準見 [sectJudge]。與 [classicalFacts] 列的得時是同一筆判定。
   *
   * 相對於得時的，是「不得時」 [outOfSect]
   */
  val hayz = IPlanetPatternFactory.single { planet, h ->
    sectJudge.getHayz(h, sectOf(h), planet)?.let { AccidentalDignity.Hayz(it) }
  }

  /**
   * 夾輔 : 被金星木星包夾 , 是很幸運的情形
   * 星體或 60/90/120/180 的光線，兩側各在 7° 內，中間沒有別的星介入（[BesiegementPorphyryImpl]）
   */
  val besiegedJupiterVenus = IPlanetPatternFactory.single { planet, h ->
    planet.takeIf { arrayOf(SUN, MOON, MERCURY, MARS, SATURN).contains(it) }?.takeIf {
      besiegementImpl.getBesiegement(h, it, setOf(VENUS, JUPITER)) != null
    }?.let {
      AccidentalDignity.Besieged_Jupiter_Venus(planet)
    }
  }

  val translationOfLight = IPlanetPatternFactory.single { planet, h ->
    translationOfLightImpl.getResult(planet, h)
      ?.let { t ->
        val deg = h.getAngle(t.from, t.to)
        // {0} 從 {1} 傳遞光線到 {2} ，{1} 與 {2} 交角 {3} 度，相位為 {4} 。 (入相位 or 出相位)
        // {0} 從 {1} 傳遞光線到 {2} ，{1} 與 {2} 交角 {3} 度，未形成相位
        AccidentalDignity.Translation_of_Light(planet, t.from, t.to, deg, t.between?.aspectType)
      }
  }

  /**
   * 目前只將「收集好光 (DIGNITIES) 」視為 Collection of Light ，而「蒐集穢光 (DEBILITIES) 」不納入考慮
   */
  val collectionOfLight = IPlanetPatternFactory.single { planet, h ->
    collectionOfLightImpl.getResult(planet, h, receivedOnly = true)?.let { c ->
      // {0} 從 {1} 與 {2} 收集光線。 {1} 與 {2} 交角 {3} 度。
      AccidentalDignity.Collection_of_Light(planet, c.planets, h.getAngle(c.planets[0], c.planets[1]))
    }
  }

  /** [planet] 與 [other] 的入相位，結局是臨陣退縮時才回傳（不論退縮者是哪一顆）。讀 [ClassicalInput] 共用的引擎結果，不另跑引擎 */
  private fun refranation(h: ClassicalInput, planet: Planet, other: Planet): ApplicationOutcome.Refranation? =
    h.outcomeOf(applicationOutcomeImpl, planet, other) as? ApplicationOutcome.Refranation

  /** [planet] 是追的那顆（applying）的結局中，型別為 [T] 的那些 */
  private inline fun <reified T : ApplicationOutcome> ClassicalInput.outcomesApplying(planet: Planet): List<T> =
    outcomes(applicationOutcomeImpl).filterIsInstance<T>().filter { it.applying === planet }

  /**
   * 在與火星或土星形成交角之前，臨陣退縮，代表避免厄運
   */
  val refrainFromMarsSaturn = IPlanetPatternFactory.single { planet, h ->
    planet.takeIf { it !== MOON && it !== SUN }
      ?.let {
        planet.takeIf { it !== MARS }?.let {
          refranation(h, planet, MARS)?.let { r ->
            val aspect = r.pattern.aspect
            logger.debug("{} 逃過了與 {} 形成 {} (Refranation)", planet, MARS, aspect)
            AccidentalDignity.Refrain_from_Mars_Saturn(planet, MARS, aspect)
          }
        } ?: planet.takeIf { it !== SATURN }?.let {
          refranation(h, planet, SATURN)?.let { r ->
            val aspect = r.pattern.aspect
            logger.debug("{} 逃過了與 {} 形成 {} (Refranation)", planet, SATURN, aspect)
            AccidentalDignity.Refrain_from_Mars_Saturn(planet, SATURN, aspect)
          }
        }
      }
  }

  /**
   * ====================================== for [Debility] ======================================
   * */

  /** In Detriment */
  val detriment = IPlanetPatternFactory.single { planet, h ->
    h.getZodiacSign(planet)?.let { sign ->
      sign.takeIf { planet === with(detrimentImpl) { sign.getDetrimentPoint() } }
        ?.let { Debility.Detriment(planet, sign) }
    }
  }

  /** In Fall */
  val fall = IPlanetPatternFactory.single { planet, h ->
    h.getZodiacSign(planet)?.let { sign ->
      sign.takeIf { planet === with(fallImpl) { sign.getFallPoint() } }
        ?.let { Debility.Fall(planet, sign) }
    }
  }

  /**
   * Peregrine : 漂泊、茫游、外出狀態
   */
  val peregrine = IPlanetPatternFactory.single { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->
      val dayNight = sectOf(h)
      h.getZodiacSign(planet)?.takeIf { sign ->

        planet !== with(rulerImpl) { sign.getRulerPoint() } &&
          planet !== with(exaltImpl) { sign.getExaltPoint() } &&
          planet !== with(detrimentImpl) { sign.getDetrimentPoint() } &&
          planet !== with(fallImpl) { sign.getFallPoint() } &&
          planet !== termImpl.getPoint(planetDeg) &&  // 絕對度數：不可用 sign.getTermPoint（收星座內度數）
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
  }

  val house_12 = IPlanetPatternFactory.single { planet, h ->
    h.getHouse(planet)
      ?.takeIf { it == 12 }
      ?.let { Debility.House_12(planet) }
  }

  val house_6_8 = IPlanetPatternFactory.single { planet, h ->
    h.getHouse(planet)
      ?.takeIf { it == 6 || it == 8 }
      ?.let { Debility.House_6_8(planet, it) }
  }

  val retrograde = IPlanetPatternFactory.single { planet, h ->
    h.getStarPosition(planet)
      ?.speedLng
      ?.takeIf { it < 0 }
      ?.let { Debility.Retrograde(planet) }
  }

  val slower = IPlanetPatternFactory.single { planet, h ->
    AverageDailyMotionMap.getAvgDailySpeed(planet)
      ?.takeIf { dailyDeg ->
        h.getStarPosition(planet)
          ?.speedLng?.let { speedLng ->
          speedLng < dailyDeg
        } ?: false
      }?.let { Debility.Slower(planet) }
  }

  /**
   * 火星、木星、或土星，在太陽西方
   */
  val occidentalBad = IPlanetPatternFactory.single { planet, h ->
    arrayOf(MARS, JUPITER, SATURN)
      .takeIf { it.contains(planet) }
      ?.let { h.getPosition(planet) }?.lngDeg?.let { planetDegree ->
      h.getPosition(SUN)?.lngDeg?.takeIf { sunDegree ->
        planetDegree.isOccidental(sunDegree)
      }?.let {
        Debility.Occidental(planet)
      }
    }
  }

  /**
   * Mercury, or Venus oriental to the Sun.
   * 金星、水星，是否 東出 於 太陽
   *
   */
  val orientalBad = IPlanetPatternFactory.single { planet, h ->
    arrayOf(MERCURY, VENUS)
      .takeIf { it.contains(planet) }
      ?.let { h.getPosition(planet) }?.lngDeg?.let { planetDegree ->
      h.getPosition(SUN)?.lngDeg?.takeIf { sunDegree ->
        planetDegree.isOriental(sunDegree)
      }?.let {
        Debility.Oriental(planet)
      }
    }
  }

  /**
   * Moon decreasing in light.
   * */
  val moonDecreaseLight = IPlanetPatternFactory.single { planet, h ->
    planet
      .takeIf { it === MOON }
      ?.let { h.getPosition(it) }?.lngDeg?.let { moonDegree ->
      h.getPosition(SUN)?.lngDeg?.takeIf { sunDegree ->
        moonDegree.isOriental(sunDegree)
      }?.let {
        Debility.Moon_Decrease_Light
      }
    }
  }

  /**
   * Combust the Sun（灼傷，預設 17 角分到 8.5° 之間，見 [CombustionOptions]）
   */
  val combustion = IPlanetPatternFactory.single { planet, h ->
    combustionJudge.getCombustion(h, planet)?.takeIf { it.zone == Combustion.Zone.COMBUST }?.let { Debility.Combustion(planet) }
  }

  /**
   * Under the Sunbeams（光束下，預設 8.5° 到 17° 之間，見 [CombustionOptions]）
   */
  val sunbeam = IPlanetPatternFactory.single { planet, h ->
    combustionJudge.getCombustion(h, planet)?.takeIf { it.zone == Combustion.Zone.UNDER_BEAMS }?.let { Debility.Sunbeam(planet) }
  }

  val partileConjMarsSaturn = IPlanetPatternFactory { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->
      val marsDeg = h.getPosition(MARS)?.lngDeg
      val saturnDeg = h.getPosition(SATURN)?.lngDeg

      val conjMars = marsDeg?.takeIf {
        planet !== MARS && planetDeg.getAngle(marsDeg) <= 1
      }?.let { Debility.Partile_Conj_Mars_Saturn(planet, MARS) }

      val conjSat = saturnDeg?.takeIf {
        planet != SATURN && planetDeg.getAngle(saturnDeg) <= 1
      }?.let { Debility.Partile_Conj_Mars_Saturn(planet, SATURN) }

      listOfNotNull(conjMars, conjSat)

    } ?: emptyList()
  }


  /**
   * Partile conjunction with Dragon's Tail (Moon's South Node).
   * */
  /** 內定採用 [NodeType.MEAN] */
  private val southNode: LunarNode = LunarNode.of(News.NorthSouth.SOUTH)

  val partileConjSouthNode = IPlanetPatternFactory.single { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->
      h.getPosition(southNode)?.lngDeg?.takeIf { southDeg ->
        planetDeg.getAngle(southDeg) <= 1
      }?.let {
        logger.debug("{} 與 {} 形成 {}", planet, southNode, CONJUNCTION)
        Debility.Partile_Conj_South_Node(planet)
      }
    }
  }

  /**
   * Besieged between Mars and Saturn.
   * 被火土夾制，只有日月水金，這四星有可能發生
   * 星體或 90/180 的光線，兩側各在 7° 內，中間沒有別的星介入（[BesiegementPorphyryImpl]）
   */
  val besiegedMarsSaturn = IPlanetPatternFactory.single { planet, h ->
    planet.takeIf { arrayOf(SUN, MOON, MERCURY, VENUS).contains(it) }
      ?.takeIf {
        //火土夾制，光線只考量「硬」角度（BesiegementOptions.maleficRays）
        besiegementImpl.getBesiegement(h, it, setOf(MARS, SATURN)) != null
      }?.let {
        Debility.Besieged_Mars_Saturn(planet)
      }
  }

  /**
   * Partile opposite Mars or Saturn.
   */
  val partileOppoMarsSaturn = IPlanetPatternFactory { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->

      val oppoMars = h.getPosition(MARS)?.lngDeg?.takeIf { marsDeg ->
        planet !== MARS && AspectEffectiveModern.isEffective(planetDeg, marsDeg, OPPOSITION, 1.0)
      }?.let {
        Debility.Partile_Oppo_Mars_Saturn(planet, MARS)
      }

      val oppoSat = h.getPosition(SATURN)?.lngDeg?.takeIf { saturnDeg ->
        planet != SATURN && AspectEffectiveModern.isEffective(planetDeg, saturnDeg, OPPOSITION, 1.0)
      }?.let {
        Debility.Partile_Oppo_Mars_Saturn(planet, SATURN)
      }

      listOfNotNull(oppoMars, oppoSat)
    } ?: emptyList()
  }

  /**
   * Partile square Mars or Saturn.
   * */
  val partileSquareMarsSaturn = IPlanetPatternFactory { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->

      val squareMars = h.getPosition(MARS)?.lngDeg?.takeIf { marsDeg ->
        planet !== MARS && AspectEffectiveModern.isEffective(planetDeg, marsDeg, SQUARE, 1.0)
      }?.let {
        Debility.Partile_Square_Mars_Saturn(planet, MARS)
      }

      val squareSat = h.getPosition(SATURN)?.lngDeg?.takeIf { saturnDeg ->
        planet != SATURN && AspectEffectiveModern.isEffective(planetDeg, saturnDeg, SQUARE, 1.0)
      }?.let {
        Debility.Partile_Square_Mars_Saturn(planet, SATURN)
      }

      listOfNotNull(squareMars, squareSat)
    } ?: emptyList()
  }

  /**
   * Within 5 deg of Caput Algol at 26 deg 10' Taurus in January 2000.
   * */
  val conjAlgol = IPlanetPatternFactory.single { planet, h ->
    h.getPosition(planet)?.lngDeg?.let { planetDeg ->
      h.getPosition(FixedStar.ALGOL)?.lngDeg?.takeIf { algolDeg ->
        AspectEffectiveModern.isEffective(planetDeg, algolDeg, CONJUNCTION, 5.0)
      }?.let {
        logger.debug("{} 與 {} 形成 {}", planet, FixedStar.ALGOL, CONJUNCTION)
        Debility.Conj_Algol(planet)
      }
    }
  }

  /**
   * 不得時（Out of sect）：與盤異隊、在地平上、星座陰陽不合，三者皆是；判準與 [hayz] 同一份（[sectJudge]）
   */
  val outOfSect = IPlanetPatternFactory.single { planet, h ->
    sectJudge.getOutOfSect(h, sectOf(h), planet)?.let { Debility.Out_of_Sect(it) }
  }

  val refrainFromVenusJupiter = IPlanetPatternFactory { planet, h ->
    // 太陽 / 月亮不會逆行
    planet.takeIf { it !== SUN && it !== MOON }?.let {

      val refrainFromVenus = planet.takeIf { it !== VENUS }?.let {
        refranation(h, planet, VENUS)
      }?.let { r ->
        val aspect = r.pattern.aspect
        logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, VENUS, aspect)
        Debility.Refrain_from_Venus_Jupiter(planet, VENUS, aspect)
      }

      val refrainFromJupiter = planet.takeIf { it !== JUPITER }?.let {
        refranation(h, planet, JUPITER)
      }?.let { r ->
        val aspect = r.pattern.aspect
        logger.debug("{} 在與 {} 形成 {} 之前，臨陣退縮 (Refranation)", planet, JUPITER, aspect)
        Debility.Refrain_from_Venus_Jupiter(planet, JUPITER, aspect)
      }

      listOfNotNull(refrainFromVenus, refrainFromJupiter)
    } ?: emptyList()
  }

  /**
   * Reception by Debility
   * DD / FF / DF 互陷
   * 兩顆行星互相進入對方的失勢之地（Detriment/Fall）
   *
   * ⚠️ 2026-10-31 評估：傳統占星找不到依據，**不列入 [debilities]**，程式保留供參考。
   * - 接納（reception）的前提是「主人」：客人落在主人有 dignity（廟、旺、三分性、界、面）的地方，主人才對客人負責。
   *   A 落在 B 落陷／入弱的星座，只代表 B 在那裡很弱 —— 那裡不是 B 的地盤，B 根本不是 A 的主人。
   *   例：月亮在牡羊，主人是火星（廟）與太陽（旺）；土星在牡羊入弱，與月亮無關。兩邊都不接納，也就談不上「互」。
   * - Lilly 的 dignity／debility 計分表裡沒有這一項；所知的傳統文獻也沒有「以 debility 互相接納」的技法。
   *   與已拿掉的「以落陷入弱接納」（光線收集的 DEBILITIES）是同一個問題。
   * - 象徵上可以聯想成「各自待在對方最不舒服的地方、本質互斥」，但那只是聯想，不是技法，不宜計入 debility 分數。
   *
   * 有依據的是另一種情形：兩星**真正互容**（廟或旺），同時兩者都落陷／入弱（例：水星在雙魚、木星在處女）——
   * 那是互容的一種，該當成互容的屬性（雙方皆弱）來描述，見 [MutualDeception]（2008 年的版本）。
   *  */
  val mutualDeception = IPlanetPatternFactory { planet, h ->
    with(essentialImpl) {

      planet.getMutualData(h.pointDegreeMap, null, setOf(Dignity.DETRIMENT, Dignity.FALL))
        .map { mutualData ->
          val sign1 = h.getZodiacSign(planet)!!
          val p2 = mutualData.getAnotherPoint(planet)
          val sign2 = h.getZodiacSign(p2)!!
          logger.debug { "[mutualDeception] : $mutualData" }
          logger.debug("{} 位於 {} , 與其 {}({}) 飛至 {} . 而 {} 的 {}({}) 飛至 {} , 形成 互陷害",
                       planet, sign1, mutualData.getDignityOf(p2), p2, sign2, sign2, mutualData.getDignityOf(planet),
                       planet, sign1)
          Debility.MutualDeception(
            planet, sign1, mutualData.getDignityOf(planet),
            p2, sign2, mutualData.getDignityOf(p2)
          )
        }.toList()
    }
  }

  /**
   * ====================================== for [Misc.Overcoming] ======================================
   * 居上 / 凌駕 : 不列入 [essentialDignities] / [accidentalDignities] / [debilities] , 不計分 , 須自行加入 config
   */
  private class OvercomingFactory(private val overcomingImpl: IOvercoming) : IPlanetPatternFactory {
    override fun getPatterns(planet: Planet, h: ClassicalInput): List<IPlanetPattern> {
      val posMap = h.positionMap
      val deg = posMap[planet]?.lngDeg ?: return emptyList()
      return Planet.classicalList
        .filter { it != planet }
        .mapNotNull { other -> posMap[other]?.lngDeg?.let { overcomingImpl.getOvercoming(planet, deg, other, it) } }
        .map { Misc.Overcoming(planet, it) }
    }

    override fun equals(other: Any?): Boolean = other is OvercomingFactory && overcomingImpl == other.overcomingImpl
    override fun hashCode(): Int = overcomingImpl.hashCode()
  }

  /**
   * 本命的居上（[natalOvercomings] 的判斷層）：只對**被居上的那顆**列一行，居上者不另列，同一件事不出現兩行。
   * 這份 context 沒有 [overcomingImpl] 時一律為空
   */
  val overcome = IPlanetPatternFactory { planet, h ->
    val list = natalOvercomings(h).filter { it.target === planet }
    if (list.isEmpty()) return@IPlanetPatternFactory emptyList()
    val sect = sectOf(h)
    list.map { n ->
      Misc.Overcoming(planet, n.overcoming, n.ascRuler, overcomingHouses(h, n.overcoming.superior as Planet, planet), sect)
    }
  }

  /** 兩顆星各落哪一宮、守護哪幾宮（盤本身的宮制）；盤沒有宮位時為 null */
  private fun overcomingHouses(h: IHoroscopeModel, superior: Planet, inferior: Planet): OvercomingHouses? {
    if (h.cuspDegreeMap.size < 12) return null
    val rules = { p: Planet -> (1..12).filter { i -> with(rulerImpl) { h.getCuspDegree(i).sign.getRulerPoint() } === p } }
    return OvercomingHouses(
      superiorHouse = h.getHouse(superior) ?: return null, superiorRules = rules(superior),
      inferiorHouse = h.getHouse(inferior) ?: return null, inferiorRules = rules(inferior),
    )
  }

  /** 映點（[IAntiscia] 的判斷層）：一對星一行，掛在 [Antiscion.planet] 上；這份 context 沒有 [antisciaImpl] 時為空 */
  val antiscion = IPlanetPatternFactory { planet, h ->
    antisciaImpl?.getResults(h).orEmpty().filter { it.planet === planet }.map { Misc.InAntiscia(planet, it) }
  }

  /** 顯現（[IPhasis] 的判斷層）：一次轉折一行；這份 context 沒有 [phasisImpl] 時為空 */
  val phasis = IPlanetPatternFactory { planet, h ->
    phasisImpl?.getPhases(h, planet).orEmpty().map { Misc.Phasis(planet, it) }
  }

  /** 野生（[IFeral] 的判斷層）；這份 context 沒有 [feralImpl] 時為空 */
  val feral = IPlanetPatternFactory.single { planet, h ->
    feralImpl?.getFeral(h, planet)?.let { Misc.Feral(planet, it) }
  }

  /** 月亮的離與合（[ILunarConnection] 的判斷層）：只掛在月亮上；這份 context 沒有 [lunarConnectionImpl] 時為空 */
  val lunarConnection = IPlanetPatternFactory.single { planet, h ->
    planet.takeIf { it === MOON }?.let { lunarConnectionImpl?.getConnection(h) }?.let { Misc.LunarConnection(planet, it) }
  }

  /** 燃燒路（[IViaCombusta] 的判斷層）；這份 context 沒有 [viaCombustaImpl] 時為空 */
  val viaCombusta = IPlanetPatternFactory.single { planet, h ->
    viaCombustaImpl?.getViaCombusta(h, planet)?.let { Misc.ViaCombusta(planet, it) }
  }

  /** 持矛者（[IDoryphoria] 的判斷層）：每個光體一行，列出它的全部持矛者；這份 context 沒有 [doryphoriaImpl] 時為空 */
  val spearBearing = IPlanetPatternFactory { planet, h ->
    val impl = doryphoriaImpl ?: return@IPlanetPatternFactory emptyList()
    if (planet !== SUN && planet !== MOON) return@IPlanetPatternFactory emptyList()
    val sect = sectOf(h)
    impl.getBearers(h, planet, sect).takeIf { it.isNotEmpty() }?.let { listOf(Misc.SpearBearing(planet, it, sect)) }.orEmpty()
  }

  /** 受剋與受扶（[IMaltreatment] 的判斷層）：受剋、受扶各一行；這份 context 沒有 [maltreatmentImpl] 時為空 */
  val maltreated = IPlanetPatternFactory { planet, h ->
    val impl = maltreatmentImpl ?: return@IPlanetPatternFactory emptyList()
    Maltreatment.Polarity.entries.mapNotNull { impl.getMaltreatment(h, planet, it) }.map { Misc.Maltreated(planet, it) }
  }

  /** 不相見（[aversions] 的判斷層）：對不相見的那顆列一行；這份 context 沒有 [aversionImpl] 時為空 */
  val averse = IPlanetPatternFactory { planet, h ->
    aversions(h).filter { it.planet === planet }.map { Misc.Aversion(planet, it) }
  }

  /** 整宮（星座）版本 */
  val overcomingBySign: IPlanetPatternFactory = OvercomingFactory(OvercomingSignImpl())

  /** 度數版本 */
  val overcomingByDegree: IPlanetPatternFactory = OvercomingFactory(OvercomingDegreeImpl())

  val essentialDignities: List<IPlanetPatternFactory> = listOf(ruler, exaltation, triplicity, term, face, beneficialMutualReception)

  val accidentalDignities: List<IPlanetPatternFactory> = listOf(house_1_10, house_4_7_11, house_2_5, house_9, house_3, direct, swift
    , orientalGood, occidentalGood, moonIncreaseLight, freeCombustion, cazimi, partileConjJupiterVenus
    , partileConjNorthNode, partileTrineJupiterVenus, partileSextileJupiterVenus, partileConjRegulus
    , partileConjSpica, joyHouse, hayz, besiegedJupiterVenus, translationOfLight, collectionOfLight, refrainFromMarsSaturn)

  val debilities: List<IPlanetPatternFactory> = listOf(detriment, fall, peregrine, house_12, house_6_8, retrograde, slower
    , occidentalBad, orientalBad, moonDecreaseLight, combustion, sunbeam, partileConjMarsSaturn, partileConjSouthNode
    , besiegedMarsSaturn, partileOppoMarsSaturn, partileSquareMarsSaturn, conjAlgol, outOfSect, refrainFromVenusJupiter)
  // mutualDeception 刻意不列入：傳統上沒有「以 debility 互相接納」，理由見 [mutualDeception]


  /**
   * ====================================== for [Misc]：入相位的結局 ======================================
   * 只從追的那顆（[ApplicationOutcome.applying]）的角度記，同一筆不會出現兩次。
   * 月亮算不算插隊者由 [applicationOutcomeImpl] 的 [OutcomeOptions] 決定（本命 [OutcomeOptions.NATAL_DB] 不算、卜卦算）
   */

  /** [planet] 的入相位被第三顆星插隊 */
  val prohibited = IPlanetPatternFactory { planet, h ->
    h.outcomesApplying<ApplicationOutcome.Prohibition>(planet).map { Misc.Prohibited(planet, it) }
  }

  /** [planet] 的入相位撲空：被追的那顆先與更慢的星成形 */
  val frustrated = IPlanetPatternFactory { planet, h ->
    h.outcomesApplying<ApplicationOutcome.Frustration>(planet).map { Misc.Frustrated(planet, it) }
  }

  /** [planet] 的入相位臨陣退縮（不限哪兩顆星） */
  val refrained = IPlanetPatternFactory { planet, h ->
    h.outcomesApplying<ApplicationOutcome.Refranation>(planet).map { Misc.Refrained(planet, it) }
  }

  /** [planet] 被火土、被金木包夾（[IBesiegement.getResults] 的同一組） */
  val besieged = IPlanetPatternFactory { planet, h ->
    IBesiegement.ENCLOSER_PAIRS.mapNotNull { enclosers ->
      besiegementImpl.getBesiegement(h, planet, enclosers)?.let { Misc.Besieged(planet, it) }
    }
  }

  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
