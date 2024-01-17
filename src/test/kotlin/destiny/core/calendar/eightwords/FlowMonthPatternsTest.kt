/**
 * Created by smallufo on 2024-01-16.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale.*
import destiny.core.calendar.eightwords.EightWordsFlowMonthPattern.*
import destiny.core.calendar.eightwords.FlowMonthPatterns.bothAffecting
import destiny.core.calendar.eightwords.FlowMonthPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowMonthPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowMonthPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowMonthPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowMonthPatterns.trilogyToFlow
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowMonthPatternsTest {

  @Test
  fun testBothAffecting() {
    val ew = EightWords(丙子, 乙未, 丙寅, 壬辰)
    with(bothAffecting) {
      ew.getPatterns(甲辰, 甲戌).also { patterns ->
        assertEquals(
          setOf(
            BothAffecting.Produced(YEAR, 丙),
            BothAffecting.Produced(DAY, 丙),
            BothAffecting.Same(MONTH, 乙),
            BothAffecting.Producing(HOUR, 壬),
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
    fun singlePillar() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(stemCombined) {
        ew.getPatterns(甲辰, 甲戌).also { patterns ->
          assertEquals(
            setOf(
              StemCombined(HOUR, 己, YEAR),
              StemCombined(HOUR, 己, MONTH)
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiplePillars() {
      val ew = EightWords(丙子, 乙未, 己巳, 己巳)
      with(stemCombined) {
        ew.getPatterns(甲辰, 甲戌).also { results: Set<EightWordsFlowMonthPattern> ->
          assertEquals(
            setOf(
              StemCombined(DAY, 己, YEAR),
              StemCombined(DAY, 己, MONTH),
              StemCombined(HOUR, 己, YEAR),
              StemCombined(HOUR, 己, MONTH),
            ), results
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
    fun singlePillar() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchCombined) {
        ew.getPatterns(甲辰, 甲戌).also { patterns ->
          assertEquals(
            setOf(
              BranchCombined(HOUR, 卯, MONTH)
            ),
            patterns
          )
        }
      }
    }

    @Test
    fun multiplePillars() {
      val ew = EightWords(丙子, 乙未, 乙未, 乙酉)
      with(branchCombined) {
        ew.getPatterns(甲辰, 庚午).also { patterns ->
          assertEquals(
            setOf(
              BranchCombined(MONTH, 未, MONTH),
              BranchCombined(DAY, 未, MONTH),
              BranchCombined(HOUR, 酉, YEAR),
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
    fun singlePillar() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(trilogyToFlow) {
        ew.getPatterns(甲辰, 丙子).also { patterns ->
          assertEquals(
            setOf(
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), YEAR to 辰)
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiplePillars() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(trilogyToFlow) {
        ew.getPatterns(甲辰, 戊辰).also { patterns ->
          assertEquals(
            setOf(
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), YEAR to 辰),
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), MONTH to 辰),
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
    fun singlePillar() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(toFlowTrilogy) {
        ew.getPatterns(甲辰, 丙子).also { patterns ->
          assertEquals(
            setOf(
              ToFlowTrilogy(HOUR, 申, setOf(YEAR to 辰, MONTH to 子))
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiplePillars() {
      val ew = EightWords(丙子, 乙未, 乙丑, 丙子)
      with(toFlowTrilogy) {
        ew.getPatterns(甲辰, 壬申).also { patterns ->
          assertEquals(
            setOf(
              ToFlowTrilogy(YEAR, 子, setOf(YEAR to 辰, MONTH to 申)),
              ToFlowTrilogy(HOUR, 子, setOf(YEAR to 辰, MONTH to 申)),
            ), patterns
          )
        }
      }
    }
  }

  @Nested
  inner class BranchOpposition {

    @Test
    fun empty() {
      with(branchOpposition) {
        assertTrue { EightWords(丙子, 乙未, 乙未, 己卯).getPatterns(甲辰, 乙亥).isEmpty() }
      }
    }

    @Test
    fun singlePillar() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchOpposition) {
        ew.getPatterns(甲辰, 癸酉).also { patterns ->
          println(patterns)
          assertEquals(
            setOf(
              BranchOpposition(HOUR, 卯, MONTH)
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiplePillars() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchOpposition) {
        ew.getPatterns(辛丑, 辛丑).also { patterns ->
          assertEquals(
            setOf(
              BranchOpposition(MONTH, 未, YEAR),
              BranchOpposition(MONTH, 未, MONTH),
              BranchOpposition(DAY, 未, YEAR),
              BranchOpposition(DAY, 未, MONTH),
            ), patterns
          )
        }
      }
    }
  }
}
