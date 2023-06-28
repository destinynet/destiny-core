/*
 * @author smallufo
 * @date 2005/1/21
 * @time 上午 11:02:28
 */
package destiny.core.iching.divine

import destiny.core.iching.Hexagram
import destiny.core.iching.Hexagram.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class HexagramDivinationComparatorTest {
  @Test
  fun testHexagramDivinationComparator() {
    val hexagrams =
      arrayOf(
        乾, 坤, 屯, 蒙, 需, 訟, 師, 比,
        小畜, 履, 泰, 否, 同人, 大有, 謙, 豫,
        隨, 蠱, 臨, 觀, 噬嗑, 賁, 剝, 復,
        無妄, 大畜, 頤, 大過, 坎, 離, 咸, 恆,
        遯, 大壯, 晉, 明夷, 家人, 睽, 蹇, 解,
        損, 益, 夬, 姤, 萃, 升, 困, 井,
        革, 鼎, 震, 艮, 漸, 歸妹, 豐, 旅,
        巽, 兌, 渙, 節, 中孚, 小過, 既濟, 未濟
      )

    val expected =
      arrayOf(
        乾, 姤, 遯, 否, 觀, 剝, 晉, 大有,
        震, 豫, 解, 恆, 升, 井, 大過, 隨,
        坎, 節, 屯, 既濟, 革, 豐, 明夷, 師,
        艮, 賁, 大畜, 損, 睽, 履, 中孚, 漸,
        坤, 復, 臨, 泰, 大壯, 夬, 需, 比,
        巽, 小畜, 家人, 益, 無妄, 噬嗑, 頤, 蠱,
        離, 旅, 鼎, 未濟, 蒙, 渙, 訟, 同人,
        兌, 困, 萃, 咸, 蹇, 謙, 小過, 歸妹
      )

    Arrays.sort(hexagrams, HexagramDivinationComparator())
    for (i in hexagrams.indices) {
      assertSame(expected[i], hexagrams[i])
    }
  }

  @Test
  fun testHexagramSequence() {
    val sequence = HexagramDivinationComparator()

    assertEquals(乾, Hexagram.of(1, sequence))
    assertEquals(姤, Hexagram.of(2, sequence))
    assertEquals(遯, Hexagram.of(3, sequence))
    assertEquals(否, Hexagram.of(4, sequence))
    assertEquals(觀, Hexagram.of(5, sequence))
    assertEquals(剝, Hexagram.of(6, sequence))
    assertEquals(晉, Hexagram.of(7, sequence))
    assertEquals(大有, Hexagram.of(8, sequence))
  }


  @Test
  fun getHexagramFromIndex() {
    val sequence = HexagramDivinationComparator()

    assertEquals(乾, sequence.getHexagram(1))
    assertEquals(乾, sequence.getHexagram(65))
    assertEquals(歸妹, sequence.getHexagram(64))
    assertEquals(歸妹, sequence.getHexagram(0))
  }
}
