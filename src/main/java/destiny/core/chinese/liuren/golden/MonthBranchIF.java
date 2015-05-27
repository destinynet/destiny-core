/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.liuren.golden;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.Branch;

/** 取「月將」 (不是月令干支！) */
public interface MonthBranchIF {

  /** 取得「月將」的方法 */
  Branch getBranch(Time lmt , Location location);
}
