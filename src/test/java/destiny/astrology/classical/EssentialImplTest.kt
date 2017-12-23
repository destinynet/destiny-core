/**
 * Created by smallufo on 2017-12-23.
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.DayNightSimpleImpl
import destiny.astrology.Planet.*
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertSame

class EssentialImplTest {

  private val triplicityImpl : ITriplicity = TriplicityWilliamImpl()

  private val rulerImpl : IRuler = RulerPtolemyImpl()
  private val detrimentImpl : IDetriment = DetrimentPtolemyImpl()
  private val exaltImpl : IExaltation = ExaltationPtolemyImpl()
  private val fallImpl : IFall = FallPtolemyImpl()
  private val termImpl: ITerm = TermPtolomyImpl()
  private val faceImpl : IFace = FacePtolomyImpl()
  private val dayNightDifferentiator = DayNightSimpleImpl()
  private val essentialImpl: IEssential = EssentialImpl(rulerImpl , exaltImpl , fallImpl , detrimentImpl , triplicityImpl , termImpl, faceImpl , dayNightDifferentiator)

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
      SUN to LIBRA ,
      MERCURY to SCORPIO,
      VENUS to VIRGO,
      MARS to SAGITTARIUS,
      SATURN to ARIES
    )

    val degreeMap = mapOf<Point , Double>(
      SUN to 180+25.0,
      MARS to 240+37.0,
      JUPITER to 150+4.0
    )

    // 太陽到 辰宮 (金星 為 主人 , RULER) , 金星要招待太陽   , 太陽 +5
    assertSame(VENUS , essentialImpl.receivingRulerFromSignMap(SUN , signMap))

    // 太陽到 辰宮 (土星 為 主秘 , EXALT) , 土星也要招待太陽 , 太陽 +4
    assertSame(SATURN , essentialImpl.receivingExaltFromSignMap(SUN , signMap))

    // 太陽 `夜晚` 到辰宮 (水星 為風象星座夜間 三分主 , TRIPLICITY) , 水星也要招待太陽 , 太陽 +3
    assertSame(MERCURY , essentialImpl.receivingTriplicityFromSignMap(SUN , signMap , DayNight.NIGHT))

    // 太陽於 辰宮 25度 , 該位置 TERM 主人是 火星 , 火星透過 TERM 接納、招待太陽 , 太陽 +2
    assertSame(MARS , essentialImpl.receivingTermFrom(SUN , degreeMap))

    // 太陽於 辰宮 25度 , 該位置 FACE 主人是 木星 , 木星透過 FACE 接納、招待太陽 , 太陽 +1
    assertSame(JUPITER , essentialImpl.receivingFaceFrom(SUN , degreeMap))

    // 太陽到 辰宮 (太陽 為 FALL) ,  太陽透過 FALL 接納太陽  , 太陽 -4
    assertSame(SUN , essentialImpl.receivingFallFromSignMap(SUN , signMap))

    // 太陽到 辰宮 (火星 為 DETRIMENT ) , 火星透過 DETRIMENT 接納太陽 , 太陽 -5
    assertSame(MARS , essentialImpl.receivingDetrimentFromSignMap(SUN , signMap))

  }
}