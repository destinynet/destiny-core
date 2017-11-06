/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology;

public class EclipseTotal extends AbstractEclipse implements IEclipseCenter {

  private final double totalBegin;

  private final double totalEnd;

  private final double centerBegin;

  private final double centerEnd;

  public EclipseTotal(double max, double partialBegin, double partialEnd, double totalBegin, double totalEnd, double centerBegin, double centerEnd) {
    super(max, partialBegin , partialEnd);
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
  public double getCenterBegin() {
    return centerBegin;
  }

  @Override
  public double getCenterEnd() {
    return centerEnd;
  }

  @Override
  public Type getType() {
    return Type.TOTAL;
  }
}
