/**
 * Created by smallufo on 2017-07-02.
 */
package destiny.astrology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionFunctions {

  private static Logger logger = LoggerFactory.getLogger(PositionFunctions.class);

  public final static IPosition posSun = new PositionStarImpl(Planet.SUN) {} ;
  public final static IPosition posMoon = new PositionStarImpl(Planet.MOON) {} ;

}
