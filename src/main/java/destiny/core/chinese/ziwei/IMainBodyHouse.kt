/**
 * Created by smallufo on 2017-04-18.
 */
package destiny.core.chinese.ziwei

import destiny.core.Descriptive
import destiny.core.calendar.ILocation
import destiny.core.chinese.Branch
import destiny.tools.getTitle
import java.time.chrono.ChronoLocalDateTime
import java.util.*

/** 取命宮、身宮地支  */
interface IMainBodyHouse : Descriptive {

  val mainBodyHouse: MainBodyHouse

  /** 命宮、身宮 、以及「最後要給主星所使用的月數 (若為占星算法，此值為空) 」 */
  fun getMainBodyHouse(lmt: ChronoLocalDateTime<*>, loc: ILocation): Triple<Branch, Branch , Int?>

  override fun toString(locale: Locale): String {
    return mainBodyHouse.getTitle(locale)
  }

  override fun getDescription(locale: Locale): String {
    return toString(locale)
  }
}
