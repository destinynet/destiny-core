/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale.*
import destiny.core.calendar.eightwords.IdentityPattern.Trilogy
import destiny.core.calendar.eightwords.IdentityPatterns.auspiciousPattern
import destiny.core.calendar.eightwords.IdentityPatterns.inauspiciousPattern
import destiny.core.calendar.eightwords.IdentityPatterns.trilogy
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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

  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  @Nested
  inner class AuspiciousTest {
    private fun 天赦日() = Stream.of(
      EightWords(乙巳, 己卯, 戊寅, 壬子), // 2025-03-10
      EightWords(乙巳, 辛巳, 甲午, 甲子), // 2025-05-25
      EightWords(乙巳, 癸未, 甲午, 甲子), // 2025-07-24
      EightWords(乙巳, 乙酉, 戊申, 壬子), // 2025-10-06
      EightWords(乙巳, 戊子, 甲子, 壬子), // 2025-12-21
    )

    @ParameterizedTest
    @MethodSource
    fun 天赦日(ew: EightWords) {
      with(auspiciousPattern) {
        ew.getPatterns().also { patterns ->
          assertTrue {
            patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天赦日, setOf(DAY)))
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
        val ew = EightWords(乙巳, 戊寅, 丁未, 丙午)
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
              patterns.contains(IdentityPattern.AuspiciousPattern(Auspicious.天德合, setOf(YEAR, DAY, HOUR)))
            }
          }
        }
      }
    }
  }


  @Nested
  inner class InauspiciousTest {

    @Nested
    inner class 陰差陽錯Test {
      /**
       * 2025-04-08 , TUESDAY , (乙巳年 庚辰月 丁未日)
       */
      @Test
      fun 日柱() {
        val ew = EightWords(乙巳, 庚辰, 丁未, 庚子)
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.陰差陽錯, setOf(DAY)))
            }
          }
        }
      }

      /**
       * 2027-07-27 , 丁未年 丁未月 丁未日 丁未時
       */
      @Test
      fun 年柱_月柱_日柱_時柱() {
        val ew = EightWords(丁未, 丁未, 丁未, 丁未)
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.陰差陽錯, setOf(YEAR, MONTH, DAY, HOUR)))
            }
          }
        }
      }
    }

    @Nested
    inner class 十惡大敗Test {

      @Test
      fun 年柱() {
        val ew = EightWords(甲辰, 乙卯, 丙寅, 丁丑) // 年柱甲辰
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.十惡大敗, setOf(YEAR)))
            }
          }
        }
      }

      @Test
      fun 年柱_月柱_日柱_時柱() {
        val ew = EightWords(甲辰, 乙巳, 丙申, 丁亥) // 四柱都是十惡大敗
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.十惡大敗, setOf(YEAR, MONTH, DAY, HOUR)))
            }
          }
        }
      }
    }

    @Nested
    inner class 四廢日Test {
      @Test
      fun 四廢日_春季() {
        val ew = EightWords(甲寅, 丁卯, 庚申, 戊寅) // 卯月庚申日
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.四廢日, setOf(DAY)))
            }
          }
        }
      }

      @Test
      fun 四廢日_夏季() {
        val ew = EightWords(甲寅, 己巳, 壬子, 戊寅) // 巳月壬子日
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.四廢日, setOf(DAY)))
            }
          }
        }
      }

      @Test
      fun 非四廢日() {
        val ew = EightWords(甲寅, 丁卯, 甲寅, 戊寅) // 卯月甲寅日
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertFalse {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.四廢日, setOf(DAY)))
            }
          }
        }
      }
    }

    @Nested
    inner class 羊刃Test {
      @Test
      fun 甲日見卯月() {
        val ew = EightWords(甲寅, 己卯, 甲申, 戊寅) // 甲日，月柱有卯
        // 應該包含羊刃在月柱
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.羊刃, setOf(MONTH)))
            }
          }
        }
      }

      @Test
      fun 多柱() {
        val ew = EightWords(乙卯, 己卯, 甲申, 乙卯) // 甲日，年月時都有卯
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.羊刃, setOf(YEAR, MONTH, HOUR)))
            }
          }
        }
      }

      @Test
      fun 丙戊見午年() {
        val ew = EightWords(甲午, 己卯, 丙申, 戊寅) // 丙日，年柱有午
        // 應該包含羊刃在年柱
        with(inauspiciousPattern) {
          ew.getPatterns().also { patterns ->
            assertTrue {
              patterns.contains(IdentityPattern.InauspiciousPattern(Inauspicious.羊刃, setOf(YEAR)))
            }
          }
        }
      }
    }
  }
}
