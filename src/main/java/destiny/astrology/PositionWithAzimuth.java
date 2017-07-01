/**
 * @author smallufo
 * Created on 2007/5/22 at 上午 6:33:11
 */
package destiny.astrology;

import org.jetbrains.annotations.NotNull;

/**
 * 星體的位置，加上地平方位角
 */
public class PositionWithAzimuth extends Position {

  private Azimuth azimuth;

  public PositionWithAzimuth(@NotNull Position position, Azimuth azimuth) {
    super(position.getLongitude(), position.getLatitude(), position.getDistance(), position.getSpeedLongitude(), position.getSpeedLatitude(), position.getSpeedDistance());
    this.azimuth = azimuth;
  }


  public Azimuth getAzimuth() {
    return azimuth;
  }

//  public void setAzimuth(Azimuth azimuth) {
//    this.azimuth = azimuth;
//  }

}
