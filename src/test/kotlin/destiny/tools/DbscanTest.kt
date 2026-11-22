/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.tools

import destiny.core.astrology.ZodiacDegree
import org.apache.commons.math3.ml.clustering.Clusterable
import org.apache.commons.math3.ml.clustering.DBSCANClusterer
import kotlin.random.Random
import kotlin.test.*

/**
 * [dbscan] 取代 commons-math3 的 `DBSCANClusterer`：群的內容與**順序**（群的先後、群內點的先後）都要相同。
 */
class DbscanTest {

  private class P(val lng: Double) : Clusterable {
    override fun getPoint() = doubleArrayOf(lng)
    override fun toString() = "%.2f".formatInvariant(lng)
  }

  private fun angle(a: Double, b: Double) = ZodiacDegree.getAngle(a, b)

  private fun viaMath3(points: List<P>, eps: Double, minPts: Int): List<List<P>> =
    DBSCANClusterer<P>(eps, minPts) { a, b -> angle(a[0], b[0]) }.cluster(points).map { it.points.toList() }

  @Test
  fun `隨機黃經：與 math3 逐一相同`() {
    val random = Random(20261122)
    var nonEmpty = 0
    repeat(20_000) { round ->
      val n = random.nextInt(0, 25)
      // 一半的回合把點擠在少數幾個區段（含跨 0°／360°），才會出現多群、邊界點與雜訊
      val points = List(n) {
        if (round % 2 == 0) P(random.nextDouble(0.0, 360.0))
        else P((listOf(355.0, 10.0, 120.0, 185.0)[random.nextInt(4)] + random.nextDouble(-9.0, 9.0)).mod(360.0))
      }
      val eps = listOf(3.0, 6.0, 10.0)[round % 3]
      val minPts = listOf(0, 1, 2, 3)[round % 4]
      val expected = viaMath3(points, eps, minPts)
      val actual = dbscan(points, eps, minPts) { a, b -> angle(a.lng, b.lng) }
      // 以參照比對：同一個物件、同樣的順序
      assertEquals(expected.size, actual.size, "round=$round")
      expected.zip(actual).forEach { (e, a) ->
        assertEquals(e.size, a.size, "round=$round")
        e.zip(a).forEach { (x, y) -> assertSame(x, y, "round=$round") }
      }
      if (expected.isNotEmpty()) nonEmpty++
    }
    assertTrue(nonEmpty > 5_000, "有分出群的回合太少：$nonEmpty")
  }

  @Test
  fun `星群：至少三顆、跨 0 度`() {
    val points = listOf(P(358.0), P(1.0), P(4.0), P(180.0), P(183.0), P(90.0))
    val clusters = dbscan(points, 6.0, 2) { a, b -> angle(a.lng, b.lng) }
    assertEquals(listOf(listOf(358.0, 1.0, 4.0)), clusters.map { c -> c.map { it.lng } })   // 180／183 只有兩顆，不成群
  }

}
