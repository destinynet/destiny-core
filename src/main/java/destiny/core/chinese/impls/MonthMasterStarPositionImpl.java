/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls;

import destiny.astrology.*;
import destiny.core.calendar.Location;
import destiny.core.chinese.Branch;
import destiny.core.chinese.MonthMasterIF;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;

public class MonthMasterStarPositionImpl implements MonthMasterIF , Serializable {

  private final StarPositionIF starPositionImpl;

  public MonthMasterStarPositionImpl(StarPositionIF starPositionImpl) {this.starPositionImpl = starPositionImpl;}

  @Override
  public String getTitle(Locale locale) {
    return "星體觀測（過中氣）";
  }

  @Override
  public String getDescription(Locale locale) {
    return "真實觀測太陽在黃道的度數，判斷月將（太陽星座）";
  }

  @Override
  public Branch getBranch(LocalDateTime lmt, Location location) {
    Position pos = starPositionImpl.getPosition(Planet.SUN , lmt , location , Centric.GEO , Coordinate.ECLIPTIC);
    return ZodiacSign.getZodiacSign(pos.getLongitude()).getBranch();
  }
}
