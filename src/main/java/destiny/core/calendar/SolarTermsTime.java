/**
 * Created by smallufo on 2015-06-01.
 */
package destiny.core.calendar;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;

/**
 * 存放 一對 SolarTerms 以及 Time 的小 class
 */
public final class SolarTermsTime implements Serializable {

  /** 節氣 */
  private final SolarTerms solarTerms;

  /** 可能是 GMT , 也可能是 LMT */
  private final ChronoLocalDateTime time;

  SolarTermsTime(SolarTerms solarTerms, ChronoLocalDateTime time) {
    this.solarTerms = solarTerms;
    this.time = time;
  }

  public SolarTerms getSolarTerms() {
    return solarTerms;
  }

  public ChronoLocalDateTime getTime() {
    return time;
  }

}
