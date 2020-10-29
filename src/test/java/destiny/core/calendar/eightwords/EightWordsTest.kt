/**
 * @author smallufo
 * Created on 2006/6/11 at 下午 11:23:55
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.StemBranch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class EightWordsTest {

  @Test
  fun testEqualsSame() {
    val expected = EightWords("甲子", "甲子", "甲子", "甲子")
    val actual = EightWords(StemBranch["甲子"], StemBranch["甲子"], StemBranch["甲子"], StemBranch["甲子"])
    assertEquals(expected, actual)
    assertNotSame(expected, actual) //不應該 same
  }

  @Test
  fun testNullStemBranch() {
    val eightWordsFull = EightWords(StemBranch["甲子"], StemBranch["乙丑"], StemBranch["丙寅"], StemBranch["丁卯"])
    assertNotNull(eightWordsFull)
    assertNotNull(eightWordsFull.toString())
    println(eightWordsFull.toString())
  }

  @Test
  fun testStemBranches() {
    val eightWordsFull = EightWords(StemBranch["甲子"], StemBranch["乙丑"], StemBranch["丙寅"], StemBranch["丁卯"])
    assertEquals(listOf(StemBranch.甲子, StemBranch.乙丑, StemBranch.丙寅, StemBranch.丁卯) , eightWordsFull.stemBranches)
  }
}
