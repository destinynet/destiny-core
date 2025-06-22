package destiny.core.calendar

import destiny.core.calendar.Constants.SECONDS_OF_DAY
import destiny.tools.serializers.GmtJulDaySerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.chrono.ChronoLocalDateTime
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@JvmInline
@kotlinx.serialization.Serializable(with = GmtJulDaySerializer::class)
value class GmtJulDay(val value: Double) : Comparable<GmtJulDay>, Serializable {

  /**
   * Returns a new GmtJulDay representing the start of the calendar day (00:00:00 GMT)
   * for this GmtJulDay.
   *
   * Julian Day numbers traditionally start at noon (12:00 UT).
   * For a GmtJulDay value `X.Y`:
   * - If `X.Y` corresponds to `2025-03-15T06:46 GMT` (JD approx `2460748.78194`),
   *   this function returns the GmtJulDay for `2025-03-15T00:00:00 GMT` (JD `2460748.5`).
   *
   * The formula `floor(jd + 0.5) - 0.5` converts a Julian Date to the JD of 00:00 UT
   * of that same calendar day.
   */
  fun startOfDay(): GmtJulDay {
    return GmtJulDay(floor(this.value + 0.5) - 0.5)
  }

  /**
   * Returns a new GmtJulDay representing noon (12:00:00 GMT) of the calendar day
   * for this GmtJulDay.
   *
   * For a GmtJulDay value `X.Y`:
   * - If `X.Y` corresponds to `2025-03-15T06:46 GMT` (JD approx `2460748.78194`),
   *   this function returns the GmtJulDay for `2025-03-15T12:00:00 GMT` (JD `2460749.0`).
   * - If `X.Y` corresponds to `2025-03-15T18:00 GMT` (JD approx `2460749.25`),
   *   this function also returns JD `2460749.0`.
   *
   * The formula `floor(jd + 0.5)` effectively gives the Julian Day number for noon
   * of the calendar day.
   */
  fun noonOfDay(): GmtJulDay {
    return GmtJulDay(floor(this.value + 0.5))
  }

  override fun compareTo(other: GmtJulDay): Int {
    return value.compareTo(other.value)
  }

  operator fun minus(amount: Number): GmtJulDay {
    return GmtJulDay(value - amount.toDouble())
  }

  operator fun plus(amount: Number): GmtJulDay {
    return GmtJulDay(value + amount.toDouble())
  }

  operator fun minus(gmtJulDay: GmtJulDay): Double {
    return value - gmtJulDay.value
  }

  companion object {
    fun Number.toGmtJulDay(): GmtJulDay {
      return GmtJulDay(this.toDouble())
    }

    fun now(): GmtJulDay {
      return Clock.System.now().toGmtJulDay()
    }

    /**
     * Returns the GmtJulDay for the upcoming or current noon (12:00 UT).
     * Example:
     * If now is Mar 15, 06:00 UT (JD ~2460748.75), returns JD for Mar 15, 12:00 UT (2460749.0).
     * If now is Mar 15, 14:00 UT (JD ~2460749.08), returns JD for Mar 16, 12:00 UT (2460750.0).
     * This is what `ceil(value)` effectively does for Julian Days.
     */
    fun nowCeilingToNoon(): GmtJulDay {
      return ceil(now().value).toGmtJulDay()
    }

    /**
     * Returns the GmtJulDay for noon (12:00 UT) of the current calendar day.
     */
    fun nowAtNoonOfDay(): GmtJulDay {
      return now().noonOfDay()
    }

    /**
     * Returns the GmtJulDay for the start (00:00 UT) of the current calendar day.
     */
    fun nowAtStartOfDay(): GmtJulDay {
      return now().startOfDay()
    }
  }

}

/** to kotlin [Instant] */
fun GmtJulDay.toInstant(): Instant {
  return ((value - Constants.UnixEpoch.JULIAN_DAY) * SECONDS_OF_DAY).let { secDouble ->
    val second = secDouble.toLong()
    val nano = ((secDouble - second) * 1_000_000_000).toInt()
    Instant.fromEpochSeconds(second, nano)
  }
}

/** from kotlin [Instant] */
fun Instant.toGmtJulDay(): GmtJulDay {
  val millis = this.toEpochMilliseconds()
  return GmtJulDay((millis + Constants.UnixEpoch.JULIAN_MILLI_SECONDS) / (SECONDS_OF_DAY * 1000).toDouble())
}

fun GmtJulDay.toLmt(loc: ILocation, julDayResolver: JulDayResolver): ChronoLocalDateTime<*> {
  return this.toLmt(loc.zoneId, julDayResolver)
}

fun GmtJulDay.toLmt(zoneId: ZoneId, julDayResolver: JulDayResolver): ChronoLocalDateTime<*> {
  return TimeTools.getLmtFromGmt(this, zoneId, julDayResolver)
}

fun GmtJulDay.toDate(zoneId: ZoneId): Date {
  val zdt: ZonedDateTime = this.toInstant().toJavaInstant().atZone(zoneId)
  return Date.from(zdt.toInstant())
}

fun GmtJulDay.absDuration(lmt: LocalDateTime, zoneId: ZoneId): Duration {
  return ((this - TimeTools.getGmtJulDay(lmt, zoneId)).absoluteValue * SECONDS_OF_DAY).toDuration(DurationUnit.SECONDS)
}

fun GmtJulDay.absDuration(lmt: kotlinx.datetime.LocalDateTime, zoneId: ZoneId): Duration {
  return absDuration(lmt.toJavaLocalDateTime(), zoneId)
}

fun GmtJulDay.absDuration(gmt: GmtJulDay): Duration {
  return ((this - gmt).absoluteValue * SECONDS_OF_DAY).toDuration(DurationUnit.SECONDS)
}
