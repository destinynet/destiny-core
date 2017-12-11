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

  /** 命宮、身宮 、以及「最後要給主星所使用的月數 (若為占星算法，此值為空) 」 */
  fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: Location): Triple<Branch, Branch , Int?>

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
