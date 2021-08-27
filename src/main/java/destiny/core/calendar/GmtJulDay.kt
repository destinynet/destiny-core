package destiny.core.calendar

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.Serializable

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

fun GmtJulDay.toInstant(): Instant {
  return ((value - Constants.UnixEpoch.JULIAN_DAY) * Constants.SECONDS_OF_DAY).let { secDouble ->
    val second = secDouble.toLong()
    val nano = ((secDouble - second) * 1_000_000_000).toInt()
    Instant.fromEpochSeconds(second, nano)
  }
}

fun Instant.toGmtJulDay(): GmtJulDay {
  val millis = this.toEpochMilliseconds()
  return GmtJulDay((millis + Constants.UnixEpoch.JULIAN_MILLI_SECONDS) / (Constants.SECONDS_OF_DAY * 1000).toDouble())
}
