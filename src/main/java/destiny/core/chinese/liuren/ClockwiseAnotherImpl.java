/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.chinese.Clockwise;
import destiny.core.chinese.ClockwiseIF;
import destiny.core.chinese.StemBranch;

import java.io.Serializable;
import java.util.Locale;

/**
 * 推算貴神
 * 甲乙丙丁戊己庚 皆為晝順夜逆； 辛壬癸 為晝逆夜順
 */
public class ClockwiseAnotherImpl implements ClockwiseIF, Serializable {

  private final DayIF dayImpl;

  private final MidnightIF midnightImpl;

  private final HourIF hourImpl;

  private final boolean changeDayAfterZi;

  private final DayNightDifferentiator differentiator;

  public ClockwiseAnotherImpl(DayIF dayImpl, MidnightIF midnightImpl, HourIF impl, boolean changeDayAfterZi, DayNightDifferentiator differentiator) {
    this.dayImpl = dayImpl;
    this.midnightImpl = midnightImpl;
    hourImpl = impl;
    this.changeDayAfterZi = changeDayAfterZi;
    this.differentiator = differentiator;
  }

  @Override
  public Clockwise getClockwise(Time lmt, Location loc) {
    StemBranch day = dayImpl.getDay(lmt, loc, midnightImpl, hourImpl, changeDayAfterZi);
    DayNight dayNight = differentiator.getDayNight(lmt, loc);
    switch (day.getStem()) {
      case 甲:
      case 乙:
      case 丙:
      case 丁:
      case 戊:
      case 己:
      case 庚:
        return (dayNight == DayNight.DAY ? Clockwise.CLOCKWISE : Clockwise.COUNTER);
      case 辛:
      case 壬:
      case 癸:
        return (dayNight == DayNight.DAY ? Clockwise.COUNTER : Clockwise.CLOCKWISE);
    }
    return null;
  }

  @Override
  public String getTitle(Locale locale) {
    return "辛壬癸 晝逆夜順";
  }

  @Override
  public String getDescription(Locale locale) {
    return "甲乙丙丁戊己庚 皆為晝順夜逆； 辛壬癸 為晝逆夜順";
  }
}
