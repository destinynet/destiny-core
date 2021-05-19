/**
 * Created by smallufo on 2021-05-19.
 */
package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZodiacDegreeTest {

  /** 東出 , 度數小 */
  fun oriental(): Stream<Pair<Double, Double>> = Stream.of(
    0.0 to 1.0,
    0.0 to 90.0,
    0.0 to 179.9,

    181.0 to 0.0,
    270.0 to 0.0,
    359.9 to 0.0,
  )

  @ParameterizedTest
  @MethodSource("oriental")
  fun testOriental(row: Pair<Double, Double>) {
    val (from, to) = row
    assertTrue(from.toZodiacDegree().isOriental(to.toZodiacDegree()))
  }

  /** 西出 , 度數大 */
  fun occidental(): Stream<Pair<Double, Double>> = Stream.of(
    1.0 to 0.0,
    90.0 to 0.0,
    179.9 to 0.0,

    0.0 to 359.9,
    0.0 to 270.0,
    0.0 to 181.0,
  )

  @ParameterizedTest
  @MethodSource("occidental")
  fun testOccidental(row: Pair<Double, Double>) {
    val (from, to) = row
    assertTrue(from.toZodiacDegree().isOccidental(to.toZodiacDegree()))
  }

  @Test
  fun testGetAngle() {
    assertEquals(ZodiacDegree.getAngle(1.0, 0.0), 1.0)
    assertEquals(ZodiacDegree.getAngle(179.0, 0.0), 179.0)
    assertEquals(ZodiacDegree.getAngle(180.0, 0.0), 180.0)
    assertEquals(ZodiacDegree.getAngle(181.0, 0.0), 179.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 179.0), 179.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 180.0), 180.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 181.0), 179.0)
    assertEquals(ZodiacDegree.getAngle(359.0, 0.0), 1.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 1.0), 1.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 179.0), 179.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 180.0), 180.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 181.0), 179.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 359.0), 1.0)
    assertEquals(ZodiacDegree.getAngle(270.0, 90.0), 180.0)
    assertEquals(ZodiacDegree.getAngle(271.0, 90.0), 179.0)
    assertEquals(ZodiacDegree.getAngle(359.0, 90.0), 91.0)
    assertEquals(ZodiacDegree.getAngle(0.0, 90.0), 90.0)
    assertEquals(ZodiacDegree.getAngle(89.0, 90.0), 1.0)
    assertEquals(ZodiacDegree.getAngle(90.0, 270.0), 180.0)
    assertEquals(ZodiacDegree.getAngle(90.0, 271.0), 179.0)
    assertEquals(ZodiacDegree.getAngle(90.0, 359.0), 91.0)
    assertEquals(ZodiacDegree.getAngle(90.0, 0.0), 90.0)
    assertEquals(ZodiacDegree.getAngle(90.0, 89.0), 1.0)
  }


  @Test
  fun testPlusInt() {
    assertEquals(10.0.toZodiacDegree(), 0.0.toZodiacDegree() + 10)
    assertEquals(0.0.toZodiacDegree(), 350.0.toZodiacDegree() + 10)
    assertEquals(10.0.toZodiacDegree(), 350.0.toZodiacDegree() + 20)
  }

  @Test
  fun testMinusInt() {
    assertEquals(350.0.toZodiacDegree(), 0.0.toZodiacDegree() - 10)
    assertEquals(0.0.toZodiacDegree(), 10.0.toZodiacDegree() - 10)
    assertEquals(350.0.toZodiacDegree(), 10.0.toZodiacDegree() - 20)
  }

}
