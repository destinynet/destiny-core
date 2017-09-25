/**
 * @author smallufo 
 * Created on 2006/5/22 at 下午 12:09:24
 */ 
package destiny.core.calendar.eightwords;

import destiny.astrology.Planet;
import destiny.astrology.StarPositionIF;
import destiny.astrology.StarTransitIF;
import destiny.core.calendar.*;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;

import static destiny.astrology.Centric.GEO;
import static destiny.astrology.Coordinate.ECLIPTIC;
import static destiny.astrology.Coordinate.EQUATORIAL;

/**
 * <pre>
 * 年：具備設定換年點的功能
 * <BR>
 * 月：「定氣法」計算地支 , 計算太陽在黃道帶 0 , 15 , 30 ... 345 度的時刻
 * <BR>具備設定 南北半球月令是否對沖﹑界定南北半球的方法（赤道/赤緯度數）
 * </pre>
 */
public class YearMonthSolarTermsStarPositionImpl implements YearMonthIF , Serializable {

  private final StarPositionIF starPositionImpl;

  private final StarTransitIF starTransitImpl;
  
  /** 南半球月令是否對沖 */
  private boolean southernHemisphereOpposition = false;

  /** 依據 赤道 {@link HemisphereBy#EQUATOR} , 還是 赤緯 {@link HemisphereBy#DECLINATION} 來界定南北半球 */
  private HemisphereBy hemisphereBy = HemisphereBy.EQUATOR;

  /** 換年的度數 , 通常是立春點 (315) 換年*/
  private double changeYearDegree;

  /** 存放『年干』 */
  @Nullable
  private Stem 年干;

  public YearMonthSolarTermsStarPositionImpl(double ChangeYearDegree, StarPositionIF starPositionImpl, StarTransitIF starTransitImpl) {
    this.starPositionImpl = starPositionImpl;
    this.starTransitImpl = starTransitImpl;
    this.setting(ChangeYearDegree);
  }

  public YearMonthSolarTermsStarPositionImpl(double ChangeYearDegree, StarPositionIF starPositionImpl, StarTransitIF starTransitImpl, boolean southernHemisphereOpposition) {
    this.starPositionImpl = starPositionImpl;
    this.starTransitImpl = starTransitImpl;
    this.southernHemisphereOpposition = southernHemisphereOpposition;
    this.setting(ChangeYearDegree);
  }

  private void setting(double changeYearDegree) {
    if (changeYearDegree < 180)
      throw new RuntimeException("Cannot set changeYearDegree smaller than 180 ");
    this.changeYearDegree = changeYearDegree;
  }


  @Override
  public StemBranch getYear(LocalDateTime lmt, Location location) {
    StemBranch resultStemBranch;
    //西元 1984 年為 甲子年
    int index;
    if (lmt.getYear() > 0)
      index =  (lmt.getYear() - 1984 ) % 60;
    else
      index = (1-lmt.getYear() - 1984) % 60;

    double gmtSecondsOffset = Time.getDstSecondOffset(lmt, location).v2();

    //Time gmt = new Time(lmt , 0-gmtSecondsOffset);
    int gmtSecondsOffsetInt = (int) gmtSecondsOffset;
    int gmtNanoOffset = (int) ((gmtSecondsOffset - gmtSecondsOffsetInt)* 1_000_000_000);
    LocalDateTime gmt = LocalDateTime.from(lmt).minusSeconds(gmtSecondsOffsetInt).minusNanos(gmtNanoOffset);


    double solarLongitude = starPositionImpl.getPosition(Planet.SUN , gmt , GEO , ECLIPTIC).getLng();
    if (solarLongitude < 180)
      //立春(0)過後，到秋分之間(180)，確定不會換年
      resultStemBranch = StemBranch.get(index);
    else
    {
      // 360 > solarLongitude >= 180

      //取得 lmt 當年 1/1 凌晨零分的度數
      //Time startOfYear = new Time(lmt.isAd() , lmt.getYear() , 1 , 1 , 0 , 0 , 0-gmtSecondsOffset);
      LocalDateTime startOfYear = LocalDateTime.from(lmt).withDayOfYear(1).withHour(0).withMinute(0).minusSeconds(gmtSecondsOffsetInt);

      double degreeOfStartOfYear = starPositionImpl.getPosition(Planet.SUN , startOfYear , GEO , ECLIPTIC).getLng();

      //System.out.println("changeYearDegree = " + changeYearDegree + " , degreeOfStartOfYear = " + degreeOfStartOfYear);

      if (changeYearDegree >= degreeOfStartOfYear )
      {
        //System.out.println("StemBranch.getIndex = " + StemBranch.get(index));
        if (solarLongitude >= changeYearDegree)
          resultStemBranch = StemBranch.get(index);
        else if (changeYearDegree > solarLongitude && solarLongitude >= degreeOfStartOfYear)
        {
          LocalDateTime tempTime = LocalDateTime.from(gmt).minusSeconds(180*24*60*60);
          //Time tempTime = new Time(gmt , 0-180*24*60*60);
          if (tempTime.isBefore(startOfYear))
            resultStemBranch = StemBranch.get(index-1);
          else
            resultStemBranch = StemBranch.get(index);
        }
        else
          resultStemBranch = StemBranch.get(index);
      }
      else
      {
        // degreeOfStartOfYear > changeYearDegree >= 秋分 (180)
        if ( solarLongitude >= degreeOfStartOfYear )
        {
          LocalDateTime tempTime = LocalDateTime.from(gmt).minusSeconds(180*24*60*60);
          //Time tempTime = new Time(gmt , 0-180*24*60*60);
          if (tempTime.isBefore(startOfYear))
            resultStemBranch = StemBranch.get(index);
          else
            resultStemBranch = StemBranch.get(index+1);
        }
        else
        {
          if (solarLongitude >= changeYearDegree)
            resultStemBranch = StemBranch.get(index+1);
          else
            resultStemBranch = StemBranch.get(index);
        }
      }

    }
    // 儲存年干 , 方便稍後推算月干
    this.年干 = resultStemBranch.getStem();
    return resultStemBranch;
  } // 年干支 , LocalDateTime 版本

  /** 取得 年干支 */
//  public StemBranch getYear(Time lmt, Location location) {
//    StemBranch resultStemBranch;
//    //西元 1984 年為 甲子年
//    int index;
//    if (lmt.isAd())
//      index =  (lmt.getYear() - 1984 ) % 60;
//    else
//      index = (1-lmt.getYear() - 1984) % 60;
//
//    double gmtSecondsOffset = DstUtils.getDstSecondOffset(lmt, location).getRight();
//
//    Time gmt = new Time(lmt , 0-gmtSecondsOffset);
//
//    double solarLongitude = starPositionImpl.getPosition(Planet.SUN , gmt , GEO , ECLIPTIC).getLng();
//    if (solarLongitude < 180)
//      //立春(0)過後，到秋分之間(180)，確定不會換年
//      resultStemBranch = StemBranch.get(index);
//    else
//    {
//      // 360 > solarLongitude >= 180
//
//      //取得 lmt 當年 1/1 凌晨零分的度數
//      //Time startOfYear = new Time(lmt.isAd() , lmt.getYear() , 1 , 1 , 0 , (int) (0-gmtMinuteOffset) , 0);
//      Time startOfYear = new Time(lmt.isAd() , lmt.getYear() , 1 , 1 , 0 , 0 , 0-gmtSecondsOffset);
//      double degreeOfStartOfYear = starPositionImpl.getPosition(Planet.SUN , startOfYear , GEO , ECLIPTIC).getLng();
//
//      //System.out.println("changeYearDegree = " + changeYearDegree + " , degreeOfStartOfYear = " + degreeOfStartOfYear);
//
//      if (changeYearDegree >= degreeOfStartOfYear )
//      {
//        //System.out.println("StemBranch.getIndex = " + StemBranch.get(index));
//        if (solarLongitude >= changeYearDegree)
//          resultStemBranch = StemBranch.get(index);
//        else if (changeYearDegree > solarLongitude && solarLongitude >= degreeOfStartOfYear)
//        {
//          Time tempTime = new Time(gmt , 0-180*24*60*60);
//          if (tempTime.isBefore(startOfYear))
//            resultStemBranch = StemBranch.get(index-1);
//          else
//            resultStemBranch = StemBranch.get(index);
//        }
//        else
//          resultStemBranch = StemBranch.get(index);
//      }
//      else
//      {
//        // degreeOfStartOfYear > changeYearDegree >= 秋分 (180)
//        if ( solarLongitude >= degreeOfStartOfYear )
//        {
//          Time tempTime = new Time(gmt , 0-180*24*60*60);
//          if (tempTime.isBefore(startOfYear))
//            resultStemBranch = StemBranch.get(index);
//          else
//            resultStemBranch = StemBranch.get(index+1);
//        }
//        else
//        {
//          if (solarLongitude >= changeYearDegree)
//            resultStemBranch = StemBranch.get(index+1);
//          else
//            resultStemBranch = StemBranch.get(index);
//        }
//      }
//
//    }
//    // 儲存年干 , 方便稍後推算月干
//    this.年干 = resultStemBranch.getStem();
//    return resultStemBranch;
//  } // getYear , Time 版本


  /**
   * @return 取得月干支
   */
  @Override
  public StemBranch getMonth(LocalDateTime lmt, Location location) {
    Branch result月支 ;
    //先算出太陽在黃經上的度數

    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);

    SolarTermsIF solarTermsImpl = new SolarTermsImpl(this.starTransitImpl , this.starPositionImpl);
    SolarTerms MonthST = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay);

    int monthIndex = (SolarTerms.getIndex(MonthST)/2)+2 ;
    if (monthIndex >= 12)
      monthIndex = monthIndex - 12;
    Branch 月支 = Branch.get(monthIndex);

    if (southernHemisphereOpposition) {
      /*
       * 解決南半球月支正沖的問題
       */
      if (hemisphereBy == HemisphereBy.EQUATOR ) {
        //如果是依據赤道來區分南北半球
        if (!location.isNorth())
          result月支 = Branch.get(monthIndex + 6);
        else
          result月支 = 月支;
      }
      else {
        /*
         * 如果 hemisphereBy == DECLINATION (赤緯) , 就必須計算 太陽在「赤緯」的度數
         */
        double solarEquatorialDegree = starPositionImpl.getPosition(Planet.SUN , gmt , GEO , EQUATORIAL).getLat();

        if (solarEquatorialDegree >=0) {
          //如果太陽在赤北緯
          if (location.isNorth()) {
            //地點在北半球
            if (location.getLatitude() >= solarEquatorialDegree)
              result月支 = 月支;
            else
              result月支 = Branch.get(monthIndex + 6); //所在地緯度低於 太陽赤緯，取對沖月份
          }
          else {
            //地點在南半球 , 取正沖
            result月支 = Branch.get(monthIndex + 6);
          }
        }
        else {
          //太陽在赤南緯
          if (!location.isNorth()) {
            //地點在南半球
            if (location.getLatitude() <= solarEquatorialDegree)
              result月支 = Branch.get(monthIndex + 6); //所在地緯度高於 太陽赤南緯，真正的南半球
            else
              result月支 = 月支; //雖在南半球，但緯度低於太陽赤南緯，視為北半球
          }
          else {
            //地點在北半球，月支不變
            result月支 = 月支;
          }
        }
      }
    }
    else
      result月支 = 月支;

    return StemBranch.get(this.getMonthStem(lmt , location , result月支) , result月支);
  }


  /**
   * @param degree 設定太陽過黃道多少度 換年。一般而言是 立春（315），但是有人會使用冬至（270）換年
   * 本實作僅接受 180(含) 到 360(不含) 之間的值
   */
  public void setChangeYearDegree(double degree)
  {
    if (degree < 180)
      throw new RuntimeException("Cannot set ChangeYearDrgree smaller than 180 ");
    this.changeYearDegree = degree;
  }


  /** 南半球月支是否對沖 , 內定是 '否' */
  public void setSouthernHemisphereOpposition(boolean value)
  {
    this.southernHemisphereOpposition = value;
  }
  
  public boolean isSouthernHemisphereOpposition()
  {
    return southernHemisphereOpposition;
  }
  
  
  /**
   * 南半球的判定方法，要依劇緯度 還是 回歸線？
   * <BR> 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   * <BR> 內定是 false
   */
  public void setHemisphereBy(HemisphereBy value)
  {
    this.hemisphereBy = value;
  }

  public HemisphereBy getHemisphereBy()
  {
    return hemisphereBy;
  }
  
  /**
   * 五虎遁月 取得月干
   * <pre>
   * <b>
   * 甲己之年丙作首
   * 乙庚之歲戊為頭
   * 丙辛之歲由庚上
   * 丁壬壬位順行流
   * 若言戊癸何方發
   * 甲寅之上好追求。
   * </b>
   * </pre>
   */
  private Stem getMonthStem(@NotNull LocalDateTime lmt , @NotNull Location location , @NotNull Branch 月支) {
    Stem 月干;

    if (年干 == null)
      this.getYear(lmt , location);  //如果年干還沒算，則強迫去算一次 年干支，其結果會儲存在 this.年干 內

    switch(年干) {
      case 甲 : case 己 : 月干 = 月支.getIndex() >=2 ? (Stem.get(Branch.getIndex(月支)) ) : (Stem.get(Branch.getIndex(月支) + 2) ) ; break;
      case 乙 : case 庚 : 月干 = 月支.getIndex() >=2 ? (Stem.get(Branch.getIndex(月支) + 2) ) : (Stem.get(Branch.getIndex(月支) + 4) ) ; break;
      case 丙 : case 辛 : 月干 = 月支.getIndex() >=2 ? (Stem.get(Branch.getIndex(月支) + 4) ) : (Stem.get(Branch.getIndex(月支) + 6) ) ; break;
      case 丁 : case 壬 : 月干 = 月支.getIndex() >=2 ? (Stem.get(Branch.getIndex(月支) + 6) ) : (Stem.get(Branch.getIndex(月支) + 8) ) ; break;
      case 戊 : case 癸 : 月干 = 月支.getIndex() >=2 ? (Stem.get(Branch.getIndex(月支) + 8) ) : (Stem.get(Branch.getIndex(月支) + 10) ) ; break;
      default : throw new RuntimeException("impossible");
    }

    if (changeYearDegree != 315) {
      if (starPositionImpl == null)
        throw new RuntimeException("Call state error ! starTransitImpl should be set.");

      LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
      double gmtJulDay = TimeTools.getGmtJulDay(gmt);

      if (changeYearDegree < 315) {
        //System.out.println("changeYearDegree < 315 , value = " + changeYearDegree);
        //換年點在立春前

        double lmtSunDegree = starPositionImpl.getPosition(Planet.SUN , gmtJulDay , GEO , ECLIPTIC).getLng();
        //System.out.println("LMT = " + lmt + " degree = " + lmtSunDegree);
        if (lmtSunDegree > changeYearDegree && 315 > lmtSunDegree) {
          // t <---立春---- LMT -----換年點
          月干 = Stem.get(月干.getIndex() - 2);
        }
      }
      else if (changeYearDegree > 315) {
        //換年點在立春後 , 還沒測試
        double lmtSunDegree = starPositionImpl.getPosition(Planet.SUN , gmtJulDay , GEO , ECLIPTIC).getLng();
        if (lmtSunDegree > 315 && changeYearDegree > lmtSunDegree)
          月干 = Stem.get(月干.getIndex() + 2);
      }
    }
    return 月干;
  }





}
