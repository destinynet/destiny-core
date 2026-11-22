/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.tools

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class AnglesTest {

  @Test
  fun `與 JDK Math 逐位元相同`() {
    val random = Random(20261122)
    val samples = List(200_000) { random.nextDouble(-1e4, 1e4) } +
      listOf(0.0, -0.0, 90.0, 180.0, 360.0, 1e-300, Double.MAX_VALUE, Double.NaN, Double.POSITIVE_INFINITY)
    samples.forEach { d ->
      assertEquals(Math.toRadians(d).toRawBits(), toRadians(d).toRawBits(), "toRadians($d)")
      assertEquals(Math.toDegrees(d).toRawBits(), toDegrees(d).toRawBits(), "toDegrees($d)")
    }
  }
}
