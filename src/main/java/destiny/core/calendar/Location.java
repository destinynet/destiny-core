package destiny.core.calendar;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;

import destiny.utils.AlignUtil;
import destiny.utils.LocaleUtils;
import destiny.utils.location.TimeZoneUtils;


/**
 * @author smallufo
 * Created on 2003/3/19 at 上午 12:35:41
 */
public class Location implements Serializable
{
  private EastWest eastWest = EastWest.EAST;
  private int     longitudeDegree = 121;
  private int     longitudeMinute = 30;
  private double  longitudeSecond = 0;

  private NorthSouth northSouth = NorthSouth.NORTH;
  private int     latitudeDegree = 25;
  private int     latitudeMinute = 03;
  private double  latitudeSecond = 0;
  
  /** 時區 */
  private TimeZone timeZone = TimeZone.getDefault();

  //private int minuteOffset = 8*60 ; //與 GMT 時差 , 內定為 8.0 小時x60分

  private double altitudeMeter = 0; //高度（公尺）

  private static Map<Locale , Location> locMap = Collections.synchronizedMap(new HashMap<Locale , Location>());
  static
  {
    // de , 柏林
    locMap.put(Locale.GERMAN  , new Location(EastWest.EAST , 13 , 24 , NorthSouth.NORTH , 52 , 31 , TimeZone.getTimeZone("Europe/Berlin")));
    // de_DE , 柏林
    locMap.put(Locale.GERMANY , new Location(EastWest.EAST , 13 , 24 , NorthSouth.NORTH , 52 , 31 , TimeZone.getTimeZone("Europe/Berlin")));
    
    // en , 紐約
    locMap.put(Locale.ENGLISH , new Location(EastWest.WEST , 73 , 58 , NorthSouth.NORTH , 40 , 47 , TimeZone.getTimeZone("America/New_York")));
    // en_AU , 雪梨
    locMap.put(new Locale("en" , "AU") , new Location(EastWest.EAST , 151 , 12 , 40 , NorthSouth.SOUTH , 33 , 51 , 36 , TimeZone.getTimeZone("Australia/Sydney")));
    // en_BW , 波札那 Botswana 
    locMap.put(new Locale("en" , "BW") , new Location(EastWest.EAST , 25 , 55 , NorthSouth.SOUTH , 24 , 40 , TimeZone.getTimeZone("Africa/Gaborone")));
    // en_CA , 多倫多
    locMap.put(Locale.CANADA, new Location(EastWest.WEST , 79 , 24 , NorthSouth.NORTH , 43 , 40 , TimeZone.getTimeZone("America/Toronto")));
    // en_DK , 丹麥 哥本哈根 Copenhagen
    locMap.put(new Locale("en" , "DK") , new Location(EastWest.EAST , 12 , 34 , NorthSouth.NORTH , 55 , 43 , TimeZone.getTimeZone("Europe/Copenhagen")));
    // en_GB , 倫敦
    locMap.put(Locale.UK , new Location(EastWest.WEST , 0 , 7 , NorthSouth.NORTH , 51 , 30 , TimeZone.getTimeZone("Europe/London")));
    // en_HK , 香港    
    locMap.put(new Locale("en" , "HK"), new Location(EastWest.EAST , 114 , 12 , NorthSouth.NORTH , 22 , 16 , TimeZone.getTimeZone("Asia/Hong_Kong")));
    // en_IE , 愛爾蘭 Ireland , 都柏林 Dublin
    locMap.put(new Locale("en" , "IE"), new Location(EastWest.WEST , 6.2592 , NorthSouth.NORTH , 53.3472 , TimeZone.getTimeZone("Europe/Dublin")));
    // en_MY , 馬來西亞 , 吉隆坡
    locMap.put(new Locale("en" , "MY"), new Location(EastWest.EAST , 101 , 42 , NorthSouth.NORTH , 3 , 8 , TimeZone.getTimeZone("Asia/Kuala_Lumpur")));
    // en_NZ , 紐西蘭 , 奧克蘭 Auckland (最大城市)
    locMap.put(new Locale("en" , "NZ"), new Location(EastWest.EAST , 174 , 45 , NorthSouth.SOUTH , 36 , 52 , TimeZone.getTimeZone("Pacific/Auckland")));
    // en_PH , 菲律賓 , 馬尼拉
    locMap.put(new Locale("en" , "PH"), new Location(EastWest.EAST , 121 , 0 , NorthSouth.NORTH , 14 , 35 , TimeZone.getTimeZone("Asia/Manila")));
    // en_SG , 新加坡
    locMap.put(new Locale("en" , "SG"), new Location(EastWest.EAST , 103 , 51 , NorthSouth.NORTH , 1 , 17 , TimeZone.getTimeZone("Asia/Singapore")));
    // en_US , 紐約
    locMap.put(Locale.US , new Location(EastWest.WEST , 73 , 58 , NorthSouth.NORTH , 40 , 47 , TimeZone.getTimeZone("America/New_York")));
    // en_ZA , 南非 , 約翰尼斯堡
    locMap.put(new Locale("en" , "ZA"), new Location(EastWest.EAST , 27 , 54 , NorthSouth.SOUTH , 26 , 8 , TimeZone.getTimeZone("Africa/Johannesburg")));
    // en_ZW , 辛巴威 , 哈拉雷
    locMap.put(new Locale("en" , "ZW"), new Location(EastWest.EAST , 31 , 3 , NorthSouth.SOUTH , 17 , 50 , TimeZone.getTimeZone("Africa/Harare")));

    // fr , 巴黎
    locMap.put(Locale.FRENCH , new Location(EastWest.EAST , 2 , 20 , NorthSouth.NORTH , 48 , 52 , TimeZone.getTimeZone("Europe/Paris")));
    // fr_FR , 巴黎
    locMap.put(Locale.FRANCE , new Location(EastWest.EAST , 2 , 20 , NorthSouth.NORTH , 48 , 52 , TimeZone.getTimeZone("Europe/Paris")));

    // it , 羅馬
    locMap.put(Locale.ITALIAN , new Location(EastWest.EAST , 12 , 29 , NorthSouth.NORTH , 41 , 54 , TimeZone.getTimeZone("Europe/Rome")));
    // it_IT , 羅馬
    locMap.put(Locale.ITALY   , new Location(EastWest.EAST , 12 , 29 , NorthSouth.NORTH , 41 , 54 , TimeZone.getTimeZone("Europe/Rome")));
    
    
    // ja , 東京
    locMap.put(Locale.JAPANESE , new Location(EastWest.EAST , 139 , 46 , 0 , NorthSouth.NORTH , 35 , 40 , 50, TimeZone.getTimeZone("Asia/Tokyo")));
    // ja_JP , 東京
    locMap.put(Locale.JAPAN    , new Location(EastWest.EAST , 139 , 45 , 0 , NorthSouth.NORTH , 35 , 40 , 0, TimeZone.getTimeZone("Asia/Tokyo")));
    
    // ko , 首爾
    locMap.put(Locale.KOREAN , new Location(EastWest.EAST , 127 , 0 , NorthSouth.NORTH , 37 , 32 , TimeZone.getTimeZone("Asia/Seoul")));
    // ko_KR , 首爾
    locMap.put(Locale.KOREA  , new Location(EastWest.EAST , 127 , 0 , NorthSouth.NORTH , 37 , 32 , TimeZone.getTimeZone("Asia/Seoul")));
    
    
    // zh , 北京
    //locMap.put(Locale.CHINESE , new Location(EastWest.EAST , 116 , 23 , NorthSouth.NORTH , 39 , 55 , TimeZone.getTimeZone("Asia/Shanghai")));
    locMap.put(Locale.CHINESE , new Location(116.397 , 39.9075 , TimeZone.getTimeZone("Asia/Harbin")));
    
    
    // zh_CN , PRC == CHINA == SIMPLIFIED_CHINESE , 北京
    locMap.put(Locale.CHINA   , new Location(EastWest.EAST , 116 , 23 , NorthSouth.NORTH , 39 , 55 , TimeZone.getTimeZone("Asia/Shanghai")));
    // zh_HK , 香港
    locMap.put(new Locale("zh" , "HK"), new Location(EastWest.EAST , 114 , 12 , NorthSouth.NORTH , 22 , 16 , TimeZone.getTimeZone("Asia/Hong_Kong")));
    // zh_MO , 澳門
    locMap.put(new Locale("zh" , "MO"), new Location(EastWest.EAST , 113 , 35 , NorthSouth.NORTH , 22 , 14 , TimeZone.getTimeZone("Asia/Macao")));
    // zh_SG , 新加坡
    locMap.put(new Locale("zh" , "SG"), new Location(EastWest.EAST , 103 , 51 , NorthSouth.NORTH , 1 , 17 , TimeZone.getTimeZone("Asia/Singapore")));
    
    // zh_TW , TAIWAN == TRADITIONAL_CHINESE , 台北
    //locMap.put(Locale.TAIWAN , new Location(EastWest.EAST , 121 , 31 , NorthSouth.NORTH , 25 , 3 , TimeZone.getTimeZone("Asia/Taipei")));
    locMap.put(Locale.TAIWAN , new Location(EastWest.EAST , 121 , 31 , 30.0 , NorthSouth.NORTH , 25 , 2 , 20 , TimeZone.getTimeZone("Asia/Taipei")));
  }
  

  /** 從 Browser 傳入 locale , 找出該 Locale 內定的 Location */
  public Location(Locale locale)
  {
    Locale matchedLocale = LocaleUtils.getBestMatchingLocale(locale, locMap.keySet());
    Location matchedLocation = locMap.get(matchedLocale);
    this.eastWest = matchedLocation.eastWest;
    this.longitudeDegree = matchedLocation.longitudeDegree;
    this.longitudeMinute = matchedLocation.longitudeMinute;
    this.longitudeSecond = matchedLocation.longitudeSecond;
    this.northSouth = matchedLocation.northSouth;
    this.latitudeDegree = matchedLocation.latitudeDegree;
    this.latitudeMinute = matchedLocation.latitudeMinute;
    this.latitudeSecond = matchedLocation.latitudeSecond;
    this.timeZone = matchedLocation.timeZone;
    this.altitudeMeter = matchedLocation.altitudeMeter;
  }

  
  public Location()
  {
  }
  
  
  /** 最詳盡的 constructor */
  public Location(EastWest eastWest , int LongitudeDegree , int LongitudeMinute , double LongitudeSecond ,
      NorthSouth northSouth , int LatitudeDegree , int LatitudeMinute , double LatitudeSecond , double altitudeMeter , TimeZone timeZone)
  {
    this.eastWest = eastWest;
    this.longitudeDegree = LongitudeDegree;
    this.longitudeMinute = LongitudeMinute;
    this.longitudeSecond = LongitudeSecond;
    this.northSouth = northSouth;
    this.latitudeDegree = LatitudeDegree;
    this.latitudeMinute = LatitudeMinute;
    this.latitudeSecond = LatitudeSecond;
    this.timeZone = timeZone;
    this.altitudeMeter = altitudeMeter;
  }
  
  /** 大家比較常用的，只有「度、分」。省略「秒」以及「高度」 */
  public Location(EastWest eastWest , int LongitudeDegree , int LongitudeMinute ,
      NorthSouth northSouth , int LatitudeDegree , int LatitudeMinute , TimeZone timeZone )
  {
    this.eastWest = eastWest;
    this.longitudeDegree = LongitudeDegree;
    this.longitudeMinute = LongitudeMinute;

    this.northSouth = northSouth;
    this.latitudeDegree = LatitudeDegree;
    this.latitudeMinute = LatitudeMinute;

    this.timeZone = timeZone;  
  }

  
  /** 省略高度 */
  public Location(EastWest eastWest , int LongitudeDegree , int LongitudeMinute , double LongitudeSecond ,
      NorthSouth northSouth , int LatitudeDegree , int LatitudeMinute , double LatitudeSecond , TimeZone timeZone )
  {
    this.eastWest = eastWest;
    this.longitudeDegree = LongitudeDegree;
    this.longitudeMinute = LongitudeMinute;
    this.longitudeSecond = LongitudeSecond;

    this.northSouth = northSouth;
    this.latitudeDegree = LatitudeDegree;
    this.latitudeMinute = LatitudeMinute;
    this.latitudeSecond = LatitudeSecond;

    this.timeZone = timeZone;
  }
  
  /** 比較省略的 constructor  , 去除東西經、南北緯 , 其值由 經度/緯度的正負去判斷 */
  public Location(int longitudeDegree , int longitudeMinute , double longitudeSecond , int latitudeDegree , int latitudeMinute , double latitudeSecond , TimeZone timeZone)
  {
    this.eastWest = (longitudeDegree >=0) ? EastWest.EAST : EastWest.WEST;
    this.longitudeDegree = Math.abs(longitudeDegree);
    this.longitudeMinute = longitudeMinute;
    this.longitudeSecond = longitudeSecond;
    
    this.northSouth = (latitudeDegree >=0) ? NorthSouth.NORTH : NorthSouth.SOUTH;
    this.latitudeDegree = Math.abs(latitudeDegree);
    this.latitudeMinute = latitudeMinute;
    this.latitudeSecond = latitudeSecond;
    
    this.timeZone = timeZone;
  }
  
  /** 較省略的 constructor , 度數以 double 取代 */
  public Location(EastWest eastWest , double longitude, NorthSouth northSouth , double latitude , TimeZone timeZone)
  {
    this.eastWest = eastWest;
    this.longitudeDegree = (int) Math.abs(longitude);
    this.longitudeMinute = (int) ((Math.abs(longitude) - longitudeDegree) * 60);
    this.longitudeSecond = Math.abs(longitude)*3600 - longitudeDegree*3600 - longitudeMinute*60; 
    
    this.northSouth = northSouth;
    this.latitudeDegree = (int) Math.abs(latitude);
    this.latitudeMinute = (int) ((Math.abs(latitude) - latitudeDegree) * 60);
    this.latitudeSecond = Math.abs(latitude)*3600 - latitudeDegree*3600 - latitudeMinute*60;
    
    this.timeZone = timeZone;
  }
  
  /** 更省略的 constructor */
  public Location(double longitude, double latitude , TimeZone timeZone)
  {
    this.eastWest = (longitude >= 0 ) ? EastWest.EAST : EastWest.WEST;
    this.longitudeDegree = (int) Math.abs(longitude);
    this.longitudeMinute = (int) ((Math.abs(longitude) - longitudeDegree) * 60);
    this.longitudeSecond = Math.abs(longitude)*3600 - longitudeDegree*3600 - longitudeMinute*60;
    
    this.northSouth = (latitude >=0 ) ? NorthSouth.NORTH : NorthSouth.SOUTH;
    this.latitudeDegree = (int) Math.abs(latitude);
    this.latitudeMinute = (int) ((Math.abs(latitude) - latitudeDegree) * 60);
    this.latitudeSecond = Math.abs(latitude)*3600 - latitudeDegree*3600 - latitudeMinute*60;
    
    this.timeZone = timeZone;
  }
  
  /** 
   * 利用 debug String 建立 Location , 缺點：「秒」只限制在小數點下兩位數
   * 
   * 新款格式
   * 012345678901234567890123456789012
   * +DDDMMSSSSS+DDMMSSSSS TimeZone A~
   * 
   * 舊款格式 
   * 0123456789012345678901234567
   * +DDDMMSSSSS+DDMMSSSSS+OOO A~
   * 
   * 分辨方法：如果能以空白切三段，就是新的，否則就是舊的。
   */
  public Location(String s)
  {
    char ew = s.charAt(0);
    if (ew == '+')
      this.eastWest = EastWest.EAST;
    else if (ew == '-')
      this.eastWest = EastWest.WEST;
    else
      throw new RuntimeException("EW not correct : " + ew);
    
    this.longitudeDegree = Integer.valueOf(s.substring(1, 4).trim()).intValue();
    this.longitudeMinute = Integer.valueOf(s.substring(4, 6).trim()).intValue();
    this.longitudeSecond = Double.valueOf(s.substring(6, 11).trim()).doubleValue();
    
    char ns = s.charAt(11);
    if (ns == '+')
      this.northSouth = NorthSouth.NORTH;
    else if (ns == '-')
      this.northSouth = NorthSouth.SOUTH;
    else
      throw new RuntimeException("ns not correct : " + ns);
    
    this.latitudeDegree = Integer.valueOf(s.substring(12, 14).trim()).intValue();
    this.latitudeMinute = Integer.valueOf(s.substring(14, 16).trim()).intValue();
    this.latitudeSecond = Double.valueOf(s.substring(16, 21).trim()).doubleValue();
    
    //包含了 高度以及時區
    String altitudeAndTimezone = s.substring(21);
    //System.out.println("altitudeAndTimezone = '" + altitudeAndTimezone+"'");
    
    StringTokenizer st = new StringTokenizer(altitudeAndTimezone , " ");
    String firstToken = st.nextToken();
    String restTokens = altitudeAndTimezone.substring( altitudeAndTimezone.indexOf(firstToken)+firstToken.length()+1).trim();
    //System.out.println("firstToken = '" + firstToken + "' , rest = '" + restTokens+"'");
    
    //檢查 restTokens 是否能轉為 double，如果能的話，代表是舊款 , 否則就是新款
    try
    {
      this.altitudeMeter = Double.parseDouble(restTokens);
      //parse 成功，代表舊款
      if (firstToken.charAt(0) == '+')
        this.timeZone = TimeZoneUtils.getTimeZone(Integer.parseInt(firstToken.substring(1)));
      else
        this.timeZone = TimeZoneUtils.getTimeZone(Integer.parseInt(firstToken));
    }
    catch(NumberFormatException e)
    {
      //新款
      this.altitudeMeter = Double.parseDouble(firstToken);
      this.timeZone = TimeZone.getTimeZone(restTokens);
    }

  }
  
  /** 
   * <pre>
   * 利用 debug String 建立 Location , 缺點：「秒」只限制在小數點下兩位數 
   * 
   * 新款格式
   * 01234567890123456789012345678901234567
   * +DDDMMSSSSS+DDMMSSSSS Alt~ TimeZone~ 
   * 範例 :
   * +1213012.34+25 312.34 12.3456 Asia/Taipei 
   * 
   * 舊款格式 
   * 
   * 0123456789012345678901234567
   * +DDDMMSSSSS+DDMMSSSSS+OOO A~
   * 範例 :
   * +1213012.34+25 312.34 480 12.3456
   * 
   * 分辨方法：如果能以空白切三段，就是新的，否則就是舊的。
   * 
   * </pre>
   */
  public static Location get(String s)
  {
    Location loc = new Location();
    try
    {
      char ew = s.charAt(0);
      if (ew == '+')
        loc.setEastWest(EastWest.EAST);
      else if (ew == '-')
        loc.setEastWest(EastWest.WEST);
      
      loc.setLongitudeDegree(Integer.valueOf(s.substring(1, 4).trim()).intValue());
      loc.setLongitudeMinute(Integer.valueOf(s.substring(4, 6).trim()).intValue());
      loc.setLongitudeSecond(Double.valueOf(s.substring(6, 11).trim()).doubleValue());
      
      char ns = s.charAt(11);
      if (ns == '+')
        loc.setNorthSouth(NorthSouth.NORTH);
      else if (ns == '-')
        loc.setNorthSouth(NorthSouth.SOUTH);

      loc.setLatitudeDegree(Integer.valueOf(s.substring(12, 14).trim()).intValue());
      loc.setLatitudeMinute(Integer.valueOf(s.substring(14, 16).trim()).intValue());
      loc.setLatitudeSecond(Double.valueOf(s.substring(16, 21).trim()).doubleValue());
      
      //包含了 高度以及時區
      String altitudeAndTimezone = s.substring(21);
      //System.out.println("altitudeAndTimezone = '" + altitudeAndTimezone+"'");
      
      StringTokenizer st = new StringTokenizer(altitudeAndTimezone , " ");
      String firstToken = st.nextToken();
      String restTokens = altitudeAndTimezone.substring( altitudeAndTimezone.indexOf(firstToken)+firstToken.length()+1).trim();
      //System.out.println("firstToken = '" + firstToken + "' , rest = '" + restTokens+"'");
      
      //檢查 restTokens 是否能轉為 double，如果能的話，代表是舊款 , 否則就是新款
      try
      {
        loc.setAltitudeMeter(Double.parseDouble(restTokens));
        //parse 成功，代表舊款
        if (firstToken.charAt(0) == '+')
          loc.setTimeZone(TimeZoneUtils.getTimeZone(Integer.parseInt(firstToken.substring(1))));
        else
          loc.setTimeZone(TimeZoneUtils.getTimeZone(Integer.parseInt(firstToken)));
      }
      catch(NumberFormatException e)
      {
        //新款
        loc.setAltitudeMeter(Double.parseDouble(firstToken));
        loc.setTimeZone(TimeZone.getTimeZone(restTokens));
      }
    }
    catch(RuntimeException ignored)
    {
    }
    return loc;
  }
  
  
  public String getDebugString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(eastWest == EastWest.EAST ? '+' : '-');
    sb.append(AlignUtil.alignRight(this.longitudeDegree, 3 , ' '));
    sb.append(AlignUtil.alignRight(this.longitudeMinute, 2 , ' '));
    sb.append(AlignUtil.alignRight(this.longitudeSecond, 5 , ' '));
    
    sb.append(northSouth == NorthSouth.NORTH ? '+' : '-');
    sb.append(AlignUtil.alignRight(this.latitudeDegree, 2 , ' '));
    sb.append(AlignUtil.alignRight(this.latitudeMinute, 2 , ' '));
    sb.append(AlignUtil.alignRight(this.latitudeSecond, 5 , ' '));
    
    sb.append(" " + this.altitudeMeter);
    //舊：sb.append(AlignUtil.alignRight(this.minuteOffset, 4 , ' '));
    sb.append(' ' + timeZone.getID());
    
    return sb.toString();
  }
  
  /** 直接設定經度 */
  public void setLongitude(double longitude)
  {
    this.eastWest = longitude >= 0 ? EastWest.EAST : EastWest.WEST;
    this.longitudeDegree = (int) Math.abs(longitude);
    this.longitudeMinute = (int) ((Math.abs(longitude) - longitudeDegree) * 60);
    this.longitudeSecond = Math.abs(longitude)*3600 - longitudeDegree*3600 - longitudeMinute*60; 
  }

  /**
   * @return 取得經度，in double，包含正負值
   */
  public double getLongitude()
  {
    double result = 0;
    result = longitudeDegree + ((double)longitudeMinute)/60 + longitudeSecond/3600;
    if (eastWest == EastWest.WEST)
      result = 0-result;
    return result;
  }

  /** 直接設定緯度 */
  public void setLatitude(double latitude)
  {
    this.northSouth = latitude >=0 ? NorthSouth.NORTH : NorthSouth.SOUTH;
    this.latitudeDegree = (int) Math.abs(latitude);
    this.latitudeMinute = (int) ((Math.abs(latitude) - latitudeDegree) * 60);
    this.latitudeSecond = Math.abs(latitude)*3600 - latitudeDegree*3600 - latitudeMinute*60;
  }
  
  /**
   * @return 取得緯度，in double，包含正負值
   */
  public double getLatitude()
  {
    double result = 0;
    result = latitudeDegree + ((double)latitudeMinute)/60 + latitudeSecond/3600;
    if (northSouth == NorthSouth.SOUTH)
      result = 0-result;
    return result;
  }

  public boolean isEast() { return eastWest==EastWest.EAST ? true : false; }
  public int getLongitudeDegree() { return this.longitudeDegree ; }
  public int getLongitudeMinute() { return this.longitudeMinute ; }
  public double getLongitudeSecond() { return this.longitudeSecond ; }

  public boolean isNorth() { return northSouth==NorthSouth.NORTH ? true : false; }
  public int getLatitudeDegree() { return this.latitudeDegree ; }
  public int getLatitudeMinute() { return this.latitudeMinute ; }
  public double getLatitudeSecond() { return this.latitudeSecond ; }

  public void setLatitudeDegree(int latitudeDegree) { this.latitudeDegree = latitudeDegree; }
  public void setLatitudeMinute(int latitudeMinute) { this.latitudeMinute = latitudeMinute; }
  public void setLatitudeSecond(double latitudeSecond) { this.latitudeSecond = latitudeSecond; }
  public void setLongitudeDegree(int longitudeDegree) { this.longitudeDegree = longitudeDegree; }
  public void setLongitudeMinute(int longitudeMinute) { this.longitudeMinute = longitudeMinute; }
  public void setLongitudeSecond(double longitudeSecond) { this.longitudeSecond = longitudeSecond; }
  public TimeZone getTimeZone() { return timeZone; }
  public void setTimeZone(TimeZone timeZone) { this.timeZone = timeZone; }

  public double getAltitudeMeter() { return this.altitudeMeter; }
  public void setAltitudeMeter(double value) { this.altitudeMeter = value; }

  //public int getMinuteOffset() { return this.minuteOffset ; }
  //public void setMinuteOffset(int minuteOffset) { this.minuteOffset = minuteOffset; }
  
  /** 設定東經/西經 */
  public void setEastWest(EastWest value)  {    this.eastWest = value;  }
  public EastWest getEastWest()  {    return this.eastWest;  }

  /** 設定北緯/南緯 */
  public void setNorthSouth(NorthSouth value)  {    this.northSouth = value;  }
  public NorthSouth getNorthSouth() { return this.northSouth; }

  @Override
  public String toString()
  {
    return LocationDecorator.getOutputString(this , Locale.getDefault());
  }//toString()

  public String toString(Locale locale)
  {
    return LocationDecorator.getOutputString(this , locale);
  }

  
  public enum EastWest
  {
    EAST("Location.EAST") , WEST("Location.WEST");
    
    private final static String resource = "destiny.core.calendar.Location";
    private String nameKey;

    private EastWest(String nameKey)
    {
      this.nameKey = nameKey;
    }

    @Override
    public String toString()
    {
      return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
    }
    
    public String toString(Locale locale)
    {
      return ResourceBundle.getBundle(resource , locale).getString(nameKey);
    }
    
    public final static EastWest getEastWest(char c)
    {
      if (c == 'E' || c == 'e')
        return EAST;
      if (c == 'W' || c == 'w')
        return WEST;
      throw new IllegalArgumentException("char '" + c + "' only accepts 'E' , 'e' , 'W' , or 'w'. ");
    }
  }

  public enum NorthSouth
  {
    NORTH("Location.NORTH") , SOUTH("Location.SOUTH");
    
    private final static String resource = "destiny.core.calendar.Location";
    private String nameKey;
    private NorthSouth(String nameKey)
    {
      this.nameKey = nameKey;
    }

    @Override
    public String toString()
    {
      return ResourceBundle.getBundle(resource , Locale.getDefault()).getString(nameKey);
    }
    
    public String toString(Locale locale)
    {
      return ResourceBundle.getBundle(resource , locale).getString(nameKey);
    }
    
    public final static NorthSouth getNorthSouth(char c)
    {
      if (c == 'N' || c == 'n')
        return NORTH;
      if (c == 'S' || c == 's')
        return SOUTH;
      throw new IllegalArgumentException("char '" + c + "' only accepts 'N' , 'n' , 'S' , or 's'. ");
    }
  }


  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((eastWest == null) ? 0 : eastWest.hashCode());
    result = prime * result + latitudeDegree;
    result = prime * result + latitudeMinute;
    long temp;
    temp = Double.doubleToLongBits(latitudeSecond);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + longitudeDegree;
    result = prime * result + longitudeMinute;
    temp = Double.doubleToLongBits(longitudeSecond);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((northSouth == null) ? 0 : northSouth.hashCode());
    //result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
    result = prime * result + timeZone.getRawOffset();
    return result;
  }


  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Location other = (Location) obj;
    if (eastWest == null)
    {
      if (other.eastWest != null)
        return false;
    }
    else if (!eastWest.equals(other.eastWest))
      return false;
    if (latitudeDegree != other.latitudeDegree)
      return false;
    if (latitudeMinute != other.latitudeMinute)
      return false;
    if (Double.doubleToLongBits(latitudeSecond) != Double.doubleToLongBits(other.latitudeSecond))
      return false;
    if (longitudeDegree != other.longitudeDegree)
      return false;
    if (longitudeMinute != other.longitudeMinute)
      return false;
    if (Double.doubleToLongBits(longitudeSecond) != Double.doubleToLongBits(other.longitudeSecond))
      return false;
    if (northSouth == null)
    {
      if (other.northSouth != null)
        return false;
    }
    else if (!northSouth.equals(other.northSouth))
      return false;
    if (timeZone == null)
    {
      if (other.timeZone != null)
        return false;
    }
    //else if (!timeZone.equals(other.timeZone))
    else if (getTimeZone().getRawOffset() != other.getTimeZone().getRawOffset())
      return false;
    return true;
  }

  // 與 GMT 的時差 (分鐘) 
  public int getMinuteOffset()
  {
    return this.timeZone.getRawOffset() / (60*1000);
  }

  // 設定與 GMT 的時差 (分鐘)
  public void setMinuteOffset(int minuteOffset)
  {
    System.out.println("Location : tz 本來是 " + timeZone.getID() + " , 現在要被設為 " + TimeZoneUtils.getTimeZone(minuteOffset).getID());
    this.timeZone = TimeZoneUtils.getTimeZone(minuteOffset);
  }
  
}
