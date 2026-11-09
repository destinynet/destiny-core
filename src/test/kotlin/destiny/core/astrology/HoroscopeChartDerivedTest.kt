/**
 * Created by smallufo on 2026-11-09.
 */
package destiny.core.astrology

import com.jayway.jsonpath.JsonPath
import destiny.core.Gender
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** [withDerivedDegrees]：各衍生欄位的定義、只由 JSON 裡看得到的值算、只補空的、舊 JSON 解得開 */
class HoroscopeChartDerivedTest {

  private fun point(key: AstroPoint, zDeg: Double) = HoroscopePointDto(
    point = key, label = key.nameKey, glyph = null, zDeg = zDeg, sign = zDeg.toZodiacDegree().sign, signDegree = zDeg % 30, house = null,
    retrograde = false, kind = key.kind)

  private fun chart(
    points: List<HoroscopePointDto>, facts: List<HoroscopeClassicalFactDto> = emptyList(), patterns: List<HoroscopePatternDto> = emptyList(),
  ) = HoroscopeChartDto(
    meta = HoroscopeMetaDto(gender = Gender.F, ascDeg = 0.0, mcDeg = 0.0),
    points = points, houses = emptyList(), aspects = emptyList(), patterns = patterns, classicalFacts = facts,
  )

  /** 一張手寫的小盤：太陽 100.259（JSON 是 100.25）、月亮 79.5、水星 350、金星 12、火星 200、木星 30.5、上升 100 */
  private val points = listOf(
    point(Planet.SUN, 100.259), point(Planet.MOON, 79.5), point(Planet.MERCURY, 350.0), point(Planet.VENUS, 12.0),
    point(Planet.MARS, 200.0), point(Planet.JUPITER, 30.5), point(Axis.RISING, 100.0),
  )

  private fun sample(days: Double, applying: Double, applied: Double) = HoroscopeOutcomeSampleDto(days, applying, applied, byLng = 0.0)

  private fun prohibition(eventDays: Double, track: List<HoroscopeOutcomeSampleDto>, aspect: Aspect = Aspect.CONJUNCTION, orb: Double = 3.0) =
    HoroscopeProhibitionDto(
      applying = Planet.MERCURY, applied = Planet.SUN, aspect = aspect, glyph = aspect.glyph, orb = orb,
      by = Planet.MARS, with = Planet.SUN, byAspect = Aspect.CONJUNCTION, byGlyph = "☌", eventDays = eventDays, track = track,
    )

  private fun frustration(eventDays: Double, track: List<HoroscopeOutcomeSampleDto>, aspect: Aspect) = HoroscopeFrustrationDto(
    applying = Planet.MERCURY, applied = Planet.SUN, aspect = aspect, glyph = aspect.glyph, orb = 3.0,
    by = Planet.MARS, byAspect = Aspect.CONJUNCTION, byGlyph = "☌", eventDays = eventDays, track = track,
  )

  private fun translation(from: AstroPoint, to: AstroPoint) = HoroscopeTranslationDto(
    by = Planet.MOON, from = from, fromAspect = Aspect.SEXTILE, fromGlyph = "⚹", fromOrb = 1.0,
    to = to, toAspect = Aspect.TRINE, toGlyph = "△", toOrb = 2.0,
  )

  private fun collection(a: AstroPoint, b: AstroPoint) = HoroscopeCollectionDto(
    by = Planet.JUPITER, a = a, aAspect = Aspect.SEXTILE, aGlyph = "⚹", aOrb = 1.0, b = b, bAspect = Aspect.TRINE, bGlyph = "△", bOrb = 2.0,
  )

  private fun stellium(vararg members: AstroPoint) =
    HoroscopePatternDto(type = HoroscopePatternType.STELLIUM_SIGN, points = members.toList(), sign = ZodiacSign.ARIES, legs = emptyList())

  private inline fun <reified T> HoroscopeChartDto.fact(index: Int = 0): T = classicalFacts[index] as T

  @Test
  fun `傳遞與收集 兩端的最短角距 跨過 0 度也對`() {
    val dto = chart(points, listOf(translation(Planet.MARS, Planet.JUPITER), collection(Planet.MERCURY, Planet.VENUS))).withDerivedDegrees()
    assertEquals(169.5, dto.fact<HoroscopeTranslationDto>(0).endsGap)
    assertEquals(22.0, dto.fact<HoroscopeCollectionDto>(1).endsGap)
  }

  /** 兩個兩位小數的黃經相減會帶浮點誤差（314.38 − 26.03 是 71.64999999999998）：整理成兩位小數，JSON 裡是乾淨的 71.65，再編碼也不變 */
  @Test
  fun `傳遞的兩端角距整理到兩位小數 存進 JSON 再讀回來也一樣`() {
    val pts = listOf(point(Planet.SATURN, 314.38), point(Planet.JUPITER, 26.03), point(Planet.MOON, 0.0))
    val dto = chart(pts, listOf(translation(Planet.SATURN, Planet.JUPITER))).withDerivedDegrees()
    val gap = dto.fact<HoroscopeTranslationDto>().endsGap!!
    assertEquals(71.65, gap)
    assertEquals(gap, Json.decodeFromString(HoroscopeChartDto.serializer(), strict.encodeToString(HoroscopeChartDto.serializer(), dto)).fact<HoroscopeTranslationDto>().endsGap)
  }

  @Test
  fun `映點 兩顆星各自離較近的至點 黃經先照 JSON 捨去`() {
    val dto = chart(points, listOf(HoroscopeAntisciaDto(Planet.SUN, Planet.MOON, contra = false, orb = 0.24))).withDerivedDegrees()
    val a = dto.fact<HoroscopeAntisciaDto>()
    // 太陽 100.259 在 JSON 是 100.25：離夏至（90°）10.25，不是 10.26
    assertEquals(10.25, a.planetOffAxis)
    assertEquals(10.5, a.otherOffAxis)
  }

  @Test
  fun `反映點 量的是分點 兩顆星量同一端`() {
    val dto = chart(points, listOf(HoroscopeAntisciaDto(Planet.MERCURY, Planet.VENUS, contra = true, orb = 2.0))).withDerivedDegrees()
    val a = dto.fact<HoroscopeAntisciaDto>()
    assertEquals(10.0, a.planetOffAxis)
    assertEquals(12.0, a.otherOffAxis)
  }

  @Test
  fun `插隊 軌跡內插到事件那一刻離相位角還差幾度`() {
    val track = listOf(sample(0.0, 10.0, 13.0), sample(1.0, 11.5, 14.0))
    // 事件在最後一個取樣點：11.5 與 14 合相還差 2.5
    assertEquals(2.5, chart(points, listOf(prohibition(1.0, track))).withDerivedDegrees().fact<HoroscopeProhibitionDto>().gapAtEvent)
    // 事件在兩個取樣點中間：內插成 10.75 與 13.5
    assertEquals(2.75, chart(points, listOf(prohibition(0.5, track))).withDerivedDegrees().fact<HoroscopeProhibitionDto>().gapAtEvent)
  }

  @Test
  fun `撲空 相位角取離盤面時刻較近的那一側 軌跡跨過 0 度也對`() {
    // 追的在被追的後面 63 度，往六分（差 60）靠近；軌跡跨過 0°
    val track = listOf(sample(0.0, 358.0, 61.0), sample(2.0, 1.0, 62.5))
    val dto = chart(points, listOf(frustration(2.0, track, Aspect.SEXTILE))).withDerivedDegrees()
    assertEquals(1.5, dto.fact<HoroscopeFrustrationDto>().gapAtEvent)
  }

  @Test
  fun `插隊 事件就在盤面時刻時是盤面的容許度 軌跡是空的算不出來`() {
    val track = listOf(sample(0.0, 10.0, 13.01), sample(1.0, 11.5, 14.0))
    // eventDays 0.004 在 JSON 是 0：那一刻的差距就是 orb（捨去後 3.0），不是軌跡相減的 3.01
    assertEquals(3.0, chart(points, listOf(prohibition(0.004, track, orb = 3.009))).withDerivedDegrees().fact<HoroscopeProhibitionDto>().gapAtEvent)
    assertNull(chart(points, listOf(prohibition(1.0, emptyList()))).withDerivedDegrees().fact<HoroscopeProhibitionDto>().gapAtEvent)
  }

  @Test
  fun `群星聚集 成員跨的度數 四軸不算 跨過 0 度也對 其餘格局沒有`() {
    val tSquare = HoroscopePatternDto(type = HoroscopePatternType.T_SQUARE, points = listOf(Planet.SUN, Planet.MARS, Planet.JUPITER), legs = emptyList())
    val dto = chart(points, patterns = listOf(stellium(Planet.MERCURY, Planet.VENUS, Planet.JUPITER, Axis.RISING), tSquare)).withDerivedDegrees()
    // 350 → 12 → 30.5：跨 40.5（上升 100 不算）
    assertEquals(40.5, dto.patterns[0].span)
    assertNull(dto.patterns[1].span)
  }

  @Test
  fun `成員不在盤上就留空`() {
    val few = points.filter { it.point == Planet.SUN }
    val dto = chart(few, listOf(translation(Planet.MARS, Planet.JUPITER), HoroscopeAntisciaDto(Planet.SUN, Planet.MOON, false, 0.2)),
                    listOf(stellium(Planet.MERCURY, Planet.VENUS))).withDerivedDegrees()
    assertNull(dto.fact<HoroscopeTranslationDto>(0).endsGap)
    assertNull(dto.fact<HoroscopeAntisciaDto>(1).planetOffAxis)
    assertNull(dto.fact<HoroscopeAntisciaDto>(1).otherOffAxis)
    assertNull(dto.patterns[0].span)
  }

  private val full = chart(
    points,
    listOf(
      translation(Planet.MARS, Planet.JUPITER), collection(Planet.MERCURY, Planet.VENUS),
      HoroscopeAntisciaDto(Planet.SUN, Planet.MOON, contra = false, orb = 0.24),
      prohibition(0.5, listOf(sample(0.0, 10.0, 13.0), sample(1.0, 11.5, 14.0))),
      frustration(2.0, listOf(sample(0.0, 358.0, 61.0), sample(2.0, 1.0, 62.5)), Aspect.SEXTILE),
    ),
    listOf(stellium(Planet.MERCURY, Planet.VENUS, Planet.JUPITER)),
  )

  @Test
  fun `重複呼叫結果不變 已經有值的不動 拿掉軌跡之後留得住`() {
    val once = full.withDerivedDegrees()
    assertEquals(once, once.withDerivedDegrees())

    val preset = chart(points, listOf(prohibition(1.0, listOf(sample(0.0, 10.0, 13.0), sample(1.0, 11.5, 14.0))).copy(gapAtEvent = 9.99)))
    assertEquals(9.99, preset.withDerivedDegrees().fact<HoroscopeProhibitionDto>().gapAtEvent)

    val trackless = once.copy(classicalFacts = once.classicalFacts.map { it.withoutTrack() }).withDerivedDegrees()
    assertEquals(2.75, trackless.fact<HoroscopeProhibitionDto>(3).gapAtEvent)
  }

  /** 與各種 Json 設定無關：還沒填的欄位不輸出 */
  private val strict = Json { encodeDefaults = true; explicitNulls = true }

  @Test
  fun `沒填的欄位不輸出 填了的照兩位小數輸出`() {
    val bare = strict.encodeToString(HoroscopeChartDto.serializer(), full)
    listOf("endsGap", "gapAtEvent", "planetOffAxis", "otherOffAxis", "span").forEach { assertFalse("\"$it\"" in bare, it) }

    val json = strict.encodeToString(HoroscopeChartDto.serializer(), full.withDerivedDegrees())
    assertEquals(169.5, JsonPath.read<Double>(json, "$.classicalFacts[0].endsGap"))
    assertEquals(22.0, JsonPath.read<Double>(json, "$.classicalFacts[1].endsGap"))
    assertEquals(10.25, JsonPath.read<Double>(json, "$.classicalFacts[2].planetOffAxis"))
    assertEquals(10.5, JsonPath.read<Double>(json, "$.classicalFacts[2].otherOffAxis"))
    assertEquals(2.75, JsonPath.read<Double>(json, "$.classicalFacts[3].gapAtEvent"))
    assertEquals(1.5, JsonPath.read<Double>(json, "$.classicalFacts[4].gapAtEvent"))
    assertEquals(40.5, JsonPath.read<Double>(json, "$.patterns[0].span"))
  }

  @Test
  fun `舊的 JSON 沒有這些 key 仍解得開 補算後與剛算出來的盤相同`() {
    val old = Json.encodeToString(HoroscopeChartDto.serializer(), full)
    assertFalse("endsGap" in old)
    val decoded = Json.decodeFromString(HoroscopeChartDto.serializer(), old)
    assertTrue(decoded.classicalFacts.filterIsInstance<HoroscopeTranslationDto>().all { it.endsGap == null })
    assertNull(decoded.patterns[0].span)

    val live = full.withDerivedDegrees()
    val backfilled = decoded.withDerivedDegrees()
    assertEquals(live.classicalFacts.map(::derivedOf), backfilled.classicalFacts.map(::derivedOf))
    assertEquals(live.patterns.map { it.span }, backfilled.patterns.map { it.span })
    // 補算後再存一次、再讀回來，數字不會再變
    val again = Json.decodeFromString(HoroscopeChartDto.serializer(), Json.encodeToString(HoroscopeChartDto.serializer(), backfilled))
    assertEquals(backfilled, again)
  }

  private fun derivedOf(f: HoroscopeClassicalFactDto): List<Double?> = when (f) {
    is HoroscopeTranslationDto -> listOf(f.endsGap)
    is HoroscopeCollectionDto  -> listOf(f.endsGap)
    is HoroscopeProhibitionDto -> listOf(f.gapAtEvent)
    is HoroscopeFrustrationDto -> listOf(f.gapAtEvent)
    is HoroscopeAntisciaDto    -> listOf(f.planetOffAxis, f.otherOffAxis)
    else                       -> emptyList()
  }

  @Test
  fun `組事實進盤的入口自己會填`() {
    val dto = chart(points).copy(classicalFacts = emptyList())
      .withClassicalFacts(listOf(destiny.core.astrology.classical.Antiscion(Planet.SUN, Planet.MOON, contra = false, orb = 0.24)))
    assertEquals(10.25, dto.fact<HoroscopeAntisciaDto>().planetOffAxis)
  }
}
