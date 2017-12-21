/**
 * Created by smallufo on 2017-12-21.
 */
package destiny.astrology.classical

import destiny.astrology.DayNight
import destiny.astrology.Planet.*
import destiny.astrology.Point
import destiny.astrology.ZodiacSign
import destiny.astrology.ZodiacSign.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * 測試 mutual reception , info & test case from https://skywriter.wordpress.com/2016/11/01/new-insights-into-mutual-reception/
 */
class EssentialToolsKtTest {

  private val essentialImpl = EssentialDefaultImpl()
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
      SUN to ARIES ,
      MARS to LEO
    )

    EssentialTools.getMutualReception(SUN , map , Dignity.RULER , Dignity.RULER , essentialImpl).let {
      assertNotNull(it)
      println(it)
    }
    EssentialTools.getMutualReception(MARS , map , Dignity.RULER , Dignity.RULER , essentialImpl).let {
      assertNotNull(it)
      println(it)
    }

    EssentialTools.getMutualReception(SUN , map , Dignity.RULER , Dignity.EXALTATION , essentialImpl).let {
      assertNull(it)
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
  fun `月亮到亥 , 金星到酉 , EXALT 互容` () {
    val map = mapOf<Point, ZodiacSign>(
      MOON to PISCES ,
      VENUS to TAURUS
    )

    EssentialTools.getMutualReception(MOON , map , Dignity.EXALTATION , Dignity.EXALTATION, essentialImpl).let {
      assertNotNull(it)
      println(it)
    }
    EssentialTools.getMutualReception(VENUS , map , Dignity.EXALTATION , Dignity.EXALTATION, essentialImpl).let {
      assertNotNull(it)
      println(it)
    }

    EssentialTools.getMutualReception(MOON , map , Dignity.EXALTATION , Dignity.RULER, essentialImpl).let {
      assertNull(it)
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
   * 參考 [EssentialTriplicityDefaultImpl]
   * 如果白天時， 太陽在雙子（申宮、風象星座）, 風象星座白天主人為土星 ; 若土星在射手（寅宮、火象星座） , 火象星座白天主人為太陽 ==> 太陽、土星 透過 Triplicity 互容
   * 如果夜晚時， 月亮在牡羊（戌宮、火象星座）, 火象星座夜晚主人為木星 ; 若木星在處女（巳宮、土象星座） , 土象星座夜晚主人為月亮 ==> 月亮、木星 透過 Triplicity 互容
   *
   * Reception by triplicity can be one-sided or reciprocal,
   * although options for reciprocity are restricted to day-lords (Sun, Venus, Saturn and Mars) or night-lords (Jupiter, Moon, Mercury and Mars).
   */
  @Test
  fun `白天 太陽雙子、土星射手 Triplicity互容` () {
    val map = mapOf<Point , ZodiacSign>(
      SUN to GEMINI,
      SATURN to SAGITTARIUS
    )
    // 白天互容
    EssentialTools.getTriplicityMutualReception(SUN , map , DayNight.DAY , essentialImpl).let {
      assertNotNull(it)
      println(it)
    }
    EssentialTools.getTriplicityMutualReception(SATURN , map , DayNight.DAY , essentialImpl).let {
      assertNotNull(it)
      println(it)
    }
    // 夜晚不成立
    EssentialTools.getTriplicityMutualReception(SUN , map , DayNight.NIGHT , essentialImpl).let {
      assertNull(it)
    }
  }

  /**
   * 測試夜晚 Triplicity 互容
   */
  @Test
  fun `夜晚 月亮牡羊、火星處女 Triplicity互容` () {

    val map = mapOf<Point , ZodiacSign>(
      MOON to ARIES,
      JUPITER to VIRGO
    )
    // 夜晚互容
    EssentialTools.getTriplicityMutualReception(MOON , map , DayNight.NIGHT , essentialImpl).let {
      assertNotNull(it)
      println(it)
    }
    EssentialTools.getTriplicityMutualReception(JUPITER , map , DayNight.NIGHT , essentialImpl).let {
      assertNotNull(it)
      println(it)
    }
    // 白天不成立
    EssentialTools.getTriplicityMutualReception(MOON , map , DayNight.DAY , essentialImpl).let {
      assertNull(it)
    }
  }


  /**
   * 根據此頁資料來測試
   * http://www.skyscript.co.uk/dig6.html
   */
  @Test
  fun `example1`() {

  }
}