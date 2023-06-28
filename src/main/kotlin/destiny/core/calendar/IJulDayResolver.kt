/**
 * Created by smallufo on 2021-08-02.
 */
package destiny.core.calendar

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinInstant


/** Experimental KMM for [JulDayResolver] */
fun interface IJulDayResolver : (Instant) -> Pair<LocalDateTime, CalType> {

  companion object

}

fun IJulDayResolver.convert(jInstant: java.time.Instant): Pair<LocalDateTime, CalType> {
  return this.invoke(jInstant.toKotlinInstant())
}


