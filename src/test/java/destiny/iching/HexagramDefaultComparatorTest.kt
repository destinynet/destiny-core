/*
 * @author smallufo
 * @date 2005/1/21
 * @time 上午 10:45:47
 */
package destiny.iching

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


class HexagramDefaultComparatorTest {
  // 輸出給 HexagramSimple
  @Test
  fun testOutput() {
    // map.put(new Boolean[]{true}, 1);
    val impl = HexagramDefaultComparator()
    for (hex in 1..64) {
      val sb = StringBuffer()

      sb.append("map.put(new Boolean[] {")

      val h = impl.getHexagram(hex)
      for (line in 1..6) {
        sb.append(h.getLine(line))
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
      arrayOf(Hexagram.乾, Hexagram.姤, Hexagram.遯, Hexagram.否, Hexagram.觀, Hexagram.剝, Hexagram.晉, Hexagram.大有,
              Hexagram.兌, Hexagram.困, Hexagram.萃, Hexagram.咸, Hexagram.蹇, Hexagram.謙, Hexagram.小過, Hexagram.歸妹,
              Hexagram.離, Hexagram.旅, Hexagram.鼎, Hexagram.未濟, Hexagram.蒙, Hexagram.渙, Hexagram.訟, Hexagram.同人,
              Hexagram.震, Hexagram.豫, Hexagram.解, Hexagram.恆, Hexagram.升, Hexagram.井, Hexagram.大過, Hexagram.隨,
              Hexagram.巽, Hexagram.小畜, Hexagram.家人, Hexagram.益, Hexagram.無妄, Hexagram.噬嗑, Hexagram.頤, Hexagram.蠱,
              Hexagram.坎, Hexagram.節, Hexagram.屯, Hexagram.既濟, Hexagram.革, Hexagram.豐, Hexagram.明夷, Hexagram.師,
              Hexagram.艮, Hexagram.賁, Hexagram.大畜, Hexagram.損, Hexagram.睽, Hexagram.履, Hexagram.中孚, Hexagram.漸,
              Hexagram.坤, Hexagram.復, Hexagram.臨, Hexagram.泰, Hexagram.大壯, Hexagram.夬, Hexagram.需, Hexagram.比)

    val expected =
      arrayOf(Hexagram.乾, Hexagram.坤, Hexagram.屯, Hexagram.蒙, Hexagram.需, Hexagram.訟, Hexagram.師, Hexagram.比,
              Hexagram.小畜, Hexagram.履, Hexagram.泰, Hexagram.否, Hexagram.同人, Hexagram.大有, Hexagram.謙, Hexagram.豫,
              Hexagram.隨, Hexagram.蠱, Hexagram.臨, Hexagram.觀, Hexagram.噬嗑, Hexagram.賁, Hexagram.剝, Hexagram.復,
              Hexagram.無妄, Hexagram.大畜, Hexagram.頤, Hexagram.大過, Hexagram.坎, Hexagram.離, Hexagram.咸, Hexagram.恆,
              Hexagram.遯, Hexagram.大壯, Hexagram.晉, Hexagram.明夷, Hexagram.家人, Hexagram.睽, Hexagram.蹇, Hexagram.解,
              Hexagram.損, Hexagram.益, Hexagram.夬, Hexagram.姤, Hexagram.萃, Hexagram.升, Hexagram.困, Hexagram.井,
              Hexagram.革, Hexagram.鼎, Hexagram.震, Hexagram.艮, Hexagram.漸, Hexagram.歸妹, Hexagram.豐, Hexagram.旅,
              Hexagram.巽, Hexagram.兌, Hexagram.渙, Hexagram.節, Hexagram.中孚, Hexagram.小過, Hexagram.既濟, Hexagram.未濟)

    Arrays.sort(hexagramDatas, HexagramDefaultComparator())
    for (i in hexagramDatas.indices) {
      assertSame(expected[i], hexagramDatas[i])
    }
  }

  @Test
  fun testHexagramSequence() {
    val sequenceImpl = HexagramDefaultComparator()

    assertEquals(Hexagram.乾, Hexagram.getHexagram(1, sequenceImpl))
    assertEquals(Hexagram.坤, Hexagram.getHexagram(2, sequenceImpl))
    assertEquals(Hexagram.屯, Hexagram.getHexagram(3, sequenceImpl))
    assertEquals(Hexagram.蒙, Hexagram.getHexagram(4, sequenceImpl))
    assertEquals(Hexagram.需, Hexagram.getHexagram(5, sequenceImpl))
    assertEquals(Hexagram.訟, Hexagram.getHexagram(6, sequenceImpl))
    assertEquals(Hexagram.師, Hexagram.getHexagram(7, sequenceImpl))
  }

  @Test
  fun testGetIndex() {
    val impl = HexagramDefaultComparator()

    assertEquals(1, impl.getIndex(Hexagram.乾).toLong())
    assertEquals(2, impl.getIndex(Hexagram.坤).toLong())
    assertEquals(3, impl.getIndex(Hexagram.屯).toLong())
    assertEquals(4, impl.getIndex(Hexagram.蒙).toLong())
    assertEquals(5, impl.getIndex(Hexagram.需).toLong())
    assertEquals(6, impl.getIndex(Hexagram.訟).toLong())
    assertEquals(7, impl.getIndex(Hexagram.師).toLong())

    assertEquals(8, impl.getIndex(Hexagram.比).toLong())
    assertEquals(9, impl.getIndex(Hexagram.小畜).toLong())
    assertEquals(10, impl.getIndex(Hexagram.履).toLong())
    assertEquals(11, impl.getIndex(Hexagram.泰).toLong())
    assertEquals(12, impl.getIndex(Hexagram.否).toLong())

    assertEquals(13, impl.getIndex(Hexagram.同人).toLong())
    assertEquals(14, impl.getIndex(Hexagram.大有).toLong())
    assertEquals(15, impl.getIndex(Hexagram.謙).toLong())
    assertEquals(16, impl.getIndex(Hexagram.豫).toLong())
    assertEquals(17, impl.getIndex(Hexagram.隨).toLong())

    assertEquals(18, impl.getIndex(Hexagram.蠱).toLong())
    assertEquals(19, impl.getIndex(Hexagram.臨).toLong())
    assertEquals(20, impl.getIndex(Hexagram.觀).toLong())
    assertEquals(21, impl.getIndex(Hexagram.噬嗑).toLong())
    assertEquals(22, impl.getIndex(Hexagram.賁).toLong())

    assertEquals(23, impl.getIndex(Hexagram.剝).toLong())
    assertEquals(24, impl.getIndex(Hexagram.復).toLong())
    assertEquals(25, impl.getIndex(Hexagram.無妄).toLong())
    assertEquals(26, impl.getIndex(Hexagram.大畜).toLong())
    assertEquals(27, impl.getIndex(Hexagram.頤).toLong())

    assertEquals(28, impl.getIndex(Hexagram.大過).toLong())
    assertEquals(29, impl.getIndex(Hexagram.坎).toLong())
    assertEquals(30, impl.getIndex(Hexagram.離).toLong())

    assertEquals(31, impl.getIndex(Hexagram.咸).toLong())
    assertEquals(32, impl.getIndex(Hexagram.恆).toLong())
    assertEquals(33, impl.getIndex(Hexagram.遯).toLong())
    assertEquals(34, impl.getIndex(Hexagram.大壯).toLong())

    assertEquals(35, impl.getIndex(Hexagram.晉).toLong())
    assertEquals(36, impl.getIndex(Hexagram.明夷).toLong())
    assertEquals(37, impl.getIndex(Hexagram.家人).toLong())
    assertEquals(38, impl.getIndex(Hexagram.睽).toLong())

    assertEquals(39, impl.getIndex(Hexagram.蹇).toLong())
    assertEquals(40, impl.getIndex(Hexagram.解).toLong())
    assertEquals(41, impl.getIndex(Hexagram.損).toLong())
    assertEquals(42, impl.getIndex(Hexagram.益).toLong())
    assertEquals(43, impl.getIndex(Hexagram.夬).toLong())
    assertEquals(44, impl.getIndex(Hexagram.姤).toLong())
    assertEquals(45, impl.getIndex(Hexagram.萃).toLong())

    assertEquals(46, impl.getIndex(Hexagram.升).toLong())
    assertEquals(47, impl.getIndex(Hexagram.困).toLong())
    assertEquals(48, impl.getIndex(Hexagram.井).toLong())
    assertEquals(49, impl.getIndex(Hexagram.革).toLong())
    assertEquals(50, impl.getIndex(Hexagram.鼎).toLong())
    assertEquals(51, impl.getIndex(Hexagram.震).toLong())

    assertEquals(52, impl.getIndex(Hexagram.艮).toLong())
    assertEquals(53, impl.getIndex(Hexagram.漸).toLong())
    assertEquals(54, impl.getIndex(Hexagram.歸妹).toLong())
    assertEquals(55, impl.getIndex(Hexagram.豐).toLong())
    assertEquals(56, impl.getIndex(Hexagram.旅).toLong())
    assertEquals(57, impl.getIndex(Hexagram.巽).toLong())

    assertEquals(58, impl.getIndex(Hexagram.兌).toLong())
    assertEquals(59, impl.getIndex(Hexagram.渙).toLong())
    assertEquals(60, impl.getIndex(Hexagram.節).toLong())
    assertEquals(61, impl.getIndex(Hexagram.中孚).toLong())

    assertEquals(62, impl.getIndex(Hexagram.小過).toLong())
    assertEquals(63, impl.getIndex(Hexagram.既濟).toLong())
    assertEquals(64, impl.getIndex(Hexagram.未濟).toLong())
  }

  @Test
  fun testGetHexagram() {
    val impl = HexagramDefaultComparator()
    assertSame(Hexagram.坤, impl.getHexagram(-126))
  }
}
