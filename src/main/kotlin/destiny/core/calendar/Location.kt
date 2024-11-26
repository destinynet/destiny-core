/**
 * Created by smallufo on 2018-03-15.
 */
package destiny.core.calendar

import destiny.core.News
import destiny.core.News.EastWest.EAST
import destiny.core.News.EastWest.WEST
import destiny.core.News.NorthSouth.NORTH
import destiny.core.News.NorthSouth.SOUTH
import destiny.core.calendar.LatValue.Companion.toLat
import destiny.core.calendar.LngValue.Companion.toLng
import destiny.tools.LocaleTools
import java.io.Serializable
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue

@JvmInline
@kotlinx.serialization.Serializable
value class LatValue(val value: Double) : Serializable {
  init {
    require(value.absoluteValue <= 90)
  }

  val northSouth: News.NorthSouth
    get() = if (value >= 0) NORTH else SOUTH

  val deg: Int
    get() = value.absoluteValue.toInt()

  val min: Int
    get() = ((value.absoluteValue - deg) * 60).toInt()

  val sec: Double
    get() = value.absoluteValue * 3600 - deg * 3600 - min * 60

  companion object {
    fun Double.toLat(): LatValue {
      return LatValue(this)
    }
  }
}


@JvmInline
@kotlinx.serialization.Serializable
value class LngValue(val value: Double) : Serializable {
  init {
    require(value.absoluteValue <= 180)
  }

  val eastWest: News.EastWest
    get() = if (value >= 0) EAST else WEST

  val deg: Int
    get() = value.absoluteValue.toInt()

  val min: Int
    get() = ((value.absoluteValue - deg) * 60).toInt()

  val sec: Double
    get() = value.absoluteValue * 3600 - deg * 3600 - min * 60

  companion object {
    fun Double.toLng(): LngValue {
      return LngValue(this)
    }
  }
}

/** 純粹經緯度座標，沒有時區 [TimeZone] [ZoneId] 或是 時差 (minuteOffset) 等資訊 */
interface ILatLng : Serializable {

  val lat: LatValue
  val lng: LngValue

  val latLng
    get() = lat to lng

  val eastWest: News.EastWest
    get() = lng.eastWest


  val lngDeg: Int
    get() = lng.deg

  val lngMin: Int
    get() = lng.min

  val lngSec: Double
    get() = lng.sec

  val northSouth: News.NorthSouth
    get() = lat.northSouth


  val latDeg: Int
    get() = lat.deg

  val latMin: Int
    get() = lat.min

  val latSec: Double
    get() = lat.sec

  /** 取得經緯度的十進位表示法，先緯度、再經度 */
  val decimal: String
    get() = with(StringBuffer()) {
      append(lat)
      append(',')
      append(lng)
    }.toString()
}

data class LatLng(override val lat: LatValue, override val lng: LngValue) : ILatLng

interface ILatLngRadius : ILatLng {
  val radiusMeters: Int
}

data class LatLngRadius(val iLatLng: ILatLng, override val radiusMeters: Int) : ILatLngRadius, ILatLng by iLatLng

interface ILocation : ILatLng {

  val tzid: String?

  /** 強制覆蓋與 GMT 的時差 , 優先權高於 [tzid]  */
  val minuteOffset: Int?

  val hasMinuteOffset: Boolean
    get() = minuteOffset != null

  val finalMinuteOffset: Int
    get() {
      return minuteOffset ?: (ZoneId.of(tzid).rules.getStandardOffset(Instant.now()).totalSeconds / 60)
    }

  /** 高度（公尺） */
  val altitudeMeter: Double?

  val zoneId: ZoneId
    get() = tzid?.let {
      ZoneId.of(it)
    } ?: minuteOffset?.let {
      TimeTools.findZoneIdByMinutes(it)?.let { id -> ZoneId.of(id) }
    } ?: GMT

} // ILocation

interface IPlace {
  val place: String
}

@kotlinx.serialization.Serializable
data class Location(override val lat: LatValue,
                    override val lng: LngValue,
                    override val tzid: String? = null,
                    override val minuteOffset: Int? = null,
                    override val altitudeMeter: Double? = null) : ILocation {

  /**
   * 最詳盡的 constructor
   *
   * 2012/03 格式：
   * 012345678901234567890123456789012345678901234567890
   * +DDDMMSSSSS+DDMMSSSSS Alt~ TimeZone~ [minuteOffset]
   * */
  constructor(
    eastWest: News.EastWest,
    lngDeg: Int,
    lngMin: Int,
    lngSec: Double = 0.0,
    northSouth: News.NorthSouth,
    latDeg: Int,
    latMin: Int,
    latSec: Double = 0.0,
    tzid: String?,
    minuteOffset: Int? = null,
    altitudeMeter: Double? = null) : this(
    getLat(northSouth, latDeg, latMin, latSec).toLat(),
    getLng(eastWest, lngDeg, lngMin, lngSec).toLng(),
    tzid,
    minuteOffset,
    altitudeMeter
  )


  /** 大家比較常用的，只有「度、分」。省略「秒」以及「高度」 */
  constructor(eastWest: News.EastWest, lngDeg: Int, lngMin: Int,
              northSouth: News.NorthSouth, latDeg: Int, latMin: Int,
              tzid: String) :
    this(eastWest, lngDeg, lngMin, 0.0, northSouth, latDeg, latMin, 0.0, tzid)


  /** 比較省略的 constructor  , 去除東西經、南北緯 , 其值由 經度/緯度的正負去判斷 */
  constructor(lngDeg: Int, lngMin: Int, lngSec: Double,
              latDeg: Int, latMin: Int, latSec: Double,
              tzid: String) : this(
    (abs(latDeg).toDouble() + latMin.toDouble() / 60.0 + latSec / 3600.0).let { if (latDeg < 0) 0 - it else it }.toLat(),
    (abs(lngDeg).toDouble() + lngMin.toDouble() / 60.0 + lngSec / 3600.0).let { if (lngDeg < 0) 0 - it else it }.toLng(),
    tzid, null, null)


  /** 較省略的 constructor , 度數以 double 取代 */
  constructor(eastWest: News.EastWest, lng: Double,
              northSouth: News.NorthSouth, lat: Double,
              tzid: String, minuteOffset: Int? = null, altitudeMeter: Double? = null) : this(
    lat.let { if (northSouth == SOUTH) 0 - it else it }.toLat(),
    lng.let { if (eastWest == WEST) 0 - it else it }.toLng(),
    tzid, minuteOffset, altitudeMeter)


  companion object {

    fun of(lat: Double, lng: Double, tzid: String? = null, minuteOffset: Int? = null, altitudeMeter: Double? = null) : Location {
      return Location(lat.toLat(), lng.toLng(), tzid, minuteOffset, altitudeMeter)
    }

    fun getLng(ew: News.EastWest, lngDeg: Int, lngMin: Int, lngSec: Double): Double {
      return (lngDeg.toDouble() + lngMin.toDouble() / 60.0 + lngSec / 3600.0).let {
        if (ew == WEST)
          0 - it
        else
          it
      }
    }

    fun getLat(nw: News.NorthSouth, latDeg: Int, latMin: Int, latSec: Double): Double {
      return (latDeg.toDouble() + latMin.toDouble() / 60.0 + latSec / 3600.0).let {
        if (nw == SOUTH)
          0 - it
        else
          it
      }
    }
  }

  fun toString(locale: Locale): String {
    return LocationDecorator.getOutputString(this, locale)
  }

  override fun toString(): String {
    return "Location(${lat.value},${lng.value}, tzid=$tzid, minuteOffset=$minuteOffset)"
  }

} // Location


interface ILocationPlace : ILocation, IPlace

data class LocationPlace(val location: ILocation, override val place: String) : ILocationPlace, ILocation by location,
  Serializable {
  fun toString(locale: Locale): String {
    return LocationDecorator.getOutputString(this, locale)
  }

  override fun toString(): String {
    return toString(Locale.getDefault())
  }
}

fun locationOf(locale: Locale): Location {
  val matchedLocale = LocaleTools.getBestMatchingLocale(locale, locMap.keys) ?: Locale.getDefault()
  val loc = locMap.getValue(matchedLocale)
  return Location(loc.lat, loc.lng, loc.tzid, loc.minuteOffset, loc.altitudeMeter)
}

fun locationOf(tzid : String) : Location? {
  return locMap.entries.firstOrNull { (_, loc) -> loc.tzid == tzid }?.value
}


val locMap = mapOf(
  // de , 柏林
  Locale.GERMAN to Location(EAST, 13, 24, NORTH, 52, 31, "Europe/Berlin"),
  // de_DE , 柏林
  Locale.GERMANY to Location(EAST, 13, 24, NORTH, 52, 31, "Europe/Berlin"),
  // en , 紐約 , 40.758899, -73.985131 , 時報廣場
  Locale.ENGLISH to Location.of(40.758899, -73.985131, "America/New_York"),
  // en_US , 紐約
  Locale.US to Location.of(40.758899, -73.985131, "America/New_York"),
  // en_AU , 雪梨
  Locale("en", "AU") to Location(EAST, 151, 12, 40.0, SOUTH, 33, 51, 36.0, "Australia/Sydney"),
  // en_BW , 波札那 Botswana
  Locale("en", "BW") to Location(EAST, 25, 55, SOUTH, 24, 40, "Africa/Gaborone"),
  // en_CA , 多倫多
  Locale.CANADA to Location(WEST, 79, 24, NORTH, 43, 40, "America/Toronto"),
  // en_DK , 丹麥 哥本哈根 Copenhagen
  Locale("en", "DK") to Location(EAST, 12, 34, NORTH, 55, 43, "Europe/Copenhagen"),
  // en_GB , 倫敦
  Locale.UK to Location(WEST, 0, 7, NORTH, 51, 30, "Europe/London"),
  // en_HK , 香港
  Locale("en", "HK") to Location.of(22.2798721, 114.1735865, "Asia/Hong_Kong"),
  // en_IE , 愛爾蘭 Ireland , 都柏林 Dublin
  Locale("en", "IE") to Location(WEST, 6.2592, NORTH, 53.3472, "Europe/Dublin"),
  // en_MY , 馬來西亞 , 吉隆坡
  Locale("en", "MY") to Location(EAST, 101, 42, NORTH, 3, 8, "Asia/Kuala_Lumpur"),
  // en_NZ , 紐西蘭 , 奧克蘭 Auckland (最大城市)
  Locale("en", "NZ") to Location(EAST, 174, 45, SOUTH, 36, 52, "Pacific/Auckland"),
  // en_PH , 菲律賓 , 馬尼拉
  Locale("en", "PH") to Location(EAST, 121, 0, NORTH, 14, 35, "Asia/Manila"),
  // en_SG , 新加坡
  Locale("en", "SG") to Location(EAST, 103, 51, NORTH, 1, 17, "Asia/Singapore"),
  // en_ZA , 南非 , 約翰尼斯堡
  Locale("en", "ZA") to Location(EAST, 27, 54, SOUTH, 26, 8, "Africa/Johannesburg"),
  // en_ZW , 辛巴威 , 哈拉雷
  Locale("en", "ZW") to Location(EAST, 31, 3, SOUTH, 17, 50, "Africa/Harare"),
  // fr , 巴黎
  Locale.FRENCH to Location(EAST, 2, 20, NORTH, 48, 52, "Europe/Paris"),
  // fr_FR , 巴黎
  Locale.FRANCE to Location(EAST, 2, 20, NORTH, 48, 52, "Europe/Paris"),
  // it , 羅馬
  Locale.ITALIAN to Location(EAST, 12, 29, NORTH, 41, 54, "Europe/Rome"),
  // it_IT , 羅馬
  Locale.ITALY to Location(EAST, 12, 29, NORTH, 41, 54, "Europe/Rome"),
  // ja , 東京
  Locale.JAPANESE to Location(EAST, 139, 46, 0.0, NORTH, 35, 40, 50.0, "Asia/Tokyo"),
  // ja_JP , 東京
  Locale.JAPAN to Location(EAST, 139, 45, NORTH, 35, 40, "Asia/Tokyo"),
  // ko , 首爾
  Locale.KOREAN to Location(EAST, 127, 0, NORTH, 37, 32, "Asia/Seoul"),
  // ko_KR , 首爾
  Locale.KOREA to Location(EAST, 127, 0, NORTH, 37, 32, "Asia/Seoul"),
  // zh , 北京
  Locale.CHINESE to Location.of(39.9075, 116.397, "Asia/Harbin"),
  // zh_CN , PRC == CHINA == SIMPLIFIED_CHINESE , 北京
  Locale.CHINA to Location(EAST, 116, 23, NORTH, 39, 55, "Asia/Shanghai"),
  // zh_HK , 香港
  Locale("zh", "HK") to Location.of(22.2798721, 114.1735865, "Asia/Hong_Kong"),
  // zh_MO , 澳門
  Locale("zh", "MO") to Location(EAST, 113, 35, NORTH, 22, 14, "Asia/Macao"),
  // zh_SG , 新加坡
  Locale("zh", "SG") to Location.of(1.2867926,103.8544739, "Asia/Singapore"),
  // zh_TW , TAIWAN == TRADITIONAL_CHINESE , 台北市 景福門 (25.039059 , 121.517675) ==> 25°02'20.5"N 121°31'03.6"E
  Locale.TAIWAN to Location.of(25.039030, 121.517668, "Asia/Taipei")
)


