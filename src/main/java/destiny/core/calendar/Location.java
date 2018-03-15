/**
 * @author smallufo
 * Created on 2003/3/19 at 上午 12:35:41
 */
package destiny.core.calendar;

import com.google.common.collect.ImmutableMap;
import destiny.tools.AlignTools;
import destiny.tools.LocaleUtils;
import destiny.tools.location.TimeZoneUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

import static destiny.core.calendar.Location.EastWest.EAST;
import static destiny.core.calendar.Location.EastWest.WEST;
import static destiny.core.calendar.Location.NorthSouth.NORTH;
import static destiny.core.calendar.Location.NorthSouth.SOUTH;

public class Location implements Serializable {

  private final EastWest eastWest;// = EAST;
  private final int lngDeg;// = 121;
  private final int lngMin;// = 30;
  private final double lngSec;// = 0;

  private final NorthSouth northSouth;// = NORTH;
  private final int latDeg; //= 25;
  private final int latMin; // = 3;
  private final double latSec;// = 0;

  /**
   * TimeZone id , 例如 "Asia/Taipei"
   * 2017-04-09 起， 用以取代 TimeZone field , 降低記憶體用量
   * */
  private String tzid = "Asia/Taipei";

  /**
   * 2012-03-04 補加上 :
   * 強制覆蓋與 GMT 的時差 , 優先權高於 {@link #tzid} !
   */
  @Nullable
  private Integer minuteOffset = null;

  private double altitudeMeter = 0; //高度（公尺）

  private final static ImmutableMap<Locale , Location> locMap = new ImmutableMap.Builder<Locale , Location>()
    // de , 柏林
    .put(Locale.GERMAN  , new Location(EAST , 13 , 24 , NORTH , 52 , 31 , "Europe/Berlin"))
    // de_DE , 柏林
    .put(Locale.GERMANY , new Location(EAST , 13 , 24 , NORTH , 52 , 31 , "Europe/Berlin"))

    // en , 紐約 , 40.758899, -73.985131 , 時報廣場
    .put(Locale.ENGLISH , new Location(-73.985131 , 40.758899 , "America/New_York"))
    // en_US , 紐約
    .put(Locale.US , new Location(-73.985131 , 40.758899, "America/New_York"))

    // en_AU , 雪梨
    .put(new Locale("en" , "AU") , new Location(EAST , 151 , 12 , 40 , SOUTH , 33 , 51 , 36 , "Australia/Sydney"))
    // en_BW , 波札那 Botswana
    .put(new Locale("en" , "BW") , new Location(EAST , 25 , 55 , SOUTH , 24 , 40 , "Africa/Gaborone"))
    // en_CA , 多倫多
    .put(Locale.CANADA, new Location(WEST , 79 , 24 , NORTH , 43 , 40 , "America/Toronto"))
    // en_DK , 丹麥 哥本哈根 Copenhagen
    .put(new Locale("en" , "DK") , new Location(EAST , 12 , 34 , NORTH , 55 , 43 , "Europe/Copenhagen"))
    // en_GB , 倫敦
    .put(Locale.UK , new Location(WEST , 0 , 7 , NORTH , 51 , 30 , "Europe/London"))
    // en_HK , 香港
    .put(new Locale("en" , "HK"), new Location(EAST , 114.1735865 , NORTH , 22.2798721 , 0 , "Asia/Hong_Kong" , null))
    // en_IE , 愛爾蘭 Ireland , 都柏林 Dublin
    .put(new Locale("en" , "IE"), new Location(WEST , 6.2592 , NORTH , 53.3472 , 0, "Europe/Dublin", null))
    // en_MY , 馬來西亞 , 吉隆坡
    .put(new Locale("en" , "MY"), new Location(EAST , 101 , 42 , NORTH , 3 , 8 , "Asia/Kuala_Lumpur"))
    // en_NZ , 紐西蘭 , 奧克蘭 Auckland (最大城市)
    .put(new Locale("en" , "NZ"), new Location(EAST , 174 , 45 , SOUTH , 36 , 52 , "Pacific/Auckland"))
    // en_PH , 菲律賓 , 馬尼拉
    .put(new Locale("en" , "PH"), new Location(EAST , 121 , 0 , NORTH , 14 , 35 , "Asia/Manila"))
    // en_SG , 新加坡
    .put(new Locale("en" , "SG"), new Location(EAST , 103 , 51 , NORTH , 1 , 17 , "Asia/Singapore"))
    // en_ZA , 南非 , 約翰尼斯堡
    .put(new Locale("en" , "ZA"), new Location(EAST , 27 , 54 , SOUTH , 26 , 8 , "Africa/Johannesburg"))
    // en_ZW , 辛巴威 , 哈拉雷
    .put(new Locale("en" , "ZW"), new Location(EAST , 31 , 3 , SOUTH , 17 , 50 , "Africa/Harare"))

    // fr , 巴黎
    .put(Locale.FRENCH , new Location(EAST , 2 , 20 , NORTH , 48 , 52 , "Europe/Paris"))
    // fr_FR , 巴黎
    .put(Locale.FRANCE , new Location(EAST , 2 , 20 , NORTH , 48 , 52 , "Europe/Paris"))

    // it , 羅馬
    .put(Locale.ITALIAN , new Location(EAST , 12 , 29 , NORTH , 41 , 54 , "Europe/Rome"))
    // it_IT , 羅馬
    .put(Locale.ITALY   , new Location(EAST , 12 , 29 , NORTH , 41 , 54 , "Europe/Rome"))


    // ja , 東京
    .put(Locale.JAPANESE , new Location(EAST , 139 , 46 , 0 , NORTH , 35 , 40 , 50, "Asia/Tokyo"))
    // ja_JP , 東京
    .put(Locale.JAPAN    , new Location(EAST , 139 , 45 , 0 , NORTH , 35 , 40 , 0, "Asia/Tokyo"))

    // ko , 首爾
    .put(Locale.KOREAN , new Location(EAST , 127 , 0 , NORTH , 37 , 32 , "Asia/Seoul"))
    // ko_KR , 首爾
    .put(Locale.KOREA  , new Location(EAST , 127 , 0 , NORTH , 37 , 32 , "Asia/Seoul"))


    // zh , 北京
    //.put(Locale.CHINESE , new Location(EastWest.EAST , 116 , 23 , NorthSouth.NORTH , 39 , 55 , "Asia/Shanghai"))
    .put(Locale.CHINESE , new Location(116.397 , 39.9075 , "Asia/Harbin"))


    // zh_CN , PRC == CHINA == SIMPLIFIED_CHINESE , 北京
    .put(Locale.CHINA   , new Location(EAST , 116 , 23 , NORTH , 39 , 55 , "Asia/Shanghai"))
    // zh_HK , 香港
    .put(new Locale("zh" , "HK"), new Location(EAST , 114.1735865 , NORTH , 22.2798721  , 0 , "Asia/Hong_Kong" , null))
    // zh_MO , 澳門
    .put(new Locale("zh" , "MO"), new Location(EAST , 113 , 35 , NORTH , 22 , 14 , "Asia/Macao"))
    // zh_SG , 新加坡
    .put(new Locale("zh" , "SG"), new Location(EAST , 103 , 51 , NORTH , 1 , 17 , "Asia/Singapore"))

    // zh_TW , TAIWAN == TRADITIONAL_CHINESE , 台北市 景福門 (25.039059 , 121.517675) ==> 25°02'20.5"N 121°31'03.6"E
    .put(Locale.TAIWAN, new Location(121.517668 , 25.039030 , "Asia/Taipei"))
    .build();


  public static Location of(Locale locale) {
    Locale matchedLocale = LocaleUtils.getBestMatchingLocale(locale, locMap.keySet()).orElse(Locale.getDefault());
    Location loc = locMap.get(matchedLocale);
    return new Location(loc.eastWest , loc.lngDeg , loc.lngMin , loc.lngSec , loc.northSouth , loc.latDeg , loc.latMin , loc.latSec , loc.altitudeMeter , loc.tzid , loc.minuteOffset);
  }


  /**
   * 最詳盡的 constructor
   *
   * 參考 {@link #getDebugString()}
   * 2012/03 格式：
   * 012345678901234567890123456789012345678901234567890
   * +DDDMMSSSSS+DDMMSSSSS Alt~ TimeZone~ [minuteOffset]
   * */
  public Location(EastWest eastWest , int lngDeg , int lngMin , double lngSec ,
                  NorthSouth northSouth , int latDeg , int latMin , double latSec ,
                  double altitudeMeter , String tzid , @Nullable Integer minuteOffset) {
    this.eastWest = eastWest;
    this.lngDeg = lngDeg;
    this.lngMin = lngMin;
    this.lngSec = lngSec;
    this.northSouth = northSouth;
    this.latDeg = latDeg;
    this.latMin = latMin;
    this.latSec = latSec;
    this.tzid = tzid;
    this.altitudeMeter = altitudeMeter;
    this.minuteOffset = minuteOffset;
  }

  /** 省去 minuteOffset */
  public Location(EastWest eastWest , int lngDeg , int lngMin , double lngSec ,
                  NorthSouth northSouth , int latDeg , int latMin , double latSec ,
                  double altitudeMeter , String timeZone) {
    this(eastWest , lngDeg , lngMin , lngSec , northSouth , latDeg , latMin , latSec , altitudeMeter , timeZone , null);
  }

  /** 承上  , 以 TimeZone 傳入 , 儲存為 tzid */
  public Location(EastWest eastWest , int lngDeg , int lngMin , double lngSec ,
                  NorthSouth northSouth , int latDeg , int latMin , double latSec ,
                  double altitudeMeter , TimeZone timeZone) {
    this(eastWest , lngDeg , lngMin , lngSec , northSouth , latDeg , latMin , latSec , altitudeMeter , timeZone.getID());
  }

  /** 大家比較常用的，只有「度、分」。省略「秒」以及「高度」 */
  public Location(EastWest eastWest, int lngDeg, int lngMin,
                  NorthSouth northSouth, int latDeg, int latMin,
                  String timeZone) {
    this(eastWest , lngDeg , lngMin , 0, northSouth , latDeg , latMin , 0 , 0 , timeZone);
  }


  /** 省略高度 */
  public Location(EastWest eastWest , int lngDeg , int lngMin , double lngSec ,
                  NorthSouth northSouth , int latDeg , int latMin , double latSec ,
                  String tzid ) {
    this(eastWest , lngDeg , lngMin , lngSec , northSouth , latDeg , latMin , latSec , 0 , tzid);
  }

  /** 承上 , 以 TimeZone 傳入 */
  public Location(EastWest eastWest , int lngDeg , int lngMin , double lngSec ,
                  NorthSouth northSouth , int latDeg , int latMin , double latSec ,
                  TimeZone timeZone ) {
    this(eastWest , lngDeg , lngMin , lngSec , northSouth , latDeg , latMin , latSec , 0 , timeZone.getID());
  }

  /** 比較省略的 constructor  , 去除東西經、南北緯 , 其值由 經度/緯度的正負去判斷 */
  public Location(int lngDeg , int lngMin , double lngSec ,
                  int latDeg , int latMin , double latSec ,
                  TimeZone timeZone) {
    this(
      (lngDeg >=0) ? EAST : WEST , Math.abs(lngDeg) , lngMin , lngSec ,
      (latDeg >=0) ? NORTH : SOUTH , Math.abs(latDeg) , latMin , latSec ,
      0 ,
      timeZone.getID()
    );
  }

  /** 較省略的 constructor , 度數以 double 取代 */
  public Location(EastWest eastWest, double lng, NorthSouth northSouth, double lat,
                  double altMeter, String zoneId, @Nullable Integer minuteOffset) {
    this.eastWest = eastWest;
    this.lngDeg = (int) Math.abs(lng);
    this.lngMin = (int) ((Math.abs(lng) - lngDeg) * 60);
    this.lngSec = Math.abs(lng)*3600 - lngDeg *3600 - lngMin *60;

    this.northSouth = northSouth;
    this.latDeg = (int) Math.abs(lat);
    this.latMin = (int) ((Math.abs(lat) - latDeg) * 60);
    this.latSec = Math.abs(lat)*3600 - latDeg *3600 - latMin *60;

    this.altitudeMeter = altMeter;
    this.tzid = zoneId;
    this.minuteOffset = minuteOffset;
  }


  /** 更省略的 constructor */
  public Location(double lng, double lat , double altMeter , String tzid , @Nullable Integer minuteOffset) {
    this(
      (lng >= 0 ) ? EAST : WEST , lng ,
      (lat >=0 ) ? NORTH : SOUTH , lat ,
      altMeter, tzid, minuteOffset);
  }

  public Location(double lng, double lat ,  String tzid , @Nullable Integer minuteOffset) {
    this(
      (lng >= 0 ) ? EAST : WEST , lng ,
      (lat >=0 ) ? NORTH : SOUTH , lat ,
      0, tzid, minuteOffset);
  }

  public Location(double lng, double lat , double altMeter , String tzid) {
    this(lng , lat , altMeter , tzid , null);
  }

  public Location(double lng, double lat , String tzid) {
    this(lng , lat , 0 , tzid);
  }

  public Location(double lng, double lat , TimeZone timeZone , @Nullable Integer minuteOffset) {
    this(lng , lat , timeZone.getID() , minuteOffset);
  }

  public Location(double lng, double lat , TimeZone timeZone) {
    this(lng , lat , timeZone.getID() , null);
  }

  /** 沒有 tzid , 直接帶入 minuteOffset (優先度最高) */
  public Location(double lng, double lat , int minuteOffset) {
    this.eastWest = (lng >= 0 ) ? EAST : WEST;
    this.lngDeg = (int) Math.abs(lng);
    this.lngMin = (int) ((Math.abs(lng) - lngDeg) * 60);
    this.lngSec = Math.abs(lng)*3600 - lngDeg *3600 - lngMin *60;

    this.northSouth = (lat >=0 ) ? NORTH : SOUTH;
    this.latDeg = (int) Math.abs(lat);
    this.latMin = (int) ((Math.abs(lat) - latDeg) * 60);
    this.latSec = Math.abs(lat)*3600 - latDeg *3600 - latMin *60;

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
  public Location(@NotNull String s) {
    char ew = s.charAt(0);
    if (ew == '+')
      this.eastWest = EAST;
    else if (ew == '-')
      this.eastWest = WEST;
    else
      throw new RuntimeException("EW not correct : " + ew);

    this.lngDeg = Integer.valueOf(s.substring(1, 4).trim());
    this.lngMin = Integer.valueOf(s.substring(4, 6).trim());
    this.lngSec = Double.valueOf(s.substring(6, 11).trim());

    char ns = s.charAt(11);
    if (ns == '+')
      this.northSouth = NORTH;
    else if (ns == '-')
      this.northSouth = SOUTH;
    else
      throw new RuntimeException("ns not correct : " + ns);

    this.latDeg = Integer.valueOf(s.substring(12, 14).trim());
    this.latMin = Integer.valueOf(s.substring(14, 16).trim());
    this.latSec = Double.valueOf(s.substring(16, 21).trim());

    //包含了 高度以及時區
    String altitudeAndTimezone = s.substring(21);
    //System.out.println("altitudeAndTimezone = '" + altitudeAndTimezone+"'");

    StringTokenizer st = new StringTokenizer(altitudeAndTimezone , " ");
    String firstToken = st.nextToken();
    // 2012/3 之後 , restToken 可能還會 append minuteOffset
    String restTokens = altitudeAndTimezone.substring( altitudeAndTimezone.indexOf(firstToken)+firstToken.length()+1).trim();
    //System.out.println("firstToken = '" + firstToken + "' , rest = '" + restTokens+"'");

    //檢查 restTokens 是否能轉為 double，如果能的話，代表是舊款 , 否則就是新款
    try {
      this.altitudeMeter = Double.parseDouble(restTokens);
      //parse 成功，代表舊款
      if (firstToken.charAt(0) == '+')
        this.tzid = TimeZoneUtils.Companion.getTimeZone(Integer.parseInt(firstToken.substring(1))).getID();
      else
        this.tzid = TimeZoneUtils.Companion.getTimeZone(Integer.parseInt(firstToken)).getID();
    } catch (NumberFormatException e) {
      //新款
      this.altitudeMeter = Double.parseDouble(firstToken);
      st = new StringTokenizer(restTokens, " ");
      if (st.countTokens() == 1)
        this.tzid = restTokens;
      else {
        // 2012/3 格式 : timeZone 之後，還附加 minuteOffset
        this.tzid = st.nextToken();
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
  public String getDebugString() {
    StringBuilder sb = new StringBuilder();
    sb.append(eastWest == EAST ? '+' : '-');
    sb.append(StringUtils.leftPad(String.valueOf(this.lngDeg), 3 , ' '));
    sb.append(StringUtils.leftPad(String.valueOf(this.lngMin), 2 , ' '));
    sb.append(AlignTools.INSTANCE.alignRight(this.lngSec, 5 , ' '));

    sb.append(northSouth == NORTH ? '+' : '-');
    sb.append(StringUtils.leftPad(String.valueOf(this.latDeg), 2 , ' '));
    sb.append(StringUtils.leftPad(String.valueOf(this.latMin), 2 , ' '));
    sb.append(AlignTools.INSTANCE.alignRight(this.latSec, 5 , ' '));

    sb.append(" ").append(this.altitudeMeter);
    //舊：sb.append(AlignUtil.alignRight(this.minuteOffset, 4 , ' '));
    sb.append(' ').append(tzid);
    if (minuteOffset != null)
      sb.append(" ").append(minuteOffset);

    return sb.toString();
  }

  /**
   * @return 取得經度，in double，包含正負值
   */
  public double getLongitude() {
    return getLongitude(eastWest , lngDeg, lngMin, lngSec);
  }

  /** 將「經度」的 (東/西) 度、分、秒 轉為十進位 */
  public static double getLongitude(EastWest eastWest , int lngDeg , int lngMin , double lngSec ) {
    double result = lngDeg + ((double)lngMin / 60.0) + lngSec / 3600.0;
    if (eastWest == WEST)
      result = 0 - result;
    return result;
  }

  /**
   * @return 取得緯度，in double，包含正負值
   */
  public double getLatitude() {
    return getLatitude(northSouth , latDeg, latMin, latSec);
  }

  public static double getLatitude(NorthSouth northSouth , int latDeg , int latMin , double latSec ) {
    double result = latDeg + ((double) latMin / 60.0) + latSec / 3600.0;
    if (northSouth == SOUTH)
      result = 0 - result;
    return result;
  }

  /**
   * 取得經緯度的十進位表示法，先緯度、再精度
   */
  public String getDecimal() {
    StringBuilder sb = new StringBuilder();
    sb.append(getLatitude());
    sb.append(',');
    sb.append(getLongitude());
    return sb.toString();
  }

  public boolean isEast() { return eastWest == EAST; }

  public int getLngDeg() { return this.lngDeg; }

  public int getLngMin() { return this.lngMin; }

  public double getLngSec() { return this.lngSec; }

  public boolean isNorth() { return northSouth == NORTH; }

  public int getLatDeg() { return this.latDeg; }

  public int getLatMin() { return this.latMin; }

  public double getLatSec() { return this.latSec; }

  public TimeZone getTimeZone() { return TimeZone.getTimeZone(tzid); }

  public ZoneId getZoneId() {
    return ZoneId.of(tzid);
  }

  public double getAltitudeMeter() { return this.altitudeMeter; }

  public EastWest getEastWest() { return this.eastWest; }

  public NorthSouth getNorthSouth() { return this.northSouth; }

  @Override
  public String toString()
  {
    return LocationDecorator.INSTANCE.getOutputString(this , Locale.getDefault());
  }//toString()

  public String toString(Locale locale)
  {
    return LocationDecorator.INSTANCE.getOutputString(this , locale);
  }


  public enum EastWest {
    EAST("Location.EAST"), WEST("Location.WEST");

    private final static String resource = destiny.core.calendar.Location.class.getName();//"destiny.core.calendar.Location";

    private final String nameKey;

    EastWest(String nameKey) {
      this.nameKey = nameKey;
    }

    @Override
    public String toString() {
      return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey);
    }

    public String toString(@NotNull Locale locale) {
      return ResourceBundle.getBundle(resource, locale).getString(nameKey);
    }

    @NotNull
    public static EastWest getEastWest(char c) {
      if (c == 'E' || c == 'e')
        return EAST;
      if (c == 'W' || c == 'w')
        return WEST;
      throw new IllegalArgumentException("char '" + c + "' only accepts 'E' , 'e' , 'W' , or 'w'. ");
    }
  }

  public enum NorthSouth {
    NORTH("Location.NORTH"), SOUTH("Location.SOUTH");

    private final static String resource =  Location.class.getName();

    private final String nameKey;

    NorthSouth(String nameKey) {
      this.nameKey = nameKey;
    }

    @Override
    public String toString() {
      return ResourceBundle.getBundle(resource, Locale.getDefault()).getString(nameKey);
    }

    public String toString(@NotNull Locale locale) {
      return ResourceBundle.getBundle(resource, locale).getString(nameKey);
    }

    @NotNull
    public static NorthSouth getNorthSouth(char c) {
      if (c == 'N' || c == 'n')
        return NORTH;
      if (c == 'S' || c == 's')
        return SOUTH;
      throw new IllegalArgumentException("char '" + c + "' only accepts 'N' , 'n' , 'S' , or 's'. ");
    }
  }


  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Location))
      return false;
    Location location = (Location) o;
    return lngDeg == location.lngDeg && lngMin == location.lngMin && Double.compare(location.lngSec, lngSec) == 0 && latDeg == location.latDeg && latMin == location.latMin && Double.compare(location.latSec, latSec) == 0 && Double.compare(location.altitudeMeter, altitudeMeter) == 0 && eastWest == location.eastWest && northSouth == location.northSouth && java.util.Objects.equals(tzid, location.tzid) && java.util.Objects.equals(minuteOffset, location.minuteOffset);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(eastWest, lngDeg, lngMin, lngSec, northSouth, latDeg, latMin, latSec, tzid, minuteOffset, altitudeMeter);
  }

  /** 查詢， minuteOffset 是否被設定 (非null) */
  public boolean hasMinuteOffset() {
    return minuteOffset!=null;
  }

  public Optional<Integer> getMinuteOffsetOptional() {
    return Optional.ofNullable(minuteOffset);
  }


  /**
   * 與 GMT 的時差 (分鐘)
   * {@link #minuteOffset} 的優先權高於 {@link #tzid}
   */
  public int getMinuteOffset() {
    if (minuteOffset != null)
      return minuteOffset;
    else
      return TimeZone.getTimeZone(tzid).getRawOffset() / (60 * 1000);
  }

  public ZoneOffset getZoneOffset() {
    int totalSeconds = getMinuteOffset()* 60;
    return ZoneOffset.ofTotalSeconds(totalSeconds);
  }
}
