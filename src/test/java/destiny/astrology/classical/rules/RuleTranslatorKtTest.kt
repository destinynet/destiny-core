/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.Planet
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity
import destiny.core.DayNight
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RuleTranslatorKtTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun `EssentialDignity test name and description`() {
    val locale = Locale.TAIWAN
    EssentialDignity.Ruler(Planet.SUN, ZodiacSign.LEO).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("廟 (Ruler)", it.getTitle(locale))
        assertEquals("太陽 位於 獅子，為其 Ruler。", it.getDescription(locale))
      }
    }

    EssentialDignity.Exaltation(Planet.SUN, ZodiacSign.ARIES).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("旺 (Exalt)", it.getTitle(locale))
        assertEquals("太陽 位於 牡羊，為其 Exaltation。", it.getDescription(locale))
      }
    }

    EssentialDignity.Triplicity(Planet.SUN, ZodiacSign.ARIES, DayNight.DAY).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("三分主星 (Triplicity)", it.getTitle(locale))
        assertEquals("太陽 位於 牡羊，為其 DAY 之 Triplicity。", it.getDescription(locale))
      }
    }

    EssentialDignity.Term(Planet.JUPITER, 6.0).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("界 (Term)", it.getTitle(locale))
        assertEquals("木星 位於其 Term ： 6.0 。", it.getDescription(locale))
      }
    }

    EssentialDignity.Face(Planet.SUN , 20.0).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("十度區間主星 (Face)" , it.getTitle(locale))
        assertEquals("太陽 位於其 Chaldean decanate or face : 20.0。" , it.getDescription(locale))
      }
    }

    EssentialDignity.BeneficialMutualReception(Planet.SUN , Dignity.RULER , Planet.MARS , Dignity.EXALTATION).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("有利互容" , it.getTitle(locale))
        assertEquals("太陽 與 火星 形成 廟旺互容。" , it.getDescription(locale))
      }
    }

  }


  @Test
  fun `AccidentalDignity test name and description`() {
    val locale = Locale.TAIWAN
    AccidentalDignity.House_1_10(Planet.SUN, 1).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("位於第一或第十宮" , it.getTitle(locale))
        assertEquals("太陽 位於第 1 宮" , it.getDescription(locale))
      }
    }


    AccidentalDignity.House_4_7_11(Planet.SUN , 11).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("位於第四、七或是第十一宮" , it.getTitle(locale))
        assertEquals("太陽 位於第 11 宮 (Good Daemon's) House" , it.getDescription(locale))
      }
    }


    AccidentalDignity.House_2_5(Planet.SUN , 2).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("位於第二或第五宮" , it.getTitle(locale))
        assertEquals("太陽 位於第 2 宮" , it.getDescription(locale))
      }
    }

    AccidentalDignity.House_9(Planet.SUN).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("位於第九宮" , it.getTitle(locale))
        assertEquals("太陽 位於第 9 宮" , it.getDescription(locale))
      }
    }

    AccidentalDignity.House_3(Planet.SUN).also { p ->
      ruleTranslator.getDescriptor(p).also {
        assertEquals("位於第三宮" , it.getTitle(locale))
        assertEquals("太陽 位於第 3 宮" , it.getDescription(locale))
      }
    }
  }



  private fun print(rule: EssentialDignity) {

    ruleTranslator.getDescriptor(rule).also {
      logger.info("{} : {}", it, it.javaClass.name)
      println("title(tw) = ${it.getTitle(Locale.TAIWAN)}")
      println("title(en) = ${it.getTitle(Locale.ENGLISH)}")
      println("title(en_US) = ${it.getTitle(Locale.US)}")
      println("title(簡) = ${it.getTitle(Locale.SIMPLIFIED_CHINESE)}")
      println("title(jp) = ${it.getTitle(Locale.JAPANESE)}")
      println("title(fr) = ${it.getTitle(Locale.FRANCE)}")

      println("\t comment(tw) = ${it.getDescription(Locale.TAIWAN)}")
      println("\t comment(en   ) = ${it.getDescription(Locale.ENGLISH)}")
//      println("\t comment(en_US) = ${it.getDescription(Locale.US)}")
//      println("\t comment(UK   ) = ${it.getDescription(Locale.UK)}") // en_GB
//      println("\t comment(簡) = ${it.getDescription(Locale.SIMPLIFIED_CHINESE)}")
//      println("\t comment(jp) = ${it.getDescription(Locale.JAPANESE)}")
//      println("\t comment(fr) = ${it.getDescription(Locale.FRANCE)}")
      println()
    }
  }

  //@Test
  fun testPrint() {
    val ruler = EssentialDignity.Ruler(Planet.SUN, ZodiacSign.ARIES)
    print(ruler)

    val exalt = EssentialDignity.Exaltation(Planet.JUPITER, ZodiacSign.CAPRICORN)
    print(exalt)

    val term = EssentialDignity.Term(Planet.JUPITER, 123.456)
    print(term)

    val face = EssentialDignity.Face(Planet.MARS, 359.9)
    print(face)

    val trip = EssentialDignity.Triplicity(Planet.MOON, ZodiacSign.GEMINI, DayNight.NIGHT)
    print(trip)

    val benMutRec = EssentialDignity.BeneficialMutualReception(Planet.SUN, Dignity.EXALTATION, Planet.MARS, Dignity.EXALTATION)
    print(benMutRec)
  }
}
