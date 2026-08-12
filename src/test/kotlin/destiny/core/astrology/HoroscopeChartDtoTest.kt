/**
 * Created by smallufo on 2026-08-13.
 */
package destiny.core.astrology

import destiny.core.ChartDensity
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
import destiny.core.identityFieldsIn
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

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
        PointAspectPattern(listOf(Planet.SUN, Planet.MERCURY), angle = 0.0, orb = 1.2),
        PointAspectPattern(listOf(Planet.SUN, Planet.MARS), angle = 30.0, orb = 0.4),
      )
    ).toHoroscopeChartDto(ChartDensity.ALL)

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

  @Test
  fun horoscopeChartDto_declaresNoIdentityField() {
    assertEquals(emptyList(), identityFieldsIn(HoroscopeChartDto.serializer().descriptor))
  }

  // ---- fixtures ----

  /** 測試用的相位投影，避免逐欄位斷言 */
  private data class AspectRow(
    val from: String, val to: String, val aspect: String,
    val angle: Double, val orb: Double, val major: Boolean
  )

  private fun fakeNatal(
    extraStars: Map<AstroPoint, StarPosInfo> = emptyMap(),
    aspects: List<IPointAspectPattern> = emptyList()
  ) = FakeNatal(
    stars = mapOf(
      Planet.SUN to starAt(10.0, house = 1),
      Planet.MERCURY to starAt(25.5, house = 1, motion = Motion.RETROGRADE),
      Planet.MARS to starAt(40.0, house = 2),
    ) + extraStars,
    houses = (1..12).map { id ->
      HouseDto(id = id, cusp = ((id - 1) * 30.0).toZodiacDegree(), ruler = Planet.MARS)
    },
    tightestAspects = aspects
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
  override val astroPatterns: List<AstroPattern> = emptyList()
  override val classicalAstrologyPatterns: List<String> = emptyList()
  override val graphPatterns: Graph<Planet> = Graph(emptySet(), emptySet(), emptySet(), emptySet())
  override val midPoints: List<IMidPointWithFocal> = emptyList()
  override val harmonics: Map<Int, Harmonic> = emptyMap()
  override val mundanePositions: Map<AstroPoint, MundanePosition> = emptyMap()
  override val arabicLots: Map<Arabic, ArabicLotInfo> = emptyMap()
  override val antiscia: Map<AstroPoint, AntisciaPair> = emptyMap()
}
