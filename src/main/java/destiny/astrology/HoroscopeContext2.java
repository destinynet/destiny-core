/**
 * Created by smallufo on 2017-07-10.
 */
package destiny.astrology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * 計算 {@link Horoscope2} 所需的設定、 preference
 */
public class HoroscopeContext2 implements Serializable {

  /** 分宮法 */
  private final HouseSystem houseSystem;

  /** 座標系統 */
  private final Coordinate coordinate;

  /** 中心系統 */
  private final Centric centric;

  /** 溫度 */
  private final double temperature;

  /** 壓力 */
  private final double pressure;

  /** 星體位置實作 */
  private final StarPositionWithAzimuthIF starPositionWithAzimuthImpl;

  /** Apsis (近點,遠點,北交,南交) 的位置 */
  private final ApsisWithAzimuthIF apsisWithAzimuthImpl;

  /** 取得宮首在「黃道」上幾度的介面 */
  private final HouseCuspIF houseCuspImpl;

  /** TrueNode / Mean Node */
  private final NodeType nodeType;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public HoroscopeContext2(HouseSystem houseSystem, Coordinate coordinate, Centric centric, double temperature, double pressure, StarPositionWithAzimuthIF starPositionWithAzimuthImpl, ApsisWithAzimuthIF apsisWithAzimuthImpl, HouseCuspIF houseCuspImpl, NodeType nodeType) {
    this.houseSystem = houseSystem;
    this.coordinate = coordinate;
    this.centric = centric;
    this.temperature = temperature;
    this.pressure = pressure;
    this.starPositionWithAzimuthImpl = starPositionWithAzimuthImpl;
    this.apsisWithAzimuthImpl = apsisWithAzimuthImpl;
    this.houseCuspImpl = houseCuspImpl;
    this.nodeType = nodeType;
  }

  public HouseSystem getHouseSystem() {
    return houseSystem;
  }

  public Coordinate getCoordinate() {
    return coordinate;
  }

  public Centric getCentric() {
    return centric;
  }

  public double getTemperature() {
    return temperature;
  }

  public double getPressure() {
    return pressure;
  }

  public StarPositionWithAzimuthIF getStarPositionWithAzimuthImpl() {
    return starPositionWithAzimuthImpl;
  }

  public ApsisWithAzimuthIF getApsisWithAzimuthImpl() {
    return apsisWithAzimuthImpl;
  }

  public HouseCuspIF getHouseCuspImpl() {
    return houseCuspImpl;
  }

  public NodeType getNodeType() {
    return nodeType;
  }
}
