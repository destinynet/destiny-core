/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

public abstract class AbstractLunarEclipse extends AbstractEclipse {

  public enum LunarType {
    TOTAL,
    PARTIAL,
    PENUMBRA  // 半影月食
  }

  public AbstractLunarEclipse(double max, double begin, double end) {
    super(max, begin, end);
  }

  public abstract LunarType getLunarType();
}
