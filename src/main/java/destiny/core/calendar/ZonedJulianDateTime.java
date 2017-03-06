/**
 * Created by smallufo on 2017-03-07.
 */
package destiny.core.calendar;

import org.threeten.extra.chrono.JulianDate;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

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


  public static ZonedJulianDateTime of(JulianDateTime julianDateTime, ZoneId zone) {
    return ofLocal(julianDateTime, zone, null);
  }

  /**
   * 嘗試作法：
   * 先把 JulianDate 轉成 LocalDate
   * 產生 LocalDateTime -> ZonedDateTime , 取得 Offset
   */
  public static ZonedJulianDateTime ofLocal(JulianDateTime julianDateTime, ZoneId zone, ZoneOffset preferredOffset) {
    Objects.requireNonNull(julianDateTime, "julianDateTime");
    Objects.requireNonNull(zone, "zone");
    if (zone instanceof ZoneOffset) {
      return new ZonedJulianDateTime(julianDateTime, (ZoneOffset) zone, zone);
    }

    LocalDate localDate = LocalDate.from(julianDateTime);
    LocalDateTime ldt = LocalDateTime.of(localDate , julianDateTime.toLocalTime());
    ZonedDateTime zdt = ZonedDateTime.ofLocal(ldt , zone , preferredOffset);
    ZoneOffset offset = zdt.getOffset();

    return new ZonedJulianDateTime(julianDateTime, offset, zone);
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
    return 0;
  }

  @Override
  public boolean isSupported(TemporalField field) {
    return false;
  }

}
