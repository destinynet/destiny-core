package destiny.core.astrology

import destiny.core.astrology.Aspect.Companion.expand
import destiny.core.astrology.BirthDataGrain.MINUTE
import destiny.core.astrology.IPointAspectPattern.AspectType
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.toLocalDateTime
import destiny.core.astrology.prediction.EventSource
import destiny.core.electional.Impact
import destiny.core.electional.Span
import destiny.tools.KotlinLogging
import destiny.tools.getTitle
import destiny.tools.reverse
import destiny.tools.truncateToString
import jakarta.inject.Named
import java.util.*
import kotlin.math.abs

/**
 * SolarArc-based
 */
@Named
class EventsTraversalSolarArcImpl(
  private val horoscopeFeature: IHoroscopeFeature,
  private val modernAspectCalculator: IAspectCalculator,
) : IEventsTraversal {

  override fun traverse(
    model: IHoroscopeModel,
    fromGmtJulDay: GmtJulDay,
    toGmtJulDay: GmtJulDay,
    loc: ILocation,
    grain: BirthDataGrain,
    config: AstrologyTraversalConfig,
    transitingPoints: Set<AstroPoint>,
    natalTargetPoints: Set<AstroPoint>,
  ): Sequence<AstroEventDto> {

    val hConfig = HoroscopeConfig()

    val threshold = 0.9

    // Guard: ensure both time bounds are after birth time
    if (fromGmtJulDay < model.gmtJulDay || toGmtJulDay < model.gmtJulDay) {
      logger.error { "Solar Arc time range invalid: fromGmtJulDay=$fromGmtJulDay, toGmtJulDay=$toGmtJulDay, model.gmtJulDay=${model.gmtJulDay}" }
      return emptySequence()
    }

    val (fromSolarArc, toSolarArc) = with(horoscopeFeature) {
      model.getSolarArc(fromGmtJulDay, grain, modernAspectCalculator, threshold, hConfig) to
        model.getSolarArc(toGmtJulDay, grain, modernAspectCalculator, threshold, hConfig)
    }

    // 內盤要考慮的星體 (Natal Points)
    // ⚠️ 軸點閘門呼叫正典 [includeAxis]，不要手抄它的內容（今日等價；手抄複本會各自演化）。
    val natalPointsToConsider = model.points.filter { it in natalTargetPoints }
      .filter { it is Planet || it is LunarNode || it is Axis }
      .filter { grain.includeAxis || it !in Axis.values }
      .toSet()

    // 外圈要考慮的星體 (Solar Arc Points)
    // Solar Arc 將所有行星推進相同度數，不應受限於 caller 傳入的 transitingPoints（通常只有外行星）。
    // SA 使用 SolarArcConfig.transitingPoints（預設包含所有行星+軸點），而非 caller 的 transitingPoints。
    //
    // ⚠️⚠️ **正因為它忽略 caller 的集合，移動端的 grain 閘門必須裝在這裡。**
    //      2026-08-31：呼叫端（`ReportFactory.scanTimeLineEvents`）已對移動端施加
    //      [allowsMovingPoint]，但本 impl 把那個集合整個丟掉 —— SA 月亮因此照樣產出。
    //      太陽弧每年只走約 1°，而 DAY 精度下本命月亮 ±6.6° ⇒ **日期誤差 ±6.6 年**，
    //      印出來的 `perfects 2015-10-17` 與 `PEAK ORB <0.1°` 都是捏造的精度。
    //      2026-08-31 起這份推導不再寫在這裡：改呼叫正典 [movingPointsOf]，
    //      因為 corpus 的 `corpusTransitingPoints`（母體自陳涵蓋哪些移動端）也要同一份答案。
    val saPointsToConsider = config.movingPointsOf(
      EventSource.SOLAR_ARC, grain, transitingPoints, natalPoints = model.points
    )


    /** 將 [GmtJulDay] 格式化為當地日期 (yyyy-MM-dd)，供描述文字使用 */
    fun GmtJulDay.ymd(): String = this.toLocalDateTime(loc.zoneId).toLocalDate().toString()

    fun searchPersonalEvents(aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH)): Sequence<SaAspectEvent> {

      /**
       * map of
       * 0 , CONJUNCTION
       * 60 , SEXTILE
       * 90 , SQUARE
       * 120 , TRINE
       * 180 , OPPOSITION
       * 240 , TRINE
       * 300 , SEXTILE
       */
      val degreesMap: Map<Double, Aspect> = aspects.expand()

      return sequence {
        // 遍歷所有 (外圈移動星體, 內圈固定星體) 的配對
        saPointsToConsider.forEach { saPoint ->
          natalPointsToConsider.forEach { natalPoint ->
            val saPos = model.getPosition(saPoint)
            val natalPos = model.getPosition(natalPoint)
            if (saPos != null && natalPos != null) {
              for ((aspectDegree, _) in degreesMap) {
                // --- 計算方向: SA saPoint -> natal natalPoint ---
                val separation = saPos.lngDeg.aheadOf(natalPos.lngDeg)
                val requiredArc = (aspectDegree - separation + 360) % 360
                if (requiredArc >= fromSolarArc.degreeMoved && requiredArc <= toSolarArc.degreeMoved) {
                  // Exact: SA aspect perfects within the time range
                  findGmtJulDayForArc(model, requiredArc, fromGmtJulDay, toGmtJulDay, hConfig)?.also { eventGmt ->
                    // 資料層維持精準點事件；「高原期」（太陽弧年速僅 ~0.99°，0.1° ≈ ±37 天）的
                    // enter/leave 提醒由呈現層 CondensedTextBuilder.buildEventsTimeline 以近似值展開。
                    val pattern = PointAspectPattern(listOf(saPoint, natalPoint), aspectDegree, null, 0.0)
                    yield(SaAspectEvent(AspectData(pattern, null, 0.0, null, eventGmt), " EXACT"))
                  }
                } else {
                  // Applying: aspect hasn't perfected yet, check if within orb at end of range
                  // orb 為 null ＝ 不回報窗界標記（見 SolarArcConfig KDoc）—— 連 resolveArcTime 的數值搜尋都省下
                  val applyingOrb = requiredArc - toSolarArc.degreeMoved
                  val applyingMaxOrb = config.solarArcConfig.applyingOrb
                  if (applyingMaxOrb != null && applyingOrb > 0 && applyingOrb <= applyingMaxOrb) {
                    // orb 在太陽弧中即時間：一定要附精準日期，否則讀者無從判斷這是多久之後的事。
                    // 解不出日期 (數值搜尋不收斂) 就整筆不回報 —— 「正在接近但不知何時」沒有資訊量。
                    resolveArcTime(model, requiredArc, fromGmtJulDay, toGmtJulDay, hConfig)?.also { perfectTime ->
                      val pattern = PointAspectPattern(listOf(saPoint, natalPoint), aspectDegree, AspectType.APPLYING, applyingOrb)
                      yield(
                        SaAspectEvent(
                          AspectData(pattern, AspectType.APPLYING, applyingOrb, null, toGmtJulDay),
                          " (applying, orb ${applyingOrb.truncateToString(2)}°, perfects ${perfectTime.ymd()})"
                        )
                      )
                    }
                  }

                  // Separating: aspect perfected just before range start, check if still within orb
                  val separatingOrb = fromSolarArc.degreeMoved - requiredArc
                  val separatingMaxOrb = config.solarArcConfig.separatingOrb
                  if (separatingMaxOrb != null && separatingOrb > 0 && separatingOrb <= separatingMaxOrb) {
                    // 同 applying：解不出精準日期就整筆不回報。
                    resolveArcTime(model, requiredArc, fromGmtJulDay, toGmtJulDay, hConfig)?.also { perfectTime ->
                      val pattern = PointAspectPattern(listOf(saPoint, natalPoint), aspectDegree, AspectType.SEPARATING, separatingOrb)
                      yield(
                        SaAspectEvent(
                          AspectData(pattern, AspectType.SEPARATING, separatingOrb, null, fromGmtJulDay),
                          " (separating, orb ${separatingOrb.truncateToString(2)}°, perfected ${perfectTime.ymd()})"
                        )
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // --- SA Sign Ingress ---
    // 換星座事件只考慮外圈移動的星體
    fun searchSignIngressEvents(): Sequence<AstroEventDto> = sequence {
      val signBoundaries = (0 until 360 step 30).map { it.toDouble().toZodiacDegree() }

      for (p1 in saPointsToConsider) {
        val natalPos1 = model.getPosition(p1) ?: continue

        for (signBoundary in signBoundaries) {
          // 計算要將 p1 推到 signBoundary 所需的弧度
          val requiredArc = (signBoundary.value - natalPos1.lngDeg.value + 360) % 360

          if (requiredArc >= fromSolarArc.degreeMoved && requiredArc <= toSolarArc.degreeMoved) {
            findGmtJulDayForArc(model, requiredArc, fromGmtJulDay, toGmtJulDay, hConfig)?.also { eventGmt ->

              // 由於太陽弧通常是順行，這裡直接判斷新舊星座
              val newSign = signBoundary.sign
              val oldSign = newSign.prev

              val desc = "[SA ${p1.getTitle(Locale.ENGLISH)}] Ingresses Sign. From ${oldSign.getTitle(Locale.ENGLISH)} to ${newSign.getTitle(Locale.ENGLISH)}"
              yield(AstroEventDto(AstroEvent.SignIngress(desc, p1, oldSign, newSign), eventGmt, null, Span.INSTANT, Impact.PERSONAL))
            }
          }
        }
      }
    }

    // --- SA House Ingress ---
    // 換宮位事件也只考慮外圈移動的星體
    fun searchHouseIngressEvents(): Sequence<AstroEventDto> = sequence {
      if (grain != MINUTE) return@sequence // 若無精確出生時間，則無法計算換宮位

      // 取得宮位邊界度數，並建立從「度數」反查「宮位數」的 Map
      val cuspDegreeMap: Map<ZodiacDegree, Int> = model.cuspDegreeMap.reverse()
      val cuspBoundaries = cuspDegreeMap.keys

      for (p1 in saPointsToConsider) {
        val natalPos1 = model.getPosition(p1) ?: continue

        for (cuspBoundary in cuspBoundaries) {
          // 計算要將 p1 推到 cuspBoundary 所需的弧度
          val requiredArc = (cuspBoundary.value - natalPos1.lngDeg.value + 360) % 360

          if (requiredArc >= fromSolarArc.degreeMoved && requiredArc <= toSolarArc.degreeMoved) {
            findGmtJulDayForArc(model, requiredArc, fromGmtJulDay, toGmtJulDay, hConfig)?.also { eventGmt ->

              // 太陽弧順行，進入新的宮位
              val newHouse = cuspDegreeMap.getValue(cuspBoundary)
              val oldHouse = if (newHouse == 1) 12 else newHouse - 1

              val desc = "[SA ${p1.getTitle(Locale.ENGLISH)}] Ingresses House. From House $oldHouse to House $newHouse"
              yield(AstroEventDto(AstroEvent.HouseIngress(desc, p1, oldHouse, newHouse), eventGmt, null, Span.INSTANT, Impact.PERSONAL))
            }
          }
        }
      }
    }

    // --- 組合所有事件 ---
    return sequence {
      if (config.personalAspect) {
        // SA to Natal 相位事件
        val personalAspects = searchPersonalEvents(Aspect.getAspects(Aspect.Importance.HIGH)).map { (aspectData, suffix) ->
          val (outerStar, innerStar) = aspectData.points.let { it[0] to it[1] }
          val description = buildString {
            append("[SA ${outerStar.getTitle(Locale.ENGLISH)}] ${aspectData.aspect} [natal ${innerStar.getTitle(Locale.ENGLISH)}]")
            append(suffix)
          }
          AstroEventDto(AstroEvent.AspectEvent(description, aspectData), aspectData.gmtJulDay, null, Span.INSTANT, Impact.PERSONAL)
        }
        yieldAll(personalAspects)
      }


      if (config.signIngress) {
        // SA 換星座事件
        yieldAll(searchSignIngressEvents())
      }

      // SA 換宮位事件
      if (config.houseIngress && grain == MINUTE) {
        yieldAll(searchHouseIngressEvents())
      }
    }
  }

  /**
   * 解出「太陽弧推進到 [targetArc]」的時刻。
   *
   * 與 [findGmtJulDayForArcInterpolated] 的差別：**允許結果落在 [fromGmt]~[toGmt] 之外**。
   * 該函式在 targetArc 超出區間對應弧度時會直接回傳 null（見其開頭的範圍檢查），
   * 但「影響區間端點」與「區間外相位的精準日期」本來就可能落在報告區間之外，
   * 因此這裡先把搜尋界限逐年往外撐開，直到把 targetArc 夾住為止。
   *
   * @return 找不到（撐開 [maxExpandYears] 年後仍夾不住，或早於出生時刻）時回傳 null
   */
  private fun resolveArcTime(
    model: IHoroscopeModel,
    targetArc: Double,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    hConfig: HoroscopeConfig,
    maxExpandYears: Int = 5
  ): GmtJulDay? {
    if (targetArc <= 0) return null

    fun arcAt(gmt: GmtJulDay): Double = with(horoscopeFeature) {
      model.getSolarArc(gmt, MINUTE, modernAspectCalculator, null, hConfig).degreeMoved
    }

    // 太陽弧自出生起算，故搜尋下限不得早於出生時刻
    val floor = model.gmtJulDay + 1.0

    var low = if (fromGmt < floor) floor else fromGmt
    var high = toGmt
    var expand = 0
    while (expand++ < maxExpandYears) {
      when {
        targetArc < arcAt(low)  -> {
          if (low <= floor) return null
          val candidate = low - Constants.TROPICAL_YEAR_DAYS
          low = if (candidate < floor) floor else candidate
        }
        targetArc > arcAt(high) -> high += Constants.TROPICAL_YEAR_DAYS
        else                    -> break
      }
    }
    if (targetArc < arcAt(low) || targetArc > arcAt(high)) return null

    return findGmtJulDayForArcInterpolated(model, targetArc, low, high, hConfig)
  }

  private fun findGmtJulDayForArc(
    inner: IHoroscopeModel,
    targetArc: Double,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    hConfig: HoroscopeConfig
  ): GmtJulDay? {
    return findGmtJulDayForArcInterpolated(inner, targetArc, fromGmt, toGmt, hConfig)
  }

  /**
   * 解出 [fromGmt]~[toGmt] 之間「太陽弧推進到 [targetArc]」的時刻；不在區間內回傳 null。
   *
   * 用 Illinois 版的試位法（regula falsi）：太陽弧對時間近乎線性，通常幾輪就收斂。
   * 純試位法在曲線稍有彎曲時，每次猜點都落在目標同一側，於是只有一端在動、另一端永遠停在起點；
   * 若最後回傳那個沒動過的端點，就差了一整個區間（一年的區間約 1°），事件因此被丟掉。
   * Illinois 修正在同一側連續落點時把另一端的權重減半，猜點出界則退回二分法；
   * 最後回傳的是目前最接近目標的那一點，而不是某個固定端點。
   */
  fun findGmtJulDayForArcInterpolated(
    inner: IHoroscopeModel,
    targetArc: Double,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    hConfig: HoroscopeConfig
  ): GmtJulDay? {
    fun arcAt(gmt: GmtJulDay): Double = with(horoscopeFeature) {
      inner.getSolarArc(gmt, MINUTE, modernAspectCalculator, null, hConfig).degreeMoved
    }

    // 1. 確保目標弧度在時間範圍內（容許端點外一點點的浮點誤差）
    val arcAtLow = arcAt(fromGmt)
    val arcAtHigh = arcAt(toGmt)
    val tolerance = 0.01
    if (targetArc < (arcAtLow - tolerance) || targetArc > (arcAtHigh + tolerance)) {
      logger.trace { "Target arc $targetArc is outside the range [$arcAtLow, $arcAtHigh]" }
      return null
    }

    // f(t) = arc(t) - targetArc，單調遞增；low 端 f ≤ 0、high 端 f ≥ 0
    var lowGmt = fromGmt
    var highGmt = toGmt
    var fLow = arcAtLow - targetArc
    var fHigh = arcAtHigh - targetArc

    var bestGmt = if (abs(fLow) <= abs(fHigh)) lowGmt else highGmt
    var bestErr = minOf(abs(fLow), abs(fHigh))

    // 目標落在端點外（容許範圍內）：最近的端點就是答案
    if (fLow >= 0 || fHigh <= 0) {
      return bestGmt.takeIf { bestErr < tolerance }
    }

    var lastSide = 0
    for (round in 1..60) {
      if (bestErr < ARC_EPSILON || (highGmt - lowGmt) < TIME_EPSILON_DAYS) break

      // 試位法猜點；出界（或分母退化）就改用二分
      var midGmt = lowGmt + (highGmt - lowGmt) * (-fLow) / (fHigh - fLow)
      if (!(midGmt > lowGmt && midGmt < highGmt)) {
        midGmt = lowGmt + (highGmt - lowGmt) / 2
      }

      val fMid = arcAt(midGmt) - targetArc
      if (abs(fMid) < bestErr) {
        bestErr = abs(fMid)
        bestGmt = midGmt
      }

      logger.trace { "[$round] f = ${fMid.truncateToString(8)} , lowGmt = ${lowGmt.value.truncateToString(6)} , highGmt = ${highGmt.value.truncateToString(6)}" }

      if (fMid < 0) {
        lowGmt = midGmt
        fLow = fMid
        if (lastSide == -1) fHigh /= 2 // Illinois：連續落在同一側，壓低另一端的權重
        lastSide = -1
      } else {
        highGmt = midGmt
        fHigh = fMid
        if (lastSide == 1) fLow /= 2
        lastSide = 1
      }
    }

    return if (bestErr < tolerance) {
      bestGmt
    } else {
      logger.error { "Failed to converge for targetArc $targetArc. Best arc error: $bestErr" }
      null
    }
  }

  /**
   * 使用數值搜尋方法，找到產生指定 solar arc 的時間點
   * @param targetArc 需要達成的太陽弧度數 (0-360)
   * @return 如果在時間範圍內找到，則返回 GmtJulDay，否則返回 null
   */
  @Deprecated("Use findGmtJulDayForArcInterpolated instead.")
  fun findGmtJulDayForArcBinary(
    inner: IHoroscopeModel,
    targetArc: Double,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    hConfig: HoroscopeConfig
  ): GmtJulDay? {
    // 粗略估算檢查事件是否可能在範圍內
    val (degreeMovedFrom, degreeMovedTo) = with(horoscopeFeature) {
      inner.getSolarArc(fromGmt, MINUTE, modernAspectCalculator, null, hConfig).degreeMoved to
        inner.getSolarArc(toGmt, MINUTE, modernAspectCalculator, null, hConfig).degreeMoved
    }

    if (targetArc < degreeMovedFrom || targetArc > degreeMovedTo) {
      logger.trace { "targetArc = $targetArc , but degreeMovedFrom = $degreeMovedFrom , degreeMovedTo = $degreeMovedTo" }
      return null
    }

    // 實作 Binary Search 或 Iterative Search
    // 搜尋空間是 [fromGmt, toGmt]
    var low = fromGmt
    var high = toGmt

    var round = 0

    while ((high - low) > 0.0001) { // 迭代直到找到足夠精確的時間
      val mid = low + (high - low) / 2
      val solarArcModel = with(horoscopeFeature) { inner.getSolarArc(mid, MINUTE, modernAspectCalculator, null, hConfig) }
      val currentArc = solarArcModel.degreeMoved

      round++
      logger.trace { "[$round] currentArc = ${currentArc.truncateToString(4)} , targetArc = ${targetArc.truncateToString(4)} , low = ${low.value.truncateToString(4)} , high = ${high.value.truncateToString(4)}" }

      if (currentArc < targetArc) {
        low = mid
      } else {
        high = mid
      }
    }

    // 最終檢查 low/high 是否滿足條件且誤差夠小
    val finalModel = with(horoscopeFeature) { inner.getSolarArc(low, MINUTE, modernAspectCalculator, null, hConfig) }
    return if (abs(finalModel.degreeMoved - targetArc) < 0.01) { // 容許的誤差
      low
    } else {
      null
    }
  }



  /** 一筆 SA 相位事件，及其要接在描述後面的註記（EXACT／進入區間／脫離區間／applying／separating） */
  private data class SaAspectEvent(val aspectData: AspectData, val suffix: String)

  companion object {
    private val logger = KotlinLogging.logger { }

    /** 收斂判準：弧度誤差（太陽弧一年約 1°，1e-6° 約半分鐘） */
    private const val ARC_EPSILON = 1e-6

    /** 收斂判準：搜尋區間寬度（天） */
    private const val TIME_EPSILON_DAYS = 1e-5
  }
}
