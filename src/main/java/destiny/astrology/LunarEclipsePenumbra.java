/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

/**
 * 半影月食
 */
public class LunarEclipsePenumbra extends AbstractLunarEclipse {

  /** 月亮觸碰地球半影 , P1 , 可視為整個 eclipse 的 begin */
  private final double penumbraBegin;

  /** 月亮完全離開地球半影 P4 , 可視為整個 eclipse 的 end */
  private final double penumbraEnd;

  public LunarEclipsePenumbra(double penumbraBegin, double max, double penumbraEnd) {
    super(penumbraBegin, max, penumbraEnd);
    this.penumbraBegin = penumbraBegin;
    this.penumbraEnd = penumbraEnd;
  }

  @Override
  public LunarType getLunarType() {
    return LunarType.PENUMBRA;
  }

  @Override
  public double getPenumbraBegin() {
    return penumbraBegin;
  }

  @Override
  public double getPenumbraEnd() {
    return penumbraEnd;
  }
}
