/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

public class LunarEclipseTotal extends AbstractLunarEclipse implements IEclipseCenter {

  private final double totalBegin;
  private final double totalEnd;

  private final double centerBegin;
  private final double centerEnd;

  public LunarEclipseTotal(double max, double begin, double end, double totalBegin, double totalEnd, double centerBegin, double centerEnd) {
    super(max, begin, end);
    this.totalBegin = totalBegin;
    this.totalEnd = totalEnd;
    this.centerBegin = centerBegin;
    this.centerEnd = centerEnd;
  }

  public double getTotalBegin() {
    return totalBegin;
  }

  public double getTotalEnd() {
    return totalEnd;
  }

  @Override
  public LunarType getLunarType() {
    return LunarType.TOTAL;
  }

  @Override
  public double getCenterBegin() {
    return centerBegin;
  }

  @Override
  public double getCenterEnd() {
    return centerEnd;
  }
}
