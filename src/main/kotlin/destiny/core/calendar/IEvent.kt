package destiny.core.calendar

import java.io.Serializable
import java.time.ZoneId
import java.time.chrono.ChronoLocalDateTime
import kotlin.time.Duration

/** 單一時間點 */
interface IEvent : Serializable , Comparable<IEvent>{
  val begin: GmtJulDay
  val zoneId: ZoneId?

  override fun compareTo(other: IEvent): Int {
    return when {
      begin < other.begin -> -1
      begin > other.begin -> 1
      else                -> 0
    }
  }
}

fun IEvent.getFromLmt(julDayResolver: JulDayResolver): ChronoLocalDateTime<*>? {
  return zoneId?.let { id ->
    TimeTools.getLmtFromGmt(begin, id, julDayResolver)
  }
}

/** 時間範圍，有開始以及結束 */
interface IEventSpan : IEvent {
  val end: GmtJulDay

  val duration: Duration
    get() {
      return end.toInstant().minus(begin.toInstant())
    }
}

fun IEventSpan.getToLmt(julDayResolver: JulDayResolver): ChronoLocalDateTime<*>? {
  return zoneId?.let { id ->
    TimeTools.getLmtFromGmt(end, id, julDayResolver)
  }
}
