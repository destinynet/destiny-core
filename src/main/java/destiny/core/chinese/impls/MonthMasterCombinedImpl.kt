/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IYearMonth
import destiny.core.chinese.Branch
import destiny.core.chinese.IMonthMaster
import destiny.core.chinese.MonthMaster
import destiny.core.chinese.asDescriptive
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime
import java.util.*

class MonthMasterCombinedImpl(val yearMonthImpl: IYearMonth) : IMonthMaster, Serializable {

  override fun toString(locale: Locale): String {
    return MonthMaster.Combined.asDescriptive().toString(locale)
  }

  override fun getDescription(locale: Locale): String {
    return MonthMaster.Combined.asDescriptive().getDescription(locale)
  }


  override fun getBranch(lmt: ChronoLocalDateTime<*>, location: ILocation): Branch {
    val monthBranch = yearMonthImpl.getMonth(lmt, location).branch
    return monthBranch.combined
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
