/**
 * Created by smallufo on 2026-10-31.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.Planet.*
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.Location
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BesiegementPorphyryImplTest {

  private val impl = BesiegementPorphyryImpl()

  private val maleficEnclosers = setOf(MARS, SATURN)

  /** 只有黃經的盤；沒列到的古典行星放在遠處，不影響判斷 */
  private fun chart(vararg lngs: Pair<Planet, Double>): IHoroscopeModel {
    val given = lngs.toMap()
    // 遠處的星：彼此錯開，也避開 60/90/120/180 的光線落到 0°～30° 之間
    val far = listOf(200.0, 203.0, 206.0, 209.0, 212.0, 215.0, 218.0)
    val positions: Map<AstroPoint, IPosWithAzimuth> = Planet.classicalList.mapIndexed { i, p ->
      p to PosWithAzimuth(Pos(given[p] ?: far[i], 0.0), Azimuth(0.0, 0.0, 0.0))
    }.toMap()
    return HoroscopeModel(
      GmtJulDay(2451545.0), Location.of(25.0, 121.5), HoroscopeConfig(points = Planet.classicalSet),
      positions, emptyMap(), emptyMap(), null, emptyMap(), emptyMap()
    )
  }

  @Test
  fun byBody_bothSidesWithinSevenDegrees() {
    val h = chart(SATURN to 1.0, VENUS to 4.0, MARS to 9.0)
    impl.getBesiegement(h, VENUS, maleficEnclosers).also { b ->
      assertNotNull(b)
      assertSame(SATURN, b.before.by)
      assertSame(MARS, b.after.by)
      assertTrue(b.byBody)
      assertEquals(3.0, b.before.distance, 1e-9)
      assertEquals(5.0, b.after.distance, 1e-9)
    }
  }

  /** 7° 是每一側各自量，不是兩側合計 */
  @Test
  fun eachSideMeasuredSeparately() {
    assertNotNull(impl.getBesiegement(chart(SATURN to 0.0, VENUS to 6.0, MARS to 12.0), VENUS, maleficEnclosers))
    assertNull(impl.getBesiegement(chart(SATURN to 0.0, VENUS to 6.0, MARS to 13.5), VENUS, maleficEnclosers))
  }

  /** 同一星座頭尾的兩顆凶星，中間的星不算被夾（距離太遠） */
  @Test
  fun tooFar() {
    assertNull(impl.getBesiegement(chart(SATURN to 1.0, VENUS to 15.0, MARS to 29.0), VENUS, maleficEnclosers))
  }

  /** 一側星體、一側光線：土星在 4°，火星以四分投射到 10°（火星本身在 100°） */
  @Test
  fun bodyAndRay() {
    val h = chart(SATURN to 4.0, VENUS to 7.0, MARS to 100.0)
    impl.getBesiegement(h, VENUS, maleficEnclosers).also { b ->
      assertNotNull(b)
      assertSame(SATURN, b.before.by)
      assertEquals(null, b.before.aspect)
      assertSame(MARS, b.after.by)
      assertSame(Aspect.SQUARE, b.after.aspect)
      assertEquals(3.0, b.after.distance, 1e-9)
    }
    // 凶星光線只算對分時，火星的四分光線不算；只算星體時也不算
    assertNull(BesiegementPorphyryImpl(BesiegementOptions(maleficRays = setOf(Aspect.OPPOSITION))).getBesiegement(h, VENUS, maleficEnclosers))
    assertNull(BesiegementPorphyryImpl(BesiegementOptions(byRays = false)).getBesiegement(h, VENUS, maleficEnclosers))
  }

  /** 中間有第三顆星就破了：土 1°、金 3°、月 5°、火 7°，金與月都不算被夾 */
  @Test
  fun intervention() {
    val h = chart(SATURN to 1.0, VENUS to 3.0, MOON to 5.0, MARS to 7.0)
    assertNull(impl.getBesiegement(h, VENUS, maleficEnclosers))
    assertNull(impl.getBesiegement(h, MOON, maleficEnclosers))
  }

  /** 第三顆星的光線介入也算：太陽以六合投射到 5°（太陽本身在 65°） */
  @Test
  fun interventionByRay() {
    assertNotNull(impl.getBesiegement(chart(SATURN to 1.0, VENUS to 3.0, MARS to 7.0), VENUS, maleficEnclosers))
    assertNull(impl.getBesiegement(chart(SATURN to 1.0, VENUS to 3.0, MARS to 7.0, SUN to 65.0), VENUS, maleficEnclosers))
  }

  /** 跨越 0° 牡羊 */
  @Test
  fun acrossAriesPoint() {
    val b = impl.getBesiegement(chart(SATURN to 357.0, VENUS to 1.0, MARS to 4.0), VENUS, maleficEnclosers)
    assertNotNull(b)
    assertSame(SATURN, b.before.by)
    assertEquals(4.0, b.before.distance, 1e-9)
  }

  /** 要求同星座時，跨星座的那一側不算：土星在雙魚 28°、金星在牡羊 1°、火星在牡羊 4° */
  @Test
  fun sameSign() {
    val h = chart(SATURN to 358.0, VENUS to 1.0, MARS to 4.0)
    assertNotNull(impl.getBesiegement(h, VENUS, maleficEnclosers))
    assertNull(BesiegementPorphyryImpl(BesiegementOptions(sameSign = true)).getBesiegement(h, VENUS, maleficEnclosers))
  }

  /** 兩側必須是不同的包夾星 */
  @Test
  fun sameEncloserBothSides() {
    // 金星前方只有火星的星體（2°），後方沒有任何一顆凶星的星體或光線
    assertNull(impl.getBesiegement(chart(MARS to 2.0, VENUS to 5.0, SATURN to 300.0), VENUS, maleficEnclosers))
  }
}
