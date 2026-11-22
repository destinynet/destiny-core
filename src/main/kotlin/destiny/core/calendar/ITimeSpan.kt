/**
 * Created by smallufo on 2026-11-23.
 */
package destiny.core.calendar

/**
 * 一段時間 `[fromTime, toTime)`：含起點、不含終點。
 * 一串依時間排列、彼此相接的區段（大運、流年、小限、法達、黃道釋放的各層 …）都是這個形狀，
 * 「某一刻落在哪一段」只有 [contains]／[indexAt] 這一份寫法 —— 交界那一刻屬於後一段。
 */
interface ITimeSpan {
  val fromTime: GmtJulDay
  val toTime: GmtJulDay
}

/** [t] 在不在這一段裡（半開：起點算、終點不算） */
operator fun ITimeSpan.contains(t: GmtJulDay): Boolean = t >= fromTime && t < toTime

/** [t] 落在第幾段；不在任何一段裡（早於第一段、晚於最後一段、或落在空隙）回 null */
fun <T : ITimeSpan> List<T>.indexAt(t: GmtJulDay): Int? = indexOfFirst { t in it }.takeIf { it >= 0 }

/** [t] 落在哪一段；不在任何一段裡回 null */
fun <T : ITimeSpan> List<T>.spanAt(t: GmtJulDay): T? = firstOrNull { t in it }

/**
 * [t] 落在第幾段；超出範圍時取最靠近的那一頭 —— 早於第一段回 0、其餘（晚於最後一段）回最後一段。
 * 給「一定要指出目前那一列」的表格用；清單不可為空。
 * 要分辨「不在範圍內」的呼叫端用 [indexAt]，不要用這支再猜。
 */
fun <T : ITimeSpan> List<T>.indexNear(t: GmtJulDay): Int =
  indexAt(t) ?: if (t < first().fromTime) 0 else lastIndex
