/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 07:51:18
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.EarthlyBranches;

import java.io.Serializable;
import java.util.Locale;

/**
 * 最簡單 , 以當地平均時間來區隔時辰 , 兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時
 */
public class HourLmtImpl implements HourIF , Serializable
{
  public EarthlyBranches getHour(Time lmt, Location location)
  {
    if (lmt == null || location == null)
      throw new RuntimeException("lmt and location cannot be null !");
    
    switch (lmt.getHour())
    {
      case 23 : case  0 : return EarthlyBranches.子; 
      case  1 : case  2 : return EarthlyBranches.丑;
      case  3 : case  4 : return EarthlyBranches.寅;
      case  5 : case  6 : return EarthlyBranches.卯;
      case  7 : case  8 : return EarthlyBranches.辰;
      case  9 : case 10 : return EarthlyBranches.巳;
      case 11 : case 12 : return EarthlyBranches.午;
      case 13 : case 14 : return EarthlyBranches.未;
      case 15 : case 16 : return EarthlyBranches.申;
      case 17 : case 18 : return EarthlyBranches.酉;
      case 19 : case 20 : return EarthlyBranches.戌;
      case 21 : case 22 : return EarthlyBranches.亥;
    }
    throw new RuntimeException("HourPureClockImpl : Cannot find EarthlyBeanches for this LMT : " + lmt.toString());
  }

  
  public Time getLmtNextStartOf(Time lmt, Location location, EarthlyBranches eb)
  {
    if (lmt == null || location == null || eb == null)
      throw new RuntimeException("lmt and location and eb cannot be null !");
    switch (eb.getIndex())
    {
      case 0 : //欲求下一個子時時刻
        if (lmt.getHour() >= 23)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 23 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 23 , 0 , 0);
      case 1 : //欲求下一個丑時的時刻
        if (lmt.getHour()<1)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 1 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 1 , 0 , 0);
      case 2 : //欲求下一個寅時的時刻
        if (lmt.getHour()<3)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 3 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 3 , 0 , 0);
      case 3 : //欲求下一個卯時的時刻
        if (lmt.getHour()<5)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 5 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 5 , 0 , 0);
      case 4 : //欲求下一個辰時的時刻
        if (lmt.getHour()<7)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 7 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 7 , 0 , 0);
      case 5 : //欲求下一個巳時的時刻
        if (lmt.getHour()<9)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 9 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 9 , 0 , 0);
      case 6 : //欲求下一個午時的時刻
        if (lmt.getHour()<11)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 11 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 11 , 0 , 0);
      case 7 : //欲求下一個未時的時刻
        if (lmt.getHour()<13)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 13 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 13 , 0 , 0);
      case 8 : //欲求下一個申時的時刻
        if (lmt.getHour()<15)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 15 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 15 , 0 , 0);
      case 9 : //欲求下一個酉時的時刻
        if (lmt.getHour()<17)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 17 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 17 , 0 , 0);
      case 10 : //欲求下一個戌時的時刻
        if (lmt.getHour()<19)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 19 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 19 , 0 , 0);
      case 11 : //欲求下一個亥時的時刻
        if (lmt.getHour()<21)
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()   , 21 , 0 , 0);
        else
          return new Time(lmt.isAd() , lmt.getYear() , lmt.getMonth() , lmt.getDay()+1 , 21 , 0 , 0);
      default :
        throw new RuntimeException("Cannot get next start time of " + eb + " , LMT = " + lmt);
    }
  }


  public String getTitle(Locale locale)
  {
    return "以當地標準鐘錶時間區隔時辰";
  }


  public String getDescription(Locale locale)
  {
    return "兩小時一個時辰 , 23-1 為子時 , 1-3 為丑時 ... 依此類推 , 每個時辰固定 2 小時";
  }

}
