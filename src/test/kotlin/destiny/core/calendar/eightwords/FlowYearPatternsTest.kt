/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.Scale.*
import destiny.core.calendar.eightwords.EightWordsFlowYearPattern.*
import destiny.core.calendar.eightwords.FlowYearPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowYearPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowYearPatterns.stemCombined
import destiny.core.calendar.eightwords.Reacting.*
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.未
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowYearPatternsTest {

  @Test
  fun testBothAffecting() {
    val ew = EightWords(丙子, 乙未, 丙寅, 壬辰)
    with(bothAffecting) {
      ew.getPatterns(甲辰, 甲戌).also { patterns ->
        assertEquals(
          setOf(
            BothAffecting(YEAR, 丙, PRODUCED),
            BothAffecting(DAY, 丙, PRODUCED),
            BothAffecting(MONTH, 乙, SAME),
            BothAffecting(HOUR, 壬, PRODUCING),
          ), patterns
        )
      }
    }
  }

  @Nested
  inner class StemCombined {

    @Test
    fun empty() {
      with(stemCombined) {
        assertTrue { EightWords(丙子, 乙未, 乙未, 庚辰).getPatterns(甲辰, 甲戌).isEmpty() }
      }
    }

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(stemCombined) {
        ew.getPatterns(甲辰, 甲戌).also { patterns ->
          assertEquals(
            setOf(
              StemCombined(HOUR, 己, FlowScale.LARGE),
              StemCombined(HOUR, 己, FlowScale.YEAR)
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 己巳, 己巳)
      with(stemCombined) {
        ew.getPatterns(甲辰, 甲戌).also { patterns ->
          assertEquals(
            setOf(
              StemCombined(DAY, 己, FlowScale.LARGE),
              StemCombined(DAY, 己, FlowScale.YEAR),
              StemCombined(HOUR, 己, FlowScale.LARGE),
              StemCombined(HOUR, 己, FlowScale.YEAR),
            ), patterns
          )
        }
      }
    }
  }

  @Nested
  inner class BranchOpposite {

    @Test
    fun empty() {
      with(branchOpposition) {
        EightWords(丙子, 乙未, 乙未, 己卯).getPatterns(甲辰, 乙亥).isEmpty()
      }
    }

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchOpposition) {
        ew.getPatterns(甲辰, 癸酉).also { patterns ->
          assertEquals(
            setOf(
              BranchOpposition(HOUR, Branch.卯, FlowScale.YEAR)
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchOpposition) {
        ew.getPatterns(辛丑, 辛丑).also { patterns ->
          assertEquals(
            setOf(
              BranchOpposition(MONTH, 未, FlowScale.LARGE),
              BranchOpposition(MONTH, 未, FlowScale.YEAR),
              BranchOpposition(DAY, 未, FlowScale.LARGE),
              BranchOpposition(DAY, 未, FlowScale.YEAR),
            ), patterns
          )
        }
      }
    }
  }
}
