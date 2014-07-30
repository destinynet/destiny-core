/**
 * @author smallufo 
 * Created on 2007/11/26 at 上午 12:48:16
 */ 
package destiny.astrology;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 「現代占星術」中，考量星體的交角容許度，內定實作 <br/>
 * 內定只考慮日月的交角有特別的容許度 <br/>
 * 參考資料 http://www.myastrologybook.com/aspects-and-orbs.htm
 */
public class AspectOrbsPlanetDefaultImpl implements AspectOrbsPlanetIF , Serializable
{
  /** key 為 "p1-p2-aspect" 或是 "p2-p1-aspect" 的組合，以 '-' 串接 */
  @NotNull
  private static String getCompositedKey(@NotNull Point p1 , @NotNull Point p2 , @NotNull Aspect aspect)
  {
    return p1.toString()+"-"+p2.toString()+"-"+aspect.toString();
  }
  
  private final static ImmutableMap<String , Double> aspectMap = new ImmutableMap.Builder<String , Double>()
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.CONJUNCTION), 12.0)
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.OPPOSITION), 12.0)
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.TRINE), 8.0)
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SQUARE), 8.0)
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SEXTILE), 5.0)
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SEMISQUARE), 2.5)
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SESQUIQUADRATE), 2.5 )
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.SEMISEXTILE), 2.0)
    .put(getCompositedKey(Planet.SUN, Planet.MOON, Aspect.QUINCUNX), 2.5)
    .build();
  
  /**
   * @param aspect 欲取得容許度之交角
   * @return 交角容許度，如果傳回 小於零，代表找不到其值
   */
  @Override
  public double getPlanetAspectOrb(@NotNull Point p1, @NotNull Point p2, @NotNull Aspect aspect)
  {
    String key = getCompositedKey(p1, p2, aspect);
    Double value = aspectMap.get(key);
    if (value != null)
      return value;
    else
    {
      key = getCompositedKey(p2, p1, aspect); //行星交換 , 再試一個 key
      value = aspectMap.get(key);
      if (value != null)
        return value;
      else
        return -1; //如果沒有值，傳回 -1
    }
  }

}
