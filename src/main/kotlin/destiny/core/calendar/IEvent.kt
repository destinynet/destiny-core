package destiny.core.calendar

import java.io.Serializable
import java.util.*
import kotlin.time.Duration

/** 單一時間點 */
interface IEvent : Serializable , Comparable<IEvent>{
  val begin: GmtJulDay

  fun getTitle(locale: Locale): String {
    return javaClass.simpleName
  }

  fun getDescription(locale: Locale) : String {
    return  getTitle(locale)
  }

  override fun compareTo(other: IEvent): Int {
    return when {
      begin < other.begin -> -1
      begin > other.begin -> 1
      else                -> 0
    }
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

