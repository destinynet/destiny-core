/**
 * Created by smallufo on 2026-10-26.
 */
package destiny.core.calendar.event

import destiny.core.astrology.LunarPhase
import destiny.core.calendar.GmtJulDay
import kotlinx.serialization.Serializable

/**
 * 一個種類 [kind] 的事件來源。
 *
 * 契約：回傳**錨點落在 [from, to)** 的事件。逐段呼叫（例如逐年）因此天然不重疊 ——
 * 跨年的逆行週期只屬於轉逆的那一年。
 */
interface ICalendarEventSource {
  val kind: CalendarEventKind

  /** [types] 只會包含本種類的系列 */
  fun events(types: Set<CalendarEventType>, from: GmtJulDay, to: GmtJulDay): List<ICalendarEvent>
}

/** 要算哪些系列 */
@Serializable
data class CalendarEventConfig(val types: Set<CalendarEventType> = CalendarEventType.defaults)

/**
 * 事件曆的計算入口：依 [CalendarEventConfig] 把系列分派給各種類的 [ICalendarEventSource]，
 * 合併後依錨點排序。錨點契約同 [ICalendarEventSource]。
 */
interface ICalendarEventFeature {
  fun events(config: CalendarEventConfig, from: GmtJulDay, to: GmtJulDay): List<ICalendarEvent>
}

/** 朔、上弦、望、下弦的時刻 */
interface ILunations {
  /** 錨點落在 [from, to) 的月相，依時間排序 */
  fun lunations(from: GmtJulDay, to: GmtJulDay, phases: Set<LunarPhase> = LunarPhase.entries.toSet()): List<LunationEvent>
}
