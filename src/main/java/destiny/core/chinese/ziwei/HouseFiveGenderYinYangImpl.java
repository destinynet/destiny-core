/**
 * Created by smallufo on 2017-04-19.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.*;
import kotlin.Pair;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;

import java.util.Optional;

import static destiny.core.chinese.Branch.寅;

/**
 * 長生 12 神煞
 *
 * 形容五行局在十二宮力量的強弱。這十二神煞影響著斗數十四顆主星的氣運。
 *
 * 重點是「五行局」， 而「五行局」又來自「命宮」
 * 但是「命宮」又有可能是依據上升星座而來，「可能」並非來自 陰曆 年份＋月份＋時辰來看
 * */
public abstract class HouseFiveGenderYinYangImpl extends HouseAbstractImpl<Tuple3<FiveElement , Gender, YinYangIF>> {

  HouseFiveGenderYinYangImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int finalMonthNumForMonthStars, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays,
                          Optional<Branch> predefinedMainHouse, ZContext context) {

    Stem stemOf寅 = IZiwei.getStemOf寅(context.getYearType() == ZContext.YearType.YEAR_LUNAR ? lunarYear.getStem() : solarYear.getStem());
    Branch mainHouse = predefinedMainHouse.orElse(IZiwei.getMainHouseBranch(finalMonthNumForMonthStars, hour));

    // 左下角，寅宮 的 干支
    StemBranch stemBranchOf寅 = StemBranch.get(stemOf寅 , 寅);
    int steps = mainHouse.getAheadOf(寅);
    StemBranch 命宮 = stemBranchOf寅.next(steps);

    //StemBranch 命宮 = IZiwei.getMainHouse(lunarYear.getStem() , finalMonthNumForMonthStars, hour);
    Pair<FiveElement , Integer> pair = IZiwei.getMainDesc(命宮);
    // 五行局數
    FiveElement fiveElement = pair.getFirst();
    return getBranch(Tuple.tuple(fiveElement , gender , lunarYear.getStem()));
  }

}
