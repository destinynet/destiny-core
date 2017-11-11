/**
 * Created by smallufo on 2017-11-08.
 */
package destiny.astrology;

public class SolarEclipseAnnularCentered extends SolarEclipseAnnular implements IEclipseCenter {

  protected final double centerBegin;

  protected final double centerEnd;

  public SolarEclipseAnnularCentered(double max, double partialBegin, double partialEnd, double totalBegin, double totalEnd, double centerBegin, double centerEnd, double annularBegin, double annularEnd) {
    super(max, partialBegin, partialEnd, totalBegin, totalEnd, annularBegin, annularEnd);

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
