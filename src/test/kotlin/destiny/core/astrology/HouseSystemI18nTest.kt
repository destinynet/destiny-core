/**
 * Created by Claude on 2026-08-15.
 *
 * [HouseSystem] 的名稱查表。
 *
 * 2026-08-15 之前它與 [Ayanamsa] 共用 `Astrology.properties`、key 慣例是
 * `HouseSystem.<常數>`，於是**全站其他列舉都能用的** `getTitle()` 對它會靜默退回列舉名
 * （`WHOLE_SIGN` 而不是 `Whole Sign`）—— 寫新程式的人不會發現自己走錯了路。
 * 現在改用 `HouseSystem.properties` 的 `<常數>.title`，兩條路徑同源。
 */
package destiny.core.astrology

import destiny.tools.Lang
import destiny.tools.getTitle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class HouseSystemI18nTest {

  /** 全站慣例的入口 —— 這條在改動前是紅的（會拿到 "PLACIDUS"） */
  @Test
  fun `getTitle 走得通`() {
    assertEquals("Placidus", HouseSystem.PLACIDUS.getTitle(Lang.ZH_TW))
    assertEquals("Whole Sign", HouseSystem.WHOLE_SIGN.getTitle(Lang.ZH_TW))
  }

  /** 既有呼叫端用的是 `toString(lang)`／`asLocaleString()`，必須與上面同源 */
  @Test
  fun `既有入口與 getTitle 同源`() {
    HouseSystem.entries.forEach {
      assertEquals(it.getTitle(Lang.ZH_TW), it.toString(Lang.ZH_TW), "$it 的兩條路徑不同源")
      assertEquals(it.getTitle(Lang.ZH_TW), it.asLocaleString().getTitle(Lang.ZH_TW))
    }
  }

  /**
   * 每一個都查得到 —— `getTitle` 查不到時退回列舉名，不會拋例外也不會有人發現。
   * 分宮法是專有名詞，各語系相同，因此只有一份 bundle。
   */
  @Test
  fun `十三個分宮法都查得到名字`() {
    HouseSystem.entries.forEach {
      assertNotEquals(it.name, it.getTitle(Lang.ZH_TW), "${it.name} 在 bundle 裡沒有對應條目")
      assertNotEquals(it.name, it.getTitle(Lang.EN), "${it.name} 在 bundle 裡沒有對應條目（en）")
    }
  }
}
