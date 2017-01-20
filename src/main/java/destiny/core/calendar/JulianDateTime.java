/**
 * Created by smallufo on 2017-01-20.
 */
package destiny.core.calendar;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.Month;

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

  public static JulianDateTime of(int year, int month, int dayOfMonth, int hour, int minute , double second) {
    JulianDate date = JulianDate.of(year , month , dayOfMonth);
    int intSecond = (int) second;
    int nanoSecond = (int) ((second - intSecond) * 1_000_000_000);
    LocalTime time = LocalTime.of(hour , minute , intSecond , nanoSecond);
    return new JulianDateTime(date , time);
  }

  public static JulianDateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
    return of(year , month , dayOfMonth , hour , minute , 0);
  }

  public static JulianDateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
    return of(year , month.getValue() , dayOfMonth , hour , minute , 0);
  }

  public int getProlepticYear() {
    return date.getProlepticYear();
  }

  /** 一定大於 0 */
  public int getYear() {
    return date.getYearOfEra();
  }

  public int getMonth() {
    return date.getMonth();
  }

  public int getDayOfMonth() {
    return date.getDayOfMonth();
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
