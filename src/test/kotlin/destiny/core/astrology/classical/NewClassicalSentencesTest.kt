/**
 * Created by smallufo on 2026-11-04.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.Aspect
import destiny.core.astrology.Planet
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.classical.rules.PatternTranslator
import destiny.core.astrology.classical.rules.participants
import io.mockk.mockk
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** 顯現、野生、月亮的離與合、燃燒路的判斷層與三語系句子 */
class NewClassicalSentencesTest {

  private fun describe(m: Misc, locale: Locale) = PatternTranslator.getDescriptor(m).getDescription(locale)

  /** 三語系都有自己的句子（沒退回標題），而且沒有殘留的 {n} */
  private fun assertSentences(m: Misc): Triple<String, String, String> {
    val zh = describe(m, Locale.TAIWAN)
    val en = describe(m, Locale.ENGLISH)
    val ja = describe(m, Locale.JAPANESE)
    listOf(zh, en, ja).forEach { s ->
      assertTrue(s.length > 20, "退回標題了：$s")
      assertFalse(Regex("""\{\d}""").containsMatchIn(s), "殘留佔位符：$s")
    }
    return Triple(zh, en, ja)
  }

  @Test
  fun `顯現 四種轉折`() {
    val retro = Misc.Phasis(MERCURY, Phasis(MERCURY, Phasis.Type.STATION_RETROGRADE, 4.13, 231.0.toZodiacDegree(), false))
    val (zh, en, ja) = assertSentences(retro)
    assertTrue("水星 在出生後 4.1 天停滯轉逆" in zh, zh)
    assertTrue("Mercury stations retrograde 4.1 days after birth" in en, en)
    assertTrue("出生の4.1日後" in ja, ja)

    val direct = Misc.Phasis(MERCURY, Phasis(MERCURY, Phasis.Type.STATION_DIRECT, -2.5, 215.0.toZodiacDegree(), true))
    assertTrue("出生前 2.5 天停滯轉順" in assertSentences(direct).first)

    val emerging = Misc.Phasis(VENUS, Phasis(VENUS, Phasis.Type.EMERGING, 1.0, 10.0.toZodiacDegree(), true))
    val (zhE, enE, _) = assertSentences(emerging)
    assertTrue("離開太陽光束" in zhE && "晨星，在太陽東方" in zhE, zhE)
    assertTrue("emerges from the Sun's beams 1.0 days after birth, becoming visible as a morning star" in enE, enE)

    val sinking = Misc.Phasis(VENUS, Phasis(VENUS, Phasis.Type.SINKING, -1.6, 355.0.toZodiacDegree(), false))
    val (zhS, enS, _) = assertSentences(sinking)
    assertTrue("進入太陽光束" in zhS && "昏星" in zhS, zhS)
    assertTrue("sinks under the Sun's beams 1.6 days before birth, disappearing as an evening star" in enS, enS)
  }

  @Test
  fun `野生 依判法用不同句子`() {
    val (zh, en, _) = assertSentences(Misc.Feral(MARS, Feral(MARS, FeralOptions.Basis.BY_ORB)))
    assertTrue("火星 野生（feral）：與其他古典行星在容許度內都沒有相位" in zh, zh)
    assertTrue("Mars is feral: it makes no aspect within orb" in en, en)
    assertTrue("以星座計" in assertSentences(Misc.Feral(MARS, Feral(MARS, FeralOptions.Basis.BY_SIGN))).first)
  }

  @Test
  fun `野生的參與者是全部古典行星`() {
    // 只知日期時月亮位置不可信 ⇒ 任何一顆的「沒有相位」都判不準，整筆要拿掉
    assertEquals(Planet.classicalSet, Misc.Feral(MARS, Feral(MARS, FeralOptions.Basis.BY_ORB)).participants)
  }

  @Test
  fun `月亮的離與合 兩端都有 只有一端`() {
    val sep = LunarConnection.Link(SATURN, Aspect.CONJUNCTION, 15.0, 1.15)
    val app = LunarConnection.Link(VENUS, Aspect.TRINE, 5.0, 0.38)
    val both = Misc.LunarConnection(MOON, LunarConnection(sep, app))
    val (zh, en, ja) = assertSentences(both)
    assertTrue("月亮最後離開 土星 的" in zh && "（1.2 天前），下一個迎上 金星 的" in zh && "（0.4 天後）" in zh, zh)
    assertTrue("The Moon last separated from a conjunction with Saturn (1.2 days ago) and next applies to a trine with Venus (in 0.4 days)" in en, en)
    assertTrue("土星との" in ja && "金星との" in ja, ja)
    assertEquals(setOf(MOON, SATURN, VENUS), both.participants)

    val (zhS, enS, _) = assertSentences(Misc.LunarConnection(MOON, LunarConnection(sep, null)))
    assertTrue("不再與其他星成相位" in zhS && "土星" in zhS, zhS)
    assertTrue("makes no further aspect" in enS, enS)
    val (zhA, enA, _) = assertSentences(Misc.LunarConnection(MOON, LunarConnection(null, app)))
    // 母音開頭的相位用 an
    val opp = LunarConnection.Link(MARS, Aspect.OPPOSITION, 1.0, 0.1)
    assertTrue("separated from an opposition with Mars" in describe(Misc.LunarConnection(MOON, LunarConnection(opp, null)), Locale.ENGLISH))
    assertTrue("沒有離開的相位，下一個迎上 金星 的" in zhA, zhA)
    assertTrue("next applies to a trine with Venus" in enA, enA)
  }

  @Test
  fun `燃燒路`() {
    val (zh, en, ja) = assertSentences(Misc.ViaCombusta(MOON, ViaCombusta(MOON, 200.0.toZodiacDegree(), 195.0, 225.0)))
    assertTrue("月亮 在燃燒路" in zh, zh)
    assertTrue("Moon is in the via combusta" in en, en)
    assertTrue("ヴィア・コンバスタ" in ja, ja)
  }

  @Test
  fun `有 Impl 才列進預設格局 並由工廠判出`() {
    fun ctx(feral: IFeral? = null, lunar: ILunarConnection? = null, via: IViaCombusta? = null, phasis: IPhasis? = null) = ClassicalPatternContext(
      RulerPtolemyImpl, mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      phasisImpl = phasis, feralImpl = feral, lunarConnectionImpl = lunar, viaCombustaImpl = via,
    )
    val none = ctx()
    assertTrue(listOf(none.phasis, none.feral, none.lunarConnection, none.viaCombusta).none { it in none.defaultFactories })

    val all = ctx(FeralImpl(), LunarConnectionImpl(), ViaCombustaImpl(), mockk(relaxed = true))
    assertTrue(listOf(all.phasis, all.feral, all.lunarConnection, all.viaCombusta).all { it in all.defaultFactories })
  }
}
