/**
 * Created by smallufo on 2015-05-18.
 */
package destiny.core.calendar.eightwords.onePalm;

import destiny.core.Descriptive;
import destiny.core.Gender;
import destiny.core.chinese.EarthlyBranches;

public interface PositiveIF extends Descriptive {

  boolean isPositive(Gender gender, EarthlyBranches yearBranch);
}
