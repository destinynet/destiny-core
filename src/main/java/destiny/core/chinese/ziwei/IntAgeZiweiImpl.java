/**
 * Created by smallufo on 2017-10-21.
 */
package destiny.core.chinese.ziwei;

import destiny.astrology.Planet;
import destiny.astrology.RelativeTransitIF;
import destiny.core.Gender;
import destiny.core.IntAge;
import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


/**
 * 紫微斗數虛歲
 * 出生當下，即為一歲。（故， age 不可以 <= 0）
 * 「一歲」終止於「順推」的「陰曆一月一日」
 */
public class IntAgeZiweiImpl implements IntAge , Serializable {

  private final ChineseDateIF chineseDateImpl;

  private final RelativeTransitIF relativeTransitImpl;

  private final static Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl::getLocalDateTimeStatic;

  private Logger logger = LoggerFactory.getLogger(getClass());

  public IntAgeZiweiImpl(ChineseDateIF chineseDateImpl, RelativeTransitIF relativeTransitImpl) {
    this.chineseDateImpl = chineseDateImpl;
    this.relativeTransitImpl = relativeTransitImpl;}

  @Override
  public Tuple2<Double, Double> getRange(Gender gender, double gmtJulDay, Location loc, int age) {

    Tuple2<Double , Double> age1 = Tuple.tuple(gmtJulDay , getNextYearSunMoonConj(gmtJulDay));
    return getRangeInner(age1 , age);
  }

  private Tuple2<Double , Double> getRangeInner(Tuple2<Double , Double> prevResult , int count) {
    if (count == 1) {
      return prevResult;
    } else {
      double newStart = prevResult.v2();
      double newEnd = getNextYearSunMoonConj(prevResult.v2()+2);
      return getRangeInner(Tuple.tuple(newStart , newEnd) , count-1);
    }
  }


  private double getNextYearSunMoonConj(double gmtJulDay) {
    ChronoLocalDateTime dateTime = revJulDayFunc.apply(gmtJulDay);

    // 陰曆日期
    ChineseDate chineseDate = chineseDateImpl.getChineseDate(dateTime.toLocalDate());

    Tuple2<Integer , StemBranch> next1Year = getNextYear(chineseDate.getCycle() , chineseDate.getYear());
    ChineseDate next1YearJan2 = new ChineseDate(next1Year.v1() , next1Year.v2() , 1 , false , 2);
    // 利用「隔年、陰曆、一月二日、中午」作為「逆推」日月合朔的時間點
    ChronoLocalDateTime next1YearJan2Time = chineseDateImpl.getYangDate(next1YearJan2).atTime(LocalTime.NOON);

    double next1YearJan2Gmt = TimeTools.getGmtJulDay(next1YearJan2Time);
    return relativeTransitImpl.getRelativeTransit(Planet.MOON , Planet.SUN , 0.0 , next1YearJan2Gmt , false)
      .orElseThrow(() -> new RuntimeException("Cannot get Sun/Moon Conj since julDay = " + next1YearJan2Gmt));
  }


  private Tuple2<Integer , StemBranch> getNextYear(int cycle , StemBranch year) {
    if (year.getIndex() == 59) {
      // 癸亥
      return Tuple.tuple(cycle + 1, year.next(1));
    } else {
      return Tuple.tuple(cycle , year.next(1));
    }
  }

  @Override
  public List<Tuple2<Double, Double>> getRanges(Gender gender, double gmtJulDay, Location loc, int fromAge, int toAge) {
    if (fromAge > toAge) {
      throw new RuntimeException("fromAge must be <= toAge");
    }
    Tuple2<Double , Double> from = getRange(gender , gmtJulDay , loc , fromAge);
    List<Tuple2<Double, Double>> result = new ArrayList<>((toAge - fromAge) + 1);
    result.add(from);
    return getRangesInner(result , (toAge - fromAge));
  }

  private List<Tuple2<Double , Double>> getRangesInner(List<Tuple2<Double , Double>> prevResults , int count) {
    if (count == 0) {
      return prevResults;
    } else {
      Tuple2<Double , Double> prevResult = prevResults.get(prevResults.size()-1);
      double stepDay = prevResult.v2()+1;

      double end = getNextYearSunMoonConj(stepDay);
      Tuple2<Double , Double> newResult = Tuple.tuple(prevResult.v2() , end);
      prevResults.add(newResult);
      return getRangesInner(prevResults , count-1);
    }
  }
}
