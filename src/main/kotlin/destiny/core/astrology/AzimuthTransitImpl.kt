/**
 * Created by smallufo on 2026-07-10.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import jakarta.inject.Named
import kotlin.math.abs
import kotlin.math.sign

/**
 * [IAzimuthTransit] 的實作。
 *
 * 純建於既有 forward API [IStarPositionWithAzimuthCalculator] 之上（粗掃偵測變號 + 二分求根），
 * 與 swisseph 無關，故置於 destiny-core。
 */
@Named
class AzimuthTransitImpl(
  private val starPositionWithAzimuthImpl: IStarPositionWithAzimuthCalculator
) : IAzimuthTransit {

  override fun getNextAzimuthTransit(
    star: Star,
    targetAzimuthDeg: Double,
    fromGmt: GmtJulDay,
    loc: ILocation,
    forward: Boolean,
    centric: Centric,
    aboveHorizonOnly: Boolean,
    temperature: Double,
    pressure: Double,
    options: StarTypeOptions
  ): AzimuthTransit? {
    // 星種守衛：Arabic (阿拉伯點) / LunarStation (二十八宿) 無法解析出單一星體位置
    if (star is Arabic || star is LunarStation)
      return null

    val target = ((targetAzimuthDeg % 360.0) + 360.0) % 360.0

    fun azAt(t: GmtJulDay): IStarPositionWithAzimuth =
      starPositionWithAzimuthImpl.getPositionWithAzimuth(star, t, loc, centric, Coordinate.ECLIPTIC, temperature, pressure, options)

    /** 與 target 的帶符號夾角，wrap 到 (-180, 180] */
    fun diff(t: GmtJulDay): Double = wrap180(azAt(t).azimuthDeg - target)

    val dir = if (forward) 1.0 else -1.0

    var t1 = fromGmt
    var d1 = diff(t1)
    var scanned = 0.0
    while (scanned < IAzimuthTransit.MAX_SCAN_DAYS) {
      val t2 = t1 + dir * COARSE_STEP
      val d2 = diff(t2)

      // 真實通過：變號，且兩端都靠近 0（排除對蹠點 ±180 環繞造成的假變號）
      if (d1 != 0.0 && sign(d1) != sign(d2) && abs(d1) < CROSSING_GUARD && abs(d2) < CROSSING_GUARD) {
        val root = refine(t1, t2, ::diff)
        val pos = azAt(root)
        if (!aboveHorizonOnly || pos.trueAltitude > 0.0) {
          return AzimuthTransit(
            gmtJulDay = root,
            azimuthDeg = pos.azimuthDeg,
            trueAltitude = pos.trueAltitude,
            apparentAltitude = pos.apparentAltitude,
            // 以「真實時間往後」探測高度變化：增加 = 上升段 (東半天)
            ascending = azAt(root + ASC_PROBE).trueAltitude > pos.trueAltitude
          )
        }
        // 被 aboveHorizonOnly 過濾掉，繼續往後掃
      }

      t1 = t2
      d1 = d2
      scanned += COARSE_STEP
    }
    return null
  }

  /** 帶符號夾角 wrap 到 (-180, 180] */
  private fun wrap180(deg: Double): Double {
    var d = deg % 360.0
    if (d > 180.0) d -= 360.0
    if (d <= -180.0) d += 360.0
    return d
  }

  /** 在 [a0, b0]（[diff] 已變號）內二分求根 */
  private fun refine(a0: GmtJulDay, b0: GmtJulDay, f: (GmtJulDay) -> Double): GmtJulDay {
    var a = a0
    var b = b0
    var fa = f(a)
    repeat(MAX_BISECT) {
      val m = a + (b.value - a.value) / 2.0
      val fm = f(m)
      if (fm == 0.0 || abs(b.value - a.value) < TOLERANCE)
        return m
      if (sign(fm) == sign(fa)) {
        a = m
        fa = fm
      } else {
        b = m
      }
    }
    return a + (b.value - a.value) / 2.0
  }

  companion object {
    /** 粗掃步長 (天)：4 分鐘。須小到讓每步方位角移動遠小於 [CROSSING_GUARD] */
    private const val COARSE_STEP = 4.0 / 1440.0

    /** 判定「真實通過」的夾角上限：變號兩端皆須 < 此值，否則視為對蹠點環繞 */
    private const val CROSSING_GUARD = 90.0

    /** 二分收斂的時間容差 (天)：約 0.5 秒 */
    private const val TOLERANCE = 0.5 / 86400.0

    /** 二分最大迭代次數 */
    private const val MAX_BISECT = 40

    /** 判定上升/下降段的高度探測間隔 (天)：約 10 秒 */
    private const val ASC_PROBE = 10.0 / 86400.0
  }
}
