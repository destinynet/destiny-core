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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;

/**
 * 以太陽過天底的時間來決定『子正』
 */
public class MidnightSolarTransImpl implements MidnightIF , Serializable {

  private Logger logger = LoggerFactory.getLogger(getClass());
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

  /** 以太陽過當地天底的時間來決定 「子正」 , 回傳 GMT 時刻 */
  @Override
  public double getNextMidnight(double gmtJulDay, @NotNull Location location) {
    Time gmtResult = riseTransImpl.getGmtTransTime(gmtJulDay , Planet.SUN , TransPoint.NADIR , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);

    return gmtResult.getGmtJulDay();
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

  @NotNull
  public String getTitle(Locale locale)
  {
    return "太陽過天底";
  }

  @NotNull
  public String getDescription(Locale locale)
  {
    return "以太陽過當地『天底』的時刻為『子正』";
  }
}
