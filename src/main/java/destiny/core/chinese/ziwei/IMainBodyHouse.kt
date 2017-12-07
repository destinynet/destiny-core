/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.calendar.Location
import destiny.core.chinese.Branch
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/** 取命宮、身宮地支  */
interface IMainBodyHouse : Descriptive {

  /** 前者為命宮、後者為身宮  */
  fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: Location): Pair<Branch, Branch>

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(IMainBodyHouse::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }
}
