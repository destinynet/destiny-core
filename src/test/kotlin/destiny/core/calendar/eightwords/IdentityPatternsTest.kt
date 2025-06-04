/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale.*
import destiny.core.calendar.eightwords.IdentityPattern.Trilogy
import destiny.core.calendar.eightwords.IdentityPatterns.auspiciousPattern
import destiny.core.calendar.eightwords.IdentityPatterns.trilogy
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IdentityPatternsTest {

  @Nested
  inner class Trilogy {

    @Test
    fun single() {
      val ew = EightWords(己卯, 辛未, 丁亥, 乙巳)
      with(trilogy) {
        ew.getPatterns().also { patterns ->
          assertEquals(
            setOf(
              Trilogy(setOf(YEAR to 卯, MONTH to 未, DAY to 亥))
            ), patterns
          )
        }
      }
    }

    @Test
    fun multiple() {
      val ew = EightWords(己卯, 辛未, 丁亥, 癸卯)
      with(trilogy) {
        ew.getPatterns().also { patterns ->
          assertEquals(
            setOf(
              Trilogy(setOf(YEAR to 卯, MONTH to 未, DAY to 亥)),
              Trilogy(setOf(MONTH to 未, DAY to 亥, HOUR to 卯)),
            ), patterns
          )
        }
      }
    }
  }

  @Nested
  inner class 天德貴人Test {

    @Nested
    inner class By天干 {
      /**
       * 丑月 庚日 -> 天德在日
       */
      @Test
      fun 日柱() {
        val ew = EightWords(甲辰, 丁丑, 庚子, 丙子)
        with(auspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天德貴人, setOf(DAY)))
            }
          }
        }
      }

      /**
       * 丑月 , 庚日 庚時
       */
      @Test
      fun 日柱_時柱() {
        val ew = EightWords(甲辰, 丁丑, 庚子, 庚辰)
        with(auspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(
                IdentityPattern.AuspiciousPattern(Auspicious.天德貴人, setOf(DAY, HOUR)),
              )
            }
          }
        }
      }
    }

    /**
     * 卯   -> Either.Right(申)
     * 午   -> Either.Right(亥)
     * 酉   -> Either.Right(寅)
     */
    @Nested
    inner class By地支 {

      /**
       * 卯月 申日 -> 天德在日
       */
      @Test
      fun 日柱() {
        val ew = EightWords(乙巳, 己卯, 甲申, 辛未)
        with(auspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天德貴人, setOf(DAY)))
            }
          }
        }
      }

      @Test
      fun 日柱_時柱() {
        val ew = EightWords(乙巳, 己卯, 甲申, 壬申)
        with(auspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天德貴人, setOf(DAY, HOUR)))
            }
          }
        }
      }
    }
  }

  @Nested
  inner class 月德貴人Test {

    /**
     * 2025 年 2 月 6  日
     */
    @Test
    fun 日柱() {
      val ew = EightWords(乙巳, 戊寅, 丙午, 乙未)
      with(auspiciousPattern) {
        ew.getPatterns().also { patterns ->
          assertTrue {
            patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.月德貴人, setOf(DAY)))
          }
        }
      }
    }

    @Test
    fun 日柱_時柱() {
      val ew = EightWords(乙巳, 戊寅, 丙午, 丙申)
      with(auspiciousPattern) {
        ew.getPatterns().also { patterns ->
          assertTrue {
            patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.月德貴人, setOf(DAY, HOUR)))
          }
        }
      }
    }

    @Test
    fun 時柱() {
      val ew = EightWords(乙巳, 戊寅, 丁未 , 丙午)
      with(auspiciousPattern) {
        ew.getPatterns().also { patterns ->
          assertTrue {
            patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.月德貴人, setOf(HOUR)))
          }
        }
      }
    }
  }

  @Nested
  inner class 天德合Test {

    /**
     * 辰月 , 天德在 壬 , 丁壬 合， 丁為 天德合
     * 2025-04-08 , TUESDAY , (乙巳年 庚辰月 丁未日)
     */
    @Test
    fun 日柱() {
      val ew = EightWords(乙巳, 庚辰, 丁未, 庚子)
      with(auspiciousPattern) {
        ew.getPatterns().also { patterns ->
          assertTrue {
            patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天德合, setOf(DAY)))
          }
        }
      }
    }

    @Test
    fun 日柱_時柱() {
      val ew = EightWords(乙巳, 庚辰, 丁未, 丁未)
      with(auspiciousPattern) {
        ew.getPatterns().also { patterns ->
          assertTrue {
            patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天德合, setOf(DAY, HOUR)))
          }
        }
      }
    }

    /**
     * 2025-04-09 , WEDNESDAY , (乙巳年 庚辰月 戊申日) 丁巳時
     */
    @Test
    fun 時柱() {
      val ew = EightWords(乙巳, 庚辰, 戊申, 丁巳)
      with(auspiciousPattern) {
        ew.getPatterns().also { patterns ->
          assertTrue {
            patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天德合, setOf(HOUR)))
          }
        }
      }
    }

    /**
     * 辰月 , 天德在 壬 , 丁壬 合， 丁為 天德合
     * 2027-05-08 未時, 丁未年 甲辰月 丁亥日
     */
    @Test
    fun 年柱_日柱_時柱() {
      val ew = EightWords(丁未, 甲辰, 丁亥, 丁未)
      with(auspiciousPattern) {
        ew.getPatterns().also { patterns ->
          assertTrue {
            patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天德合, setOf(YEAR , DAY, HOUR)))
          }
        }
      }
    }
  }
}
