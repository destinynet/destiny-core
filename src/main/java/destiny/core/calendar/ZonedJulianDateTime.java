/**
 * Created by smallufo on 2017-03-07.
 */
package destiny.core.calendar;

import org.threeten.extra.chrono.JulianDate;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.*;
import java.time.zone.ZoneRules;
import java.util.Objects;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;

/**
 * see {@link java.time.ZonedDateTime}
 */
public class ZonedJulianDateTime implements Temporal, ChronoZonedDateTime<JulianDate>, Serializable {

  /**
   * The local date-time.
   */
  private final JulianDateTime dateTime;

  /**
   * The offset from UTC/Greenwich.
   */
  private final ZoneOffset offset;

  /**
   * The time-zone.
   */
  private final ZoneId zone;

  private ZonedJulianDateTime(JulianDateTime dateTime, ZoneOffset offset, ZoneId zone) {
    this.dateTime = dateTime;
    this.offset = offset;
    this.zone = zone;
  }

  private static ZonedJulianDateTime of(JulianDate date, LocalTime time, ZoneId zone) {
        return of(JulianDateTime.of(date, time), zone);
    }

  public static ZonedJulianDateTime of(JulianDateTime julianDateTime, ZoneId zone) {
    return ofLocal(julianDateTime, zone, null);
  }

  /**
   * 嘗試作法：
   * 先把 JulianDate 轉成 LocalDate
   * 產生 LocalDateTime -> ZonedDateTime , 取得 Offset
   */
  private static ZonedJulianDateTime ofLocal(JulianDateTime julianDateTime, ZoneId zone, ZoneOffset preferredOffset) {
    Objects.requireNonNull(julianDateTime, "julianDateTime");
    Objects.requireNonNull(zone, "zone");
    if (zone instanceof ZoneOffset) {
      return new ZonedJulianDateTime(julianDateTime, (ZoneOffset) zone, zone);
    }

    LocalDate localDate = LocalDate.from(julianDateTime);
    LocalDateTime ldt = LocalDateTime.of(localDate, julianDateTime.toLocalTime());
    ZonedDateTime zdt = ZonedDateTime.ofLocal(ldt, zone, preferredOffset);
    ZoneOffset offset = zdt.getOffset();

    return new ZonedJulianDateTime(julianDateTime, offset, zone);
  }

  private static ZonedJulianDateTime create(long epochSecond, int nanoOfSecond, ZoneId zone) {
    ZoneRules rules = zone.getRules();
    Instant instant = Instant.ofEpochSecond(epochSecond, nanoOfSecond);  // TODO: rules should be queryable by epochSeconds
    ZoneOffset offset = rules.getOffset(instant);
    JulianDateTime jdt = JulianDateTime.ofEpochSecond(epochSecond, nanoOfSecond, offset);
    return new ZonedJulianDateTime(jdt, offset, zone);
  }


  private static ZonedJulianDateTime from(TemporalAccessor temporal) {
    if (temporal instanceof ZonedJulianDateTime) {
      return (ZonedJulianDateTime) temporal;
    }
    try {
      ZoneId zone = ZoneId.from(temporal);
      if (temporal.isSupported(INSTANT_SECONDS)) {
        long epochSecond = temporal.getLong(INSTANT_SECONDS);
        int nanoOfSecond = temporal.get(NANO_OF_SECOND);
        return create(epochSecond, nanoOfSecond, zone);
      }
      else {
        JulianDate date = JulianDate.from(temporal);
        LocalTime time = LocalTime.from(temporal);
        return of(date, time, zone);
      }
    } catch (DateTimeException ex) {
      throw new DateTimeException("Unable to obtain ZonedDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
    }
  }



  @Override
  public JulianDateTime toLocalDateTime() {
    return dateTime;
  }

  @Override
  public ZoneOffset getOffset() {
    return offset;
  }

  @Override
  public ZoneId getZone() {
    return zone;
  }

  /**
   * see {@link ZonedDateTime#withEarlierOffsetAtOverlap}
   */
  @Override
  public ZonedJulianDateTime withEarlierOffsetAtOverlap() {
    return null;
  }

  @Override
  public ZonedJulianDateTime withLaterOffsetAtOverlap() {
    return null;
  }

  @Override
  public ZonedJulianDateTime withZoneSameLocal(ZoneId zone) {
    return null;
  }

  @Override
  public ZonedJulianDateTime withZoneSameInstant(ZoneId zone) {
    return null;
  }


  @Override
  public ZonedJulianDateTime with(TemporalField field, long newValue) {
    return null;
  }

  @Override
  public ZonedJulianDateTime plus(long amountToAdd, TemporalUnit unit) {
    return null;
  }



  @Override
  public long until(Temporal endExclusive, TemporalUnit unit) {
    ZonedJulianDateTime end = ZonedJulianDateTime.from(endExclusive);
    if (unit instanceof ChronoUnit) {
      end = end.withZoneSameInstant(zone);
      if (unit.isDateBased()) {
        return dateTime.until(end.dateTime, unit);
      }
      else {
        return toOffsetDateTime().until(end.toOffsetDateTime(), unit);
      }
    }
    return unit.between(this, end);
  }

  /**
   * TODO finish
   */
  private OffsetDateTime toOffsetDateTime() {
//     return OffsetDateTime.of(dateTime, offset);
    return null;
  }

  @Override
  public boolean isSupported(TemporalField field) {
    return false;
  }

  /**
   * Gets the hour-of-day field.
   *
   * @return the hour-of-day, from 0 to 23
   */
  public int getHour() {
    return dateTime.getHour();
  }

  /**
   * Gets the minute-of-hour field.
   *
   * @return the minute-of-hour, from 0 to 59
   */
  public int getMinute() {
    return dateTime.getMinute();
  }

  /**
   * Gets the second-of-minute field.
   *
   * @return the second-of-minute, from 0 to 59
   */
  public int getSecond() {
    return dateTime.getSecond();
  }

  /**
   * Gets the nano-of-second field.
   *
   * @return the nano-of-second, from 0 to 999,999,999
   */
  public int getNano() {
    return dateTime.getNano();
  }


}
