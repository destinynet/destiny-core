/**
 * Created by smallufo on 2024-01-16.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale
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
      ew.getPatterns(甲辰, 甲戌).also { results: Set<IEightWordsFlowMonthPattern> ->
        assertEquals(
          setOf(
            BothAffecting.Produced(Scale.YEAR, 丙),
            BothAffecting.Produced(Scale.DAY, 丙),
            BothAffecting.Same(Scale.MONTH, 乙),
            BothAffecting.Producing(Scale.HOUR, 壬),
          ) , results
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
        ew.getPatterns(甲辰, 甲戌).also { results: Set<IEightWordsFlowMonthPattern> ->
          assertEquals(
            setOf(
              StemCombined(Scale.HOUR, 己, Scale.YEAR),
              StemCombined(Scale.HOUR, 己, Scale.MONTH)
            ), results
          )
        }
      }
    }

    @Test
    fun multiplePillars() {
      val ew = EightWords(丙子, 乙未, 己巳, 己巳)
      with(stemCombined) {
        ew.getPatterns(甲辰, 甲戌).also { results: Set<IEightWordsFlowMonthPattern> ->
          results.forEach { println(it) }
          assertEquals(
            setOf(
              StemCombined(Scale.DAY, 己, Scale.YEAR),
              StemCombined(Scale.DAY, 己, Scale.MONTH),
              StemCombined(Scale.HOUR, 己, Scale.YEAR),
              StemCombined(Scale.HOUR, 己, Scale.MONTH),
            ), results
          )
        }
      }
    }
  }




}
