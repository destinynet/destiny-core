/*
 * @author smallufo
 * @date 2004/11/1
 * @time 下午 10:10:16
 */
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;

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

  default double getEquationSecs(ChronoLocalDateTime gmtTime) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmtTime);
    return getEquationSecs(gmtJulDay);
  }

  /** 取得 LMT 時刻所對應的 真太陽時 */
  default ChronoLocalDateTime getTrueSolarTime(ChronoLocalDateTime lmt , Location location) {
    ChronoLocalDateTime gmt = TimeTools.getGmtFromLmt(lmt , location);
    logger.debug("gmt = {}" , gmt);
    double e = getEquationSecs(gmt);
    Tuple2<Integer , Integer> pair = TimeTools.splitSecond(e);

    ChronoLocalDateTime gmtWithE = gmt.plus(pair.v1() , ChronoUnit.SECONDS).plus(pair.v2() , ChronoUnit.NANOS);
    logger.debug("gmt  = {}" , gmt);
    logger.debug("gmtE = {}" , gmtWithE);

    ChronoLocalDateTime lmtWithE = TimeTools.getLmtFromGmt(gmtWithE , location);
    logger.debug("lmtE = {}" , lmtWithE);

    return TimeTools.getLongitudeTime(lmtWithE, location);
  }

}
