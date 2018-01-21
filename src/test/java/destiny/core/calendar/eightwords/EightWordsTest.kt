/**
 * @author smallufo
 * Created on 2006/6/11 at 下午 11:23:55
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.StemBranch
import org.junit.Assert.*
import kotlin.test.Test

class EightWordsTest {

  @Test
  fun testEqualsSame() {
    val expected = EightWords("甲子", "甲子", "甲子", "甲子")
    val actual = EightWords(StemBranch.get("甲子"), StemBranch.get("甲子"), StemBranch.get("甲子"), StemBranch.get("甲子"))
    assertEquals(expected, actual)
    assertNotSame(expected, actual) //不應該 same
  }

  /** 測試傳入 null 到 EightWords 的 constructor 中  */
  @Test
  fun testNullInConstructor1_year() {
    try {
      EightWords(null!!, StemBranch.get("乙丑"), StemBranch.get("丙寅"), StemBranch.get("丁卯"))
      fail("year's StemBranch is null , should throw RuntimeException !")
    } catch (expected: RuntimeException) {
      assertTrue(true)
    }

  }

  @Test
  fun testNullInConstructor1_month() {
    try {
      EightWords(StemBranch.get("甲子"), null!!, StemBranch.get("丙寅"), StemBranch.get("丁卯"))
      fail("month's StemBranch is null , should throw RuntimeException !")
    } catch (expected: RuntimeException) {
      assertTrue(true)
    }

  }


  @Test
  fun testNullInConstructor1_day() {
    try {
      EightWords(StemBranch.get("甲子"), StemBranch.get("乙丑"), null!!, StemBranch.get("丁卯"))
      fail("day's StemBranch is null , should throw RuntimeException !")
    } catch (expected: Exception) {
      assertTrue(true)
    }

  }

  @Test
  fun testNullInConstructor1_hour() {
    try {

      EightWords(StemBranch.get("甲子"), StemBranch.get("乙丑"), StemBranch.get("丙寅"), null!!)
      fail("day's StemBranch is null , should throw RuntimeException !")
    } catch (expected: RuntimeException) {
      assertTrue(true)
    }

  }


  /** 測試傳入 null 到 EightWords 的 constructor 中 , 與前例不同在於 , 前例傳的是干支 , 本例傳的是字串  */
  @Test
  fun testNullInConstructor2() {
    try {
      EightWords(null!!, "乙丑", "丙寅", "丁卯")
      fail("year's StemBranch is null , should throw RuntimeException !")
      EightWords("甲子", null!!, "丙寅", "丁卯")
      fail("month's StemBranch is null , should throw RuntimeException !")
      EightWords("甲子", "乙丑", null!!, "丁卯")
      fail("day's StemBranch is null , should throw RuntimeException !")
      EightWords("甲子", "乙丑", "丙寅", null!!)
      fail("hour's StemBranch is null , should throw RuntimeException !")
    } catch (expected: RuntimeException) {
      assertTrue(true)
    }

  }

  @Test
  fun testNullStemBranch() {
    val eightWordsFull =
      EightWords(StemBranch.get("甲子"), StemBranch.get("乙丑"), StemBranch.get("丙寅"), StemBranch.get("丁卯"))
    assertNotNull(eightWordsFull)
    assertNotNull(eightWordsFull.toString())
    println(eightWordsFull.toString())
  }
}
