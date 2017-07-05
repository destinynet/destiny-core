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
    super(position.getLng(), position.getLat(), position.getDistance(), position.getSpeedLng(), position.getSpeedLat(), position.getSpeedDistance());
    this.azimuth = azimuth;
  }

  public Azimuth getAzimuth() {
    return azimuth;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("[PositionWithAzimuth " + "lng=").append(lng).append(", lat=").append(lat).append(", distance=").append(distance).append(", speedLng=").append(speedLng).append(", speedLat=").append(speedLat).append(", speedDistance=").append(speedDistance);
    sb.append(" , azimuth=").append(azimuth).append(']');

    return sb.toString();
  }
}
