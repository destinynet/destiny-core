/**
 * Created by smallufo on 2017-03-07.
 */
package destiny.core.calendar

import org.threeten.extra.chrono.JulianDate
import java.io.Serializable
import java.time.*
import java.time.chrono.ChronoZonedDateTime
import java.time.temporal.*
import java.time.temporal.ChronoField.*

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
  override fun withEarlierOffsetAtOverlap(): ZonedJulianDateTime {
    // 獲取所有可能的偏移量
    val localDateTime = LocalDateTime.from(dateTime)
    val offsets = zone.rules.getValidOffsets(localDateTime)

    // 若只有一個偏移量或沒有偏移量，則返回當前實例
    if (offsets.size <= 1) {
      return this
    }

    // 返回最小（最早）的偏移量
    val earlierOffset = offsets.minOrNull() ?: return this
    return if (earlierOffset == offset) this else ZonedJulianDateTime(dateTime, earlierOffset, zone)
  }

  override fun withLaterOffsetAtOverlap(): ZonedJulianDateTime {
    // 獲取所有可能的偏移量
    val localDateTime = LocalDateTime.from(dateTime)
    val offsets = zone.rules.getValidOffsets(localDateTime)

    // 若只有一個偏移量或沒有偏移量，則返回當前實例
    if (offsets.size <= 1) {
      return this
    }

    // 返回最大（最晚）的偏移量
    val laterOffset = offsets.maxOrNull() ?: return this
    return if (laterOffset == offset) this else ZonedJulianDateTime(dateTime, laterOffset, zone)
  }

  override fun withZoneSameLocal(zone: ZoneId): ZonedJulianDateTime {
    return if (this.zone == zone) {
      this
    } else {
      // 使用同一本地時間，但新的時區
      val newOffset = if (zone is ZoneOffset) {
        zone
      } else {
        val localDateTime = LocalDateTime.from(dateTime)
        val rules = zone.rules
        rules.getOffset(localDateTime)
      }

      ZonedJulianDateTime(dateTime, newOffset, zone)
    }
  }

  override fun withZoneSameInstant(zone: ZoneId): ZonedJulianDateTime {
    return if (this.zone == zone)
      this
    else {
      val epochSecs = toEpochSecond()
      val nanos = dateTime.nano
      create(epochSecs, nanos, zone)
    }
  }

  override fun with(field: TemporalField, newValue: Long): ZonedJulianDateTime {
    if (field is ChronoField) {
      when (field) {
        // 處理時區相關字段特殊情況
        INSTANT_SECONDS -> return create(newValue, nano, zone)
        OFFSET_SECONDS  -> {
          val newOffset = ZoneOffset.ofTotalSeconds(newValue.toInt())
          return ZonedJulianDateTime(dateTime, newOffset, zone)
        }

        else                        -> {
          // 大多數字段只影響本地日期時間
          val newDateTime = dateTime.with(field, newValue) as JulianDateTime
          return resolveLocal(newDateTime)
        }
      }
    }

    // 自定義字段委派給字段自身處理
    return field.adjustInto(this, newValue) as ZonedJulianDateTime
  }

  override fun plus(amountToAdd: Long, unit: TemporalUnit): ZonedJulianDateTime {
    if (unit is ChronoUnit) {
      return when (unit) {
        // 這些單位可能改變偏移量，需要解析重疊或缺口
        ChronoUnit.DAYS,
        ChronoUnit.WEEKS,
        ChronoUnit.MONTHS,
        ChronoUnit.YEARS,
        ChronoUnit.DECADES,
        ChronoUnit.CENTURIES,
        ChronoUnit.MILLENNIA,
        ChronoUnit.ERAS -> {
          val newDateTime = dateTime.plus(amountToAdd, unit) as JulianDateTime
          resolveLocal(newDateTime)
        }
        // 這些單位保證不會改變日期，所以無需處理重疊或缺口
        else            -> {
          val newDateTime = dateTime.plus(amountToAdd, unit) as JulianDateTime
          ZonedJulianDateTime(newDateTime, offset, zone)
        }
      }
    }

    // 自定義單位委派給單位自身處理
    return unit.addTo(this, amountToAdd) as ZonedJulianDateTime
  }


  override fun until(endExclusive: Temporal, unit: TemporalUnit): Long {
    var end = from(endExclusive)
    if (unit is ChronoUnit) {
      end = end.withZoneSameInstant(zone)
      return if (unit.isDateBased()) {
        dateTime.until(end.dateTime, unit)
      } else {
        toOffsetDateTime().until(end.toOffsetDateTime(), unit)
      }
    }
    return unit.between(this, end)
  }

  private fun toOffsetDateTime(): OffsetDateTime {
    // 將JulianDateTime轉換為OffsetDateTime
    val localDate = LocalDate.from(dateTime)
    val localTime = dateTime.toLocalTime()
    return OffsetDateTime.of(LocalDateTime.of(localDate, localTime), offset)
  }

  override fun isSupported(field: TemporalField): Boolean {
    return if (field is ChronoField) {
      // 支持所有ChronoField
      true
    } else {
      // 對於自定義字段，委派給字段自身判斷
      field.isSupportedBy(this)
    }
  }

  /**
   * 解析本地時間，處理時區重疊和缺口
   */
  private fun resolveLocal(newDateTime: JulianDateTime): ZonedJulianDateTime {
    val localDateTime = LocalDateTime.from(newDateTime)
    val rules = zone.rules
    val validOffsets = rules.getValidOffsets(localDateTime)

    if (validOffsets.contains(offset)) {
      // 如果當前偏移量仍然有效，保持不變
      return ZonedJulianDateTime(newDateTime, offset, zone)
    } else if (validOffsets.size > 0) {
      // 選擇第一個有效偏移量
      return ZonedJulianDateTime(newDateTime, validOffsets[0], zone)
    } else {
      // 處理缺口：使用轉換生成的偏移量
      val trans = rules.getTransition(localDateTime)
      val offsetBefore = trans.offsetBefore
      val offsetAfter = trans.offsetAfter
      val newOffset = if (offsetAfter.totalSeconds > offsetBefore.totalSeconds) {
        // 向後調整
        offsetAfter
      } else {
        // 向前調整
        offsetBefore
      }
      return ZonedJulianDateTime(newDateTime, newOffset, zone)
    }
  }

  override fun toEpochSecond(): Long {
    val epochDay = LocalDate.from(dateTime).toEpochDay()
    val secondsOfDay = dateTime.toLocalTime().toSecondOfDay()
    return epochDay * 86400 + secondsOfDay - offset.totalSeconds
  }

  override fun toString(): String {
    return dateTime.toString() + offset.toString() + "[" + zone.toString() + "]"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is ZonedJulianDateTime) return false

    if (dateTime != other.dateTime) return false
    if (offset != other.offset) return false
    return zone == other.zone
  }

  override fun hashCode(): Int {
    var result = dateTime.hashCode()
    result = 31 * result + offset.hashCode()
    result = 31 * result + zone.hashCode()
    return result
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
        Instant.ofEpochSecond(epochSecond, nanoOfSecond.toLong())
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
