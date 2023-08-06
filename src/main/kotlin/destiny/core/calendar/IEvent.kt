package destiny.core.calendar

import java.io.Serializable
import java.time.ZoneId
import java.time.chrono.ChronoLocalDateTime
import kotlin.time.Duration

/** 單一時間點 */
interface IEvent : Serializable , Comparable<IEvent>{
  val begin: GmtJulDay

  override fun compareTo(other: IEvent): Int {
    return when {
      begin < other.begin -> -1
      begin > other.begin -> 1
      else                -> 0
    }
  }
}

fun IEvent.getBeginLmt(julDayResolver: JulDayResolver, zoneId : ZoneId): ChronoLocalDateTime<*>? {
  return TimeTools.getLmtFromGmt(begin, zoneId, julDayResolver)
}

/** 時間範圍，有開始以及結束 */
interface IEventSpan : IEvent {
  val end: GmtJulDay

  val duration: Duration
    get() {
      return end.toInstant().minus(begin.toInstant())
    }
}

fun IEventSpan.getEndLmt(julDayResolver: JulDayResolver, zoneId: ZoneId): ChronoLocalDateTime<*>? {
  return TimeTools.getLmtFromGmt(end, zoneId, julDayResolver)
}
