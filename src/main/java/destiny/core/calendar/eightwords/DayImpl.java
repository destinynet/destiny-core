/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 05:19:35
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.util.function.Function;

/**
 * 換日 的實作
 */
public class DayImpl implements DayIF , Serializable {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl::getLocalDateTimeStatic;

  public DayImpl()
  {
  }


  @Override
  public StemBranch getDay(double gmtJulDay, Location location, MidnightIF midnightImpl, HourIF hourImpl, boolean changeDayAfterZi) {

    ChronoLocalDateTime lmt = TimeTools.getLmtFromGmt(gmtJulDay , location , revJulDayFunc);

    int lmtJulDay = (int) ( TimeTools.getGmtJulDay(lmt)+0.5);
    logger.debug("lmtJulDay = {}" , lmtJulDay);

    int index = (lmtJulDay-11) % 60;


    ChronoLocalDateTime nextMidnightLmt = midnightImpl.getNextMidnight(lmt, location , revJulDayFunc);
    ChronoLocalDateTime 下個子初時刻 = hourImpl.getLmtNextStartOf(lmt , location , Branch.子 , revJulDayFunc);

    if (nextMidnightLmt.get(ChronoField.HOUR_OF_DAY) >=12) {
      //子正，在 LMT 零時之前
      index = getIndex(index , nextMidnightLmt , lmt , hourImpl , location , changeDayAfterZi , 下個子初時刻);
    } else {
      //子正，在 LMT 零時之後（含）
      if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
        // lmt 落於當地 零時 到 子正的這段期間
        if (TimeTools.isBefore(下個子初時刻 , nextMidnightLmt)) {
          // lmt 落於零時到子初之間 (這代表當地地點「極西」) , 此時一定還沒換日
          index--;
        } else {
          // lmt 落於子初到子正之間
          if (!changeDayAfterZi) //如果子正才換日
            index--;
        }
      } else {
        // lmt 落於前一個子正之後，到當天24時為止 (範圍最大的一塊「餅」)
        if (changeDayAfterZi
          && lmt.get(ChronoField.DAY_OF_MONTH) != 下個子初時刻.get(ChronoField.DAY_OF_MONTH)
          && 下個子初時刻.get(ChronoField.HOUR_OF_DAY) >=12) {
          // lmt 落於 子初之後 , 零時之前 , 而子初又是在零時之前（hour >=12 , 過濾掉極西的狀況)
          index++;
        }
      }
    }
    return StemBranch.get(index);
  } // GMT 版本

  private int getIndex(int index , ChronoLocalDateTime nextMidnightLmt , ChronoLocalDateTime lmt , HourIF hourImpl , Location location , boolean changeDayAfterZi , ChronoLocalDateTime 下個子初時刻) {
    //子正，在 LMT 零時之前
    if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
      // lmt 落於 當日零時之後，子正之前（餅最大的那一塊）
      ChronoLocalDateTime midnightNextZi = hourImpl.getLmtNextStartOf(nextMidnightLmt, location, Branch.子, revJulDayFunc);
      if (changeDayAfterZi && 下個子初時刻.get(ChronoField.DAY_OF_MONTH) == midnightNextZi.get(ChronoField.DAY_OF_MONTH))
        index++;
    }
    else {
      // lmt 落於 子正之後，到 24 時之間 (其 nextMidnight 其實是明日的子正) , 則不論是否早子時換日，都一定換日
      index++;
    }
    return index;
  }

  @Override
  public StemBranch getDay(ChronoLocalDateTime lmt, Location location, MidnightIF midnightImpl, HourIF hourImpl, boolean changeDayAfterZi) {
    // 這是很特別的作法，將 lmt 當作 GMT 取 JulDay
    int lmtJulDay = (int)(TimeTools.getGmtJulDay(lmt)+0.5);
    int index = (lmtJulDay-11) % 60;

    ChronoLocalDateTime nextMidnightLmt = midnightImpl.getNextMidnight(lmt , location , revJulDayFunc);
    ChronoLocalDateTime 下個子初時刻 = hourImpl.getLmtNextStartOf(lmt , location , Branch.子 , revJulDayFunc);

    if (nextMidnightLmt.get(ChronoField.HOUR_OF_DAY) >=12 ) {
      //子正，在 LMT 零時之前
      index = getIndex(index , nextMidnightLmt , lmt , hourImpl , location , changeDayAfterZi , 下個子初時刻);
    } else {
      //子正，在 LMT 零時之後（含）
      if (nextMidnightLmt.get(ChronoField.DAY_OF_MONTH) == lmt.get(ChronoField.DAY_OF_MONTH)) {
        // lmt 落於當地 零時 到 子正的這段期間
        if (TimeTools.isBefore(下個子初時刻 , nextMidnightLmt)) {
          // lmt 落於零時到子初之間 (這代表當地地點「極西」) , 此時一定還沒換日
          index--;
        } else {
          // lmt 落於子初到子正之間
          if (!changeDayAfterZi) //如果子正才換日
            index--;
        }
      } else {
        // lmt 落於前一個子正之後，到當天24時為止 (範圍最大的一塊「餅」)
        if (changeDayAfterZi
          && lmt.get(ChronoField.DAY_OF_MONTH) != 下個子初時刻.get(ChronoField.DAY_OF_MONTH)
          && 下個子初時刻.get(ChronoField.HOUR_OF_DAY) >= 12)
          // lmt 落於 子初之後 , 零時之前 , 而子初又是在零時之前（hour >=12 , 過濾掉極西的狀況)
          index++;
      }
    }
    return StemBranch.get(index);
  } // LMT 版本


} //DayImpl()