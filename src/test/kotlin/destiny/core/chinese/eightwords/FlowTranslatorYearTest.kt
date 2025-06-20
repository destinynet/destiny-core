/**
 * Created by smallufo on 2024-01-17.
 */
package destiny.core.chinese.eightwords

import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.affecting
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.branchCombined
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.branchOpposition
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.stemCombined
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.toFlowTrilogy
import destiny.core.calendar.eightwords.FlowLargeYearPatterns.trilogyToFlow
import destiny.core.chinese.StemBranch.*
import destiny.core.chinese.eightwords.FlowTranslator.translateAffecting
import destiny.core.chinese.eightwords.FlowTranslator.translateBranchCombined
import destiny.core.chinese.eightwords.FlowTranslator.translateBranchOpposition
import destiny.core.chinese.eightwords.FlowTranslator.translateStemCombined
import destiny.core.chinese.eightwords.FlowTranslator.translateToFlowTrilogy
import destiny.core.chinese.eightwords.FlowTranslator.translateTrilogyToFlow
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlowTranslatorYearTest {

  @Test
  fun 大運流年同時影響本命天干() {
    val ew = EightWords(丙子, 乙未, 丙寅, 壬辰)
    with(affecting) {
      ew.getPatterns(甲辰, 甲戌).translateAffecting().also { list ->
        assertEquals(3, list.size)
        assertTrue { list.contains("本命年干、日干(均為 丙) 得到 大運、流年 五行 所生") }
        assertTrue { list.contains("本命月干(乙) 與 大運、流年五行相同") }
        assertTrue { list.contains("本命時干(壬) 生/洩 出大運、流年") }
      }
    }
  }

  @Nested
  inner class 大運流年合住本命天干 {

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(stemCombined) {
        ew.getPatterns(甲辰, 甲戌).translateStemCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("本命時干(己) 被大運、流年(均為 甲)合住，甲己合化土") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 己巳, 己巳)
      with(stemCombined) {
        ew.getPatterns(甲辰, 甲戌).translateStemCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("本命日干、時干(均為 己) 被大運、流年(均為 甲)合住，甲己合化土") }
        }
      }
    }
  }

  @Nested
  inner class 大運流年合住本命地支 {

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchCombined) {
        ew.getPatterns(甲辰, 甲戌).translateBranchCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("流年地支(戌) 合住 本命時支(卯)") }
        }
      }
    }

    @Test
    fun multiple1() {
      val ew = EightWords(丙子, 乙未, 乙未, 乙酉)
      with(branchCombined) {
        ew.getPatterns(甲辰, 庚午).translateBranchCombined().also { list ->
          assertTrue { list.contains("大運地支(辰) 合住 本命時支(酉)") }
          assertTrue { list.contains("流年地支(午) 合住 本命月支、日支(均為 未)") }
        }
      }
    }

    @Test
    fun multiple2() {
      val ew = EightWords(丙子, 乙未, 乙未, 乙酉)
      with(branchCombined) {
        ew.getPatterns(丙午, 丙午).translateBranchCombined().also { list ->
          assertEquals(1 , list.size)
          assertTrue { list.contains("大運、流年地支(均為 午) 合住 本命月支、日支(均為 未)") }
        }
      }
    }
  }

  @Nested
  inner class 本命已經三合取二_再拱大運或流年 {

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(trilogyToFlow) {
        ew.getPatterns(甲辰, 丙子).translateTrilogyToFlow().also { list ->
          assertEquals(1 , list.size)
          assertTrue { list.contains("大運(辰)與本命年柱(子)、時柱(申)三合水局") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(trilogyToFlow) {
        ew.getPatterns(甲辰, 戊辰).translateTrilogyToFlow().also { list ->
          assertEquals(1 , list.size)
          assertTrue { list.contains("大運(辰)、流年(辰)與本命年柱(子)、時柱(申)三合水局") }
        }
      }
    }
  }

  @Nested
  inner class 大運流年三合_再拱本命地支 {

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙丑, 甲申)
      with(toFlowTrilogy) {
        ew.getPatterns(甲辰, 丙子).translateToFlowTrilogy().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("大運(辰)、流年(子) 與本命時支(申) 三合 水局") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙丑, 丙子)
      with(toFlowTrilogy) {
        ew.getPatterns(甲辰, 壬申).translateToFlowTrilogy().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("大運(辰)、流年(申) 與本命年支、時支(均為子) 三合 水局") }
        }
      }
    }
  }

  @Nested
  inner class 大運流年正沖本命地支 {

    @Test
    fun single() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchOpposition) {
        ew.getPatterns(甲戌, 癸酉).translateBranchOpposition().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("流年地支(酉) 正沖 本命時支(卯)") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丙子, 乙未, 乙未, 己卯)
      with(branchOpposition) {
        ew.getPatterns(辛丑, 辛丑).translateBranchOpposition().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("大運、流年地支(均為 丑) 正沖 本命月支、日支(均為 未)") }
        }
      }
    }
  }

}
