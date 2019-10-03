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

  val logger = KotlinLogging.logger {  }

  @Test
  fun `test name and description`() {
    val locale = Locale.TAIWAN
    EssentialDignity.Ruler(Planet.SUN, ZodiacSign.LEO).also {
      assertEquals("廟 (Ruler)" , RuleTranslator.getDescriptor(it).getTitle(locale))
      assertEquals("太陽 位於 獅子，為其 Ruler。" , RuleTranslator.getDescriptor(it).getDescription(locale))
    }

    EssentialDignity.Exaltation(Planet.SUN, ZodiacSign.ARIES).also {
      assertEquals("旺 (Exalt)" , RuleTranslator.getDescriptor(it).getTitle(locale))
      assertEquals("太陽 位於 牡羊，為其 Exaltation。" , RuleTranslator.getDescriptor(it).getDescription(locale))
    }

    EssentialDignity.Triplicity(Planet.SUN, ZodiacSign.ARIES , DayNight.DAY).also {
      assertEquals("三分主星 (Triplicity)" , RuleTranslator.getDescriptor(it).getTitle(locale))
      assertEquals("太陽 位於 牡羊，為其 DAY 之 Triplicity。" , RuleTranslator.getDescriptor(it).getDescription(locale))
    }

  }


  private fun print(rule: EssentialDignity) {

    RuleTranslator.getDescriptor(rule).also {
      logger.info("{} : {}" , it , it.javaClass.name)
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

    val benMutRec = EssentialDignity.BeneficialMutualReception(Planet.SUN , Dignity.EXALTATION , Planet.MARS , Dignity.EXALTATION)
    print(benMutRec)
  }
}