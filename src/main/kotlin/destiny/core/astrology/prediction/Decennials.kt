/**
 * 十年限（Decennials）—— Vettius Valens《Anthology》的時主星技法。
 *
 * - **主限（L1）**：七星輪流各主 129 個月（10 年 9 個月），一輪 903 個月（75 年 3 個月），之後重新輪替。
 * - **起始星**：區分光體（日生從太陽、夜生從月亮），見 [IDecennialsStart]。
 * - **順序**：其餘六星依本命盤的黃經，從起始星順黃道往後排（不是迦勒底順序），見 [decennialsOrder]。
 * - **子限（L2）**：主限的 129 個月依七星最小年分給七星（土 30、木 12、火 15、日 19、金 8、水 20、月 25 個月），
 *   從主限主星起、順序與主限相同。
 * - **第三層以下**：同一套比例再分一次 —— 每顆星分得上一層長度 × 最小年 / 129
 *   （例：土星 30 個月的子限 = 900 天，其中土星分得 900 × 30 / 129 ≈ 209.3 天）。
 *
 * 月以 30 天、年以 360 天計（[ValensYearLength.DEFAULT]）；改用 365.25 天時各層等比放大。
 *
 * Created by smallufo on 2026-11-26.
 */
package destiny.core.astrology.prediction

import destiny.core.astrology.Planet
import destiny.core.astrology.ZodiacDegree
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ITimeSpan
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/** 主限長度：129 個月 */
const val DECENNIAL_MAJOR_MONTHS: Int = 129

/**
 * 十年限的一段。
 *
 * @property lords 從主限到這一層的主星，外層在前：`[主限主星, 子限主星, …]`；本段的主星是最後一個
 */
@Serializable
data class Decennial(
  val lords: List<Planet>,
  @Contextual
  override val fromTime: GmtJulDay,
  @Contextual
  override val toTime: GmtJulDay,
) : ITimeSpan {
  init {
    require(lords.isNotEmpty()) { "lords must not be empty" }
  }

  /** 第幾層（主限 = 1） */
  val level: Int get() = lords.size

  /** 本段的主星 */
  val lord: Planet get() = lords.last()

  /** 所屬主限的主星 */
  val majorLord: Planet get() = lords.first()
}

/**
 * 十年限從哪顆星起算。
 *
 * 各家的共識是區分光體；有些作法在區分光體落在不利位置時改從另一顆光體、甚至從上升之後的第一顆星起算，
 * 判準不一，需要時以另一個實作加入。
 */
interface IDecennialsStart {
  /**
   * @param diurnal 是否日生盤
   * @param positions 七星的本命黃經
   */
  fun starter(diurnal: Boolean, positions: Map<Planet, ZodiacDegree>): Planet
}

/** 區分光體：日生從太陽、夜生從月亮 */
data object DecennialsSectLight : IDecennialsStart {
  override fun starter(diurnal: Boolean, positions: Map<Planet, ZodiacDegree>): Planet =
    if (diurnal) Planet.SUN else Planet.MOON
}

/**
 * 從 [starter] 起，七星依本命黃經順黃道排列（[starter] 在第一個）。
 * 同度的兩顆星依 [Planet.classicalList] 的順序，只為了結果固定。
 */
fun decennialsOrder(starter: Planet, positions: Map<Planet, ZodiacDegree>): List<Planet> {
  require(positions.keys.containsAll(Planet.classicalList)) { "positions must contain all seven classical planets" }
  val origin = positions.getValue(starter)
  return Planet.classicalList.sortedWith(
    compareBy<Planet> { if (it == starter) -1.0 else positions.getValue(it).aheadOf(origin) }
      .thenBy { Planet.classicalList.indexOf(it) }
  )
}

/**
 * 把 [parent] 依七星最小年分成下一層，從 [parent] 的主星起、依 [order] 輪替。
 * 最後一段的結束時間與 [parent] 精確吻合。
 */
private fun subdivide(parent: Decennial, order: List<Planet>): List<Decennial> {
  val total = parent.toTime - parent.fromTime
  val startIndex = order.indexOf(parent.lord)
  var cursor = parent.fromTime
  return (0 until 7).map { i ->
    val lord = order[(startIndex + i) % 7]
    val end = if (i == 6) parent.toTime else cursor + total * planetLeastYears.getValue(lord) / PLANET_LEAST_YEARS_TOTAL
    Decennial(parent.lords + lord, cursor, end).also { cursor = end }
  }
}

/** 從 [birth] 起無限輪替的主限，惰性產生 */
private fun majorDecennials(order: List<Planet>, birth: GmtJulDay, yearLength: ValensYearLength): Sequence<Decennial> {
  val majorDays = DECENNIAL_MAJOR_MONTHS * yearLength.monthDays
  return generateSequence(0) { it + 1 }.map { i ->
    val from = birth + i * majorDays
    Decennial(listOf(order[i % 7]), from, from + majorDays)
  }
}

/** [period] 及其下到 [maxLevel] 層、且與 `[from, to)` 重疊的所有段落 */
private fun expand(period: Decennial, order: List<Planet>, maxLevel: Int, from: GmtJulDay, to: GmtJulDay): List<Decennial> {
  if (!(period.fromTime < to && from < period.toTime)) return emptyList()
  return listOf(period) + if (period.level < maxLevel) subdivide(period, order).flatMap { expand(it, order, maxLevel, from, to) } else emptyList()
}

/**
 * 與 `[from, to)` 重疊的所有十年限段落（L1..[maxLevel]），依起始時間、再依層次排序。
 * 段落一律完整（不在 [from]／[to] 截斷）。
 *
 * @param order 七星的輪替順序，見 [decennialsOrder]
 * @param birth 出生時刻 —— 第一個主限的起點
 * @param maxLevel 1 = 只有主限；2 = 加子限（以月計）；3 = 再細分（以日計）
 */
fun generateDecennials(
  order: List<Planet>,
  birth: GmtJulDay,
  from: GmtJulDay,
  to: GmtJulDay,
  maxLevel: Int = 2,
  yearLength: ValensYearLength = ValensYearLength.DEFAULT,
): List<Decennial> {
  require(order.size == 7 && order.toSet() == Planet.classicalSet) { "order must be the seven classical planets" }
  require(maxLevel >= 1) { "maxLevel must be at least 1" }
  return majorDecennials(order, birth, yearLength)
    .dropWhile { it.toTime <= from }
    .takeWhile { it.fromTime < to }
    .flatMap { expand(it, order, maxLevel, from, to) }
    .sortedWith(compareBy({ it.fromTime }, { it.level }))
    .toList()
}

/**
 * [gmt] 所在的各層段落（L1..[maxLevel] 各一，外層在前）；區間是半開的 `[fromTime, toTime)`。
 */
fun decennialsAt(
  order: List<Planet>,
  birth: GmtJulDay,
  gmt: GmtJulDay,
  maxLevel: Int = 3,
  yearLength: ValensYearLength = ValensYearLength.DEFAULT,
): List<Decennial> {
  require(gmt >= birth) { "Query time must be at or after birth time." }
  return generateDecennials(order, birth, gmt, gmt + 1e-9, maxLevel, yearLength)
    .filter { it.fromTime <= gmt && gmt < it.toTime }
    .sortedBy { it.level }
}
