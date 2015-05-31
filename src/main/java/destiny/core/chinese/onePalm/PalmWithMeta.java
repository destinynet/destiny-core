/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.onePalm;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;

import java.io.Serializable;

public class PalmWithMeta implements Serializable {

  private final Palm palm;

  private final Time lmt;

  private final Location loc;

  private final String place;

  private final ChineseDateIF chineseDateImpl;

  private final DayIF dayImpl;

  private final PositiveIF positiveImpl;

  private final HourIF hourImpl;

  private final MidnightIF midnightImpl;

  private final boolean changeDayAfterZi;

  public PalmWithMeta(Palm palm, Time lmt, Location loc, String place, ChineseDateIF chineseDateImpl, DayIF dayImpl, PositiveIF positiveImpl, HourIF impl, MidnightIF midnightImpl, boolean changeDayAfterZi) {
    this.palm = palm;
    this.lmt = lmt;
    this.loc = loc;
    this.place = place;
    this.chineseDateImpl = chineseDateImpl;
    this.dayImpl = dayImpl;
    this.positiveImpl = positiveImpl;
    hourImpl = impl;
    this.midnightImpl = midnightImpl;
    this.changeDayAfterZi = changeDayAfterZi;
  }

  public Palm getPalm() {
    return palm;
  }

  public Time getLmt() {
    return lmt;
  }

  public Location getLoc() {
    return loc;
  }

  public String getPlace() {
    return place;
  }

  public ChineseDateIF getChineseDateImpl() {
    return chineseDateImpl;
  }

  public DayIF getDayImpl() {
    return dayImpl;
  }

  public PositiveIF getPositiveImpl() {
    return positiveImpl;
  }

  public HourIF getHourImpl() {
    return hourImpl;
  }

  public MidnightIF getMidnightImpl() {
    return midnightImpl;
  }

  public boolean isChangeDayAfterZi() {
    return changeDayAfterZi;
  }

  public ChineseDate getChineseDate() {
    return chineseDateImpl.getChineseDate(lmt, loc, dayImpl, hourImpl, midnightImpl, changeDayAfterZi);
  }
}
