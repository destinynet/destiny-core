/**
 * Created by smallufo on 2017-11-08.
 */
package destiny.astrology;

public class SolarEclipseTotalCentered extends SolarEclipseTotal implements IEclipseCenter {

  protected final double centerBegin;

  protected final double centerEnd;

  public SolarEclipseTotalCentered(double max, double partialBegin, double partialEnd, double totalBegin, double totalEnd, double centerBegin, double centerEnd) {
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
