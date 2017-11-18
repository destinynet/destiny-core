/**
 * Created by smallufo on 2017-11-11.
 */
package destiny.astrology.eclipse;

/** 全環食 */
public class SolarEclipseHybrid extends SolarEclipseAnnularCentered {

  public SolarEclipseHybrid(double max, double partialBegin, double partialEnd, double totalBegin, double totalEnd, double centerBegin, double centerEnd, double annularBegin, double annularEnd) {
    super(max, partialBegin, partialEnd, totalBegin, totalEnd, centerBegin, centerEnd, annularBegin, annularEnd);
  }

  @Override
  public SolarType getSolarType() {
    return SolarType.HYBRID;
  }

}
