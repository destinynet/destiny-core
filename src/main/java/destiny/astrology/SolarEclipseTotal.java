/**
 * Created by smallufo on 2017-11-05.
 */
package destiny.astrology;

public class SolarEclipseTotal extends AbstractSolarEclipse {

  protected final double totalBegin;

  protected final double totalEnd;

  public SolarEclipseTotal(double max, double partialBegin, double partialEnd, double totalBegin, double totalEnd) {
    super(max, partialBegin , partialEnd);
    this.totalBegin = totalBegin;
    this.totalEnd = totalEnd;

  }

  public double getTotalBegin() {
    return totalBegin;
  }

  public double getTotalEnd() {
    return totalEnd;
  }

  @Override
  public SolarType getSolarType() {
    return SolarType.TOTAL;
  }

  @Override
  public String toString() {
    return "[全食 " +
      "begin=" + begin +
      ", totalBegin=" + totalBegin +
      ", max=" + max +
      ", totalEnd=" + totalEnd +
      ", end=" + end + ']';
  }

}
