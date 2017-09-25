/**
 * @author smallufo 
 * Created on 2007/5/29 at 上午 7:00:17
 */ 
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.TimeTools;
import destiny.core.calendar.eightwords.RisingSignIF;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * 取得宮首在「黃道」上幾度的介面<BR>
 * SwissEph 的實作是 HouseCuspImpl
 */
public interface HouseCuspIF extends RisingSignIF {


  /**
   * 取得所有宮 (1~12) 的宮首在黃道幾度 , 傳回一個 length=13 的 array , array[0] 不使用, array[1] 為第 1 宮 , ... , array[12] 為第 12 宮 
   */
  double[] getHouseCusps(double gmtJulDay , Location loc , HouseSystem houseSystem, Coordinate coordinate);


  default double[] getHouseCusps(LocalDateTime lmt , Location location , HouseSystem houseSystem, Coordinate coordinate) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getHouseCusps(gmtJulDay , location , houseSystem , coordinate);
  }

  /**
   * 取得所有宮（1~12）的宮首，是什麼星座 . 傳回一個 length=13 的 array , array[0] 不使用。
   */
  default ZodiacSign[] getHouseSigns(double gmtJulDay , Location location , HouseSystem houseSystem , Coordinate coordinate) {
    double[] cusps = getHouseCusps(gmtJulDay , location , houseSystem, coordinate);
    ZodiacSign[] signs = new ZodiacSign[13];
    for(int i=1 ; i<=12 ; i++) {
      signs[i] = ZodiacSign.getZodiacSign(cusps[i]);
    }
    return signs;
  }


  /** 取得「上升星座」 (分宮法/HouseSystem  或許不需要) */
  @Override
  default ZodiacSign getRisingSign(double gmtJulDay , Location location , HouseSystem houseSystem , Coordinate coordinate) {
    return getHouseSigns(gmtJulDay , location , houseSystem , coordinate)[1];
  }

  /**
   * @param house : 1 ~ 12
   */
  default ZodiacSign getSign(int house , LocalDateTime gmt , Location location , HouseSystem houseSystem , Coordinate coordinate) {
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getHouseSigns(gmtJulDay , location , houseSystem , coordinate)[house];
  }



  
  /**
   * 取得第 index 宮的宮首在黃道幾度 , 為 1-based , 1 <= index <=12
   */
  double getHouseCusp(int index , double gmtJulDay , Location location , HouseSystem houseSystem , Coordinate coordinate);

  default double getHouseCusp(int index , LocalDateTime lmt , Location location , HouseSystem houseSystem , Coordinate coordinate) {
    LocalDateTime gmt = Time.getGmtFromLmt(lmt , location);
    double gmtJulDay = TimeTools.getGmtJulDay(gmt);
    return getHouseCusp(index , gmtJulDay , location , houseSystem , coordinate);
  }

  @Override
  default String getTitle(Locale locale) {
    return "真實星體觀測";
  }

  @Override
  default String getDescription(Locale locale) {
    return getTitle(locale);
  }

}
