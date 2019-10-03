/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.astrology.classical.rules

import destiny.astrology.Aspect
import destiny.astrology.IAspectApplySeparate
import destiny.astrology.LunarNode
import destiny.astrology.Planet.*
import destiny.astrology.ZodiacSign
import destiny.astrology.classical.Dignity
import destiny.astrology.classical.rules.AccidentalDignity.*
import destiny.core.DayNight
import destiny.core.chinese.YinYang
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class RuleTranslatorKtTest {

  val logger = KotlinLogging.logger { }

  private val IPlanetPattern.title: String
    get() = ruleTranslator.getDescriptor(this).getTitle(Locale.TAIWAN)

  private val IPlanetPattern.description: String
    get() = ruleTranslator.getDescriptor(this).getDescription(Locale.TAIWAN)


  @Test
  fun `EssentialDignity test name and description`() {
    EssentialDignity.Ruler(SUN, ZodiacSign.LEO).also {
      assertEquals("廟 (Ruler)", it.title)
      assertEquals("太陽 位於 獅子，為其 Ruler。", it.description)
    }

    EssentialDignity.Exaltation(SUN, ZodiacSign.ARIES).also {
      assertEquals("旺 (Exalt)", it.title)
      assertEquals("太陽 位於 牡羊，為其 Exaltation。", it.description)
    }

    EssentialDignity.Triplicity(SUN, ZodiacSign.ARIES, DayNight.DAY).also {
      assertEquals("三分主星 (Triplicity)", it.title)
      assertEquals("太陽 位於 牡羊，為其 DAY 之 Triplicity。", it.description)
    }

    EssentialDignity.Term(JUPITER, 6.0).also {
      assertEquals("界 (Term)", it.title)
      assertEquals("木星 位於其 Term ： 6.0 。", it.description)
    }

    EssentialDignity.Face(SUN, 20.0).also {
      assertEquals("十度區間主星 (Face)", it.title)
      assertEquals("太陽 位於其 Chaldean decanate or face : 20.0。", it.description)
    }

    EssentialDignity.BeneficialMutualReception(SUN, Dignity.RULER, MARS, Dignity.EXALTATION).also {
      assertEquals("有利互容", it.title)
      assertEquals("太陽 與 火星 形成 廟旺互容。", it.description)
    }
  }


  @Test
  fun `AccidentalDignity test name and description`() {
    House_1_10(SUN, 1).also {
      assertEquals("位於第一或第十宮", it.title)
      assertEquals("太陽 位於第 1 宮", it.description)
    }

    House_4_7_11(SUN, 11).also {
      assertEquals("位於第四、七或是第十一宮", it.title)
      assertEquals("太陽 位於第 11 宮 (Good Daemon's) House", it.description)
    }


    House_2_5(SUN, 2).also {
      assertEquals("位於第二或第五宮", it.title)
      assertEquals("太陽 位於第 2 宮", it.description)
    }

    House_9(SUN).also {
      assertEquals("位於第九宮", it.title)
      assertEquals("太陽 位於第 9 宮", it.description)
    }

    House_3(SUN).also {
      assertEquals("位於第三宮", it.title)
      assertEquals("太陽 位於第 3 宮", it.description)
    }

    Direct(MERCURY).also {
      assertEquals("順行", it.title)
      assertEquals("水星 順行 (direct)", it.description)
    }

    Swift(JUPITER).also {
      assertEquals("速行", it.title)
      assertEquals("木星 每日移動速度比平均值還快", it.description)
    }

    Oriental(MERCURY).also {
      assertEquals("東出", it.title)
      assertEquals("水星 在太陽東邊", it.description)
    }

    Occidental(MERCURY).also {
      assertEquals("西出", it.title)
      assertEquals("水星 在太陽西邊", it.description)
    }

    Moon_Increase_Light.also {
      assertEquals("月增光", it.title)
      assertEquals("月亮 在太陽西邊（月增光/上弦月）", it.description)
    }

    Free_Combustion(MERCURY).also {
      assertEquals("遠離太陽焦傷" , it.title)
      assertEquals("水星 遠離太陽焦傷" , it.description)
    }

    Cazimi(VENUS).also {
      assertEquals("日心" , it.title)
      assertEquals("金星 進入太陽中心範圍 (Cazimi)" , it.description)
    }

    Partile_Conj_Jupiter_Venus(JUPITER , VENUS).also {
      assertEquals("與金或木合相" , it.title)
      assertEquals("木星 與 金星 形成 partile 合" , it.description)
    }

    Partile_Conj_North_Node(MERCURY , LunarNode.NORTH_MEAN).also {
      assertEquals("與北交合相" , it.title)
      assertEquals("水星 與 北交點 形成 partile 合" , it.description)
    }

    Partile_Trine_Jupiter_Venus(MERCURY , VENUS).also {
      assertEquals("拱金或木" , it.title)
      assertEquals("水星 與 金星 形成 partile 三合" , it.description)
    }

    Partile_Sextile_Jupiter_Venus(MERCURY , JUPITER).also {
      assertEquals("六合金或木" , it.title)
      assertEquals("水星 與 木星 形成 partile 六合" , it.description)
    }

    Partile_Conj_Regulus(MERCURY).also {
      assertEquals("與軒轅十四合" , it.title)
      assertEquals("水星 與 軒轅十四 形成 partile 合" , it.description)
    }

    Partile_Conj_Spica(MERCURY).also {
      assertEquals("與角宿一合" , it.title)
      assertEquals("水星 與 角宿一 形成 partile 合" , it.description)
    }

    JoyHouse(MERCURY , 1).also {
      assertEquals("喜樂宮" , it.title)
      assertEquals("水星 位於第 1 宮，為其喜樂宮（Joy House）" , it.description)
    }

    Hayz(SUN , DayNight.DAY , YinYang.陽 , ZodiacSign.ARIES).also {
      assertEquals("得時" , it.title)
      assertEquals("晝星 太陽 於白天在地平面上，落入陽性星座 牡羊，得時" , it.description)
    }
    Hayz(MOON, DayNight.NIGHT, YinYang.陰 , ZodiacSign.TAURUS).also {
      assertEquals("得時" , it.title)
      assertEquals("夜星 月亮 於夜晚在地平面上，落入陰性星座 金牛，得時" , it.description)
    }

    Besieged_Jupiter_Venus(SUN).also {
      assertEquals("金木夾輔" , it.title)
      assertEquals("太陽 被 金星 及 木星 包夾 (善意 Besieged)" , it.description)
    }

    Translation_of_Light(SATURN , VENUS , JUPITER , 120.0 , IAspectApplySeparate.AspectType.APPLYING).also {
      assertEquals("傳遞光線" , it.title)
      assertEquals("土星 從 金星 傳遞光線到 木星 ，金星 與 木星 交角 120.0 度，相位為 APPLYING" , it.description)
    }
    Translation_of_Light(SATURN , VENUS , JUPITER , 80.0 , null).also {
      assertEquals("傳遞光線" , it.title)
      assertEquals("土星 從 金星 傳遞光線到 木星 ，金星 與 木星 交角 80.0 度，未形成相位" , it.description)
    }

    Collection_of_Light(SATURN , listOf(VENUS , JUPITER) , 120.0).also {
      assertEquals("收集光線" , it.title)
      assertEquals("土星 從 金星 與 木星 收集光線。 金星 與 木星 交角 120.0 度" , it.description)
    }

    Refrain_from_Mars_Saturn(VENUS , MARS , Aspect.OPPOSITION).also {
      assertEquals("逃離火土" , it.title)
      assertEquals("金星 逃過了與 火星 形成 沖 (Refranation)" , it.description)
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
    val ruler = EssentialDignity.Ruler(SUN, ZodiacSign.ARIES)
    print(ruler)

    val exalt = EssentialDignity.Exaltation(JUPITER, ZodiacSign.CAPRICORN)
    print(exalt)

    val term = EssentialDignity.Term(JUPITER, 123.456)
    print(term)

    val face = EssentialDignity.Face(MARS, 359.9)
    print(face)

    val trip = EssentialDignity.Triplicity(MOON, ZodiacSign.GEMINI, DayNight.NIGHT)
    print(trip)

    val benMutRec = EssentialDignity.BeneficialMutualReception(SUN, Dignity.EXALTATION, MARS, Dignity.EXALTATION)
    print(benMutRec)
  }
}
