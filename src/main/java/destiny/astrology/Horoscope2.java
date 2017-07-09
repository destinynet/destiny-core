/**
 * Created by smallufo on 2017-07-10.
 */
package destiny.astrology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

public class Horoscope2 implements Serializable {

  private final Map<Point , PositionWithAzimuth> positionMap;

  /**
   * 地盤 12宮 (1~12) , 每宮宮首在黃道幾度
   */
  private final Map<Integer , Double> cuspDegreeMap;

  private transient static Logger logger = LoggerFactory.getLogger(Horoscope2.class);


  public Horoscope2(Map<Point, PositionWithAzimuth> positionMap, Map<Integer, Double> cuspDegreeMap) {
    this.positionMap = positionMap;
    this.cuspDegreeMap = cuspDegreeMap;
  }
}
