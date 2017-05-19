/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

/**
 * 年干 + 性別
 * 被用於 {@link StarDoctor} : 博士12神煞
 * 必須判別 {@link ZContext.YearType}
 * 因為 博士12神煞 depend on {@link StarLucky#祿存} , 而 祿存 又是 年干 系星 ,
 * 錄存的 實作繼承 {@link HouseYearStemImpl} , 與 {@link ZContext.YearType} 相關
 * 故此博士十二神煞，也必須與祿存的年干設定相同
 */
public abstract class HouseYearStemGenderImpl extends HouseAbstractImpl<Tuple2<Stem, Gender>> {

  HouseYearStemGenderImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(StemBranch lunarYear, StemBranch solarYear, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, boolean leap, int prevMonthDays, ZContext context) {
    Stem yearStem = context.getYearType() == ZContext.YearType.YEAR_LUNAR ? lunarYear.getStem() : solarYear.getStem();
    return getBranch(Tuple.tuple(yearStem, gender));
  }
}
