/**
 * Created by kevin.huang on 2019-10-08.
 */
package destiny.astrology

import destiny.astrology.AstroPattern.*
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign.*
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
    GrandTrine(setOf(SUN, VENUS, MOON), Element.WATER).also {
      assertEquals("大三角", it.title)
      assertEquals("太陽,金星,月亮 在 水相星座 形成大三角。", it.description)
    }.copy(score = 0.95).also {
      assertEquals("太陽,金星,月亮 在 水相星座 形成大三角。分數 0.95。", it.description)
    }

    GrandTrine(setOf(SUN, VENUS, MOON), Element.WATER, 0.95).also {
      assertEquals("大三角", it.title)
      assertEquals("太陽,金星,月亮 在 水相星座 形成大三角。分數 0.95。", it.description)
    }

    Kite(head = PointSignHouse(MERCURY, TAURUS, 2),
      wings = setOf(MARS, URANUS),
      tail = PointSignHouse(JUPITER, SCORPIO, 8)).also {
      assertEquals("風箏", it.title)
      assertEquals("風箏：水星為首，火星,天王星為兩翼，木星為風箏尾。", it.description)
    }.copy(score = 0.99).also {
      assertEquals("風箏：水星為首，火星,天王星為兩翼，木星為風箏尾。分數 0.99。", it.description)
    }

    TSquared(
      oppoPoints = setOf(MERCURY, SATURN),
      squared = PointSignHouse(MARS, ARIES, 2)
    ).also {
      assertEquals("三刑會沖", it.title)
      assertEquals("三刑會沖：水星,土星對沖，彼此又刑剋位於牡羊座（第2宮)的火星。", it.description)
    }.copy(score = 80.0).also {
      assertEquals("三刑會沖：水星,土星對沖，彼此又刑剋位於牡羊座（第2宮)的火星。分數 80.00。", it.description)
    }

    Yod(
      bottoms = setOf(SUN , VENUS) ,
      pointer = PointSignHouse(JUPITER , AQUARIUS , 2)
    ).also {
      assertEquals("上帝之指" , it.title)
      assertEquals("上帝之指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。" , it.description)
    }.copy(score = 90.1).also {
      assertEquals("上帝之指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。分數 90.10。" , it.description)
    }

    Boomerang(
      yod = Yod(
        bottoms = setOf(SUN , VENUS) ,
        pointer = PointSignHouse(JUPITER , AQUARIUS , 2)
      ) ,
      oppoPoint = PointSignHouse(SATURN , LEO , 8)
    ).also {
      assertEquals("回力鏢" , it.title)
      assertEquals("回力鏢：木星,太陽,金星形成上帝之指（木星為指尖，指向水瓶座，第2宮），另外有土星（獅子座，第8宮）與指尖對沖。" , it.description)
    }.copy(score = 90.123).also {
      assertEquals("回力鏢：木星,太陽,金星形成上帝之指（木星為指尖，指向水瓶座，第2宮），另外有土星（獅子座，第8宮）與指尖對沖。分數 90.12。" , it.description)
    }


    GoldenYod(
      bottoms = setOf(SUN , VENUS) ,
      pointer = PointSignHouse(JUPITER , AQUARIUS , 2)
    ).also {
      assertEquals("黃金指" , it.title)
      assertEquals("黃金指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。" , it.description)
    }.copy(score = 90.1).also {
      assertEquals("黃金指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。分數 90.10。" , it.description)
    }

    GrandCross(
      points = setOf(SUN , MARS , JUPITER , SATURN),
      quality = Quality.FIXED
    ).also {
      assertEquals("大十字" , it.title)
      assertEquals("大十字：太陽,火星,木星,土星 於 固定宮 形成大十字。" , it.description)
    }.copy(score = 87.87).also {
      assertEquals("大十字：太陽,火星,木星,土星 於 固定宮 形成大十字。分數 87.87。" , it.description)
    }
  }

  @Test
  fun testLocales() {
    val pattern = GrandTrine(setOf(SUN, VENUS, MOON), Element.WATER, null)

    astroPatternTranslator.getDescriptor(pattern).also { d ->
      logger.info("title ({})= {}", Locale.TAIWAN, d.getTitle(Locale.TAIWAN))
      logger.info("title ({})= {}", Locale.SIMPLIFIED_CHINESE, d.getTitle(Locale.SIMPLIFIED_CHINESE))
      logger.info("title ({})= {}", Locale.ENGLISH, d.getTitle(Locale.ENGLISH))
      logger.info("title ({})= {}", Locale.FRANCE, d.getTitle(Locale.FRANCE))
      logger.info("title ({})= {}", Locale.JAPANESE, d.getTitle(Locale.JAPANESE))
    }


  }

}
