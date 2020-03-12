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
    get() = astroPatternTranslator.getDescriptor(this).toString(Locale.TAIWAN)

  private val AstroPattern.description: String
    get() = astroPatternTranslator.getDescriptor(this).getDescription(Locale.TAIWAN)


  @Test
  fun getDescriptor() {
    GrandTrine(setOf(SUN, VENUS, MOON), Element.WATER).also {
      assertEquals("大三角", it.title)
      assertEquals("大三角：太陽,金星,月亮 在 水相星座 形成大三角。", it.description)
    }.copy(score = 0.95).also {
      assertEquals("大三角：太陽,金星,月亮 在 水相星座 形成大三角。分數 0.95。", it.description)
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
      bottoms = setOf(SUN, VENUS),
      pointer = PointSignHouse(JUPITER, AQUARIUS, 2)
       ).also {
      assertEquals("上帝之指", it.title)
      assertEquals("上帝之指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。", it.description)
    }.copy(score = 90.1).also {
      assertEquals("上帝之指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。分數 90.10。", it.description)
    }

    Boomerang(
      yod = Yod(
        bottoms = setOf(SUN, VENUS),
        pointer = PointSignHouse(JUPITER, AQUARIUS, 2)
               ),
      oppoPoint = PointSignHouse(SATURN, LEO, 8)
             ).also {
      assertEquals("回力鏢", it.title)
      assertEquals("回力鏢：木星,太陽,金星形成上帝之指（木星為指尖，指向水瓶座，第2宮），另外有土星（獅子座，第8宮）與指尖對沖。", it.description)
    }.copy(score = 90.123).also {
      assertEquals("回力鏢：木星,太陽,金星形成上帝之指（木星為指尖，指向水瓶座，第2宮），另外有土星（獅子座，第8宮）與指尖對沖。分數 90.12。", it.description)
    }


    GoldenYod(
      bottoms = setOf(SUN, VENUS),
      pointer = PointSignHouse(JUPITER, AQUARIUS, 2)
             ).also {
      assertEquals("黃金指", it.title)
      assertEquals("黃金指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。", it.description)
    }.copy(score = 90.1).also {
      assertEquals("黃金指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。分數 90.10。", it.description)
    }

    GrandCross(
      points = setOf(SUN, MARS, JUPITER, SATURN),
      quality = Quality.FIXED
              ).also {
      assertEquals("大十字", it.title)
      assertEquals("大十字：太陽,火星,木星,土星 於 固定宮 形成大十字。", it.description)
    }.copy(score = 87.87).also {
      assertEquals("大十字：太陽,火星,木星,土星 於 固定宮 形成大十字。分數 87.87。", it.description)
    }

    DoubleT(
      tSquares = setOf
        (TSquared(oppoPoints = setOf(SUN, SATURN), squared = PointSignHouse(MARS, ARIES, 1)),
         TSquared(oppoPoints = setOf(MERCURY, JUPITER), squared = PointSignHouse(VENUS, LEO, 5))
        )).also {
      assertEquals("DoubleT" , it.title)
      assertEquals("DoubleT：火星,太陽,土星 與 金星,水星,木星 形成兩組三刑會沖。" , it.description)
    }.copy(score = 80.123).also {
      assertEquals("DoubleT：火星,太陽,土星 與 金星,水星,木星 形成兩組三刑會沖。分數 80.12。" , it.description)
    }

    Hexagon(
      grandTrines = setOf
        (GrandTrine(points = setOf(SUN , MARS , SATURN) , element = Element.WATER),
         GrandTrine(points = setOf(MERCURY , JUPITER , VENUS) , element = Element.AIR)
         )).also {
      assertEquals("六芒星" , it.title)
      assertEquals("六芒星：太陽,火星,土星 與 水星,木星,金星 形成六芒星。" , it.description)
    }.copy(score = 90.0).also {
      assertEquals("六芒星：太陽,火星,土星 與 水星,木星,金星 形成六芒星。分數 90.00。" , it.description)
    }

    Wedge(
      oppoPoints = setOf(MARS , SATURN) ,
      moderator = PointSignHouse(JUPITER , SAGITTARIUS , 2)
         ).also {
      assertEquals("楔子" , it.title)
      assertEquals("楔子：火星,土星 對沖，逢 木星（射手座，第2宮）介入而緩和局勢。" , it.description)
    }.copy(score = 80.0).also {
      assertEquals("楔子：火星,土星 對沖，逢 木星（射手座，第2宮）介入而緩和局勢。分數 80.00。" , it.description)
    }

    MysticRectangle(setOf(SUN , MOON , MARS , JUPITER)).also {
      assertEquals("神秘長方形" , it.title)
      assertEquals("神秘長方形：由 太陽,月亮,火星,木星 所組成。" , it.description)
    }.copy(score = 99.9).also {
      assertEquals("神秘長方形：由 太陽,月亮,火星,木星 所組成。分數 99.90。" , it.description)
    }

    Pentagram(setOf(SUN , MOON , MARS , JUPITER , SATURN)).also {
      assertEquals("五芒星" , it.title)
      assertEquals("五芒星：由 太陽,月亮,火星,木星,土星 所組成。" , it.description)
    }.copy(score = 99.99).also {
      assertEquals("五芒星：由 太陽,月亮,火星,木星,土星 所組成。分數 99.99。" , it.description)
    }

    StelliumSign(setOf(SUN , MOON , MARS , JUPITER , SATURN) , SAGITTARIUS).also {
      assertEquals("聚集星座" , it.title)
      assertEquals("聚集星座：太陽,月亮,火星,木星,土星 聚集在 射手座中。" , it.description)
    }.copy(score = 89.9).also {
      assertEquals("聚集星座：太陽,月亮,火星,木星,土星 聚集在 射手座中。分數 89.90。" , it.description)
    }

    StelliumHouse(setOf(SUN, MOON, MARS, JUPITER, SATURN), 12).also {
      assertEquals("聚集宮位" , it.title)
      assertEquals("聚集宮位：太陽,月亮,火星,木星,土星 聚集在 第12宮中。" , it.description)
    }.copy(score = 70.0).also {
      assertEquals("聚集宮位：太陽,月亮,火星,木星,土星 聚集在 第12宮中。分數 70.00。" , it.description)
    }

    Confrontation(setOf(
      setOf(SUN , MOON , MERCURY , VENUS),
      setOf(MARS , JUPITER , SATURN)
                       )).also {
      assertEquals("群星對峙" , it.title)
      assertEquals("群星對峙：太陽,月亮,水星,金星 與 火星,木星,土星 形成兩群對峙的星群。" , it.description)
    }.copy(score = 91.23).also {
      assertEquals("群星對峙：太陽,月亮,水星,金星 與 火星,木星,土星 形成兩群對峙的星群。分數 91.23。" , it.description)
    }
  }

  @Test
  fun testLocales() {
    val pattern = GrandTrine(setOf(SUN, VENUS, MOON), Element.WATER, null)

    astroPatternTranslator.getDescriptor(pattern).also { d ->
      assertEquals("大三角" , d.toString(Locale.TAIWAN))
      assertEquals("大三角" , d.toString(Locale.SIMPLIFIED_CHINESE))
      assertEquals("大三角" , d.toString(Locale.ENGLISH))
      assertEquals("大三角" , d.toString(Locale.FRANCE))
      assertEquals("大三角" , d.toString(Locale.JAPANESE))
    }
  }

}
