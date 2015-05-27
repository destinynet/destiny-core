/*
 * @author smallufo
 * @date 2004/11/25
 * @time 下午 07:45:47
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;

/**
 * 取得月干支的介面
 */
public interface MonthIF
{
  /**
   * @param lmt 傳入當地的手錶時間
   * @param location 當地的經緯度等資料
   * @return 月干支
   */
  @NotNull
  StemBranch getMonth(Time lmt , Location location);
  
  /**
   * 南半球月支是否對沖 , 內定是 '否'
   */
  void setSouthernHemisphereOpposition(boolean value);
}
