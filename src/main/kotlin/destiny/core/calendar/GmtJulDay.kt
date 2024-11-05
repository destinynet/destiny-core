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
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@JvmInline
@kotlinx.serialization.Serializable(with = GmtJulDaySerializer::class)
value class GmtJulDay(val value: Double) : Comparable<GmtJulDay>, Serializable {

  companion object {
    fun Number.toGmtJulDay(): GmtJulDay {
      return GmtJulDay(this.toDouble())
    }

    fun now() : GmtJulDay {
      return Clock.System.now().toGmtJulDay()
    }

    fun nowCeiling() : GmtJulDay {
      return ceil(now().value).toGmtJulDay()
    }
  }

  override fun compareTo(other: GmtJulDay): Int {
    return if (value == other.value)
      0
    else if (value - other.value < 0) -1 else 1
  }

  operator fun minus(amount: Number): GmtJulDay {
    return (value - amount.toDouble()).toGmtJulDay()
  }

  operator fun plus(amount: Number): GmtJulDay {
    return (value + amount.toDouble()).toGmtJulDay()
  }

  operator fun minus(gmtJulDay: GmtJulDay): Double {
    return value - gmtJulDay.value
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
  return TimeTools.getLmtFromGmt(this, loc, julDayResolver)
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
