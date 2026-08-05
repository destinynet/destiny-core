package destiny.core.calendar

import destiny.tools.JSerializable
import destiny.tools.Lang
import destiny.tools.toLang
import java.util.*
import kotlin.time.Duration

/** 單一時間點 */
interface IEvent : JSerializable , Comparable<IEvent>{
  val begin: GmtJulDay

  fun getTitle(lang: Lang): String {
    return javaClass.simpleName
  }

  fun getDescription(lang: Lang) : String {
    return getTitle(lang)
  }

  /** 橋接，說明見 [destiny.tools.ILocaleString]。**實作端不要覆寫。** */
  fun getTitle(locale: Locale): String = getTitle(locale.toLang())

  /** 橋接，說明見 [destiny.tools.ILocaleString]。**實作端不要覆寫。** */
  fun getDescription(locale: Locale): String = getDescription(locale.toLang())

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

