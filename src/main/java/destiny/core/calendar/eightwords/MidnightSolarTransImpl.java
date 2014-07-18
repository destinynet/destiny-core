/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 02:05:04
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.Planet;
import destiny.astrology.RiseTransIF;
import destiny.astrology.TransPoint;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;

/**
 * 以太陽過天底的時間來決定『子正』
 */
public class MidnightSolarTransImpl implements MidnightIF , Serializable
{
  private double atmosphericPressure = 1013.25;
  private double atmosphericTemperature = 0;
  private boolean isDiscCenter = true;
  private boolean hasRefraction = true;

  @Inject
  private RiseTransIF riseTransImpl;

  protected MidnightSolarTransImpl() {
  }
  
  public MidnightSolarTransImpl(RiseTransIF riseTransImpl)
  {
    this.riseTransImpl = riseTransImpl;
  }
  
  /** 以太陽過當地天底的時間來決定 「子正」 */
  public Time getNextMidnight(Time lmt, Location location)
  {
    if (lmt == null || location == null)
      throw new RuntimeException("lmt and location cannot be null !");
    
    /** 太陽過當地天底 (NADIR) 的時間 */ 
    Time gmt = Time.getGMTfromLMT(lmt, location);
    Time gmtResult = riseTransImpl.getGmtTransTime(gmt , Planet.SUN , TransPoint.NADIR , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    return Time.getLMTfromGMT(gmtResult, location);
  }

  /**
   * @param atmosphericPressure 大氣壓力 in mPar，內定 1013.25 
   */
  public void setAtmosphericPressure(double atmosphericPressure)
  {
    this.atmosphericPressure = atmosphericPressure;
  }
  /**
   * @param atmosphericTemperature 攝氏溫度，內定 0 度
   */
  public void setAtmosphericTemperature(double atmosphericTemperature)
  {
    this.atmosphericTemperature = atmosphericTemperature;
  }
  /**
   * @param hasRefraction 是否考慮大氣折射（濛氣差）
   */
  public void setHasRefraction(boolean hasRefraction)
  {
    this.hasRefraction = hasRefraction;
  }
  /**
   * @param isDiscCenter 是否以星體中心點來計算（影響：日、月）
   */
  public void setDiscCenter(boolean isDiscCenter)
  {
    this.isDiscCenter = isDiscCenter;
  }

  public String getTitle(Locale locale)
  {
    return "太陽過天底";
  }

  public String getDescription(Locale locale)
  {
    return "以太陽過當地『天底』的時刻為『子正』";
  }
}
