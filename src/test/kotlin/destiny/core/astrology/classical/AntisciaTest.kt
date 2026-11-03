/**
 * Created by smallufo on 2026-11-02.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.AstroPoint
import destiny.core.astrology.IHoroscopeModel
import destiny.core.astrology.IPosWithAzimuth
import destiny.core.astrology.Planet.*
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.classical.rules.ClassicalPatternContext
import destiny.core.astrology.classical.rules.Misc
import destiny.core.astrology.classical.rules.PatternTranslator
import io.mockk.every
import io.mockk.mockk
import java.util.*
import kotlin.random.Random
import kotlin.test.*

class AntisciaTest {

  @Test
  fun `映點與反映點的度數`() {
    assertEquals(110.0, 70.0.toZodiacDegree().antiscion.value, 1e-9)        // 雙子 10° ↔ 巨蟹 20°
    assertEquals(80.0, 100.0.toZodiacDegree().antiscion.value, 1e-9)        // 巨蟹 10° ↔ 雙子 20°
    assertEquals(150.0, 30.0.toZodiacDegree().antiscion.value, 1e-9)        // 金牛 0° ↔ 處女 0°
    assertEquals(330.0, 30.0.toZodiacDegree().contraAntiscion.value, 1e-9)  // 金牛 0° ↔ 雙魚 0°
    assertEquals(0.0, 0.0.toZodiacDegree().contraAntiscion.value, 1e-9)
  }

  /** 太陽 70°（映點 110°、反映點 290°）：金星 110.5° 映點（差 0.5°）、火星 290.2° 反映點（差 0.2°）、木星 112° 差 2° 不收 */
  private val h: IHoroscopeModel = run {
    val posMap: Map<AstroPoint, IPosWithAzimuth> = mapOf(SUN to 70.0, VENUS to 110.5, MARS to 290.2, JUPITER to 112.0)
      .mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
    mockk { every { positionMap } returns posMap }
  }

  @Test
  fun `一對星一筆 容許度 反映點可關`() {
    val found = AntisciaImpl().getResults(h)
    assertEquals(setOf(Triple(SUN, VENUS, false), Triple(SUN, MARS, true)), found.map { Triple(it.planet, it.other, it.contra) }.toSet())
    assertEquals(0.5, found.single { !it.contra }.orb, 1e-9)

    val noContra = AntisciaImpl(AntisciaOptions(contra = false)).getResults(h)
    assertEquals(listOf(VENUS), noContra.map { it.other })
    assertEquals(3, AntisciaImpl(AntisciaOptions(orb = 2.5)).getResults(h).size, "放寬到 2.5° 收進木星")
  }

  @Test
  fun `判斷層與句子`() {
    val ctx = ClassicalPatternContext(
      RulerPtolemyImpl, mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true), mockk(relaxed = true),
      antisciaImpl = AntisciaImpl(),
    )
    assertTrue(ctx.antiscion in ctx.defaultFactories)
    val sun = ctx.antiscion.getPatterns(SUN, h).filterIsInstance<Misc.InAntiscia>()
    assertEquals(2, sun.size)
    assertTrue(ctx.antiscion.getPatterns(VENUS, h).isEmpty(), "掛在排前面的那顆，不重複")
    val zh = sun.map { PatternTranslator.getDescriptor(it).getDescription(Locale.TAIWAN) }
    assertTrue(zh.any { "太陽 與 金星 互為映點（相差 0.50°）" in it } && zh.any { "太陽 與 火星 互為反映點" in it }, zh.toString())
    val en = PatternTranslator.getDescriptor(sun.first { !it.antiscion.contra }).getDescription(Locale.ENGLISH)
    assertTrue("Sun and Venus are in antiscia (0.50° off)" in en, en)
  }

  /**
   * 映點、反映點的數學性質（固定種子的隨機度數）：
   * 對合（映點的映點、反映點的反映點都回到原處）、反映點 ＝ 映點的對沖點、
   * 對稱（A 的映點與 B 的差距 ＝ B 的映點與 A 的差距 —— [AntisciaImpl] 一對星只判一次，就靠這一條）
   */
  @Test
  fun `映點與反映點的性質`() {
    val rnd = Random(20261103)
    repeat(5000) {
      val a = (rnd.nextDouble() * 360).toZodiacDegree()
      val b = (rnd.nextDouble() * 360).toZodiacDegree()
      assertEquals(0.0, a.antiscion.antiscion.getAngle(a), 1e-9, "映點對合 $a")
      assertEquals(0.0, a.contraAntiscion.contraAntiscion.getAngle(a), 1e-9, "反映點對合 $a")
      assertEquals(180.0, a.antiscion.getAngle(a.contraAntiscion), 1e-9, "反映點是映點的對沖點 $a")
      assertEquals(a.antiscion.getAngle(b), b.antiscion.getAngle(a), 1e-9, "映點對稱 $a $b")
      assertEquals(a.contraAntiscion.getAngle(b), b.contraAntiscion.getAngle(a), 1e-9, "反映點對稱 $a $b")
    }
  }

  /**
   * 實作層的對稱：[AntisciaOptions.points] 反過來排，找到的星對（不分前後）、種類與差距都一樣。
   * 度數取 0.25° 的倍數（浮點精確，容許度邊界不會因誤差翻轉），並刻意把一部分星放在別顆的映點、反映點附近
   */
  @Test
  fun `星的順序不影響結果`() {
    val rnd = Random(20261103)
    fun quarter(range: Int) = rnd.nextInt(range * 4) / 4.0
    val points = AntisciaOptions.DEFAULT.points
    val forward = AntisciaImpl()
    val backward = AntisciaImpl(AntisciaOptions(points = points.reversed()))
    var found = 0
    repeat(1000) {
      val lngs = mutableMapOf<AstroPoint, Double>()
      points.forEach { p ->
        val anchor = lngs.values.randomOrNull(rnd)
        lngs[p] = if (anchor != null && rnd.nextBoolean()) {
          val mirror = if (rnd.nextBoolean()) 180.0 - anchor else 360.0 - anchor
          ((mirror + quarter(3) - 1.5) % 360 + 360) % 360
        } else quarter(360)
      }
      val posMap: Map<AstroPoint, IPosWithAzimuth> = lngs.mapValues { (_, d) -> mockk<IPosWithAzimuth> { every { lngDeg } returns d.toZodiacDegree() } }
      val chart = mockk<IHoroscopeModel> { every { positionMap } returns posMap }
      fun IAntiscia.normalized(): Set<Triple<Set<AstroPoint>, Boolean, Double>> =
        getResults(chart).map { Triple(setOf(it.planet, it.other), it.contra, it.orb) }.toSet()
      val f = forward.normalized()
      assertEquals(f, backward.normalized(), "lngs=$lngs")
      found += f.size
    }
    assertTrue(found > 500, "隨機盤要有夠多的映點，測試才有意義：$found")
  }
}
