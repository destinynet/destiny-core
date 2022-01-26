/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.Descriptive
import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IYearMonth
import destiny.core.chinese.Branch
import destiny.core.chinese.IMonthMaster
import destiny.core.chinese.MonthMaster
import destiny.tools.asDescriptive
import java.io.Serializable

class MonthMasterCombinedImpl(private val yearMonthImpl: IYearMonth) : IMonthMaster,
                                                                       Descriptive by MonthMaster.Combined.asDescriptive(),
                                                                       Serializable {

  override fun getBranch(gmtJulDay: GmtJulDay, loc: ILocation): Branch {
    val monthBranch = yearMonthImpl.getMonth(gmtJulDay, loc).branch
    return monthBranch.combined
  }
}
