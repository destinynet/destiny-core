/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.chinese.eightwords

import destiny.core.calendar.eightwords.EightWords
import destiny.core.calendar.eightwords.IdentityPatterns.branchCombined
import destiny.core.calendar.eightwords.IdentityPatterns.branchOpposition
import destiny.core.calendar.eightwords.IdentityPatterns.stemCombined
import destiny.core.calendar.eightwords.IdentityPatterns.stemRooted
import destiny.core.calendar.eightwords.IdentityPatterns.trilogy
import destiny.core.chinese.StemBranch.*
import destiny.core.chinese.eightwords.IdentityTranslator.translateBranchCombined
import destiny.core.chinese.eightwords.IdentityTranslator.translateBranchOpposition
import destiny.core.chinese.eightwords.IdentityTranslator.translateStemCombined
import destiny.core.chinese.eightwords.IdentityTranslator.translateStemRooted
import destiny.core.chinese.eightwords.IdentityTranslator.translateTrilogy
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IdentityTranslatorTest  {

  @Nested
  inner class 天干五合 {

    @Test
    fun single() {
      val ew = EightWords(丁丑, 壬子, 癸未, 己未)
      with(stemCombined) {
        ew.getPatterns().translateStemCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("年干(丁) 與 月干(壬) 合木") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(丁丑, 壬子, 癸未, 戊午)
      with(stemCombined) {
        ew.getPatterns().translateStemCombined().also { list ->
          assertEquals(2, list.size)
          assertTrue { list.contains("年干(丁) 與 月干(壬) 合木") }
          assertTrue { list.contains("日干(癸) 與 時干(戊) 合火") }
        }
      }
    }

    @Test
    fun multipleCombined1() {
      val ew = EightWords(丁丑, 壬子, 癸未, 壬戌)
      with(stemCombined) {
        ew.getPatterns().translateStemCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("年干(丁) 與 月干、時干(均為壬) 合木") }
        }
      }
    }

    @Test
    fun multipleCombined2() {
      val ew = EightWords(丁丑, 壬子, 丁丑, 壬子)
      with(stemCombined) {
        ew.getPatterns().translateStemCombined().also { list ->
          assertTrue { list.contains("年干、日干(均為丁) 與 月干、時干(均為壬) 合木") }
        }
      }
    }
  }

  @Nested
  inner class 地支六合 {

    @Test
    fun isolated() {
      val ew = EightWords(丙子, 辛丑, 癸亥, 甲寅)
      with(branchCombined) {
        ew.getPatterns().translateBranchCombined().also { list ->
          assertEquals(2, list.size)
          assertTrue { list.contains("年支(子) 六合 月支(丑)") }
          assertTrue { list.contains("日支(亥) 六合 時支(寅)") }
        }
      }
    }

    @Test
    fun merged_1_3() {
      val ew = EightWords(丙子, 辛丑, 乙丑, 丁丑)
      with(branchCombined) {
        ew.getPatterns().translateBranchCombined().also { list ->
          assertEquals(1 , list.size)
          assertTrue { list.contains("年支(子) 六合 月支、日支、時支(均為 丑)") }
        }
      }
    }

    @Test
    fun merged_3_1() {
      val ew = EightWords(丙子, 庚子, 辛丑, 戊子)
      with(branchCombined) {
        ew.getPatterns().translateBranchCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("年支、月支、時支(均為 子) 六合 日支(丑)") }
        }
      }
    }

    @Test
    fun merged_2_2() {
      val ew = EightWords(丙子, 庚子, 辛丑, 己丑)
      with(branchCombined) {
        ew.getPatterns().translateBranchCombined().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("年支、月支(均為 子) 六合 日支、時支(均為 丑)") }
        }
      }
    }
  }

  @Nested
  inner class 地支三合 {

    @Test
    fun single() {
      val ew = EightWords(己卯, 辛未, 丁亥, 乙巳)
      with(trilogy) {
        ew.getPatterns().translateTrilogy().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("年支、月支、日支三合木局") }
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(己卯, 辛未, 丁亥, 癸卯)
      with(trilogy) {
        ew.getPatterns().translateTrilogy().also { list ->
          assertEquals(2, list.size)
          assertTrue { list.contains("年支、月支、日支三合木局") }
          assertTrue { list.contains("月支、日支、時支三合木局") }
        }
      }
    }
  }

  @Nested
  inner class 地支正沖 {

    @Test
    fun isolated() {
      val ew = EightWords(丁丑, 壬子, 癸未, 戊午)
      with(branchOpposition) {
        ew.getPatterns().translateBranchOpposition().also { list ->
          assertEquals(2, list.size)
          assertTrue { list.contains("年支(丑) 正沖 日支(未)") }
          assertTrue { list.contains("月支(子) 正沖 時支(午)") }
        }
      }
    }

    @Test
    fun merged1() {
      val ew = EightWords(癸未, 乙丑, 辛丑, 己丑)
      with(branchOpposition) {
        ew.getPatterns().translateBranchOpposition().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("年支(未) 正沖 月支、日支、時支(均為 丑)") }
        }
      }
    }

    @Test
    fun merged2() {
      val ew = EightWords(癸未, 乙丑, 辛丑, 乙未)
      with(branchOpposition) {
        ew.getPatterns().translateBranchOpposition().also { list ->
          assertEquals(1, list.size)
          assertTrue { list.contains("年支、時支(均為 未) 正沖 月支、日支(均為 丑)") }
        }
      }
    }
  }

  @Nested
  inner class 天干通根 {

    @Test
    fun single() {
      val ew = EightWords(庚辰, 戊寅, 甲午, 癸酉)
      with(stemRooted) {
        ew.getPatterns().translateStemRooted().also { list ->
          assertEquals(3 , list.size)
          assertTrue { list.contains("月干(戊) 通根 年支(辰)、月支(寅)") }
          assertTrue { list.contains("日干(甲) 通根 月支(寅)") }
          assertTrue { list.contains("時干(癸) 通根 年支(辰)") }
        }
      }
    }

    @Test
    fun multiple_branchMerged() {
      val ew = EightWords(丁丑, 壬子, 癸未, 戊午)
      with(stemRooted) {
        ew.getPatterns().translateStemRooted().also { list ->
          assertEquals(2 , list.size)
          assertTrue { list.contains("年干(丁) 通根 日支(未)、時支(午)") }
          assertTrue { list.contains("日干(癸) 通根 年支(丑)、月支(子)") }
        }
      }
    }

    @Test
    fun multiple_stemMerged() {
      val ew = EightWords(丁丑, 壬子, 癸未, 丁巳)
      with(stemRooted) {
        ew.getPatterns().translateStemRooted().also { list ->
          assertEquals(2, list.size)
          assertTrue { list.contains("年干、時干(均為 丁) 通根 日支(未)") }
          assertTrue { list.contains("日干(癸) 通根 年支(丑)、月支(子)") }
        }
      }
    }
  }
}
