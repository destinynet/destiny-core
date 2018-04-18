/**
 * Created by smallufo on 2017-01-20.
 */
package destiny.core.calendar;

import kotlin.Pair;
import org.threeten.extra.chrono.JulianDate;

import java.io.Serializable;
import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.*;
import java.util.Objects;

import static java.time.temporal.ChronoField.*;

/**
 * reference : {@link java.time.LocalDateTime}
 */
public class JulianDateTime implements Serializable , ChronoLocalDateTime<JulianDate> {

  /**
   * Hours per day.
   */
  private static final int HOURS_PER_DAY = 24;

  /**
   * Minutes per hour.
   */
  private static final int MINUTES_PER_HOUR = 60;

  /**
   * Minutes per day.
   */
  private static final int MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY;

  /**
   * Seconds per minute.
   */
  private static final int SECONDS_PER_MINUTE = 60;

  /**
   * Seconds per hour.
   */
  private static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

  /**
   * Seconds per day.
   */
  private static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;

  /**
   * Milliseconds per day.
   */
  private static final long MILLIS_PER_DAY = SECONDS_PER_DAY * 1000L;

  /**
   * Microseconds per day.
   */
  private static final long MICROS_PER_DAY = SECONDS_PER_DAY * 1000_000L;

  /**
   * Nanos per second.
   */
  private static final long NANOS_PER_SECOND = 1000_000_000L;

  /**
   * Nanos per minute.
   */
  private static final long NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE;

  /**
   * Nanos per hour.
   */
  private static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR;

  /**
   * Nanos per day.
   */
  private static final long NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY;

  private final JulianDate date;
  private final LocalTime time;


  private JulianDateTime(JulianDate date, LocalTime time) {
    this.date = date;
    this.time = time;
  }

  public static JulianDateTime from(TemporalAccessor temporal) {
    if (temporal instanceof JulianDateTime) {
      return (JulianDateTime) temporal;
    }
    else if (temporal instanceof ZonedJulianDateTime) {
      return ((ZonedJulianDateTime) temporal).toLocalDateTime();
    }
//    else if (temporal instanceof OffsetDateTime) {
//      return ((OffsetDateTime) temporal).toLocalDateTime();
//    }
    try {
      JulianDate date = JulianDate.from(temporal);
      LocalTime time = LocalTime.from(temporal);
      return new JulianDateTime(date, time);
    } catch (DateTimeException ex) {
      throw new DateTimeException("Unable to obtain JulianDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
    }
  }

  public static JulianDateTime of(JulianDate date, LocalTime time) {
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(time, "time");
    return new JulianDateTime(date, time);
  }

  /**
   * @param prolepticYear maybe <= 0
   */
  public static JulianDateTime of(int prolepticYear, int month, int dayOfMonth, int hour, int minute , int second , int nano) {
    JulianDate date = JulianDate.of(prolepticYear , month , dayOfMonth);
    LocalTime time = LocalTime.of(hour , minute , second , nano);
    return new JulianDateTime(date , time);
  }

  /**
   * @param prolepticYear maybe <= 0
   */
  public static JulianDateTime of(int prolepticYear, int month, int dayOfMonth, int hour, int minute , int second) {
    JulianDate date = JulianDate.of(prolepticYear , month , dayOfMonth);

    Pair<Integer , Integer> pair = TimeTools.Companion.splitSecond(second);
    LocalTime time = LocalTime.of(hour , minute , pair.getFirst(), pair.getSecond());
    return new JulianDateTime(date , time);
  }

  /**
   * @param prolepticYear maybe <= 0
   */
  public static JulianDateTime of(int prolepticYear, int month, int dayOfMonth, int hour, int minute) {
    return of(prolepticYear , month , dayOfMonth , hour , minute , 0);
  }

  /**
   * @param prolepticYear maybe <= 0
   */
  public static JulianDateTime of(int prolepticYear, Month month, int dayOfMonth, int hour, int minute) {
    return of(prolepticYear , month.getValue() , dayOfMonth , hour , minute , 0);
  }

  public static JulianDateTime ofEpochSecond(long epochSecond, int nanoOfSecond, ZoneOffset offset) {
    Objects.requireNonNull(offset, "offset");
    NANO_OF_SECOND.checkValidValue(nanoOfSecond);
    long localSecond = epochSecond + offset.getTotalSeconds();  // overflow caught later
    long localEpochDay = Math.floorDiv(localSecond, (long)SECONDS_PER_DAY);
    int secsOfDay = (int) Math.floorMod(localSecond, (long)SECONDS_PER_DAY);
    LocalDate gDate = LocalDate.ofEpochDay(localEpochDay);
    JulianDate date = JulianDate.from(gDate);
    LocalTime time = LocalTime.ofNanoOfDay(secsOfDay * NANOS_PER_SECOND + nanoOfSecond);
    return new JulianDateTime(date, time);
  }

  /** 一定為正值 */
  public int getYearOfEra() {
    return date.get(YEAR_OF_ERA);
  }

  /** 與 {@link LocalDate#year} 一樣，都為 proleptic year . 西元元年=1 , 西元前一年=0 */
  public int getYear() {
    return date.get(YEAR);
  }

  public int getMonth() {
    return date.get(MONTH_OF_YEAR);
  }

  public int getDayOfMonth() {
    return date.get(DAY_OF_MONTH);
  }

  public int getHour() {
    return time.getHour();
  }

  public int getMinute() {
    return time.getMinute();
  }

  public int getSecond() {
    return time.getSecond();
  }

  public int getNano() {
        return time.getNano();
    }

  public JulianDate toLocalDate() {
    return date;
  }

  public LocalTime toLocalTime() {
    return time;
  }

  /**
   * see {@link java.time.LocalDateTime#isSupported(TemporalField)}
   */
  @Override
  public boolean isSupported(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      return f.isDateBased() || f.isTimeBased();
    }
    return field != null && field.isSupportedBy(this);
  }

  /**
   * see {@link java.time.LocalDateTime#getLong(TemporalField)}
   */
  @Override
  public long getLong(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      return (f.isTimeBased() ? time.getLong(field) : date.getLong(field));
    }
    return field.getFrom(this);
  }

  /**
   * see {@link LocalDateTime#with(TemporalField, long)}
   */
  @Override
  public ChronoLocalDateTime<JulianDate> with(TemporalField field, long newValue) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      if (f.isTimeBased()) {
        return with(date, time.with(field, newValue));
      }
      else {
        return with(date.with(field, newValue), time);
      }
    }
    return field.adjustInto(this, newValue);
  }

  /**
   * see {@link LocalDateTime#with(LocalDate, LocalTime)}
   */
  private JulianDateTime with(JulianDate newDate, LocalTime newTime) {
    if (date == newDate && time == newTime) {
      return this;
    }
    return new JulianDateTime(newDate, newTime);
  }

  /**
   * see {@link LocalDateTime#plus(long, TemporalUnit)}
   */
  @Override
  public ChronoLocalDateTime<JulianDate> plus(long amountToAdd, TemporalUnit unit) {
    if (unit instanceof ChronoUnit) {
      ChronoUnit f = (ChronoUnit) unit;
      switch (f) {
        case NANOS: return plusNanos(amountToAdd);
        case MICROS:return plusDays(amountToAdd / MICROS_PER_DAY).plusNanos((amountToAdd % MICROS_PER_DAY) * 1000);
        case MILLIS:return plusDays(amountToAdd / MILLIS_PER_DAY).plusNanos((amountToAdd % MILLIS_PER_DAY) * 1000_000);
        case SECONDS:return plusSeconds(amountToAdd);
        case MINUTES:return plusMinutes(amountToAdd);
        case HOURS:return plusHours(amountToAdd);
        case HALF_DAYS:return plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12);  // no overflow (256 is multiple of 2)
      }
      return with(date.plus(amountToAdd, unit), time);
    }
    return unit.addTo(this, amountToAdd);
  }

  @Override
  public ChronoLocalDateTime<JulianDate> minus(long amountToSubtract, TemporalUnit unit) {
    return plus(-amountToSubtract , unit);
  }

  private JulianDateTime plusNanos(long nanos) {
    return plusWithOverflow(date, 0, 0, 0, nanos, 1);
  }

  private JulianDateTime plusSeconds(long seconds) {
    return plusWithOverflow(date, 0, 0, seconds, 0, 1);
  }

  private JulianDateTime plusMinutes(long minutes) {
    return plusWithOverflow(date, 0, minutes, 0, 0, 1);
  }

  private JulianDateTime plusHours(long hours) {
    return plusWithOverflow(date, hours, 0, 0, 0, 1);
  }

  private JulianDateTime plusDays(long days) {
    JulianDate newDate = date.plus(days , ChronoUnit.DAYS);
    return with(newDate, time);
  }

  /**
   * see {@link LocalDateTime#plusWithOverflow}
   */
  private JulianDateTime plusWithOverflow(JulianDate newDate, long hours, long minutes, long seconds, long nanos, int sign) {
    // 9223372036854775808 long, 2147483648 int
    if ((hours | minutes | seconds | nanos) == 0) {
      return with(newDate, time);
    }
    long totDays = nanos / NANOS_PER_DAY +             //   max/24*60*60*1B
      seconds / SECONDS_PER_DAY +                //   max/24*60*60
      minutes / MINUTES_PER_DAY +                //   max/24*60
      hours / HOURS_PER_DAY;                     //   max/24
    totDays *= sign;                                   // total max*0.4237...
    long totNanos = nanos % NANOS_PER_DAY +                    //   max  86400000000000
      (seconds % SECONDS_PER_DAY) * NANOS_PER_SECOND +   //   max  86400000000000
      (minutes % MINUTES_PER_DAY) * NANOS_PER_MINUTE +   //   max  86400000000000
      (hours % HOURS_PER_DAY) * NANOS_PER_HOUR;          //   max  86400000000000
    long curNoD = time.toNanoOfDay();                       //   max  86400000000000
    totNanos = totNanos * sign + curNoD;                    // total 432000000000000
    totDays += Math.floorDiv(totNanos, NANOS_PER_DAY);
    long newNoD = Math.floorMod(totNanos, NANOS_PER_DAY);
    LocalTime newTime = (newNoD == curNoD ? time : LocalTime.ofNanoOfDay(newNoD));
    return with(newDate.plus(totDays , ChronoUnit.DAYS), newTime);
  }


  /**
   * see {@link LocalDateTime#until(Temporal, TemporalUnit)}
   */
  @Override
  public long until(Temporal endExclusive, TemporalUnit unit) {
    JulianDateTime end = JulianDateTime.from(endExclusive);
    if (unit instanceof ChronoUnit) {
      if (unit.isTimeBased()) {
        // long amount = date.daysUntil(end.date);
        long amount = date.until(end.date).get(ChronoUnit.DAYS);
        if (amount == 0) {
          return time.until(end.time, unit);
        }
        long timePart = end.time.toNanoOfDay() - time.toNanoOfDay();
        if (amount > 0) {
          amount--;  // safe
          timePart += NANOS_PER_DAY;  // safe
        }
        else {
          amount++;  // safe
          timePart -= NANOS_PER_DAY;  // safe
        }
        switch ((ChronoUnit) unit) {
          case NANOS:
            amount = Math.multiplyExact(amount, NANOS_PER_DAY);
            break;
          case MICROS:
            amount = Math.multiplyExact(amount, MICROS_PER_DAY);
            timePart = timePart / 1000;
            break;
          case MILLIS:
            amount = Math.multiplyExact(amount, MILLIS_PER_DAY);
            timePart = timePart / 1_000_000;
            break;
          case SECONDS:
            amount = Math.multiplyExact(amount, SECONDS_PER_DAY);
            timePart = timePart / NANOS_PER_SECOND;
            break;
          case MINUTES:
            amount = Math.multiplyExact(amount, MINUTES_PER_DAY);
            timePart = timePart / NANOS_PER_MINUTE;
            break;
          case HOURS:
            amount = Math.multiplyExact(amount, HOURS_PER_DAY);
            timePart = timePart / NANOS_PER_HOUR;
            break;
          case HALF_DAYS:
            amount = Math.multiplyExact(amount, 2);
            timePart = timePart / (NANOS_PER_HOUR * 12);
            break;
        }
        return Math.addExact(amount, timePart);
      }
      JulianDate endDate = end.date;
      if (endDate.isAfter(date) && end.time.isBefore(time)) {
        // endDate = endDate.minusDays(1);
        endDate = endDate.minus(1 , ChronoUnit.DAYS);
      }
      else if (endDate.isBefore(date) && end.time.isAfter(time)) {
        // endDate = endDate.plusDays(1);
        endDate = endDate.plus(1 , ChronoUnit.DAYS);
      }
      return date.until(endDate, unit);
    }
    return unit.between(this, end);
  }

  @Override
  public ZonedJulianDateTime atZone(ZoneId zone) {
    return ZonedJulianDateTime.of(this, zone);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof JulianDateTime))
      return false;
    JulianDateTime that = (JulianDateTime) o;
    return Objects.equals(date, that.date) && Objects.equals(time, that.time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, time);
  }


  @Override
  public String toString() {
    return "[JulianDateTime " + "date=" + date + ", time=" + time + ']';
  }
}
