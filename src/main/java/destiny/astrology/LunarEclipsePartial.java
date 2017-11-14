/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

public class LunarEclipsePartial extends LunarEclipsePenumbra {

  /** 月亮最先碰觸地球本影 (U1) */
  private final double partialBegin;

  /** 月亮完全離開地球本影 (U4) */
  private final double partialEnd;

  public LunarEclipsePartial(double penumbraBegin, double partialBegin, double max, double partialEnd, double penumbraEnd) {
    super(penumbraBegin , max, penumbraEnd);
    this.partialBegin = partialBegin;
    this.partialEnd = partialEnd;
  }

  public double getPartialBegin() {
    return partialBegin;
  }

  public double getPartialEnd() {
    return partialEnd;
  }

  @Override
  public LunarType getLunarType() {
    return LunarType.PARTIAL;
  }

}
