/**
 * @author smallufo 
 * Created on 2007/11/26 at 上午 12:48:16
 */ 
package destiny.astrology;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 「現代占星術」中，考量星體的交角容許度，內定實作 <br/>
 * 內定只考慮日月的交角有特別的容許度 <br/>
 * 參考資料 http://www.myastrologybook.com/aspects-and-orbs.htm
 */
public class AspectOrbsPlanetDefaultImpl implements AspectOrbsPlanetIF , Serializable
{
  /** key 為 "p1-p2-aspect" 或是 "p2-p1-aspect" 的組合，以 '-' 串接 */
  private static String getCompositedKey(Point p1 , Point p2 , Aspect aspect)
  {
    return p1.toString()+"-"+p2.toString()+"-"+aspect.toString();
  }
  
  private static Map<String,Double> aspectMap = Collections.synchronizedMap(new HashMap<String,Double>());
  static 
  {
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.CONJUNCTION), 12.0);
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.OPPOSITION), 12.0);
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.TRINE), 8.0);
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SQUARE), 8.0);
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SEXTILE), 5.0);
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SEMISQUARE), 2.5);
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SESQUIQUADRATE), 2.5 );
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SEMISEXTILE), 2.0);
    aspectMap.put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.QUINCUNX), 2.5);
  }

  @Override
  public double getPlanetAspectOrb(Point p1, Point p2, Aspect aspect)
  {
    String key = getCompositedKey(p1, p2, aspect);
    Double value = aspectMap.get(key);
    if (value != null)
      return value.doubleValue();
    else
    {
      key = getCompositedKey(p2, p1, aspect); //行星交換 , 再試一個 key
      value = aspectMap.get(key);
      if (value != null)
        return value.doubleValue();
      else
        return -1; //如果沒有值，傳回 -1
    }
  }

}
