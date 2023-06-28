/**
 * Created by smallufo on 2018-03-03.
 */
package destiny.core.fengshui

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class MountainYinYangEmptyImplTest {

  @Test
  fun getYinYang() {
    MountainYinYangEmptyImpl().run {
      assertTrue(getYinYang(Mountain.壬))
      assertFalse(getYinYang(Mountain.子))
      assertFalse(getYinYang(Mountain.癸))

      assertFalse(getYinYang(Mountain.丑))
      assertTrue(getYinYang(Mountain.艮))
      assertTrue(getYinYang(Mountain.寅))

      assertTrue(getYinYang(Mountain.甲))
      assertFalse(getYinYang(Mountain.卯))
      assertFalse(getYinYang(Mountain.乙))

      assertFalse(getYinYang(Mountain.辰))
      assertTrue(getYinYang(Mountain.巽))
      assertTrue(getYinYang(Mountain.巳))

      assertTrue(getYinYang(Mountain.丙))
      assertFalse(getYinYang(Mountain.午))
      assertFalse(getYinYang(Mountain.丁))

      assertFalse(getYinYang(Mountain.未))
      assertTrue(getYinYang(Mountain.坤))
      assertTrue(getYinYang(Mountain.申))

      assertTrue(getYinYang(Mountain.庚))
      assertFalse(getYinYang(Mountain.酉))
      assertFalse(getYinYang(Mountain.辛))

      assertFalse(getYinYang(Mountain.戌))
      assertTrue(getYinYang(Mountain.乾))
      assertTrue(getYinYang(Mountain.亥))
    }
  }
}
