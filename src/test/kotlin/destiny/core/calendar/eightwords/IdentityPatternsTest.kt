/**
 * Created by smallufo on 2024-01-20.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale.*
import destiny.core.calendar.eightwords.IdentityPattern.Trilogy
import destiny.core.calendar.eightwords.IdentityPatterns.trilogy
import destiny.core.chinese.Branch.*
import destiny.core.chinese.StemBranch.*
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
