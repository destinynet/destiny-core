/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.calendar.GmtJulDay
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IYearMonth
import destiny.core.chinese.Branch
import destiny.core.chinese.IMonthMaster
import destiny.core.chinese.MonthMaster
import destiny.tools.getDescription
import destiny.tools.getTitle
import java.io.Serializable
import java.util.*

class MonthMasterCombinedImpl(val yearMonthImpl: IYearMonth) : IMonthMaster, Serializable {

  override fun getBranch(gmtJulDay: GmtJulDay, loc: ILocation): Branch {
    val monthBranch = yearMonthImpl.getMonth(gmtJulDay, loc).branch
    return monthBranch.combined
  }

  override fun toString(locale: Locale): String {
    return MonthMaster.Combined.getTitle(locale)
  }

  override fun getDescription(locale: Locale): String {
    return MonthMaster.Combined.getDescription(locale)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is MonthMasterCombinedImpl) return false

    if (yearMonthImpl != other.yearMonthImpl) return false

    return true
  }

  override fun hashCode(): Int {
    return yearMonthImpl.hashCode()
  }


}
