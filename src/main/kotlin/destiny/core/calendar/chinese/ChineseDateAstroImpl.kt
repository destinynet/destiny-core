/**
 * Created by smallufo on 2026-10-29.
 */
package destiny.core.calendar.chinese

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import destiny.core.astrology.LunarPhase
import destiny.core.calendar.CalType
import destiny.core.calendar.ConversionException
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.JulDayResolver
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools.toGmtJulDay
import destiny.core.calendar.event.ILunations
import destiny.core.chinese.StemBranch
import destiny.tools.Lang
import org.threeten.extra.chrono.JulianDate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.chrono.ChronoLocalDate
import java.time.temporal.ChronoUnit.DAYS
import kotlin.math.abs

/**
 * 以天文計算（朔、中氣的瞬間）建表的陰陽曆。
 *
 * 規則同 GB/T 33661-2017（1645 年時憲曆以來的定朔定氣法）：
 * - 歲 = 冬至到冬至；冬至所在的月為十一月。
 * - 歲內若有 13 個朔（12 個完整月），第一個沒有中氣的月為閏月，沿用前一個月的月名。
 *
 * 「某個瞬間屬於哪個月」只看一條鍵軸：[MonthBoundary.DAY] 時是日（由 [DayOf] 決定瞬間屬於哪一天），
 * [MonthBoundary.INSTANT] 時是瞬間本身。朔與中氣的瞬間來自 [lunations] 與 [solarTerms]，與地點無關；
 * 以歲為單位建表後快取，查詢只是查表。
 *
 * [IChineseDate] 的介面（以日為單位）一律走 [civil] 的民曆：[CivilStandard.CHINA] 即紫金山天文台的做法。
 * 1645 年以前的歷史曆法（平氣平朔）不在此範圍，對那之前的日期得到的是「若當時用定氣定朔會怎樣」。
 */
class ChineseDateAstroImpl(
  private val lunations: ILunations,
  private val solarTerms: ISolarTerms,
  override val julDayResolver: JulDayResolver,
  val civil: CivilStandard = CivilStandard.CHINA,
) : ILunisolarCalendar {

  private enum class Kind { DAY, INSTANT }

  /** 一張歲表的鍵：鍵軸種類、日界、起點冬至所在的西元年 */
  private data class TableKey(val kind: Kind, val dayOf: DayOf, val k: Int)

  /** 一個月在鍵軸上的區間 [keyBegin, keyEnd) */
  private class Span(val month: LunarMonth, val keyBegin: Double, val keyEnd: Double) {
    operator fun contains(key: Double): Boolean = key >= keyBegin && key < keyEnd
  }

  private val cache: Cache<TableKey, List<Span>> = Caffeine.newBuilder().maximumSize(4096).build()

  private val civilDayOf = CivilDayOf(civil)

  private fun solstice(gYear: Int): GmtJulDay {
    val from = LocalDateTime.of(gYear, 12, 1, 0, 0).toGmtJulDay(ZoneOffset.UTC)
    return solarTerms.getSolarTermsTime(SolarTerms.冬至, from, true)
  }

  private fun sui(key: TableKey): List<Span> = cache.get(key) { buildSui(it) }!!

  private fun buildSui(key: TableKey): List<Span> {
    val dayOf = key.dayOf
    val keyOf: (GmtJulDay) -> Double = when (key.kind) {
      Kind.DAY -> { gmt -> dayOf.dayOf(gmt).toEpochDay().toDouble() }
      Kind.INSTANT -> { gmt -> gmt.value }
    }
    val k = key.k
    val s1 = solstice(k)
    val s2 = solstice(k + 1)
    val k1 = keyOf(s1)
    val k2 = keyOf(s2)

    val newMoons = lunations.lunations(s1 - 40, s2 + 40, setOf(LunarPhase.NEW)).map { it.anchor }
    val keys = newMoons.map(keyOf)
    // 冬至所在月（十一月）的 index：最後一個朔 <= 冬至
    val a = keys.indexOfLast { it <= k1 }
    val b = keys.indexOfLast { it <= k2 }
    val count = b - a
    require(count in 12..13) { "歲 $k 有 $count 個月" }

    val zhongqi = solarTerms.getSolarTermsEvents(s1 - 1, s2 + 1)
      .filter { !it.solarTerms.major }
      .map { keyOf(it.begin) }
    fun hasZhongqi(i: Int) = zhongqi.any { it >= keys[i] && it < keys[i + 1] }

    val leapIndex: Int? = if (count == 13) (a + 1 until b).firstOrNull { !hasZhongqi(it) } else null

    var number = 11
    return (a until b).map { i ->
      val leap = i == leapIndex
      if (i != a && !leap) number = number % 12 + 1
      val nextDay = dayOf.dayOf(newMoons[i + 1])
      val month = LunarMonth(
        year = if (number >= 11) k else k + 1,
        number = number,
        leap = leap,
        newMoon = newMoons[i],
        nextNewMoon = newMoons[i + 1],
        firstDay = dayOf.dayOf(newMoons[i]),
        lastDay = if (key.kind == Kind.DAY) nextDay.minusDays(1) else nextDay,
      )
      Span(month, keys[i], keys[i + 1])
    }
  }

  /**
   * 鍵值 [key] 落在哪個月。西元 y 年的日期多半在「y-1 年冬至起」的歲裡，
   * 只有該年十一月初一（可能早於冬至日）之後才落到下一個歲，所以從 [k0] = y-1 起、往後再往前各試一次。
   */
  private fun spanAt(kind: Kind, dayOf: DayOf, key: Double, k0: Int): Span {
    for (k in intArrayOf(k0, k0 + 1, k0 - 1)) {
      sui(TableKey(kind, dayOf, k)).firstOrNull { key in it }?.let { return it }
    }
    throw ConversionException("找不到 key=$key 所在的月（$kind, k0=$k0）")
  }

  private fun daySpanAt(dayOf: DayOf, date: LocalDate): Span =
    spanAt(Kind.DAY, dayOf, date.toEpochDay().toDouble(), date.year - 1)

  /** 以同一個朔對應回民曆的月名 */
  private fun civilMonthOfNewMoon(newMoon: GmtJulDay, civilDayOf: CivilDayOf): LunarMonth {
    val k0 = civilDayOf.dayOf(newMoon).year - 1
    for (k in intArrayOf(k0, k0 + 1, k0 - 1)) {
      sui(TableKey(Kind.DAY, civilDayOf, k)).firstOrNull { abs(it.month.newMoon.value - newMoon.value) < NEW_MOON_TOLERANCE }?.let { return it.month }
    }
    throw ConversionException("民曆歲表裡找不到朔 $newMoon 的月")
  }

  private fun LunarMonth.toChineseDate(day: Int): ChineseDate =
    ChineseDate(IChineseDate.getCycleOfYear(year), StemBranch[(year - 1984).mod(60)], number, leap, day)

  // =============== ILunisolarCalendar ===============

  override fun lunarDate(gmt: GmtJulDay, lunisolar: LunisolarConfig, dayOf: DayOf): ChineseDate {
    val day = dayOf.dayOf(gmt)
    val civilDayOf = CivilDayOf(lunisolar.civil)
    return when (lunisolar.monthBoundary) {
      MonthBoundary.DAY -> when (lunisolar.monthTable) {
        // 民曆的月表：這一天（依日界歸出來的日期）在萬年曆上是幾月幾日
        MonthTable.CIVIL -> daySpanAt(civilDayOf, day).month.let { it.toChineseDate(DAYS.between(it.firstDay, day).toInt() + 1) }
        // 初一依本日界（含合朔那一天），月名以同一個朔對回萬年曆
        MonthTable.CIVIL_NAMES -> daySpanAt(dayOf, day).month.let { m ->
          civilMonthOfNewMoon(m.newMoon, civilDayOf).toChineseDate(DAYS.between(m.firstDay, day).toInt() + 1)
        }
        MonthTable.DERIVED -> daySpanAt(dayOf, day).month.let { it.toChineseDate(DAYS.between(it.firstDay, day).toInt() + 1) }
      }

      MonthBoundary.INSTANT -> {
        val span = spanAt(Kind.INSTANT, dayOf, gmt.value, day.year - 1)
        val named = if (lunisolar.monthTable == MonthTable.DERIVED) span.month else civilMonthOfNewMoon(span.month.newMoon, civilDayOf)
        named.toChineseDate(DAYS.between(span.month.firstDay, day).toInt() + 1)
      }
    }
  }

  override fun monthsOf(year: Int, lunisolar: LunisolarConfig, dayOf: DayOf): List<LunarMonth> {
    val civilDayOf = CivilDayOf(lunisolar.civil)
    fun LunarMonth.civilNamed(): LunarMonth = civilMonthOfNewMoon(newMoon, civilDayOf).let { copy(year = it.year, number = it.number, leap = it.leap) }
    return when (lunisolar.monthBoundary) {
      MonthBoundary.DAY -> {
        val tableDayOf: DayOf = if (lunisolar.monthTable == MonthTable.CIVIL) civilDayOf else dayOf
        (sui(TableKey(Kind.DAY, tableDayOf, year - 1)) + sui(TableKey(Kind.DAY, tableDayOf, year)))
          .map { if (lunisolar.monthTable == MonthTable.CIVIL_NAMES) it.month.civilNamed() else it.month }
          .filter { it.year == year }
      }

      MonthBoundary.INSTANT -> {
        (sui(TableKey(Kind.INSTANT, dayOf, year - 1)) + sui(TableKey(Kind.INSTANT, dayOf, year)))
          .map { if (lunisolar.monthTable == MonthTable.DERIVED) it.month else it.month.civilNamed() }
          .filter { it.year == year }
      }
    }
  }

  // =============== 民曆（IChineseDate） ===============

  /** 此日期所在的民曆月 */
  fun monthOf(date: LocalDate): LunarMonth = daySpanAt(civilDayOf, date).month

  /** 該民曆月；不存在（例如該年沒有這個閏月）則回傳 null */
  fun monthOrNull(year: Int, month: Int, leap: Boolean): LunarMonth? {
    val k = if (month >= 11) year else year - 1
    return sui(TableKey(Kind.DAY, civilDayOf, k)).map { it.month }.firstOrNull { it.year == year && it.number == month && it.leap == leap }
  }

  override fun getChineseDate(calType: CalType, year: Int, month: Int, day: Int): ChineseDate {
    val date: LocalDate = when (calType) {
      CalType.GREGORIAN -> LocalDate.of(year, month, day)
      CalType.JULIAN -> LocalDate.from(JulianDate.of(year, month, day))
    }
    return getChineseDate(date)
  }

  override fun getChineseDate(localDate: LocalDate): ChineseDate {
    val month = monthOf(localDate)
    return month.toChineseDate(DAYS.between(month.firstDay, localDate).toInt() + 1)
  }

  override fun getYangDate(cycle: Int, year: StemBranch, leap: Boolean, month: Int, day: Int): ChronoLocalDate {
    val westYear = ChineseDate.westYearOf(cycle, year)
    val lunarMonth = monthOrNull(westYear, month, leap)
      ?: throw ConversionException("$westYear 年（cycle=$cycle $year）沒有${if (leap) "閏" else ""}${month}月")
    // day 超過月底時不擋（例如十二月三十一），順延到下個月，與既有行為相同
    val date = lunarMonth.firstDay.plusDays(day - 1L)
    return if (date < GREGORIAN_START) JulianDate.from(date) else date
  }

  override fun plusDays(chineseDate: ChineseDate, days: Int): ChineseDate {
    val date = LocalDate.from(getYangDate(chineseDate)).plusDays(days.toLong())
    return getChineseDate(date)
  }

  override fun nextMonthStart(chineseDate: ChineseDate): ChineseDate {
    val first = LocalDate.from(getYangDate(chineseDate.cycleOrZero, chineseDate.year, chineseDate.leapMonth, chineseDate.month, 1))
    return getChineseDate(monthOf(first).lastDay.plusDays(1))
  }

  override fun prevMonthStart(chineseDate: ChineseDate): ChineseDate {
    val first = LocalDate.from(getYangDate(chineseDate.cycleOrZero, chineseDate.year, chineseDate.leapMonth, chineseDate.month, 1))
    return getChineseDate(monthOf(first.minusDays(1)).firstDay)
  }

  override fun getTitle(lang: Lang): String = "民用"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ChineseDateAstroImpl) return false
    return civil == other.civil
  }

  override fun hashCode(): Int = civil.hashCode()

  companion object {
    private val GREGORIAN_START: LocalDate = LocalDate.of(1582, 10, 15)

    /** 兩張歲表各自求出的同一個朔，時刻可能差到數值誤差；小於 0.1 秒視為同一個 */
    private const val NEW_MOON_TOLERANCE = 1.0 / 86400 / 10
  }
}
