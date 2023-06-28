/*
 * @author smallufo
 * @date 2005/1/21
 * @time 上午 10:45:47
 */
package destiny.core.iching

import destiny.core.iching.Hexagram.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


class HexagramDefaultComparatorTest {
  // 輸出給 HexagramSimple
  @Test
  fun testOutput() {
    // map.put(new Boolean[]{true}, 1);
    val impl = HexagramDefaultComparator.instance
    for (hex in 1..64) {
      val sb = StringBuffer()

      sb.append("map.put(new Boolean[] {")

      val h = impl.getHexagram(hex)
      for (line in 1..6) {
        sb.append(h.getBoolean(line))
        sb.append(',')
      }
      sb.append("},")
      sb.append(hex)

      sb.append(");")
      println(sb)
    }

  }

  @Test
  fun testHexagramDefaultComparator() {
    val hexagramDatas =
      arrayOf(
        乾, 姤, 遯, 否, 觀, 剝, 晉, 大有,
        兌, 困, 萃, 咸, 蹇, 謙, 小過, 歸妹,
        離, 旅, 鼎, 未濟, 蒙, 渙, 訟, 同人,
        震, 豫, 解, 恆, 升, 井, 大過, 隨,
        巽, 小畜, 家人, 益, 無妄, 噬嗑, 頤, 蠱,
        坎, 節, 屯, 既濟, 革, 豐, 明夷, 師,
        艮, 賁, 大畜, 損, 睽, 履, 中孚, 漸,
        坤, 復, 臨, 泰, 大壯, 夬, 需, 比
      )

    val expected =
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

    Arrays.sort(hexagramDatas, HexagramDefaultComparator.instance)
    for (i in hexagramDatas.indices) {
      assertSame(expected[i], hexagramDatas[i])
    }
  }

  @Test
  fun testHexagramSequence() {
    val sequenceImpl = HexagramDefaultComparator.instance

    assertEquals(乾, Hexagram.of(1, sequenceImpl))
    assertEquals(坤, Hexagram.of(2, sequenceImpl))
    assertEquals(屯, Hexagram.of(3, sequenceImpl))
    assertEquals(蒙, Hexagram.of(4, sequenceImpl))
    assertEquals(需, Hexagram.of(5, sequenceImpl))
    assertEquals(訟, Hexagram.of(6, sequenceImpl))
    assertEquals(師, Hexagram.of(7, sequenceImpl))
  }

  @Test
  fun testHexagramSequenceWithDefault() {
    assertEquals(乾, Hexagram.of(1))
    assertEquals(坤, Hexagram.of(2))
    assertEquals(屯, Hexagram.of(3))
    assertEquals(蒙, Hexagram.of(4))
    assertEquals(需, Hexagram.of(5))
    assertEquals(訟, Hexagram.of(6))
    assertEquals(師, Hexagram.of(7))
  }

  @Test
  fun testGetIndex() {
    val impl = HexagramDefaultComparator.instance

    assertEquals(1, impl.getIndex(乾).toLong())
    assertEquals(2, impl.getIndex(坤).toLong())
    assertEquals(3, impl.getIndex(屯).toLong())
    assertEquals(4, impl.getIndex(蒙).toLong())
    assertEquals(5, impl.getIndex(需).toLong())
    assertEquals(6, impl.getIndex(訟).toLong())
    assertEquals(7, impl.getIndex(師).toLong())

    assertEquals(8, impl.getIndex(比).toLong())
    assertEquals(9, impl.getIndex(小畜).toLong())
    assertEquals(10, impl.getIndex(履).toLong())
    assertEquals(11, impl.getIndex(泰).toLong())
    assertEquals(12, impl.getIndex(否).toLong())

    assertEquals(13, impl.getIndex(同人).toLong())
    assertEquals(14, impl.getIndex(大有).toLong())
    assertEquals(15, impl.getIndex(謙).toLong())
    assertEquals(16, impl.getIndex(豫).toLong())
    assertEquals(17, impl.getIndex(隨).toLong())

    assertEquals(18, impl.getIndex(蠱).toLong())
    assertEquals(19, impl.getIndex(臨).toLong())
    assertEquals(20, impl.getIndex(觀).toLong())
    assertEquals(21, impl.getIndex(噬嗑).toLong())
    assertEquals(22, impl.getIndex(賁).toLong())

    assertEquals(23, impl.getIndex(剝).toLong())
    assertEquals(24, impl.getIndex(復).toLong())
    assertEquals(25, impl.getIndex(無妄).toLong())
    assertEquals(26, impl.getIndex(大畜).toLong())
    assertEquals(27, impl.getIndex(頤).toLong())

    assertEquals(28, impl.getIndex(大過).toLong())
    assertEquals(29, impl.getIndex(坎).toLong())
    assertEquals(30, impl.getIndex(離).toLong())

    assertEquals(31, impl.getIndex(咸).toLong())
    assertEquals(32, impl.getIndex(恆).toLong())
    assertEquals(33, impl.getIndex(遯).toLong())
    assertEquals(34, impl.getIndex(大壯).toLong())

    assertEquals(35, impl.getIndex(晉).toLong())
    assertEquals(36, impl.getIndex(明夷).toLong())
    assertEquals(37, impl.getIndex(家人).toLong())
    assertEquals(38, impl.getIndex(睽).toLong())

    assertEquals(39, impl.getIndex(蹇).toLong())
    assertEquals(40, impl.getIndex(解).toLong())
    assertEquals(41, impl.getIndex(損).toLong())
    assertEquals(42, impl.getIndex(益).toLong())
    assertEquals(43, impl.getIndex(夬).toLong())
    assertEquals(44, impl.getIndex(姤).toLong())
    assertEquals(45, impl.getIndex(萃).toLong())

    assertEquals(46, impl.getIndex(升).toLong())
    assertEquals(47, impl.getIndex(困).toLong())
    assertEquals(48, impl.getIndex(井).toLong())
    assertEquals(49, impl.getIndex(革).toLong())
    assertEquals(50, impl.getIndex(鼎).toLong())
    assertEquals(51, impl.getIndex(震).toLong())

    assertEquals(52, impl.getIndex(艮).toLong())
    assertEquals(53, impl.getIndex(漸).toLong())
    assertEquals(54, impl.getIndex(歸妹).toLong())
    assertEquals(55, impl.getIndex(豐).toLong())
    assertEquals(56, impl.getIndex(旅).toLong())
    assertEquals(57, impl.getIndex(巽).toLong())

    assertEquals(58, impl.getIndex(兌).toLong())
    assertEquals(59, impl.getIndex(渙).toLong())
    assertEquals(60, impl.getIndex(節).toLong())
    assertEquals(61, impl.getIndex(中孚).toLong())

    assertEquals(62, impl.getIndex(小過).toLong())
    assertEquals(63, impl.getIndex(既濟).toLong())
    assertEquals(64, impl.getIndex(未濟).toLong())
  }

  @Test
  fun testSeq() {
    val comparator = HexagramDefaultComparator.instance

    assertSame(乾, comparator.getHexagram(1))
    assertSame(坤, comparator.getHexagram(2))
    assertSame(未濟, comparator.getHexagram(64))

    assertSame(既濟, comparator.getHexagram(-1))
    assertSame(未濟, comparator.getHexagram(0))
    assertSame(乾, comparator.getHexagram(65))
    assertSame(坤, comparator.getHexagram(66))

    assertSame(坤, comparator.getHexagram(-126))
  }

}
