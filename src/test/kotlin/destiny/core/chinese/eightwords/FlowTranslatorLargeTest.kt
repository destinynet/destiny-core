/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.chinese.eightwords

import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.FlowLargePatterns.branchCombined
import destiny.core.calendar.eightwords.FlowLargePatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowLargePatterns.stemCombined
import destiny.core.calendar.eightwords.FlowLargePatterns.trilogyToFlow
import destiny.core.chinese.StemBranch.*
import destiny.core.chinese.eightwords.FlowTranslator.translateBranchCombined
import destiny.core.chinese.eightwords.FlowTranslator.translateBranchOpposition
import destiny.core.chinese.eightwords.FlowTranslator.translateStemCombined
import destiny.core.chinese.eightwords.FlowTranslator.translateTrilogyToFlow
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowTranslatorLargeTest {

  @Nested
  inner class 大運天干合住本命天干 {
    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(stemCombined) {
        ew.getPatterns(甲辰).translateStemCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("本命時干(己) 被大運(甲)合住，甲己合化土") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 己巳, 己巳)
      with(stemCombined) {
        ew.getPatterns(甲辰).translateStemCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("本命日干、時干(均為 己) 被大運(甲)合住，甲己合化土") }
        }
      }
    }
  }

  @Nested
  inner class 大運與本命地支相合 {

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchCombined) {
        ew.getPatterns(甲戌).translateBranchCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("大運地支(戌) 合住 本命時支(卯)") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙未, 癸未)
      with(branchCombined) {
        ew.getPatterns(庚午).translateBranchCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("大運地支(午) 合住 本命月支、日支、時支(均為 未)") }
        }
      }
    }
  }

  @Nested
  inner class 本命已經三合取二_再拱大運 {

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(trilogyToFlow) {
        ew.getPatterns(甲辰).translateTrilogyToFlow().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("大運(辰)與本命年柱(子)、時柱(申)三合水局") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(trilogyToFlow) {
        ew.getPatterns(戊辰).translateTrilogyToFlow().also { list ->
          assertEquals(1 , list.size)
          assertTrue { list.contains("大運(辰)與本命年柱(子)、時柱(申)三合水局") }
        }
      }
    }
  }

  @Nested
  inner class 大運地支正沖本命 {

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchOpposition) {
        ew.getPatterns(癸酉).translateBranchOpposition().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("大運地支(酉) 正沖 本命時支(卯)") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchOpposition) {
        ew.getPatterns(辛丑).translateBranchOpposition().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("大運地支(丑) 正沖 本命月支、日支(均為 未)") }
        }
      }
    }
  }
}
