/**
 * Created by kevin.huang on 2019-10-08.
 */
package destiny.astrology

import destiny.astrology.AstroPattern.*
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class astroPatternTranslatorTest {

  val logger = KotlinLogging.logger {}

  private val AstroPattern.title: String
    get() = astroPatternTranslator.getDescriptor(this).getTitle(Locale.TAIWAN)

  private val AstroPattern.description: String
    get() = astroPatternTranslator.getDescriptor(this).getDescription(Locale.TAIWAN)


  @Test
  fun getDescriptor() {
    GrandTrine(setOf(Planet.SUN, Planet.VENUS, Planet.MOON), Element.WATER).also {
      assertEquals("大三角", it.title)
      assertEquals("太陽,金星,月亮 在 水相星座 形成大三角。", it.description)
    }.copy(score = 0.95).also {
      assertEquals("太陽,金星,月亮 在 水相星座 形成大三角。分數 0.95。", it.description)
    }

    GrandTrine(setOf(Planet.SUN, Planet.VENUS, Planet.MOON), Element.WATER, 0.95).also {
      assertEquals("大三角", it.title)
      assertEquals("太陽,金星,月亮 在 水相星座 形成大三角。分數 0.95。", it.description)
    }

    Kite(head = PointSignHouse(Planet.MERCURY, ZodiacSign.TAURUS, 2),
      wings = setOf(Planet.MARS, Planet.URANUS),
      tail = PointSignHouse(Planet.JUPITER, ZodiacSign.SCORPIO, 8)).also {
      assertEquals("風箏", it.title)
      assertEquals("風箏：水星為首，火星,天王星為兩翼，木星為風箏尾。", it.description)
    }.copy(score = 0.99).also {
      assertEquals("風箏：水星為首，火星,天王星為兩翼，木星為風箏尾。分數 0.99。", it.description)
    }

    TSquared(
      oppoPoints = setOf(Planet.MERCURY, Planet.SATURN),
      squared = PointSignHouse(Planet.MARS, ZodiacSign.ARIES, 2)
    ).also {
      assertEquals("三刑會沖", it.title)
      assertEquals("三刑會沖：水星,土星對沖，彼此又刑剋位於牡羊座（第2宮)的火星。", it.description)
    }.copy(score = 80.0).also {
      assertEquals("三刑會沖：水星,土星對沖，彼此又刑剋位於牡羊座（第2宮)的火星。分數 80.00。", it.description)
    }
  }

  @Test
  fun testLocales() {
    val pattern = GrandTrine(setOf(Planet.SUN, Planet.VENUS, Planet.MOON), Element.WATER, null)

    astroPatternTranslator.getDescriptor(pattern).also { d ->
      logger.info("title ({})= {}", Locale.TAIWAN, d.getTitle(Locale.TAIWAN))
      logger.info("title ({})= {}", Locale.SIMPLIFIED_CHINESE, d.getTitle(Locale.SIMPLIFIED_CHINESE))
      logger.info("title ({})= {}", Locale.ENGLISH, d.getTitle(Locale.ENGLISH))
      logger.info("title ({})= {}", Locale.FRANCE, d.getTitle(Locale.FRANCE))
      logger.info("title ({})= {}", Locale.JAPANESE, d.getTitle(Locale.JAPANESE))
    }


  }

}
