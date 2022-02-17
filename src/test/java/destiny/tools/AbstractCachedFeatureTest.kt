/**
 * Created by smallufo on 2022-02-17.
 */
package destiny.tools

import destiny.tools.AbstractCachedFeature.Companion.grainDay
import destiny.tools.AbstractCachedFeature.Companion.grainHour
import destiny.tools.AbstractCachedFeature.Companion.grainMinute
import destiny.tools.AbstractCachedFeature.Companion.grainSecond
import org.junit.jupiter.api.Nested
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AbstractCachedFeatureTest {

  @Nested
  inner class GrainTest {
    private val lmt = LocalDateTime.of(2021, 2, 17, 12, 31, 10, 123456)

    @Test
    fun testGrainSecond() {
      assertEquals(LocalDateTime.of(2021, 2, 17, 12, 31, 10), lmt.grainSecond())
    }

    @Test
    fun testGrainMinute() {
      assertEquals(LocalDateTime.of(2021, 2, 17, 12, 31), lmt.grainMinute())
    }

    @Test
    fun testGrainHour() {
      assertEquals(LocalDateTime.of(2021, 2, 17, 12, 0), lmt.grainHour())
    }

    @Test
    fun testGrainDay() {
      assertEquals(LocalDateTime.of(2021, 2, 17, 0, 0, 1), lmt.grainDay())
    }
  }


}
