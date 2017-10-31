/**
 * Created by smallufo on 2017-10-27.
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.*;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.TimeTools;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.StemBranchUtils;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;

public class EightWordsDetailImpl implements IEightWordsDetail , Serializable {

  @Override
  public EightWordsContextModel getDetails(ChronoLocalDateTime lmt, Location location, String place , EightWordsIF eightWordsImpl, YearMonthIF yearMonthImpl, ChineseDateIF chineseDateImpl, DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi, IRisingSign risingSignImpl, IStarPosition starPositionImpl , SolarTermsIF solarTermsImpl) {
    double gmtJulDay = TimeTools.getGmtJulDay(lmt , location);

    // 現在的節氣
    SolarTerms currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay);
    EightWords eightWords = eightWordsImpl.getEightWords(lmt , location);
    ChineseDate chineseDate = chineseDateImpl.getChineseDate(lmt, location, dayImpl, hourImpl, midnightImpl, changeDayAfterZi);

    Tuple2<SolarTerms , SolarTerms> prevNextMajorSolarTerms = getPrevNextMajorSolarTerms(currentSolarTerms);

    StemBranch risingSign = getRisingStemBranch(lmt , location , eightWords , risingSignImpl);

    Branch sunBranch = getBranchOf(Planet.SUN , lmt , location , starPositionImpl);
    Branch moonBranch = getBranchOf(Planet.MOON , lmt , location , starPositionImpl);

    return new EightWordsContextModel(eightWords, lmt , location, place , chineseDate ,
      prevNextMajorSolarTerms.v1() , prevNextMajorSolarTerms.v2() , risingSign ,
      sunBranch , moonBranch);
  }

  /** 上一個「節」、下一個「節」
   * */
  private Tuple2<SolarTerms , SolarTerms> getPrevNextMajorSolarTerms(SolarTerms currentSolarTerms) {
    int currentSolarTermsIndex = SolarTerms.getIndex(currentSolarTerms);
    SolarTerms prevMajorSolarTerms;
    SolarTerms nextMajorSolarTerms;
    if (currentSolarTermsIndex % 2 == 0)  //立春 , 驚蟄 , 清明 ...
    {
      prevMajorSolarTerms = currentSolarTerms;
      nextMajorSolarTerms = currentSolarTerms.next().next();
    }
    else
    {
      prevMajorSolarTerms = currentSolarTerms.previous();
      nextMajorSolarTerms = currentSolarTerms.next();
    }
    return Tuple.tuple(prevMajorSolarTerms , nextMajorSolarTerms);
  }

  /**
   * 計算命宮干支
   */
  private StemBranch getRisingStemBranch(ChronoLocalDateTime lmt , Location location , EightWords eightWords , IRisingSign risingSignImpl ) {
    // 命宮地支
    Branch risingBranch = risingSignImpl.getRisingSign(lmt, location , HouseSystem.PLACIDUS , Coordinate.ECLIPTIC).getBranch();
    // 命宮天干：利用「五虎遁」起月 => 年干 + 命宮地支（當作月份），算出命宮的天干
    Stem risingStem = StemBranchUtils.getMonthStem(eightWords.getYearStem(), risingBranch);
    // 組合成干支
    return StemBranch.get(risingStem, risingBranch);
  }

  /**
   * @return 計算星體的地支位置
   */
  private Branch getBranchOf(Star star , ChronoLocalDateTime lmt , Location location , IStarPosition starPositionImpl) {
    Position pos = starPositionImpl.getPosition(star , lmt , location , Centric.GEO ,Coordinate.ECLIPTIC);
    return ZodiacSign.getZodiacSign(pos.getLng()).getBranch();
  }
}
