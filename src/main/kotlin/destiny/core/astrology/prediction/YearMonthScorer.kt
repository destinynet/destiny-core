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
import destiny.core.astrology.ZodiacSign
import destiny.core.toString
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
  fun rawStrength(source: EventSource, aspect: Aspect, orb: Double, applying: Boolean): Double {
    val sourceWeight = config.sourceWeights[source] ?: return 0.0
    val importanceWeight = config.importanceWeights[aspect.importance] ?: 0.0
    val maxOrb = config.maxOrbs[aspect] ?: config.defaultMaxOrb
    val orbFactor = (1.0 - orb / maxOrb).coerceIn(0.0, 1.0)
    val applyingFactor = if (applying) config.applyingFactor else config.separatingFactor
    return sourceWeight * importanceWeight * orbFactor * applyingFactor
  }

  /** 機率式 OR:`1 - ∏(1 - hᵢ)`。多重證言疊加、飽和 ≤1,避免線性爆衝。 */
  fun aggregateOr(strengths: List<Double>): Double {
    return 1.0 - strengths.fold(1.0) { acc, h -> acc * (1.0 - h.coerceIn(0.0, 1.0)) }
  }

  /**
   * 從單一 [ITimeLineEvent] 抽出點層 hit(若該事件命中主題)。Phase 1 只處理 [AstroEvent.AspectEvent]。
   * 命中條件:該相位的本命端(points[1])∈ significators / targetLots / 目標宮宮主。
   * @param houseRulers 本命「宮主星 → 它所主的目標宮」對照(由 service 從 Natal 解出)。
   * @return 命中則回傳 [InstantHit](含算好的 rawStrength);否則 null。
   */
  fun extractHit(
    event: ITimeLineEvent,
    significators: Set<AstroPoint>,
    targetLots: Set<Arabic>,
    houseRulers: Map<AstroPoint, Int>,
  ): InstantHit? {
    val astro = event.astro
    if (astro !is AstroEvent.AspectEvent) return null   // Phase 1: 只處理相位事件
    val ad = astro.aspectData
    if (ad.points.size != 2) return null

    // 慣例上 points[0]=transiting、points[1]=natal(見 EventsTraversalTransitImpl),
    // 但對 AspectData.of() 的排序路徑保持穩健:以「哪一端落在 target 集合」判定 natal 端。
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
    return InstantHit(
      source = event.source,
      transiting = transiting,
      target = target,
      aspect = ad.aspect,
      orb = ad.orb,
      applying = applying,
      dignityScore = null,
      rawStrength = rawStrength(event.source, ad.aspect, ad.orb, applying),
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
      val base = aggregateOr(hits.map { it.rawStrength })
      val periodMultiplier = periodHits.fold(1.0) { acc, g -> acc * g.multiplier }
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

  /** 把嚴格相鄰(間隔 1 個月)的單月 window 併成一段;strength 取段內峰值。 */
  private fun mergeAdjacentMonths(windows: List<YearMonthWindow>): List<YearMonthWindow> {
    if (windows.size < 2) return windows
    val sorted = windows.sortedBy { it.from }
    val groups = mutableListOf<MutableList<YearMonthWindow>>()
    for (w in sorted) {
      val last = groups.lastOrNull()?.last()
      if (last != null && last.to.plusMonths(1) == w.from) groups.last() += w
      else groups += mutableListOf(w)
    }
    return groups.map { group ->
      YearMonthWindow(
        from = group.first().from,
        to = group.last().to,
        strength = group.maxOf { it.strength },
        instantHits = group.flatMap { it.instantHits }.sortedByDescending { it.rawStrength }.take(5),
        periodHits = group.flatMap { it.periodHits }.distinct(),
      )
    }
  }
}
