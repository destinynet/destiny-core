/**
 * Created by smallufo at 2008/11/6 下午 5:52:48
 */
package destiny.astrology;

import java.util.Arrays;
import java.util.Collection;

/** 一個星盤當中，兩顆星體，是否形成某交角 */
public interface AspectEffectiveIF
{
  /**
   * @param p1 Point 1
   * @param deg1 Point 1 於黃道帶上的度數
   * @param p2 Point 2
   * @param deg2 Point 2 於黃道帶上的角度
   * @return 是否形成有效交角
   */
  boolean isEffective(Point p1 , double deg1 , Point p2 , double deg2 , Aspect aspect);

  default boolean isEffective(Point p1 , double deg1 , Point p2 , double deg2 , Aspect... aspects) {
    return isEffective(p1, deg1, p2, deg2, Arrays.asList(aspects));
  }

  default boolean isEffective(Point p1 , double deg1 , Point p2 , double deg2 , Collection<Aspect> aspects) {
    for(Aspect aspect : aspects) {
      if (isEffective(p1 , deg1 , p2 , deg2 , aspect))
        return true;
    }
    return false;
  }
}
