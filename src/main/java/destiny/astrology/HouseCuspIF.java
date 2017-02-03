/**
 * @author smallufo 
 * Created on 2007/5/29 at 上午 7:00:17
 */ 
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.RisingSignIF;

/**
 * 取得宮首在「黃道」上幾度的介面<BR>
 * SwissEph 的實作是 HouseCuspImpl
 */
public interface HouseCuspIF extends RisingSignIF
{
  /**
   * 取得所有宮 (1~12) 的宮首在黃道幾度 , 傳回一個 length=13 的 array , array[0] 不使用, array[1] 為第 1 宮 , ... , array[12] 為第 12 宮 
   */
  double[] getHouseCusps(Time gmt, Location location, HouseSystem houseSystem, Coordinate coordinate);

  /**
   * 取得所有宮（1~12）的宮首，是什麼星座 . 傳回一個 length=13 的 array , array[0] 不使用。
   */
  default ZodiacSign[] getHouseSigns(Time gmt , Location location , HouseSystem houseSystem , Coordinate coordinate) {
    double[] cusps = getHouseCusps(gmt , location , houseSystem, coordinate);
    ZodiacSign[] signs = new ZodiacSign[13];
    for(int i=1 ; i<=12 ; i++) {
      signs[i] = ZodiacSign.getZodiacSign(cusps[i]);
    }
    return signs;
  }

  /**
   * @param house : 1 ~ 12
   */
  default ZodiacSign getSign(int house , Time gmt , Location location , HouseSystem houseSystem , Coordinate coordinate) {
    return getHouseSigns(gmt , location , houseSystem , coordinate)[house];
  }

  default ZodiacSign getRisingSign(Time gmt , Location location , HouseSystem houseSystem , Coordinate coordinate) {
    return getHouseSigns(gmt , location , houseSystem , coordinate)[1];
  }

  @Override
  default ZodiacSign getRisingSign(Time lmt , Location location , Coordinate coordinate) {
    Time gmt = Time.getGMTfromLMT(lmt , location);
    return getHouseSigns(gmt , location , HouseSystem.PLACIDUS , coordinate)[1];
  }


  
  /**
   * 取得第 index 宮的宮首在黃道幾度 , 為 1-based , 1 <= index <=12
   */
  double getHouseCusp(int index , Time gmt , Location location , HouseSystem houseSystem , Coordinate coordinate);

  @Override
  default String getRisingSignName() {
    return "真實星體觀測";
  }
}
