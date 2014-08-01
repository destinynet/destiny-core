/**
 * @author smallufo 
 * Created on 2007/7/25 at 上午 12:09:59
 */ 
package destiny.core.calendar;

import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * 從 Location 傳回網址，可用於 Google Maps 或是 Yahoo Maps
 */
public interface LocationUrlBuilder
{
  @Nullable
  public URL getUrl(Location location);
}
