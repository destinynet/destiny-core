/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei;

import destiny.astrology.Coordinate;
import destiny.astrology.HouseSystem;
import destiny.core.calendar.Location;
import destiny.core.calendar.eightwords.RisingSignIF;
import destiny.core.chinese.Branch;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 利用上升星座，計算命宮
 */
public class MainHouseRisingImpl implements IMainHouse , Serializable {

  private final RisingSignIF risingSignImpl;

  public MainHouseRisingImpl(RisingSignIF risingSignImpl) {this.risingSignImpl = risingSignImpl;}

  @Override
  public Branch getMainHouse(LocalDateTime lmt, Location loc) {
    return risingSignImpl.getRisingSign(lmt , loc , HouseSystem.PLACIDUS , Coordinate.ECLIPTIC).getBranch();
  }
}
