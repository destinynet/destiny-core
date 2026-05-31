/**
 * YearMonth Search 中性計分核心的純單元測試(無 Spring / 無 ephemeris)。
 * 以 @Nested 依被測函式分組。
 */
package destiny.core.astrology.prediction

import destiny.core.Scale
import destiny.core.astrology.Arabic
import destiny.core.astrology.Aspect
import destiny.core.astrology.AspectData
import destiny.core.astrology.AstroEvent
import destiny.core.astrology.AstroEventDto
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.ITimeLineEvent
import destiny.core.astrology.IPointAspectPattern.AspectType
import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.Planet
import destiny.core.astrology.PointAspectPattern
import destiny.core.astrology.Pos
import destiny.core.astrology.Stationary
import destiny.core.astrology.StationaryType
import destiny.core.astrology.SynastryAspect
import destiny.core.astrology.TimeLineEvent
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.eclipse.AbstractLunarEclipse
import destiny.core.astrology.eclipse.AbstractSolarEclipse
import destiny.core.astrology.eclipse.IEclipse
import destiny.core.calendar.GmtJulDay
import destiny.core.electional.Impact
import destiny.core.electional.Span
import destiny.tools.Score.Companion.toScore
import org.junit.jupiter.api.Nested
import java.time.YearMonth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class YearMonthScorerTest {

  private val scorer = YearMonthScorer()
  private val tol = 1e-9
  private val dummyGmt = GmtJulDay(2451545.0)
  private val yearLater = GmtJulDay(2451545.0 + 365)

  @Nested
  inner class RawStrength {

    @Test
    fun tighterOrbScoresHigher() {
      val tight = scorer.rawStrength(EventSource.TRANSIT, Aspect.TRINE, orb = 0.5, applying = true)
      val loose = scorer.rawStrength(EventSource.TRANSIT, Aspect.TRINE, orb = 6.0, applying = true)
      assertTrue(tight > loose, "tighter orb should score higher: $tight vs $loose")
      assertTrue(tight.value in 0.0..1.0)
    }

    @Test
    fun applyingBeatsSeparating() {
      val applying = scorer.rawStrength(EventSource.TRANSIT, Aspect.TRINE, 1.0, applying = true)
      val separating = scorer.rawStrength(EventSource.TRANSIT, Aspect.TRINE, 1.0, applying = false)
      assertTrue(applying > separating)
    }

    @Test
    fun highImportanceBeatsLow() {
      val high = scorer.rawStrength(EventSource.TRANSIT, Aspect.TRINE, 1.0, true)      // HIGH
      val low = scorer.rawStrength(EventSource.TRANSIT, Aspect.QUINTILE, 1.0, true)    // LOW
      assertTrue(high > low)
    }

    @Test
    fun solarArcBeatsTransit() {
      val sa = scorer.rawStrength(EventSource.SOLAR_ARC, Aspect.TRINE, 1.0, true)
      val tr = scorer.rawStrength(EventSource.TRANSIT, Aspect.TRINE, 1.0, true)
      assertTrue(sa > tr)
    }

    @Test
    fun aspectWeightsTuneHardVsSoftPerLens() {
      val hard = YearMonthScorer(YearMonthScoringConfig(aspectWeights = AspectWeights.HARD))
      val soft = YearMonthScorer(YearMonthScoringConfig(aspectWeights = AspectWeights.SOFT))
      // 同一相位跨 lens 比較,隔離 aspectWeight 效果(其餘參數相同):
      // 硬相位(square)在 HARD lens 比 SOFT lens 高
      assertTrue(
        hard.rawStrength(EventSource.TRANSIT, Aspect.SQUARE, 0.0, true) >
          soft.rawStrength(EventSource.TRANSIT, Aspect.SQUARE, 0.0, true)
      )
      // 軟相位(trine)在 SOFT lens 比 HARD lens 高
      assertTrue(
        soft.rawStrength(EventSource.TRANSIT, Aspect.TRINE, 0.0, true) >
          hard.rawStrength(EventSource.TRANSIT, Aspect.TRINE, 0.0, true)
      )
      // 預設(空 map)中性:硬相位不被壓低(= HARD 的 square),軟相位也不被壓低(> HARD 的 trine)
      val neutral = scorer
      assertEquals(
        hard.rawStrength(EventSource.TRANSIT, Aspect.SQUARE, 0.0, true).value,
        neutral.rawStrength(EventSource.TRANSIT, Aspect.SQUARE, 0.0, true).value, tol
      )
      assertTrue(
        neutral.rawStrength(EventSource.TRANSIT, Aspect.TRINE, 0.0, true) >
          hard.rawStrength(EventSource.TRANSIT, Aspect.TRINE, 0.0, true)
      )
    }
  }

  @Nested
  inner class AggregateOr {

    @Test
    fun empty_isZero() {
      assertEquals(0.0, scorer.aggregateOr(emptyList()), tol)
    }

    @Test
    fun single_isIdentity() {
      assertEquals(0.5, scorer.aggregateOr(listOf(0.5)), tol)
    }

    @Test
    fun twoHalves_is075() {
      // 1 - (1-0.5)(1-0.5) = 0.75
      assertEquals(0.75, scorer.aggregateOr(listOf(0.5, 0.5)), tol)
    }

    @Test
    fun saturatesBelowOne() {
      val s = scorer.aggregateOr(listOf(0.9, 0.9, 0.9))
      assertTrue(s < 1.0 && s > 0.99, "should saturate near but below 1: $s")
    }
  }

  @Nested
  inner class ExtractHits {

    /** 造一個「行運 transiting [aspect] 本命 natal」的 AspectEvent。points 順序 = [transiting, natal]。 */
    private fun aspectEvent(
      source: EventSource,
      transiting: AstroPoint,
      natal: AstroPoint,
      aspect: Aspect,
      orb: Double,
      applying: Boolean,
    ): ITimeLineEvent {
      val type = if (applying) AspectType.APPLYING else AspectType.SEPARATING
      val pattern = PointAspectPattern(listOf(transiting, natal), aspect.degree, type, orb)
      val ad = AspectData(pattern, type, orb, null, dummyGmt)
      val dto = AstroEventDto(AstroEvent.AspectEvent("desc", ad), dummyGmt, null, Span.INSTANT, Impact.PERSONAL)
      return TimeLineEvent(source, dto, dummyGmt)
    }

    /** outer = 行運/食/滯留端, inner = 本命端(慣例同 transitToNatalAspects)。 */
    private fun syn(outer: AstroPoint, inner: AstroPoint, aspect: Aspect, orb: Double = 1.0) =
      SynastryAspect(outer, inner, null, null, aspect, orb, AspectType.APPLYING, null)

    private fun eclipseEvent(
      toNatal: List<SynastryAspect>,
      eclipse: IEclipse = AbstractSolarEclipse.SolarEclipsePartial(dummyGmt, dummyGmt, dummyGmt),
    ): ITimeLineEvent {
      val dto = AstroEventDto(AstroEvent.Eclipse("eclipse", eclipse, toNatal), dummyGmt, null, Span.INSTANT, Impact.PERSONAL)
      return TimeLineEvent(EventSource.TRANSIT, dto, dummyGmt)
    }

    private val partialSolar = AbstractSolarEclipse.SolarEclipsePartial(dummyGmt, dummyGmt, dummyGmt)
    private val totalSolar = AbstractSolarEclipse.SolarEclipseTotal(partialSolar, dummyGmt, dummyGmt)
    private val penumbraLunar = AbstractLunarEclipse.LunarEclipsePenumbra(dummyGmt, dummyGmt, dummyGmt)
    private val totalLunar = AbstractLunarEclipse.LunarEclipseTotal(
      AbstractLunarEclipse.LunarEclipsePartial(penumbraLunar, dummyGmt, dummyGmt), dummyGmt, dummyGmt,
    )

    /** @param degree 滯留的黃道度數(用來測落宮通道)。 */
    private fun stationEvent(toNatal: List<SynastryAspect>, degree: Double = 285.0): ITimeLineEvent {
      val stationary = Stationary(dummyGmt, Planet.JUPITER, StationaryType.RETROGRADE_TO_DIRECT, Pos(degree, 0.0))
      val dto = AstroEventDto(
        AstroEvent.PlanetStationary("station", stationary, degree.toZodiacDegree(), toNatal),
        dummyGmt, null, Span.DAY, Impact.PERSONAL
      )
      return TimeLineEvent(EventSource.TRANSIT, dto, dummyGmt)
    }

    @Test
    fun significatorMatch() {
      val e = aspectEvent(EventSource.TRANSIT, Planet.JUPITER, Planet.VENUS, Aspect.TRINE, 1.0, true)
      val hit = scorer.extractHits(e, setOf(Planet.VENUS), emptySet(), emptyMap()).single()
      assertTrue(hit is InstantHit.AstroPointHit)
      assertEquals(HitTarget.Significator(Planet.VENUS), hit.target)
      assertEquals(Planet.JUPITER, hit.transiting)
      assertEquals(Aspect.TRINE, hit.contact?.aspect)
      assertTrue(hit.rawStrength.value > 0.0)
    }

    @Test
    fun lotMatch() {
      val e = aspectEvent(EventSource.TRANSIT, Planet.SATURN, Arabic.Eros, Aspect.CONJUNCTION, 0.5, true)
      val hit = scorer.extractHits(e, emptySet(), setOf(Arabic.Eros), emptyMap()).single()
      assertEquals(HitTarget.Lot(Arabic.Eros), hit.target)
    }

    @Test
    fun houseRulerMatch() {
      val e = aspectEvent(EventSource.TRANSIT, Planet.JUPITER, Planet.MARS, Aspect.SQUARE, 2.0, false)
      val hit = scorer.extractHits(e, emptySet(), emptySet(), mapOf<AstroPoint, Int>(Planet.MARS to 7)).single()
      assertEquals(HitTarget.House(7, Planet.MARS), hit.target)
    }

    @Test
    fun noTargetMatch_isEmpty() {
      val e = aspectEvent(EventSource.TRANSIT, Planet.JUPITER, Planet.MERCURY, Aspect.TRINE, 1.0, true)
      assertTrue(scorer.extractHits(e, setOf(Planet.VENUS), emptySet(), emptyMap()).isEmpty())
    }

    @Test
    fun nonHandledEvent_isEmpty() {
      val dto = AstroEventDto(
        AstroEvent.PlanetRetrograde("retro", Planet.MARS, 0.5), dummyGmt, null, Span.DAY, Impact.PERSONAL
      )
      val e = TimeLineEvent(EventSource.TRANSIT, dto, dummyGmt)
      assertTrue(scorer.extractHits(e, setOf(Planet.MARS), emptySet(), emptyMap()).isEmpty())
    }

    // ---- 日食:相位通道 ----
    @Test
    fun eclipse_aspectChannel_hitsNatalSignificator() {
      // 日食(發光體 Sun)opp 本命 ASC(以 Planet.MARS 代表 significator)
      val e = eclipseEvent(listOf(syn(Planet.SUN, Planet.MARS, Aspect.OPPOSITION, 1.3)))
      val hits = scorer.extractHits(e, setOf(Planet.MARS), emptySet(), emptyMap())
      val hit = hits.single()
      assertTrue(hit is InstantHit.EclipseHit, "expected EclipseHit, got ${hit::class.simpleName}")
      assertEquals(HitTarget.Significator(Planet.MARS), hit.target)
      assertEquals(Planet.SUN, hit.transiting)
      assertEquals(Aspect.OPPOSITION, hit.contact?.aspect)
    }

    @Test
    fun eclipse_noNatalContact_isEmpty() {
      val e = eclipseEvent(listOf(syn(Planet.SUN, Planet.MERCURY, Aspect.OPPOSITION)))
      assertTrue(scorer.extractHits(e, setOf(Planet.MARS), emptySet(), emptyMap()).isEmpty())
    }

    // ---- B1:食的型別 salience 因子(全食 > 偏食 > 半影);中性響度,非吉凶 ----
    @Test
    fun eclipse_totalSolarScoresHigherThanPartial() {
      val contact = listOf(syn(Planet.SUN, Planet.MARS, Aspect.OPPOSITION, 1.0))
      val total = scorer.extractHits(eclipseEvent(contact, totalSolar), setOf(Planet.MARS), emptySet(), emptyMap()).single()
      val partial = scorer.extractHits(eclipseEvent(contact, partialSolar), setOf(Planet.MARS), emptySet(), emptyMap()).single()
      assertTrue(
        total.rawStrength > partial.rawStrength,
        "total eclipse should be more salient than partial: ${total.rawStrength} vs ${partial.rawStrength}",
      )
      assertTrue(total.rawStrength.value <= 1.0, "type factor must keep rawStrength in Score range")
    }

    @Test
    fun eclipse_penumbralLunarIsWeakerThanTotalLunar() {
      val contact = listOf(syn(Planet.MOON, Planet.MARS, Aspect.OPPOSITION, 1.0))
      val total = scorer.extractHits(eclipseEvent(contact, totalLunar), setOf(Planet.MARS), emptySet(), emptyMap()).single()
      val penumbra = scorer.extractHits(eclipseEvent(contact, penumbraLunar), setOf(Planet.MARS), emptySet(), emptyMap()).single()
      assertTrue(total.rawStrength > penumbra.rawStrength, "${total.rawStrength} vs ${penumbra.rawStrength}")
    }

    // ---- 滯留:相位通道 ----
    @Test
    fun station_aspectChannel_hitsNatalSignificator() {
      val e = stationEvent(listOf(syn(Planet.JUPITER, Planet.MARS, Aspect.SQUARE, 0.5)))
      val hits = scorer.extractHits(e, setOf(Planet.MARS), emptySet(), emptyMap())
      val hit = hits.single { it.contact != null }
      assertTrue(hit is InstantHit.StationHit)
      assertEquals(HitTarget.Significator(Planet.MARS), hit.target)
      assertEquals(Aspect.SQUARE, hit.contact?.aspect)
    }

    // ---- 滯留:落宮通道(無相位也一定觸發)----
    @Test
    fun station_houseChannel_firesEvenWithoutAspect() {
      // 滯留落於 285° → houseOf 對應到第 8 宮(target);無 transitToNatalAspects
      val e = stationEvent(emptyList(), degree = 285.0)
      val houseOf: (IZodiacDegree) -> Int? = { 8 }
      val hits = scorer.extractHits(
        e, emptySet(), emptySet(), emptyMap(),
        targetHouses = setOf(8), houseOf = houseOf, rulerOfHouse = mapOf(8 to Planet.SATURN),
      )
      val hit = hits.single()
      assertTrue(hit is InstantHit.StationHit)
      assertEquals(HitTarget.House(8, Planet.SATURN), hit.target)
      assertNull(hit.contact, "house-placement hit has no aspect contact")
      assertEquals(scorer.config.stationInHouseStrength, hit.rawStrength)   // 落宮通道 = 最強常數
    }

    @Test
    fun station_houseChannel_notInTargetHouses_isEmpty() {
      val e = stationEvent(emptyList(), degree = 285.0)
      val houseOf: (IZodiacDegree) -> Int? = { 8 }
      val hits = scorer.extractHits(
        e, emptySet(), emptySet(), emptyMap(),
        targetHouses = setOf(7), houseOf = houseOf, rulerOfHouse = mapOf(7 to Planet.SATURN),
      )
      assertTrue(hits.isEmpty(), "station fell in H8 but only H7 was targeted")
    }

    // ---- A1:換座 / 換宮 ingress(落點通道,contact=null;membership-gated)----
    private fun signIngressEvent(point: AstroPoint, old: ZodiacSign, new: ZodiacSign, source: EventSource = EventSource.TRANSIT): ITimeLineEvent {
      val dto = AstroEventDto(AstroEvent.SignIngress("sign", point, old, new), dummyGmt, null, Span.INSTANT, Impact.GLOBAL)
      return TimeLineEvent(source, dto, dummyGmt)
    }

    private fun houseIngressEvent(point: AstroPoint, old: Int, new: Int, source: EventSource = EventSource.TRANSIT): ITimeLineEvent {
      val dto = AstroEventDto(AstroEvent.HouseIngress("house", point, old, new), dummyGmt, null, Span.INSTANT, Impact.PERSONAL)
      return TimeLineEvent(source, dto, dummyGmt)
    }

    @Test
    fun houseIngress_intoTargetHouse_fires() {
      // 木星進入本命第 7 宮(target)→ 落點通道,target = House(7, 宮主 Venus)
      val e = houseIngressEvent(Planet.JUPITER, old = 6, new = 7)
      val hit = scorer.extractHits(
        e, emptySet(), emptySet(), emptyMap(), targetHouses = setOf(7), rulerOfHouse = mapOf(7 to Planet.VENUS),
      ).single() as InstantHit.IngressHit
      assertEquals(IngressKind.HOUSE, hit.kind)
      assertEquals(HitTarget.House(7, Planet.VENUS), hit.target)
      assertEquals(Planet.JUPITER, hit.transiting)
      assertNull(hit.contact, "ingress has no aspect contact")
      assertEquals(7, hit.newHouse)
      assertTrue(hit.rawStrength.value > 0.0)
    }

    @Test
    fun houseIngress_notTargetHouse_isEmpty() {
      val e = houseIngressEvent(Planet.JUPITER, old = 5, new = 6)
      val hits = scorer.extractHits(e, emptySet(), emptySet(), emptyMap(), targetHouses = setOf(7), rulerOfHouse = mapOf(7 to Planet.VENUS))
      assertTrue(hits.isEmpty(), "entered H6 but only H7 targeted")
    }

    @Test
    fun signIngress_significatorChangesSign_fires() {
      val e = signIngressEvent(Planet.VENUS, ZodiacSign.ARIES, ZodiacSign.TAURUS)
      val hit = scorer.extractHits(e, setOf(Planet.VENUS), emptySet(), emptyMap()).single() as InstantHit.IngressHit
      assertEquals(IngressKind.SIGN, hit.kind)
      assertEquals(HitTarget.Significator(Planet.VENUS), hit.target)
      assertNull(hit.contact)
      assertEquals(ZodiacSign.TAURUS, hit.newSign)
    }

    @Test
    fun signIngress_nonSignificator_isEmpty() {
      val e = signIngressEvent(Planet.MERCURY, ZodiacSign.ARIES, ZodiacSign.TAURUS)
      assertTrue(scorer.extractHits(e, setOf(Planet.VENUS), emptySet(), emptyMap()).isEmpty())
    }

    @Test
    fun ingress_solarArcStrongerThanTransit() {
      // 同一換宮,SA(年級深刻)強度應 > transit(月級表層),因 rawStrength × sourceWeights[source]
      val tr = scorer.extractHits(
        houseIngressEvent(Planet.JUPITER, 6, 7, EventSource.TRANSIT),
        emptySet(), emptySet(), emptyMap(), targetHouses = setOf(7), rulerOfHouse = mapOf(7 to Planet.VENUS),
      ).single()
      val sa = scorer.extractHits(
        houseIngressEvent(Planet.JUPITER, 6, 7, EventSource.SOLAR_ARC),
        emptySet(), emptySet(), emptyMap(), targetHouses = setOf(7), rulerOfHouse = mapOf(7 to Planet.VENUS),
      ).single()
      assertTrue(sa.rawStrength > tr.rawStrength, "SA ingress should outweigh transit: ${sa.rawStrength} vs ${tr.rawStrength}")
    }
  }

  @Nested
  inner class ProfectionPeriod {

    private fun profection(house: Int, lord: Planet) =
      Profection(Scale.YEAR, lord, ZodiacSign.ARIES, house, dummyGmt, yearLater)

    @Test
    fun houseMatch_emitsPeriodHit() {
      val hits = scorer.profectionPeriodHits(profection(7, Planet.SATURN), setOf(7), emptySet())
      assertEquals(1, hits.size)
      assertEquals(PeriodSource.PROFECTION, hits[0].source)
      assertTrue(hits[0].multiplier > 1.0)
    }

    @Test
    fun lordIsSignificator_emitsPeriodHit() {
      val hits = scorer.profectionPeriodHits(profection(3, Planet.VENUS), emptySet(), setOf(Planet.VENUS))
      assertEquals(1, hits.size)
      // reason 用 locale-independent 英文名(不隨 JVM locale 變「??」)
      assertTrue(hits[0].reason.contains("Venus"), "stable English name expected: ${hits[0].reason}")
    }

    @Test
    fun bothConditions_emitsTwo() {
      val hits = scorer.profectionPeriodHits(profection(7, Planet.VENUS), setOf(7), setOf(Planet.VENUS))
      assertEquals(2, hits.size)
    }

    @Test
    fun neither_emitsEmpty() {
      val hits = scorer.profectionPeriodHits(profection(3, Planet.SATURN), setOf(7), setOf(Planet.VENUS))
      assertTrue(hits.isEmpty())
    }
  }

  @Nested
  inner class ReturnPeriod {

    private fun returnDto(
      type: ReturnType,
      houseOverlay: Map<Int, List<Planet>> = emptyMap(),
      keyAspects: List<SynastryAspect> = emptyList(),
    ) = ReturnCoverageDto(
      returnType = type,
      validFrom = dummyGmt,
      validTo = yearLater,
      coveragePercent = 100,
      ascSign = ZodiacSign.ARIES, ascDegree = 0, mcSign = ZodiacSign.CAPRICORN, mcDegree = 0,
      planets = emptyMap(),
      keyAspectsToNatal = keyAspects,
      houseOverlay = houseOverlay,
    )

    @Test
    fun significatorInTargetHouse() {
      val ret = returnDto(ReturnType.SOLAR, houseOverlay = mapOf(7 to listOf(Planet.VENUS, Planet.SUN)))
      val hits = scorer.returnPeriodHits(ret, setOf(Planet.VENUS), setOf(7), emptySet())
      assertEquals(1, hits.size)
      assertEquals(PeriodSource.SOLAR_RETURN, hits[0].source)
    }

    @Test
    fun aspectToSignificator() {
      val asp = SynastryAspect(Planet.JUPITER, Planet.VENUS, null, null, Aspect.TRINE, 1.0, AspectType.APPLYING, null)
      val ret = returnDto(ReturnType.LUNAR, keyAspects = listOf(asp))
      val hits = scorer.returnPeriodHits(ret, setOf(Planet.VENUS), emptySet(), emptySet())
      assertEquals(1, hits.size)
      assertEquals(PeriodSource.LUNAR_RETURN, hits[0].source)
    }

    @Test
    fun atMostOne_houseTakesPriority() {
      // 同時符合 A(house)與 B(aspect)→ 仍只回一個,且為 A
      val asp = SynastryAspect(Planet.JUPITER, Planet.VENUS, null, null, Aspect.TRINE, 1.0, AspectType.APPLYING, null)
      val ret = returnDto(ReturnType.SOLAR, houseOverlay = mapOf(7 to listOf(Planet.VENUS)), keyAspects = listOf(asp))
      val hits = scorer.returnPeriodHits(ret, setOf(Planet.VENUS), setOf(7), emptySet())
      assertEquals(1, hits.size)
      assertTrue(hits[0].reason.contains("7th"), "house reason expected: ${hits[0].reason}")
    }

    @Test
    fun irrelevantReturn_emitsEmpty() {
      val ret = returnDto(ReturnType.SOLAR, houseOverlay = mapOf(3 to listOf(Planet.SATURN)))
      assertTrue(scorer.returnPeriodHits(ret, setOf(Planet.VENUS), setOf(7), emptySet()).isEmpty())
    }

    @Test
    fun nonSolarLunarReturn_emitsEmpty() {
      val ret = returnDto(ReturnType.SATURN, houseOverlay = mapOf(7 to listOf(Planet.VENUS)))
      assertTrue(scorer.returnPeriodHits(ret, setOf(Planet.VENUS), setOf(7), emptySet()).isEmpty())
    }
  }

  @Nested
  inner class FirdariaPeriod {

    private fun firdaria(major: Planet, sub: Planet) = Firdaria(major, sub, dummyGmt, yearLater)

    @Test
    fun majorLordIsSignificator() {
      val hits = scorer.firdariaPeriodHits(firdaria(Planet.VENUS, Planet.SATURN), setOf(Planet.VENUS))
      assertEquals(1, hits.size)
      assertEquals(PeriodSource.FIRDARIA, hits[0].source)
      assertTrue(hits[0].reason.contains("Venus"), "stable English name expected: ${hits[0].reason}")
    }

    @Test
    fun subLordIsSignificator() {
      val hits = scorer.firdariaPeriodHits(firdaria(Planet.SATURN, Planet.VENUS), setOf(Planet.VENUS))
      assertEquals(1, hits.size)
    }

    @Test
    fun neither_emitsEmpty() {
      assertTrue(scorer.firdariaPeriodHits(firdaria(Planet.SATURN, Planet.JUPITER), setOf(Planet.VENUS)).isEmpty())
    }
  }

  @Nested
  inner class ZrPeriod {

    private fun zr(sign: ZodiacSign, loosingOfBond: Boolean = false, level: Int = 1) =
      ZodiacalReleasing(level, sign, Planet.SATURN, dummyGmt, yearLater, loosingOfBond)

    @Test
    fun peakPeriod_fires() {
      // 第 10 宮(從 ARIES 起算)= CAPRICORN → PEAK
      val hit = scorer.zrPeriodHit(zr(ZodiacSign.CAPRICORN), ZodiacSign.ARIES, "Eros")
      assertNotNull(hit)
      assertEquals(PeriodSource.ZODIACAL_RELEASING, hit.source)
      assertTrue(hit.reason.contains("peak", ignoreCase = true))
    }

    @Test
    fun loosingOfBond_fires() {
      val hit = scorer.zrPeriodHit(zr(ZodiacSign.TAURUS, loosingOfBond = true), ZodiacSign.ARIES, "Eros")
      assertNotNull(hit)
      assertTrue(hit.reason.contains("loosing", ignoreCase = true))
    }

    @Test
    fun ordinaryPeriod_isNull() {
      assertNull(scorer.zrPeriodHit(zr(ZodiacSign.TAURUS), ZodiacSign.ARIES, "Eros"))
    }
  }

  @Nested
  inner class BuildWindows {

    private val noPeriods: (YearMonth) -> List<PeriodHit> = { emptyList() }
    private val venusTarget = HitTarget.Significator(Planet.VENUS)
    private val marsTarget = HitTarget.Significator(Planet.MARS)

    private fun instant(target: HitTarget, rawStrength: Double, source: EventSource = EventSource.TRANSIT) =
      InstantHit.AstroPointHit(source, target, Planet.JUPITER, AspectContact(Aspect.TRINE, 1.0, true), rawStrength.toScore())

    @Test
    fun empty_isEmpty() {
      assertTrue(scorer.buildWindows(emptyList(), SearchGrain.MONTH, Combine.OR, noPeriods).isEmpty())
    }

    @Test
    fun singleMonth_strengthEqualsOrAggregate() {
      val ym = YearMonth.of(2026, 3)
      val timed = listOf(
        TimedInstantHit(ym, instant(venusTarget, 0.5)),
        TimedInstantHit(ym, instant(venusTarget, 0.5)),
      )
      val w = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods).single()
      assertEquals(ym, w.from)
      assertEquals(ym, w.to)
      assertEquals(0.75, w.strength, tol)   // single distinct target → no confluence bonus
    }

    @Test
    fun periodHitMultiplies() {
      val ym = YearMonth.of(2026, 3)
      val timed = listOf(TimedInstantHit(ym, instant(venusTarget, 0.5)))
      val periods: (YearMonth) -> List<PeriodHit> = { listOf(PeriodHit(PeriodSource.PROFECTION, "x", 2.0)) }
      val w = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, periods).single()
      assertEquals(1.0, w.strength, tol)    // base 0.5 × 2.0
      assertEquals(1, w.periodHits.size)
    }

    @Test
    fun periodMultiplier_sameSourceTakesMaxNotProduct() {
      val ym = YearMonth.of(2026, 3)
      val timed = listOf(TimedInstantHit(ym, instant(venusTarget, 0.5)))
      // 同源兩個 PROFECTION(profected-house 1.5 + year-lord 1.3)應只取最強 1.5,而非 1.5×1.3
      val periods: (YearMonth) -> List<PeriodHit> = {
        listOf(PeriodHit(PeriodSource.PROFECTION, "house", 1.5), PeriodHit(PeriodSource.PROFECTION, "lord", 1.3))
      }
      val w = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, periods).single()
      assertEquals(0.75, w.strength, tol)   // 0.5 × 1.5(非 0.5 × 1.95)
    }

    @Test
    fun periodMultiplier_cappedAcrossSources() {
      val ym = YearMonth.of(2026, 3)
      val timed = listOf(TimedInstantHit(ym, instant(venusTarget, 0.5)))
      // 跨源:PROFECTION 1.5 × SR 1.3 × FIRDARIA 1.3 = 2.535 → cap 至 maxPeriodMultiplier(預設 2.0)
      val periods: (YearMonth) -> List<PeriodHit> = {
        listOf(
          PeriodHit(PeriodSource.PROFECTION, "house", 1.5),
          PeriodHit(PeriodSource.PROFECTION, "lord", 1.3),
          PeriodHit(PeriodSource.SOLAR_RETURN, "sr", 1.3),
          PeriodHit(PeriodSource.FIRDARIA, "fir", 1.3),
        )
      }
      val w = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, periods).single()
      assertEquals(1.0, w.strength, tol)    // 0.5 × min(2.535, 2.0) = 0.5 × 2.0
    }

    @Test
    fun mergesAdjacentMonths() {
      val timed = listOf(
        TimedInstantHit(YearMonth.of(2026, 3), instant(venusTarget, 0.4)),
        TimedInstantHit(YearMonth.of(2026, 4), instant(venusTarget, 0.6)),
      )
      val w = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods).single()
      assertEquals(YearMonth.of(2026, 3), w.from)
      assertEquals(YearMonth.of(2026, 4), w.to)
      assertEquals(0.6, w.strength, tol)    // peak month represents the merged window
    }

    @Test
    fun splitsContiguousRunAtValleyIntoSeparatePeaks() {
      // 連續 5 個月、strengths 形成兩個波峰(谷在第 3 月)→ 不該黏成一段,而是切成兩個 window。
      val strengths = listOf(0.5, 0.8, 0.3, 0.7, 0.4)
      val timed = strengths.mapIndexed { i, s -> TimedInstantHit(YearMonth.of(2026, i + 1), instant(venusTarget, s)) }
      val windows = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods).sortedBy { it.from }
      assertEquals(2, windows.size, "valley at month 3 should split the run into two bumps")
      assertEquals(YearMonth.of(2026, 1), windows[0].from)
      assertEquals(YearMonth.of(2026, 3), windows[0].to)     // 谷值月歸入前一個 bump
      assertEquals(0.8, windows[0].strength, tol)
      assertEquals(YearMonth.of(2026, 4), windows[1].from)
      assertEquals(YearMonth.of(2026, 5), windows[1].to)
      assertEquals(0.7, windows[1].strength, tol)
    }

    @Test
    fun singleBumpRunStaysOneWindow() {
      // 單一波峰(升-峰-降,無內部谷值)→ 仍是一個 window。
      val strengths = listOf(0.3, 0.6, 0.9, 0.5, 0.2)
      val timed = strengths.mapIndexed { i, s -> TimedInstantHit(YearMonth.of(2026, i + 1), instant(venusTarget, s)) }
      val w = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods).single()
      assertEquals(YearMonth.of(2026, 1), w.from)
      assertEquals(YearMonth.of(2026, 5), w.to)
      assertEquals(0.9, w.strength, tol)
    }

    @Test
    fun nonAdjacentMonthsStaySeparate() {
      val timed = listOf(
        TimedInstantHit(YearMonth.of(2026, 1), instant(venusTarget, 0.4)),
        TimedInstantHit(YearMonth.of(2026, 6), instant(venusTarget, 0.6)),
      )
      assertEquals(2, scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods).size)
    }

    @Test
    fun andCombine_dropsSingleTargetBucket() {
      val ym = YearMonth.of(2026, 3)
      val timed = listOf(TimedInstantHit(ym, instant(venusTarget, 0.5)))   // only 1 distinct target
      assertTrue(scorer.buildWindows(timed, SearchGrain.MONTH, Combine.AND, noPeriods).isEmpty())
      assertEquals(1, scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods).size)
    }

    @Test
    fun confluenceBonusForTwoDistinctTargets() {
      val ym = YearMonth.of(2026, 3)
      val timed = listOf(
        TimedInstantHit(ym, instant(venusTarget, 0.5)),
        TimedInstantHit(ym, instant(marsTarget, 0.5)),
      )
      val w = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods).single()
      // base 0.75 × confluenceBonus(1.25) = 0.9375
      assertEquals(0.9375, w.strength, tol)
    }

    @Test
    fun topNLimits() {
      // 用非相鄰月份(間隔 ≥2)避免合併,才有多個 window 可供 topN 篩選
      val timed = listOf(1, 3, 5, 7, 9, 11).map { TimedInstantHit(YearMonth.of(2026, it), instant(venusTarget, 0.08 * it)) }
      val windows = scorer.buildWindows(timed, SearchGrain.MONTH, Combine.OR, noPeriods, topN = 3)
      assertEquals(3, windows.size)
      assertTrue(windows[0].strength >= windows[1].strength)   // sorted by strength desc
    }
  }
}
