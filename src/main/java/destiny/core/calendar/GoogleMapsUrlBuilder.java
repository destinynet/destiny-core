/**
 * @author smallufo 
 * Created on 2007/7/25 at 上午 12:11:33
 */ 
package destiny.core.calendar;

import java.io.Serializable;

public class GoogleMapsUrlBuilder implements LocationUrlBuilder , Serializable
{
  public GoogleMapsUrlBuilder()
  {
  }

  // http://maps.google.com/maps?&z=10&q=25.039059+121.517675&ll=25.039059+121.517675
  @Override
  public String getUrl(double lat, double lng) {
    return String.format("http://maps.google.com/maps?&z=10&q=%f+%f&ll=%f+%f&z=14" , lat , lng , lat , lng);
  }

//  @Nullable
//  public URL getUrl(@NotNull Location location)
//  {
//    StringBuilder sb = new StringBuilder();
//    sb.append("http://maps.google.com/maps?");
//    sb.append("ll=");
//    //緯度
//    sb.append(location.getLatitude());
//    sb.append(",");
//    //經度
//    sb.append(location.getLongitude());
//    sb.append("&");
//    //spn 不知道是啥 , 先跳過
//    sb.append("z=12");
//    sb.append("&");
//    sb.append("om=1");
//    try
//    {
//      return new URL(sb.toString());
//    }
//    catch (MalformedURLException ignored)
//    {
//    }
//    return null;
//  }

}
