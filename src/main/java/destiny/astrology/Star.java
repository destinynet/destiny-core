/**
 * @author smallufo 
 * Created on 2007/6/12 at 上午 5:18:37
 */ 
package destiny.astrology;


/**
 * 代表「星體 (celestial bodies)」 的抽象 class , 星體包括： 
 * <li>恆星 FixedStar
 * <li>行星 Planet ,含日月,冥王星
 * <li>小行星 Asteroid
 * <li>漢堡派八虛星 Hamburger
 * <li>日月交點 ApsisMoon
 * <pre>
 * 目前繼承圖如下 : 
 *                     Star
 *                       |
 *   +---------+---------+--------+-----------------+
 *   |         |         |        |                 |      
 * Planet  Asteroid  FixedStar LunarPoint(A)   Hamburger  
 *  行星    小行星     恆星     日月交點         漢堡虛星  
 *                                |
 *                                |
 *                       +--------+--------+
 *                       |                 |
 *                   LunarNode         LunarApsis
 *                   [TRUE/MEAN]       [MEAN/OSCU]
 *                   North/South   PERIGEE (近)/APOGEE (遠)
 * </pre>
 */
public abstract class Star extends Point
{
  protected Star[] values;
  
  protected Star(String nameKey , String abbrKey , String resource)
  {
    super(nameKey , resource);
    this.abbrKey = abbrKey;
  }
  
  protected Star(String nameKey , String resource)
  {
    super(nameKey , resource);
  }
}
