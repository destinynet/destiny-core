package destiny.astrology;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Point 的位置 , 不限定 Star,
 * Point(南北交點) 也會有 Position
 */
public class Position implements Serializable {

  /** 座標系統 赤道/黃道/恆星 */
  // private Coordinate coordinate;
  private final double longitude;
  private final double latitude;
  private final double distance; //in AU

  private final double speedLongitude; //speed in longitude (degree / day)
  private final double speedLatitude; //speed in latitude (degree / day)
  private final double speedDistance; //speed in distance (AU / day)


  public Position(double lng, double lat, double distance, double speedLng, double speedLat, double speedDistance) {
    this.longitude = Utils.getNormalizeDegree(lng);
    this.latitude = lat;
    this.distance = distance;
    this.speedLongitude = speedLng;
    this.speedLatitude = speedLat;
    this.speedDistance = speedDistance;
  }
  
  @NotNull
  @Override
  public String toString()
  {
    return 
    "Longitude:" + longitude + " " +
    "Latitude:"  + latitude  + " " +
    "Distance:"  + distance  + " " +
    "SpeedLongitude:" + speedLongitude + " " +
    "SpeedLatitude:"  + speedLatitude  + " " +
    "SpeedDistance:"  + speedDistance;
  }

  /**
   * @return Returns the distance.
   */
  public double getDistance() 
  {
    return distance;
  }

  /**
   * @return Returns the latitude.
   */
  public double getLatitude() 
  {
    return latitude;
  }

  /**
   * @return Returns the longitude.
   */
  public double getLongitude() 
  {
    return longitude;
  }

  /**
   * @return Returns the speedDistance.
   */
  public double getSpeedDistance() 
  {
    return speedDistance;
  }

  /**
   * @return Returns the speedLatitude.
   */
  public double getSpeedLatitude() 
  {
    return speedLatitude;
  }

  /**
   * @return Returns the speedLongitude.
   */
  public double getSpeedLongitude() 
  {
    return speedLongitude;
  }


}