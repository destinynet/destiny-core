/**
 * Created by smallufo on 2017-11-06.
 */
package destiny.astrology;

public class SolarEclipsePartial extends AbstractSolarEclipse {

  public SolarEclipsePartial(double max, double begin, double end) {
    super(max, begin, end);
  }

  @Override
  public SolarType getSolarType() {
    return SolarType.PARTIAL;
  }

  @Override
  public String toString() {
    return "[偏食 " + "begin=" + begin + ", max=" + max + ", end=" + end + ']';
  }
}
