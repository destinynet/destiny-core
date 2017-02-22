/*
 * @author smallufo
 * @date 2004/11/1
 * @time 下午 10:10:16
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.LongitudeTimeBean;
import destiny.core.calendar.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * 真太陽時計算介面 <br/>
 * Swiss Ephemeris 實作是 TrueSolarTimeImpl
 */
public interface TrueSolarTimeIF {

  Logger logger = LoggerFactory.getLogger(TrueSolarTimeIF.class);

  /**
   * E : Equation of Time
   * E = LAT - LMT
   * 均時差 = 真太陽時 - LMT
   * 真太陽時 = LMT + 均時差
   */
  double getEquationSecs(double gmtJulDay);

  default double getEquationSecs(LocalDateTime gmtTime) {
    double gmtJulDay = Time.getGmtJulDay(gmtTime);
    return getEquationSecs(gmtJulDay);
  }

  /** 取得 LMT 時刻所對應的 真太陽時 */
  default LocalDateTime getTrueSolarTime(LocalDateTime lmt , Location location) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    double e = getEquationSecs(gmt);
    long eSecs = (long) e;
    long eNano = (long) ((e - eSecs)*1_000_000_000);
    logger.debug("e = {} , eSecs = {} , eNano = {}" , e , eSecs , eNano);
    LocalDateTime gmtWithE = LocalDateTime.from(gmt).plusSeconds(eSecs).plusNanos(eNano);
    logger.debug("gmt  = {}" , gmt);
    logger.debug("gmtE = {}" , gmtWithE);
    LocalDateTime lmtWithE = Time.getLmtFromGmt(gmtWithE , location);
    logger.debug("lmtE = {}" , lmtWithE);

    return LongitudeTimeBean.getLocalTime(lmtWithE, location);
  }

}
