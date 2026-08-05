/**
 * Created by smallufo on 2026-08-06.
 */
package destiny.tools

import destiny.core.Descriptive
import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacSign
import destiny.core.calendar.eightwords.Direction
import destiny.core.chinese.FortuneOutput
import destiny.core.toString
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 階段 2：[ILocaleString.getTitle] 的參數由 `java.util.Locale` 換成 [Lang]。
 *
 * 這是本次 KMP 遷移**第一個改動公開簽章**的步驟。安全網有兩層：
 *
 *  1. 本測試釘住新舊兩種呼叫法**結果相同**
 *  2. destiny-core 既有的 2105 個測試絕大多數是用 `Locale` 呼叫的 ——
 *     它們全部改走 [ILocaleString.getTitle] 的 `Locale` 多載（橋接），
 *     等於一次大規模的等價驗證
 */
class ILocaleStringLangTest {

  private val subjects: List<Descriptive> = listOf(FortuneOutput.虛歲, FortuneOutput.實歲, Direction.L2R)

  @Test
  fun `Descriptive 的 getTitle 吃 Lang`() {
    assertEquals("左至右", Direction.L2R.getTitle(Lang.ZH_TW))
    assertEquals("虛歲", FortuneOutput.虛歲.getTitle(Lang.ZH_TW))
  }

  /** 介面內建的 `Locale` 多載 —— 下游 16 個 repo 的呼叫端因此一行都不必改 */
  @Test
  fun `Locale 橋接與 Lang 等價`() {
    for (locale in listOf(Locale.TAIWAN, Locale.ENGLISH, Locale.JAPANESE, Locale.CHINA, Locale.ROOT)) {
      for (s in subjects) {
        assertEquals(s.getTitle(locale.toLang()), s.getTitle(locale), "$s @ $locale (title)")
        assertEquals(s.getDescription(locale.toLang()), s.getDescription(locale), "$s @ $locale (desc)")
      }
    }
  }

  /** 不帶參數時走 [Lang.DEFAULT]（zh_TW），與先前的 `defaultLocale` 一致 */
  @Test
  fun `預設參數為 Lang DEFAULT`() {
    assertEquals(Direction.L2R.getTitle(Lang.ZH_TW), Direction.L2R.getTitle())
  }

  /** enum 走 `Extensions.kt` 的 reified 擴充（8 個 repo、116 檔 import 的最熱路徑） */
  @Test
  fun `reified enum 擴充的 Lang 版與 Locale 版等價`() {
    for (locale in listOf(Locale.TAIWAN, Locale.ENGLISH, Locale.JAPANESE)) {
      assertEquals(
        ZodiacSign.ARIES.getTitle(locale.toLang()),
        ZodiacSign.ARIES.getTitle(locale),
        "ZodiacSign.ARIES @ $locale"
      )
    }
  }

  /**
   * `Point` 本身不實作 [ILocaleString]（它是 `Planet` / `ZStar` 的共同基底），
   * 對外的入口是 `Point.toString(lang)` extension。
   */
  @Test
  fun `Point toString 吃 Lang 且與 Locale 版等價`() {
    assertEquals("火星", Planet.MARS.toString(Lang.ZH_TW))
    assertEquals("Mars", Planet.MARS.toString(Lang.EN))

    for (locale in listOf(Locale.TAIWAN, Locale.ENGLISH, Locale.JAPANESE)) {
      assertEquals(Planet.MARS.toString(locale.toLang()), Planet.MARS.toString(locale), "Planet.MARS @ $locale")
    }
  }
}
