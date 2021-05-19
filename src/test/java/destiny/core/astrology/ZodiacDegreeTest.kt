/**
 * Created by smallufo on 2021-05-19.
 */
package destiny.core.astrology

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
    assertTrue(ZodiacDegree(from).isOriental(ZodiacDegree(to)))
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
    assertTrue(ZodiacDegree(from).isOccidental(ZodiacDegree(to)))
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
    assertEquals(ZodiacDegree(10.0), ZodiacDegree(0.0) + 10)
    assertEquals(ZodiacDegree(0.0), ZodiacDegree(350.0) + 10)
    assertEquals(ZodiacDegree(10.0), ZodiacDegree(350.0) + 20)
  }

  @Test
  fun testMinusInt() {
    assertEquals(ZodiacDegree(350.0), ZodiacDegree(0.0) - 10)
    assertEquals(ZodiacDegree(0.0), ZodiacDegree(10.0) - 10)
    assertEquals(ZodiacDegree(350.0), ZodiacDegree(10.0) - 20)
  }


//  @Test
//  fun testAheadOf() {
//    assertEquals(1.0, ZodiacDegree(1.0).aheadOf(ZodiacDegree(0.0)))
//    assertEquals(180.0, ZodiacDegree(180.0).aheadOf(ZodiacDegree(0.0)))
//    assertEquals(181.0, ZodiacDegree(181.0).aheadOf(ZodiacDegree(0.0)))
//    assertEquals(1.0, ZodiacDegree(0.0).aheadOf(ZodiacDegree(359.0)))
//    assertEquals(180.0, ZodiacDegree(180.0).aheadOf(ZodiacDegree(360.0)))
//    assertEquals(180.0, ZodiacDegree(360.0).aheadOf(ZodiacDegree(180.0)))
//    assertEquals(180.0, ZodiacDegree(270.0).aheadOf(ZodiacDegree(90.0)))
//    assertEquals(180.0, ZodiacDegree(90.0).aheadOf(ZodiacDegree(270.0)))
//  }

//  @Test
//  fun testNormalize() {
//    //測試大於零的度數
//    assertEquals(ZodiacDegree(0.0).normalize(), ZodiacDegree(0.0))
//    assertEquals(ZodiacDegree(359.0).normalize(), ZodiacDegree(359.0))
//    assertEquals(ZodiacDegree(360.0).normalize(), ZodiacDegree(0.0))
//    assertEquals(ZodiacDegree(361.0).normalize(), ZodiacDegree(1.0))
//    assertEquals(ZodiacDegree(720.0).normalize(), ZodiacDegree(0.0))
//    assertEquals(ZodiacDegree(721.0).normalize(), ZodiacDegree(1.0))
//
//    //測試小於零的度數
//    assertEquals(ZodiacDegree(-1.0).normalize(), ZodiacDegree(359.0))
//    assertEquals(ZodiacDegree(-359.0).normalize(), ZodiacDegree(1.0))
//    assertEquals(ZodiacDegree(-360.0).normalize(), ZodiacDegree(0.0))
//    assertEquals(ZodiacDegree(-361.0).normalize(), ZodiacDegree(359.0))
//    assertEquals(ZodiacDegree(-719.0).normalize(), ZodiacDegree(1.0))
//    assertEquals(ZodiacDegree(-720.0).normalize(), ZodiacDegree(0.0))
//    assertEquals(ZodiacDegree(-721.0).normalize(), ZodiacDegree(359.0))
//  }
}
