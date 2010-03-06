/*
 * @author smallufo
 * @date 2004/10/23
 * @time 上午 03:36:11
 */
package destiny.core.calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import destiny.astrology.Coordinate;
import destiny.astrology.Planet;
import destiny.astrology.Position;
import destiny.astrology.StarPositionIF;
import destiny.astrology.StarTransitIF;

/**
 * 節氣工具箱 
 */
public class SolarTermsBean implements SolarTermsIF , Serializable
{ 
  private StarTransitIF starTransitImpl;
  private StarPositionIF starPositionImpl;
  
  public SolarTermsBean()
  {
  }
  
  public void setStarPositionImpl(StarPositionIF starPositionImpl)
  {
    this.starPositionImpl = starPositionImpl;
  }

  public void setStarTransitImpl(StarTransitIF starTransitImpl)
  {
    this.starTransitImpl = starTransitImpl;
  }
  
  
  public SolarTermsBean(StarTransitIF StarTransitImpl , StarPositionIF starPositionImpl)
  {
    this.starTransitImpl = StarTransitImpl;
    this.starPositionImpl = starPositionImpl;
  }
  
  /**
   * 計算某時刻當下的節氣
   * 步驟：
   * 1. 計算太陽在黃道面的度數
   * 2. 比對此度數 , 將此度數除以 15 取整數
   * 3. 將以上的值代入 SolarTermsArray[int] 即是答案
   */
  public SolarTerms getSolarTermsFromGMT(Time gmt)
  {
    // Step 1: Calculate the Longitude of SUN
    starPositionImpl.setCoordinate(Coordinate.ECLIPTIC);
    Position sp = starPositionImpl.getPosition(Planet.SUN , gmt);
    //System.out.println("Utils.getSolarTermsFromGMT() : Longitude = " + sp.Longitude);
    // Step 2
    int SolarTermsArray = (int)(sp.getLongitude()/15)+3 ;
    if ( SolarTermsArray >= 24 )
      SolarTermsArray = SolarTermsArray - 24;
    return SolarTerms.get(SolarTermsArray);
  }
  
  /**
   * 計算從某時(fromLmtTime) 到某時(toLmtTime) 之間的節氣 , in LMT
   * @return List of <b>LMT</b> Time , 傳回 LMT 表示的節氣列表
   * 注意，此方法因為經過 Julian Day 的轉換，精確度比 GMT 差了 約萬分之一秒
   * @return List < SolarTermsTime >
   */
  public List<SolarTermsTime> getLocalPeriodSolarTerms(Time fromLmt , Time toLmt , Location location)
  {
    Time fromGmtTime = Time.getGMTfromLMT(fromLmt, location);
    Time   toGmtTime = Time.getGMTfromLMT(toLmt , location);
    
    //原本 resultList 內是放 GMT
    List<SolarTermsTime> resultList = this.getPeriodSolarTerms(fromGmtTime, toGmtTime) ;
    for (int i=0 ; i < resultList.size() ; i++)
    {
      SolarTermsTime solarTermsTimeInGMT = resultList.get(i);
      Time gmt = solarTermsTimeInGMT.getTime();
      solarTermsTimeInGMT.setTime(Time.getLMTfromGMT(gmt, location));
      
      //改以 LMT 替換
      resultList.set(i , solarTermsTimeInGMT );
    }
    return resultList;
  }//計算節氣，in LMT
  
  /**
   * 計算從某時(fromGmtTime) 到某時(toGmtTime) 之間的節氣 , in GMT
   * @return List <SolarTermsTime>
   */
  public List<SolarTermsTime> getPeriodSolarTerms(Time fromGmtTime , Time toGmtTime )
  {
    SolarTerms nowST = getSolarTermsFromGMT(fromGmtTime);

    int nextZodiacDegree = (int) destiny.astrology.Utils.getNormalizeDegree(nowST.getZodiacDegree()+15);
    
    List<SolarTermsTime> resultList = new ArrayList<SolarTermsTime>();
    
    while( fromGmtTime.isBefore(toGmtTime))
    {
      SolarTermsTime solarTermsTime;
      fromGmtTime = this.starTransitImpl.getNextTransit(Planet.SUN , nextZodiacDegree , Coordinate.ECLIPTIC , fromGmtTime , true );
      
      if (!fromGmtTime.isBefore(toGmtTime))
        break;
      nowST = nowST.next();
      solarTermsTime = new SolarTermsTime(nowST , fromGmtTime);
      resultList.add(solarTermsTime);
      nextZodiacDegree = (int) destiny.astrology.Utils.getNormalizeDegree(nextZodiacDegree+15);
    }
    
    return resultList;
  }  
  
  /** 
   * 存放 一對 SolarTerms 以及 Time 的小 class 
   */
  public final class SolarTermsTime implements Serializable
  {
    /** 節氣 */
    private SolarTerms solarTerms;
    
    /** 可能是 GMT , 也可能是 LMT */
    private Time time;
    
    public SolarTermsTime(SolarTerms solarTerms , Time time)
    {
      this.solarTerms = solarTerms;
      this.time = time;
    }
    public SolarTerms getSolarTerms()
    {
      return solarTerms;
    }
    public Time getTime()
    {
      return time;
    }
    
    public void setTime(Time time)
    {
      this.time = time;
    }
    
  }


}
