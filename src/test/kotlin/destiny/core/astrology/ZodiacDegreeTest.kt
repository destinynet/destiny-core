/**
 * Created by smallufo on 2021-05-19.
 */
package destiny.core.astrology

import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ZodiacDegreeTest {

  private fun between() = Stream.of(
    Arguments.of(0.0, 359.0, 1.0, true),
    Arguments.of(0, 1, 359, true),
    Arguments.of(0, 270.1, 90, true),
    Arguments.of(0, 271, 90, true),
    Arguments.of(0, 270, 89.9, true),
    Arguments.of(180, 359.0, 1.0, false),
    Arguments.of(180, 1, 359, false),
    Arguments.of(180, 270.1, 90, false),
    Arguments.of(180, 271, 90, false),
    Arguments.of(180, 270, 89.9, false),


    Arguments.of(90, 0.1, 179.9, true),
    Arguments.of(90, 179.9, 0.1, true),
    Arguments.of(270, 0.1, 179.9, false),
    Arguments.of(270, 179.9, 0.1, false),

    Arguments.of(180, 90.1, 269.9, true),
    Arguments.of(180, 269.9, 90.1, true),
    Arguments.of(0, 90.1, 269.9, false),
    Arguments.of(0, 269.9, 90.1, false),

    Arguments.of(270, 180.1, 359.9, true),
    Arguments.of(270, 359.9, 180.1, true),
    Arguments.of(90, 180.1, 359.9, false),
    Arguments.of(90, 359.9, 180.1, false),

    // 以下為 edge 貼合 cases
    Arguments.of(1.0, 1.0, 2.0, true),
    Arguments.of(1.0, 2.0, 1.0, true),
    Arguments.of(359.0, 359.0, 1.0, true),
    Arguments.of(359.0, 1.0, 359.0, true),
    Arguments.of(179.0, 179.0, 181.0, true),
    Arguments.of(179.0, 181.0, 179.0, true),


  )

  @ParameterizedTest
  @MethodSource("between")
  fun between(deg: Double, from: Double, to: Double, result: Boolean) {
    if (result) {
      assertTrue(deg.toZodiacDegree().between(from.toZodiacDegree(), to.toZodiacDegree()))
    } else {
      assertFalse(deg.toZodiacDegree().between(from.toZodiacDegree(), to.toZodiacDegree()))
    }
  }

  /** 東出 , 度數小 */
  private fun oriental(): Stream<Pair<Double, Double>> = Stream.of(
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
  private fun occidental(): Stream<Pair<Double, Double>> = Stream.of(
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

  @Test
  fun testMin() {
    assertEquals(0, 0.toZodiacDegree().intMin)
    assertEquals(6, 0.1.toZodiacDegree().intMin)
    assertEquals(30, 0.5.toZodiacDegree().intMin)
    assertEquals(54, 0.9.toZodiacDegree().intMin)
  }

  @ParameterizedTest(name = "midpoint between {0}° and {1}° should be {2}°")
  @CsvSource(
    "0.0, 100.0, 50.0     ",
    "0.0, 179.0, 89.5     ",
    "0.0, 181.0, 270.5    ",
    "350.0, 10.0, 0.0     ",
    "300.0, 60.0, 0.0     ",
    "-10.0, 10.0, 0.0     ",
    "370.0, 10.0, 10.0    ",
    "0.0, 0.0, 0.0        ",
    "0.0, 360.0, 0.0      ",
    "45.0, 135.0, 90.0    ",
    "315.0, 45.0, 0.0     ",
    "359.0, 1.0, 0.0      ",
    "179.0, 181.0, 180.0  ",
    "10.5, 20.5, 15.5     ",
    "350.5, 10.5, 0.5     ",
    "10.0, 350.0, 0.0     ",
    "350.0, 10.0, 0.0     ",
    "355.0, 5.0, 0.0      ",
    "175.0, 185.0, 180.0  ",
  )
  fun testMidPoint(deg1: Double, deg2: Double, expected: Double) {
    val point1 = deg1.toZodiacDegree()
    val point2 = deg2.toZodiacDegree()

    // 測試正向順序
    assertEquals(
      expected,
      point1.midPoint(point2).value,
      "Midpoint between $deg1° and $deg2° should be $expected°"
    )

    // 測試反向順序
    assertEquals(
      expected,
      point2.midPoint(point1).value,
      "Midpoint between $deg2° and $deg1° should be $expected°"
    )
  }
}
