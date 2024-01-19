/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.Scale.*
import destiny.core.calendar.eightwords.EightWordsFlowPattern.*
import destiny.core.calendar.eightwords.FlowYearPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowYearPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowYearPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowYearPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowYearPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowYearPatterns.trilogyToFlow
import destiny.core.calendar.eightwords.Reacting.*
import destiny.core.chinese.Branch.*
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
            BothAffecting(YEAR, 丙, PRODUCED, setOf(FlowScale.LARGE, FlowScale.YEAR)),
            BothAffecting(DAY, 丙, PRODUCED, setOf(FlowScale.LARGE, FlowScale.YEAR)),
            BothAffecting(MONTH, 乙, SAME, setOf(FlowScale.LARGE, FlowScale.YEAR)),
            BothAffecting(HOUR, 壬, PRODUCING, setOf(FlowScale.LARGE, FlowScale.YEAR)),
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
  inner class BranchCombined {

    @Test
    fun empty() {
      with(branchCombined) {
        assertTrue { EightWords(丙子, 乙未, 乙未, 己卯).getPatterns(甲辰, 乙亥).isEmpty() }
      }
    }

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchCombined) {
        ew.getPatterns(甲辰, 甲戌).also { patterns ->
          assertEquals(
            setOf(
              BranchCombined(HOUR, 卯, FlowScale.YEAR)
            ),
            patterns
          )
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙未, 乙酉)
      with(branchCombined) {
        ew.getPatterns(甲辰, 庚午).also { patterns ->
          assertEquals(
            setOf(
              BranchCombined(MONTH, 未, FlowScale.YEAR),
              BranchCombined(DAY, 未, FlowScale.YEAR),
              BranchCombined(HOUR, 酉, FlowScale.LARGE),
            ), patterns
          )
        }
      }
    }
  }

  @Nested
  inner class TrilogyToFlow {

    @Test
    fun empty() {
      with(trilogyToFlow) {
        assertTrue { EightWords(丙子, 乙未, 乙丑, 甲申).getPatterns(癸卯, 壬戌).isEmpty() }
        assertTrue { EightWords(丙子, 乙未, 乙丑, 乙酉).getPatterns(甲辰, 丙子).isEmpty() }
      }
    }

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(trilogyToFlow) {
        ew.getPatterns(甲辰, 丙子).also { patterns ->
          assertEquals(
            setOf(
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), FlowScale.LARGE to 辰)
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(trilogyToFlow) {
        ew.getPatterns(甲辰, 戊辰).also { patterns ->
          assertEquals(
            setOf(
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), FlowScale.LARGE to 辰),
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), FlowScale.YEAR to 辰),
            ), patterns
          )
        }
      }
    }
  }

  @Nested
  inner class ToFlowTrilogy {

    @Test
    fun empty() {
      with(toFlowTrilogy) {
        assertTrue { EightWords(丙子, 乙未, 乙丑, 甲申).getPatterns(癸卯, 壬戌).isEmpty() }
        assertTrue { EightWords(丙子, 乙未, 乙丑, 乙酉).getPatterns(甲辰, 丙子).isEmpty() }
      }
    }

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(toFlowTrilogy) {
        ew.getPatterns(甲辰, 丙子).also { patterns ->
          assertEquals(
            expected = setOf(
              ToFlowTrilogy(HOUR, 申, setOf(FlowScale.LARGE to 辰, FlowScale.YEAR to 子))
            ), actual = patterns
          )
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙丑, 丙子)
      with(toFlowTrilogy) {
        ew.getPatterns(甲辰, 壬申).also { patterns ->
          assertEquals(
            setOf(
              ToFlowTrilogy(YEAR, 子, setOf(FlowScale.LARGE to 辰, FlowScale.YEAR to 申)),
              ToFlowTrilogy(HOUR, 子, setOf(FlowScale.LARGE to 辰, FlowScale.YEAR to 申)),
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
              BranchOpposition(HOUR, 卯, FlowScale.YEAR)
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
