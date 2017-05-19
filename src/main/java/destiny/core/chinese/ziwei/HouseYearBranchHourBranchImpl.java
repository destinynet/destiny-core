/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.jooq.lambda.tuple.Tuple2;

/**
 *
 * (年支,時支) -> 地支
 *
 * {@link HouseFunctions#house火星} {@link HouseFunctions#house鈴星}  使用
 * */
public abstract class HouseYearBranchHourBranchImpl extends HouseAbstractImpl<Tuple2<Branch , Branch>> {

  HouseYearBranchHourBranchImpl(ZStar star) {
    super(star);
  }

  @Override
  public Branch getBranch(Tuple2<Branch, Branch> objects) {
    throw new RuntimeException("error : " + objects);
  }


}
