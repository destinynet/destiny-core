/**
 * Created by smallufo on 2022-02-17.
 */
package destiny.tools

import destiny.tools.AbstractCachedFeature.Companion.fixError
import destiny.tools.AbstractCachedFeature.Companion.grainDay
import destiny.tools.AbstractCachedFeature.Companion.grainHour
import destiny.tools.AbstractCachedFeature.Companion.grainMinute
import destiny.tools.AbstractCachedFeature.Companion.grainSecond
import org.junit.jupiter.api.Nested
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class AbstractCachedFeatureTest {

  @Test
  fun fixErrorTest() {
    assertEquals(LocalDateTime.of(2021, 2, 17, 18, 30, 0), LocalDateTime.of(2021, 2, 17, 18, 29, 59, 999_990_001).fixError())
    assertEquals(LocalDateTime.of(2021, 2, 17, 18, 0, 0), LocalDateTime.of(2021, 2, 17, 17, 59, 59, 999_990_001).fixError())
    assertNotEquals(LocalDateTime.of(2021, 2, 17, 18, 0, 0), LocalDateTime.of(2021, 2, 17, 17, 59, 59, 999_990_000).fixError())
    assertNotEquals(LocalDateTime.of(2021, 2, 17, 18, 0, 0), LocalDateTime.of(2021, 2, 17, 17, 59, 59, 999_989_999).fixError())

    assertEquals(LocalDateTime.of(2021, 2, 17, 18, 30, 0), LocalDateTime.of(2021, 2, 17, 18, 30, 0, 9999).fixError())
    assertEquals(LocalDateTime.of(2021, 2, 17, 18, 0, 0), LocalDateTime.of(2021, 2, 17, 18, 0, 0, 1).fixError())
    assertEquals(LocalDateTime.of(2021, 2, 17, 18, 0, 0), LocalDateTime.of(2021, 2, 17, 18, 0, 0, 9999).fixError())
    assertNotEquals(LocalDateTime.of(2021, 2, 17, 18, 0, 0), LocalDateTime.of(2021, 2, 17, 18, 0, 0, 10000).fixError())
  }

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
