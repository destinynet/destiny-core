/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.Location;
import destiny.core.chinese.Branch;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 傳統紫微計算命宮
 */
public class MainHouseTradImpl implements IMainHouse , Serializable {

  @Override
  public Branch getMainHouse(LocalDateTime lmt, Location loc) {
    return null;
  }
}
