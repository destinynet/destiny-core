/**
 * Created by smallufo on 2017-03-07.
 */
package destiny.core.calendar

import org.threeten.extra.chrono.JulianDate
import java.io.Serializable
import java.time.*
import java.time.chrono.ChronoZonedDateTime
import java.time.temporal.*
import java.time.temporal.ChronoField.INSTANT_SECONDS
import java.time.temporal.ChronoField.NANO_OF_SECOND
import java.util.*

/**
 * see [java.time.ZonedDateTime]
 */
class ZonedJulianDateTime private constructor(
  /**
   * The local date-time.
   */
  private val dateTime: JulianDateTime,
  /**
   * The offset from UTC/Greenwich.
   */
  private val offset: ZoneOffset,
  /**
   * The time-zone.
   */
  private val zone: ZoneId) : Temporal, ChronoZonedDateTime<JulianDate>, Serializable {

  /**
   * Gets the hour-of-day field.
   *
   * @return the hour-of-day, from 0 to 23
   */
  val hour: Int
    get() = dateTime.hour

  /**
   * Gets the minute-of-hour field.
   *
   * @return the minute-of-hour, from 0 to 59
   */
  val minute: Int
    get() = dateTime.minute

  /**
   * Gets the second-of-minute field.
   *
   * @return the second-of-minute, from 0 to 59
   */
  val second: Int
    get() = dateTime.second

  /**
   * Gets the nano-of-second field.
   *
   * @return the nano-of-second, from 0 to 999,999,999
   */
  val nano: Int
    get() = dateTime.nano


  override fun toLocalDateTime(): JulianDateTime {
    return dateTime
  }

  override fun getOffset(): ZoneOffset {
    return offset
  }

  override fun getZone(): ZoneId {
    return zone
  }

  /**
   * see [ZonedDateTime.withEarlierOffsetAtOverlap]
   */
  override fun withEarlierOffsetAtOverlap(): ZonedJulianDateTime? {
    return null
  }

  override fun withLaterOffsetAtOverlap(): ZonedJulianDateTime? {
    return null
  }

  override fun withZoneSameLocal(zone: ZoneId): ZonedJulianDateTime? {
    return null
  }

  override fun withZoneSameInstant(zone: ZoneId): ZonedJulianDateTime {
    Objects.requireNonNull(zone, "zone")
    return if (this.zone == zone)
      this
    else {
      val epochSecs = dateTime.toEpochSecond(offset)
      val nanos = dateTime.nano
      create(epochSecs, nanos, zone)
    }
  }

  override fun with(field: TemporalField, newValue: Long): ZonedJulianDateTime? {
    return null
  }

  override fun plus(amountToAdd: Long, unit: TemporalUnit): ZonedJulianDateTime? {
    return null
  }


  override fun until(endExclusive: Temporal, unit: TemporalUnit): Long {
    var end = from(endExclusive)
    if (unit is ChronoUnit) {
      end = end.withZoneSameInstant(zone)
      return if (unit.isDateBased()) {
        dateTime.until(end.dateTime, unit)
      } else {
        toOffsetDateTime()!!.until(end.toOffsetDateTime()!!, unit)
      }
    }
    return unit.between(this, end)
  }

  private fun toOffsetDateTime(): OffsetDateTime? {
    //     return OffsetDateTime.of(dateTime, offset);
    TODO()
  }

  override fun isSupported(field: TemporalField): Boolean {
    return false
  }

  companion object {

    private fun of(date: JulianDate, time: LocalTime, zone: ZoneId): ZonedJulianDateTime {
      return of(JulianDateTime.of(date, time), zone)
    }

    fun of(julianDateTime: JulianDateTime, zone: ZoneId): ZonedJulianDateTime {
      return ofLocal(julianDateTime, zone, null)
    }

    /**
     * 嘗試作法：
     * 先把 JulianDate 轉成 LocalDate
     * 產生 LocalDateTime -> ZonedDateTime , 取得 Offset
     */
    private fun ofLocal(julianDateTime: JulianDateTime,
                        zone: ZoneId,
                        preferredOffset: ZoneOffset?): ZonedJulianDateTime {
      Objects.requireNonNull(julianDateTime, "julianDateTime")
      Objects.requireNonNull(zone, "zone")
      if (zone is ZoneOffset) {
        return ZonedJulianDateTime(julianDateTime, zone, zone)
      }

      val localDate = LocalDate.from(julianDateTime)
      val ldt = LocalDateTime.of(localDate, julianDateTime.toLocalTime())
      val zdt = ZonedDateTime.ofLocal(ldt, zone, preferredOffset)
      val offset = zdt.offset

      return ZonedJulianDateTime(julianDateTime, offset, zone)
    }

    private fun create(epochSecond: Long, nanoOfSecond: Int, zone: ZoneId): ZonedJulianDateTime {
      val rules = zone.rules
      val instant =
        Instant.ofEpochSecond(epochSecond, nanoOfSecond.toLong())  // TODO: rules should be queryable by epochSeconds
      val offset = rules.getOffset(instant)
      val jdt = JulianDateTime.ofEpochSecond(epochSecond, nanoOfSecond, offset)
      return ZonedJulianDateTime(jdt, offset, zone)
    }


    private fun from(temporal: TemporalAccessor): ZonedJulianDateTime {
      if (temporal is ZonedJulianDateTime) {
        return temporal
      }
      try {
        val zone = ZoneId.from(temporal)
        return if (temporal.isSupported(INSTANT_SECONDS)) {
          val epochSecond = temporal.getLong(INSTANT_SECONDS)
          val nanoOfSecond = temporal.get(NANO_OF_SECOND)
          create(epochSecond, nanoOfSecond, zone)
        } else {
          val date = JulianDate.from(temporal)
          val time = LocalTime.from(temporal)
          of(date, time, zone)
        }
      } catch (ex: DateTimeException) {
        throw DateTimeException(
          "Unable to obtain ZonedDateTime from TemporalAccessor: " + temporal + " of type " + temporal.javaClass.name,
          ex)
      }

    }
  }


}
