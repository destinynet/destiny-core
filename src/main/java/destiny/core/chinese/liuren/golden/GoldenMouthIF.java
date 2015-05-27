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

  default GoldenMouth getGoldenMouth(Branch direction, Time lmt , Location loc , MonthBranchIF monthBranchImpl ,
                                     DayNightDifferentiator dayNightImpl ,
                                     TianyiIF tianyiImpl , ClockwiseIF clockwiseImpl , GeneralSeqIF seq,
                                     EightWordsIF eightWordsImpl ) {
    EightWords ew = eightWordsImpl.getEightWords(lmt , loc);

    Branch 月將 = monthBranchImpl.getBranch(lmt , loc);
    System.out.println("月將 = " + 月將);

    Clockwise clockwise = clockwiseImpl.getClockwise(lmt , loc) ;

    DayNight dayNight = dayNightImpl.getDayNight(lmt , loc);

    // 天乙貴人(起點)
    Branch 天乙貴人 = tianyiImpl.getFirstTianyi(ew.getDayStem() , dayNight);

    int steps = 0;
    switch (clockwise) {
      case CLOCKWISE: steps = direction.getAheadOf(天乙貴人); break;
      case COUNTER  : steps = 天乙貴人.getAheadOf(direction); break;
    }

    System.out.println("天乙貴人 (日干"+ ew.getDayStem()+" + " + dayNight + " ) = " + 天乙貴人 + " , direction = " + direction + " , 順逆 = " + clockwise + " , 間隔 = " + steps);

    // 貴神
    Branch 貴神地支 = General.貴人.next(steps , seq).getStemBranch().getBranch();
    Stem 貴神天干 = StemBranchUtils.getHourStem(ew.getDayStem() , 貴神地支);
    System.out.println("從 " + General.貴人 + " 開始走 " + steps + "步 , 得到 : " + General.貴人.next(steps , seq) + " , 地支為 " + 貴神地支);
    StemBranch 貴神 = StemBranch.get(貴神天干 , 貴神地支);

    return new GoldenMouth(ew , direction, 月將 , 貴神);

  }
}
