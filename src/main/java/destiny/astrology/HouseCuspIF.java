/**
 * @author smallufo 
 * Created on 2007/5/29 at 上午 7:00:17
 */ 
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * 取得宮首在「黃道」上幾度的介面<BR>
 * SwissEph 的實作是 HouseCuspImpl
 */
public interface HouseCuspIF
{
  /**
   * 取得所有宮 (1~12) 的宮首在黃道幾度 , 傳回一個 length=13 的 array , array[0] 不使用, array[1] 為第 1 宮 , ... , array[12] 為第 12 宮 
   */
  public double[] getHouseCusps(Time gmt , Location location , HouseSystem houseSystem);
  
  /**
   * 取得第 index 宮的宮首在黃道幾度 , 為 1-based , 1 <= index <=12
   */
  public double getHouseCusp(int index , Time gmt , Location location , HouseSystem houseSystem);
}
