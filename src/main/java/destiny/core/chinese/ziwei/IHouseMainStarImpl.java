/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import static destiny.core.chinese.ziwei.FuncType.SET_DAY_NUM;

/**
 * 14 顆主星
 * (局數,生日)
 */
public abstract class IHouseMainStarImpl implements IHouse<Tuple2<Integer, Integer>> {

  @Override
  public FuncType getFuncType() {
    return SET_DAY_NUM;
  }

  @Override
  public Branch getBranch(Stem yearStem, Branch yearBranch, Branch monthBranch, int monthNum, int days, Branch hour, int set) {
    return getBranch(Tuple.tuple(set , days));
  }
}
