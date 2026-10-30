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
 * 依「兩星的差距」判斷臨陣退縮：自盤面時刻往後追兩星黃經，看離成形還差幾度（帶正負號的 gap）——
 * - gap 先變號（穿過 0）＝ 相位成形，不算
 * - gap 還沒歸零就開始變大 ＝ 沒有成形；這段期間內有一顆星停滯，它就是退縮者
 * - 期間內沒有星停滯（只是快星減速、被慢星甩開）不算 —— 古典的 refranation 要有轉向
 *
 * 2026-10-30 取代 2014 年的舊版（比「下次停滯」是否早於「下次成形」）。舊版的問題：
 * - 「下次成形」用真實星曆找，被拒絕的相位，下次成形常在幾個月、幾年後，中間幾乎任何一顆星都會停滯一次
 * - 對方停滯、轉向後反而迎上來的（相位提早成形），也被算成退縮
 * - 只要在交角容許度內入相位就判斷，停滯若發生在兩星早已分離之後也算
 */
class RefranationImpl(
  private val aspectCalculator: IAspectCalculator,
  private val starPositionImpl: IStarPosition<*>,
  private val retrogradeImpl: IRetrograde,
  private val aspects: Set<Aspect> = Aspect.getAspects(Aspect.Importance.HIGH).toSet(),
) : IRefranation {

  override fun getResult(h: IHoroscopeModel, p1: Planet, p2: Planet): Refranation? {
    if (p1 === p2) return null
    val applying = with(aspectCalculator) { h.getAspectPattern(setOf(p1, p2), aspects) }
      ?.takeIf { it.aspectType == APPLYING } ?: return null

    val t0 = h.gmtJulDay
    // 日月不會停滯；兩顆都不會停滯就不可能退縮
    val stationers = listOf(p1, p2).filter { it !== Planet.SUN && it !== Planet.MOON }
    if (stationers.isEmpty()) return null

    fun pos(p: Planet, t: GmtJulDay) = starPositionImpl.getPosition(p, t, GEO, ECLIPTIC, StarTypeOptions.MEAN)

    // 帶號的差距：兩星黃經差 減去 交角（交角兩側取盤面時刻較近的那一側）
    val deg = applying.aspect.degree
    val r0 = (pos(p1, t0).lng - pos(p2, t0).lng).normalizeSigned()
    val target = listOf(deg, -deg).minBy { abs((r0 - it).normalizeSigned()) }

    data class Sample(val t: GmtJulDay, val lng1: Double, val lng2: Double, val gap: Double, val rate: Double)

    fun sample(t: GmtJulDay): Sample {
      val a = pos(p1, t)
      val b = pos(p2, t)
      return Sample(t, a.lng, b.lng, (a.lng - b.lng - target).normalizeSigned(), a.speedLng - b.speedLng)
    }

    /** 下一步：剩下的差距照目前的相對速度要走幾天，取八分之一，夾在 15 分鐘到 2 天之間 */
    fun step(s: Sample): Double =
      (abs(s.gap) / max(abs(s.rate), 1e-6) / 8).coerceIn(1.0 / 96, 2.0)

    // 1. 往後追到 gap 變號（成形）或開始變大（最接近點已過）
    val samples = mutableListOf(sample(t0))
    while (true) {
      val last = samples.last()
      if (last.t - t0 > MAX_DAYS) return null
      val next = sample(last.t + step(last))
      if (next.gap == 0.0 || next.gap.sign != last.gap.sign) return null
      samples += next
      if (abs(next.gap) > abs(last.gap)) break
    }

    // 2. 在最接近的取樣前後細找最接近的時刻
    val n = samples.size
    val (closestT, closestGap) = ExtremumTools.minimize(samples[maxOf(n - 3, 0)].t.value, samples[n - 1].t.value, 1e-6) { t ->
      abs(sample(GmtJulDay(t)).gap)
    }.let { (t, gap) -> GmtJulDay(t) to gap }
    if (closestGap < PERFECT_ORB) return null

    // 3. 繼續追到差距拉回盤面時刻的大小（或與接近段等長），給動畫的軌跡
    val initialGap = abs(samples.first().gap)
    val tEnd = closestT + (closestT - t0)
    while (samples.last().t < tEnd && abs(samples.last().gap) < initialGap) {
      samples += sample(samples.last().t + step(samples.last()))
    }

    // 4. 退縮者：這段期間內停滯、而且離最接近時刻最近的那顆。
    //    停滯的搜尋最貴，放到確定「沒成形就拉開」之後才做 —— 大部分入相位在步驟 1 就成形了
    val stationary = stationers.map { retrogradeImpl.getNextStationary(it, t0, true, starPositionImpl) }
      .filter { it.gmtJulDay > t0 && it.gmtJulDay <= samples.last().t }
      .minByOrNull { abs(it.gmtJulDay - closestT) } ?: return null
    val refranator = stationary.star as Planet
    val other = if (refranator === p1) p2 else p1

    // 動畫用的軌跡：與搜尋的步長脫鉤，等距取樣，盤面時刻離停滯很近時也有足夠的點演出轉向
    val span = samples.last().t - t0
    val track = (0..TRACK_SAMPLES).map { i ->
      val s = sample(t0 + span * i / TRACK_SAMPLES)
      if (refranator === p1) RefranationSample(s.t - t0, s.lng1, s.lng2)
      else RefranationSample(s.t - t0, s.lng2, s.lng1)
    }

    return Refranation(
      refranator, other, applying, stationary,
      stationDays = stationary.gmtJulDay - t0,
      closestDays = closestT - t0,
      closestOrb = closestGap,
      track = track,
    )
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
