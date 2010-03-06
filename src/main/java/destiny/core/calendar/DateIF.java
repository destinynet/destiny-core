/**
 * @author smallufo 
 * Created on 2008/7/26 at 上午 2:29:14
 */ 
package destiny.core.calendar;

/** 具備 年 , 月 , 日 的介面 */
public interface DateIF
{
  /** 是否西元後 */
  public boolean isAd();
  
  public int getYear();
  
  public int getMonth();
  
  public int getDay();
}
