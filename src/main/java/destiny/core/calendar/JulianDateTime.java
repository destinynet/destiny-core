/**
 * Created by smallufo on 2017-01-20.
 */
package destiny.core.calendar;

import org.apache.commons.lang3.tuple.Pair;
import org.threeten.extra.chrono.JulianDate;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.Month;

import static java.time.temporal.ChronoField.*;

/**
 * reference : {@link java.time.LocalDateTime}
 * TODO: implements ChronoLocalDateTime<JulianDate>
 */
public class JulianDateTime implements Serializable {

  private final JulianDate date;
  private final LocalTime time;


  private JulianDateTime(JulianDate date, LocalTime time) {
    this.date = date;
    this.time = time;
  }

  /**
   * @param year maybe <= 0
   */
  public static JulianDateTime of(int year, int month, int dayOfMonth, int hour, int minute , double second) {
    JulianDate date = JulianDate.of(year , month , dayOfMonth);

    Pair<Long , Long> pair = Time.splitSecond(second);
    LocalTime time = LocalTime.of(hour , minute , pair.getLeft().intValue() , pair.getRight().intValue());
    return new JulianDateTime(date , time);
  }

  /**
   * @param year maybe <= 0
   */
  public static JulianDateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
    return of(year , month , dayOfMonth , hour , minute , 0);
  }

  /**
   * @param year maybe <= 0
   */
  public static JulianDateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
    return of(year , month.getValue() , dayOfMonth , hour , minute , 0);
  }

  public int getProlepticYear() {
    return date.get(YEAR);
  }

  /** must be >= 0 */
  public int getYear() {
    return date.get(YEAR_OF_ERA);
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

  public double getSecond() {
    return time.getSecond() + (time.getNano() / 1_000_000_000.0);
  }

  public JulianDate toLocalDate() {
    return date;
  }

  public LocalTime toLocalTime() {
    return time;
  }


}
