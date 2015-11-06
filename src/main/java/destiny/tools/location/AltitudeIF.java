/** 2009/12/7 下午6:24:26 by smallufo */
package destiny.tools.location;

import java.io.IOException;

public interface AltitudeIF
{
  /** 從經緯度取得高度 */
  int getAltitude(double longitude , double latitude) throws IOException;
}

