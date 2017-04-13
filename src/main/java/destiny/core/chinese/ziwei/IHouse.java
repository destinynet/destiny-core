/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;

public interface  IHouse<T> {

  FuncType getFuncType();

  Branch getBranch(T t);

  Branch getBranch(Stem yearStem, Branch yearBranch, Branch monthBranch, int monthNum, int days, Branch hour, int set);
}
