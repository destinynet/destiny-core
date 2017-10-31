/**
 * @author smallufo
 * Created on 2006/5/22 at 下午 12:09:24
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.Planet;
import destiny.astrology.IStarPosition;
import destiny.astrology.IStarTransit;
import destiny.core.calendar.*;
import destiny.core.chinese.Branch;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import static destiny.astrology.Centric.GEO;
import static destiny.astrology.Coordinate.ECLIPTIC;
import static destiny.astrology.Coordinate.EQUATORIAL;
import static java.time.temporal.ChronoField.*;

/**
 * <pre>
 * 年：具備設定換年點的功能
 * <BR>
 * 月：「定氣法」計算地支 , 計算太陽在黃道帶 0 , 15 , 30 ... 345 度的時刻
 * <BR>具備設定 南北半球月令是否對沖﹑界定南北半球的方法（赤道/赤緯度數）
 * </pre>
 */
public class YearMonthSolarTermsStarPositionImpl implements YearMonthIF, Serializable {

  private final IStarPosition starPositionImpl;

  private final IStarTransit starTransitImpl;

  /** 南半球月令是否對沖 */
  private boolean southernHemisphereOpposition = false;

  /** 依據 赤道 {@link HemisphereBy#EQUATOR} , 還是 赤緯 {@link HemisphereBy#DECLINATION} 來界定南北半球 */
  private HemisphereBy hemisphereBy = HemisphereBy.EQUATOR;

  /** 換年的度數 , 通常是立春點 (315) 換年*/
  private double changeYearDegree;

  private Logger logger = LoggerFactory.getLogger(getClass());

  private final static Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl::getLocalDateTimeStatic;

  public YearMonthSolarTermsStarPositionImpl(double changeYearDegree, IStarPosition starPositionImpl, IStarTransit starTransitImpl) {
    this.starPositionImpl = starPositionImpl;
    this.starTransitImpl = starTransitImpl;
    this.setChangeYearDegree(changeYearDegree);
  }

  public YearMonthSolarTermsStarPositionImpl(double changeYearDegree, IStarPosition starPositionImpl, IStarTransit starTransitImpl, boolean southernHemisphereOpposition) {
    this.starPositionImpl = starPositionImpl;
    this.starTransitImpl = starTransitImpl;
    this.southernHemisphereOpposition = southernHemisphereOpposition;
    this.setChangeYearDegree(changeYearDegree);
  }

  private void setChangeYearDegree(double changeYearDegree) {
    if (changeYearDegree < 180)
      throw new RuntimeException("Cannot set changeYearDegree smaller than 180 ");
    this.changeYearDegree = changeYearDegree;
  }


  @Override
  public StemBranch getYear(double gmtJulDay, Location loc) {
    ChronoLocalDateTime lmt = TimeTools.getLmtFromGmt(gmtJulDay , loc , revJulDayFunc);

    StemBranch resultStemBranch;
    //西元 1984 年為 甲子年
    int index;
    if (lmt.get(ChronoField.YEAR) > 0)
      index = (lmt.get(ChronoField.YEAR) - 1984) % 60;
    else
      index = (1 - lmt.get(ChronoField.YEAR) - 1984) % 60;

    double gmtSecondsOffset = TimeTools.getDstSecondOffset(lmt, loc).v2();

    int gmtSecondsOffsetInt = (int) gmtSecondsOffset;
    int gmtNanoOffset = (int) ((gmtSecondsOffset - gmtSecondsOffsetInt) * 1_000_000_000);

    ChronoLocalDateTime gmt = lmt.minus(gmtSecondsOffsetInt, ChronoUnit.SECONDS).minus(gmtNanoOffset, ChronoUnit.NANOS);


    double solarLongitude = starPositionImpl.getPosition(Planet.SUN, gmt, GEO, ECLIPTIC).getLng();
    if (solarLongitude < 180)
      //立春(0)過後，到秋分之間(180)，確定不會換年
      resultStemBranch = StemBranch.get(index);
    else {
      // 360 > solarLongitude >= 180

      //取得 lmt 當年 1/1 凌晨零分的度數
      ChronoLocalDateTime startOfYear = lmt.with(DAY_OF_YEAR, 1).with(HOUR_OF_DAY, 0).with(MINUTE_OF_HOUR, 0).minus(gmtSecondsOffsetInt, ChronoUnit.SECONDS);

      double degreeOfStartOfYear = starPositionImpl.getPosition(Planet.SUN, startOfYear, GEO, ECLIPTIC).getLng();

      if (changeYearDegree >= degreeOfStartOfYear) {
        if (solarLongitude >= changeYearDegree)
          resultStemBranch = StemBranch.get(index);
        else if (changeYearDegree > solarLongitude && solarLongitude >= degreeOfStartOfYear) {
          ChronoLocalDateTime tempTime = gmt.minus(180 * 24 * 60 * 60, ChronoUnit.SECONDS);
          if (TimeTools.isBefore(tempTime, startOfYear))
            resultStemBranch = StemBranch.get(index - 1);
          else
            resultStemBranch = StemBranch.get(index);
        }
        else
          resultStemBranch = StemBranch.get(index);
      }
      else {
        // degreeOfStartOfYear > changeYearDegree >= 秋分 (180)
        if (solarLongitude >= degreeOfStartOfYear) {
          ChronoLocalDateTime tempTime = gmt.minus(180 * 24 * 60 * 60 , ChronoUnit.SECONDS);
          if (TimeTools.isBefore(tempTime , startOfYear))
            resultStemBranch = StemBranch.get(index);
          else
            resultStemBranch = StemBranch.get(index + 1);
        }
        else {
          if (solarLongitude >= changeYearDegree)
            resultStemBranch = StemBranch.get(index + 1);
          else
            resultStemBranch = StemBranch.get(index);
        }
      }

    }
    // 儲存年干 , 方便稍後推算月干
//    this.年干 = resultStemBranch.getStem();
    return resultStemBranch;
  }


  /**
   * @return 取得月干支
   */
  @Override
  public StemBranch getMonth(double gmtJulDay, Location location) {
    Branch result月支;
    //先算出太陽在黃經上的度數

    SolarTermsIF solarTermsImpl = new SolarTermsImpl(this.starTransitImpl, this.starPositionImpl);
    SolarTerms MonthST = solarTermsImpl.getSolarTermsFromGMT(gmtJulDay);

    int monthIndex = (SolarTerms.getIndex(MonthST) / 2) + 2;
    if (monthIndex >= 12)
      monthIndex = monthIndex - 12;
    Branch 月支 = Branch.get(monthIndex);

    if (southernHemisphereOpposition) {
      /*
       * 解決南半球月支正沖的問題
       */
      if (hemisphereBy == HemisphereBy.EQUATOR) {
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
        double solarEquatorialDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, GEO, EQUATORIAL).getLat();

        if (solarEquatorialDegree >= 0) {
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

    Stem 年干 = getYear(gmtJulDay , location).getStem();
    return StemBranch.get(this.getMonthStem(gmtJulDay, 年干 , result月支), result月支);
  }




  /** 南半球月支是否對沖 , 內定是 '否' */
  public void setSouthernHemisphereOpposition(boolean value) {
    this.southernHemisphereOpposition = value;
  }

  public boolean isSouthernHemisphereOpposition() {
    return southernHemisphereOpposition;
  }


  /**
   * 南半球的判定方法，要依劇緯度 還是 回歸線？
   * <BR> 舉例，夏至時，太陽在北回歸線，北回歸線過嘉義，則此時，嘉義以南是否算南半球？
   * <BR> 內定是 false
   */
  public void setHemisphereBy(HemisphereBy value) {
    this.hemisphereBy = value;
  }

  public HemisphereBy getHemisphereBy() {
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
  private Stem getMonthStem(double gmtJulDay, @NotNull Stem 年干, @NotNull Branch 月支) {
    Stem 月干;

    switch (年干) {
      case 甲:
      case 己:
        月干 = 月支.getIndex() >= 2 ? (Stem.get(Branch.getIndex(月支))) : (Stem.get(Branch.getIndex(月支) + 2));
        break;
      case 乙:
      case 庚:
        月干 = 月支.getIndex() >= 2 ? (Stem.get(Branch.getIndex(月支) + 2)) : (Stem.get(Branch.getIndex(月支) + 4));
        break;
      case 丙:
      case 辛:
        月干 = 月支.getIndex() >= 2 ? (Stem.get(Branch.getIndex(月支) + 4)) : (Stem.get(Branch.getIndex(月支) + 6));
        break;
      case 丁:
      case 壬:
        月干 = 月支.getIndex() >= 2 ? (Stem.get(Branch.getIndex(月支) + 6)) : (Stem.get(Branch.getIndex(月支) + 8));
        break;
      case 戊:
      case 癸:
        月干 = 月支.getIndex() >= 2 ? (Stem.get(Branch.getIndex(月支) + 8)) : (Stem.get(Branch.getIndex(月支) + 10));
        break;
      default:
        throw new RuntimeException("impossible");
    }

    if (changeYearDegree != 315) {
      if (starPositionImpl == null)
        throw new RuntimeException("Call state error ! starTransitImpl should be set.");

      if (changeYearDegree < 315) {
        logger.debug("換年點在立春前 , changeYearDegree < 315 , value = {}" , changeYearDegree);

        double lmtSunDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, GEO, ECLIPTIC).getLng();
        if (lmtSunDegree > changeYearDegree && 315 > lmtSunDegree) {
          // t <---立春---- LMT -----換年點
          月干 = Stem.get(月干.getIndex() - 2);
        }
      }
      else if (changeYearDegree > 315) {
        //換年點在立春後 , 還沒測試
        double lmtSunDegree = starPositionImpl.getPosition(Planet.SUN, gmtJulDay, GEO, ECLIPTIC).getLng();
        if (lmtSunDegree > 315 && changeYearDegree > lmtSunDegree)
          月干 = Stem.get(月干.getIndex() + 2);
      }
    }
    return 月干;
  }


}
