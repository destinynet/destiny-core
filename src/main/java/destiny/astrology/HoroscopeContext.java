/**
 * @author smallufo 
 * Created on 2007/9/1 at 下午 4:22:29
 */ 
package destiny.astrology;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * 星盤的 Context， 除了基本的時間地點之外，還包括各種計算的介面 (星體位置 / 分宮法 ...)
 * TODO : 2015-06-11 附註： mutable object , 應該做另一個 immutable 的版本
 */
@Deprecated
public class HoroscopeContext implements Serializable, HoroscopeContextIF {
  /** 當地時間 */
  private final LocalDateTime lmt;
  
  /** 地點 */
  private final Location location;
  
  /** GMT 時間 */
  private final LocalDateTime gmt;
  
  /** 分宮法 */
  private HouseSystem houseSystem;
  
  /** 座標系統 */
  private Coordinate coordinate;
  
  /** 中心系統 */
  private Centric centric;
  
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
  
  /** 最完整的 constructor */
  public HoroscopeContext(@NotNull LocalDateTime lmt , @NotNull Location location , HouseSystem houseSystem ,
      Coordinate coordinate , Centric centric , double temperature , double pressure , 
      StarPositionWithAzimuthIF positionWithAzimuthImpl , HouseCuspIF houseCuspImpl , 
      ApsisWithAzimuthIF apsisWithAzimuthImpl, NodeType nodeType)
  {
    this.lmt = lmt;
    this.location = location;
    this.gmt = Time.getGmtFromLmt(lmt , location);
    this.houseSystem = houseSystem;
    this.coordinate = coordinate;
    this.centric = centric;
    
    this.temperature = temperature;
    this.pressure = pressure;
    
    this.starPositionWithAzimuthImpl = positionWithAzimuthImpl;
    this.houseCuspImpl = houseCuspImpl;
    this.apsisWithAzimuthImpl = apsisWithAzimuthImpl;
    this.nodeType = nodeType;
  }
  
  /** 建立新的 HoroscopeContext 物件 , 其中 lmt 以 newLmt 取代 */
  @NotNull
  public static HoroscopeContext getNewLmtHoroscope(LocalDateTime newLmt , HoroscopeContext horoscopeContext) {
    return new HoroscopeContext(newLmt , horoscopeContext.getLocation() , horoscopeContext.getHouseSystem() ,
        horoscopeContext.getCoordinate() , horoscopeContext.getCentric() , horoscopeContext.getTemperature() , horoscopeContext.getPressure() ,
        horoscopeContext.getStarPositionWithAzimuthImpl() , horoscopeContext.getHouseCuspImpl() ,
        horoscopeContext.getApsisWithAzimuthImpl() , horoscopeContext.getNodeType());
  }
  
  /** 取得星體的位置以及地平方位角 */
  public PositionWithAzimuth getPosition(Point point) {
    starPositionWithAzimuthImpl.setLocation(location);
    return starPositionWithAzimuthImpl.getPositionFromGmt((Star) point, gmt, location, centric, coordinate, temperature, pressure);
  }
  
  @NotNull
  private List<Star> getPointList() {
    List<Star> stars = new ArrayList<>();
    Collections.addAll(stars, Planet.values);//行星
    Collections.addAll(stars, LunarNode.values);//月亮南北交點
    Collections.addAll(stars, LunarApsis.values);//近地點,遠地點
    Collections.addAll(stars, Asteroid.values);//小行星
    Collections.addAll(stars, Hamburger.values);//漢堡星
    Collections.addAll(stars, FixedStar.values);//恆星
    return stars;
  }
  
  
  /** 取得星盤 Horoscope */
  @NotNull
  public HoroscopeIF getHoroscope()
  {
    return new Horoscope(this , houseCuspImpl.getHouseCusps(lmt, location, houseSystem, coordinate));
  }

  /** 
   * 取得某星位於第幾宮
   * @param point 某星
   * @return 1 <= point <= 12
   */
  public int getHouse(Point point) {
    PositionWithAzimuth position = getPosition(point);
    return getHoroscope().getHouse(position.getLng());
  }
  
  /**
   * <b>此 method 僅供測試或是 debug 使用，平常最好不要用 ！</b><br/>
   * 取得第幾宮內的星星列表 , 1 <= index <=12 , 並且按照黃道度數「由小到大」排序
   */
  @NotNull
  public List<Point> getHousePoints(int index)
  {
    if (index < 1)
      return getHousePoints(index + 12);
    if (index > 12)
      return getHousePoints(index - 12);

    List<Point> resultList = getPointList().stream().filter(
      eachPoint -> getHoroscope().getHouse(getPosition(eachPoint).getLng()) == index
    )
      .sorted(new DegreeComparator(this))
      .collect(toList());


//    DegreeComparator comparator = new DegreeComparator(this);
//    Collections.sort(resultList , comparator);
    logger.debug("取得第 {} 宮的星體 : {}" , index , resultList);
    return resultList;
  }
  
  /** 取得某星 位於什麼星座 */
  public ZodiacSign getZodiacSign(@NotNull Point point) {
    PositionWithAzimuth position = getPosition(point);
    return ZodiacSign.getZodiacSign(position.getLng());
  }

  @Override
  @NotNull
  public LocalDateTime getLmt() {
    return lmt;
  }

  @NotNull
  public LocalDateTime getGmt() {
    return gmt;
  }

  @Override
  @NotNull
  public Location getLocation()
  {
    return location;
  }

  public void setHouseSystem(HouseSystem houseSystem)
  {
    this.houseSystem = houseSystem;
  }
  
  public HouseSystem getHouseSystem()
  {
    return houseSystem;
  }
  
  public void setCoordinate(Coordinate coordinate)
  {
    this.coordinate = coordinate;
  }

  public Coordinate getCoordinate()
  {
    return coordinate;
  }

  public double getTemperature()
  {
    return temperature;
  }

  public double getPressure()
  {
    return pressure;
  }

  public NodeType getNodeType()
  {
    return nodeType;
  }

  public StarPositionWithAzimuthIF getStarPositionWithAzimuthImpl()
  {
    return starPositionWithAzimuthImpl;
  }

  public ApsisWithAzimuthIF getApsisWithAzimuthImpl()
  {
    return apsisWithAzimuthImpl;
  }

  public void setCentric(Centric centric)
  {
    this.centric = centric;
  }
  
  public Centric getCentric()
  {
    return centric;
  }

  public HouseCuspIF getHouseCuspImpl()
  {
    return houseCuspImpl;
  }
  
}
