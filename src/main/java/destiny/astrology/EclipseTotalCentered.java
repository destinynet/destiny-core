/**
 * Created by smallufo on 2017-11-08.
 */
package destiny.astrology;

public class EclipseTotalCentered extends EclipseTotal implements IEclipseCenter {

  protected final double centerBegin;

  protected final double centerEnd;

  public EclipseTotalCentered(double max, double partialBegin, double partialEnd, double totalBegin, double totalEnd, double centerBegin, double centerEnd) {
    super(max, partialBegin, partialEnd, totalBegin, totalEnd);

    this.centerBegin = centerBegin;
    this.centerEnd = centerEnd;
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
