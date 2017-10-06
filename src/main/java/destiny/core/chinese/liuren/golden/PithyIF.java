/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren.golden;

import destiny.astrology.DayNight;
import destiny.astrology.DayNightDifferentiator;
import destiny.core.calendar.Location;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.calendar.eightwords.EightWordsIF;
import destiny.core.chinese.*;
import destiny.core.chinese.liuren.General;
import destiny.core.chinese.liuren.GeneralSeqIF;
import destiny.core.chinese.liuren.GeneralStemBranchIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.chrono.ChronoLocalDateTime;

public interface PithyIF {

  Logger logger = LoggerFactory.getLogger(PithyIF.class);

  default Pithy getPithy(Branch direction, EightWords ew, Branch 月將, TianyiIF tianyiImpl, DayNight dayNight,
                         Clockwise clockwise, GeneralSeqIF seq , GeneralStemBranchIF generalStemBranchImpl) {

    // 天乙貴人(起點)
    Branch 天乙貴人 = tianyiImpl.getFirstTianyi(ew.getDayStem() , dayNight);

    int steps = 0;
    switch (clockwise) {
      case CLOCKWISE: steps = direction.getAheadOf(天乙貴人); break;
      case COUNTER  : steps = 天乙貴人.getAheadOf(direction); break;
    }

    logger.debug("天乙貴人 (日干 {} + {} ) = {} . 地分 = {} , 順逆 = {} , steps = {}" , ew.getDayStem() , dayNight , 天乙貴人 , direction , clockwise , steps);

    // 貴神
    Branch 貴神地支 = General.貴人.next(steps , seq).getStemBranch(generalStemBranchImpl).getBranch();
    Stem 貴神天干 = StemBranchUtils.getHourStem(ew.getDayStem() , 貴神地支);
    logger.debug("推導貴神，從 {} 開始走 {} 步，得到 {} , 地支為 {} , 天干為 {}" , General.貴人 , steps , General.貴人.next(steps , seq) , 貴神地支 , 貴神天干);
    StemBranch 貴神 = StemBranch.get(貴神天干 , 貴神地支);

    return new Pithy(ew , direction, 月將 , dayNight, 貴神);
  }

  default Pithy getPithy(Branch direction, ChronoLocalDateTime lmt, Location loc, MonthMasterIF monthBranchImpl,
                         DayNightDifferentiator dayNightImpl, TianyiIF tianyiImpl, ClockwiseIF clockwiseImpl,
                         GeneralSeqIF seq, GeneralStemBranchIF generalStemBranchImpl, EightWordsIF eightWordsImpl) {
    EightWords ew = eightWordsImpl.getEightWords(lmt , loc);

    Branch 月將 = monthBranchImpl.getBranch(lmt , loc);
    logger.debug("月將 = {}" , 月將);

    Clockwise clockwise = clockwiseImpl.getClockwise(lmt , loc) ;

    DayNight dayNight = dayNightImpl.getDayNight(lmt , loc);

    return getPithy(direction, ew, 月將, tianyiImpl, dayNight, clockwise, seq , generalStemBranchImpl);
  }

}
