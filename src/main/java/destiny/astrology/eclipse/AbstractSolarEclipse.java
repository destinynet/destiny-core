/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology.eclipse;

import destiny.astrology.eclipse.AbstractEclipse;

public abstract class AbstractSolarEclipse extends AbstractEclipse {

  public enum SolarType {
    PARTIAL ,
    TOTAL ,
    ANNULAR ,
    HYBRID    // 極為罕見的「全環食」
  }

  public AbstractSolarEclipse(double max, double begin, double end) {
    super(begin, max, end);
  }


  public abstract SolarType getSolarType();


}
