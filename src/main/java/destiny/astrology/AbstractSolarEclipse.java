/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

public abstract class AbstractSolarEclipse extends AbstractEclipse {

  public enum SolarType {
    PARTIAL ,
    TOTAL ,
    ANNULAR ,
    HYBRID    // 極為罕見的「全環食」
  }

  public AbstractSolarEclipse(double max, double begin, double end) {
    super(max, begin, end);
  }


  public abstract SolarType getSolarType();


}
