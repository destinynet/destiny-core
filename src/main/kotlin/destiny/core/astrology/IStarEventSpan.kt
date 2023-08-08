/**
 * Created by smallufo on 2023-08-04.
 */
package destiny.core.astrology

import destiny.core.calendar.IEventSpan


interface IStarEventSpan : IEventSpan {
  val star: Star
  val fromPos: IZodiacDegree
  val toPos: IZodiacDegree
}

sealed interface IStarLocalEventSpan : IStarEventSpan {
  val description: String
}
