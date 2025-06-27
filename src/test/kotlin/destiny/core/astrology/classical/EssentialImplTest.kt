/**
 * Created by smallufo on 2017-12-23.
 */
package destiny.core.astrology.classical

import destiny.core.DayNight.DAY
import destiny.core.DayNight.NIGHT
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.DayNightSimpleImpl
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.ZodiacSign.*
import destiny.core.astrology.classical.Dignity.*
import destiny.core.calendar.JulDayResolver1582CutoverImpl
import destiny.tools.KotlinLogging
import kotlin.test.*

class EssentialImplTest {

  private val logger = KotlinLogging.logger { }

  private val triplicityImpl: ITriplicity = TriplicityWilliamImpl

  private val rulerImpl: IRuler = RulerPtolemyImpl
  private val detrimentImpl: IDetriment = DetrimentPtolemyImpl
  private val exaltImpl: IExaltation = ExaltationPtolemyImpl
  private val fallImpl: IFall = FallPtolemyImpl
  private val termImpl: ITerm = TermPtolomyImpl
  private val faceImpl: IFace = FacePtolomyImpl
  private val dayNightDifferentiator = DayNightSimpleImpl(JulDayResolver1582CutoverImpl())
  private val essentialImpl: IEssential = EssentialImpl(rulerImpl, exaltImpl, fallImpl, detrimentImpl, triplicityImpl, termImpl, faceImpl, dayNightDifferentiator)

  /**
   * 測試星體本身的強弱 (純粹 sign map)
   */
  @Test
  fun testGetDignitiesFromSignMap() {
    with(essentialImpl) {

      // 太陽
      SUN.also { planet ->
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES), DAY), listOf(EXALTATION, TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES)), listOf(EXALTATION)) // 沒指定日夜 , 就不判定 TRIPLICITY
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO), DAY), listOf(RULER, TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO), NIGHT), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA)), listOf(FALL))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS), DAY), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS), NIGHT), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES)), listOf())
      } // sun

      // 月亮
      MOON.also { planet ->
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES)), emptyList())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS)), listOf(EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS), DAY), listOf(EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS), NIGHT), listOf(EXALTATION, TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI)), emptyList())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER), DAY), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER), NIGHT), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO)), emptyList())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO)), emptyList())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO), NIGHT), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA)), emptyList())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO)), listOf(FALL))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES)), listOf())
      } // moon

      // 水星
      MERCURY.also { planet ->
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI), DAY), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI), NIGHT), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO)), listOf(RULER, EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO), DAY), listOf(EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO), NIGHT), listOf(RULER, EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA), NIGHT), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS), NIGHT), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES)), listOf(FALL, DETRIMENT))
      } // mercury

      // 金星
      VENUS.also { planet ->
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS), DAY), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS), NIGHT), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO)), listOf(FALL))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA), DAY), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA), NIGHT), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES)), listOf(EXALTATION))
      } // venus

      // 火星
      MARS.also { planet ->
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES), DAY), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES), NIGHT), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER)), listOf(TRIPLICITY, FALL))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER), DAY), listOf(TRIPLICITY, FALL))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER), NIGHT), listOf(TRIPLICITY, FALL))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO)), listOf(RULER, TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO), DAY), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO), NIGHT), listOf(RULER, TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN)), listOf(EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES)), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES), DAY), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES), NIGHT), listOf(TRIPLICITY))
      } // mars

      // 木星
      JUPITER.also { planet ->
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES), DAY), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES), NIGHT), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER)), listOf(EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO), NIGHT), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS), DAY), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS), NIGHT), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN)), listOf(FALL))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES), DAY), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES), NIGHT), listOf(RULER))
      } // jupiter

      // 土星
      SATURN.also { planet ->
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to ARIES)), listOf(FALL))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to TAURUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to GEMINI), DAY), listOf(TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CANCER)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LEO)), listOf(DETRIMENT))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to VIRGO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA)), listOf(EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA), DAY), listOf(EXALTATION, TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to LIBRA), NIGHT), listOf(EXALTATION))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SCORPIO)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to SAGITTARIUS)), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN), DAY), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to CAPRICORN), NIGHT), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS)), listOf(RULER))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS), DAY), listOf(RULER, TRIPLICITY))
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to AQUARIUS), NIGHT), listOf())
        assertEquals(planet.getDignitiesFromSignMap(mapOf(planet to PISCES)), listOf())
      } // saturn
    }
  }

  /**
   * 測試各種 Dignity 的接納
   * 根據此頁資料來測試各種 [Dignity] 的接納
   * http://www.skyscript.co.uk/dig6.html
   *
   * Consider the Sun in Libra. Venus is said to 'receive' the Sun because he is visiting her sign.
   * In this capacity Venus is known as the Sun's dispositor.
   *
   * 太陽到 辰宮 (金星 為 主人 , RULER) , 金星要招待太陽   , 太陽 +5
   *
   * The Sun in Libra is also received, to a lesser degree, by Saturn since he has dignity in Libra by exaltation.
   * 太陽到 辰宮 (土星 為 主秘 , EXALT) , 土星也要招待太陽 , 太陽 +4
   *
   * If the chart is a nocturnal one, Mercury offers a milder reception as ruler of the triplicity.
   * 太陽 `夜晚` 到辰宮 (水星 為風象星座夜間 三分主 , TRIPLICITY) , 水星也要招待太陽 , 太陽 +3
   *
   * 太陽到 辰宮 (太陽 為 FALL) ,  太陽透過 FALL 接納太陽  , 太陽 -4
   * 太陽到 辰宮 (火星 為 DETRIMENT ) , 火星透過 DETRIMENT 接納太陽 , 太陽 -5
   */
  @Test
  fun testDignitiesReception() {
    val signMap = mapOf<AstroPoint, ZodiacSign>(
      SUN to LIBRA,
      MERCURY to SCORPIO,
      VENUS to VIRGO,
      MARS to SAGITTARIUS,
      JUPITER to VIRGO,
      SATURN to ARIES
    )

    val degreeMap = mapOf<AstroPoint, ZodiacDegree>(
      SUN to (180 + 25.0).toZodiacDegree(),
      MERCURY to (210 + 1.0).toZodiacDegree(),
      VENUS to (150 + 1.0).toZodiacDegree(),
      MARS to (240 + 37.0).toZodiacDegree(),
      JUPITER to (150 + 4.0).toZodiacDegree(),
      SATURN to (1.0).toZodiacDegree()
    )

    with(essentialImpl) {

      // 太陽 土星 透過 EXALT 互相接納
      SUN.getMutualDataFromSign(signMap, NIGHT, setOf(EXALTATION)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(SUN, EXALTATION, SATURN, EXALTATION), it.first())
      }

      // 太陽到 辰宮 (金星 為 主人 , RULER) , 金星要招待太陽   , 太陽 +5
      assertSame(VENUS, SUN.receivingRulerFromSignMap(signMap))
      assertSame(VENUS, SUN.receiving(RULER, degreeMap))

      // 太陽到 辰宮 (土星 為 主秘 , EXALT) , 土星也要招待太陽 , 太陽 +4
      assertSame(SATURN, SUN.receivingExaltFromSignMap(signMap))
      assertSame(SATURN, SUN.receiving(EXALTATION, degreeMap))

      // 太陽 `夜晚` 到辰宮 (水星 為風象星座夜間 三分主 , TRIPLICITY) , 水星也要招待太陽 , 太陽 +3
      assertSame(MERCURY, SUN.receivingTriplicityFromSignMap(signMap, NIGHT))
      assertSame(MERCURY, SUN.receiving(TRIPLICITY, degreeMap, NIGHT))

      // 太陽於 辰宮 25度 , 該位置 TERM 主人是 火星 , 火星透過 TERM 接納、招待太陽 , 太陽 +2
      assertSame(MARS, SUN.receivingTermFrom(degreeMap))
      assertSame(MARS, SUN.receiving(TERM, degreeMap))

      // 太陽於 辰宮 25度 , 該位置 FACE 主人是 木星 , 木星透過 FACE 接納、招待太陽 , 太陽 +1
      assertSame(JUPITER, SUN.receivingFaceFrom(degreeMap))
      assertSame(JUPITER, SUN.receiving(FACE, degreeMap))

      // 太陽到 辰宮 (太陽 為 FALL) ,  太陽透過 FALL 接納太陽  , 太陽 -4
      assertSame(SUN, SUN.receivingFallFromSignMap(signMap))
      assertSame(SUN, SUN.receiving(FALL, degreeMap))

      // 太陽到 辰宮 (火星 為 DETRIMENT ) , 火星透過 DETRIMENT 接納太陽 , 太陽 -5
      assertSame(MARS, SUN.receivingDetrimentFromSignMap(signMap))
      assertSame(MARS, SUN.receiving(DETRIMENT, degreeMap))
    }
  }

  /**
   * 整合 map
   * https://kknews.cc/zh-tw/astrology/6lxgvo3.html
   * 如：在一張日生盤上，火星落在天秤座3度，則火星被金星接納（5，入廟），也被土星接納（4+3+2=9，旺+三分+界）
   */
  @Test
  fun testReceptionMap1() {

    val signMap: Map<AstroPoint, ZodiacSign> = mapOf(
      MARS to LIBRA,
      VENUS to ARIES,
      SATURN to ARIES
    )
    val degreeMap: Map<AstroPoint, ZodiacDegree> = mapOf(
      MARS to (180 + 3.0).toZodiacDegree(),
      VENUS to 1.toZodiacDegree(),
      SATURN to 1.toZodiacDegree()
    )



    with(essentialImpl) {

      // 廟、旺
      MARS.getReceptionsFromSign(signMap, null, setOf(RULER, EXALTATION, TRIPLICITY, TERM)).also {
        assertEquals(VENUS, it[RULER])
        assertEquals(SATURN, it[EXALTATION])
      }

      // 三分
      MARS.receivingTriplicityFrom(degreeMap, DAY).also {
        assertNotNull(it)
        assertSame(SATURN, it)
      }

      // 界
      MARS.receivingTermFrom(degreeMap).also {
        assertNotNull(it)
        assertSame(SATURN, it)
      }
    }


    // 整合 map
    essentialImpl.getReceptionMap(degreeMap, DAY, setOf(RULER, EXALTATION, TRIPLICITY, TERM)).also {
      // 火星被金星接納（5，入廟）
      assertTrue { it.contains(Triple(MARS, RULER, VENUS)) }
      // 也被土星接納（4+3+2=9，旺+三分+界）
      assertTrue { it.contains(Triple(MARS, EXALTATION, SATURN)) }
      assertTrue { it.contains(Triple(MARS, TRIPLICITY, SATURN)) }
      assertTrue { it.contains(Triple(MARS, TERM, SATURN)) }

      logger.debug("{}", it)
    }
  }

  /**
   * 整合 map
   * 與上相同的頁面 : https://kknews.cc/astrology/6lxgvo3.html
   * 又如：夜生盤的月亮落在獅子座28度，則它被太陽接納（5，入廟），被木星接納（3，三分），也被火星接納（2+1=3，界+面），但容易理解，月亮被火木的接納程度沒有太陽高。
   */
  @Test
  fun testReceptionMap2() {
    val degreeMap: Map<AstroPoint, ZodiacDegree> = mapOf(
      MOON to (120 + 28.0).toZodiacDegree(),
      SUN to 1.toZodiacDegree(),
      JUPITER to 1.toZodiacDegree(),
      MARS to 1.toZodiacDegree()
    )
    essentialImpl.getReceptionMap(degreeMap, NIGHT, setOf(RULER, EXALTATION, TRIPLICITY, TERM, FACE)).also {
      assertTrue { it.contains(Triple(MOON, RULER, SUN)) }
      assertTrue { it.contains(Triple(MOON, TRIPLICITY, JUPITER)) }
      assertTrue { it.contains(Triple(MOON, TERM, MARS)) }
      assertTrue { it.contains(Triple(MOON, FACE, MARS)) }
      logger.debug("{}", it)
    }
  }

  /**
   * Reception by sign
   *
   * In reception by sign, Planet A occupies a sign ruled by Planet B, who thus receives Planet A by domicile.
   * The word dispositor denotes the planet holding the power, ie, ruling the sign occupied by another planet.
   *
   * If Mars occupies Leo, Mars is received by the Sun who rules Leo. Conversely, if the Sun occupies Aries or Scorpio,
   * it’s received by Mars who owns those signs.
   *
   * 如果火星到獅子 , 火星就受到太陽的照顧 , 如果太陽到牡羊（or 天蠍），太陽就受到火星的照顧
   *
   * Note: traditional sign rulership applies here – Mars rules Scorpio, Saturn rules Aquarius, Jupiter rules Pisces.
   *
   * Reception by sign can be one-sided or reciprocal. When two planets occupy each other’s sign simultaneously,
   * eg, the Sun in Aries and Mars in Leo, we call this mutual reception.
   *
   * (planet1=太陽, sign1=牡羊, planet2=火星, sign2=獅子) , 互相透過 RULER 互訪而互容
   */
  @Test
  fun `太陽到戌 , 火星到午 , RULER 互訪而形成互容`() {
    val map = mapOf<AstroPoint, ZodiacSign>(
      SUN to ARIES,
      MARS to LEO
    )

    with(essentialImpl) {
      // 太陽觀點
      SUN.getMutualDataFromSign(map, null, setOf(RULER)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(SUN, RULER, MARS, RULER), it.firstOrNull())
      }

      // 火星觀點
      MARS.getMutualDataFromSign(map, null, setOf(RULER)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(MARS, RULER, SUN, RULER), it.firstOrNull())
      }

      // EXALT 無資料
      SUN.getMutualDataFromSign(map, null, setOf(EXALTATION)).also {
        assertTrue(it.isEmpty())
      }
      // EXALT 無資料
      MARS.getMutualDataFromSign(map, null, setOf(EXALTATION)).also {
        assertTrue(it.isEmpty())
      }
    }

  }

  /**
   * 擢升互容
   * Reception by exaltation
   *
   * In reception by exaltation, Planet A occupies the exaltation sign of Planet B, who thus receives Planet A in its place of honor.
   *
   * If the Moon occupies Pisces, it’s received by Venus who’s exalted in Pisces.
   * Conversely, if Venus occupies Taurus, irrespective of being in its own sign, it’s also received by the Moon who is exalted in Taurus.
   *
   * 如果月亮到雙魚 (主人是木星，秘書是金星) , 月亮就受到金星（秘書）的幫助
   * 如果金星到金牛 (主人是金星，秘書是月亮) , 金星就受到月亮（秘書）的幫助
   *
   * Reception by exaltation can be one-sided or reciprocal, since we can always find a scenario
   * where two planets occupy each other’s sign of exaltation.
   */
  @Test
  fun `月亮到亥 , 金星到酉 , EXALT 互容`() {
    val map = mapOf<AstroPoint, ZodiacSign>(
      MOON to PISCES,
      VENUS to TAURUS
    )

    with(essentialImpl) {
      // 月亮觀點
      MOON.getMutualDataFromSign(map, null, setOf(RULER, EXALTATION)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(MOON, EXALTATION, VENUS, EXALTATION), it.first())
      }
      // 金星觀點
      VENUS.getMutualDataFromSign(map, null, setOf(RULER, EXALTATION)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(VENUS, EXALTATION, MOON, EXALTATION), it.first())
      }
      // RULER 無資料
      MOON.getMutualDataFromSign(map, null, setOf(RULER)).also {
        assertTrue(it.isEmpty())
      }
      // RULER 無資料
      VENUS.getMutualDataFromSign(map, null, setOf(RULER)).also {
        assertTrue(it.isEmpty())
      }
    }


  }

  /**
   * Reception by triplicity
   *
   * In reception by triplicity, Planet A occupies an element ruled by Planet B, who thus receives Planet A by virtue of triplicity.
   * However, the rulers of triplicities vary between day and night births.
   *
   *
   * Sun rules fire signs in a day chart, while Jupiter rules them by night.
   * Venus rules earth signs by day, the Moon by night.
   * Saturn rules air signs in a day chart,
   * Mercury by night. Water signs are ruled by Mars, day or night.
   *
   * In a day chart, if the Sun occupies Gemini, it’s received by Saturn who rules air signs by day.
   *    If in that same chart Saturn occupies Sagittarius, it’s received by the Sun who rules fire signs by day.
   * In a night chart, if the Moon occupies Aries, it’s received by Jupiter ruling fire signs by night.
   *    If that Jupiter occupies Virgo, it’s received by the Moon ruling earth signs by night.
   *
   * 參考 [TriplicityWilliamImpl]
   * 如果白天時， 太陽在雙子（申宮、風象星座）, 風象星座白天主人為土星 ; 若土星在射手（寅宮、火象星座） , 火象星座白天主人為太陽 ==> 太陽、土星 透過 Triplicity 互容
   * 如果夜晚時， 月亮在牡羊（戌宮、火象星座）, 火象星座夜晚主人為木星 ; 若木星在處女（巳宮、土象星座） , 土象星座夜晚主人為月亮 ==> 月亮、木星 透過 Triplicity 互容
   *
   * Reception by triplicity can be one-sided or reciprocal,
   * although options for reciprocity are restricted to day-lords (Sun, Venus, Saturn and Mars) or night-lords (Jupiter, Moon, Mercury and Mars).
   */
  @Test
  fun `白天 太陽雙子、土星射手 Triplicity互容`() {
    val map = mapOf<AstroPoint, ZodiacSign>(
      SUN to GEMINI,
      SATURN to SAGITTARIUS
    )

    // 太陽、白天，成立
    with(essentialImpl) {
      SUN.getMutualDataFromSign(map, DAY, setOf(TRIPLICITY)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(SUN, TRIPLICITY, SATURN, TRIPLICITY), it.first())
      }
      // 土星、白天，成立
      SATURN.getMutualDataFromSign(map, DAY, setOf(TRIPLICITY)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(SATURN, TRIPLICITY, SUN, TRIPLICITY), it.first())
      }
      // 夜晚不成立
      SUN.getMutualDataFromSign(map, NIGHT, setOf(TRIPLICITY)).also {
        assertTrue(it.isEmpty())
      }
      // 夜晚不成立
      SATURN.getMutualDataFromSign(map, NIGHT, setOf(TRIPLICITY)).also {
        assertTrue(it.isEmpty())
      }
    }


  }


  /**
   * 測試夜晚 Triplicity 互容
   */
  @Test
  fun `夜晚 月亮牡羊、火星處女 Triplicity互容`() {
    val map = mapOf<AstroPoint, ZodiacSign>(
      MOON to ARIES,
      JUPITER to VIRGO
    )

    with((essentialImpl)) {
      // 月亮觀點、夜晚互容
      MOON.getMutualDataFromSign(map, NIGHT, setOf(TRIPLICITY)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(MOON, TRIPLICITY, JUPITER, TRIPLICITY), it.first())
      }
      // 木星觀點、夜晚互容
      JUPITER.getMutualDataFromSign(map, NIGHT, setOf(TRIPLICITY)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(JUPITER, TRIPLICITY, MOON, TRIPLICITY), it.first())
      }
      // 白天不成立
      MOON.getMutualDataFromSign(map, DAY, setOf(TRIPLICITY)).also {
        assertTrue(it.isEmpty())
      }
      // 白天不成立
      JUPITER.getMutualDataFromSign(map, DAY, setOf(TRIPLICITY)).also {
        assertTrue(it.isEmpty())
      }
    }

  }


  /**
   * 木星到戌宮 , 戌宮 為 土星 FALL      (落) 之處
   * 土星到申宮 , 申宮 為 木星 DETRIMENT (陷) 之處
   *
   * 兩星互相踩對方痛腳
   */
  @Test
  fun `木星到牡羊 , 土星到雙子 , 互相踩痛腳`() {
    val signMap = mapOf<AstroPoint, ZodiacSign>(
      JUPITER to ARIES,
      SATURN to GEMINI
    )

    with(essentialImpl) {
      JUPITER.getMutualDataFromSign(signMap, null, setOf(DETRIMENT, FALL)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(JUPITER, DETRIMENT, SATURN, FALL), it.first())
      }

      SATURN.getMutualDataFromSign(signMap, null, setOf(DETRIMENT, FALL)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(SATURN, FALL, JUPITER, DETRIMENT), it.first())
      }
    }

  }

  /**
   * 水星到未宮 , 未宮 為 火星 FALL      (落) 之處
   * 火星到寅宮 , 寅宮 為 水星 DETRIMENT (陷) 之處
   *
   * 兩星互相踩對方痛腳
   */
  @Test
  fun `水星到巨蟹 , 火星到射手 , 互相踩痛腳`() {
    val signMap = mapOf<AstroPoint, ZodiacSign>(
      MERCURY to CANCER,
      MARS to SAGITTARIUS
    )

    with(essentialImpl) {
      MERCURY.getMutualDataFromSign(signMap, null, setOf(DETRIMENT, FALL)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(MERCURY, DETRIMENT, MARS, FALL), it.first())
      }

      MARS.getMutualDataFromSign(signMap, null, setOf(FALL, DETRIMENT)).also {
        assertTrue(it.isNotEmpty())
        assertSame(1, it.size)
        assertEquals(MutualData(MARS, FALL, MERCURY, DETRIMENT), it.first())
      }
    }

  }
}
