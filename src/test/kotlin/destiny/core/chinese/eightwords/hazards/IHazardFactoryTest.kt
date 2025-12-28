/**
 * Created by smallufo on 2022-07-19.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.Gender
import destiny.core.calendar.eightwords.EightWords
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


class IHazardFactoryTest {

  @Test
  fun testWithPreferredBook() {
    val ew = EightWords("甲午", "甲子", "甲子", "甲寅")

    // p千日關A (象吉通書) : 甲年 , 午時 , 不符合
    assertFalse(h千日關.test(ew, Gender.M, Book.象吉通書))

    // p千日關B (鰲頭通書) : 甲年 , 午時 , 不符合
    assertFalse(h千日關.test(ew, Gender.M, Book.鰲頭通書))

    // p千日關C (黃曆解秘) : 凡午年寅、申、巳、亥時生人，犯此忌三歲上高落低之患。 , 符合
    assertTrue(h千日關.test(ew, Gender.M, Book.黃曆解秘))
  }


  @Test
  fun testWithoutPreferredBook() {
    val ew1 = EightWords("甲午", "甲子", "甲子", "甲寅")

    // p千日關A (象吉通書) : 甲年 , 午時 , 不符合
    // p千日關B (鰲頭通書) : 甲年 , 午時 , 不符合
    // p千日關C (黃曆解秘) : 凡午年寅、申、巳、亥時生人，犯此忌三歲上高落低之患。 , 符合
    assertTrue(h千日關.test(ew1, Gender.M))

    // p千日關A,B,C 都不符合
    val ew2 = EightWords("甲午", "甲子", "甲子", "甲辰")
    assertFalse(h千日關.test(ew2, Gender.M))
  }

  @Nested
  inner class TestBook {
    /**
     * 百日關
     *
     * 寅申巳亥月：辰戌丑未時。
     * 子午卯酉月：寅申巳亥時。
     * 辰戌丑未月：子午卯酉時。
     */
    @Test
    fun bookWithinMatch() {
      val ew = EightWords("甲午", "甲寅", "甲子", "甲辰")
      assertTrue(h百日關.testBook(ew, Gender.M, Book.象吉通書)!!)
      assertTrue(h百日關.testBook(ew, Gender.M, Book.鰲頭通書)!!)
      assertTrue(h百日關.testBook(ew, Gender.M, Book.星平會海)!!)
      assertTrue(h百日關.testBook(ew, Gender.M, Book.生育禮俗)!!)
      assertTrue(h百日關.testBook(ew, Gender.M, Book.黃曆解秘)!!)
    }

    @Test
    fun bookWithinUnMatch() {
      val ew = EightWords("甲午", "甲寅", "甲子", "甲子")
      assertFalse(h百日關.testBook(ew, Gender.M, Book.象吉通書)!!)
      assertFalse(h百日關.testBook(ew, Gender.M, Book.鰲頭通書)!!)
      assertFalse(h百日關.testBook(ew, Gender.M, Book.星平會海)!!)
      assertFalse(h百日關.testBook(ew, Gender.M, Book.生育禮俗)!!)
      assertFalse(h百日關.testBook(ew, Gender.M, Book.黃曆解秘)!!)
    }

    @Test
    fun bookWithout() {
      val ew = EightWords("甲午", "甲寅", "甲子", "甲辰")
      assertNull(h百日關.testBook(ew, Gender.M, Book.小兒關煞圖))
    }
  }


}
