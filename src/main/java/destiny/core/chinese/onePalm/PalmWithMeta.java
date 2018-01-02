/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.onePalm;

import destiny.core.calendar.Location;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.IChineseDate;
import destiny.core.calendar.chinese.IFinalMonthNumber;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.IHour;
import destiny.core.calendar.eightwords.IMidnight;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;

public class PalmWithMeta implements Serializable {

  private final Palm palm;

  private final ChronoLocalDateTime lmt;

  private final Location loc;

  private final String place;

  private final IChineseDate chineseDateImpl;

  private final DayIF dayImpl;

  private final PositiveIF positiveImpl;

  private final IHour hourImpl;

  private final IMidnight midnightImpl;

  private final boolean changeDayAfterZi;

  private final boolean trueRisingSign;

  private final IFinalMonthNumber.MonthAlgo monthAlgo;

  public PalmWithMeta(Palm palm, ChronoLocalDateTime lmt, Location loc, String place, IChineseDate chineseDateImpl, DayIF dayImpl, PositiveIF positiveImpl, IHour impl, IMidnight midnightImpl, boolean changeDayAfterZi, boolean trueRisingSign, IFinalMonthNumber.MonthAlgo monthAlgo) {
    this.palm = palm;
    this.lmt = lmt;
    this.loc = loc;
    this.place = place;
    this.chineseDateImpl = chineseDateImpl;
    this.dayImpl = dayImpl;
    this.positiveImpl = positiveImpl;
    this.hourImpl = impl;
    this.midnightImpl = midnightImpl;
    this.changeDayAfterZi = changeDayAfterZi;
    this.trueRisingSign = trueRisingSign;
    this.monthAlgo = monthAlgo;
  }



  public Palm getPalm() {
    return palm;
  }

  public ChronoLocalDateTime getLmt() {
    return lmt;
  }

  public Location getLoc() {
    return loc;
  }

  public String getPlace() {
    return place;
  }

  public IChineseDate getChineseDateImpl() {
    return chineseDateImpl;
  }

  public DayIF getDayImpl() {
    return dayImpl;
  }

  public PositiveIF getPositiveImpl() {
    return positiveImpl;
  }

  public IHour getHourImpl() {
    return hourImpl;
  }

  public IMidnight getMidnightImpl() {
    return midnightImpl;
  }

  public boolean isChangeDayAfterZi() {
    return changeDayAfterZi;
  }

  public boolean isTrueRisingSign() {
    return trueRisingSign;
  }

  public IFinalMonthNumber.MonthAlgo getMonthAlgo() {
    return monthAlgo;
  }

  public ChineseDate getChineseDate() {
    return chineseDateImpl.getChineseDate(lmt, loc, dayImpl, hourImpl, midnightImpl, changeDayAfterZi);
  }
}
