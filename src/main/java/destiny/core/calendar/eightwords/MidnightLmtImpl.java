/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 01:53:20
 */
package destiny.core.calendar.eightwords;

import java.io.Serializable;
import java.util.Locale;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** 
 * 純粹以地方平均時（手錶時間）來判定 
 */
public class MidnightLmtImpl implements MidnightIF , Serializable
{
  @NotNull
  public Time getNextMidnight(@Nullable Time lmt, @Nullable Location location)
  {
    if (lmt == null || location == null)
      throw new RuntimeException("lmt and location cannot be null !");
    
    return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 0 , 0 , 0);
  }

  @NotNull
  public String getTitle(Locale locale)
  {
    return "以地方平均時（ LMT）來判定";
  }

  @NotNull
  public String getDescription(Locale locale)
  {
    return "晚上零時就是子正，不校正經度差以及真太陽時";
  }

}
