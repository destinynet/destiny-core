/**
 * Created by smallufo on 2018-03-21.
 */
package destiny.core.calendar

import destiny.tools.AlignTools
import java.time.ZoneId

object LocationTools {

  /**
   * 2018-03 格式： (直接帶入 data class [Location] 之值)
   * [Location.latitude],[Location.longitude] ([Location.tzid]) ([Location.minuteOffset]m) ([Location.altitudeMeter])
   */
  fun getDebugString(loc: Location): String {
    return StringBuilder().apply {
      loc.apply {
        append(latitude)
        append(',')
        append(longitude)
        tzid?.also { append(' ').append(it) }
        minuteOffset?.also { append(' ').append(it).append('m') } // 「分鐘」尾端加上 'm' , 保留未來可能出現 's' (秒數) 的可能性
        altitudeMeter?.takeIf { it != 0.0 }?.also { append(' ').append(it) }
      }
    }.toString()
  }

  /**
   * 解碼 2018-03 的 [Location] debugString
   * [Location.latitude],[Location.longitude] ([Location.tzid]) ([Location.minuteOffset]m) ([Location.altitudeMeter])
   */
  fun decodeDebugStringNew(string: String): Location? {
    val parts: Set<LocationPadding> =
      string.splitToSequence(" ").map { it -> LocationPadding.getPadding(it) }.filterNotNull().toSet()

    return (parts.first { it is LocationPadding.latLng } as LocationPadding.latLng).let {
      val tzid: String? = parts.firstOrNull { it is LocationPadding.tzid }?.let { it as LocationPadding.tzid}?.value?.id
      val minuteOffset: Int? = parts.firstOrNull { it is LocationPadding.minOffset }?.let {it as LocationPadding.minOffset}?.value
      val altMeter: Double? = parts.firstOrNull { it is LocationPadding.altMeter}?.let { it as LocationPadding.altMeter}?.value
      Location(it.lng, it.lat , tzid , minuteOffset , altMeter?:0.0)
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
  fun getOldDebugString(loc: Location): String {
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
}