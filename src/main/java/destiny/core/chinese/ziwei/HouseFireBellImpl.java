/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.jooq.lambda.tuple.Tuple2;

/** (年支,時支) -> 地支
 * {@link StarUnlucky#火星} {@link StarUnlucky#鈴星} 專用
 * */
public abstract class HouseFireBellImpl extends HouseAbstractImpl<Tuple2<Branch , Branch>> {

  protected HouseFireBellImpl(ZStar star) {
    super(star);
  }

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH_HOUR_BRANCH;
  }

  /** 火星、鈴星 不走這條路 */
  @Override
  public Branch getBranch(Tuple2<Branch, Branch> objects) {
    throw new RuntimeException("error");
  }
}
