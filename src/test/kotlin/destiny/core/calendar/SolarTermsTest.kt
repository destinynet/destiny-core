/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:29:46
 */
package destiny.core.calendar

import destiny.core.calendar.SolarTerms.*
import destiny.core.calendar.SolarTerms.Companion.getIndex
import destiny.core.chinese.Branch.*
import kotlin.test.*


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

  @Test
  fun testMajor() {
    // 節（奇數索引）
    assertTrue(立春.major)
    assertFalse(雨水.major)
    assertTrue(驚蟄.major)
    assertFalse(春分.major)
    assertTrue(清明.major)
    assertFalse(穀雨.major)
    assertTrue(立夏.major)
    assertFalse(小滿.major)
    assertTrue(芒種.major)
    assertFalse(夏至.major)
    assertTrue(小暑.major)
    assertFalse(大暑.major)
    assertTrue(立秋.major)
    assertFalse(處暑.major)
    assertTrue(白露.major)
    assertFalse(秋分.major)
    assertTrue(寒露.major)
    assertFalse(霜降.major)
    assertTrue(立冬.major)
    assertFalse(小雪.major)
    assertTrue(大雪.major)
    assertFalse(冬至.major)
    assertTrue(小寒.major)
    assertFalse(大寒.major)
  }

  /** 測試根據地支取得節氣列表 */
  @Test
  fun testOfBranch() {
    assertEquals(listOf(立春, 雨水), SolarTerms.of(寅))
    assertEquals(listOf(驚蟄, 春分), SolarTerms.of(卯))
    assertEquals(listOf(清明, 穀雨), SolarTerms.of(辰))
    assertEquals(listOf(立夏, 小滿), SolarTerms.of(巳))
    assertEquals(listOf(芒種, 夏至), SolarTerms.of(午))
    assertEquals(listOf(小暑, 大暑), SolarTerms.of(未))
    assertEquals(listOf(立秋, 處暑), SolarTerms.of(申))
    assertEquals(listOf(白露, 秋分), SolarTerms.of(酉))
    assertEquals(listOf(寒露, 霜降), SolarTerms.of(戌))
    assertEquals(listOf(立冬, 小雪), SolarTerms.of(亥))
    assertEquals(listOf(大雪, 冬至), SolarTerms.of(子))
    assertEquals(listOf(小寒, 大寒), SolarTerms.of(丑))
  }

  /** 測試從黃經度數取得節氣 */
  @Test
  fun testGetFromDegree() {
    assertEquals(春分, SolarTerms.getFromDegree(0.0))
    assertEquals(清明, SolarTerms.getFromDegree(15.0))
    assertEquals(穀雨, SolarTerms.getFromDegree(30.0))
    assertEquals(立夏, SolarTerms.getFromDegree(45.0))
    assertEquals(小滿, SolarTerms.getFromDegree(60.0))
    assertEquals(芒種, SolarTerms.getFromDegree(75.0))
    assertEquals(夏至, SolarTerms.getFromDegree(90.0))
    assertEquals(小暑, SolarTerms.getFromDegree(105.0))
    assertEquals(大暑, SolarTerms.getFromDegree(120.0))
    assertEquals(立秋, SolarTerms.getFromDegree(135.0))
    assertEquals(處暑, SolarTerms.getFromDegree(150.0))
    assertEquals(白露, SolarTerms.getFromDegree(165.0))
    assertEquals(秋分, SolarTerms.getFromDegree(180.0))
    assertEquals(寒露, SolarTerms.getFromDegree(195.0))
    assertEquals(霜降, SolarTerms.getFromDegree(210.0))
    assertEquals(立冬, SolarTerms.getFromDegree(225.0))
    assertEquals(小雪, SolarTerms.getFromDegree(240.0))
    assertEquals(大雪, SolarTerms.getFromDegree(255.0))
    assertEquals(冬至, SolarTerms.getFromDegree(270.0))
    assertEquals(小寒, SolarTerms.getFromDegree(285.0))
    assertEquals(大寒, SolarTerms.getFromDegree(300.0))
    assertEquals(立春, SolarTerms.getFromDegree(315.0))
    assertEquals(雨水, SolarTerms.getFromDegree(330.0))
    assertEquals(驚蟄, SolarTerms.getFromDegree(345.0))

    // 測試度數超過360的情況
    assertEquals(春分, SolarTerms.getFromDegree(360.0))
    assertEquals(清明, SolarTerms.getFromDegree(375.0))
    assertEquals(立春, SolarTerms.getFromDegree(675.0)) // 315 + 360

    // 測試負度數
    assertEquals(大寒, SolarTerms.getFromDegree(-60.0)) // 300 - 360
  }


  /** 測試取得上一個「節」、下一個「節」 */
  @Test
  fun testGetPrevNextMajorSolarTerms() {
    // 當前就是「節」的情況
    assertEquals(立春 to 驚蟄, SolarTerms.getPrevNextMajorSolarTerms(立春))
    assertEquals(驚蟄 to 清明, SolarTerms.getPrevNextMajorSolarTerms(驚蟄))
    assertEquals(清明 to 立夏, SolarTerms.getPrevNextMajorSolarTerms(清明))

    // 當前是「氣」的情況
    assertEquals(立春 to 驚蟄, SolarTerms.getPrevNextMajorSolarTerms(雨水))
    assertEquals(驚蟄 to 清明, SolarTerms.getPrevNextMajorSolarTerms(春分))
    assertEquals(清明 to 立夏, SolarTerms.getPrevNextMajorSolarTerms(穀雨))

    // 邊界情況
    assertEquals(小寒 to 立春, SolarTerms.getPrevNextMajorSolarTerms(大寒))
    assertEquals(大雪 to 小寒, SolarTerms.getPrevNextMajorSolarTerms(冬至))
  }

  /** 測試取得下一個「節」 */
  @Test
  fun testGetNextMajorSolarTerms() {
    // 順推情況
    // 當前是「節」
    assertEquals(驚蟄, SolarTerms.getNextMajorSolarTerms(立春, false))
    assertEquals(清明, SolarTerms.getNextMajorSolarTerms(驚蟄, false))
    assertEquals(立夏, SolarTerms.getNextMajorSolarTerms(清明, false))

    // 當前是「氣」
    assertEquals(驚蟄, SolarTerms.getNextMajorSolarTerms(雨水, false))
    assertEquals(清明, SolarTerms.getNextMajorSolarTerms(春分, false))
    assertEquals(立夏, SolarTerms.getNextMajorSolarTerms(穀雨, false))

    // 逆推情況
    // 當前是「節」
    assertEquals(立春, SolarTerms.getNextMajorSolarTerms(立春, true))
    assertEquals(驚蟄, SolarTerms.getNextMajorSolarTerms(驚蟄, true))
    assertEquals(清明, SolarTerms.getNextMajorSolarTerms(清明, true))

    // 當前是「氣」
    assertEquals(立春, SolarTerms.getNextMajorSolarTerms(雨水, true))
    assertEquals(驚蟄, SolarTerms.getNextMajorSolarTerms(春分, true))
    assertEquals(清明, SolarTerms.getNextMajorSolarTerms(穀雨, true))

    // 邊界情況測試
    assertEquals(立春, SolarTerms.getNextMajorSolarTerms(小寒, false))
    assertEquals(立春, SolarTerms.getNextMajorSolarTerms(大寒, false))
    assertEquals(小寒, SolarTerms.getNextMajorSolarTerms(大寒, true))
  }

  @Test
  fun testZodiacDegree() {
    assertEquals(315, 立春.zodiacDegree)
    assertEquals(330, 雨水.zodiacDegree)
    assertEquals(345, 驚蟄.zodiacDegree)
    assertEquals(0, 春分.zodiacDegree)
    assertEquals(15, 清明.zodiacDegree)
    assertEquals(30, 穀雨.zodiacDegree)
    assertEquals(45, 立夏.zodiacDegree)
    assertEquals(60, 小滿.zodiacDegree)
    assertEquals(75, 芒種.zodiacDegree)
    assertEquals(90, 夏至.zodiacDegree)
    assertEquals(105, 小暑.zodiacDegree)
    assertEquals(120, 大暑.zodiacDegree)
    assertEquals(135, 立秋.zodiacDegree)
    assertEquals(150, 處暑.zodiacDegree)
    assertEquals(165, 白露.zodiacDegree)
    assertEquals(180, 秋分.zodiacDegree)
    assertEquals(195, 寒露.zodiacDegree)
    assertEquals(210, 霜降.zodiacDegree)
    assertEquals(225, 立冬.zodiacDegree)
    assertEquals(240, 小雪.zodiacDegree)
    assertEquals(255, 大雪.zodiacDegree)
    assertEquals(270, 冬至.zodiacDegree)
    assertEquals(285, 小寒.zodiacDegree)
    assertEquals(300, 大寒.zodiacDegree)
  }
}
