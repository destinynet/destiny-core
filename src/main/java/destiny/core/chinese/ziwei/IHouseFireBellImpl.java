/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import org.jooq.lambda.tuple.Tuple2;

/** (年支,時支,設定) -> 地支
 * {@link UnluckyStar#火星} {@link UnluckyStar#鈴星} 專用
 * */
public abstract class IHouseFireBellImpl implements IHouse<Tuple2<Branch , Branch>> {

  @Override
  public FuncType getFuncType() {
    return FuncType.YEAR_BRANCH_HOUR_BRANCH;
  }

  @Override
  public Branch getBranch(Tuple2<Branch, Branch> objects) {
    throw new RuntimeException("error");
  }
}
