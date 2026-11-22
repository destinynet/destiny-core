/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.Planet
import destiny.core.calendar.GmtJulDay
import kotlin.math.abs
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RingTimelineTest {

  private val t0 = GmtJulDay(2_460_000.0)

  /** 等速 0.1°／天，從 0° 走起 */
  private fun linear(t: GmtJulDay) = ((t - t0) * 0.1).mod(360.0)

  private fun sample(step: Double, days: Int, f: (GmtJulDay) -> Double): Pair<List<GmtJulDay>, List<Double>> {
    val times = (0..(days / step).toInt()).map { t0 + it * step }
    return times to times.map(f)
  }

  @Test
  fun `等速通過：一段區間、進出各在精準前後 orb 度、精準在中間`() {
    val (times, lngs) = sample(10.0, 400, ::linear)
    // 本命 20°、合相、容許度 1.5° ⇒ 18.5°–21.5° ＝ 第 185–215 天，精準第 200 天
    val spans = aspectSpansOf(Planet.SATURN, Planet.SUN, 20.0, Aspect.CONJUNCTION, 1.5, times, lngs, ::linear)
    val s = spans.single()
    assertEquals(185.0, s.enter - t0, 0.01)
    assertEquals(215.0, s.leave - t0, 0.01)
    assertEquals(200.0, s.exacts.single() - t0, 0.01)
  }

  @Test
  fun `四分相左右各一：本命 ±90 兩段都列`() {
    val (times, lngs) = sample(10.0, 3600, ::linear)
    val spans = aspectSpansOf(Planet.SATURN, Planet.SUN, 180.0, Aspect.SQUARE, 1.0, times, lngs, ::linear)
    // 90° 在第 900 天、270° 在第 2700 天
    assertEquals(listOf(900.0, 2700.0), spans.map { Math.round(it.exacts.single() - t0).toDouble() })
  }

  @Test
  fun `逆行來回：精準好幾次，與逐百分之一天暴力找的次數相同`() {
    // 繞著 20° 前後擺動（振幅 2°，週期 200 天），整體慢慢往前 0.004°／天：先來回三次，最後離開
    val f = { t: GmtJulDay -> val x = t - t0; 18.5 + x * 0.004 + 2.0 * sin(x / 200.0 * 2 * Math.PI) }
    val (times, lngs) = sample(5.0, 500, f)
    val spans = aspectSpansOf(Planet.JUPITER, Planet.MOON, 20.0, Aspect.CONJUNCTION, 3.0, times, lngs, f)
    val brute = (0 until 50_000).count { i -> (f(t0 + i * 0.01) - 20.0) * (f(t0 + (i + 1) * 0.01) - 20.0) < 0 }
    val exacts = spans.flatMap { it.exacts }
    assertTrue(brute >= 3)
    assertEquals(brute, exacts.size)
    assertTrue(exacts.zipWithNext().all { (a, b) -> a < b })
    exacts.forEach { assertTrue(abs(f(it) - 20.0) < 0.01) }
    // 精準一定落在某一段容許度區間之內
    assertTrue(spans.all { s -> s.exacts.all { it >= s.enter && it <= s.leave } })
  }

  @Test
  fun `取樣開頭就在容許度內：區間從取樣的第一刻開始`() {
    val (times, lngs) = sample(10.0, 100, ::linear)
    val s = aspectSpansOf(Planet.SATURN, Planet.SUN, 0.5, Aspect.CONJUNCTION, 1.5, times, lngs, ::linear).single()
    assertEquals(t0, s.enter)
  }

  @Test
  fun `跨過 0 度：雙魚尾到牡羊頭也算得到`() {
    val f = { t: GmtJulDay -> (355.0 + (t - t0) * 0.1).mod(360.0) }
    val (times, lngs) = sample(10.0, 200, f)
    val s = aspectSpansOf(Planet.SATURN, Planet.SUN, 359.5, Aspect.CONJUNCTION, 1.0, times, lngs, f).single()
    assertEquals(45.0, s.exacts.single() - t0, 0.01)
  }

  @Test
  fun `RingTrack 的時刻與頭尾`() {
    val track = RingTrack(destiny.core.astrology.RingKind.TRANSIT, t0, 10.0, listOf(Planet.SATURN), listOf(listOf(1.0, 2.0, 3.0)), listOf(listOf(false, false, true)))
    assertEquals(3, track.size)
    assertEquals(t0 + 20.0, track.to)
  }

  @Test
  fun `lngAt：線性內插，跨過 0 度走短的那一邊，超出範圍取頭尾`() {
    val track = RingTrack(destiny.core.astrology.RingKind.TRANSIT, t0, 10.0, listOf(Planet.SATURN), listOf(listOf(358.0, 2.0, 6.0)))
    assertEquals(0.0, track.lngAt(0, t0 + 5.0), 1e-9)
    assertEquals(4.0, track.lngAt(0, t0 + 15.0), 1e-9)
    assertEquals(358.0, track.lngAt(0, t0 - 3.0), 1e-9)
    assertEquals(6.0, track.lngAt(0, t0 + 99.0), 1e-9)
  }

  @Test
  fun `走得快、一步超過十度也找得到精準（不再寫死離精準十度以內）`() {
    // 月亮約 13°／天，一天取一筆：本命 100° 的合相在第 100/13 天
    val moon = { t: GmtJulDay -> ((t - t0) * 13.0).mod(360.0) }
    val (times, lngs) = sample(1.0, 20, moon)
    val exacts = exactTimesOf(100.0, 0.0, times, lngs, moon)
    assertEquals(100.0 / 13.0, exacts.single() - t0, 1e-4)
  }

  @Test
  fun `正對面的變號不是精準`() {
    // 從 170° 走到 190°：對本命 0° 的合相，角距在 ±180 變號，不該算
    val f = { t: GmtJulDay -> 170.0 + (t - t0) * 1.0 }
    val (times, lngs) = sample(5.0, 20, f)
    assertTrue(exactTimesOf(0.0, 0.0, times, lngs, f).isEmpty())
    // 同一段對沖（side 180）才是精準：第 10 天
    assertEquals(10.0, exactTimesOf(0.0, 180.0, times, lngs, f).single() - t0, 1e-4)
  }

  @Test
  fun `相位的兩個方向：合沖一個、其餘左右各一`() {
    assertEquals(listOf(0.0), Aspect.CONJUNCTION.sides)
    assertEquals(listOf(180.0), Aspect.OPPOSITION.sides)
    assertEquals(listOf(90.0, -90.0), Aspect.SQUARE.sides)
    assertEquals(listOf(120.0, -120.0), Aspect.TRINE.sides)
  }
}
