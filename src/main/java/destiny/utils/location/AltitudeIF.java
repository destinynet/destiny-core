/** 2009/12/7 下午6:24:26 by smallufo */
package destiny.utils.location;

import java.io.IOException;

public interface AltitudeIF
{
  /** 從經緯度取得高度 */
  public int getAltitude(double longitude , double latitude) throws IOException;
}

