/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:29:46
 */
package destiny.core.calendar

import destiny.core.calendar.SolarTerms.*
import destiny.core.calendar.SolarTerms.Companion.getIndex
import destiny.core.chinese.Branch.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


class SolarTermsTest {

  /** 節氣 -> 地支  */
  @Test
  fun testGetBranch() {
    assertSame(寅, 立春.branch)
    assertSame(寅, 雨水.branch)
    assertSame(卯, 驚蟄.branch)
    assertSame(卯, 春分.branch)
    assertSame(辰, 清明.branch)
    assertSame(辰, 穀雨.branch)
    assertSame(巳, 立夏.branch)
    assertSame(巳, 小滿.branch)
    assertSame(午, 芒種.branch)
    assertSame(午, 夏至.branch)
    assertSame(未, 小暑.branch)
    assertSame(未, 大暑.branch)
    assertSame(申, 立秋.branch)
    assertSame(申, 處暑.branch)
    assertSame(酉, 白露.branch)
    assertSame(酉, 秋分.branch)
    assertSame(戌, 寒露.branch)
    assertSame(戌, 霜降.branch)
    assertSame(亥, 立冬.branch)
    assertSame(亥, 小雪.branch)
    assertSame(子, 大雪.branch)
    assertSame(子, 冬至.branch)
    assertSame(丑, 小寒.branch)
    assertSame(丑, 大寒.branch)
  }

  @Test
  fun testGetIndex() {
    assertEquals(0, getIndex(立春).toLong())
    assertEquals(1, getIndex(雨水).toLong())
    assertEquals(2, getIndex(驚蟄).toLong())
    assertEquals(3, getIndex(春分).toLong())
    assertEquals(4, getIndex(清明).toLong())
    assertEquals(5, getIndex(穀雨).toLong())
    assertEquals(6, getIndex(立夏).toLong())
    assertEquals(7, getIndex(小滿).toLong())
    assertEquals(8, getIndex(芒種).toLong())
    assertEquals(9, getIndex(夏至).toLong())
    assertEquals(10, getIndex(小暑).toLong())
    assertEquals(11, getIndex(大暑).toLong())
    assertEquals(12, getIndex(立秋).toLong())
    assertEquals(13, getIndex(處暑).toLong())
    assertEquals(14, getIndex(白露).toLong())
    assertEquals(15, getIndex(秋分).toLong())
    assertEquals(16, getIndex(寒露).toLong())
    assertEquals(17, getIndex(霜降).toLong())
    assertEquals(18, getIndex(立冬).toLong())
    assertEquals(19, getIndex(小雪).toLong())
    assertEquals(20, getIndex(大雪).toLong())
    assertEquals(21, getIndex(冬至).toLong())
    assertEquals(22, getIndex(小寒).toLong())
    assertEquals(23, getIndex(大寒).toLong())

  }

  @Test
  fun testNext() {
    assertSame(雨水, 立春.next())
    assertSame(驚蟄, 雨水.next())
    assertSame(春分, 驚蟄.next())
    assertSame(清明, 春分.next())
    assertSame(穀雨, 清明.next())
    assertSame(立夏, 穀雨.next())
    assertSame(小滿, 立夏.next())
    assertSame(芒種, 小滿.next())
    assertSame(夏至, 芒種.next())
    assertSame(小暑, 夏至.next())
    assertSame(大暑, 小暑.next())
    assertSame(立秋, 大暑.next())
    assertSame(處暑, 立秋.next())
    assertSame(白露, 處暑.next())
    assertSame(秋分, 白露.next())
    assertSame(寒露, 秋分.next())
    assertSame(霜降, 寒露.next())
    assertSame(立冬, 霜降.next())
    assertSame(小雪, 立冬.next())
    assertSame(大雪, 小雪.next())
    assertSame(冬至, 大雪.next())
    assertSame(小寒, 冬至.next())
    assertSame(大寒, 小寒.next())
    assertSame(立春, 大寒.next())
  }


  @Test
  fun testPrevious() {
    assertSame(大寒, 立春.previous())
    assertSame(立春, 雨水.previous())
    assertSame(雨水, 驚蟄.previous())
    assertSame(驚蟄, 春分.previous())
    assertSame(春分, 清明.previous())
    assertSame(清明, 穀雨.previous())
    assertSame(穀雨, 立夏.previous())
    assertSame(立夏, 小滿.previous())
    assertSame(小滿, 芒種.previous())
    assertSame(芒種, 夏至.previous())
    assertSame(夏至, 小暑.previous())
    assertSame(小暑, 大暑.previous())
    assertSame(大暑, 立秋.previous())
    assertSame(立秋, 處暑.previous())
    assertSame(處暑, 白露.previous())
    assertSame(白露, 秋分.previous())
    assertSame(秋分, 寒露.previous())
    assertSame(寒露, 霜降.previous())
    assertSame(霜降, 立冬.previous())
    assertSame(立冬, 小雪.previous())
    assertSame(小雪, 大雪.previous())
    assertSame(大雪, 冬至.previous())
    assertSame(冬至, 小寒.previous())
    assertSame(小寒, 大寒.previous())
  }

  @Test
  fun testGet() {
    assertSame(立春, SolarTerms[-24])
    assertSame(大寒, SolarTerms[-1])
    assertSame(立春, SolarTerms[0])
    assertSame(雨水, SolarTerms[1])
    assertSame(驚蟄, SolarTerms[2])
    assertSame(春分, SolarTerms[3])
    assertSame(清明, SolarTerms[4])
    assertSame(穀雨, SolarTerms[5])
    assertSame(立夏, SolarTerms[6])
    assertSame(小滿, SolarTerms[7])
    assertSame(芒種, SolarTerms[8])
    assertSame(夏至, SolarTerms[9])
    assertSame(小暑, SolarTerms[10])
    assertSame(大暑, SolarTerms[11])
    assertSame(立秋, SolarTerms[12])
    assertSame(處暑, SolarTerms[13])
    assertSame(白露, SolarTerms[14])
    assertSame(秋分, SolarTerms[15])
    assertSame(寒露, SolarTerms[16])
    assertSame(霜降, SolarTerms[17])
    assertSame(立冬, SolarTerms[18])
    assertSame(小雪, SolarTerms[19])
    assertSame(大雪, SolarTerms[20])
    assertSame(冬至, SolarTerms[21])
    assertSame(小寒, SolarTerms[22])
    assertSame(大寒, SolarTerms[23])
    assertSame(立春, SolarTerms[24])

  }
}
