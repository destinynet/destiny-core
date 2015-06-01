/**
 * @author smallufo 
 * Created on 2008/7/26 at 上午 2:48:02
 */ 
package destiny.core.calendar;

/** 具備 時(Hour) , 分(Minute) , 秒 (Second) 的資料結構的介面 */
public interface HmsIF
{
  int getHour();
  
  int getMinute();
  
  double getSecond();
}
