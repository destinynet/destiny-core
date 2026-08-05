package destiny.core.calendar

import destiny.core.Descriptive
import destiny.tools.Lang
import kotlin.time.Duration

/** 單一時間點 */
interface IEvent : Comparable<IEvent> , Descriptive {
  val begin: GmtJulDay

  override fun getTitle(lang: Lang): String {
    return javaClass.simpleName
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

