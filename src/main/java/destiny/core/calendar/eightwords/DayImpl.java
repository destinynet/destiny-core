/*
 * @author smallufo
 * @date 2004/12/6
 * @time 下午 05:19:35
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 換日 的實作
 */
public class DayImpl implements DayIF , Serializable
{
  public DayImpl()
  {
  }

  @Override
  public StemBranch getDay(double gmtJulDay, Location location, MidnightIF midnightImpl, HourIF hourImpl, boolean changeDayAfterZi) {
    int lmtJulDay = (int) (gmtJulDay+0.5);
    int index = (lmtJulDay-11) % 60;

    double nextMidnightGmt = midnightImpl.getNextMidnight(gmtJulDay , location);

    //FIXME TODO
    return null;
  }

  @Override
  public StemBranch getDay(@NotNull Time lmt, Location location, @NotNull MidnightIF midnightImpl, @NotNull HourIF hourImpl, boolean changeDayAfterZi) {

    //Time gmt = Time.getGMTfromLMT(lmt, location);
    
    int lmtJulDay = (int) (lmt.getGmtJulDay()+0.5);
    int index = (lmtJulDay-11) % 60;
    
    
    Time nextMidnightLmt = midnightImpl.getNextMidnight(lmt , location);
    Time 下個子初時刻 = hourImpl.getLmtNextStartOf(lmt , location , Branch.子);
    
    if (nextMidnightLmt.getHour() >=12 )
    {
      //子正，在 LMT 零時之前
      if (nextMidnightLmt.getDay()==lmt.getDay())
      {
        // lmt 落於 當日零時之後，子正之前（餅最大的那一塊）
        Time midnightNextZi = hourImpl.getLmtNextStartOf(nextMidnightLmt , location , Branch.子);
        if (changeDayAfterZi && 下個子初時刻.getDay() == midnightNextZi.getDay())
          index++;
      } 
      else
        // lmt 落於 子正之後，到 24 時之間 (其 nextMidnight 其實是明日的子正) , 則不論是否早子時換日，都一定換日
        index++;
    }
    else
    {
      //子正，在 LMT 零時之後（含）
      if (nextMidnightLmt.getDay() == lmt.getDay())
      {
        // lmt 落於當地 零時 到 子正的這段期間
        if (下個子初時刻.isBefore(nextMidnightLmt))
        {
          // lmt 落於零時到子初之間 (這代表當地地點「極西」) , 此時一定還沒換日
          index--;
        }
        else
        {
          // lmt 落於子初到子正之間
          if (!changeDayAfterZi) //如果子正才換日
            index--;
        }
      }
      else
      {
        // lmt 落於前一個子正之後，到當天24時為止 (範圍最大的一塊「餅」)
        if (changeDayAfterZi && lmt.getDay() != 下個子初時刻.getDay() && 下個子初時刻.getHour() >=12 )
          // lmt 落於 子初之後 , 零時之前 , 而子初又是在零時之前（hour >=12 , 過濾掉極西的狀況)
          index++;
      }
    }
    return StemBranch.get(index);
  } // getDay()
} //DayImpl()