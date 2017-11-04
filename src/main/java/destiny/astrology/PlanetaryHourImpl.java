/**
 * Created by smallufo on 2017-10-31.
 */
package destiny.astrology;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.jooq.lambda.tuple.Tuple4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static destiny.astrology.Planet.*;
import static destiny.astrology.TransPoint.*;

/**
 * TODO 實作 IPlanetaryHour
 * <p>
 * http://www.astrology.com.tr/planetary-hours.asp
 * <p>
 * http://pansci.asia/archives/126644
 * <p>
 * 晝夜、分別劃分 12等分
 */
public class PlanetaryHourImpl implements IPlanetaryHour, Serializable {

  private final IRiseTrans riseTransImpl;

  /**
   * 星期六白天起，七顆行星順序： 土、木、火、日、金、水、月
   */
  private final static Planet[] seqPlanet = new Planet[]{SATURN, JUPITER, MARS, SUN, VENUS, MERCURY, MOON};

  /**
   * 日期順序
   */
  private final static int[] seqDay = new int[]{6, 7, 1, 2, 3, 4, 5};

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final static Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl::getLocalDateTimeStatic;

  public PlanetaryHourImpl(IRiseTrans riseTransImpl) {this.riseTransImpl = riseTransImpl;}

  @Override
  public PlanetaryHour getPlanetaryHour(double gmtJulDay, Location loc) {

    // hourStart , hourEnd , hourIndex (1 to 24)
    Tuple4<Double , Double , Integer , DayNight> tuples = getHourIndexOfDay(gmtJulDay , loc);

    Planet planet = getPlanet(tuples.v3() , gmtJulDay , loc);
    return new PlanetaryHour(tuples.v1() , tuples.v2() , tuples.v4(), planet , loc);
  } // getPlanetaryHour



  @Override
  public List<PlanetaryHour> getPlanetaryHours(double fromGmt, double toGmt, Location loc) {
    if (fromGmt >= toGmt) {
      throw new RuntimeException("fromGmt : " + fromGmt + " larger than or equal to toGmt : " + toGmt);
    }

    List<PlanetaryHour> result = new ArrayList<>();

    double cursor = fromGmt;
    while (cursor < toGmt) {
      // hourStart , hourEnd , hourIndex (1 to 24)
      Tuple4<Double , Double , Integer , DayNight> hourIndexOfDayAndAvgHour = getHourIndexOfDay(cursor , loc);
      double hourStart = hourIndexOfDayAndAvgHour.v1();
      double hourEnd = hourIndexOfDayAndAvgHour.v2();
      int dayIndex = hourIndexOfDayAndAvgHour.v3();
      DayNight dayNight = hourIndexOfDayAndAvgHour.v4();
      Planet planet = getPlanet(dayIndex , hourStart , loc);

      PlanetaryHour planetaryHour = new PlanetaryHour(hourStart , hourEnd , dayNight, planet , loc);
      result.add(planetaryHour);

      cursor = hourEnd + (1/86400.0);
    }
    return result;
  } // getPlanetaryHours , 一段時間內的 Planetary Hours


  /**
   * tuple1 : hourStart
   * tuple2 : hourEnd
   * tuple3 : 整天 的 hour index , from 1 to 24
   * tuple4 : DayNight
   */
  private Tuple4<Double , Double , Integer , DayNight> getHourIndexOfDay(double gmtJulDay , Location loc ) {

    double nextRising = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, RISING, loc);
    double nextSetting = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, SETTING, loc);

    Tuple4<Double , Double , Integer , DayNight> halfDayIndex;
    DayNight dayNight;
    if (nextRising < nextSetting) {
      // 目前是黑夜
      dayNight = DayNight.NIGHT;
      // 先計算「接近上一個中午」的時刻，這裡不用算得很精準
      double nearPrevMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, MERIDIAN, loc) - 1; // 記得減一
      // 接著，計算「上一個」日落時刻
      double prevSetting = riseTransImpl.getGmtTransJulDay(nearPrevMeridian, SUN, SETTING, loc);

      halfDayIndex = getHourIndexOfHalfDay(prevSetting , nextRising , gmtJulDay).concat(dayNight);
    }
    else {
      // 目前是白天
      dayNight = DayNight.DAY;
      // 先計算「接近上一個子正的時刻」，這禮不用算得很經準
      double nearPrevMidNight = riseTransImpl.getGmtTransJulDay(gmtJulDay, SUN, NADIR, loc) - 1; // 記得減一
      // 接著，計算「上一個」日出時刻
      double prevRising = riseTransImpl.getGmtTransJulDay(nearPrevMidNight, SUN, RISING, loc);

      halfDayIndex = getHourIndexOfHalfDay(prevRising, nextSetting , gmtJulDay).concat(dayNight);
    }

    return halfDayIndex.map3(value -> dayNight == DayNight.NIGHT ? value + 12 : value); // 夜晚 + 12
  }

  /**
   * 「半天」的 hour index , from 1 to 12
   * @return Tuple[ hourStart , hourEnd , hourIndex]
   */
  private Tuple3<Double , Double , Integer> getHourIndexOfHalfDay(double from, double to, double gmtJulDay) {
    if (gmtJulDay < from || gmtJulDay > to) {
      // gmtJulDay 一定要在 from 與 to 的範圍內
      throw new RuntimeException("gmtJulDay " + gmtJulDay + " not between " + from + " and " + to);
    }
    else {
      double avgHour = (to - from) / 12.0;
      for (int i = 1; i <= 11; i++) {
        double stepFrom = from + avgHour * (i - 1);
        double stepTo = from + avgHour * i;
        if (gmtJulDay >= stepFrom && gmtJulDay < stepTo) {
          return Tuple.tuple(stepFrom , stepTo , i);
        }
      }
      return Tuple.tuple(from + avgHour * 11, to , 12);
    }
  } // getHourIndexOfHalfDay , return 1 to 12

  private Planet getPlanet(int hourIndexOfDay , double gmtJulDay , Location loc) {
    ChronoLocalDateTime lmt = TimeTools.getLmtFromGmt(gmtJulDay , loc , revJulDayFunc);

    // 1:星期一 , 2:星期二 ... , 6:星期六 , 7:星期日
    int dayOfWeek = lmt.get(ChronoField.DAY_OF_WEEK);

    logger.debug("dayOfWeek = {}" , dayOfWeek);

    // from 0 to 6
    int indexOfDayTable = ArrayUtils.indexOf(seqDay , dayOfWeek);
    logger.debug("indexOfDayTable = {}" , indexOfDayTable);

    // 0 to (24x7-1)
    int hourIndexFromSaturday = indexOfDayTable * 24 + hourIndexOfDay-1;
    logger.debug("hourIndexFromSaturday = {}" , hourIndexFromSaturday);

    return seqPlanet[hourIndexFromSaturday % 7];
  }
}
