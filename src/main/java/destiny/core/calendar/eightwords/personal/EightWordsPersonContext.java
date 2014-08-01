/**
 * @author smallufo
 * @date 2005/3/24
 * @time 下午 05:28:23
 * 
 * 
 */
package destiny.core.calendar.eightwords.personal;

import destiny.astrology.Coordinate;
import destiny.astrology.Planet;
import destiny.astrology.StarTransitIF;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.*;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 個人命例 的八字資料 Context , 多出性別欄位 以及計算流年大運等 method
 * <BR>
 * <BR> 天干 陽男陰女，由月干支順排
 * <BR> 天干 陰男陽女，由月干支逆排
 * 
 * <BR> 起運時刻相關資料： 
 * <BR> 男性生陽年，或女性生於陰年，就從本人生日的那天起到下一個節為止，以三天為一歲；
 * <BR> 反之男性生於陰年，女性生於陽年，則從本人生日的那天起，逆數到上一個節為止，也是三天為一歲。
 * <BR> 多下來的日子一天抵四個月，一個時辰抵十天。
 * <BR> 一分鐘抵 10/120 天 , 一秒鐘抵 10/(120x60) 天 == 10x24x60x60/120x60 秒 = 120秒
 * <BR>
 */
public class EightWordsPersonContext extends EightWordsContext implements Serializable
{
  /** 實作計算節氣的介面 */
  private SolarTermsIF solarTermsImpl; 
  /** 星體運行到某點的介面 */
  private StarTransitIF starTransitImpl;
  
  /** 出生時刻 */
  private Time lmt ;
  /** 出生地點 */
  private Location location;
  /** 性別 */
  private Gender gender;
  
  /** 此人的八字 */
  @Nullable
  private EightWords eightWords;
  /** 現在（LMT）的節/氣 */
  private SolarTerms currentSolarTerms;
  
  /** 運 :「月」的 span 倍數，內定 120，即：一個月干支 擴展(乘以)120 倍，變成十年 */
  private double fortuneMonthSpan = 120;
  
  /** 運：「日」的 span 倍數，內定 365，即：一日走一年 */
  private double fortuneDaySpan = 365 ;
  
  /** 運 :「時辰」的 span 倍數，內定 365x12，即：一時辰走一年 */
  private double fortuneHourSpan = 365*12;
  
  /** 
   * ThreadLoacl 物件，存放 Map<Integer , Time> , Integer 為往前/後 地幾個「節」，而 Time 則為其 GMT 值 ，作為推算大運時所使用
   * 
   * 2010/7/30 重新設計此 ThreadLocal , 本來是 ThreadLocal<Map<Integer,Time>> , 
   * 但是一個 thread 很有可能 new 出多個 EightWordsPersonContext , 而此 threadLocal 物件並沒有紀錄 EightWordsPersonContext 的特徵 
   * 因此將此 threadLocal 物件另外包一層 Map , key 為 EightWordsPersonContext
   */
  @NotNull
  private static ThreadLocal<Map<EightWordsPersonContext,Map<Integer,Time>>> targetMajorSolarTermsGmtHolder = new ThreadLocal<Map<EightWordsPersonContext,Map<Integer,Time>>>()
  {
    @Override
    protected Map<EightWordsPersonContext , Map<Integer, Time>> initialValue()
    {
      return Collections.synchronizedMap(new HashMap<>());
    }
  };
  
  /** 大運的順逆，內定採用『陽男陰女順排；陰男陽女逆排』的演算法 */
  private FortuneDirectionIF fortuneDirectionImpl = new FortuneDirectionDefaultImpl();

  /** constructor */
  public EightWordsPersonContext(YearMonthIF yearMonth , DayIF day, HourIF hour, MidnightIF midnight , boolean changeDayAfterZi ,
      @NotNull SolarTermsIF solarTermsImpl , StarTransitIF starTransitImpl ,
      @NotNull Time lmt , @NotNull Location location , Gender gender , double fortuneMonthSpan , FortuneDirectionIF fortuneDirectionImpl)
  {
    super(yearMonth, day, hour, midnight , changeDayAfterZi);
    this.solarTermsImpl = solarTermsImpl;
    this.starTransitImpl = starTransitImpl;
    this.fortuneMonthSpan = fortuneMonthSpan;
    this.fortuneDirectionImpl = fortuneDirectionImpl;
    
    // LMT 的八字
    this.lmt = lmt;
    this.location = location;
    this.gender = gender;
    this.eightWords = this.getEightWords(lmt , location);
    Time gmt = Time.getGMTfromLMT(lmt, location);
    this.currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt);
    
    //System.out.println("map size = " + targetMajorSolarTermsGmtHolder.get().size());
  }
 
  
  /** 取得出生時刻 */
  public Time getLmt()
  {
    return lmt;
  }
  
  /** 取得出生地點 */
  public Location getLocation()
  {
    return location;
  }
  
  /** 取得出生時刻的八字 */
  @Nullable
  public EightWords getEightWords()
  {
    return this.eightWords;
  }
  
  /** 性別 */
  public Gender getGender()
  {
    return gender;
  }

  /*
  public void setGender(Gender gender)
  {
    this.gender = gender;
  }
  */
  
  /** 運 : 日干支的倍數 */
  public double getFortuneDaySpan()
  {
    return fortuneDaySpan;
  }
  
  /*
  public void setFortuneDaySpan(double fortuneDaySpan)
  {
    this.fortuneDaySpan = fortuneDaySpan;
  }
  */
  
  /** 運 : 時干支的倍數 */
  public double getFortuneHourSpan()
  {
    return fortuneHourSpan;
  }
  
  /*
  public void setFortuneHourSpan(double fortuneHourSpan)
  {
    this.fortuneHourSpan = fortuneHourSpan;
  }
  */
  
  /** 大運 : 月干支的倍數 , 內定為 120 */
  public double getFortuneMonthSpan()
  {
    return fortuneMonthSpan;
  }
  
  /*
  public void setFortuneMonthSpan(double fortuneMonthSpan)
  {
    this.fortuneMonthSpan = fortuneMonthSpan;
  }
  */

  /** 八字大運是否順行 */
  public boolean isFortuneDirectionForward()
  {
    return this.fortuneDirectionImpl.isForward(this);
  }
  
  
  /** 現在的節氣 */
  public SolarTerms getCurrentSolarTerms()
  {
    return currentSolarTerms;
  }
  
  /** 
   * 距離下 N 個「節」有幾秒 , 如果 index 為負，代表計算之前的「節」。 index 不能等於 0 
   * @return <b>如果 index 為正，則傳回正值; 如果 index 為負，則傳回負值</b>
   * */
  public double getTargetMajorSolarTermsSeconds(int index)
  {
    if (index==0)
      throw new RuntimeException("index cannot be 0 !");
    
    boolean reverse = false;
    if (index < 0 )
      reverse = true;
    
    Time gmt = Time.getGMTfromLMT(lmt, location);
    Time stepGmt = new Time(gmt , 0);
    //現在的 節氣
    SolarTerms currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt);
    SolarTerms stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms , reverse);
    int i;
    if (!reverse)
      i=1;
    else
      i=-1;
    
    
    //Map<Integer,Time> hashMap = targetMajorSolarTermsGmtHolder.get();
    Map<EightWordsPersonContext , Map<Integer,Time>> hashMap = targetMajorSolarTermsGmtHolder.get();
    if (hashMap.get(this) == null)
      hashMap.put(this, new HashMap<>());
    Time targetGmt = hashMap.get(this).get(index);
    
    if (targetGmt == null)
    {
      if (!reverse)
      {
        //順推
        if (hashMap.get(this).get(index - 1) != null)
          i=index-1;
        
        while (i <= index)
        {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推
          if (hashMap.get(this).get(i) == null)
          {
            //沒有計算過
            targetGmt = this.starTransitImpl.getNextTransit(Planet.SUN , stepMajorSolarTerms.getZodiacDegree() , Coordinate.ECLIPTIC , stepGmt , true);
            //以隔天計算現在節氣
            stepGmt = new Time(targetGmt , 24*60*60);
            
            Map<Integer,Time> m = hashMap.get(this);
            m.put(i, targetGmt);
            hashMap.put(this, m);
            targetMajorSolarTermsGmtHolder.set(hashMap);
          }
          else
          {
            //之前計算過
            targetGmt = hashMap.get(this).get(i);
            stepGmt = new Time(targetGmt , 24*60*60);
          }
          
          currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmt);
          stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms , false);
          i++;
        } // while (i <= index)
      } //順推
      else
      {
        //逆推
        if (hashMap.get(this).get(index + 1) != null)
          i=index+1;
        
        while (i >= index)
        {
          // 推算到上一個/下一個「節」的秒數：陽男陰女順推，陰男陽女逆推
          
          if (hashMap.get(this).get(i) == null)
          {
            //沒有計算過
            
            targetGmt = this.starTransitImpl.getNextTransit(Planet.SUN , stepMajorSolarTerms.getZodiacDegree() , Coordinate.ECLIPTIC , stepGmt , false);
            //以前一天計算現在節氣
            stepGmt = new Time(targetGmt , -24*60*60);
            
            Map<Integer,Time> m = hashMap.get(this);
            m.put(i, targetGmt);
            hashMap.put(this , m);
            targetMajorSolarTermsGmtHolder.set(hashMap);
          }
          else
          {
            //之前計算過
            targetGmt = hashMap.get(this).get(i);
            stepGmt = new Time(targetGmt , -24*60*60);        
          }
          
          currentSolarTerms = solarTermsImpl.getSolarTermsFromGMT(stepGmt);
          stepMajorSolarTerms = this.getNextMajorSolarTerms(currentSolarTerms , true);
          i--;
        } //while (i >= index)
      } //逆推
    }
    
    return targetGmt.diffSeconds(gmt);
  } // getTargetMajorSolarTermsSeconds(int)
  
  
  /**
   * 取得下一個「節」
   * @param currentSolarTerms 現在的「節」
   * @param reverse 是否逆推
   * @return 下一個「節」（如果 reverse == true，則傳回上一個「節」）
   */
  private SolarTerms getNextMajorSolarTerms(@NotNull SolarTerms currentSolarTerms , boolean reverse)
  {
    int currentSolarTermsIndex = SolarTerms.getIndex(currentSolarTerms);
    if (currentSolarTermsIndex % 2 == 0)  //立春 , 驚蟄 , 清明 ...
    {
      if (!reverse) //順推
        return currentSolarTerms.next().next();
      else
        return currentSolarTerms;
    }
    else //雨水 , 春分 , 榖雨 ...
    { 
      if (!reverse) //順推
        return currentSolarTerms.next();
      else
        return currentSolarTerms.previous();
    }
  } // getNextMajorSolarTerms()
  
  
  /**
   * 由 GMT 反推月大運
   * @param targetGmt 目標時刻 (in GMT)
   * @return 月大運干支
   */
  public StemBranch getStemBranchOfFortunrMonth(@NotNull Time targetGmt)
  {
    return this.getStemBranchOfFortune(targetGmt , this.fortuneMonthSpan);    
  }
  
  /**
   * 由 GMT 反推日大運
   * @param targetGmt 目標時刻 (in GMT)
   * @return 日大運干支
   */
  public StemBranch getStemBranchOfFortuneDay(@NotNull Time targetGmt)
  {
    return this.getStemBranchOfFortune(targetGmt , this.fortuneDaySpan);
  }
  
  /**
   * 由 GMT 反推時大運
   * @param targetGmt 目標時刻 (in GMT)
   * @return 時大運干支
   */
  public StemBranch getStemBranchOfFortuneHour(@NotNull Time targetGmt)
  {
    return this.getStemBranchOfFortune(targetGmt , this.fortuneHourSpan);
  }
  
  /**
   * 由 GMT 反推月大運，private method
   * @param targetGmt 目標時刻（必須在初生時刻之後）
   * @param span 放大倍數
   * @return 干支
   */
  private StemBranch getStemBranchOfFortune(@NotNull Time targetGmt , double span)
  {
    Time gmt = Time.getGMTfromLMT(lmt, location);
    StemBranch resultStemBranch = this.getEightWords().getMonth();
    if ( targetGmt.isAfter(gmt) && isFortuneDirectionForward() )
    {
      //大運順行
      int index = 1;
      while (getTargetMajorSolarTermsSeconds(index)*span < targetGmt.diffSeconds(gmt))
      {
        resultStemBranch = resultStemBranch.getNext(); 
        index++;
      } 
      return resultStemBranch;
    }
    if ( targetGmt.isAfter(gmt) && !isFortuneDirectionForward() )
    {
      //大運逆行
      int index = -1;
      while (Math.abs(getTargetMajorSolarTermsSeconds(index)*span) < targetGmt.diffSeconds(gmt))
      {
        resultStemBranch = resultStemBranch.getPrevious();
        index--;
      }
      return resultStemBranch;
    }
    else
      throw new RuntimeException("Error while getStemBranchOfFortune("+targetGmt+")");
  }


  @NotNull
  @Override
  public String toString()
  {
    return "EightWordsPersonContext [gender=" + gender + ", lmt=" + lmt + ", location=" + location + "]";
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((currentSolarTerms == null) ? 0 : currentSolarTerms
        .hashCode());
    result = prime * result + ((eightWords == null) ? 0 : eightWords.hashCode());
    long temp;
    temp = Double.doubleToLongBits(fortuneDaySpan);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((fortuneDirectionImpl == null) ? 0 : fortuneDirectionImpl
        .hashCode());
    temp = Double.doubleToLongBits(fortuneHourSpan);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(fortuneMonthSpan);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((gender == null) ? 0 : gender.hashCode());
    result = prime * result + ((lmt == null) ? 0 : lmt.hashCode());
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    result = prime * result + ((solarTermsImpl == null) ? 0 : solarTermsImpl
        .hashCode());
    result = prime * result + ((starTransitImpl == null) ? 0 : starTransitImpl
        .hashCode());
    return result;
  }


  @Override
  public boolean equals(@Nullable Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EightWordsPersonContext other = (EightWordsPersonContext) obj;
    if (currentSolarTerms != other.currentSolarTerms)
      return false;
    if (eightWords == null)
    {
      if (other.eightWords != null)
        return false;
    }
    else if (!eightWords.equals(other.eightWords))
      return false;
    if (Double.doubleToLongBits(fortuneDaySpan) != Double
        .doubleToLongBits(other.fortuneDaySpan))
      return false;
    if (fortuneDirectionImpl == null)
    {
      if (other.fortuneDirectionImpl != null)
        return false;
    }
    else if (!fortuneDirectionImpl.equals(other.fortuneDirectionImpl))
      return false;
    if (Double.doubleToLongBits(fortuneHourSpan) != Double
        .doubleToLongBits(other.fortuneHourSpan))
      return false;
    if (Double.doubleToLongBits(fortuneMonthSpan) != Double
        .doubleToLongBits(other.fortuneMonthSpan))
      return false;
    if (gender != other.gender)
      return false;
    if (lmt == null)
    {
      if (other.lmt != null)
        return false;
    }
    else if (!lmt.equals(other.lmt))
      return false;
    if (location == null)
    {
      if (other.location != null)
        return false;
    }
    else if (!location.equals(other.location))
      return false;
    if (solarTermsImpl == null)
    {
      if (other.solarTermsImpl != null)
        return false;
    }
    else if (!solarTermsImpl.equals(other.solarTermsImpl))
      return false;
    if (starTransitImpl == null)
    {
      if (other.starTransitImpl != null)
        return false;
    }
    else if (!starTransitImpl.equals(other.starTransitImpl))
      return false;
    return true;
  }
}
