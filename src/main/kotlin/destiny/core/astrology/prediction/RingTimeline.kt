/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
import destiny.core.astrology.Planet
import destiny.core.astrology.RingKind
import destiny.core.calendar.GmtJulDay
import destiny.tools.BisectionTools
import destiny.tools.CircleTools.normalizeSigned
import kotlin.math.abs

/**
 * 外圈各點（行運、次限、太陽弧）的等距取樣：第 i 筆是 [from] ＋ i × [stepDays] 那一刻。
 * 播放端只查表、在相鄰兩筆之間內插，不自己算星曆。
 *
 * @param lngs 一個點一串黃經（0–360），與 [points] 同順序、每串 [size] 筆
 * @param retrograde 與 [lngs] 同形：行運與次限的星體有（次限的停滯、逆行有意義；推運軸點恆為 false）；太陽弧只會順行，為 null
 */
data class RingTrack(
  val kind: RingKind,
  val from: GmtJulDay,
  val stepDays: Double,
  val points: List<AstroPoint>,
  val lngs: List<List<Double>>,
  val retrograde: List<List<Boolean>>? = null,
) {
  init {
    require(stepDays > 0) { "取樣間隔要大於 0" }
    require(lngs.size == points.size) { "lngs 要一個點一串" }
    require(lngs.map { it.size }.distinct().size <= 1) { "每串的筆數要相同" }
    retrograde?.let { r -> require(r.size == points.size && r.all { it.size == size }) { "retrograde 要與 lngs 同形" } }
  }

  /** 每串幾筆 */
  val size: Int get() = lngs.firstOrNull()?.size ?: 0

  /** 第 [i] 筆的時刻 */
  fun timeAt(i: Int): GmtJulDay = from + i * stepDays

  /** 取樣的最後一刻 */
  val to: GmtJulDay get() = timeAt(size - 1)

  /**
   * 第 [k] 個點在 [t] 的黃經：相鄰兩筆之間線性內插（走最短的那一邊，跨過 0° 也對）；超出範圍取頭尾那一筆。
   * 播放端用同一個方法畫外圈；慢行星十天一筆時誤差在 0.1° 以內
   */
  fun lngAt(k: Int, t: GmtJulDay): Double {
    val s = lngs[k]
    val x = ((t - from) / stepDays).coerceIn(0.0, (size - 1).toDouble())
    val i = x.toInt().coerceAtMost(size - 2).coerceAtLeast(0)
    if (size == 1) return s[0]
    val f = x - i
    return (s[i] + (s[i + 1] - s[i]).normalizeSigned() * f).mod(360.0)
  }
}

/**
 * 一組相位的容許度區間：外圈的 [mover] 對本命的 [natal] 成 [aspect]，從 [enter] 進入容許度、到 [leave] 離開。
 * 行運逆行時，同一個區間裡可能精準好幾次（[exacts]，依時間排序）；也可能進了容許度又退出、沒有精準（[exacts] 為空）。
 * 區間被取樣範圍截斷時，[enter]／[leave] 就是取樣的頭尾
 */
data class RingAspectSpan(
  val mover: AstroPoint,
  val natal: AstroPoint,
  val aspect: Aspect,
  val enter: GmtJulDay,
  val exacts: List<GmtJulDay>,
  val leave: GmtJulDay,
) {
  operator fun contains(t: GmtJulDay): Boolean = t >= enter && t < leave
}

/**
 * 一條取樣（[times]、[lngs]）上，外圈點走到「[natalLng] ＋ [side] 度」的每一個時刻（依時間排序）—— 相位精準的那一刻。
 * [side] 是 [Aspect.sides] 的其中一個（合相 0、四分 ±90 …），所以一個相位要對每個 side 各呼叫一次。
 *
 * 做法：相鄰兩筆的有號角距變號 ⇒ 中間過了一次精準，再用 [lngAt] 以二分法逼近（[rounds] 輪）。
 * 取樣只用來找「大約在哪兩筆之間」，所以 [lngAt] 可以是取樣之間的內插，也可以是真的星曆（要到分鐘就給星曆）。
 *
 * 角距在正對面（±180°）也會變號，那不是精準：兩筆的角距差超過半圈就是那一種，略過。
 * 前提是相鄰兩筆之間外圈點走不到半圈（180°）—— 任何合理的取樣都滿足。
 * 逆行來回、在同一個取樣間隔內精準兩次（變號兩次 ⇒ 兩端同號）的看不到；要抓得到就取密一點
 */
fun exactTimesOf(
  natalLng: Double, side: Double, times: List<GmtJulDay>, lngs: List<Double>,
  lngAt: (GmtJulDay) -> Double, rounds: Int = 20,
): List<GmtJulDay> {
  require(times.size == lngs.size) { "times 與 lngs 要一樣長" }
  /** 外圈點距精準的有號角距 (-180, 180] */
  fun delta(lng: Double) = (lng - natalLng - side).normalizeSigned()
  val d = lngs.map(::delta)
  return (0 until d.size - 1).mapNotNull { i ->
    val d0 = d[i]; val d1 = d[i + 1]
    // 取樣剛好落在精準上：算這一筆的（它的前一對是「非零 → 0」，乘積為 0、不會重複算）
    if (d0 == 0.0) return@mapNotNull times[i]
    if (d0 * d1 >= 0 || abs(d1 - d0) >= 180) return@mapNotNull null
    // 還沒過精準的那一邊 ＝ 角距與 d0 同號
    BisectionTools.bisect(times[i].value, times[i + 1].value, rounds) { mid -> delta(lngAt(GmtJulDay(mid))) * d0 > 0 }
      .let { (lo, hi) -> GmtJulDay((lo + hi) / 2) }
  }
}

/**
 * 一條取樣（[times]、[lngs]）上，外圈點 [mover] 對黃經 [natalLng] 的本命點 [natal] 成 [aspect]（容許度 [orb] 度）的全部區間。
 *
 * 取樣只用來找「大約在哪兩筆之間」：進出容許度與精準的時刻都用 [lngAt] 以二分法逼近（[rounds] 輪）。
 * 呼叫端要保證取樣夠密 —— 相鄰兩筆之間外圈點走的角度小於 2 × [orb]，否則可能整段漏掉。
 * 合相、對沖只有一個方向；其餘相位左右各一（本命點 ± 角度，[Aspect.sides]），分開列。結果依 [RingAspectSpan.enter] 排序。
 * 精準的時刻由 [exactTimesOf] 找。
 */
fun aspectSpansOf(
  mover: AstroPoint, natal: AstroPoint, natalLng: Double, aspect: Aspect, orb: Double,
  times: List<GmtJulDay>, lngs: List<Double>,
  lngAt: (GmtJulDay) -> Double,
  rounds: Int = 20,
): List<RingAspectSpan> {
  require(times.size == lngs.size) { "times 與 lngs 要一樣長" }
  if (times.size < 2) return emptyList()
  return aspect.sides.flatMap { side ->
    /** 外圈點距精準的有號角距 (-180, 180] */
    fun delta(lng: Double) = (lng - natalLng - side).normalizeSigned()
    fun deltaAt(t: GmtJulDay) = delta(lngAt(t))
    fun inOrb(d: Double) = abs(d) <= orb
    val d = lngs.map(::delta)

    /** 進出容許度的交界：[a] 在 [aIn] 那一邊 */
    fun edge(a: GmtJulDay, b: GmtJulDay, aIn: Boolean): GmtJulDay =
      BisectionTools.bisect(a.value, b.value, rounds) { mid -> inOrb(deltaAt(GmtJulDay(mid))) == aIn }.let { (lo, hi) -> GmtJulDay((lo + hi) / 2) }

    val exactAll: List<GmtJulDay> = exactTimesOf(natalLng, side, times, lngs, lngAt, rounds)

    val spans = mutableListOf<Pair<GmtJulDay, GmtJulDay>>()
    var start: GmtJulDay? = if (inOrb(d[0])) times[0] else null
    for (i in 1 until d.size) {
      val was = inOrb(d[i - 1]); val now = inOrb(d[i])
      when {
        !was && now -> start = edge(times[i - 1], times[i], aIn = false)
        was && !now -> { spans += start!! to edge(times[i - 1], times[i], aIn = true); start = null }
        // 兩筆都在外面、中間卻精準過：走得太快穿過整個容許度（取樣不夠密時的保險）
        !was && !now -> exactAll.firstOrNull { it > times[i - 1] && it < times[i] }?.let { x ->
          spans += edge(times[i - 1], x, aIn = false) to edge(x, times[i], aIn = true)
        }
      }
    }
    start?.let { spans += it to times.last() }

    spans.map { (enter, leave) ->
      RingAspectSpan(mover, natal, aspect, enter, exactAll.filter { it >= enter && it <= leave }, leave)
    }
  }.sortedBy { it.enter }
}

/**
 * 一次回歸（太陽回歸、月亮回歸）：[planet] 在 [at] 回到本命的黃經，這張回歸盤管到下一次回歸 [to]。
 * [asc]：回歸那一刻在出生地的上升黃經（出生時間不夠精確、或沒有算 ⇒ null）
 */
data class ReturnPeriod(val planet: Planet, val at: GmtJulDay, val to: GmtJulDay, val asc: Double? = null) {
  operator fun contains(t: GmtJulDay): Boolean = t >= at && t < to
}
