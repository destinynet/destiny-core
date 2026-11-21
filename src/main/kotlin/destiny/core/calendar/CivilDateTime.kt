/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlinx.serialization.Serializable

/**
 * 帶曆法標記的本地日期時間：**欄位＋[CalType] 旗標**。
 *
 * 用在「邊界」—— 輸入（使用者輸入、匯入的原始資料）、儲存與顯示 —— 保留資料原本的曆法標記；
 * 計算一律先換成 [toGregorian]（proleptic 格里曆的 [LocalDateTime]）或儒略日。
 *
 * 為什麼不用 `LocalDateTime` 加一個旗標：`LocalDateTime` 以格里曆驗證日期，
 * 存不下儒略曆的 1700-02-29（儒略曆每 4 年一閏，格里曆的 1700 不閏）；
 * 在儒略曆標記上做加減也會套錯閏年規則。這裡的年月日自己存、依 [calType] 驗證，沒有這兩個問題。
 *
 * 曆法是**資料自帶的**：不能從日期推斷（英國到 1752、俄國到 1918 才改用格里曆；也有早於 1582 卻以格里曆標記的資料）。
 *
 * @property year proleptic 年（西元前 1 年 ＝ 0，西元前 2 年 ＝ −1）
 *
 * 支援範圍：儒略日 ≥ 0，即儒略曆西元前 4713 年 1 月 1 日（proleptic −4712）起。
 */
@Serializable
data class CivilDateTime(
  val calType: CalType,
  val year: Int,
  val month: Int,
  val day: Int,
  val hour: Int = 0,
  val minute: Int = 0,
  val second: Int = 0,
  val nano: Int = 0,
) : Comparable<CivilDateTime> {

  init {
    require(month in 1..12) { "month 超出範圍：$month" }
    require(day in 1..monthLength(calType, year, month)) { "$calType $year-$month 沒有 $day 日" }
    require(hour in 0..23 && minute in 0..59 && second in 0..59 && nano in 0..999_999_999) {
      "時間超出範圍：$hour:$minute:$second.$nano"
    }
    require(julianDayNumber() >= 0) { "早於支援範圍（儒略日 < 0）：$this" }
  }

  /** 這一天（本地）的儒略日數（JDN，整數，當天正午為 .0）。與曆法無關：同一天用兩種曆法標記，JDN 相同 */
  fun julianDayNumber(): Long = toJdn(calType, year, month, day)

  /** 本地時刻的儒略日（含時間；午夜為 .5）。換成 GMT 要再減去時差（見時區接縫） */
  fun localJulianDay(): Double =
    julianDayNumber() - 0.5 + (hour * 3600 + minute * 60 + second + nano / 1e9) / 86400.0

  /** 同一時刻、改以 [target] 曆法標記 */
  fun withCalendar(target: CalType): CivilDateTime =
    if (target == calType) this
    else fromJdn(target, julianDayNumber()).let { (y, m, d) -> copy(calType = target, year = y, month = m, day = d) }

  /** 同一時刻的 proleptic 格里曆 [LocalDateTime]（計算用） */
  fun toGregorian(): LocalDateTime = withCalendar(CalType.GREGORIAN).let {
    LocalDateTime(it.year, it.month, it.day, it.hour, it.minute, it.second, it.nano)
  }

  /** 依時刻比較（不同曆法也能比） */
  override fun compareTo(other: CivilDateTime): Int =
    compareValuesBy(this, other, { it.julianDayNumber() }, { it.hour }, { it.minute }, { it.second }, { it.nano })

  /** 例：`J 1700-02-29 08:30:00`、`G -43-03-13 12:00:00` */
  override fun toString(): String {
    val c = if (calType == CalType.JULIAN) "J" else "G"
    val y = if (year < 0) "-" + (-year).toString().padStart(4, '0') else year.toString().padStart(4, '0')
    fun p2(n: Int) = n.toString().padStart(2, '0')
    val frac = if (nano == 0) "" else "." + nano.toString().padStart(9, '0').trimEnd('0')
    return "$c $y-${p2(month)}-${p2(day)} ${p2(hour)}:${p2(minute)}:${p2(second)}$frac"
  }

  companion object {

    /** 以 [calType] 標記一個 proleptic 格里曆的時刻 */
    fun of(gregorian: LocalDateTime, calType: CalType = CalType.GREGORIAN): CivilDateTime =
      CivilDateTime(CalType.GREGORIAN, gregorian.year, gregorian.month.number, gregorian.day,
                    gregorian.hour, gregorian.minute, gregorian.second, gregorian.nanosecond).withCalendar(calType)

    /** 以 [calType] 標記一個 proleptic 格里曆的日期（午夜） */
    fun of(gregorian: LocalDate, calType: CalType = CalType.GREGORIAN): CivilDateTime =
      of(LocalDateTime(gregorian.year, gregorian.month.number, gregorian.day, 0, 0), calType)

    fun isLeapYear(calType: CalType, year: Int): Boolean = when (calType) {
      CalType.JULIAN    -> year.mod(4) == 0
      CalType.GREGORIAN -> (year.mod(4) == 0 && year.mod(100) != 0) || year.mod(400) == 0
    }

    fun monthLength(calType: CalType, year: Int, month: Int): Int = when (month) {
      2                -> if (isLeapYear(calType, year)) 29 else 28
      4, 6, 9, 11      -> 30
      else             -> 31
    }

    /**
     * 年月日 → 儒略日數（Fliegel & Van Flandern 型式；整數運算、以 floorDiv 支援 proleptic 負年）。
     */
    internal fun toJdn(calType: CalType, year: Int, month: Int, day: Int): Long {
      val a = (14 - month) / 12
      val y = year.toLong() + 4800 - a
      val m = month + 12 * a - 3
      val base = day + (153L * m + 2) / 5 + 365 * y + y.floorDiv(4)
      return when (calType) {
        CalType.GREGORIAN -> base - y.floorDiv(100) + y.floorDiv(400) - 32045
        CalType.JULIAN    -> base - 32083
      }
    }

    /**
     * 儒略日數 → 年月日（Richards 的演算法；JDN ≥ 0 有效）。
     */
    internal fun fromJdn(calType: CalType, jdn: Long): Triple<Int, Int, Int> {
      require(jdn >= 0) { "JDN 必須 ≥ 0：$jdn" }
      val f = when (calType) {
        CalType.JULIAN    -> jdn + 1401
        CalType.GREGORIAN -> jdn + 1401 + (((4 * jdn + 274277) / 146097) * 3) / 4 - 38
      }
      val e = 4 * f + 3
      val g = (e % 1461) / 4
      val h = 5 * g + 2
      val day = (h % 153) / 5 + 1
      val month = ((h / 153 + 2) % 12) + 1
      val year = e / 1461 - 4716 + (12 + 2 - month) / 12
      return Triple(year.toInt(), month.toInt(), day.toInt())
    }
  }
}
