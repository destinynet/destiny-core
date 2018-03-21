/**
 * Created by smallufo on 2018-03-21.
 */
package destiny.core.calendar

import destiny.tools.AlignTools
import destiny.tools.location.TimeZoneUtils
import java.time.ZoneId
import java.util.*

object LocationTools {

  /**
   * 2018-03 格式： (直接帶入 data class [Location] 之值)
   * [Location.lat],[Location.lng] ([Location.tzid]) ([Location.minuteOffset]m) ([Location.altitudeMeter])
   */
  fun getDebugString(loc: Location): String {
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
   * 解碼 2018-03 的 [Location] debugString
   * [Location.lat],[Location.lng] ([Location.tzid]) ([Location.minuteOffset]m) ([Location.altitudeMeter])
   */
  fun decodeDebugStringNew(string: String): Location? {
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

  sealed class LocationPadding {
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
          value.endsWith('m') && value.takeWhile { it.isDigit() }.toIntOrNull() != null ->
            LocationPadding.minOffset(value.takeWhile { it.isDigit() }.toInt())
          value.toDoubleOrNull() != null -> LocationPadding.altMeter(value.toDouble())
          else -> null
        }
      }
    }
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
  fun getDebugString2012(loc: ILocation): String {
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


  fun fromDebugString(s:String) : Location {
    return fromDebugString2012(s)
  }

  /**
   * decode 2012-03 格式
   */
  fun fromDebugString2012(s: String): Location {
    val eastWest: EastWest
    val ew = s[0]
    eastWest = when (ew) {
      '+' -> EastWest.EAST
      '-' -> EastWest.WEST
      else -> throw RuntimeException("EW not correct : $ew")
    }

    val lngDeg = Integer.valueOf(s.substring(1, 4).trim { it <= ' ' })
    val lngMin = Integer.valueOf(s.substring(4, 6).trim { it <= ' ' })
    val lngSec = java.lang.Double.valueOf(s.substring(6, 11).trim { it <= ' ' })

    val northSouth: NorthSouth
    val ns = s[11]
    northSouth = when (ns) {
      '+' -> NorthSouth.NORTH
      '-' -> NorthSouth.SOUTH
      else -> throw RuntimeException("ns not correct : $ns")
    }

    val latDeg = Integer.valueOf(s.substring(12, 14).trim { it <= ' ' })
    val latMin = Integer.valueOf(s.substring(14, 16).trim { it <= ' ' })
    val latSec = java.lang.Double.valueOf(s.substring(16, 21).trim { it <= ' ' })

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
        TimeZoneUtils.getTimeZone(Integer.parseInt(firstToken.substring(1))).id
      else
        TimeZoneUtils.getTimeZone(Integer.parseInt(firstToken)).id
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