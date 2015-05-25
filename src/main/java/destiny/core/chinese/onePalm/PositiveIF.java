/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.chinese.onePalm;

import destiny.core.Descriptive;
import destiny.core.Gender;
import destiny.core.chinese.Branch;

public interface PositiveIF extends Descriptive {

  boolean isPositive(Gender gender, Branch yearBranch);
}
