/**
 * Created by smallufo on 2026-11-09.
 */
package destiny.core.astrology

import destiny.tools.truncate
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * 填好盤上的**衍生度數** —— 圖上會另外標出來、但不是任何原始欄位的那幾個數字。算法只有這一份，renderer 與旁白的檢查都只讀欄位：
 *
 * | 欄位 | 定義 |
 * |---|---|
 * | [HoroscopeTranslationDto.endsGap]、[HoroscopeCollectionDto.endsGap] | 兩端（傳遞的 from／to、收集的 a／b）黃經的最短角距，0–180 |
 * | [HoroscopeProhibitionDto.gapAtEvent]、[HoroscopeFrustrationDto.gapAtEvent] | 軌跡內插到 `eventDays` 那一刻，追的與被追的兩顆星離相位角還差幾度 |
 * | [HoroscopeAntisciaDto.planetOffAxis]、[HoroscopeAntisciaDto.otherOffAxis] | 兩顆星各自離對稱軸幾度（軸端取離 planet 較近的至點／分點） |
 * | [HoroscopePatternDto.span] | 群星聚集的成員（四軸除外）跨了幾度 |
 *
 * **輸入一律取「JSON 裡看得到的值」**：黃經、日數、容許度在 JSON 是無條件捨去到兩位小數（[destiny.tools.serializers.DoubleTwoDecimalSerializer]），
 * 這裡先照樣捨去再算。所以剛算出來的盤，與存成 JSON 之後再讀回來補算的盤，得到同一組數字。
 * 結果四捨五入到兩位小數（輸入只有兩位，多出來的是浮點誤差），再編碼不會變。
 *
 * 只補還沒有值（null）的欄位，已經有值的不動 —— 重複呼叫結果不變；[withoutTrack] 拿掉軌跡之後，先前算好的 `gapAtEvent` 也留得住。
 * 算不出來（軌跡是空的、成員不在 [HoroscopeChartDto.points] 內）就留 null。
 *
 * [toHoroscopeChartDto] 與 [withClassicalFacts] 已經呼叫過；只有自己由 JSON 解出舊資料（沒有這些 key）時才需要另外呼叫
 */
fun HoroscopeChartDto.withDerivedDegrees(): HoroscopeChartDto {
  val lngs: Map<AstroPoint, Double> = buildMap { points.forEach { p -> if (p.point !in this) put(p.point, p.zDeg.inJson()) } }
  return copy(
    patterns = patterns.map { it.withDerivedDegrees(lngs) },
    classicalFacts = classicalFacts.map { it.withDerivedDegrees(lngs) },
  )
}

/** 這個欄位在 JSON 裡的值（無條件捨去到兩位小數） */
private fun Double.inJson(): Double = truncate(2)

/** 四捨五入到兩位小數（都是非負的度數） */
private fun Double.toHundredths(): Double = (this * 100).roundToLong() / 100.0

/** -180（含）～180 的有號角差 a − b */
private fun signedGap(a: Double, b: Double): Double = ((a - b + 180) % 360 + 360) % 360 - 180

/** 兩個黃經的最短角距，0–180 */
private fun angleGap(a: Double, b: Double): Double = abs(signedGap(a, b))

private fun HoroscopePatternDto.withDerivedDegrees(lngs: Map<AstroPoint, Double>): HoroscopePatternDto = when (type) {
  HoroscopePatternType.STELLIUM_SIGN, HoroscopePatternType.STELLIUM_HOUSE -> if (span != null) this else copy(span = stelliumSpan(points, lngs))
  else                                                                    -> this
}

/** 成員（四軸除外）的黃經排序後，整圈扣掉最大的那段空隙；沒有成員回 null */
private fun stelliumSpan(members: List<AstroPoint>, lngs: Map<AstroPoint, Double>): Double? {
  val sorted = members.filterNot { it is Axis }.mapNotNull { lngs[it] }.sorted()
  if (sorted.isEmpty()) return null
  var span = sorted.last() - sorted.first()
  sorted.zipWithNext().forEach { (a, b) -> span = minOf(span, 360 - (b - a)) }
  return span.toHundredths()
}

/** 沒有 `else`：新增一種事實時，要在這裡決定它有沒有衍生度數 */
private fun HoroscopeClassicalFactDto.withDerivedDegrees(lngs: Map<AstroPoint, Double>): HoroscopeClassicalFactDto = when (this) {
  is HoroscopeTranslationDto -> if (endsGap != null) this else copy(endsGap = endsGap(lngs[from], lngs[to]))
  is HoroscopeCollectionDto  -> if (endsGap != null) this else copy(endsGap = endsGap(lngs[a], lngs[b]))
  is HoroscopeProhibitionDto -> if (gapAtEvent != null) this else copy(gapAtEvent = gapAtEvent(aspect, orb, eventDays, track))
  is HoroscopeFrustrationDto -> if (gapAtEvent != null) this else copy(gapAtEvent = gapAtEvent(aspect, orb, eventDays, track))
  is HoroscopeAntisciaDto    -> {
    val planetLng = lngs[planet]
    val otherLng = lngs[other]
    if ((planetOffAxis != null && otherOffAxis != null) || planetLng == null || otherLng == null) this
    else {
      val (p, q) = if (contra) 0.0 to 180.0 else 90.0 to 270.0
      // 平手取前一個（春分／夏至）
      val pivot = if (angleGap(planetLng, p) <= angleGap(planetLng, q)) p else q
      copy(planetOffAxis = angleGap(planetLng, pivot).toHundredths(), otherOffAxis = angleGap(otherLng, pivot).toHundredths())
    }
  }

  is HoroscopeRefranationDto, is HoroscopeBesiegementDto, is HoroscopeOvercomingDto, is HoroscopeAversionDto,
  is HoroscopeMaltreatmentDto, is HoroscopeBonificationDto, is HoroscopeDoryphoriaDto, is HoroscopeCombustionDto,
  is HoroscopeSectDto, is HoroscopeHayzDto, is HoroscopePhasisDto, is HoroscopeFeralDto, is HoroscopeLunarConnectionDto,
  is HoroscopeViaCombustaDto, is HoroscopeLotDto, is HoroscopeLotPairDto -> this
}

private fun endsGap(a: Double?, b: Double?): Double? = if (a == null || b == null) null else angleGap(a, b).toHundredths()

/**
 * 插隊／撲空在事件那一刻原相位還差幾度：軌跡線性內插（跨 0° 也對）到 [eventDays]，追的與被追的之間離相位角還差多少。
 * 相位角取兩側（±角度）裡離盤面時刻較近的那一側，平手取正的那一側。
 *
 * 事件就在盤面時刻（[eventDays] 捨去後是 0）時，回盤面的容許度 [orb]：軌跡的黃經各自捨去過，相減會與 [orb] 差到 0.01，
 * 而那一刻的差距照定義就是 [orb]
 */
private fun gapAtEvent(aspect: Aspect, orb: Double, eventDays: Double, track: List<HoroscopeOutcomeSampleDto>): Double? {
  if (track.isEmpty()) return null
  val event = eventDays.inJson()
  if (event == 0.0) return orb.inJson()

  val days = track.map { it.days.inJson() }
  val applying = track.map { it.applyingLng.inJson() }
  val applied = track.map { it.appliedLng.inJson() }

  /** 第 [day] 天兩顆星的黃經（追的、被追的） */
  fun at(day: Double): Pair<Double, Double> {
    if (day <= days.first()) return applying.first() to applied.first()
    for (i in 1 until days.size) {
      if (day <= days[i]) {
        val k = (day - days[i - 1]) / (days[i] - days[i - 1]).let { if (it == 0.0) 1.0 else it }
        return (applying[i - 1] + signedGap(applying[i], applying[i - 1]) * k) to (applied[i - 1] + signedGap(applied[i], applied[i - 1]) * k)
      }
    }
    return applying.last() to applied.last()
  }

  val deg = aspect.degree
  val (start0, start1) = at(0.0)
  val rel0 = signedGap(start0, start1)
  val side = if (angleGap(rel0, -deg) < angleGap(rel0, deg)) -deg else deg
  val (end0, end1) = at(event)
  return angleGap(signedGap(end0, end1), side).toHundredths()
}
