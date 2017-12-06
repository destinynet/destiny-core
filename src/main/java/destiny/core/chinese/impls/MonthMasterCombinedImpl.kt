/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.calendar.Location
import destiny.core.calendar.eightwords.YearMonthIF
import destiny.core.chinese.Branch
import destiny.core.chinese.MonthMasterIF
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

class MonthMasterCombinedImpl(private val yearMonthImpl: YearMonthIF) : MonthMasterIF, Serializable {

  override fun getTitle(locale: Locale): String {
    return "月支六合（過節）"
  }

  override fun getDescription(locale: Locale): String {
    return "純粹以八字月支六合取月將"
  }


  override fun getBranch(lmt: ChronoLocalDateTime<*>, location: Location): Branch {
    val monthBranch = yearMonthImpl.getMonth(lmt, location).branch
    return monthBranch.combined
  }
}
