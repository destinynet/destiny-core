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


/**
 * 有「定義性時刻」的事件：食甚、停滯轉逆、日月合沖 ……
 * 它與 [begin] 不同（食從初虧開始、逆行週期從進入陰影開始），
 * 事件的識別、排序、歸日都以 [anchor] 為準。
 */
interface IAnchoredEvent : IEvent {
  val anchor: GmtJulDay
}


/** 時間範圍，有開始以及結束 */
interface IEventSpan : IEvent {
  val end: GmtJulDay

  val duration: Duration
    get() {
      return end.toInstant().minus(begin.toInstant())
    }
}

