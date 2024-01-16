/**
 * Created by smallufo on 2024-01-16.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowMonthPatternFactoryTest {

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
        ew.getPatterns(甲辰, 甲戌).also { results: Set<IEightWordsFlowMonthPattern> ->
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
  inner class ToFlowTrilogyTest {

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


}
