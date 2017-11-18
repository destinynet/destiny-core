/**
 * Created by smallufo on 2017-11-06.
 */
package destiny.astrology.eclipse;

public class SolarEclipseAnnular extends SolarEclipseTotal implements IEclipseAnnular {

  protected final double annularBegin;

  protected final double annularEnd;

  public SolarEclipseAnnular(double max, double partialBegin, double partialEnd, double totalBegin, double totalEnd, double annularBegin, double annularEnd) {
    super(max, partialBegin, partialEnd, totalBegin, totalEnd);
    this.annularBegin = annularBegin;
    this.annularEnd = annularEnd;
  }

  @Override
  public double getAnnularBegin() {
    return annularBegin;
  }

  @Override
  public double getAnnularEnd() {
    return annularEnd;
  }

  @Override
  public SolarType getSolarType() {
    return SolarType.ANNULAR;
  }


  @Override
  public String toString() {
    return "[環食 "
      + "begin=" + begin
      + ", totalBegin=" + totalBegin
      + ", max=" + max
      + ", totalEnd=" + totalEnd
      + ", end=" + end + ']';
  }
}
