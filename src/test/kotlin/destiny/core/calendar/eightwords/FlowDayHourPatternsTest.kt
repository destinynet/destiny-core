/**
 * Created by smallufo on 2025-05-27.
 */
package destiny.core.calendar.eightwords

import destiny.core.FlowScale
import destiny.core.Scale.*
import destiny.core.calendar.eightwords.FlowDayHourPatterns.affecting
import destiny.core.calendar.eightwords.FlowDayHourPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowDayHourPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowDayHourPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowDayHourPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowDayHourPatterns.trilogyToFlow
import destiny.core.calendar.eightwords.FlowPattern.*
import destiny.core.calendar.eightwords.Reacting.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class FlowDayHourPatternsTest {

  @Test
  fun testAffecting() {
    val ew = EightWords(丙子, 乙未, 丙寅, 壬辰)
    with(affecting) {
      ew.getPatterns(甲辰, 甲戌).also { patterns ->
        assertEquals(
          setOf(
            // 本命 年干 丙火 , 同時被 流日、流時的「甲」所生
            Affecting(YEAR, 丙, PRODUCED, setOf(FlowScale.DAY, FlowScale.HOUR)),
            // 本命 月干 乙木 , 同時與 流日、流時的「甲」相同五行
            Affecting(MONTH, 乙, SAME, setOf(FlowScale.DAY, FlowScale.HOUR)),
            // 本命 日干 丙火 , 同時被 流日、流時的「甲」所生
            Affecting(DAY, 丙, PRODUCED, setOf(FlowScale.DAY, FlowScale.HOUR)),
            // 本命 時干 壬水 , 同時洩氣給 流日、流時的「甲」木
            Affecting(HOUR, 壬, PRODUCING, setOf(FlowScale.DAY, FlowScale.HOUR)),
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
              // 時支 己，同時被流日、流時 的 甲 合住
              StemCombined(HOUR, 己, FlowScale.DAY),
              StemCombined(HOUR, 己, FlowScale.HOUR)
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiplePillars() {
      val ew = EightWords(丙子, 乙未, 己巳, 己巳)
      with(stemCombined) {
        ew.getPatterns(甲辰, 甲戌).also { patterns ->
          assertEquals(
            setOf(
              StemCombined(DAY, 己, FlowScale.DAY),
              StemCombined(DAY, 己, FlowScale.HOUR),
              StemCombined(HOUR, 己, FlowScale.DAY),
              StemCombined(HOUR, 己, FlowScale.HOUR),
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
    fun singlePillar() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchCombined) {
        ew.getPatterns(甲辰, 甲戌).also { patterns ->
          assertEquals(
            setOf(
              BranchCombined(HOUR, 卯, FlowScale.HOUR)
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
              BranchCombined(MONTH, 未, FlowScale.HOUR),
              BranchCombined(DAY, 未, FlowScale.HOUR),
              BranchCombined(HOUR, 酉, FlowScale.DAY),
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
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), FlowScale.DAY to 辰)
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
              // 本命年支（子）、時支（申） 與 流日辰，三合
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), FlowScale.DAY to 辰),
              // 本命年支（子）、時支（申） 與 流時辰，三合
              TrilogyToFlow(setOf(YEAR to 子, HOUR to 申), FlowScale.HOUR to 辰),
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
              ToFlowTrilogy(HOUR, 申, setOf(FlowScale.DAY to 辰, FlowScale.HOUR to 子))
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
              // 本命 年支 子，與流日辰、流時申，形成三合
              ToFlowTrilogy(YEAR, 子, setOf(FlowScale.DAY to 辰, FlowScale.HOUR to 申)),
              // 本命 時支 子，與流日辰、流時申，形成三合
              ToFlowTrilogy(HOUR, 子, setOf(FlowScale.DAY to 辰, FlowScale.HOUR to 申)),
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
          assertEquals(
            setOf(
              BranchOpposition(HOUR, 卯, FlowScale.HOUR)
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
              BranchOpposition(MONTH, 未, FlowScale.DAY),
              BranchOpposition(MONTH, 未, FlowScale.HOUR),
              BranchOpposition(DAY, 未, FlowScale.DAY),
              BranchOpposition(DAY, 未, FlowScale.HOUR),
            ), patterns
          )
        }
      }
    }
  }


}
