/*
 * @author smallufo
 * @date 2004/12/10
 * @time 下午 03:53:08
 */
package destiny.core.calendar.eightwords;

import java.io.Serializable;
import java.util.Locale;

import destiny.astrology.Planet;
import destiny.astrology.RiseTransIF;
import destiny.astrology.TransPoint;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.EarthlyBranches;

/**
 * <PRE>
 * 時辰的劃分實作
 * 利用太陽過天底 到天頂之間，劃分十二等份
 * 再從太陽過天頂到天底，平均劃分十二等份
 * 依此來切割 12 時辰
 * </PRE>
 */
public class HourSolarTransImpl implements HourIF , Serializable
{
  private double atmosphericPressure = 1013.25;
  private double atmosphericTemperature = 0;
  private boolean isDiscCenter = true;
  private boolean hasRefraction = true;
  
  private RiseTransIF riseTransImpl;
  
  protected HourSolarTransImpl()
  {
  }
  
  public HourSolarTransImpl(RiseTransIF riseTransImpl)
  {
    if (riseTransImpl == null)
      throw new RuntimeException("riseTransImpl and location cannot be null !");
    
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

  @Override
  public EarthlyBranches getHour(Time lmt, Location location)
  {
    if (lmt == null || location == null)
      throw new RuntimeException("lmt and location cannot be null !");
    
    Time gmt = Time.getGMTfromLMT(lmt, location);
    
    Time nextMeridian = riseTransImpl.getGmtTransTime(gmt , Planet.SUN , TransPoint.MERIDIAN , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    //System.out.println("HourSolarTransImpl : nextMeridian (GMT) = " + nextMeridian);
    Time nextNadir    = riseTransImpl.getGmtTransTime(gmt , Planet.SUN , TransPoint.NADIR    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    //System.out.println("HourSolarTransImpl : nextNadir    (GMT) = " + nextNadir);
    
    if (nextNadir.isAfter(nextMeridian))
    {
      //子正到午正（上半天）
      Time thirteenHoursAgo = new Time(gmt.isAd() , gmt.getYear() , gmt.getMonth() , gmt.getDay() , gmt.getHour()-13 , gmt.getMinute() , gmt.getSecond());
      Time previousNadir = riseTransImpl.getGmtTransTime(thirteenHoursAgo , Planet.SUN , TransPoint.NADIR    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
      
      double diffSeconds = (nextMeridian.diffSeconds(previousNadir)); //從子正到午正，總共幾秒
      double oneUnitSeconds = diffSeconds/12;
      if (gmt.isBefore(new Time(previousNadir , oneUnitSeconds)))
        return EarthlyBranches.子;
      else if (gmt.isBefore(new Time(previousNadir , oneUnitSeconds*3)))
        return EarthlyBranches.丑;
      else if (gmt.isBefore(new Time(previousNadir , oneUnitSeconds*5)))
        return EarthlyBranches.寅;
      else if (gmt.isBefore(new Time(previousNadir , oneUnitSeconds*7)))
        return EarthlyBranches.卯;
      else if (gmt.isBefore(new Time(previousNadir , oneUnitSeconds*9)))
        return EarthlyBranches.辰;
      else if (gmt.isBefore(new Time(previousNadir , oneUnitSeconds*11)))
        return EarthlyBranches.巳;
      else
        return EarthlyBranches.午;
        
    }
    else
    {
      //午正到子正（下半天）
      Time thirteenHoursAgo = new Time(gmt.isAd() , gmt.getYear() , gmt.getMonth() , gmt.getDay() , gmt.getHour()-13 , gmt.getMinute() , gmt.getSecond());
      Time previousMeridian = riseTransImpl.getGmtTransTime(thirteenHoursAgo , Planet.SUN , TransPoint.MERIDIAN    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
      
      double diffSeconds = (nextNadir.diffSeconds(previousMeridian));
      double oneUnitSeconds = diffSeconds/12;
      if (gmt.isBefore(new Time(previousMeridian , oneUnitSeconds)))
        return EarthlyBranches.午;
      else if (gmt.isBefore(new Time(previousMeridian , oneUnitSeconds*3)))
        return EarthlyBranches.未;
      else if (gmt.isBefore(new Time(previousMeridian , oneUnitSeconds*5)))
        return EarthlyBranches.申;
      else if (gmt.isBefore(new Time(previousMeridian , oneUnitSeconds*7)))
        return EarthlyBranches.酉;
      else if (gmt.isBefore(new Time(previousMeridian , oneUnitSeconds*9)))
        return EarthlyBranches.戌;
      else if (gmt.isBefore(new Time(previousMeridian , oneUnitSeconds*11)))
        return EarthlyBranches.亥;
      else
        return EarthlyBranches.子;
    }    
  }

  @Override
  public Time getLmtNextStartOf(Time lmt, Location location, EarthlyBranches targetEb)
  {
    Time resultGmt = null;
    Time gmt = Time.getGMTfromLMT(lmt, location);
    Time nextMeridian = riseTransImpl.getGmtTransTime(gmt , Planet.SUN , TransPoint.MERIDIAN , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    Time nextNadir    = riseTransImpl.getGmtTransTime(gmt , Planet.SUN , TransPoint.NADIR    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
    if (nextNadir.isAfter(nextMeridian))
    {
      //LMT 位於子正到午正（上半天）
      Time twelveHoursAgo = new Time(gmt.isAd() , gmt.getYear() , gmt.getMonth() , gmt.getDay() , gmt.getHour()-12 , gmt.getMinute() , gmt.getSecond());
      Time previousNadir = riseTransImpl.getGmtTransTime(twelveHoursAgo , Planet.SUN , TransPoint.NADIR    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
      double diffSeconds1 = nextMeridian.diffSeconds(previousNadir); //從子正到午正，總共幾秒
      double oneUnit1 = diffSeconds1 / 12;
      double diffSeconds2 = nextNadir.diffSeconds(nextMeridian); //從午正到下一個子正，總共幾秒
      double oneUnit2 = diffSeconds2 / 12;
      
      EarthlyBranches currentEb = this.getHour(lmt , location); //取得目前在哪個時辰之中
      if (targetEb.getIndex() > currentEb.getIndex() || targetEb == EarthlyBranches.子)
      {
        //代表現在所處的時辰，未超過欲求的時辰 
        if (targetEb == EarthlyBranches.丑 || targetEb == EarthlyBranches.寅 || targetEb == EarthlyBranches.卯 || targetEb == EarthlyBranches.辰 || targetEb == EarthlyBranches.巳 ||targetEb == EarthlyBranches.午 )
          resultGmt = new Time(previousNadir , oneUnit1 * ((targetEb.getIndex()-1)*2+1) );
        else if (targetEb == EarthlyBranches.未 || targetEb == EarthlyBranches.申 || targetEb == EarthlyBranches.酉 || targetEb == EarthlyBranches.戌 || targetEb == EarthlyBranches.亥 )
          resultGmt = new Time(nextMeridian , oneUnit2 * ((targetEb.getIndex()-7)*2+1)  );
        else
          resultGmt = new Time(nextMeridian , oneUnit2 * 11); // eb ==子時
      }
      else
      {
        //欲求的時辰，早於現在所處的時辰 ==> 代表算的是明天的時辰 
        Time nextNextMeridian = riseTransImpl.getGmtTransTime(nextNadir , Planet.SUN , TransPoint.MERIDIAN    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
        double diffSeconds3 = nextNextMeridian.diffSeconds(nextNadir);
        double oneUnit3 = diffSeconds3 / 12;
        Time nextNextNadir = riseTransImpl.getGmtTransTime(nextNextMeridian , Planet.SUN , TransPoint.NADIR ,location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
        double diffSeconds4 = nextNextNadir.diffSeconds(nextNextMeridian);
        double oneUnit4 = diffSeconds4 / 12;
        if (targetEb == EarthlyBranches.丑 || targetEb == EarthlyBranches.寅 || targetEb == EarthlyBranches.卯 || targetEb == EarthlyBranches.辰 || targetEb == EarthlyBranches.巳 ||targetEb == EarthlyBranches.午 )
          resultGmt = new Time(nextNadir , oneUnit3 * ((targetEb.getIndex()-1)*2+1) );
        else if (targetEb == EarthlyBranches.未 || targetEb == EarthlyBranches.申 || targetEb == EarthlyBranches.酉 || targetEb == EarthlyBranches.戌 || targetEb == EarthlyBranches.亥 )
          resultGmt = new Time(nextNextMeridian , oneUnit4 * ((targetEb.getIndex()-7)*2+1) );
        else
          throw new RuntimeException("Runtime Exception : 沒有子時的情況"); //沒有子時的情況
      }
    }
    else
    {
      //LMT 位於 午正到子正（下半天）
      Time thirteenHoursAgo = new Time(gmt.isAd() , gmt.getYear() , gmt.getMonth() , gmt.getDay() , gmt.getHour()-13 , gmt.getMinute() , gmt.getSecond());
      Time previousMeridian = riseTransImpl.getGmtTransTime(thirteenHoursAgo , Planet.SUN , TransPoint.MERIDIAN    , location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
      //System.out.println("LMT 下半天， previousMeridian = " + previousMeridian + " , nextNadir = " + nextNadir);
      
      double diffSeconds1 = nextMeridian.diffSeconds(nextNadir); //從 下一個子正 到 下一個午正，總共幾秒
      double oneUnit1 = diffSeconds1 / 12;
      double diffSeconds2 = nextNadir.diffSeconds(previousMeridian); //從 下一個子正 到 上一個午正，總共幾秒
      double oneUnit2 = diffSeconds2 / 12;
      
      EarthlyBranches currentEb = this.getHour(lmt , location); //取得目前在哪個時辰之中

      if( (currentEb.getIndex() >=6 && currentEb.getIndex() <= 11) &&  //如果現在時辰在晚子時之前 : 午6 ~ 亥11
          ((targetEb.getIndex() >=6 && targetEb.getIndex() > currentEb.getIndex()) ||  targetEb ==EarthlyBranches.子) //而且現在所處的時辰，未超過欲求的時辰 
        )
      {
          if (targetEb == EarthlyBranches.未 || targetEb == EarthlyBranches.申 || targetEb == EarthlyBranches.酉 || targetEb == EarthlyBranches.戌 || targetEb == EarthlyBranches.亥 )
            resultGmt = new Time(previousMeridian , oneUnit2 * ((targetEb.getIndex()-7)*2 +1 ) );
          else if (targetEb == EarthlyBranches.丑 || targetEb == EarthlyBranches.寅 || targetEb == EarthlyBranches.卯 || targetEb == EarthlyBranches.辰 || targetEb == EarthlyBranches.巳 ||targetEb == EarthlyBranches.午 )
            resultGmt = new Time(nextNadir , oneUnit1 * ((targetEb.getIndex()-1)*2+1));
          else
            resultGmt = new Time(previousMeridian , oneUnit2 * 11); //晚子時之始
      }
      else
      {
        //欲求的時辰，早於現在所處的時辰
        double diffSeconds3 = nextMeridian.diffSeconds(nextNadir);
        double oneUnit3 = diffSeconds3 / 12;
        Time nextNextNadir = riseTransImpl.getGmtTransTime(nextMeridian , Planet.SUN , TransPoint.NADIR ,location , atmosphericPressure , atmosphericTemperature , isDiscCenter , hasRefraction);
        double diffSeconds4 = nextNextNadir.diffSeconds(nextMeridian);
        double oneUnit4 = diffSeconds4 / 12;
        if (targetEb == EarthlyBranches.未 || targetEb == EarthlyBranches.申 || targetEb == EarthlyBranches.酉 || targetEb == EarthlyBranches.戌 || targetEb == EarthlyBranches.亥 )
          resultGmt = new Time(nextMeridian , oneUnit4 * ((targetEb.getIndex()-7)*2+1));
        else if (targetEb == EarthlyBranches.子) 
          resultGmt = new Time(nextMeridian , oneUnit4 * 11);
        else //丑寅卯辰巳午
          resultGmt = new Time(nextNadir , oneUnit3 * ((targetEb.getIndex()-1)*2+1) );        
      }    
    }
    return Time.getLMTfromGMT(resultGmt, location);
  }

  public String getTitle(Locale locale)
  {
    return "日過天頂天底平均切分";
  }

  public String getDescription(Locale locale)
  {
    return "利用太陽過天底 到天頂之間，劃分十二等份，再從太陽過天頂到天底，平均劃分十二等份，依此來切割 12 時辰";
  }

}
