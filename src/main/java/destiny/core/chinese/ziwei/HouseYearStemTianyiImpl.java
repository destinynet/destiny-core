/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.ImmutableMap;
import destiny.core.Gender;
import destiny.core.calendar.SolarTerms;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import destiny.core.chinese.TianyiIF;
import destiny.core.chinese.impls.TianyiAuthorizedImpl;
import destiny.core.chinese.impls.TianyiLiurenPithyImpl;
import destiny.core.chinese.impls.TianyiOceanImpl;
import destiny.core.chinese.impls.TianyiZiweiBookImpl;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Map;

/**
 * (年干,天乙貴人設定) -> 地支
 * 適用於 {@link StarLucky#天魁} (陽貴人) , {@link StarLucky#天鉞} (陰貴人)
 */
public abstract class HouseYearStemTianyiImpl extends HouseAbstractImpl<Tuple2<Stem, TianyiIF>> {

  private final Map<Settings.Tianyi, TianyiIF> map = new ImmutableMap.Builder<Settings.Tianyi, TianyiIF>()
    .put(Settings.Tianyi.TIANYI_ZIWEI_BOOK, new TianyiZiweiBookImpl())
    .put(Settings.Tianyi.TIANYI_AUTHORIZED, new TianyiAuthorizedImpl())
    .put(Settings.Tianyi.TIANYI_OCEAN, new TianyiOceanImpl())
    .put(Settings.Tianyi.TIANYI_LIUREN_PITHY, new TianyiLiurenPithyImpl())
    .build();

  protected HouseYearStemTianyiImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_STEM;
  }

  @Override
  public Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, SolarTerms solarTerms, int days, Branch hour, int set, Gender gender, Settings settings) {
    return getBranch(Tuple.tuple(year.getStem() , map.get(settings.getTianyi())));
  }
}
