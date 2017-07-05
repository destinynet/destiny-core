package destiny.astrology;

import java.io.Serializable;

/**
 * Point 的位置 , 不限定 Star,
 * Point(南北交點) 也會有 Position
 */
public class Position implements Serializable {

  /** 座標系統 赤道/黃道/恆星 */
  // private Coordinate coordinate;
  protected final double lng;
  protected final double lat;
  protected final double distance; //in AU

  protected final double speedLng; //speed in lng (degree / day)
  protected final double speedLat; //speed in lat (degree / day)
  protected final double speedDistance; //speed in distance (AU / day)


  public Position(double lng, double lat, double distance, double speedLng, double speedLat, double speedDistance) {
    this.lng = Utils.getNormalizeDegree(lng);
    this.lat = lat;
    this.distance = distance;
    this.speedLng = speedLng;
    this.speedLat = speedLat;
    this.speedDistance = speedDistance;
  }



  /**
   * @return Returns the distance.
   */
  public double getDistance() 
  {
    return distance;
  }

  /**
   * @return Returns the lat.
   */
  public double getLat()
  {
    return lat;
  }

  /**
   * @return Returns the lng.
   */
  public double getLng()
  {
    return lng;
  }

  /**
   * @return Returns the speedDistance.
   */
  public double getSpeedDistance() 
  {
    return speedDistance;
  }

  /**
   * @return Returns the speedLat.
   */
  public double getSpeedLat()
  {
    return speedLat;
  }

  /**
   * @return Returns the speedLng.
   */
  public double getSpeedLng()
  {
    return speedLng;
  }


  @Override
  public String toString() {
    return "[Position " + "lng=" + lng + ", lat=" + lat + ", distance=" + distance + ", speedLng=" + speedLng + ", speedLat=" + speedLat + ", speedDistance=" + speedDistance + ']';
  }
}