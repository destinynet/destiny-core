/**
 * Created by smallufo on 2017-12-19.
 */
package destiny.core.astrology.classical.rules

import destiny.core.DayNight
import destiny.core.astrology.Aspect.OPPOSITION
import destiny.core.astrology.Aspect.TRINE
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.LunarNode
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.ZodiacSign.*
import destiny.core.astrology.classical.Dignity.*
import destiny.core.astrology.classical.rules.AccidentalDignity.*
import destiny.core.astrology.classical.rules.AccidentalDignity.Occidental
import destiny.core.astrology.classical.rules.AccidentalDignity.Oriental
import destiny.core.astrology.classical.rules.Debility.*
import destiny.core.astrology.classical.rules.Debility.MutualDeception
import destiny.core.chinese.YinYang
import mu.KotlinLogging
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class PatternTranslatorKtTest {

  val logger = KotlinLogging.logger { }

  private val IPlanetPattern.title: String
    get() = PatternTranslator.getDescriptor(this).getTitle(Locale.TAIWAN)

  private val IPlanetPattern.twDesc: String
    get() = PatternTranslator.getDescriptor(this).getDescription(Locale.TAIWAN)

  private val IPlanetPattern.enDesc: String
    get() = PatternTranslator.getDescriptor(this).getDescription(Locale.ENGLISH)


  @Test
  fun `essentialDignities test name and description`() {
    EssentialDignity.Ruler(SUN, LEO).also {
      assertEquals("廟 (Ruler)", it.title)
      assertEquals("太陽 位於 獅子，為其 Ruler", it.twDesc)
    }

    EssentialDignity.Exaltation(SUN, ARIES).also {
      assertEquals("旺 (Exalt)", it.title)
      assertEquals("太陽 位於 牡羊，為其 Exaltation", it.twDesc)
    }

    EssentialDignity.Triplicity(SUN, ARIES, DayNight.DAY).also {
      assertEquals("三分主星 (Triplicity)", it.title)
      assertEquals("太陽 位於 牡羊，為其 DAY 之 Triplicity", it.twDesc)
    }

    EssentialDignity.Term(JUPITER, 6.toZodiacDegree()).also {
      assertEquals("界 (Term)", it.title)
      assertEquals("木星 位於其 Term ： 6.0", it.twDesc)
    }

    EssentialDignity.Face(SUN, 20.toZodiacDegree()).also {
      assertEquals("十度區間主星 (Face)", it.title)
      assertEquals("太陽 位於其 Chaldean decanate or face : 20.0", it.twDesc)
    }

    EssentialDignity.MutualReception(VENUS, CAPRICORN , RULER, SATURN, TAURUS , RULER).also {
      assertEquals("互容", it.title)
      assertEquals("金星 位於 摩羯，與其 廟 (RULER, 土星) 飛至 金牛，形成 廟廟互容。", it.twDesc)
      assertEquals("Venus is in Capricorn, with its Ruler (RULER, Saturn) flying to Taurus, forming a Ruler/Ruler mutual reception.", it.enDesc)
    }
    EssentialDignity.MutualReception(VENUS, CAPRICORN , EXALTATION, MARS, PISCES , EXALTATION).also {
      assertEquals("互容", it.title)
      assertEquals("金星 位於 摩羯，與其 旺 (EXALTATION, 火星) 飛至 雙魚，形成 旺旺互容。", it.twDesc)
      assertEquals("Venus is in Capricorn, with its Exaltation (EXALTATION, Mars) flying to Pisces, forming a Exaltation/Exaltation mutual reception.", it.enDesc)
    }
    EssentialDignity.MutualReception(MOON, CAPRICORN , EXALTATION, SATURN, TAURUS, RULER).also {
      assertEquals("互容", it.title)
      assertEquals("月亮 位於 摩羯，與其 廟 (RULER, 土星) 飛至 金牛，形成 旺廟互容。", it.twDesc)
      assertEquals("Moon is in Capricorn, with its Ruler (RULER, Saturn) flying to Taurus, forming a Exaltation/Ruler mutual reception.", it.enDesc)
    }
  }


  @Test
  fun `accidentalDignities test name and description`() {
    House_1_10(SUN, 1).also {
      assertEquals("位於第一或第十宮", it.title)
      assertEquals("太陽 位於第 1 宮", it.twDesc)
    }

    House_4_7_11(SUN, 11).also {
      assertEquals("位於第四、七或是第十一宮", it.title)
      assertEquals("太陽 位於第 11 宮 (Good Daemon's) House", it.twDesc)
    }


    House_2_5(SUN, 2).also {
      assertEquals("位於第二或第五宮", it.title)
      assertEquals("太陽 位於第 2 宮", it.twDesc)
    }

    House_9(SUN).also {
      assertEquals("位於第九宮", it.title)
      assertEquals("太陽 位於第 9 宮", it.twDesc)
    }

    House_3(SUN).also {
      assertEquals("位於第三宮", it.title)
      assertEquals("太陽 位於第 3 宮", it.twDesc)
    }

    Direct(MERCURY).also {
      assertEquals("順行", it.title)
      assertEquals("水星 順行 (direct)", it.twDesc)
    }

    Swift(JUPITER).also {
      assertEquals("速行", it.title)
      assertEquals("木星 每日移動速度比平均值還快", it.twDesc)
    }

    Oriental(MERCURY).also {
      assertEquals("東出", it.title)
      assertEquals("水星 在太陽東邊", it.twDesc)
    }

    Occidental(MERCURY).also {
      assertEquals("西出", it.title)
      assertEquals("水星 在太陽西邊", it.twDesc)
    }

    Moon_Increase_Light.also {
      assertEquals("月增光", it.title)
      assertEquals("月亮 在太陽西邊（月增光/上弦月）", it.twDesc)
    }

    Free_Combustion(MERCURY).also {
      assertEquals("遠離太陽焦傷" , it.title)
      assertEquals("水星 遠離太陽焦傷" , it.twDesc)
    }

    Cazimi(VENUS).also {
      assertEquals("日心" , it.title)
      assertEquals("金星 進入太陽中心範圍 (Cazimi)" , it.twDesc)
    }

    Partile_Conj_Jupiter_Venus(JUPITER , VENUS).also {
      assertEquals("與金或木合相" , it.title)
      assertEquals("木星 與 金星 形成 partile 合" , it.twDesc)
    }

    Partile_Conj_North_Node(MERCURY , LunarNode.NORTH_MEAN).also {
      assertEquals("與北交合相" , it.title)
      assertEquals("水星 與 北交點 形成 partile 合" , it.twDesc)
    }

    Partile_Trine_Jupiter_Venus(MERCURY , VENUS).also {
      assertEquals("拱金或木" , it.title)
      assertEquals("水星 與 金星 形成 partile 三合" , it.twDesc)
    }

    Partile_Sextile_Jupiter_Venus(MERCURY , JUPITER).also {
      assertEquals("六合金或木" , it.title)
      assertEquals("水星 與 木星 形成 partile 六合" , it.twDesc)
    }

    Partile_Conj_Regulus(MERCURY).also {
      assertEquals("與軒轅十四合" , it.title)
      assertEquals("水星 與 軒轅十四 形成 partile 合" , it.twDesc)
    }

    Partile_Conj_Spica(MERCURY).also {
      assertEquals("與角宿一合" , it.title)
      assertEquals("水星 與 角宿一 形成 partile 合" , it.twDesc)
    }

    JoyHouse(MERCURY , 1).also {
      assertEquals("喜樂宮" , it.title)
      assertEquals("水星 位於第 1 宮，為其喜樂宮（Joy House）" , it.twDesc)
    }

    Hayz(SUN , DayNight.DAY , YinYang.陽 , ARIES).also {
      assertEquals("得時" , it.title)
      assertEquals("晝星 太陽 於白天在地平面上，落入陽性星座 牡羊，得時。" , it.twDesc)
      assertEquals("Hayz : Day star Sun is in the Yang sign Aries and above the horizon during the day." , it.enDesc)
    }
    Hayz(MOON, DayNight.NIGHT, YinYang.陰 , TAURUS).also {
      assertEquals("得時" , it.title)
      assertEquals("夜星 月亮 於夜晚在地平面上，落入陰性星座 金牛，得時。" , it.twDesc)
      assertEquals("Hayz : Night Star Moon is in the Ying sign Taurus and above the horizon at night." , it.enDesc)
    }

    Besieged_Jupiter_Venus(SUN).also {
      assertEquals("金木夾輔" , it.title)
      assertEquals("太陽 被 金星 及 木星 包夾 (善意 Besieged)。" , it.twDesc)
      assertEquals("Sun is besieged by Venus and Jupiter." , it.enDesc)
    }

    Translation_of_Light(SATURN, VENUS, JUPITER, 120.0, IPointAspectPattern.Type.APPLYING).also {
      assertEquals("傳遞光線" , it.title)
      assertEquals("土星 從 金星 傳遞光線到 木星 ，金星 與 木星 APPLYING 交角 120.0 度。" , it.twDesc)
      assertEquals("Saturn translates light from Venus to Jupiter. Venus and Jupiter are APPLYING aspect 120.0 degree." , it.enDesc)
    }
    Translation_of_Light(SATURN, VENUS, JUPITER, 120.0, IPointAspectPattern.Type.SEPARATING).also {
      assertEquals("傳遞光線", it.title)
      assertEquals("土星 從 金星 傳遞光線到 木星 ，金星 與 木星 SEPARATING 交角 120.0 度。", it.twDesc)
      assertEquals("Saturn translates light from Venus to Jupiter. Venus and Jupiter are SEPARATING aspect 120.0 degree.", it.enDesc)
    }

    Translation_of_Light(SATURN , VENUS , JUPITER , 80.0 , null).also {
      assertEquals("傳遞光線" , it.title)
      assertEquals("土星 從 金星 傳遞光線到 木星 ，金星 與 木星 交角 80.0 度，未形成相位。" , it.twDesc)
      assertEquals("Saturn translates light from Venus to Jupiter. Venus and Jupiter aspect 80.0 degree." , it.enDesc)
    }

    Collection_of_Light(SATURN , listOf(VENUS , JUPITER) , 120.0).also {
      assertEquals("收集光線" , it.title)
      assertEquals("土星 從 金星 與 木星 收集光線。 金星 與 木星 交角 120.0 度。" , it.twDesc)
      assertEquals("Saturn collects light from Venus and Jupiter. Venus and Jupiter form an aspect of 120.0 degree." , it.enDesc)
    }

    Refrain_from_Mars_Saturn(VENUS , MARS , OPPOSITION).also {
      assertEquals("逃離火土" , it.title)
      assertEquals("金星 逃過了與 火星 形成 沖 (Refranation)" , it.twDesc)
    }
  }

  @Test
  fun `debilities test name and description`() {
    Detriment(SUN, AQUARIUS).also {
      assertEquals("陷 (Detriment)" , it.title)
      assertEquals("太陽 位於 水瓶，為其 Detriment" , it.twDesc)
    }

    Fall(SUN , LIBRA).also {
      assertEquals("落 (Fall)" , it.title)
      assertEquals("太陽 位於 天秤，為其 Fall" , it.twDesc)
    }

    Peregrine(MERCURY).also {
      assertEquals("漂泊" , it.title)
      assertEquals("水星 處於 漂泊、茫游、外出狀態 (Peregrine)" , it.twDesc)
    }

    House_12(SUN).also {
      assertEquals("位於十二宮" , it.title)
      assertEquals("太陽 位於十二宮 (of the Evil Demon)" , it.twDesc)
    }

    House_6_8(SUN , 6).also {
      assertEquals("位於第六或第八宮" , it.title)
      assertEquals("太陽 位於第 6 宮" , it.twDesc)
    }

    Retrograde(SATURN).also {
      assertEquals("逆行" , it.title)
      assertEquals("土星 逆行" , it.twDesc)
    }

    Slower(JUPITER).also {
      assertEquals("慢行" , it.title)
      assertEquals("木星 每日移動速度比平均值還慢" , it.twDesc)
    }

    Debility.Occidental(MARS).also {
      assertEquals("西出" , it.title)
      assertEquals("火星 在太陽西邊" , it.twDesc)
    }

    Debility.Oriental(VENUS).also {
      assertEquals("東出" , it.title)
      assertEquals("金星 在太陽東邊" , it.twDesc)
    }

    Moon_Decrease_Light.also {
      assertEquals("月減光" , it.title)
      assertEquals("月亮 在太陽東邊（月減光/下弦月）" , it.twDesc)
    }

    Combustion(MERCURY).also {
      assertEquals("被太陽焦傷" , it.title)
      assertEquals("水星 被太陽焦傷 (Combustion) , 17分 到 8.5 度內" , it.twDesc)
    }

    Sunbeam(MERCURY).also {
      assertEquals("被太陽曬傷" , it.title)
      assertEquals("水星 被太陽曬傷 (Sunbeam) , 8.5 到 17 度內" , it.twDesc)
    }

    Partile_Conj_Mars_Saturn(MERCURY , MARS).also {
      assertEquals("與火或土合" , it.title)
      assertEquals("水星 與 火星 形成 partile 合" , it.twDesc)
    }

    Partile_Conj_South_Node(MERCURY).also {
      assertEquals("與南交合相" , it.title)
      assertEquals("水星 與 南交點 形成 partile 合" , it.twDesc)
    }

    Besieged_Mars_Saturn(MERCURY).also {
      assertEquals("火土夾制" , it.title)
      assertEquals("水星 被 火星 及 土星 夾制 (Besieged)。" , it.twDesc)
      assertEquals("Mercury is Besieged by Mars and Saturn." , it.enDesc)
    }

    Partile_Oppo_Mars_Saturn(MERCURY , MARS).also {
      assertEquals("與火或土對沖" , it.title)
      assertEquals("水星 與 火星 形成 partile 沖" , it.twDesc)
    }

    Partile_Square_Mars_Saturn(MERCURY , MARS).also {
      assertEquals("與火或土相刑" , it.title)
      assertEquals("水星 與 火星 形成 partile 刑" , it.twDesc)
    }

    Conj_Algol(MERCURY).also {
      assertEquals("與大陵五合相" , it.title)
      assertEquals("水星 與 大陵五 形成 合。" , it.twDesc)
      assertEquals("Mercury and Algol forms Conjunction." , it.enDesc)
    }

    Out_of_Sect(SUN , DayNight.NIGHT, YinYang.陰 , TAURUS).also {
      assertEquals("不得時" , it.title)
      assertEquals("晝星 太陽 於夜晚在地平面上，落入陰性星座 金牛，不得時。" , it.twDesc)
      assertEquals("Day Star Sun is in the Ying sign Taurus and above the horizon at night. Out of Sect." , it.enDesc)
    }
    Out_of_Sect(MOON, DayNight.DAY, YinYang.陽 , ARIES).also {
      assertEquals("不得時" , it.title)
      assertEquals("夜星 月亮 於白天在地平面上，落入陽性星座 牡羊，不得時。" , it.twDesc)
      assertEquals("Night Star Moon is in the Yang sign Aries and above the horizon during the day, Out of Sect." , it.enDesc)
    }

    Refrain_from_Venus_Jupiter(JUPITER , VENUS , TRINE).also {
      assertEquals("臨陣退縮" , it.title)
      assertEquals("木星 在與 金星 形成 三合 之前，臨陣退縮 (Refraining)。" , it.twDesc)
      assertEquals("Jupiter shrinks back from forming Trine with Venus. (Refraining)" , it.enDesc)
    }

    MutualDeception(MOON, ARIES, DETRIMENT, SATURN, CAPRICORN, FALL).also {
      assertEquals("互陷", it.title)
      assertEquals("月亮 位於 牡羊 , 與其 落 (土星) 飛至 摩羯 , 形成 陷落互陷", it.twDesc)
    }
  }
}
