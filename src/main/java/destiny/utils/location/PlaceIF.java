/** 2009/11/26 上午10:38:15 by smallufo */
package destiny.utils.location;

import java.io.IOException;

import destiny.utils.Tuple;

/** 從地名尋找經緯度 */
public interface PlaceIF
{
  public Tuple<Double , Double> getLongLat(String placeName) throws IOException;
}

