package destiny.astrology;

/** 
 * Point 的位置 , 不限定 Star,
 * Point(南北交點) 也會有 Position
 */
public class Position implements java.io.Serializable
{
  /** 座標系統 赤道/黃道/恆星 */
  // private Coordinate coordinate;
  private double longitude;
  private double latitude;
  private double distance; //in AU

  private double speedLongitude; //speed in longitude (degree / day)
  private double speedLatitude; //speed in latitude (degree / day)
  private double speedDistance; //speed in distance (AU / day)


  public Position(double longitude      , double latitude      , double distance     ,
                      double speedLongitude , double speedLatitude , double speedDistance )
  {
    this.longitude = Utils.getNormalizeDegree(longitude);
    this.latitude  = latitude ;
    this.distance  = distance ;
    this.speedLongitude = speedLongitude;
    this.speedLatitude  = speedLatitude ;
    this.speedDistance  = speedDistance ;
  }
  
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