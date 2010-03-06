/**
 * @author smallufo 
 * Created on 2007/9/1 at 下午 4:22:29
 */ 
package destiny.astrology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import destiny.core.calendar.Location;
import destiny.core.calendar.Time;

/**
 * 星盤的 Context
 */
public class HoroscopeContext implements Serializable
{
  /** 當地時間 */
  private final Time lmt;
  
  /** 地點 */
  private final Location location;
  
  /** GMT 時間 */
  private final Time gmt;
  
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
  
  /** 星 (Star) 與 位置(Position)＋地平方位角(Azimuth) ==>  (PositionWithAzimuth) 的 Map , 作為 cache 層 */
  //private Map<Point , PositionWithAzimuth> positionMap = new HashMap<Point , PositionWithAzimuth>();

  /** 最簡易的 constructor , 其餘的 member variables 則使用內定  */
  /*
  public HoroscopeContext(Time lmt , Location location)
  {
    this.lmt = lmt;
    this.location = location;
    this.gmt = Time.getGMTfromLMT(lmt, location);
    
    AzimuthIF azimuthImpl = new AzimuthImpl();
    this.starPositionWithAzimuthImpl = new StarPositionWithAzimuthImpl(azimuthImpl);
    this.apsisWithAzimuthImpl = new ApsisWithAzimuthImpl(azimuthImpl);
    this.houseCuspImpl = new HouseCuspImpl();
    this.houseSystem = HouseSystem.PLACIDUS;
    this.nodeType = NodeType.MEAN;
    this.centric = Centric.GEO;
    this.coordinate = Coordinate.ECLIPTIC;
    this.pressure = 1013.25;
    this.temperature = 20;
  }
  */
  
  /** 最完整的 constructor */
  public HoroscopeContext(Time lmt , Location location , HouseSystem houseSystem , 
      Coordinate coordinate , Centric centric , double temperature , double pressure , 
      StarPositionWithAzimuthIF positionWithAzimuthImpl , HouseCuspIF houseCuspImpl , 
      ApsisWithAzimuthIF apsisWithAzimuthImpl, NodeType nodeType)
  {
    this.lmt = lmt;
    this.location = location;
    this.gmt = Time.getGMTfromLMT(lmt, location);
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
  public static HoroscopeContext getNewLmtHoroscope(Time newLmt , HoroscopeContext horoscopeContext)
  {
    return new HoroscopeContext(newLmt , horoscopeContext.getLocation() , horoscopeContext.getHouseSystem() , 
        horoscopeContext.getCoordinate() , horoscopeContext.getCentric() , horoscopeContext.getTemperature() , horoscopeContext.getPressure() ,
        horoscopeContext.getStarPositionWithAzimuthImpl() , horoscopeContext.getHouseCuspImpl() , 
        horoscopeContext.getApsisWithAzimuthImpl() , horoscopeContext.getNodeType());
  }
  
  /** 取得星體的位置以及地平方位角 */
  public PositionWithAzimuth getPosition(Point point)
  {
    starPositionWithAzimuthImpl.setLocation(location);
    starPositionWithAzimuthImpl.setCoordinate(coordinate);
    starPositionWithAzimuthImpl.setCentric(centric);
    
    PositionWithAzimuth position = null;
    if (point instanceof Star)
    {
      Star star = (Star) point;
      position = starPositionWithAzimuthImpl.getPositionWithAzimuth(star, gmt, location, temperature, pressure);
    }
    //if (point instanceof Planet) System.out.println("lmt = " + lmt + "\tHoroscopeContext.getPosition("+point+") = " + ZodiacDegreeDecorator.getSimpOutputString(position.getLongitude() , Locale.TAIWAN));
    return position;
  }
  
  private List<Star> getPointList()
  {
    List<Star> stars = new ArrayList<Star>();
    for (Planet planet : Planet.values) //行星
      stars.add(planet);
    for (LunarNode lunarNode : LunarNode.values) //月亮南北交點
      stars.add(lunarNode);
    for (LunarApsis lunarApsis : LunarApsis.values) //近地點,遠地點
      stars.add(lunarApsis);
    for (Asteroid asteroid : Asteroid.values) //小行星
      stars.add(asteroid);
    for (Hamburger hamburger : Hamburger.values) //漢堡星
      stars.add(hamburger);
    for (FixedStar fixedStar : FixedStar.values) //恆星
      stars.add(fixedStar);
    
    return stars;
  }
  
  
  /** 取得星盤 Horoscope */
  public Horoscope getHoroscope()
  {
    return new Horoscope(this , houseCuspImpl.getHouseCusps(Time.getGMTfromLMT(lmt, location), location, houseSystem));
  }

  /** 
   * 取得某星位於第幾宮
   * @param point
   * @return 1 <= point <= 12
   */
  public int getHouse(Point point)
  {
    PositionWithAzimuth position = getPosition(point);
    return getHoroscope().getHouse(position.getLongitude());
  }
  
  /**
   * <b>此 method 僅供測試或是 debug 使用，平常最好不要用 ！</b><br/>
   * 取得第幾宮內的星星列表 , 1 <= index <=12 , 並且按照黃道度數「由小到大」排序
   */
  public List<Point> getHousePoints(int index)
  {
    if (index < 1)
      return getHousePoints(index + 12);
    if (index > 12)
      return getHousePoints(index - 12);
    
    List<Point> resultList = new ArrayList<Point>();
    for (Point eachPoint : getPointList())
    {
      double longitude = getPosition(eachPoint).getLongitude();
      if (getHoroscope().getHouse(longitude) == index)
        resultList.add(eachPoint);
    }
    DegreeComparator comparator = new DegreeComparator(this);
    Collections.sort(resultList , comparator);
    //System.out.println("lmt = " + lmt + " , 取得第 " + index + " 宮的星體 : " + resultList);
    return resultList;
  }
  
  /** 取得某星 位於什麼星座 */
  public ZodiacSign getZodiacSign(Point point)
  {
    if (point == null)
      throw new RuntimeException("嘗試計算 null 所在的星座!");
    PositionWithAzimuth position = getPosition(point);
    return ZodiacSign.getZodiacSign(position.getLongitude());
  }
  
  public Time getGmt()
  {
    return Time.getGMTfromLMT(lmt, location);
  }
  
  public Time getLmt()
  {
    return lmt;
  }

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
