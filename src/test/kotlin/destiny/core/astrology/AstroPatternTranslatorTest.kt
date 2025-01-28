/**
 * Created by smallufo on 2019-10-08.
 */
package destiny.core.astrology

import destiny.core.astrology.AstroPattern.*
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacSign.*
import destiny.tools.KotlinLogging
import destiny.tools.Score.Companion.toScore
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class AstroPatternTranslatorTest {

  val logger = KotlinLogging.logger {}

  private val AstroPattern.title: String
    get() = AstroPatternTranslator.getDescriptor(this).getTitle(Locale.TAIWAN)

  private val AstroPattern.zhDesc: String
    get() = AstroPatternTranslator.getDescriptor(this).getDescription(Locale.TAIWAN)

  private val AstroPattern.enDesc: String
    get() = AstroPatternTranslator.getDescriptor(this).getDescription(Locale.ENGLISH)


  @Test
  fun getDescriptor() {
    GrandTrine(setOf(SUN, VENUS, MOON), Element.WATER).also {
      assertEquals("大三角", it.title)
      assertEquals("大三角：太陽,金星,月亮 在 水相星座 形成大三角。", it.zhDesc)
      assertEquals("Grand Trine : Sun,Venus,Moon form Grand Trine in Water sign.", it.enDesc)
    }.copy(score = 0.95.toScore()).also {
      assertEquals("大三角：太陽,金星,月亮 在 水相星座 形成大三角。分數 95%。", it.zhDesc)
      assertEquals("Grand Trine : Sun,Venus,Moon form Grand Trine in Water sign, with a tightness of 95%.", it.enDesc)
    }


    Kite(head = PointSignHouse(MERCURY, TAURUS, 2),
         wings = setOf(MARS, URANUS),
         tail = PointSignHouse(JUPITER, SCORPIO, 8)).also {
      assertEquals("風箏", it.title)
      assertEquals("風箏：水星為首，火星,天王星為兩翼，木星為風箏尾。", it.zhDesc)
      assertEquals("Kite : Mercury is the kite's front, Mars,Uranus are the wingtips, Jupiter is the tail.", it.enDesc)
    }.copy(score = 0.99.toScore()).also {
      assertEquals("風箏：水星為首，火星,天王星為兩翼，木星為風箏尾。分數 99%。", it.zhDesc)
      assertEquals("Kite : Mercury is the kite's front, Mars,Uranus are the wingtips, Jupiter is the tail, with a tightness of 99%.", it.enDesc)
    }

    TSquared(
      oppoPoints = setOf(MERCURY, SATURN),
      squared = PointSignHouse(MARS, ARIES, 2)
            ).also {
      assertEquals("三刑會沖", it.title)
      assertEquals("三刑會沖：水星,土星對沖，彼此又刑剋位於牡羊座（第2宮)的火星。", it.zhDesc)
      assertEquals("T-Square : Mercury and Saturn are in opposition to each other and both in square aspect to Aries's（House 2) Mars.", it.enDesc)
    }.copy(score = 0.8.toScore()).also {
      assertEquals("三刑會沖：水星,土星對沖，彼此又刑剋位於牡羊座（第2宮)的火星。分數 80%。", it.zhDesc)
      assertEquals("T-Square : Mercury and Saturn are in opposition to each other and both in square aspect to Aries's（House 2) Mars, with a tightness of 80%.", it.enDesc)
    }

    Yod(
      bottoms = setOf(SUN, VENUS),
      apex = PointSignHouse(JUPITER, AQUARIUS, 2)
       ).also {
      assertEquals("上帝之指", it.title)
      assertEquals("上帝之指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。", it.zhDesc)
      assertEquals("Yod : Jupiter is the apex (pointing to Aquarius, House 2), Sun and Venus are the base.", it.enDesc)
    }.copy(score = 0.901.toScore()).also {
      assertEquals("上帝之指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。分數 90%。", it.zhDesc)
      assertEquals("Yod : Jupiter is the apex (pointing to Aquarius, House 2), Sun and Venus are the base, with a tightness of 90%.", it.enDesc)
    }

    Boomerang(
      yod = Yod(
        bottoms = setOf(SUN, VENUS),
        apex = PointSignHouse(JUPITER, AQUARIUS, 2)
               ),
      oppoPoint = PointSignHouse(SATURN, LEO, 8)
             ).also {
      assertEquals("回力鏢", it.title)
      assertEquals("回力鏢：木星,太陽,金星形成上帝之指（木星為指尖，指向水瓶座，第2宮），另外有土星（獅子座，第8宮）與指尖對沖。", it.zhDesc)
      assertEquals("Boomerang : Jupiter,Sun and Venus form YoD（Jupiter is the apex, pointing to Aquarius, House 2). There is another Saturn（located at Leo, House 8）opposing to the apex.", it.enDesc)
    }.copy(score = 0.90123.toScore()).also {
      assertEquals("回力鏢：木星,太陽,金星形成上帝之指（木星為指尖，指向水瓶座，第2宮），另外有土星（獅子座，第8宮）與指尖對沖。分數 90%。", it.zhDesc)
      assertEquals("Boomerang : Jupiter,Sun and Venus form YoD（Jupiter is the apex, pointing to Aquarius, House 2). There is another Saturn（located at Leo, House 8）opposing to the apex, with a tightness of 90%.", it.enDesc)
    }


    GoldenYod(
      bottoms = setOf(SUN, VENUS),
      pointer = PointSignHouse(JUPITER, AQUARIUS, 2)
    ).also {
      assertEquals("黃金指", it.title)
      assertEquals("黃金指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。", it.zhDesc)
      assertEquals("Golden Yod : Jupiter is the apex（located at Aquarius, House 2); Sun and Venus are the base.", it.enDesc)
    }.copy(score = 0.901.toScore()).also {
      assertEquals("黃金指：木星為指尖（指向水瓶座，第2宮），太陽,金星兩星為底。分數 90%。", it.zhDesc)
      assertEquals("Golden Yod : Jupiter is the apex（located at Aquarius, House 2); Sun and Venus are the base, with a tightness of 90%.", it.enDesc)
    }

    GrandCross(
      points = setOf(SUN, MARS, JUPITER, SATURN),
      quality = Quality.FIXED
              ).also {
      assertEquals("大十字", it.title)
      assertEquals("大十字：太陽,火星,木星,土星 於 固定宮 形成大十字。", it.zhDesc)
      assertEquals("Grand Cross : Sun, Mars, Jupiter and Saturn form a Grand Cross at Fixed signs.", it.enDesc)
    }.copy(score = 0.8787.toScore()).also {
      assertEquals("大十字：太陽,火星,木星,土星 於 固定宮 形成大十字。分數 87%。", it.zhDesc)
      assertEquals("Grand Cross : Sun, Mars, Jupiter and Saturn form a Grand Cross at Fixed signs, with a tightness of 87%.", it.enDesc)
    }

    DoubleT(
      tSquares = setOf
        (TSquared(oppoPoints = setOf(SUN, SATURN), squared = PointSignHouse(MARS, ARIES, 1)),
         TSquared(oppoPoints = setOf(MERCURY, JUPITER), squared = PointSignHouse(VENUS, LEO, 5))
        )).also {
      assertEquals("DoubleT" , it.title)
      assertEquals("DoubleT：火星,太陽,土星 與 金星,水星,木星 形成兩組三刑會沖。" , it.zhDesc)
      assertEquals("DoubleT : [Mars, Sun, Saturn] and [Venus, Mercury, Jupiter] form two T-Square patterns." , it.enDesc)
    }.copy(score = 0.80123.toScore()).also {
      assertEquals("DoubleT：火星,太陽,土星 與 金星,水星,木星 形成兩組三刑會沖。分數 80%。" , it.zhDesc)
      assertEquals("DoubleT : [Mars, Sun, Saturn] and [Venus, Mercury, Jupiter] form two T-Square patterns, with a tightness of 80%." , it.enDesc)
    }

    Hexagon(
      grandTrines = setOf
        (GrandTrine(points = setOf(SUN , MARS , SATURN) , element = Element.WATER),
         GrandTrine(points = setOf(MERCURY , JUPITER , VENUS) , element = Element.AIR)
         )).also {
      assertEquals("六芒星" , it.title)
      assertEquals("六芒星：太陽,火星,土星 與 水星,木星,金星 形成六芒星。" , it.zhDesc)
      assertEquals("Hexagon : Sun, Mars, Saturn and Mercury, Jupiter, Venus form a Hexagon." , it.enDesc)
    }.copy(score = 0.9.toScore()).also {
      assertEquals("六芒星：太陽,火星,土星 與 水星,木星,金星 形成六芒星。分數 90%。" , it.zhDesc)
      assertEquals("Hexagon : Sun, Mars, Saturn and Mercury, Jupiter, Venus form a Hexagon, with a tightness of 90%." , it.enDesc)
    }

    Wedge(
      oppoPoints = setOf(MARS , SATURN),
      mediator = PointSignHouse(JUPITER, SAGITTARIUS, 2)
         ).also {
      assertEquals("楔子" , it.title)
      assertEquals("楔子：火星,土星 對沖，逢 木星（射手座，第2宮）介入而緩和局勢。" , it.zhDesc)
      assertEquals("Wedge : Mars and Saturn opposites with each other, with Jupiter（Sagittarius, House 2）alleviating the tension at 60°/120°." , it.enDesc)
    }.copy(score = 0.8.toScore()).also {
      assertEquals("楔子：火星,土星 對沖，逢 木星（射手座，第2宮）介入而緩和局勢。分數 80%。" , it.zhDesc)
      assertEquals("Wedge : Mars and Saturn opposites with each other, with Jupiter（Sagittarius, House 2）alleviating the tension at 60°/120°, with a tightness of 80%." , it.enDesc)
    }

    MysticRectangle(setOf(SUN , MOON , MARS , JUPITER)).also {
      assertEquals("神秘長方形" , it.title)
      assertEquals("神秘長方形：由 太陽,月亮,火星,木星 所組成。" , it.zhDesc)
      assertEquals("Mystic Rectangle : Composed of Sun, Moon, Mars, and Jupiter." , it.enDesc)
    }.copy(score = 0.999.toScore()).also {
      assertEquals("神秘長方形：由 太陽,月亮,火星,木星 所組成。分數 99%。" , it.zhDesc)
      assertEquals("Mystic Rectangle : Composed of Sun, Moon, Mars, and Jupiter, with a tightness of 99%." , it.enDesc)
    }

    Pentagram(setOf(SUN , MOON , MARS , JUPITER , SATURN)).also {
      assertEquals("五芒星" , it.title)
      assertEquals("五芒星：由 太陽,月亮,火星,木星,土星 所組成。" , it.zhDesc)
      assertEquals("Pentagram : Composed of Sun, Moon, Mars, Jupiter and Saturn." , it.enDesc)
    }.copy(score = 0.9999.toScore()).also {
      assertEquals("五芒星：由 太陽,月亮,火星,木星,土星 所組成。分數 99%。" , it.zhDesc)
      assertEquals("Pentagram : Composed of Sun, Moon, Mars, Jupiter and Saturn, with a tightness of 99%." , it.enDesc)
    }

    StelliumSign(setOf(SUN , MOON , MARS , JUPITER , SATURN) , SAGITTARIUS).also {
      assertEquals("聚集星座" , it.title)
      assertEquals("聚集星座：太陽,月亮,火星,木星,土星 聚集在 射手座中。" , it.zhDesc)
      assertEquals("Stellium Sign : Sun,Moon,Mars,Jupiter,Saturn gathering at Sagittarius." , it.enDesc)
    }.copy(score = 0.899.toScore()).also {
      assertEquals("聚集星座：太陽,月亮,火星,木星,土星 聚集在 射手座中。分數 89%。" , it.zhDesc)
      assertEquals("Stellium Sign : Sun,Moon,Mars,Jupiter,Saturn gathering at Sagittarius, with a tightness of 89%." , it.enDesc)
    }

    StelliumHouse(setOf(SUN, MOON, MARS, JUPITER, SATURN), 12).also {
      assertEquals("聚集宮位" , it.title)
      assertEquals("聚集宮位：太陽,月亮,火星,木星,土星 聚集在 第12宮中。" , it.zhDesc)
      assertEquals("Stellium House : Sun,Moon,Mars,Jupiter,Saturn gathering at House 12." , it.enDesc)
    }.copy(score = 0.7.toScore()).also {
      assertEquals("聚集宮位：太陽,月亮,火星,木星,土星 聚集在 第12宮中。分數 70%。" , it.zhDesc)
      assertEquals("Stellium House : Sun,Moon,Mars,Jupiter,Saturn gathering at House 12, with a tightness of 70%." , it.enDesc)
    }

    Confrontation(setOf(
      setOf(SUN , MOON , MERCURY , VENUS),
      setOf(MARS , JUPITER , SATURN)
                       )).also {
      assertEquals("群星對峙" , it.title)
      assertEquals("群星對峙：太陽,月亮,水星,金星 與 火星,木星,土星 形成兩群對峙的星群。" , it.zhDesc)
      assertEquals("Confrontation : [Sun,Moon,Mercury,Venus] and [Mars,Jupiter,Saturn] form two opposing groups." , it.enDesc)
    }.copy(score = 0.9123.toScore()).also {
      assertEquals("群星對峙：太陽,月亮,水星,金星 與 火星,木星,土星 形成兩群對峙的星群。分數 91%。" , it.zhDesc)
      assertEquals("Confrontation : [Sun,Moon,Mercury,Venus] and [Mars,Jupiter,Saturn] form two opposing groups, with a tightness of 91%." , it.enDesc)
    }
  }

  @Test
  fun testLocales() {
    val pattern = GrandTrine(setOf(SUN, VENUS, MOON), Element.WATER, null)

    AstroPatternTranslator.getDescriptor(pattern).also { d ->
      assertEquals("大三角" , d.getTitle(Locale.TAIWAN))
      assertEquals("大三角" , d.getTitle(Locale.SIMPLIFIED_CHINESE))
      assertEquals("大三角" , d.getTitle(Locale.FRANCE))
      assertEquals("Grand Trine" , d.getTitle(Locale.ENGLISH))
      assertEquals("グランドトライン" , d.getTitle(Locale.JAPANESE))
    }
  }

}
