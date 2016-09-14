/**
 * @author smallufo
 * Created on 2003/3/19 at 上午 12:35:41
 */
package destiny.core.calendar;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import destiny.tools.AlignUtil;
import destiny.tools.LocaleUtils;
import destiny.tools.location.TimeZoneUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class Location implements Serializable
{
  private EastWest eastWest = EastWest.EAST;
  private int     longitudeDegree = 121;
  private int     longitudeMinute = 30;
  private double  longitudeSecond = 0;

  private NorthSouth northSouth = NorthSouth.NORTH;
  private int     latitudeDegree = 25;
  private int     latitudeMinute = 3;
  private double  latitudeSecond = 0;
  
  /** 時區 */
  private TimeZone timeZone = TimeZone.getDefault();

  /** 2012/3/4 補加上 : 
   * 與 GMT 的時差 , 優先權高於 timeZone !
   */
  @Nullable
  private Integer minuteOffset = null;
  //private int minuteOffset = 8*60 ; //與 GMT 時差 , 內定為 8.0 小時x60分

  private double altitudeMeter = 0; //高度（公尺）

  private final static ImmutableMap<Locale , Location> locMap = new ImmutableMap.Builder<Locale , Location>()
    // de , 柏林
    .put(Locale.GERMAN  , new Location(EastWest.EAST , 13 , 24 , NorthSouth.NORTH , 52 , 31 , TimeZone.getTimeZone("Europe/Berlin")))
    // de_DE , 柏林
    .put(Locale.GERMANY , new Location(EastWest.EAST , 13 , 24 , NorthSouth.NORTH , 52 , 31 , TimeZone.getTimeZone("Europe/Berlin")))
    
    // en , 紐約
    .put(Locale.ENGLISH , new Location(EastWest.WEST , 73 , 58 , NorthSouth.NORTH , 40 , 47 , TimeZone.getTimeZone("America/New_York")))
    // en_AU , 雪梨
    .put(new Locale("en" , "AU") , new Location(EastWest.EAST , 151 , 12 , 40 , NorthSouth.SOUTH , 33 , 51 , 36 , TimeZone.getTimeZone("Australia/Sydney")))
    // en_BW , 波札那 Botswana 
    .put(new Locale("en" , "BW") , new Location(EastWest.EAST , 25 , 55 , NorthSouth.SOUTH , 24 , 40 , TimeZone.getTimeZone("Africa/Gaborone")))
    // en_CA , 多倫多
    .put(Locale.CANADA, new Location(EastWest.WEST , 79 , 24 , NorthSouth.NORTH , 43 , 40 , TimeZone.getTimeZone("America/Toronto")))
    // en_DK , 丹麥 哥本哈根 Copenhagen
    .put(new Locale("en" , "DK") , new Location(EastWest.EAST , 12 , 34 , NorthSouth.NORTH , 55 , 43 , TimeZone.getTimeZone("Europe/Copenhagen")))
    // en_GB , 倫敦
    .put(Locale.UK , new Location(EastWest.WEST , 0 , 7 , NorthSouth.NORTH , 51 , 30 , TimeZone.getTimeZone("Europe/London")))
    // en_HK , 香港    
    .put(new Locale("en" , "HK"), new Location(EastWest.EAST , 114 , 12 , NorthSouth.NORTH , 22 , 16 , TimeZone.getTimeZone("Asia/Hong_Kong")))
    // en_IE , 愛爾蘭 Ireland , 都柏林 Dublin
    .put(new Locale("en" , "IE"), new Location(EastWest.WEST , 6.2592 , NorthSouth.NORTH , 53.3472 , TimeZone.getTimeZone("Europe/Dublin")))
    // en_MY , 馬來西亞 , 吉隆坡
    .put(new Locale("en" , "MY"), new Location(EastWest.EAST , 101 , 42 , NorthSouth.NORTH , 3 , 8 , TimeZone.getTimeZone("Asia/Kuala_Lumpur")))
    // en_NZ , 紐西蘭 , 奧克蘭 Auckland (最大城市)
    .put(new Locale("en" , "NZ"), new Location(EastWest.EAST , 174 , 45 , NorthSouth.SOUTH , 36 , 52 , TimeZone.getTimeZone("Pacific/Auckland")))
    // en_PH , 菲律賓 , 馬尼拉
    .put(new Locale("en" , "PH"), new Location(EastWest.EAST , 121 , 0 , NorthSouth.NORTH , 14 , 35 , TimeZone.getTimeZone("Asia/Manila")))
    // en_SG , 新加坡
    .put(new Locale("en" , "SG"), new Location(EastWest.EAST , 103 , 51 , NorthSouth.NORTH , 1 , 17 , TimeZone.getTimeZone("Asia/Singapore")))
    // en_US , 紐約
    .put(Locale.US , new Location(EastWest.WEST , 73 , 58 , NorthSouth.NORTH , 40 , 47 , TimeZone.getTimeZone("America/New_York")))
    // en_ZA , 南非 , 約翰尼斯堡
    .put(new Locale("en" , "ZA"), new Location(EastWest.EAST , 27 , 54 , NorthSouth.SOUTH , 26 , 8 , TimeZone.getTimeZone("Africa/Johannesburg")))
    // en_ZW , 辛巴威 , 哈拉雷
    .put(new Locale("en" , "ZW"), new Location(EastWest.EAST , 31 , 3 , NorthSouth.SOUTH , 17 , 50 , TimeZone.getTimeZone("Africa/Harare")))

    // fr , 巴黎
    .put(Locale.FRENCH , new Location(EastWest.EAST , 2 , 20 , NorthSouth.NORTH , 48 , 52 , TimeZone.getTimeZone("Europe/Paris")))
    // fr_FR , 巴黎
    .put(Locale.FRANCE , new Location(EastWest.EAST , 2 , 20 , NorthSouth.NORTH , 48 , 52 , TimeZone.getTimeZone("Europe/Paris")))

    // it , 羅馬
    .put(Locale.ITALIAN , new Location(EastWest.EAST , 12 , 29 , NorthSouth.NORTH , 41 , 54 , TimeZone.getTimeZone("Europe/Rome")))
    // it_IT , 羅馬
    .put(Locale.ITALY   , new Location(EastWest.EAST , 12 , 29 , NorthSouth.NORTH , 41 , 54 , TimeZone.getTimeZone("Europe/Rome")))
    
    
    // ja , 東京
    .put(Locale.JAPANESE , new Location(EastWest.EAST , 139 , 46 , 0 , NorthSouth.NORTH , 35 , 40 , 50, TimeZone.getTimeZone("Asia/Tokyo")))
    // ja_JP , 東京
    .put(Locale.JAPAN    , new Location(EastWest.EAST , 139 , 45 , 0 , NorthSouth.NORTH , 35 , 40 , 0, TimeZone.getTimeZone("Asia/Tokyo")))
    
    // ko , 首爾
    .put(Locale.KOREAN , new Location(EastWest.EAST , 127 , 0 , NorthSouth.NORTH , 37 , 32 , TimeZone.getTimeZone("Asia/Seoul")))
    // ko_KR , 首爾
    .put(Locale.KOREA  , new Location(EastWest.EAST , 127 , 0 , NorthSouth.NORTH , 37 , 32 , TimeZone.getTimeZone("Asia/Seoul")))
    
    
    // zh , 北京
    //.put(Locale.CHINESE , new Location(EastWest.EAST , 116 , 23 , NorthSouth.NORTH , 39 , 55 , TimeZone.getTimeZone("Asia/Shanghai")))
    .put(Locale.CHINESE , new Location(116.397 , 39.9075 , TimeZone.getTimeZone("Asia/Harbin")))
    
    
    // zh_CN , PRC == CHINA == SIMPLIFIED_CHINESE , 北京
    .put(Locale.CHINA   , new Location(EastWest.EAST , 116 , 23 , NorthSouth.NORTH , 39 , 55 , TimeZone.getTimeZone("Asia/Shanghai")))
    // zh_HK , 香港
    .put(new Locale("zh" , "HK"), new Location(EastWest.EAST , 114 , 9 , 0, NorthSouth.NORTH , 22 , 17 , 2.4, TimeZone.getTimeZone("Asia/Hong_Kong")))
    // zh_MO , 澳門
    .put(new Locale("zh" , "MO"), new Location(EastWest.EAST , 113 , 35 , NorthSouth.NORTH , 22 , 14 , TimeZone.getTimeZone("Asia/Macao")))
    // zh_SG , 新加坡
    .put(new Locale("zh" , "SG"), new Location(EastWest.EAST , 103 , 51 , NorthSouth.NORTH , 1 , 17 , TimeZone.getTimeZone("Asia/Singapore")))
    
    // zh_TW , TAIWAN == TRADITIONAL_CHINESE , 台北市 景福門 (121.517675 , 25.039059)
    .put(Locale.TAIWAN, new Location(EastWest.EAST, 121, 31, 4.0, NorthSouth.NORTH, 25, 2, 21.0 , TimeZone.getTimeZone("Asia/Taipei")))
    .build();

  /** 從 Browser 傳入 locale , 找出該 Locale 內定的 Location */
  public Location(Locale locale)
  {
    Locale matchedLocale = LocaleUtils.getBestMatchingLocale(locale, locMap.keySet()).orElse(Locale.getDefault());
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

  /** 更省略的 constructor */
  public Location(double longitude, double latitude , int minuteOffset)
  {
    this.eastWest = (longitude >= 0 ) ? EastWest.EAST : EastWest.WEST;
    this.longitudeDegree = (int) Math.abs(longitude);
    this.longitudeMinute = (int) ((Math.abs(longitude) - longitudeDegree) * 60);
    this.longitudeSecond = Math.abs(longitude)*3600 - longitudeDegree*3600 - longitudeMinute*60;

    this.northSouth = (latitude >=0 ) ? NorthSouth.NORTH : NorthSouth.SOUTH;
    this.latitudeDegree = (int) Math.abs(latitude);
    this.latitudeMinute = (int) ((Math.abs(latitude) - latitudeDegree) * 60);
    this.latitudeSecond = Math.abs(latitude)*3600 - latitudeDegree*3600 - latitudeMinute*60;

    this.minuteOffset = minuteOffset;
  }

  
  /** 
   * 利用 debug String 建立 Location , 缺點：「秒」只限制在小數點下兩位數
   * 
   * 2012/3 之後 , 新款格式 : 新增 minuteOffset 欄位
   * 012345678901234567890123~
   * +DDDMMSSSSS+DDMMSSSSS Alt~ TimeZone~ minuteOffset
   * 
   * prior 2012/3 格式
   * 012345678901234567890123456789012
   * +DDDMMSSSSS+DDMMSSSSS Alt~ Timezone
   * 範例:
   * +12130  0.0+25 3  0.0 0.0 Asia/Taipei
   * 
   * 舊款格式 (2012/3之後不支援)
   * 0123456789012345678901234567
   * +DDDMMSSSSS+DDMMSSSSS+OOO A~
   * 
   * 分辨方法：如果能以空白切三段，就是新的，否則就是舊的。
   */
  public Location(@NotNull String s)
  {
    char ew = s.charAt(0);
    if (ew == '+')
      this.eastWest = EastWest.EAST;
    else if (ew == '-')
      this.eastWest = EastWest.WEST;
    else
      throw new RuntimeException("EW not correct : " + ew);
    
    this.longitudeDegree = Integer.valueOf(s.substring(1, 4).trim());
    this.longitudeMinute = Integer.valueOf(s.substring(4, 6).trim());
    this.longitudeSecond = Double.valueOf(s.substring(6, 11).trim());
    
    char ns = s.charAt(11);
    if (ns == '+')
      this.northSouth = NorthSouth.NORTH;
    else if (ns == '-')
      this.northSouth = NorthSouth.SOUTH;
    else
      throw new RuntimeException("ns not correct : " + ns);
    
    this.latitudeDegree = Integer.valueOf(s.substring(12, 14).trim());
    this.latitudeMinute = Integer.valueOf(s.substring(14, 16).trim());
    this.latitudeSecond = Double.valueOf(s.substring(16, 21).trim());
    
    //包含了 高度以及時區
    String altitudeAndTimezone = s.substring(21);
    //System.out.println("altitudeAndTimezone = '" + altitudeAndTimezone+"'");
    
    StringTokenizer st = new StringTokenizer(altitudeAndTimezone , " ");
    String firstToken = st.nextToken();
    // 2012/3 之後 , restToken 可能還會 append minuteOffset
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
      st = new StringTokenizer(restTokens , " ");
      if (st.countTokens() == 1)
        this.timeZone = TimeZone.getTimeZone(restTokens);
      else
      {
        // 2012/3 格式 : timeZone 之後，還附加 minuteOffset
        this.timeZone = TimeZone.getTimeZone(st.nextToken());
        this.minuteOffset = Integer.valueOf(st.nextToken());
      }
    }

  }
  
  @NotNull
  public static Location get(@NotNull String s)
  {
    return new Location(s);
  }
  
  /**
   * 2012/03 格式：
   * 012345678901234567890123456789012345678901234567890
   * +DDDMMSSSSS+DDMMSSSSS Alt~ TimeZone~ [minuteOffset]
   * 範例 :
   * +1213012.34+25 312.34 12.3456 Asia/Taipei 480
   * 尾方的 minuteOffset 為 optional , 如果有的話，會 override Asia/Taipei 的 minuteOffset
   */
  @NotNull
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
    
    sb.append(" ").append(this.altitudeMeter);
    //舊：sb.append(AlignUtil.alignRight(this.minuteOffset, 4 , ' '));
    sb.append(' ').append(timeZone.getID());
    if (minuteOffset != null)
      sb.append(" ").append(minuteOffset);
    
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

  public boolean isEast() { return eastWest == EastWest.EAST; }
  public int getLongitudeDegree() { return this.longitudeDegree ; }
  public int getLongitudeMinute() { return this.longitudeMinute ; }
  public double getLongitudeSecond() { return this.longitudeSecond ; }

  public boolean isNorth() { return northSouth == NorthSouth.NORTH; }
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
    
    private final static String resource = destiny.core.calendar.Location.class.getName();//"destiny.core.calendar.Location";
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
    
    public String toString(@NotNull Locale locale)
    {
      return ResourceBundle.getBundle(resource , locale).getString(nameKey);
    }
    
    @NotNull
    public static EastWest getEastWest(char c)
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
    
    public String toString(@NotNull Locale locale)
    {
      return ResourceBundle.getBundle(resource , locale).getString(nameKey);
    }
    
    @NotNull
    public static NorthSouth getNorthSouth(char c)
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
  public boolean equals(@Nullable Object obj)
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
    else if (!timeZone.equals(other.timeZone))
      return false;
    
    return Objects.equal(minuteOffset, other.minuteOffset);
  }

  /** 查詢， minuteOffset 是否被設定 (非null) */
  public boolean isMinuteOffsetSet()
  {
    return minuteOffset!=null;
  }
  
  // 與 GMT 的時差 (分鐘) 
  public int getMinuteOffset()
  {
    if (minuteOffset != null)
      return minuteOffset;
    else
      return this.timeZone.getRawOffset() / (60*1000);
  }

  // 設定與 GMT 的時差 (分鐘)
  /**
   * 2012/3/4 發現： 不能直接將 minuteOffset 換成 timezone , 因為 timezone 會受歷史因素/DST的影響
   * 有時強制指定 minuteOffset , 結果被轉成不正確的 TZ , 反而會受到歷史因素或是DST的影響
   */
  public void setMinuteOffset(int minuteOffset)
  {
    this.minuteOffset = minuteOffset;
//    System.out.println("Location : tz 本來是 " + timeZone.getID() + " , 現在要被設為 " + TimeZoneUtils.getTimeZone(minuteOffset).getID());
//    this.timeZone = TimeZoneUtils.getTimeZone(minuteOffset);
  }
  
}
