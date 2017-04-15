/**
 * Created by smallufo on 2015-05-25.
 */
package destiny.core.chinese;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.core.Descriptive;
import destiny.core.calendar.Location;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 天乙貴人
 * CU Draconis (CU Dra / 10 Draconis / HD 121130)
 * 天龍座10，又名天龍座CU
 * */
public interface TianyiIF extends Descriptive {

  /**
   * 取得天干的天乙貴人、分晝夜
   */
  Branch getFirstTianyi(Stem stem, YinYangIF yinYangIF);

  /** 取得天干對應的天乙貴人，不分晝夜，一起傳回來 */
  default List<Branch> getTianyis(Stem stem) {
    return Arrays.stream(DayNight.values())
      .map(dayNight -> getFirstTianyi(stem, dayNight))
      .collect(Collectors.toList());
  }

  default Branch getTianyi(LocalDateTime lmt, Location loc, DayIF dayImpl, MidnightIF midnightImpl, HourIF hourImpl, boolean changeDayAfterZi, DayNightDifferentiator differentiator) {
    StemBranch day = dayImpl.getDay(lmt, loc, midnightImpl, hourImpl, changeDayAfterZi);
    DayNight dayNight = differentiator.getDayNight(lmt , loc);
    return getFirstTianyi(day.getStem(), dayNight);
  }
}
