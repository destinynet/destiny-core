/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Aspect
import destiny.core.astrology.AstroPoint
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
 * @param retrograde 與 [lngs] 同形；只有行運才有（推運與太陽弧不談逆行）
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
 * 一條取樣（[times]、[lngs]）上，外圈點 [mover] 對黃經 [natalLng] 的本命點 [natal] 成 [aspect]（容許度 [orb] 度）的全部區間。
 *
 * 取樣只用來找「大約在哪兩筆之間」：進出容許度與精準的時刻都用 [lngAt] 以二分法逼近（[rounds] 輪）。
 * 呼叫端要保證取樣夠密 —— 相鄰兩筆之間外圈點走的角度小於 2 × [orb]，否則可能整段漏掉。
 * 合相、對沖只有一個方向；其餘相位左右各一（本命點 ± 角度），分開列。結果依 [RingAspectSpan.enter] 排序。
 */
fun aspectSpansOf(
  mover: AstroPoint, natal: AstroPoint, natalLng: Double, aspect: Aspect, orb: Double,
  times: List<GmtJulDay>, lngs: List<Double>,
  lngAt: (GmtJulDay) -> Double,
  rounds: Int = 20,
): List<RingAspectSpan> {
  require(times.size == lngs.size) { "times 與 lngs 要一樣長" }
  if (times.size < 2) return emptyList()
  val sides = if (aspect.degree == 0.0 || aspect.degree == 180.0) listOf(aspect.degree) else listOf(aspect.degree, -aspect.degree)

  return sides.flatMap { side ->
    /** 外圈點距精準的有號角距 (-180, 180] */
    fun delta(lng: Double) = (lng - natalLng - side).normalizeSigned()
    fun deltaAt(t: GmtJulDay) = delta(lngAt(t))
    fun inOrb(d: Double) = abs(d) <= orb
    val d = lngs.map(::delta)

    /** 進出容許度的交界：[a] 在 [aIn] 那一邊 */
    fun edge(a: GmtJulDay, b: GmtJulDay, aIn: Boolean): GmtJulDay =
      BisectionTools.bisect(a.value, b.value, rounds) { mid -> inOrb(deltaAt(GmtJulDay(mid))) == aIn }.let { (lo, hi) -> GmtJulDay((lo + hi) / 2) }

    // 相鄰兩筆角距變號（且離精準不遠、排除 ±180 那一邊的跳號）⇒ 中間有一次精準
    val exactAll: List<GmtJulDay> = (0 until d.size - 1).mapNotNull { i ->
      val d0 = d[i]; val d1 = d[i + 1]
      if (d0 == 0.0) return@mapNotNull times[i]
      if (d0 * d1 >= 0 || abs(d0) > 10 || abs(d1) > 10) return@mapNotNull null
      BisectionTools.bisect(times[i].value, times[i + 1].value, rounds) { mid -> deltaAt(GmtJulDay(mid)) * d0 > 0 }
        .let { (lo, hi) -> GmtJulDay((lo + hi) / 2) }
    }

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
