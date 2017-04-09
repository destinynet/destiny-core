/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.Location;

import java.time.LocalDateTime;

/** 紫微斗數 */
public interface ZiweiIF {

  void calculate(Gender gender , LocalDateTime time , Location loc);
}
