/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale.*
import destiny.core.calendar.eightwords.EightWordsFlowYearPattern.BranchOpposite
import destiny.core.calendar.eightwords.EightWordsFlowYearPattern.StemCombined
import destiny.core.calendar.eightwords.FlowYearPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowYearPatterns.stemCombined
import destiny.core.chinese.Branch.未
import destiny.core.chinese.Branch.辰
import destiny.core.chinese.Stem.乙
import destiny.core.chinese.Stem.庚
import destiny.core.chinese.StemBranch.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowYearPatternsTest {

  @Nested
  inner class StemCombined {

    @Test
    fun empty() {
      with(stemCombined) {
        assertTrue { EightWords(丙子, 乙未, 乙未, 庚辰).getPatterns(甲辰).isEmpty() }
      }
    }

    @Test
    fun single() {
      with(stemCombined) {
        assertEquals(
          setOf(StemCombined(setOf(HOUR), 庚)),
          EightWords(丙子, 乙未, 乙未, 庚辰).getPatterns(乙酉)
        )
      }
    }

    @Test
    fun multiple() {
      with(stemCombined) {
        assertEquals(
          setOf(StemCombined(setOf(MONTH, DAY), 乙)),
          EightWords(丙子, 乙未, 乙未, 庚辰).getPatterns(庚戌)
        )
      }
    }
  }

  @Nested
  inner class BranchOpposite {

    @Test
    fun empty() {
      with(branchOpposition) {
        assertTrue { EightWords(丙子, 乙未, 乙未, 庚辰).getPatterns(甲辰).isEmpty() }
      }
    }

    @Test
    fun single() {
      with(branchOpposition) {
        assertEquals(
          setOf(
            BranchOpposite(setOf(HOUR), 辰)
          ),
          EightWords(丙子, 乙未, 乙未, 庚辰).getPatterns(甲戌)
        )
      }
    }

    @Test
    fun multiple() {
      with(branchOpposition) {
        assertEquals(
          setOf(
            BranchOpposite(setOf(MONTH, DAY), 未)
          ),
          EightWords(丙子, 乙未, 乙未, 庚辰).getPatterns(丁丑)
        )
      }
    }
  }
}
