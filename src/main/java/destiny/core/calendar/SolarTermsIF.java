/*
 * @author smallufo
 * @date 2005/3/28
 * @time 下午 07:22:50
 */
package destiny.core.calendar;


/**
 * 計算節氣的介面
 */
public interface SolarTermsIF 
{
  /** 計算某時刻當下的節氣 */
  public SolarTerms getSolarTermsFromGMT(Time gmt);
}
