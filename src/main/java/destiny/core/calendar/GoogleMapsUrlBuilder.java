/**
 * @author smallufo 
 * Created on 2007/7/25 at 上午 12:11:33
 */ 
package destiny.core.calendar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class GoogleMapsUrlBuilder implements LocationUrlBuilder , Serializable
{
  public GoogleMapsUrlBuilder()
  {
  }

  /**
   * 參考資料 
   * http://mapki.com/wiki/Google_Map_Parameters
   */
  @Nullable
  public URL getUrl(@NotNull Location location)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("http://maps.google.com/maps?");
    sb.append("ll=");
    //緯度
    sb.append(location.getLatitude());
    sb.append(",");
    //經度
    sb.append(location.getLongitude());
    sb.append("&");
    //spn 不知道是啥 , 先跳過
    sb.append("z=12");
    sb.append("&");
    sb.append("om=1");
    try
    {
      return new URL(sb.toString());
    }
    catch (MalformedURLException ignored)
    {
    }
    return null;
  }

}
