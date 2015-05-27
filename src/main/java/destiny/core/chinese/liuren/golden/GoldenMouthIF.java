/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren.golden;

import destiny.astrology.*;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.calendar.eightwords.EightWordsIF;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Clockwise;
import destiny.core.chinese.ClockwiseIF;
import destiny.core.chinese.TianyiIF;

public interface GoldenMouthIF {

  default GoldenMouth getGoldenMouth(Branch selected , Time lmt , Location loc , MonthBranchIF monthBranchImpl ,
                                     DayNightDifferentiator dayNightImpl ,
                                     TianyiIF tianyiImpl , ClockwiseIF clockwiseImpl ,
                                     EightWordsIF eightWordsImpl , StarPositionIF starPositionImpl ) {
    EightWords ew = eightWordsImpl.getEightWords(lmt , loc);

    ZodiacSign sign = ZodiacSign.getZodiacSign(starPositionImpl.getPosition(Planet.SUN, lmt, loc).getLongitude());
    Branch 月將 = monthBranchImpl.getBranch(lmt , loc);

    int direction = clockwiseImpl.getClockwise(lmt , loc) == Clockwise.CLOCKWISE ? 1 : -1;

    DayNight dayNight = dayNightImpl.getDayNight(lmt , loc);

    // 天乙貴人
    Branch 天乙貴人 = tianyiImpl.getFirstTianyi(ew.getDayStem() , dayNight);

    // 貴神
    Branch 貴神地支 = null;

    return new GoldenMouth(ew , selected , sign.getBranch() , null);

  }
}
