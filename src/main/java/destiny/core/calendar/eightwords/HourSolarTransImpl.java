/*
 * @author smallufo
 * @date 2004/12/10
 * @time 下午 03:53:08
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.Planet;
import destiny.astrology.RiseTransIF;
import destiny.astrology.TransPoint;
import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.chinese.Branch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Locale;

/**
 * <PRE>
 * 時辰的劃分實作
 * 利用太陽過天底 到天頂之間，劃分十二等份
 * 再從太陽過天頂到天底，平均劃分十二等份
 * 依此來切割 12 時辰
 * </PRE>
 */
public class HourSolarTransImpl implements HourIF , Serializable {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final double atmosphericPressure = 1013.25;
  private final double atmosphericTemperature = 0;
  private boolean isDiscCenter = true;
  private boolean hasRefraction = true;

  private final RiseTransIF riseTransImpl;
  
  public HourSolarTransImpl(RiseTransIF riseTransImpl)
  {
    this.riseTransImpl = riseTransImpl;
  }

  public void setDiscCenter(boolean isDiscCenter)
  {
    this.isDiscCenter = isDiscCenter;
  }

  public void setHasRefraction(boolean hasRefraction)
  {
    this.hasRefraction = hasRefraction;
  }

  @NotNull
  @Override
  public Branch getHour(double gmtJulDay, Location location) {

    double nextMeridian = riseTransImpl.getGmtTransJulDay(gmtJulDay , Planet.SUN , TransPoint.MERIDIAN , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    double nextNadir    = riseTransImpl.getGmtTransJulDay(gmtJulDay , Planet.SUN , TransPoint.NADIR    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);

    if (nextNadir > nextMeridian) {
      //子正到午正（上半天）
      double thirteenHoursAgo = gmtJulDay - (13/24.0);
      double previousNadirGmt = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo , Planet.SUN , TransPoint.NADIR , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);

      logger.debug("gmtJulDay = {} , 上一個子正(GMT) = {}" , gmtJulDay , JulDayResolver1582CutoverImpl.getLocalDateTimeStatic(previousNadirGmt));

      double diffDays = (nextMeridian - previousNadirGmt); // 從子正到午正，總共幾秒
      double oneUnitDays = diffDays/12.0;
      logger.debug("diffDays = {} , oneUnitDays = {}" , diffDays , oneUnitDays);
      if (gmtJulDay < previousNadirGmt + oneUnitDays)
        return Branch.子;
      else if (gmtJulDay < previousNadirGmt + oneUnitDays*3)
        return Branch.丑;
      else if (gmtJulDay < previousNadirGmt + oneUnitDays*5)
        return Branch.寅;
      else if (gmtJulDay < previousNadirGmt + oneUnitDays*7)
        return Branch.卯;
      else if (gmtJulDay < previousNadirGmt + oneUnitDays*9)
        return Branch.辰;
      else if (gmtJulDay < previousNadirGmt + oneUnitDays*11)
        return Branch.巳;
      else
        return Branch.午;
    } else {
      //午正到子正（下半天）
      double thirteenHoursAgo = gmtJulDay - (13/24.0);
      double previousMeridian = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo , Planet.SUN , TransPoint.MERIDIAN , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);

      double diffDays = (nextNadir - previousMeridian);
      double oneUnitDays = diffDays/(12.0);

      if (gmtJulDay < previousMeridian + oneUnitDays)
        return Branch.午;
      else if (gmtJulDay < previousMeridian + oneUnitDays*3)
        return Branch.未;
      else if (gmtJulDay < previousMeridian + oneUnitDays*5)
        return Branch.申;
      else if (gmtJulDay < previousMeridian + oneUnitDays*7)
        return Branch.酉;
      else if (gmtJulDay < previousMeridian + oneUnitDays*9)
        return Branch.戌;
      else if (gmtJulDay < previousMeridian + oneUnitDays*11)
        return Branch.亥;
      else
        return Branch.子;
    }
  }

  @Override
  public double getGmtNextStartOf(double gmtJulDay, Location location, Branch targetEb) {
    double resultGmt;
    double nextMeridianGmt = riseTransImpl.getGmtTransJulDay(gmtJulDay , Planet.SUN , TransPoint.MERIDIAN , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    double nextNadirGmt    = riseTransImpl.getGmtTransJulDay(gmtJulDay , Planet.SUN , TransPoint.NADIR    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);

    if (nextNadirGmt > nextMeridianGmt) {
      //LMT 位於子正到午正（上半天）
      double twelveHoursAgo = gmtJulDay-0.5;
      double previousNadir = riseTransImpl.getGmtTransJulDay(twelveHoursAgo , Planet.SUN , TransPoint.NADIR , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);

      double oneUnit1 = (nextMeridianGmt - previousNadir) / 12.0; // 單位為 day
      double oneUnit2 = (nextNadirGmt - nextMeridianGmt) / 12.0;

      Branch currentEb = getHour(gmtJulDay , location); // 取得目前在哪個時辰之中
      if (targetEb.getIndex() > currentEb.getIndex() || targetEb == Branch.子) {
        //代表現在所處的時辰，未超過欲求的時辰
        if (targetEb == Branch.丑 || targetEb == Branch.寅 || targetEb == Branch.卯 || targetEb == Branch.辰 || targetEb == Branch.巳 || targetEb == Branch.午) {
          resultGmt = previousNadir + oneUnit1 * ((targetEb.getIndex() - 1) * 2 + 1);
        }
        else if (targetEb == Branch.未 || targetEb == Branch.申 || targetEb == Branch.酉 || targetEb == Branch.戌 || targetEb == Branch.亥) {
          resultGmt = nextMeridianGmt + oneUnit2 * ((targetEb.getIndex() - 7) * 2 + 1);
        }
        else {
          resultGmt = nextMeridianGmt + oneUnit2 * 11; // eb == 子時
        }
      } else {
        //欲求的時辰，早於現在所處的時辰 ==> 代表算的是明天的時辰
        double nextNextMeridianGmt = riseTransImpl.getGmtTransJulDay(nextNadirGmt , Planet.SUN , TransPoint.MERIDIAN , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
        double oneUnit3 = (nextNextMeridianGmt - nextNadirGmt) / 12.0;
        double nextNextNadir = riseTransImpl.getGmtTransJulDay(nextNextMeridianGmt, Planet.SUN, TransPoint.NADIR, location, atmosphericPressure, atmosphericTemperature, isDiscCenter, hasRefraction);
        double oneUnit4 = (nextNextNadir - nextNextMeridianGmt) / 12.0;
        if (targetEb == Branch.丑 || targetEb == Branch.寅 || targetEb == Branch.卯 || targetEb == Branch.辰 || targetEb == Branch.巳 ||targetEb == Branch.午 ) {
          resultGmt = nextNadirGmt + oneUnit3 * ((targetEb.getIndex() - 1) * 2 + 1);
        } else if (targetEb == Branch.未 || targetEb == Branch.申 || targetEb == Branch.酉 || targetEb == Branch.戌 || targetEb == Branch.亥 ) {
          resultGmt = nextNextMeridianGmt + oneUnit4 * ((targetEb.getIndex()-7)*2+1);
        } else {
          throw new RuntimeException("Runtime Exception : 沒有子時的情況"); //沒有子時的情況
        }
      }

    } else {
      //LMT 位於 午正到子正（下半天）
      double thirteenHoursAgo = gmtJulDay - 13/24.0;
      double previousMeridian = riseTransImpl.getGmtTransJulDay(thirteenHoursAgo , Planet.SUN , TransPoint.MERIDIAN , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);

      double oneUnit1 = (nextMeridianGmt - nextNadirGmt) / 12.0; //從 下一個子正 到 下一個午正，總共幾天
      double oneUnit2 = (nextNadirGmt - previousMeridian) / 12.0; //從 下一個子正 到 上一個午正，總共幾秒

      Branch currentEb = this.getHour(gmtJulDay , location); // 取得目前在哪個時辰中
      if ((currentEb.getIndex() >= 6 && currentEb.getIndex() <= 11) &&  //如果現在時辰在晚子時之前 : 午6 ~ 亥11
          ((targetEb.getIndex() >= 6 && targetEb.getIndex() > currentEb.getIndex()) || targetEb == Branch.子) //而且現在所處的時辰，未超過欲求的時辰
        ) {
        if (targetEb == Branch.未 || targetEb == Branch.申 || targetEb == Branch.酉 || targetEb == Branch.戌 || targetEb == Branch.亥) {
          resultGmt = previousMeridian + oneUnit2 * ((targetEb.getIndex() - 7) * 2 + 1);
        }
        else if (targetEb == Branch.丑 || targetEb == Branch.寅 || targetEb == Branch.卯 || targetEb == Branch.辰 || targetEb == Branch.巳 || targetEb == Branch.午) {
          resultGmt = nextNadirGmt + oneUnit1 * ((targetEb.getIndex() - 1) * 2 + 1);
        }
        else {
          resultGmt = previousMeridian + oneUnit2 * 11; // 晚子時之始
        }
      }
      else {
        // 欲求的時辰，早於現在所處的時辰
        double oneUnit3 = (nextMeridianGmt - nextNadirGmt) / 12.0;
        double nextNextNadir = riseTransImpl.getGmtTransJulDay(nextMeridianGmt , Planet.SUN , TransPoint.NADIR , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
        double oneUnit4 = (nextNextNadir - nextMeridianGmt) / 12.0;
        if (targetEb == Branch.未 || targetEb == Branch.申 || targetEb == Branch.酉 || targetEb == Branch.戌 || targetEb == Branch.亥 ) {
          resultGmt = nextMeridianGmt + oneUnit4 * ((targetEb.getIndex()-7)*2+1);
        } else if (targetEb == Branch.子) {
          resultGmt = nextMeridianGmt + oneUnit4 * 11;
        } else {
          //丑寅卯辰巳午
          resultGmt = nextNadirGmt + oneUnit3 * ((targetEb.getIndex() - 1) * 2 + 1);
        }
      }
    }
    logger.debug("resultGmt = {}" , resultGmt);
    return resultGmt;
  }


  @NotNull
  public String getTitle(Locale locale)
  {
    return "真太陽時";
  }

  @NotNull
  public String getDescription(Locale locale)
  {
    return "利用太陽過天底 到天頂之間，劃分十二等份，再從太陽過天頂到天底，平均劃分十二等份，依此來切割 12 時辰";
  }

}
