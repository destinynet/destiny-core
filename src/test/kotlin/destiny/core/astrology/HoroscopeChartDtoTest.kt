/**
 * Created by smallufo on 2026-08-13.
 */
package destiny.core.astrology

import destiny.core.ChartDensity
import destiny.core.Circular
import destiny.core.Gender
import destiny.core.Graph
import destiny.core.astrology.Element.FIRE
import destiny.core.astrology.Natal.HouseStarDistribution
import destiny.core.astrology.Natal.StarPosInfo
import destiny.core.astrology.Quality.CARDINAL
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.ZodiacSign.ARIES
import destiny.core.calendar.ILocation
import destiny.core.calendar.Location
import destiny.core.astrology.classical.Besiegement
import destiny.core.identityFieldsIn
import com.jayway.jsonpath.JsonPath
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HoroscopeChartDtoTest {

  @Test
  fun points_carryAbsoluteDegreeGlyphSignAndHouse() {
    val dto = fakeNatal().toHoroscopeChartDto(ChartDensity.FULL)

    val sun = dto.points.single { it.point == "Planet.SUN" }
    assertEquals(10.0, sun.zDeg)
    assertEquals("☉", sun.glyph)
    assertEquals("ARIES", sun.sign)
    assertEquals(10.0, sun.signDegree)
    assertEquals(1, sun.house)
    assertEquals(false, sun.retrograde)
  }

  /** 3D viewer 用的欄位：縮寫（中／英）、赤緯、逆行階段 —— renderer 不再自維護行星表 */
  @Test
  fun points_carryAbbreviationsDeclinationAndRetrogradePhase() {
    val dto = fakeNatal(extraStars = mapOf(
      Planet.VENUS to starAt(50.0, house = 2).copy(declination = 24.123, retrogradePhase = RetrogradePhase.PREPARING),
    )).toHoroscopeChartDto(ChartDensity.FULL)

    val sun = dto.points.single { it.point == "Planet.SUN" }
    assertEquals("日", sun.abbr)
    assertEquals("Su", sun.abbrEn)
    assertEquals(null, sun.declination)
    assertEquals(null, sun.retrogradePhase)

    val venus = dto.points.single { it.point == "Planet.VENUS" }
    assertEquals(24.123, venus.declination)
    assertEquals(RetrogradePhase.PREPARING, venus.retrogradePhase)
  }

  @Test
  fun retrogradePoint_flagged() {
    val dto = fakeNatal().toHoroscopeChartDto(ChartDensity.FULL)

    assertEquals(true, dto.points.single { it.point == "Planet.MERCURY" }.retrograde)
  }

  @Test
  fun houses_carryCuspDegrees() {
    val dto = fakeNatal().toHoroscopeChartDto(ChartDensity.FULL)

    assertEquals(12, dto.houses.size)
    assertEquals(0.0, dto.houses.single { it.id == 1 }.cuspDeg)
    assertEquals(270.0, dto.houses.single { it.id == 10 }.cuspDeg)
  }

  /** 上升與天頂不是獨立資料，就是第 1／第 10 宮頭 —— renderer 不該自己去 houses 裡撈 */
  @Test
  fun meta_derivesAscAndMcFromHouseCusps() {
    val dto = fakeNatal().toHoroscopeChartDto(ChartDensity.FULL)

    assertEquals(0.0, dto.meta.ascDeg)
    assertEquals(270.0, dto.meta.mcDeg)
  }

  /**
   * 整宮制（Whole Sign）的第一宮頭是上升星座的 0°，**不等於**上升度數，
   * 所以上升不能一律由宮頭推導 —— 有 [Axis.RISING] 就以它為準。
   */
  @Test
  fun ascDeg_prefersRisingAxisOverHouseCusp() {
    val withAxis = fakeNatal(extraStars = mapOf(Axis.RISING to starAt(17.5, house = 1)))

    val dto = withAxis.toHoroscopeChartDto(ChartDensity.FULL)

    assertEquals(17.5, dto.meta.ascDeg)
    assertEquals(0.0, dto.houses.single { it.id == 1 }.cuspDeg, "宮頭本身不該被改寫")
  }

  @Test
  fun aspects_carryAngleOrbAndMajorFlag() {
    val dto = fakeNatal(
      aspects = listOf(
        PointAspectPattern(listOf(Planet.SUN, Planet.MERCURY), angle = 0.0, orb = 1.2, score = destiny.tools.Score(0.93)),
        PointAspectPattern(listOf(Planet.SUN, Planet.MARS), angle = 30.0, orb = 0.4),
      )
    ).toHoroscopeChartDto(ChartDensity.ALL)

    assertEquals(0.93, dto.aspects.single { it.aspect == "CONJUNCTION" }.score, "3D 用 score 決定線寬")
    assertEquals(
      setOf(
        // 合相是主要相位 → 畫粗線
        AspectRow("Planet.SUN", "Planet.MERCURY", "CONJUNCTION", 0.0, 1.2, true),
        // 十二分相不是 → 細線
        AspectRow("Planet.SUN", "Planet.MARS", "SEMISEXTILE", 30.0, 0.4, false),
      ),
      dto.aspects.map { AspectRow(it.from, it.to, it.aspect, it.angle, it.orb, it.major) }.toSet()
    )
  }

  /**
   * 次要相位（十二分相、五分相…）只在 [ChartDensity.ALL] 出現 ——
   * 圓盤上全開會糊成一團，FULL 仍只畫主要相位。
   */
  @Test
  fun minorAspects_onlyInAllDensity() {
    val natal = fakeNatal(
      aspects = listOf(
        PointAspectPattern(listOf(Planet.SUN, Planet.MERCURY), angle = 0.0, orb = 1.2),
        PointAspectPattern(listOf(Planet.SUN, Planet.MARS), angle = 30.0, orb = 0.4),
      )
    )

    assertEquals(listOf("CONJUNCTION"), natal.toHoroscopeChartDto(ChartDensity.COMPACT).aspects.map { it.aspect })
    assertEquals(listOf("☌"), natal.toHoroscopeChartDto(ChartDensity.COMPACT).aspects.map { it.glyph }, "符號由 core 給，renderer 不另維護表")
    assertEquals(listOf("CONJUNCTION"), natal.toHoroscopeChartDto(ChartDensity.FULL).aspects.map { it.aspect })
    assertEquals(
      setOf("CONJUNCTION", "SEMISEXTILE"),
      natal.toHoroscopeChartDto(ChartDensity.ALL).aspects.map { it.aspect }.toSet()
    )
  }

  /** COMPACT 只留行星；交點、恆星等留給 FULL */
  @Test
  fun compactDensity_keepsOnlyPlanets() {
    val natal = fakeNatal(extraStars = mapOf(LunarNode.NORTH to starAt(88.0, house = 3)))

    assertEquals(
      setOf("Planet.SUN", "Planet.MERCURY", "Planet.MARS"),
      natal.toHoroscopeChartDto(ChartDensity.COMPACT).points.map { it.point }.toSet()
    )
    assertEquals(4, natal.toHoroscopeChartDto(ChartDensity.FULL).points.size)
  }

  /** 相位只在兩端點都出現在盤面上時才畫 —— 否則會有連到空氣的線 */
  @Test
  fun aspects_droppedWhenEndpointFilteredOut() {
    val natal = fakeNatal(
      extraStars = mapOf(LunarNode.NORTH to starAt(88.0, house = 3)),
      aspects = listOf(PointAspectPattern(listOf(Planet.SUN, LunarNode.NORTH), angle = 90.0, orb = 2.0))
    )

    assertEquals(0, natal.toHoroscopeChartDto(ChartDensity.COMPACT).aspects.size)
    assertEquals(1, natal.toHoroscopeChartDto(ChartDensity.FULL).aspects.size)
  }

  /** 定位星的圖由 server 算好，DTO 只換成 nameKey —— 鏈與環的順序（定位方向）原樣保留 */
  @Test
  fun dispositors_projectGraphAsNameKeys() {
    val natal = fakeNatal(
      extraStars = mapOf(Planet.VENUS to starAt(50.0, house = 2)),
      graph = Graph(
        circles = setOf(Circular.of(Planet.VENUS, Planet.MARS)),
        paths = setOf(listOf(Planet.SUN, Planet.MARS), listOf(Planet.MERCURY, Planet.VENUS)),
        isolated = emptySet(),
        terminals = emptySet(),
      )
    )

    val d = natal.toHoroscopeChartDto(ChartDensity.FULL).dispositors!!
    assertEquals(listOf(listOf("Planet.VENUS", "Planet.MARS")), d.circles)
    assertEquals(setOf(listOf("Planet.SUN", "Planet.MARS"), listOf("Planet.MERCURY", "Planet.VENUS")), d.paths.toSet())
    assertEquals(emptyList(), d.terminals)
  }

  @Test
  fun dispositors_terminalAndIsolated() {
    val natal = fakeNatal(graph = Graph(
      circles = emptySet(),
      paths = setOf(listOf(Planet.MERCURY, Planet.SUN)),
      isolated = setOf(Planet.MARS),
      terminals = setOf(Planet.SUN),
    ))

    val d = natal.toHoroscopeChartDto(ChartDensity.COMPACT).dispositors!!
    assertEquals(listOf("Planet.SUN"), d.terminals)
    assertEquals(listOf("Planet.MARS"), d.isolated)
  }

  /** 沒有算（空圖），或圖裡有星不在盤面上 —— 寧可不給，也不給一張連到空氣的圖 */
  @Test
  fun dispositors_nullWhenGraphEmptyOrPlanetMissing() {
    assertEquals(null, fakeNatal().toHoroscopeChartDto(ChartDensity.FULL).dispositors)

    val missingJupiter = fakeNatal(graph = Graph(
      circles = emptySet(), paths = setOf(listOf(Planet.SUN, Planet.JUPITER)), isolated = emptySet(), terminals = setOf(Planet.JUPITER),
    ))
    assertEquals(null, missingJupiter.toHoroscopeChartDto(ChartDensity.FULL).dispositors)
  }

  // ---- 格局 ----

  /** T 三角：太陽 ☍ 木星，月亮在頂點；邊由兩星黃經算出（不必在 aspects 清單裡） */
  private val tSquare = AstroPattern.TSquared(setOf(Planet.SUN, Planet.JUPITER), PointSignHouse(Planet.MOON, ZodiacSign.CANCER, 4))

  private val withTSquare: Map<AstroPoint, StarPosInfo> = mapOf(
    Planet.JUPITER to starAt(186.0, house = 7),
    Planet.MOON to starAt(98.0, house = 4),
  )

  @Test
  fun patterns_tSquareCarriesApexAndLegsWithOrbs() {
    val dto = fakeNatal(extraStars = withTSquare, patterns = listOf(tSquare)).toHoroscopeChartDto(ChartDensity.FULL)

    val p = dto.patterns.single()
    assertEquals("T_SQUARE", p.type)
    assertEquals("Planet.MOON", p.apex)
    assertEquals(
      setOf(
        LegRow(setOf("Planet.SUN", "Planet.JUPITER"), "OPPOSITION", 4.0),
        LegRow(setOf("Planet.SUN", "Planet.MOON"), "SQUARE", 2.0),
        LegRow(setOf("Planet.JUPITER", "Planet.MOON"), "SQUARE", 2.0),
      ),
      p.legs.map { LegRow(setOf(it.from, it.to), it.aspect, it.orb) }.toSet()
    )
  }

  /** 雙 T：組成部分另列，[HoroscopePatternDto.parts] 指回它們；邊是兩組 T 的聯集 */
  @Test
  fun patterns_doubleTPointsToItsParts() {
    val second = AstroPattern.TSquared(setOf(Planet.MERCURY, Planet.SATURN), PointSignHouse(Planet.VENUS, ZodiacSign.CANCER, 4))
    val stars = withTSquare + mapOf(Planet.SATURN to starAt(205.5, house = 7), Planet.VENUS to starAt(115.5, house = 4))
    val dto = fakeNatal(extraStars = stars, patterns = listOf(AstroPattern.DoubleT(setOf(tSquare, second))))
      .toHoroscopeChartDto(ChartDensity.FULL)

    val doubleT = dto.patterns.single { it.type == "DOUBLE_T" }
    assertEquals(2, doubleT.parts.size)
    assertTrue(doubleT.parts.all { dto.patterns[it].type == "T_SQUARE" })
    assertEquals(6, doubleT.legs.size)
  }

  @Test
  fun patterns_skipFixedStarsAndOffChartMembers() {
    val withStar = AstroPattern.StelliumSign(setOf(Planet.SUN, Planet.MERCURY, FixedStar.ALGOL), ARIES)
    val offChart = AstroPattern.GrandTrine(setOf(Planet.SUN, Planet.PLUTO, Planet.NEPTUNE), FIRE)
    val dto = fakeNatal(
      extraStars = mapOf(FixedStar.ALGOL to starAt(26.0, house = 1)),
      patterns = listOf(withStar, offChart)
    ).toHoroscopeChartDto(ChartDensity.FULL)

    assertEquals(emptyList(), dto.patterns)
  }

  // ---- 中點 ----

  @Test
  fun midpoints_projectFocalAndOrb() {
    val mid = MidPointWithFocal(MidPoint(setOf(Planet.SUN, Planet.MARS), 25.0.toZodiacDegree(), 1), Planet.MERCURY, 0.5)
    val dto = fakeNatal(midPoints = listOf(mid)).toHoroscopeChartDto(ChartDensity.FULL)

    val m = dto.midpoints.single()
    assertEquals(setOf("Planet.SUN", "Planet.MARS"), setOf(m.a, m.b))
    assertEquals("Planet.MERCURY", m.focal)
    assertEquals(25.0, m.zDeg)
    assertEquals(0.5, m.orb)
  }

  // ---- keepingPoints ----

  /** 拿掉一顆星：用到它的格局（連同組合格局）、中點、定位星關係一起拿掉，其餘的索引重排 */
  @Test
  fun keepingPoints_dropsDependentsAndReindexesParts() {
    val second = AstroPattern.TSquared(setOf(Planet.MERCURY, Planet.SATURN), PointSignHouse(Planet.VENUS, ZodiacSign.CANCER, 4))
    val stars = withTSquare + mapOf(Planet.SATURN to starAt(205.5, house = 7), Planet.VENUS to starAt(115.5, house = 4))
    val mid = MidPointWithFocal(MidPoint(setOf(Planet.SUN, Planet.MOON), 54.0.toZodiacDegree(), 2), Planet.MARS, 0.5)
    val dto = fakeNatal(
      extraStars = stars,
      patterns = listOf(tSquare, AstroPattern.DoubleT(setOf(tSquare, second)), second),
      midPoints = listOf(mid),
      graph = Graph(emptySet(), setOf(listOf(Planet.MOON, Planet.SUN)), emptySet(), setOf(Planet.SUN)),
    ).toHoroscopeChartDto(ChartDensity.FULL)
    assertEquals(3, dto.patterns.size)

    val noMoon = dto.keepingPoints { it.point != "Planet.MOON" }
    assertEquals(listOf("T_SQUARE"), noMoon.patterns.map { it.type }, "月亮那組 T 與雙 T 都拿掉")
    assertEquals(setOf("Planet.MERCURY", "Planet.SATURN", "Planet.VENUS"), noMoon.patterns.single().points.toSet())
    assertEquals(emptyList(), noMoon.midpoints)
    assertEquals(null, noMoon.dispositors)

    val all = dto.keepingPoints { true }
    assertEquals(dto.patterns, all.patterns, "什麼都不拿掉時原樣保留（含 parts 索引）")
  }

  /** 古典事實以 `kind` 區分種類；有星不在盤上就不附，拿掉一顆星時牽涉它的也一起拿掉 */
  @Test
  fun classicalFacts_kindDiscriminatorAndMemberFiltering() {
    val besieged = Besiegement(
      Planet.MERCURY,
      before = Besiegement.Side(Planet.SUN, null, 5.5),
      after = Besiegement.Side(Planet.MARS, Aspect.SQUARE, 4.5),
    )
    val offChart = Besiegement(Planet.MERCURY, Besiegement.Side(Planet.SATURN, null, 1.0), Besiegement.Side(Planet.MARS, null, 1.0))
    val dto = fakeNatal().toHoroscopeChartDto(ChartDensity.FULL).withClassicalFacts(listOf(besieged, offChart))
    assertEquals(1, dto.classicalFacts.size, "土星不在盤上，那一筆不附")

    val json = Json.encodeToString(HoroscopeChartDto.serializer(), dto)
    assertEquals("besiegement", JsonPath.read<String>(json, "$.classicalFacts[0].kind"))
    assertEquals("Planet.MERCURY", JsonPath.read<String>(json, "$.classicalFacts[0].planet"))
    assertEquals("SQUARE", JsonPath.read<String>(json, "$.classicalFacts[0].after.aspect"))
    assertEquals(dto, Json.decodeFromString(HoroscopeChartDto.serializer(), json), "來回不失真")

    assertEquals(emptyList(), dto.keepingPoints { it.point != "Planet.MARS" }.classicalFacts)
  }

  @Test
  fun horoscopeChartDto_declaresNoIdentityField() {
    assertEquals(emptyList(), identityFieldsIn(HoroscopeChartDto.serializer().descriptor))
  }

  // ---- fixtures ----

  private data class LegRow(val ends: Set<String>, val aspect: String, val orb: Double)

  /** 測試用的相位投影，避免逐欄位斷言 */
  private data class AspectRow(
    val from: String, val to: String, val aspect: String,
    val angle: Double, val orb: Double, val major: Boolean
  )

  private fun fakeNatal(
    extraStars: Map<AstroPoint, StarPosInfo> = emptyMap(),
    aspects: List<IPointAspectPattern> = emptyList(),
    graph: Graph<Planet> = Graph(emptySet(), emptySet(), emptySet(), emptySet()),
    patterns: List<AstroPattern> = emptyList(),
    midPoints: List<IMidPointWithFocal> = emptyList(),
  ) = FakeNatal(
    stars = mapOf(
      Planet.SUN to starAt(10.0, house = 1),
      Planet.MERCURY to starAt(25.5, house = 1, motion = Motion.RETROGRADE),
      Planet.MARS to starAt(40.0, house = 2),
    ) + extraStars,
    houses = (1..12).map { id ->
      HouseDto(id = id, cusp = ((id - 1) * 30.0).toZodiacDegree(), ruler = Planet.MARS)
    },
    tightestAspects = aspects,
    graphPatterns = graph,
    astroPatterns = patterns,
    midPoints = midPoints,
  )

  private fun starAt(zDeg: Double, house: Int?, motion: Motion = Motion.DIRECT) = StarPosInfo(
    signDegree = zDeg.toZodiacDegree(),
    element = FIRE,
    quality = CARDINAL,
    house = house,
    motion = motion,
    dispositors = emptySet()
  )
}

/**
 * 最小假盤 —— 只填 mapper 讀得到的欄位。
 * 刻意帶入 [name] 與 [place]，好讓「盤面 DTO 不得洩漏身分」有東西可測。
 */
private class FakeNatal(
  override val stars: Map<AstroPoint, StarPosInfo>,
  override val houses: List<HouseDto>,
  override val tightestAspects: List<IPointAspectPattern> = emptyList(),
  override val graphPatterns: Graph<Planet> = Graph(emptySet(), emptySet(), emptySet(), emptySet()),
  override val astroPatterns: List<AstroPattern> = emptyList(),
  override val midPoints: List<IMidPointWithFocal> = emptyList(),
) : IPersonHoroscopeDtoV2 {
  override val gender: Gender = Gender.M
  override val name: String? = "王小明"
  override val place: String? = "台北市"
  override val time: LocalDateTime = LocalDateTime.of(1980, 3, 31, 4, 30)
  override val utc: LocalDateTime = time
  override val location: ILocation = Location.of(25.0, 121.5, "Asia/Taipei")
  override val signs: Map<ZodiacSign, List<AstroPoint>> = emptyMap()
  override val axisStars: Map<Axis, List<AxisStar>> = emptyMap()
  override val houseStarDistribution: Map<HouseType, HouseStarDistribution> = emptyMap()
  override val elementPercentage: Map<Element, Double> = emptyMap()
  override val qualityPercentage: Map<Quality, Double> = emptyMap()
  override val harmonics: Map<Int, Harmonic> = emptyMap()
  override val mundanePositions: Map<AstroPoint, MundanePosition> = emptyMap()
  override val arabicLots: Map<Arabic, ArabicLotInfo> = emptyMap()
  override val antiscia: Map<AstroPoint, AntisciaPair> = emptyMap()
}
