/**
 * Created by smallufo on 2018-03-21.
 */
package destiny.core.calendar

import destiny.core.News.EastWest
import destiny.core.News.NorthSouth.NORTH
import destiny.core.News.NorthSouth.SOUTH
import destiny.tools.AlignTools
import destiny.tools.KotlinLogging
import java.time.ZoneId
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationTools {

  private val logger = KotlinLogging.logger {  }

  private fun String.localTrim() =
    this
      .replace("\\p{Cc}\\d+;".toRegex(), "")
      .replace("\\p{Cntrl}".toRegex(), "")
      .trim { it == ' ' }

  fun encode(loc: ILocation): String {
    return encode2018(loc)
  }

  /**
   * 2018-03 格式： (直接帶入 data class [Location] 之值)
   * lat,lng ([Location.tzid]) ([Location.minuteOffset]m) ([Location.altitudeMeter])
   */
  fun encode2018(loc: ILocation): String {
    return StringBuilder().apply {
      loc.apply {
        append(lat.value)
        append(',')
        append(lng.value)
        tzid?.also { append(' ').append(it) }
        minuteOffset?.also { append(' ').append(it).append('m') } // 「分鐘」尾端加上 'm' , 保留未來可能出現 's' (秒數) 的可能性
        altitudeMeter?.takeIf { it != 0.0 }?.also { append(' ').append(it) }
      }
    }.toString()
  }


  /**
   * 2012/03 格式：
   * 012345678901234567890123456789012345678901234567890
   * +DDDMMSSSSS+DDMMSSSSS (altitudeMeter)~ (tzid)~ (minuteOffset)
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

        append(if (northSouth == NORTH) '+' else '-')
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
    return s.localTrim().let {
      decode2018(it) ?: decode2012(it) ?: throw IllegalArgumentException("cannot decode $s")
    }
  }

  /**
   * 解碼 2018-03 的 [Location] debugString
   * lat,lng ([Location.tzid]) ([Location.minuteOffset]m) ([Location.altitudeMeter])
   */
  private fun decode2018(string: String): ILocation? {
    val parts: Set<LocationPadding> =
      string.splitToSequence(" ").map { LocationPadding.getPadding(it) }.filterNotNull().toSet()
    return try {
      (parts.firstOrNull { it is LocationPadding.LatLng } as LocationPadding.LatLng).let {
        val tzid: String? =
          parts.firstOrNull { padding -> padding is LocationPadding.Tzid }?.let { pad -> pad as LocationPadding.Tzid }
            ?.value?.id
        val minuteOffset: Int? =
          parts.firstOrNull { padding -> padding is LocationPadding.MinOffset }
            ?.let { pad -> pad as LocationPadding.MinOffset }?.value
        val altMeter: Double? =
          parts.firstOrNull { padding -> padding is LocationPadding.AltMeter }
            ?.let { pad -> pad as LocationPadding.AltMeter }?.value
        Location.of(it.lat, it.lng, tzid, minuteOffset, altMeter)
      }
    } catch (e: Exception) {
      null
    }
  }

  private sealed class LocationPadding {
    data class LatLng(val lat: Double, val lng: Double) : LocationPadding()
    data class Tzid(val value: ZoneId) : LocationPadding()
    data class MinOffset(val value: Int) : LocationPadding()
    data class AltMeter(val value: Double) : LocationPadding()

    companion object {
      fun getPadding(value: String): LocationPadding? {
        return when {
          value.contains(",") -> {
            val (lat, lng) = value.split(",").map { it.toDouble() }
            LatLng(lat, lng)
          }
          ZoneId.getAvailableZoneIds().contains(value) -> Tzid(ZoneId.of(value))

          value.endsWith('m') && value.substring(0, value.length - 1).toIntOrNull() != null ->
            MinOffset((value.substring(0, value.length - 1)).toInt())

          value.toDoubleOrNull() != null -> AltMeter(value.toDouble())
          else -> null
        }
      }
    }
  }

  /**
   * decode 2012-03 格式
   */
  private fun decode2012(s: String): ILocation? {
    val ew = s[0]

    return when (ew) {
      '+' -> EastWest.EAST
      '-' -> EastWest.WEST
      else -> {
        logger.error { "EW not correct : $ew" }
        null
      }
    }?.let { eastWest ->
      val ns = s[11]
      when (ns) {
        '+' -> NORTH
        '-' -> SOUTH
        else -> {
          logger.error { "ns not correct : $ns" }
          null
        }
      }?.let { northSouth ->
        val lngDeg = (s.substring(1, 4).trim { it <= ' ' }).toInt()
        val lngMin = (s.substring(4, 6).trim { it <= ' ' }).toInt()
        val lngSec = (s.substring(6, 11).trim { it <= ' ' }).toDouble()

        val latDeg = (s.substring(12, 14).trim { it <= ' ' }).toInt()
        val latMin = (s.substring(14, 16).trim { it <= ' ' }).toInt()
        val latSec = (s.substring(16, 21).trim { it <= ' ' }).toDouble()


        //包含了 高度以及時區
        val altitudeAndTimezone = s.substring(21)

        var st = StringTokenizer(altitudeAndTimezone, " ")
        val firstToken = st.nextToken()
        // 2012/3 之後 , restToken 可能還會 append minuteOffset
        val restTokens = altitudeAndTimezone.substring(altitudeAndTimezone.indexOf(firstToken) + firstToken.length + 1)
          .trim { it <= ' ' }


        var altitudeMeter: Double?
        var tzid: String
        var minuteOffset: Int? = null
        //檢查 restTokens 是否能轉為 double，如果能的話，代表是舊款 , 否則就是新款
        try {
          altitudeMeter = restTokens.toDouble()
          //parse 成功，代表舊款
          tzid = if (firstToken[0] == '+')
            TimeTools.findZoneIdByMinutes(firstToken.substring(1).toInt())!!
          else{
            TimeTools.findZoneIdByMinutes(firstToken.toInt())!!
          }
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

        Location(eastWest, lngDeg, lngMin, lngSec, northSouth, latDeg, latMin, latSec, tzid, minuteOffset, altitudeMeter)
      }
    }


  } // fromDebugString

  /** in meters */
  fun calculateDistance(lat1: Lat, lng1: Lng, lat2: Lat, lng2: Lng): Int {
    // Earth radius in kilometers
    val earthRadius = 6371

    val dLat = Math.toRadians(lat2.value - lat1.value)
    val dLng = Math.toRadians(lng2.value - lng1.value)

    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(Math.toRadians(lat1.value)) * cos(Math.toRadians(lat2.value)) *
      sin(dLng / 2) * sin(dLng / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (earthRadius * c * 1000).toInt()
  }


  fun calculateAzimuth(loc1 : ILatLng, loc2 : ILatLng): Double {
    val (lat1, lng1) = loc1.lat to loc1.lng
    val (lat2, lng2) = loc2.lat to loc2.lng

    return calculateAzimuth(lat1.value, lng1.value, lat2.value, lng2.value)
  }

  fun calculateAzimuth(lat1: Double, lng1 : Double , lat2: Double , lng2: Double) : Double {
    val lat1Radian = Math.toRadians(lat1)
    val lat2Radian = Math.toRadians(lat2)
    val deltaLng = Math.toRadians(lng2 - lng1)

    val y = sin(deltaLng) * cos(lat2Radian)
    val x = cos(lat1Radian) * sin(lat2Radian) - sin(lat1Radian) * cos(lat2Radian) * cos(deltaLng)

    var azimuth = atan2(y, x)
    azimuth = Math.toDegrees(azimuth)

    // Normalize azimuth to be in the range [0, 360)
    if (azimuth < 0) {
      azimuth += 360
    }

    return azimuth
  }

}
