/**
 * Created by smallufo on 2017-01-20.
 */
package destiny.core.calendar

import org.threeten.extra.chrono.JulianDate
import java.io.Serializable
import java.time.*
import java.time.chrono.ChronoLocalDateTime
import java.time.temporal.*
import java.time.temporal.ChronoField.*
import java.util.*

/**
 * reference : [java.time.LocalDateTime]
 */
class JulianDateTime private constructor(private val date: JulianDate, private val time: LocalTime) : Serializable,
  ChronoLocalDateTime<JulianDate> {

  /** 一定為正值  */
  val yearOfEra: Int = date.get(YEAR_OF_ERA)

  /** 與 [LocalDate.year] 一樣，都為 proleptic year . 西元元年=1 , 西元前一年=0  */
  val year: Int = date.get(YEAR)

  val month: Int = date.get(MONTH_OF_YEAR)

  val dayOfMonth: Int = date.get(DAY_OF_MONTH)

  val hour: Int  = time.hour

  val minute: Int = time.minute

  val second: Int = time.second

  val nano: Int = time.nano

  override fun toLocalDate(): JulianDate {
    return date
  }

  override fun toLocalTime(): LocalTime {
    return time
  }

  /**
   * see [java.time.LocalDateTime.isSupported]
   */
  override fun isSupported(field: TemporalField?): Boolean {
    if (field is ChronoField) {
      val f = field as ChronoField?
      return f!!.isDateBased || f.isTimeBased
    }
    return field != null && field.isSupportedBy(this)
  }

  /**
   * see [java.time.LocalDateTime.getLong]
   */
  override fun getLong(field: TemporalField): Long {
    return if (field is ChronoField) {
      if (field.isTimeBased) time.getLong(field) else date.getLong(field)
    } else field.getFrom(this)
  }

  /**
   * see [LocalDateTime.with]
   */
  override fun with(field: TemporalField, newValue: Long): ChronoLocalDateTime<JulianDate> {
    return if (field is ChronoField) {
      if (field.isTimeBased) {
        with(date, time.with(field, newValue))
      } else {
        with(date.with(field, newValue), time)
      }
    } else field.adjustInto(this, newValue)
  }

  /**
   * see [LocalDateTime.with]
   */
  private fun with(newDate: JulianDate, newTime: LocalTime): JulianDateTime {
    return if (date === newDate && time === newTime) {
      this
    } else JulianDateTime(newDate, newTime)
  }

  /**
   * see [LocalDateTime.plus]
   */
  override fun plus(amountToAdd: Long, unit: TemporalUnit): ChronoLocalDateTime<JulianDate> {
    if (unit is ChronoUnit) {
      when (unit) {
        ChronoUnit.NANOS -> return plusNanos(amountToAdd)
        ChronoUnit.MICROS -> return plusDays(amountToAdd / MICROS_PER_DAY).plusNanos(
          amountToAdd % MICROS_PER_DAY * 1000)
        ChronoUnit.MILLIS -> return plusDays(amountToAdd / MILLIS_PER_DAY).plusNanos(
          amountToAdd % MILLIS_PER_DAY * 1000000)
        ChronoUnit.SECONDS -> return plusSeconds(amountToAdd)
        ChronoUnit.MINUTES -> return plusMinutes(amountToAdd)
        ChronoUnit.HOURS -> return plusHours(amountToAdd)
        ChronoUnit.HALF_DAYS -> return plusDays(amountToAdd / 256).plusHours(
          amountToAdd % 256 * 12)  // no overflow (256 is multiple of 2)
      }
      return with(date.plus(amountToAdd, unit), time)
    }
    return unit.addTo(this, amountToAdd)
  }

  override fun minus(amountToSubtract: Long, unit: TemporalUnit): ChronoLocalDateTime<JulianDate> {
    return plus(-amountToSubtract, unit)
  }

  fun plusNanos(nanos: Long): JulianDateTime {
    return plusWithOverflow(date, 0, 0, 0, nanos, 1)
  }

  fun plusSeconds(seconds: Long): JulianDateTime {
    return plusWithOverflow(date, 0, 0, seconds, 0, 1)
  }

  fun plusMinutes(minutes: Long): JulianDateTime {
    return plusWithOverflow(date, 0, minutes, 0, 0, 1)
  }

  fun plusHours(hours: Long): JulianDateTime {
    return plusWithOverflow(date, hours, 0, 0, 0, 1)
  }

  fun plusDays(days: Long): JulianDateTime {
    val newDate = date.plus(days, ChronoUnit.DAYS)
    return with(newDate, time)
  }

  fun plusMonths(months: Long) : JulianDateTime {
    val newDate = date.plus(months, ChronoUnit.MONTHS)
    return with(newDate , time)
  }

  /**
   * see [LocalDateTime.plusWithOverflow]
   */
  private fun plusWithOverflow(newDate: JulianDate,
                               hours: Long,
                               minutes: Long,
                               seconds: Long,
                               nanos: Long,
                               sign: Int): JulianDateTime {
    // 9223372036854775808 long, 2147483648 int
    if (hours or minutes or seconds or nanos == 0L) {
      return with(newDate, time)
    }
    var totDays = nanos / NANOS_PER_DAY +             //   max/24*60*60*1B

      seconds / SECONDS_PER_DAY +                     //   max/24*60*60

      minutes / MINUTES_PER_DAY +                     //   max/24*60

      hours / HOURS_PER_DAY                           //   max/24
    totDays *= sign.toLong()                          // total max*0.4237...
    var totNanos = nanos % NANOS_PER_DAY +            //   max  86400000000000

      seconds % SECONDS_PER_DAY * NANOS_PER_SECOND +  //   max  86400000000000

      minutes % MINUTES_PER_DAY * NANOS_PER_MINUTE +  //   max  86400000000000

      hours % HOURS_PER_DAY * NANOS_PER_HOUR          //   max  86400000000000
    val curNoD = time.toNanoOfDay()                   //   max  86400000000000
    totNanos = totNanos * sign + curNoD               // total 432000000000000
    totDays += Math.floorDiv(totNanos, NANOS_PER_DAY)
    val newNoD = Math.floorMod(totNanos, NANOS_PER_DAY)
    val newTime = if (newNoD == curNoD) time else LocalTime.ofNanoOfDay(newNoD)
    return with(newDate.plus(totDays, ChronoUnit.DAYS), newTime)
  }


  /**
   * see [LocalDateTime.until]
   */
  override fun until(endExclusive: Temporal, unit: TemporalUnit): Long {
    val end = from(endExclusive)
    if (unit is ChronoUnit) {
      if (unit.isTimeBased()) {
        // long amount = date.daysUntil(end.date);
        var amount = date.until(end.date).get(ChronoUnit.DAYS)
        if (amount == 0L) {
          return time.until(end.time, unit)
        }
        var timePart = end.time.toNanoOfDay() - time.toNanoOfDay()
        if (amount > 0) {
          amount--  // safe
          timePart += NANOS_PER_DAY  // safe
        } else {
          amount++  // safe
          timePart -= NANOS_PER_DAY  // safe
        }
        when (unit) {
          ChronoUnit.NANOS -> amount = Math.multiplyExact(amount, NANOS_PER_DAY)
          ChronoUnit.MICROS -> {
            amount = Math.multiplyExact(amount, MICROS_PER_DAY)
            timePart /= 1000
          }
          ChronoUnit.MILLIS -> {
            amount = Math.multiplyExact(amount, MILLIS_PER_DAY)
            timePart /= 1000000
          }
          ChronoUnit.SECONDS -> {
            amount = Math.multiplyExact(amount, SECONDS_PER_DAY.toLong())
            timePart /= NANOS_PER_SECOND
          }
          ChronoUnit.MINUTES -> {
            amount = Math.multiplyExact(amount, MINUTES_PER_DAY.toLong())
            timePart /= NANOS_PER_MINUTE
          }
          ChronoUnit.HOURS -> {
            amount = Math.multiplyExact(amount, HOURS_PER_DAY.toLong())
            timePart /= NANOS_PER_HOUR
          }
          ChronoUnit.HALF_DAYS -> {
            amount = Math.multiplyExact(amount, 2)
            timePart /= (NANOS_PER_HOUR * 12)
          }
        }
        return Math.addExact(amount, timePart)
      }
      var endDate = end.date
      if (endDate.isAfter(date) && end.time.isBefore(time)) {
        // endDate = endDate.minusDays(1);
        endDate = endDate.minus(1, ChronoUnit.DAYS)
      } else if (endDate.isBefore(date) && end.time.isAfter(time)) {
        // endDate = endDate.plusDays(1);
        endDate = endDate.plus(1, ChronoUnit.DAYS)
      }
      return date.until(endDate, unit)
    }
    return unit.between(this, end)
  }

  override fun atZone(zone: ZoneId): ZonedJulianDateTime {
    return ZonedJulianDateTime.of(this, zone)
  }




  override fun toString(): String {
    return "[JulianDateTime date=$date, time=$time]"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is JulianDateTime) return false

    if (date != other.date) return false
    if (time != other.time) return false

    return true
  }

  override fun hashCode(): Int {
    var result = date.hashCode()
    result = 31 * result + time.hashCode()
    return result
  }

  companion object {

    /**
     * Hours per day.
     */
    private const val HOURS_PER_DAY = 24

    /**
     * Minutes per hour.
     */
    private const val MINUTES_PER_HOUR = 60

    /**
     * Minutes per day.
     */
    private const val MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY

    /**
     * Seconds per minute.
     */
    private const val SECONDS_PER_MINUTE = 60

    /**
     * Seconds per hour.
     */
    private const val SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR

    /**
     * Seconds per day.
     */
    private const val SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY

    /**
     * Milliseconds per day.
     */
    private const val MILLIS_PER_DAY = SECONDS_PER_DAY * 1000L

    /**
     * Microseconds per day.
     */
    private const val MICROS_PER_DAY = SECONDS_PER_DAY * 1000_000L

    /**
     * Nanos per second.
     */
    private const val NANOS_PER_SECOND = 1000_000_000L

    /**
     * Nanos per minute.
     */
    private const val NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE

    /**
     * Nanos per hour.
     */
    private const val NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR

    /**
     * Nanos per day.
     */
    private const val NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY

    fun from(temporal: TemporalAccessor): JulianDateTime {
      if (temporal is JulianDateTime) {
        return temporal
      } else if (temporal is ZonedJulianDateTime) {
        return temporal.toLocalDateTime()
      }
      //    else if (temporal instanceof OffsetDateTime) {
      //      return ((OffsetDateTime) temporal).toLocalDateTime();
      //    }
      try {
        val date = JulianDate.from(temporal)
        val time = LocalTime.from(temporal)
        return JulianDateTime(date, time)
      } catch (ex: DateTimeException) {
        throw DateTimeException(
          "Unable to obtain JulianDateTime from TemporalAccessor: " + temporal + " of type " + temporal.javaClass.name,
          ex)
      }

    }

    fun of(date: JulianDate, time: LocalTime): JulianDateTime {
      Objects.requireNonNull(date, "date")
      Objects.requireNonNull(time, "time")
      return JulianDateTime(date, time)
    }

    /**
     * @param prolepticYear maybe <= 0
     */
    fun of(prolepticYear: Int,
           month: Int,
           dayOfMonth: Int,
           hour: Int,
           minute: Int,
           second: Int,
           nano: Int): JulianDateTime {
      val date = JulianDate.of(prolepticYear, month, dayOfMonth)
      val time = LocalTime.of(hour, minute, second, nano)
      return JulianDateTime(date, time)
    }

    /**
     * @param prolepticYear maybe <= 0
     */
    //@JvmOverloads
    fun of(prolepticYear: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int, second: Int = 0): JulianDateTime {
      val date = JulianDate.of(prolepticYear, month, dayOfMonth)

      val (first, second1) = TimeTools.splitSecond(second.toDouble())
      val time = LocalTime.of(hour, minute, first, second1)
      return JulianDateTime(date, time)
    }

    /**
     * @param prolepticYear maybe <= 0
     */
    fun of(prolepticYear: Int, month: Month, dayOfMonth: Int, hour: Int, minute: Int): JulianDateTime {
      return of(prolepticYear, month.value, dayOfMonth, hour, minute, 0)
    }

    fun ofEpochSecond(epochSecond: Long, nanoOfSecond: Int, offset: ZoneOffset): JulianDateTime {
      Objects.requireNonNull(offset, "offset")
      NANO_OF_SECOND.checkValidValue(nanoOfSecond.toLong())
      val localSecond = epochSecond + offset.totalSeconds  // overflow caught later
      val localEpochDay = Math.floorDiv(localSecond, SECONDS_PER_DAY.toLong())
      val secsOfDay = Math.floorMod(localSecond, SECONDS_PER_DAY.toLong()).toInt()
      val gDate = LocalDate.ofEpochDay(localEpochDay)
      val date = JulianDate.from(gDate)
      val time = LocalTime.ofNanoOfDay(secsOfDay * NANOS_PER_SECOND + nanoOfSecond)
      return JulianDateTime(date, time)
    }
  }
}
