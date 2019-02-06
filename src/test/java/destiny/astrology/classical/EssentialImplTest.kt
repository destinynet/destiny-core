/**
 * Created by smallufo on 2017-12-23.
 */
package destiny.astrology.classical

import destiny.astrology.DayNightSimpleImpl
import destiny.astrology.Planet.*
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.ZodiacSign.*
import destiny.astrology.classical.Dignity.*
import destiny.core.DayNight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class EssentialImplTest {

  private val triplicityImpl: ITriplicity = TriplicityWilliamImpl()

  private val rulerImpl: IRuler = RulerPtolemyImpl()
  private val detrimentImpl: IDetriment = DetrimentPtolemyImpl()
  private val exaltImpl: IExaltation = ExaltationPtolemyImpl()
  private val fallImpl: IFall = FallPtolemyImpl()
  private val termImpl: ITerm = TermPtolomyImpl()
  private val faceImpl: IFace = FacePtolomyImpl()
  private val dayNightDifferentiator = DayNightSimpleImpl()
  private val essentialImpl: IEssential = EssentialImpl(rulerImpl, exaltImpl, fallImpl, detrimentImpl, triplicityImpl, termImpl, faceImpl, dayNightDifferentiator)

  /**
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
  fun `測試各種 Dignity 的接納`() {
    val signMap = mapOf<Point, ZodiacSign>(
      SUN to LIBRA,
      MERCURY to SCORPIO,
      VENUS to VIRGO,
      MARS to SAGITTARIUS,
      SATURN to ARIES
    )

    val degreeMap = mapOf<Point, Double>(
      SUN to 180 + 25.0,
      MARS to 240 + 37.0,
      JUPITER to 150 + 4.0
    )


    // 太陽 土星 透過 EXALT 互相接納
    essentialImpl.getMutualDataFromSign(SUN, signMap, DayNight.NIGHT, setOf(EXALTATION)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(SUN, EXALTATION, SATURN, EXALTATION), it.first())
    }
    // 太陽到 辰宮 (金星 為 主人 , RULER) , 金星要招待太陽   , 太陽 +5
    assertSame(VENUS, essentialImpl.receivingRulerFromSignMap(SUN, signMap))

    // 太陽到 辰宮 (土星 為 主秘 , EXALT) , 土星也要招待太陽 , 太陽 +4
    assertSame(SATURN, essentialImpl.receivingExaltFromSignMap(SUN, signMap))

    // 太陽 `夜晚` 到辰宮 (水星 為風象星座夜間 三分主 , TRIPLICITY) , 水星也要招待太陽 , 太陽 +3
    assertSame(MERCURY, essentialImpl.receivingTriplicityFromSignMap(SUN, signMap, DayNight.NIGHT))

    // 太陽於 辰宮 25度 , 該位置 TERM 主人是 火星 , 火星透過 TERM 接納、招待太陽 , 太陽 +2
    assertSame(MARS, essentialImpl.receivingTermFrom(SUN, degreeMap))

    // 太陽於 辰宮 25度 , 該位置 FACE 主人是 木星 , 木星透過 FACE 接納、招待太陽 , 太陽 +1
    assertSame(JUPITER, essentialImpl.receivingFaceFrom(SUN, degreeMap))

    // 太陽到 辰宮 (太陽 為 FALL) ,  太陽透過 FALL 接納太陽  , 太陽 -4
    assertSame(SUN, essentialImpl.receivingFallFromSignMap(SUN, signMap))

    // 太陽到 辰宮 (火星 為 DETRIMENT ) , 火星透過 DETRIMENT 接納太陽 , 太陽 -5
    assertSame(MARS, essentialImpl.receivingDetrimentFromSignMap(SUN, signMap))
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
    val map = mapOf<Point, ZodiacSign>(
      SUN to ARIES,
      MARS to LEO
    )

    // 太陽觀點
    essentialImpl.getMutualDataFromSign(SUN, map, null, setOf(RULER)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(SUN, RULER, MARS, RULER), it.firstOrNull())
    }

    // 火星觀點
    essentialImpl.getMutualDataFromSign(MARS, map, null, setOf(RULER)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(MARS, RULER, SUN, RULER), it.firstOrNull())
    }

    // EXALT 無資料
    essentialImpl.getMutualDataFromSign(SUN, map, null, setOf(EXALTATION)).also {
      assertTrue(it.isEmpty())
    }
    // EXALT 無資料
    essentialImpl.getMutualDataFromSign(MARS, map, null, setOf(EXALTATION)).also {
      assertTrue(it.isEmpty())
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
    val map = mapOf<Point, ZodiacSign>(
      MOON to PISCES,
      VENUS to TAURUS
    )

    // 月亮觀點
    essentialImpl.getMutualDataFromSign(MOON, map, null, setOf(RULER, EXALTATION)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(MOON, EXALTATION, VENUS, EXALTATION), it.first())
    }
    // 金星觀點
    essentialImpl.getMutualDataFromSign(VENUS, map, null, setOf(RULER, EXALTATION)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(VENUS, EXALTATION, MOON, EXALTATION), it.first())
    }
    // RULER 無資料
    essentialImpl.getMutualDataFromSign(MOON, map, null, setOf(RULER)).also {
      assertTrue(it.isEmpty())
    }
    // RULER 無資料
    essentialImpl.getMutualDataFromSign(VENUS, map, null, setOf(RULER)).also {
      assertTrue(it.isEmpty())
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
    val map = mapOf<Point, ZodiacSign>(
      SUN to GEMINI,
      SATURN to SAGITTARIUS
    )

    // 太陽、白天，成立
    essentialImpl.getMutualDataFromSign(SUN, map, DayNight.DAY, setOf(TRIPLICITY)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(SUN , TRIPLICITY , SATURN , TRIPLICITY) , it.first())
    }
    // 土星、白天，成立
    essentialImpl.getMutualDataFromSign(SATURN, map, DayNight.DAY, setOf(TRIPLICITY)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(SATURN , TRIPLICITY , SUN , TRIPLICITY) , it.first())
    }
    // 夜晚不成立
    essentialImpl.getMutualDataFromSign(SUN, map, DayNight.NIGHT, setOf(TRIPLICITY)).also {
      assertTrue(it.isEmpty())
    }
    // 夜晚不成立
    essentialImpl.getMutualDataFromSign(SATURN, map, DayNight.NIGHT, setOf(TRIPLICITY)).also {
      assertTrue(it.isEmpty())
    }
  }


  /**
   * 測試夜晚 Triplicity 互容
   */
  @Test
  fun `夜晚 月亮牡羊、火星處女 Triplicity互容`() {
    val map = mapOf<Point, ZodiacSign>(
      MOON to ARIES,
      JUPITER to VIRGO
    )
    // 月亮觀點、夜晚互容
    essentialImpl.getMutualDataFromSign(MOON, map, DayNight.NIGHT, setOf(TRIPLICITY)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(MOON , TRIPLICITY , JUPITER , TRIPLICITY) , it.first())
    }
    // 木星觀點、夜晚互容
    essentialImpl.getMutualDataFromSign(JUPITER, map, DayNight.NIGHT, setOf(TRIPLICITY)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(JUPITER, TRIPLICITY , MOON , TRIPLICITY) , it.first())
    }
    // 白天不成立
    essentialImpl.getMutualDataFromSign(MOON, map, DayNight.DAY, setOf(TRIPLICITY)).also {
      assertTrue(it.isEmpty())
    }
    // 白天不成立
    essentialImpl.getMutualDataFromSign(JUPITER, map, DayNight.DAY, setOf(TRIPLICITY)).also {
      assertTrue(it.isEmpty())
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
    val signMap = mapOf<Point, ZodiacSign>(
      JUPITER to ARIES,
      SATURN to GEMINI
    )

    essentialImpl.getMutualDataFromSign(JUPITER, signMap, null, setOf(DETRIMENT, FALL)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(JUPITER, DETRIMENT, SATURN, FALL), it.first())
    }

    essentialImpl.getMutualDataFromSign(SATURN, signMap, null, setOf(DETRIMENT, FALL)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(SATURN, FALL, JUPITER, DETRIMENT), it.first())
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
    val signMap = mapOf<Point, ZodiacSign>(
      MERCURY to CANCER,
      MARS to SAGITTARIUS
    )

    essentialImpl.getMutualDataFromSign(MERCURY, signMap, null, setOf(DETRIMENT, FALL)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(MERCURY, DETRIMENT, MARS, FALL), it.first())
    }

    essentialImpl.getMutualDataFromSign(MARS, signMap, null, setOf(FALL, DETRIMENT)).also {
      assertTrue(it.isNotEmpty())
      assertSame(1, it.size)
      assertEquals(MutualData(MARS, FALL, MERCURY, DETRIMENT), it.first())
    }
  }
}