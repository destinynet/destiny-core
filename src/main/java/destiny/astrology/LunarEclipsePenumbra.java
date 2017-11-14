/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

/**
 * 半影月食
 */
public class LunarEclipsePenumbra extends AbstractLunarEclipse {

  private final double penumbraBegin;

  private final double penumbraEnd;

  public LunarEclipsePenumbra(double max, double begin, double end, double penumbraBegin, double penumbraEnd) {
    super(max, begin, end);
    this.penumbraBegin = penumbraBegin;
    this.penumbraEnd = penumbraEnd;
  }

  public double getPenumbraBegin() {
    return penumbraBegin;
  }

  public double getPenumbraEnd() {
    return penumbraEnd;
  }

  @Override
  public LunarType getLunarType() {
    return LunarType.PENUMBRA;
  }


}
