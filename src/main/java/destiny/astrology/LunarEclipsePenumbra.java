/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

/**
 * 半影月食
 * TODO 其 begin/end 似乎就等於 centerBegin/centerEnd ? 待查
 */
public class LunarEclipsePenumbra extends AbstractLunarEclipse {

  private final double centerBegin;

  private final double centerEnd;

  public LunarEclipsePenumbra(double max, double begin, double end, double centerBegin, double centerEnd) {
    super(max, begin, end);
    this.centerBegin = centerBegin;
    this.centerEnd = centerEnd;
  }

  public double getCenterBegin() {
    return centerBegin;
  }

  public double getCenterEnd() {
    return centerEnd;
  }

  @Override
  public LunarType getLunarType() {
    return LunarType.PENUMBRA;
  }


}
