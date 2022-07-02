/**
 * @author smallufo
 * Created on 2006/6/11 at 下午 11:23:55
 */
package destiny.core.calendar.eightwords

import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchUnconstrained
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame

class EightWordsTest {

  val logger = KotlinLogging.logger { }

  @Test
  fun testSerializeMonthNormal() {
    val ew = EightWords("甲子", "乙丑", "丙寅", "丁卯")
    val encoded = Json.encodeToString(ew)
    logger.info { encoded }
    assertEquals("""{"year":"甲子","month":"乙丑","day":"丙寅","hour":"丁卯"}""", encoded)

    assertEquals(ew , Json.decodeFromString(encoded))
  }

  @Test
  fun testSerializeMonthUnconstrained() {
    val ew = EightWords(StemBranch.甲子, StemBranchUnconstrained.甲丑, StemBranch.丙寅, StemBranch.丁卯)
    val encoded = Json.encodeToString(ew)
    logger.info { encoded }
    assertEquals("""{"year":"甲子","month":"甲丑","day":"丙寅","hour":"丁卯"}""", encoded)
    assertEquals(ew, Json.decodeFromString(encoded))
  }

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
    assertEquals(listOf(StemBranch.甲子, StemBranch.乙丑, StemBranch.丙寅, StemBranch.丁卯), eightWordsFull.stemBranches)
  }
}
