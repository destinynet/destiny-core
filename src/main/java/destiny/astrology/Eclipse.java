/**
 * Created by smallufo on 2017-11-06.
 */
package destiny.astrology;

import java.io.Serializable;

/** 亦為「偏食」 */
public class Eclipse implements Serializable {

  /** 不論是 全食、偏食、還是環食，都會有「最大值」 */
  protected final double max;

  protected final double begin;

  protected final double end;

  public Eclipse(double max, double begin, double end) {
    this.max = max;
    this.begin = begin;
    this.end = end;
  }

  public double getMax() {
    return max;
  }

  public double getBegin() {
    return begin;
  }

  public double getEnd() {
    return end;
  }
}
