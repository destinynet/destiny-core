/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren.golden;

import destiny.astrology.*;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.calendar.eightwords.EightWordsIF;
import destiny.core.chinese.*;
import destiny.core.chinese.liuren.General;
import destiny.core.chinese.liuren.GeneralSeqIF;

public interface GoldenMouthIF {

  default GoldenMouth getGoldenMouth(Branch 地分, Time lmt , Location loc , MonthBranchIF monthBranchImpl ,
                                     DayNightDifferentiator dayNightImpl ,
                                     TianyiIF tianyiImpl , ClockwiseIF clockwiseImpl , GeneralSeqIF seq,
                                     EightWordsIF eightWordsImpl , StarPositionIF starPositionImpl ) {
    EightWords ew = eightWordsImpl.getEightWords(lmt , loc);

    ZodiacSign sign = ZodiacSign.getZodiacSign(starPositionImpl.getPosition(Planet.SUN, lmt, loc).getLongitude());
    Branch 月將 = monthBranchImpl.getBranch(lmt , loc);

    Clockwise clockwise = clockwiseImpl.getClockwise(lmt , loc) ;
    int direction = clockwiseImpl.getClockwise(lmt , loc) == Clockwise.CLOCKWISE ? 1 : -1;

    DayNight dayNight = dayNightImpl.getDayNight(lmt , loc);

    // 天乙貴人(起點)
    Branch 天乙貴人 = tianyiImpl.getFirstTianyi(ew.getDayStem() , dayNight);

    int gap = 0;
    switch (clockwise) {
      case CLOCKWISE: gap = 天乙貴人.getAheadOf(地分); break;
      case COUNTER  : gap = 地分.getAheadOf(天乙貴人); break;
    }

    // 貴神
    Branch 貴神地支 = General.貴人.next(gap , seq).getStemBranch().getBranch();
    Stem 貴神天干 = StemBranchUtils.getHourStem(ew.getDayStem() , 貴神地支);
    StemBranch 貴神 = StemBranch.get(貴神天干 , 貴神地支);

    return new GoldenMouth(ew , 地分, sign.getBranch() , 貴神);

  }
}
