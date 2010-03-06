/** 2009/11/27 下午7:20:26 by smallufo */
package destiny.utils.location;

import java.io.IOException;
import java.util.Locale;

/**
 * 從經緯度尋找附近的地名
 */
public interface NearbyLocationIF
{
  public String getNearbyLocation(double longitude , double latitude , Locale locale) throws IOException;
}

