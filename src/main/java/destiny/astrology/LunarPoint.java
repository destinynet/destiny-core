/**
 * @author smallufo
 * Created on 2007/12/23 at 上午 5:24:56
 */
package destiny.astrology;


/**
 * 代表黃白道的交點，以及近遠點 , 繼承圖如下：
 * <pre>
 *       LunarPoint(Abstract)
 *            日月交點
 *              |
 *              |
 *     +--------+--------+ 
 *     |                 | 
 * LunarNode         LunarApsis
 * [TRUE/MEAN]       [MEAN/OSCU]
 * North/South   PERIGEE (近)/APOGEE (遠)
 * </pre>
 */
public abstract class LunarPoint extends Star {

  /*
   * [WARN] 2017-04-08 : parent class 不應 reference 到 sub-class 的 field , class loading 可能會出現問題
   * 參考搜尋字串： referencing subclass from superclass initializer might lead to class loading deadlock
   *
  public final static LunarPoint[] values = {
    LunarNode.NORTH_MEAN , LunarNode.NORTH_TRUE ,       //北交點
    LunarNode.SOUTH_MEAN , LunarNode.SOUTH_TRUE,        //南交點
    LunarApsis.APOGEE_MEAN  , LunarApsis.APOGEE_OSCU ,  //遠地點
    LunarApsis.PERIGEE_MEAN , LunarApsis.PERIGEE_OSCU   //近地點
   };
   */

  LunarPoint(String nameKey, String abbrKey, String resource) {
    super(nameKey, abbrKey, resource);
  }

}
