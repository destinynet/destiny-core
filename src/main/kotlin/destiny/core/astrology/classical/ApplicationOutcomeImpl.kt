/**
 * Created by smallufo on 2026-10-30.
 */
package destiny.core.astrology.classical

import destiny.core.astrology.*
import destiny.core.astrology.Centric.GEO
import destiny.core.astrology.Coordinate.ECLIPTIC
import destiny.core.astrology.IPointAspectPattern.AspectType.APPLYING
import destiny.core.calendar.GmtJulDay
import destiny.tools.CircleTools.normalizeSigned
import destiny.tools.ExtremumTools
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

/**
 * 入相位的結局：自盤面時刻往後追兩星的帶號差距（gap：黃經差 減去 交角，交角兩側取盤面時刻較近的那一側），
 * 看最先發生的是哪件事 ——
 * 1. gap 變號（穿過 0）＝ 成形 → [ApplicationOutcome.Perfected]
 * 2. gap 還沒歸零就開始變大，而這段期間內有一顆星停滯（離最接近時刻最近的那次）→ [ApplicationOutcome.Refranation]；
 *    期間內沒有停滯（只是快星減速、被慢星甩開）→ 沒有結局（null）
 * 3. 在 1／2 之前，第三顆星先與**被追的那顆**成形 → [ApplicationOutcome.Prohibition] 或 [ApplicationOutcome.Frustration]
 *
 * 插隊只看被追的那顆（B）：Lilly 的 prohibition 是「A 到達之前，別的星先碰到 B」。第三顆星先與快星 A 成形不算 ——
 * 快星（尤其月亮）一天內就會與容許度內的星成形，若連 A 也算，幾乎每個慢星對都被「插隊」，臨陣退縮也被蓋掉。
 * 插隊者只考慮**盤面時刻就已在容許度內入相位 B** 的古典行星（與光線傳遞「只看當下容許度」一致）。
 *
 * 2026-10-30 由同日改寫的 RefranationImpl（臨陣退縮）擴充而來：判準 2 不變，多了判準 1 的結果與判準 3 的插隊。
 * 2014 年的舊版（比「下次停滯」是否早於「下次成形」）的問題：
 * - 「下次成形」用真實星曆找，被拒絕的相位，下次成形常在幾個月、幾年後，中間幾乎任何一顆星都會停滯一次
 * - 對方停滯、轉向後反而迎上來的（相位提早成形），也被算成退縮
 * - 只要在交角容許度內入相位就判斷，停滯若發生在兩星早已分離之後也算
 */
class ApplicationOutcomeImpl(
  private val aspectCalculator: IAspectCalculator,
  private val starPositionImpl: IStarPosition<*>,
  private val retrogradeImpl: IRetrograde,
  private val aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH).toSet(),
) : IApplicationOutcome {

  private fun pos(p: Planet, t: GmtJulDay) = starPositionImpl.getPosition(p, t, GEO, ECLIPTIC, StarTypeOptions.MEAN)

  private data class Sample(val t: GmtJulDay, val lng1: Double, val lng2: Double, val gap: Double, val rate: Double)

  /** [p1] 與 [p2] 以 [degree] 度交角的帶號差距 */
  private inner class Gap(val p1: Planet, val p2: Planet, degree: Double, t0: GmtJulDay) {
    private val target: Double = (pos(p1, t0).lng - pos(p2, t0).lng).normalizeSigned().let { r0 ->
      listOf(degree, -degree).minBy { abs((r0 - it).normalizeSigned()) }
    }

    fun sample(t: GmtJulDay): Sample {
      val a = pos(p1, t)
      val b = pos(p2, t)
      return Sample(t, a.lng, b.lng, (a.lng - b.lng - target).normalizeSigned(), a.speedLng - b.speedLng)
    }

    /** 下一步：剩下的差距照目前的相對速度要走幾天，取八分之一，夾在 15 分鐘到 2 天之間 */
    fun step(s: Sample): Double =
      (abs(s.gap) / max(abs(s.rate), 1e-6) / 8).coerceIn(1.0 / 96, 2.0)
  }

  private sealed interface Approach {
    data class Perfects(val t: GmtJulDay) : Approach
    data class TurnsAway(val closestT: GmtJulDay, val closestGap: Double) : Approach
  }

  /**
   * 往後追到成形或最接近點，沿途取樣存進 [samples]（第一點是 [t0]）。
   * 超過 [limit] 還沒有結果、或追了 [MAX_DAYS] 還沒有結果時為 null
   */
  private fun approach(g: Gap, t0: GmtJulDay, samples: MutableList<Sample>, limit: GmtJulDay? = null): Approach? {
    samples += g.sample(t0)
    while (true) {
      val last = samples.last()
      if (last.t - t0 > MAX_DAYS || (limit != null && last.t > limit)) return null
      val next = g.sample(last.t + g.step(last))
      samples += next
      if (next.gap == 0.0 || next.gap.sign != last.gap.sign) {
        val (t, _) = ExtremumTools.minimize(last.t.value, next.t.value, 1e-6) { t -> abs(g.sample(GmtJulDay(t)).gap) }
        return Approach.Perfects(GmtJulDay(t))
      }
      if (abs(next.gap) > abs(last.gap)) break
    }

    // 在最接近的取樣前後細找最接近的時刻
    val n = samples.size
    val (t, gap) = ExtremumTools.minimize(samples[maxOf(n - 3, 0)].t.value, samples[n - 1].t.value, 1e-6) { t ->
      abs(g.sample(GmtJulDay(t)).gap)
    }
    return if (gap < PERFECT_ORB) Approach.Perfects(GmtJulDay(t)) else Approach.TurnsAway(GmtJulDay(t), gap)
  }

  /** 插隊：第三顆星 [by] 在 [t] 先與被追的那顆成形（[pattern] 是盤面時刻兩者的入相位） */
  private data class Interruption(val by: Planet, val pattern: IPointAspectPattern, val t: GmtJulDay)

  /** 盤面時刻在容許度內與 [b] 入相位的第三顆星中，在 [before] 之前最先成形的那個（[a] 除外） */
  private fun interruption(h: IHoroscopeModel, a: Planet, b: Planet, t0: GmtJulDay, before: GmtJulDay): Interruption? =
    with(aspectCalculator) {
      Planet.classicalList.filter { it !== a && it !== b }.mapNotNull { c ->
        val p = h.getAspectPattern(setOf(c, b), aspects)?.takeIf { it.aspectType == APPLYING } ?: return@mapNotNull null
        (approach(Gap(c, b, p.aspect.degree, t0), t0, mutableListOf(), before) as? Approach.Perfects)
          ?.takeIf { it.t < before }
          ?.let { Interruption(c, p, it.t) }
      }.minByOrNull { it.t }
    }

  override fun getOutcome(h: IHoroscopeModel, p1: Planet, p2: Planet): ApplicationOutcome? {
    if (p1 === p2) return null
    val pattern = with(aspectCalculator) { h.getAspectPattern(setOf(p1, p2), aspects) }
      ?.takeIf { it.aspectType == APPLYING } ?: return null

    val speedOf = { p: Planet -> abs(h.getStarPosition(p)?.speedLng ?: 0.0) }
    val (a, b) = if (speedOf(p1) >= speedOf(p2)) p1 to p2 else p2 to p1
    val t0 = h.gmtJulDay

    // 1. 兩星本身：成形，或最接近後轉開
    val gap = Gap(a, b, pattern.aspect.degree, t0)
    val samples = mutableListOf<Sample>()
    val approach = approach(gap, t0, samples) ?: return null

    // 2. 轉開的：找退縮者。停滯的搜尋最貴，放到確定「沒成形就拉開」之後才做 —— 大部分入相位在 1 就成形了
    data class Refrain(val stationary: Stationary, val closestT: GmtJulDay, val closestGap: Double)

    val refrain: Refrain? = (approach as? Approach.TurnsAway)?.let { away ->
      // 繼續追到差距拉回盤面時刻的大小（或與接近段等長），給動畫的軌跡
      val initialGap = abs(samples.first().gap)
      val tEnd = away.closestT + (away.closestT - t0)
      while (samples.last().t < tEnd && abs(samples.last().gap) < initialGap) {
        samples += gap.sample(samples.last().t + gap.step(samples.last()))
      }
      // 這段期間內停滯、而且離最接近時刻最近的那顆；日月不會停滯
      listOf(a, b).filter { it !== Planet.SUN && it !== Planet.MOON }
        .map { retrogradeImpl.getNextStationary(it, t0, true, starPositionImpl) }
        .filter { it.gmtJulDay > t0 && it.gmtJulDay <= samples.last().t }
        .minByOrNull { abs(it.gmtJulDay - away.closestT) }
        ?.let { Refrain(it, away.closestT, away.closestGap) }
        ?: return null
    }
    val eventT = when (approach) {
      is Approach.Perfects  -> approach.t
      is Approach.TurnsAway -> approach.closestT
    }

    // 3. 在那之前有沒有第三顆星先碰到被追的那顆
    val cut = interruption(h, a, b, t0, eventT)
    val decidedT = cut?.t ?: eventT

    val ingress = ingressBefore(samples, decidedT, a, b)

    // 動畫用的軌跡：與搜尋的步長脫鉤，等距取樣。退縮演到兩星再度拉開；其餘演到決定結局的那一刻
    val trackEnd = if (refrain != null && cut == null) samples.last().t else decidedT
    val span = trackEnd - t0
    val track = (0..TRACK_SAMPLES).map { i ->
      val s = gap.sample(t0 + span * i / TRACK_SAMPLES)
      OutcomeSample(s.t - t0, s.lng1, s.lng2)
    }

    return when {
      cut != null && speedOf(b) > speedOf(cut.by) ->
        ApplicationOutcome.Frustration(a, b, pattern, cut.t - t0, ingress, track, frustrator = cut.by, interruption = cut.pattern)

      cut != null ->
        ApplicationOutcome.Prohibition(a, b, pattern, cut.t - t0, ingress, track, prohibitor = cut.by, interruption = cut.pattern)

      refrain != null ->
        ApplicationOutcome.Refranation(
          a, b, pattern, ingress, track,
          refranator = refrain.stationary.star as Planet,
          stationary = refrain.stationary,
          stationDays = refrain.stationary.gmtJulDay - t0,
          closestDays = refrain.closestT - t0,
          closestOrb = refrain.closestGap,
        )

      else -> ApplicationOutcome.Perfected(a, b, pattern, eventT - t0, ingress, track)
    }
  }

  /** [before] 之前換星座的那顆（[a]／[b]），兩顆都換取先換的；以追蹤時的取樣判斷（步長最多 2 日） */
  private fun ingressBefore(samples: List<Sample>, before: GmtJulDay, a: Planet, b: Planet): Planet? {
    val first = samples.first()
    val within = samples.filter { it.t <= before }
    val ia = within.indexOfFirst { ZodiacSign.of(it.lng1) != ZodiacSign.of(first.lng1) }
    val ib = within.indexOfFirst { ZodiacSign.of(it.lng2) != ZodiacSign.of(first.lng2) }
    return when {
      ia < 0 && ib < 0 -> null
      ib < 0           -> a
      ia < 0           -> b
      else             -> if (ia <= ib) a else b
    }
  }

  companion object {
    /** 最多往後追兩年（木土這類慢星的入相位可以拖很久） */
    private const val MAX_DAYS = 730.0

    /** 軌跡等分幾段 */
    private const val TRACK_SAMPLES = 48

    /** 最接近時差距小於此（度），視同成形 */
    private const val PERFECT_ORB = 0.01
  }
}
