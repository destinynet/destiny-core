/**
 * YearMonth Search — 中性計分核心(純函式,無 Spring / 無 ephemeris 相依)。
 *
 * 職責切割:
 *  - 本類別:把已抽好的事件特徵 → 中性 strength,分桶,套段層乘數,合併相鄰,取 top-N。
 *  - 時間轉換(GmtJulDay → YearMonth)與撈事件(getTimeLineEvents)由 impl 的 service 負責。
 *
 * 所有權重來自 [YearMonthScoringConfig](可調參,無公認真值)。
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Arabic
import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroEvent
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.ITimeLineEvent
import destiny.core.astrology.IPointAspectPattern
import destiny.core.astrology.IZodiacDegree
import destiny.core.astrology.SynastryAspect
import destiny.core.astrology.ZodiacSign
import destiny.core.astrology.eclipse.IEclipse
import destiny.core.astrology.eclipse.ILunarEclipse
import destiny.core.astrology.eclipse.ISolarEclipse
import destiny.core.toString
import destiny.tools.Score
import destiny.tools.Score.Companion.toScore
import java.time.YearMonth
import java.util.Locale

/** [InstantHit] 加上它所屬的時間桶(由 service 依 convergentTime 換算)。 */
data class TimedInstantHit(val yearMonth: YearMonth, val hit: InstantHit)

class YearMonthScorer(val config: YearMonthScoringConfig = YearMonthScoringConfig()) {

  /**
   * 點事件中性量級(0..1):
   * `sourceWeight × importanceWeight × orbFactor × applyingFactor`。
   * dignity 不進量級(維持中性,留給上層當特徵)。stationBonus Phase 1 視為 1.0。
   */
  fun rawStrength(source: EventSource, aspect: Aspect, orb: Double, applying: Boolean): Score {
    val sourceWeight = config.sourceWeights[source] ?: return 0.0.toScore()
    val importanceWeight = config.importanceWeights[aspect.importance] ?: 0.0
    val maxOrb = config.maxOrbs[aspect] ?: config.defaultMaxOrb
    val orbFactor = (1.0 - orb / maxOrb).coerceIn(0.0, 1.0)
    val applyingFactor = if (applying) config.applyingFactor else config.separatingFactor
    // 相位偏好(human lens 第三 dial);空 map / 查無 → 1.0(中性,不偏硬軟)。
    val aspectWeight = config.aspectWeights[aspect] ?: 1.0
    return (sourceWeight * importanceWeight * orbFactor * applyingFactor * aspectWeight).coerceIn(0.0, 1.0).toScore()
  }

  /** 機率式 OR:`1 - ∏(1 - hᵢ)`。多重證言疊加、飽和 ≤1,避免線性爆衝。 */
  fun aggregateOr(strengths: List<Double>): Double {
    return 1.0 - strengths.fold(1.0) { acc, h -> acc * (1.0 - h.coerceIn(0.0, 1.0)) }
  }

  /**
   * 從單一 [ITimeLineEvent] 抽出點層 hit(可 0..n 筆)。處理三種事件 × 兩條通道
   * (見 docs/yearmonth-signal-recall.md §5.0a):
   *  - [AstroEvent.AspectEvent] → 相位通道 → 0..1 筆 [InstantHit.AstroPointHit]。
   *  - [AstroEvent.Eclipse] → 對本命的每個相位接觸各 1 筆 [InstantHit.EclipseHit](相位通道)。
   *  - [AstroEvent.PlanetStationary] → 相位通道(每接觸 1 筆 [InstantHit.StationHit])
   *    **+ 落宮通道**(滯留 `zodiacDegree` 落在 target house → 一定觸發,`contact=null`)。
   *
   * @param houseRulers 本命「宮主星 → 它所主的目標宮」對照(相位通道的 [HitTarget.House] 用)。
   * @param targetHouses 落宮通道的目標宮(由 request 給)。
   * @param houseOf 度數 → 本命宮(service 由 Natal 宮頭注入;scorer 純函式不自算)。
   * @param rulerOfHouse 宮 → 宮主(落宮通道組 [HitTarget.House] 用)。
   */
  fun extractHits(
    event: ITimeLineEvent,
    significators: Set<AstroPoint>,
    targetLots: Set<Arabic>,
    houseRulers: Map<AstroPoint, Int>,
    targetHouses: Set<Int> = emptySet(),
    houseOf: (IZodiacDegree) -> Int? = { null },
    rulerOfHouse: Map<Int, AstroPoint> = emptyMap(),
  ): List<InstantHit> {
    return when (val astro = event.astro) {
      is AstroEvent.AspectEvent -> listOfNotNull(
        aspectEventHit(event.source, astro, significators, targetLots, houseRulers)
      )

      is AstroEvent.Eclipse -> {
        // B1:食型別 salience 因子(全食 > 偏食 > 半影),乘進相位通道 rawStrength(≤1,Score 仍合法)。
        val typeFactor = eclipseTypeFactor(astro.eclipse)
        astro.transitToNatalAspects.mapNotNull { syn ->
          synContact(event.source, syn, significators, targetLots, houseRulers)?.let { c ->
            val scaled = (c.rawStrength.value * typeFactor).toScore()
            InstantHit.EclipseHit(event.source, c.target, c.transiting, c.contact, scaled, astro.eclipse)
          }
        }
      }

      is AstroEvent.PlanetStationary -> buildList {
        // 相位通道:滯留星對本命 significator/lot/宮主 成相位
        astro.transitToNatalAspects.forEach { syn ->
          synContact(event.source, syn, significators, targetLots, houseRulers)?.let { c ->
            add(InstantHit.StationHit(event.source, c.target, c.transiting, c.contact, c.rawStrength, astro.stationary, astro.zodiacDegree))
          }
        }
        // 落宮通道:滯留落在 target house → 一定觸發(無相位,最強訊號)
        houseOf(astro.zodiacDegree)?.takeIf { it in targetHouses }?.let { h ->
          val ruler = rulerOfHouse[h] ?: astro.stationary.star
          add(
            InstantHit.StationHit(
              source = event.source,
              target = HitTarget.House(h, ruler),
              transiting = astro.stationary.star,
              contact = null,
              rawStrength = config.stationInHouseStrength,
              stationary = astro.stationary,
              zodiacDegree = astro.zodiacDegree,
            )
          )
        }
      }

      else -> emptyList()
    }
  }

  /** 相位通道命中的中間結果(transiting / target / contact / rawStrength)。 */
  private data class ContactHit(val transiting: AstroPoint, val target: HitTarget, val contact: AspectContact, val rawStrength: Score)

  /** [AstroEvent.AspectEvent] → 相位通道(points 順序穩健:以「哪端落在 target 集合」判 natal 端)。 */
  private fun aspectEventHit(
    source: EventSource,
    astro: AstroEvent.AspectEvent,
    significators: Set<AstroPoint>,
    targetLots: Set<Arabic>,
    houseRulers: Map<AstroPoint, Int>,
  ): InstantHit.AstroPointHit? {
    val ad = astro.aspectData
    if (ad.points.size != 2) return null
    val p0 = ad.points[0]
    val p1 = ad.points[1]
    val (transiting, target) = when {
      classifyTarget(p1, significators, targetLots, houseRulers) != null ->
        p0 to classifyTarget(p1, significators, targetLots, houseRulers)!!
      classifyTarget(p0, significators, targetLots, houseRulers) != null ->
        p1 to classifyTarget(p0, significators, targetLots, houseRulers)!!
      else -> return null
    }
    val applying = ad.aspectType == IPointAspectPattern.AspectType.APPLYING
    return InstantHit.AstroPointHit(
      source = source,
      target = target,
      transiting = transiting,
      contact = AspectContact(ad.aspect, ad.orb, applying),
      rawStrength = rawStrength(source, ad.aspect, ad.orb, applying),
    )
  }

  /** 食的型別 salience 因子(全食/環食 > 偏食 > 半影);非食或查無 → 1.0。中性響度,不判吉凶。 */
  private fun eclipseTypeFactor(eclipse: IEclipse): Double = when (eclipse) {
    is ISolarEclipse -> config.solarEclipseFactor[eclipse.solarType] ?: 1.0
    is ILunarEclipse -> config.lunarEclipseFactor[eclipse.lunarType] ?: 1.0
    else             -> 1.0
  }

  /** [SynastryAspect](食/滯留對本命)→ 相位通道。慣例:innerPoint=本命端、outerPoint=行運端。 */
  private fun synContact(
    source: EventSource,
    syn: SynastryAspect,
    significators: Set<AstroPoint>,
    targetLots: Set<Arabic>,
    houseRulers: Map<AstroPoint, Int>,
  ): ContactHit? {
    val target = classifyTarget(syn.innerPoint, significators, targetLots, houseRulers) ?: return null
    val applying = syn.aspectType == IPointAspectPattern.AspectType.APPLYING
    return ContactHit(
      transiting = syn.outerPoint,
      target = target,
      contact = AspectContact(syn.aspect, syn.orb, applying),
      rawStrength = rawStrength(source, syn.aspect, syn.orb, applying),
    )
  }

  /** significator > lot > house 的優先序;都不符回 null。 */
  private fun classifyTarget(
    point: AstroPoint,
    significators: Set<AstroPoint>,
    targetLots: Set<Arabic>,
    houseRulers: Map<AstroPoint, Int>,
  ): HitTarget? = when {
    point in significators                 -> HitTarget.Significator(point)
    point is Arabic && point in targetLots -> HitTarget.Lot(point)
    houseRulers.containsKey(point)         -> HitTarget.House(houseRulers.getValue(point), point)
    else                                   -> null
  }

  /**
   * 評估某個 profection 是否點亮段層(Phase 1 唯一接線的 [PeriodSource])。
   *  - profected house ∈ targetHouses → 一個 [PeriodHit]
   *  - year-lord ∈ significators       → 一個 [PeriodHit]
   */
  fun profectionPeriodHits(
    profection: Profection,
    targetHouses: Set<Int>,
    significators: Set<AstroPoint>,
  ): List<PeriodHit> {
    val out = mutableListOf<PeriodHit>()
    if (profection.house in targetHouses) {
      out += PeriodHit(
        PeriodSource.PROFECTION,
        "profected ${profection.house}th house",
        config.periodMultipliers.getValue(PeriodSource.PROFECTION),
      )
    }
    if (profection.lord in significators) {
      out += PeriodHit(
        PeriodSource.PROFECTION,
        "year-lord ${profection.lord.toString(Locale.ENGLISH)} is significator",
        config.profectionLordMultiplier,
      )
    }
    return out
  }

  /**
   * 評估某個回歸盤([ReturnCoverageDto])是否點亮段層(Phase 2:SOLAR_RETURN / LUNAR_RETURN)。
   * 為避免單一回歸盤把乘數疊到爆,**至多回傳一個 [PeriodHit]**;命中優先序:
   *  A. significator 回歸行星落入 target house(houseOverlay)—— 行星與宮位都切題,最強
   *  B. 回歸行星與本命 significator/lot 成相位(keyAspectsToNatal)
   * 兩者皆不符 → 空(該回歸盤與主題無關,不施加乘數)。Mars/Jupiter/Saturn 回歸不在 Phase 2 範圍。
   */
  fun returnPeriodHits(
    ret: ReturnCoverageDto,
    significators: Set<AstroPoint>,
    targetHouses: Set<Int>,
    targetLots: Set<Arabic>,
  ): List<PeriodHit> {
    val source = when (ret.returnType) {
      ReturnType.SOLAR -> PeriodSource.SOLAR_RETURN
      ReturnType.LUNAR -> PeriodSource.LUNAR_RETURN
      else             -> return emptyList()   // Mars/Jupiter/Saturn 回歸不在 Phase 2 範圍
    }
    val multiplier = config.periodMultipliers[source] ?: return emptyList()

    // A. significator 回歸行星落入 target house(行星與宮位都切題)
    targetHouses.firstNotNullOfOrNull { house ->
      ret.houseOverlay[house]?.firstOrNull { it in significators }?.let { house to it }
    }?.let { (house, planet) ->
      return listOf(PeriodHit(source, "$source ${planet.toString(Locale.ENGLISH)} in natal ${house}th house", multiplier))
    }

    // B. 回歸行星與本命 significator/lot 成相位
    val sigOrLot: Set<AstroPoint> = significators + targetLots
    ret.keyAspectsToNatal.firstOrNull { it.innerPoint in sigOrLot }?.let { asp ->
      return listOf(
        PeriodHit(
          source,
          "$source ${asp.outerPoint.toString(Locale.ENGLISH)} ${asp.aspect.name} natal ${asp.innerPoint.toString(Locale.ENGLISH)}",
          multiplier,
        )
      )
    }

    return emptyList()
  }

  /**
   * Firdaria 段層(Phase 3):major 或 sub lord ∈ significators → 一個 [PeriodHit](優先 major)。
   */
  fun firdariaPeriodHits(firdaria: Firdaria, significators: Set<AstroPoint>): List<PeriodHit> {
    val multiplier = config.periodMultipliers[PeriodSource.FIRDARIA] ?: return emptyList()
    return when {
      firdaria.majorRuler in significators ->
        listOf(PeriodHit(PeriodSource.FIRDARIA, "firdaria major-lord ${firdaria.majorRuler.toString(Locale.ENGLISH)} is significator", multiplier))
      firdaria.subRuler in significators ->
        listOf(PeriodHit(PeriodSource.FIRDARIA, "firdaria sub-lord ${firdaria.subRuler.toString(Locale.ENGLISH)} is significator", multiplier))
      else -> emptyList()
    }
  }

  /**
   * ZR(從某 lot 釋放)段層(Phase 3):此釋放期為 **PEAK**(第 10 宮釋放,角宮高峰)或
   * **loosing-of-bond**(束縛解除,重大轉折)→ 一個 [PeriodHit];否則 null。ZR 不與 house 掛鉤。
   * @param lotSign 該 lot 的本命星座(算 angularity 用);@param lotName 顯示用(e.g. "Eros")。
   */
  fun zrPeriodHit(zr: ZodiacalReleasing, lotSign: ZodiacSign, lotName: String): PeriodHit? {
    val multiplier = config.periodMultipliers[PeriodSource.ZODIACAL_RELEASING] ?: return null
    return when {
      zr.angularityFrom(lotSign) == ZrAngularity.PEAK ->
        PeriodHit(PeriodSource.ZODIACAL_RELEASING, "ZR-$lotName peak (L${zr.level}, ${zr.sign.name})", multiplier)
      zr.isLoosingOfBond ->
        PeriodHit(PeriodSource.ZODIACAL_RELEASING, "ZR-$lotName loosing-of-bond (L${zr.level})", multiplier)
      else -> null
    }
  }

  /**
   * 分桶 → 套段層 → 機率式 OR 聚合 → 合併相鄰 → 取 top-N。
   * @param periodHitsAt 給定某時間桶代表月,回傳當時生效的段層命中。
   */
  fun buildWindows(
    timedHits: List<TimedInstantHit>,
    grain: SearchGrain,
    combine: Combine,
    periodHitsAt: (YearMonth) -> List<PeriodHit>,
    topN: Int = 12,
  ): List<YearMonthWindow> {
    if (timedHits.isEmpty()) return emptyList()

    // 1. 依粒度分桶。YEAR 以該年 1 月為桶鍵。
    val buckets: Map<YearMonth, List<InstantHit>> = timedHits
      .groupBy { bucketKey(it.yearMonth, grain) }
      .mapValues { (_, list) -> list.map { it.hit } }

    // 2. 每桶:機率式 OR 聚合 → 套段層乘數 → confluence 加成。
    val perBucket: List<Pair<Int, YearMonthWindow>> = buckets.map { (key, hits) ->
      val distinctTargets = hits.map { it.target }.distinct().size
      val periodHits = periodHitsAt(key)
      val base = aggregateOr(hits.map { it.rawStrength.value })
      // 同一 PeriodSource 內只取最強乘數(去同技法重複計數),跨源相乘後 cap 上限。
      val periodMultiplier = periodHits
        .groupBy { it.source }
        .values
        .fold(1.0) { acc, perSource -> acc * perSource.maxOf { it.multiplier } }
        .coerceAtMost(config.maxPeriodMultiplier)
      val confluence = if (distinctTargets >= 2) config.confluenceBonus else 1.0
      val (from, to) = bucketRange(key, grain)
      val window = YearMonthWindow(
        from = from,
        to = to,
        strength = base * periodMultiplier * confluence,
        instantHits = hits.sortedByDescending { it.rawStrength }.take(5),
        periodHits = periodHits,
      )
      distinctTargets to window
    }

    // 3. AND 模式:丟掉單一 significator/宮的桶(古典多重證言精神)。
    val filtered = when (combine) {
      Combine.AND -> perBucket.filter { it.first >= 2 }.map { it.second }
      Combine.OR  -> perBucket.map { it.second }
    }

    // 4. 合併相鄰(僅 MONTH 粒度;YEAR 桶各自獨立)→ 依 strength 排序取 top-N。
    val merged = if (grain == SearchGrain.MONTH) mergeAdjacentMonths(filtered) else filtered
    return merged.sortedByDescending { it.strength }.take(topN)
  }

  private fun bucketKey(ym: YearMonth, grain: SearchGrain): YearMonth = when (grain) {
    SearchGrain.MONTH -> ym
    SearchGrain.YEAR  -> YearMonth.of(ym.year, 1)
  }

  private fun bucketRange(key: YearMonth, grain: SearchGrain): Pair<YearMonth, YearMonth> = when (grain) {
    SearchGrain.MONTH -> key to key
    SearchGrain.YEAR  -> YearMonth.of(key.year, 1) to YearMonth.of(key.year, 12)
  }

  /**
   * 相鄰月份合併:先切出「無間隔」的連續月份段(run),再於各段內**依谷值(local minimum)再切**,
   * 讓每個「波峰(bump)」各成一個 window(strength = 段內峰值)。
   *
   * 訊號變密(每月都有命中)時,單純「相鄰即併」會把一長串月份黏成一個多年大窗、失去解析度
   * (見 docs/yearmonth-signal-recall.md §6.4)。依谷值切峰後,每個高峰自成一窗,再由 topN 取最強者。
   */
  private fun mergeAdjacentMonths(windows: List<YearMonthWindow>): List<YearMonthWindow> {
    if (windows.size < 2) return windows
    val sorted = windows.sortedBy { it.from }
    // 1. 切成無間隔的連續月份段(run)
    val runs = mutableListOf<MutableList<YearMonthWindow>>()
    for (w in sorted) {
      val last = runs.lastOrNull()?.last()
      if (last != null && last.to.plusMonths(1) == w.from) runs.last() += w
      else runs += mutableListOf(w)
    }
    // 2. 每個 run 內依谷值再切成 bump,各 bump 併成一個 window
    return runs.flatMap { run -> segmentAtValleys(run) }.map { combineWindows(it) }
  }

  /** 在 run 內找嚴格谷值(strength 同時低於前後鄰月)當切點,讓每個波峰各成一段(谷值月歸入前一段)。 */
  private fun segmentAtValleys(run: List<YearMonthWindow>): List<List<YearMonthWindow>> {
    if (run.size < 3) return listOf(run)
    val segments = mutableListOf<MutableList<YearMonthWindow>>()
    var cur = mutableListOf(run[0])
    for (i in 1 until run.size) {
      cur += run[i]
      val isValley = i < run.size - 1 &&
        run[i].strength < run[i - 1].strength &&
        run[i].strength < run[i + 1].strength
      if (isValley) {
        segments += cur
        cur = mutableListOf()
      }
    }
    if (cur.isNotEmpty()) segments += cur
    return segments
  }

  /** 把一段(連續且同屬一個波峰的)單月 window 併成一個;strength 取峰值。 */
  private fun combineWindows(group: List<YearMonthWindow>): YearMonthWindow {
    return YearMonthWindow(
      from = group.first().from,
      to = group.last().to,
      strength = group.maxOf { it.strength },
      instantHits = group.flatMap { it.instantHits }.sortedByDescending { it.rawStrength }.take(5),
      periodHits = group.flatMap { it.periodHits }.distinct(),
    )
  }
}
