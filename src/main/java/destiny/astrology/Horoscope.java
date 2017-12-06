/**
 * Created by smallufo on 2017-07-10.
 */
package destiny.astrology;

import destiny.core.calendar.JulDayResolver1582CutoverImpl;
import destiny.core.calendar.Location;
import destiny.core.calendar.TimeTools;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Horoscope implements Serializable {

  /**
   * GMT's Julian Day
   */
  private final double gmtJulDay;

  private final Location location;

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

  private final Map<Point, PositionWithAzimuth> positionMap;

  /**
   * 地盤 12宮 (1~12) , 每宮宮首在黃道幾度
   */
  private final Map<Integer, Double> cuspDegreeMap;

  private transient static Logger logger = LoggerFactory.getLogger(Horoscope.class);

  private final transient Function<Double , ChronoLocalDateTime> revJulDayFunc = JulDayResolver1582CutoverImpl.Companion::getLocalDateTimeStatic;


  public Horoscope(double gmtJulDay, Location location, HouseSystem houseSystem, Coordinate coordinate, Centric centric, double temperature, double pressure, Map<Point, PositionWithAzimuth> positionMap, Map<Integer, Double> cuspDegreeMap) {
    this.gmtJulDay = gmtJulDay;
    this.location = location;
    this.houseSystem = houseSystem;
    this.coordinate = coordinate;
    this.centric = centric;
    this.temperature = temperature;
    this.pressure = pressure;
    this.positionMap = positionMap;
    this.cuspDegreeMap = cuspDegreeMap;
  }

  public double getGmtJulDay() {
    return gmtJulDay;
  }

  /**
   * @return 取得 GMT 時刻
   */
  @NotNull
  public ChronoLocalDateTime getGmt() {
    return revJulDayFunc.apply(gmtJulDay);
  }

  /**
   * 承上，但帶入自訂的 reverse Julian Day converter
   */
  @NotNull
  public ChronoLocalDateTime getGmt(Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    return revJulDayFunc.apply(gmtJulDay);
  }

  /**
   * @return 取得 LMT 時刻
   */
  @NotNull
  public ChronoLocalDateTime getLmt() {
    return TimeTools.getLmtFromGmt(getGmt() , location);
  }

  /**
   * 承上，但帶入自訂的 reverse Julian Day converter
   */
  @NotNull
  public ChronoLocalDateTime getLmt(Function<Double , ChronoLocalDateTime> revJulDayFunc) {
    return TimeTools.getGmtFromLmt(getGmt(revJulDayFunc) , location);
  }



  @NotNull
  public Location getLocation() {
    return location;
  }

  @NotNull
  public HouseSystem getHouseSystem() {
    return houseSystem;
  }

  @NotNull
  public Coordinate getCoordinate() {
    return coordinate;
  }

  @NotNull
  public Centric getCentric() {
    return centric;
  }

  public double getTemperature() {
    return temperature;
  }

  public double getPressure() {
    return pressure;
  }

  /**
   * 取得第幾宮內的星星列表 , 1 <= index <=12 , 並且按照黃道度數「由小到大」排序
   */
  @NotNull
  public List<Point> getHousePoints(int index) {
    if (index < 1)
      return getHousePoints(index + 12);
    if (index > 12)
      return getHousePoints(index - 12);

    return positionMap.entrySet().stream()
      .filter(e -> getHouse(e.getValue().getLng()) == index)
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
  }

  public Set<Point> getPoints() {
    return positionMap.keySet();
  }

  /**
   * 取得第幾宮的宮首落於黃道幾度。
   *
   * @param cusp 1 <= cusp <= 12
   */
  public double getCuspDegree(int cusp) {
    if (cusp > 12)
      return getCuspDegree(cusp - 12);
    else if (cusp < 1)
      return getCuspDegree(cusp + 12);
    return cuspDegreeMap.get(cusp);
  }

  /**
   * 取得單一 Horoscope 中 , 任兩顆星的交角
   */
  public double getAngle(Point fromPoint, Point toPoint) {
    return getAngle(positionMap.get(fromPoint).getLng(), positionMap.get(toPoint).getLng());
  }

  /**
   * 取得此兩顆星，對於此交角 Aspect 的誤差是幾度
   * 例如兩星交角 175 度 , Aspect = 沖 (180) , 則 誤差 5 度
   */
  public double getAspectError(Point p1 , Point p2 , @NotNull Aspect aspect) {
    double angle = getAngle(p1 , p2); //其值必定小於等於 180度
    return Math.abs( aspect.getDegree() - angle);
  }


  /** 取得一顆星體 Point / Star 在星盤上的角度 */
  public PositionWithAzimuth getPositionWithAzimuth(Point point) {
    return positionMap.get(point);
  }

    /**
   * 取得一連串星體的位置（含地平方位角）
   */
  public Map<Star , PositionWithAzimuth> getPositionWithAzimuth(@NotNull List<Star> stars) {
    return positionMap.entrySet().stream()
      .filter(entry -> entry.getKey() instanceof Star && stars.contains(entry.getKey()))
      .map(e -> new AbstractMap.SimpleEntry<>((Star) e.getKey() , e.getValue()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }


  public Map<Point , PositionWithAzimuth> getPositionWithAzimuth(Class<? extends Point> clazz) {
    return positionMap.entrySet().stream()
      .filter(entry -> entry.getKey().getClass().equals(clazz))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * 取得所有行星 {@link Planet} 的位置
   */
  public Map<Planet , PositionWithAzimuth> getPlanetPositionWithAzimuth() {
    return positionMap.entrySet().stream()
      .filter(entry -> entry.getKey() instanceof Planet)
      .map(e -> new AbstractMap.SimpleEntry<>((Planet) e.getKey(), e.getValue()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * 取得所有小行星 {@link Asteroid} 的位置
   */
  public Map<Asteroid , PositionWithAzimuth> getAsteroidPositionWithAzimuth() {
    return positionMap.entrySet().stream()
      .filter(entry -> entry.getKey() instanceof Asteroid)
      .map(e -> new AbstractMap.SimpleEntry<>((Asteroid) e.getKey(), e.getValue()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * 取得八個漢堡學派虛星 {@link Hamburger} 的位置
   */
  public Map<Hamburger , PositionWithAzimuth> getHamburgerPositionWithAzimuth() {
    return positionMap.entrySet().stream()
      .filter(entry -> entry.getKey() instanceof Hamburger)
      .map(e -> new AbstractMap.SimpleEntry<>((Hamburger) e.getKey(), e.getValue()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * 黃道幾度，落於第幾宮 ( 1 <= house <= 12 )
   */
  public int getHouse(double degree) {
    for (int i = 1; i <= 11; i++) {
      if (Math.abs(cuspDegreeMap.get(i+1) - cuspDegreeMap.get(i)) < 180) {
        //沒有切換360度的問題
        if (cuspDegreeMap.get(i) <= degree && degree < cuspDegreeMap.get(i + 1))
          return i;
      }
      else {
        //切換360度
        if (
            (cuspDegreeMap.get(i) <= degree && degree < (cuspDegreeMap.get(i + 1) + 360)) ||
            (cuspDegreeMap.get(i) <= (degree + 360) && degree < cuspDegreeMap.get(i + 1))
          )
          return i;
      }
    }
    return 12;
  } //getHouse()

  /**
   * @param point 取得此星體在第幾宮
   */
  public Optional<Integer> getHouse(Point point) {
    Optional<PositionWithAzimuth> pos = Optional.ofNullable(positionMap.get(point));
    return pos.map(Position::getLng).map(this::getHouse);
  }

  /** 取得星體的位置以及地平方位角 */
  public Optional<PositionWithAzimuth> getPosition(Point point)  {
    return Optional.ofNullable(positionMap.get(point));
  }

  /** 取得某星 位於什麼星座 */
  public Optional<ZodiacSign> getZodiacSign(Point point) {
    return getPosition(point)
      .map(pos -> ZodiacSign.getZodiacSign(pos.getLng()));
  }

  /**
   * ========================== 以下為 static utility methods ==========================
   */


  /**
   * @return 計算黃道帶上兩個度數的交角 , 其值必定小於等於 180度
   */
  public static double getAngle(double from, double to) {
    if (from - to >= 180)
      return (360 - from + to);
    else if (from - to >= 0)
      return (from - to);
    else if (from - to >= -180)
      return (to - from);
    else // (from - to < -180)
      return (from + 360 - to);
  }

  /**
   * @return 計算 from 是否在 to 的東邊 (度數小，為東邊) , true 就是東邊 , false 就是西邊(含對沖/合相)
   */
  public static boolean isOriental(double from, double to) {
    if (from < to && to - from < 180)
      return true;
    else
      return from > to && from - to > 180;
  }

  /**
   * @return 計算 from 是否在 to 的西邊 (度數大，為西邊) , true 就是西邊 , false 就是東邊(含對沖/合相)
   */
  public static boolean isOccidental(double from, double to) {
    if (from < to && to - from > 180)
      return true;
    else
      return from > to && from - to < 180;
  }
}
