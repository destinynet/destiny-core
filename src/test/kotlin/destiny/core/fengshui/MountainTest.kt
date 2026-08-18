/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.core.fengshui

import destiny.core.fengshui.Mountain.子
import destiny.core.fengshui.Mountain.午
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class MountainTest {

  /**
   * 24 山，對山即為羅盤上正對面的那一山（index 相差 12）。
   * 原本這裡只是把每一山的 index 與 opposite 用 `logger.trace` 印出來 ——
   * 連 trace level 都預設關閉，等於什麼都沒發生。
   */
  @Test
  fun opposite() {
    assertEquals(24, Mountain.entries.size)
    assertSame(午, 子.opposite)
    assertSame(子, 午.opposite)

    Mountain.entries.forEach { mnt ->
      // 對山的 index 相差 12（繞過 24 取餘）
      assertEquals((mnt.index + 12) % 24, mnt.opposite.index, "$mnt 的對山")
      // 對山的對山即為自己；且沒有任何一山以自己為對山
      assertSame(mnt, mnt.opposite.opposite, "$mnt")
      assertNotEquals(mnt, mnt.opposite)
    }

    // index 與宣告順序一致
    assertEquals((0..23).toList(), Mountain.entries.map { it.index })
  }
}
