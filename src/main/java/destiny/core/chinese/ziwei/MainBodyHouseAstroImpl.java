/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei;

import destiny.astrology.*;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.eightwords.RisingSignIF;
import destiny.core.chinese.Branch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 利用上升星座，計算命宮
 * 利用月亮星座，計算身宮
 */
public class MainBodyHouseAstroImpl implements IMainBodyHouse, Serializable {

  private final RisingSignIF risingSignImpl;

  private final StarPositionIF starPositionImpl;

  public MainBodyHouseAstroImpl(RisingSignIF risingSignImpl, StarPositionIF starPositionImpl) {
    this.risingSignImpl = risingSignImpl;
    this.starPositionImpl = starPositionImpl;
  }

  @Override
  public Tuple2<Branch , Branch> getMainBodyHouse(LocalDateTime lmt, Location loc) {
    Branch mainHouse = risingSignImpl.getRisingSign(lmt , loc , HouseSystem.PLACIDUS , Coordinate.ECLIPTIC).getBranch();
    Position moonPos = starPositionImpl.getPosition(Planet.MOON , lmt , loc , Centric.GEO , Coordinate.ECLIPTIC);
    Branch bodyHouse = SolarTerms.getFromDegree(moonPos.getLongitude()).getBranch();

    return Tuple.tuple(mainHouse , bodyHouse);
  }
}
