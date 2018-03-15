/**
 * Created by smallufo on 2018-03-15.
 */
package destiny.core.calendar

import destiny.tools.AlignTools
import java.io.Serializable
import java.time.ZoneId
import java.util.*
import kotlin.math.abs

interface ILoc {
  val longitude: Double
  val latitude: Double
  val tzid: String

  /** 強制覆蓋與 GMT 的時差 , 優先權高於 [tzid]  */
  val minuteOffset: Int?
  //get() = field ?: TimeZone.getTimeZone(tzid).rawOffset / (60 * 1000)

  val hasMinuteOffset: Boolean
    get() = minuteOffset != null

  val finalMinuteOffset: Int
    get() = if (minuteOffset != null) minuteOffset!! else TimeZone.getTimeZone(tzid).rawOffset / (60 * 1000)

  /** 高度（公尺） */
  val altitudeMeter: Int?

  val eastWest: EastWest
    get() =
      if (longitude >= 0)
        EastWest.EAST
      else
        EastWest.WEST


  val lngDeg: Int
    get() = abs(longitude).toInt()

  val lngMin: Int
    get() = ((abs(longitude) - lngDeg) * 60).toInt()

  val lngSec: Double
    get() = abs(longitude) * 3600 - (lngDeg * 3600).toDouble() - (lngMin * 60).toDouble()

  val northSouth: NorthSouth
    get() =
      if (latitude >= 0)
        NorthSouth.NORTH
      else
        NorthSouth.SOUTH


  val latDeg: Int
    get() = abs(latitude).toInt()

  val latMin: Int
    get() = ((abs(latitude) - latDeg) * 60).toInt()

  val latSec: Double
    get() = abs(latitude) * 3600 - (latDeg * 3600).toDouble() - (latMin * 60).toDouble()

  /** 取得經緯度的十進位表示法，先緯度、再精度 */
  val decimal: String
    get() = with(StringBuffer()) {
      append(latitude)
      append(',')
      append(longitude)
    }.toString()

  val timeZone: TimeZone
    get() = TimeZone.getTimeZone(tzid)

  val zoneId: ZoneId
    get() = ZoneId.of(tzid)

  /**
   * 2012/03 格式：
   * 012345678901234567890123456789012345678901234567890
   * +DDDMMSSSSS+DDMMSSSSS Alt~ TimeZone~ [minuteOffset]
   * 範例 :
   * +1213012.34+25 312.34 12.3456 Asia/Taipei 480
   * 尾方的 minuteOffset 為 optional , 如果有的話，會 override Asia/Taipei 的 minuteOffset
   */
  val debugString: String
    get() = with(StringBuffer()) {
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
      if (minuteOffset != null)
        append(" ").append(minuteOffset.toString())
    }.toString()

  //  override fun toString() : String {
  //    LocationDecorator.getOutputString(this, Locale.getDefault());
  //  }
}

data class Loc(override val longitude: Double,
               override val latitude: Double,
               override val tzid: String,
               override val minuteOffset: Int?,
               override val altitudeMeter: Int? = 0) : ILoc, Serializable