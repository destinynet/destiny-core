/**
 * Created by smallufo on 2026-10-28.
 */
package destiny.core.chinese

import destiny.tools.Lang
import destiny.tools.getTitle
import kotlin.test.Test
import kotlin.test.assertEquals

/** 天干地支的名稱：韓文讀音（2026-10-28）；其他語系與加檔之前一樣，就是列舉名 */
class StemBranchTitleTest {

  @Test
  fun `韓文讀音`() {
    assertEquals("갑을병정무기경신임계", Stem.entries.joinToString("") { it.getTitle(Lang.KO) })
    assertEquals("자축인묘진사오미신유술해", Branch.entries.joinToString("") { it.getTitle(Lang.KO) })
  }

  @Test
  fun `其他語系照舊是列舉名`() {
    listOf(Lang.ZH_TW, Lang.JA, Lang.EN).forEach { lang ->
      assertEquals(Stem.entries.joinToString("") { it.name }, Stem.entries.joinToString("") { it.getTitle(lang) }, "$lang")
      assertEquals(Branch.entries.joinToString("") { it.name }, Branch.entries.joinToString("") { it.getTitle(lang) }, "$lang")
    }
  }
}
