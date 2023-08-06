/**
 * Created by smallufo on 2023-08-04.
 */
package destiny.core.astrology

import destiny.core.calendar.IEventSpan
import java.time.ZoneId


interface IStarEventSpan : IEventSpan {
  val star: Star
  val fromPos: IZodiacDegree
  val toPos: IZodiacDegree
}

interface IStarLocalEventSpan : IStarEventSpan {
  override val zoneId: ZoneId
  val title: String
  val description: String
}
