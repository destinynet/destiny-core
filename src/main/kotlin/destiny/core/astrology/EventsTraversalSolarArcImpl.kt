package destiny.core.astrology

import destiny.core.asLocaleString
import destiny.core.astrology.Aspect.Companion.expand
import destiny.core.astrology.ZodiacDegree.Companion.toZodiacDegree
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
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
    includeHour: Boolean,
    config: AstrologyTraversalConfig,
    outerPoints: Set<AstroPoint>,
    innerPoints: Set<AstroPoint>,
  ): Sequence<AstroEventDto> {

    val hConfig = HoroscopeConfig()

    val threshold = 0.9

    val (fromSolarArc, toSolarArc) = with(horoscopeFeature) {
      model.getSolarArc(fromGmtJulDay, includeHour, modernAspectCalculator, threshold, hConfig) to
        model.getSolarArc(toGmtJulDay, includeHour, modernAspectCalculator, threshold, hConfig)
    }

    // 內盤要考慮的星體 (Natal Points)
    val natalPointsToConsider = model.points.filter { it in innerPoints }
      .filter { it is Planet || it is LunarNode || it is Axis }
      .filter {
        if (includeHour)
          true
        else
          it !in Axis.values
      }.toSet()

    // 外圈要考慮的星體 (Solar Arc Points)
    val saPointsToConsider = outerPoints.filter { it is Planet || it is LunarNode || it is Axis }
      .filter {
        if (includeHour)
          true
        else
          it !in Axis.values
      }.toSet()


    fun searchPersonalEvents(aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH).toSet()): Sequence<AspectData> {

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
                  findGmtJulDayForArc(model, requiredArc, fromGmtJulDay, toGmtJulDay, hConfig)?.also { eventGmt ->
                    val pattern = PointAspectPattern(listOf(saPoint, natalPoint), aspectDegree, null, 0.0)
                    val aspectData = AspectData(pattern, null, 0.0, null, eventGmt)
                    yield(aspectData)
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

              val desc = "[SA ${p1.asLocaleString().getTitle(Locale.ENGLISH)}] Ingresses Sign. From ${oldSign.getTitle(Locale.ENGLISH)} to ${newSign.getTitle(Locale.ENGLISH)}"
              yield(AstroEventDto(AstroEvent.SignIngress(desc, p1, oldSign, newSign), eventGmt, null, Span.INSTANT, Impact.PERSONAL))
            }
          }
        }
      }
    }

    // --- SA House Ingress ---
    // 換宮位事件也只考慮外圈移動的星體
    fun searchHouseIngressEvents(): Sequence<AstroEventDto> = sequence {
      if (!includeHour) return@sequence // 若無精確出生時間，則無法計算換宮位

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

              val desc = "[SA ${p1.asLocaleString().getTitle(Locale.ENGLISH)}] Ingresses House. From House $oldHouse to House $newHouse"
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
        val personalAspects = searchPersonalEvents(Aspect.getAspects(Aspect.Importance.HIGH).toSet()).map { aspectData ->
          val (outerStar, innerStar) = aspectData.points.let { it[0] to it[1] }
          val description = buildString {
            append("[SA ${outerStar.asLocaleString().getTitle(Locale.ENGLISH)}] ${aspectData.aspect} [natal ${innerStar.asLocaleString().getTitle(Locale.ENGLISH)}]")
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
      if (config.houseIngress && includeHour) {
        yieldAll(searchHouseIngressEvents())
      }
    }
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

  fun findGmtJulDayForArcInterpolated(
    inner: IHoroscopeModel,
    targetArc: Double,
    fromGmt: GmtJulDay,
    toGmt: GmtJulDay,
    hConfig: HoroscopeConfig
  ): GmtJulDay? {
    // 1. 粗略估算檢查，確保目標弧度在時間範圍內，這是一個重要的初步過濾
    val (arcAtLow, arcAtHigh) = with(horoscopeFeature) {
      inner.getSolarArc(fromGmt, true, modernAspectCalculator, null, hConfig).degreeMoved to
        inner.getSolarArc(toGmt, true, modernAspectCalculator, null, hConfig).degreeMoved
    }

    // 檢查 targetArc 是否真的在範圍內
    val tolerance = 0.01
    if (targetArc < (arcAtLow - tolerance) || targetArc > (arcAtHigh + tolerance)) {
      logger.trace { "Target arc $targetArc is outside the range [$arcAtLow, $arcAtHigh]" }
      return null
    }

    // 2. 實作內插搜尋法 (Interpolation Search / Secant Method)
    var lowGmt = fromGmt
    var highGmt = toGmt
    var currentArcAtLow = arcAtLow
    var currentArcAtHigh = arcAtHigh

    for (round in 1..15) { // 使用 for 迴圈取代 while，設定一個最大迭代次數以防止無限迴圈

      // 檢查時間間隔或弧度差是否已經足夠小
      if ((highGmt - lowGmt) < 0.00001 || abs(currentArcAtHigh - currentArcAtLow) < 0.0001) {
        break
      }

      // --- 內插法核心公式 ---
      // 根據目標弧度在當前弧度範圍內的比例，來估算下一個時間點
      val midGmt = lowGmt + (highGmt - lowGmt) * (targetArc - currentArcAtLow) / (currentArcAtHigh - currentArcAtLow)

      // 防止因浮點數計算導致猜測超出範圍
      if (midGmt <= lowGmt || midGmt >= highGmt) {
        logger.warn("Interpolation guess is out of bounds, falling back to binary search.")
        // 若發生異常，可退回至二分法以確保穩健性
        // midGmt = lowGmt + (highGmt - lowGmt) / 2
        break
      }

      val arcAtMid = with(horoscopeFeature) { inner.getSolarArc(midGmt, true, modernAspectCalculator, null, hConfig).degreeMoved }

      logger.trace {
        "[$round] currentArc = ${arcAtMid.truncateToString(4)} , targetArc = ${targetArc.truncateToString(4)} , lowGmt = ${lowGmt.value.truncateToString(4)} , highGmt = ${
          highGmt.value.truncateToString(
            4
          )
        }"
      }

      // 根據猜測結果，縮小搜尋範圍
      if (arcAtMid < targetArc) {
        lowGmt = midGmt
        currentArcAtLow = arcAtMid
      } else {
        highGmt = midGmt
        currentArcAtHigh = arcAtMid
      }
    }

    // 3. 最終檢查 lowGmt 是否滿足條件且誤差夠小
    val finalArc = with(horoscopeFeature) { inner.getSolarArc(lowGmt, true, modernAspectCalculator, null, hConfig).degreeMoved }
    return if (abs(finalArc - targetArc) < 0.01) { // 容許的最終誤差
      lowGmt
    } else {
      // 如果收斂失敗，可以回傳 null 或記錄一個更詳細的錯誤
      logger.error("Failed to converge for targetArc $targetArc. Final arc: $finalArc")
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
      inner.getSolarArc(fromGmt, true, modernAspectCalculator, null, hConfig).degreeMoved to
        inner.getSolarArc(toGmt, true, modernAspectCalculator, null, hConfig).degreeMoved
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
      val solarArcModel = with(horoscopeFeature) { inner.getSolarArc(mid, true, modernAspectCalculator, null, hConfig) }
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
    val finalModel = with(horoscopeFeature) { inner.getSolarArc(low, true, modernAspectCalculator, null, hConfig) }
    return if (abs(finalModel.degreeMoved - targetArc) < 0.01) { // 容許的誤差
      low
    } else {
      null
    }
  }



  companion object {
    private val logger = KotlinLogging.logger { }
  }
}
