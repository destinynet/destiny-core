/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

public interface  IHouse<T> {

  ZStar getStar();

  FuncType getFuncType();

  Branch getBranch(T t);

  Branch getBranch(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, int set, Gender gender, Settings settings);
}
