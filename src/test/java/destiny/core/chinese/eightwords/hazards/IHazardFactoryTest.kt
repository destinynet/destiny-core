/**
 * Created by smallufo on 2022-07-19.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.Gender
import destiny.core.calendar.eightwords.EightWords
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class IHazardFactoryTest {

  @Test
  fun testWithPreferredBook() {
    val ew = EightWords("甲午", "甲子", "甲子", "甲寅")

    // p千日關A (象吉通書) : 甲年 , 午時 , 不符合
    assertFalse(h千日關.test(ew, Gender.男, Book.象吉通書))

    // p千日關B (鰲頭通書) : 甲年 , 午時 , 不符合
    assertFalse(h千日關.test(ew, Gender.男, Book.鰲頭通書))

    // p千日關C (黃曆解秘) : 凡午年寅、申、巳、亥時生人，犯此忌三歲上高落低之患。 , 符合
    assertTrue(h千日關.test(ew, Gender.男, Book.黃曆解秘))
  }


  @Test
  fun testWithoutPreferredBook() {
    val ew1 = EightWords("甲午", "甲子", "甲子", "甲寅")

    // p千日關A (象吉通書) : 甲年 , 午時 , 不符合
    // p千日關B (鰲頭通書) : 甲年 , 午時 , 不符合
    // p千日關C (黃曆解秘) : 凡午年寅、申、巳、亥時生人，犯此忌三歲上高落低之患。 , 符合
    assertTrue(h千日關.test(ew1, Gender.男))

    // p千日關A,B,C 都不符合
    val ew2 = EightWords("甲午", "甲子", "甲子", "甲辰")
    assertFalse(h千日關.test(ew2, Gender.男))
  }
}
