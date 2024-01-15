/**
 * Created by smallufo on 2024-01-16.
 */
package destiny.core.calendar.eightwords

import destiny.core.Scale
import destiny.core.chinese.Stem.*
import destiny.core.chinese.StemBranch.*
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
}
