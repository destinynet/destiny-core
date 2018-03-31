/**
 * Created by smallufo on 2018-03-21.
 */
package destiny.core.calendar

import destiny.tools.AlignTools
import destiny.tools.location.TimeZoneUtils
import java.time.ZoneId
import java.util.*

object LocationTools {


  fun encode(loc: ILocation): String {
    return encode2018(loc)
  }

  /**
   * 2018-03 格式： (直接帶入 data class [Location] 之值)
   * [Location.lat],[Location.lng] ([Location.tzid]) ([Location.minuteOffset]m) ([Location.altitudeMeter])
   */
  fun encode2018(loc: ILocation): String {
    return StringBuilder().apply {
      loc.apply {
        append(lat)
        append(',')
        append(lng)
        tzid?.also { append(' ').append(it) }
        minuteOffset?.also { append(' ').append(it).append('m') } // 「分鐘」尾端加上 'm' , 保留未來可能出現 's' (秒數) 的可能性
        altitudeMeter?.takeIf { it != 0.0 }?.also { append(' ').append(it) }
      }
    }.toString()
  }


  /**
   * 2012/03 格式：
   * 012345678901234567890123456789012345678901234567890
   * +DDDMMSSSSS+DDMMSSSSS [altitudeMeter]~ [tzid]~ [minuteOffset]
   * 範例 :
   * +1213012.34+25 312.34 12.3456 Asia/Taipei 480
   * 尾方的 minuteOffset 為 optional , 如果有的話，會 override Asia/Taipei 的 minuteOffset
   *
   */
  fun encode2012(loc: ILocation): String {
    return StringBuilder().apply {
      loc.apply {
        append(if (eastWest == EastWest.EAST) '+' else '-')
        append(AlignTools.leftPad(lngDeg.toString(), 3, ' '))
        append(AlignTools.leftPad(lngMin.toString(), 2, ' '))
        append(AlignTools.alignRight(lngSec, 5, ' '))

        append(if (northSouth == NorthSouth.NORTH) '+' else '-')
        append(AlignTools.leftPad(latDeg.toString(), 2, ' '))
        append(AlignTools.leftPad(latMin.toString(), 2, ' '))
        append(AlignTools.alignRight(latSec, 5, ' '))

        append(" ").append(altitudeMeter)
        append(' ').append(tzid)

        minuteOffset?.also {
          append(' ').append(it)
        }
      }
    }.toString()
  }


  fun decode(s: String): ILocation {
    return decode2018(s) ?: {
      decode2012(s)
    }.invoke()
  }

  /**
   * 解碼 2018-03 的 [Location] debugString
   * [Location.lat],[Location.lng] ([Location.tzid]) ([Location.minuteOffset]m) ([Location.altitudeMeter])
   */
  fun decode2018(string: String): ILocation? {
    val parts: Set<LocationPadding> =
      string.splitToSequence(" ").map { it -> LocationPadding.getPadding(it) }.filterNotNull().toSet()
    return (parts.first { it is LocationPadding.latLng } as LocationPadding.latLng).let {
      val tzid: String? =
        parts.firstOrNull { it is LocationPadding.tzid }?.let { it as LocationPadding.tzid }?.value?.id
      val minuteOffset: Int? =
        parts.firstOrNull { it is LocationPadding.minOffset }?.let { it as LocationPadding.minOffset }?.value
      val altMeter: Double? =
        parts.firstOrNull { it is LocationPadding.altMeter }?.let { it as LocationPadding.altMeter }?.value
      Location(it.lng, it.lat, tzid, minuteOffset, altMeter)
    }
  }

  private sealed class LocationPadding {
    data class latLng(val lat: Double, val lng: Double) : LocationPadding()
    data class tzid(val value: ZoneId) : LocationPadding()
    data class minOffset(val value: Int) : LocationPadding()
    data class altMeter(val value: Double) : LocationPadding()

    companion object {
      fun getPadding(value: String): LocationPadding? {
        return when {
          value.contains(",") -> {
            val (lat, lng) = value.split(",").map { it.toDouble() }
            LocationPadding.latLng(lat, lng)
          }
          ZoneId.getAvailableZoneIds().contains(value) -> LocationPadding.tzid(ZoneId.of(value))

          value.endsWith('m') && value.substring(0,value.length-1).toIntOrNull() != null ->
            LocationPadding.minOffset((value.substring(0,value.length-1)).toInt())

          value.toDoubleOrNull() != null -> LocationPadding.altMeter(value.toDouble())
          else -> null
        }
      }
    }
  }

  /**
   * decode 2012-03 格式
   */
  fun decode2012(s: String): ILocation {
    val eastWest: EastWest
    val ew = s[0]
    eastWest = when (ew) {
      '+' -> EastWest.EAST
      '-' -> EastWest.WEST
      else -> throw RuntimeException("EW not correct : $ew")
    }

    val lngDeg = (s.substring(1, 4).trim { it <= ' ' }).toInt()
    val lngMin = (s.substring(4, 6).trim { it <= ' ' }).toInt()
    val lngSec = (s.substring(6, 11).trim { it <= ' ' }).toDouble()

    val northSouth: NorthSouth
    val ns = s[11]
    northSouth = when (ns) {
      '+' -> NorthSouth.NORTH
      '-' -> NorthSouth.SOUTH
      else -> throw RuntimeException("ns not correct : $ns")
    }

    val latDeg = (s.substring(12, 14).trim { it <= ' ' }).toInt()
    val latMin = (s.substring(14, 16).trim { it <= ' ' }).toInt()
    val latSec = (s.substring(16, 21).trim { it <= ' ' }).toDouble()

    //包含了 高度以及時區
    val altitudeAndTimezone = s.substring(21)
    //System.out.println("altitudeAndTimezone = '" + altitudeAndTimezone+"'");

    var st = StringTokenizer(altitudeAndTimezone, " ")
    val firstToken = st.nextToken()
    // 2012/3 之後 , restToken 可能還會 append minuteOffset
    val restTokens = altitudeAndTimezone.substring(altitudeAndTimezone.indexOf(firstToken) + firstToken.length + 1)
      .trim { it <= ' ' }
    //System.out.println("firstToken = '" + firstToken + "' , rest = '" + restTokens+"'");

    var altitudeMeter: Double?
    var tzid: String
    var minuteOffset: Int? = null
    //檢查 restTokens 是否能轉為 double，如果能的話，代表是舊款 , 否則就是新款
    try {
      altitudeMeter = restTokens.toDouble()
      //parse 成功，代表舊款
      tzid = if (firstToken[0] == '+')
        TimeZoneUtils.getTimeZone(firstToken.substring(1).toInt()).id
      else
        TimeZoneUtils.getTimeZone(firstToken.toInt()).id
    } catch (e: NumberFormatException) {
      //新款
      //println("新款 , firstToken = $firstToken")
      altitudeMeter = if ("null" == firstToken) {
        null
      } else {
        firstToken.toDouble()
      }

      st = StringTokenizer(restTokens, " ")
      if (st.countTokens() == 1)
        tzid = restTokens
      else {
        // 2012/3 格式 : timeZone 之後，還附加 minuteOffset
        tzid = st.nextToken()
        minuteOffset = st.nextToken().toInt()
      }
    }

    return Location(eastWest, lngDeg, lngMin, lngSec, northSouth, latDeg, latMin, latSec, tzid, minuteOffset,
                    altitudeMeter)
  } // fromDebugString
}