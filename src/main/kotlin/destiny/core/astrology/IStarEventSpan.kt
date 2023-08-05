/**
 * Created by smallufo on 2023-08-04.
 */
package destiny.core.astrology

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.toInstant
import java.io.Serializable
import java.time.ZoneId
import java.time.chrono.ChronoLocalDateTime
import kotlin.time.Duration


interface IStarEventSpan : Serializable {
  val star: Star
  val fromGmt: GmtJulDay
  val toGmt: GmtJulDay
  val fromPos: IZodiacDegree
  val toPos: IZodiacDegree

  val duration: Duration
    get() {
      return toGmt.toInstant().minus(fromGmt.toInstant())
    }
}

interface IStarLocalEventSpan : IStarEventSpan {
  val fromLmt : ChronoLocalDateTime<*>
  val toLmt : ChronoLocalDateTime<*>
  val zoneId: ZoneId
  val title: String
  val description: String
}
