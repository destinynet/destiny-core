/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.fengshui

import destiny.fengshui.sanyuan.Mountain
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class MountainYinYangTrilogyImplTest {

  @Test
  fun testGetYinYang() {
    MountainYinYangTrilogyImpl().run {
      assertTrue(getYinYang(Mountain.壬))
      assertTrue(getYinYang(Mountain.子))
      assertTrue(getYinYang(Mountain.癸))

      assertFalse(getYinYang(Mountain.丑))
      assertFalse(getYinYang(Mountain.艮))
      assertTrue(getYinYang(Mountain.寅))

      assertTrue(getYinYang(Mountain.甲))
      assertFalse(getYinYang(Mountain.卯))
      assertTrue(getYinYang(Mountain.乙))

      assertTrue(getYinYang(Mountain.辰))
      assertFalse(getYinYang(Mountain.巽))
      assertFalse(getYinYang(Mountain.巳))

      assertFalse(getYinYang(Mountain.丙))
      assertTrue(getYinYang(Mountain.午))
      assertFalse(getYinYang(Mountain.丁))

      assertFalse(getYinYang(Mountain.未))
      assertTrue(getYinYang(Mountain.坤))
      assertTrue(getYinYang(Mountain.申))

      assertFalse(getYinYang(Mountain.庚))
      assertFalse(getYinYang(Mountain.酉))
      assertFalse(getYinYang(Mountain.辛))

      assertTrue(getYinYang(Mountain.戌))
      assertTrue(getYinYang(Mountain.乾))
      assertFalse(getYinYang(Mountain.亥))
    }
  }
}
