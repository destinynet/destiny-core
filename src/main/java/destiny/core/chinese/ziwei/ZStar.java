/**
 * Created by smallufo on 2017-04-11.
 */
package destiny.core.chinese.ziwei;

import destiny.astrology.Point;

/** 紫微斗數的諸星 */
public abstract class ZStar extends Point {

  public ZStar(String nameKey, String resource) {
    super(nameKey, resource);
  }

  public ZStar(String nameKey, String resource , String abbrKey) {
    super(nameKey, resource , abbrKey);
  }
}
