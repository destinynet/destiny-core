/**
 * @author smallufo 
 * Created on 2006/6/30 at 下午 11:13:01
 */ 
package destiny.core.calendar.chinese;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;

/**
 * 從 Time(LMT) / Location 取得 ChineseDate
 */
public interface ChineseDateIF
{
  @NotNull
  public ChineseDate getChineseDate(Time lmt , Location location);
}
