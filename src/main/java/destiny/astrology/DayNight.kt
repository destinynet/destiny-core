/**
 * @author smallufo
 * Created on 2007/12/11 at 下午 11:29:14
 */
package destiny.astrology

import destiny.core.chinese.YinYangIF

enum class DayNight(private val value: Boolean) : YinYangIF {

  /** 日  */
  DAY(true),

  /** 夜  */
  NIGHT(false);

  override fun getBooleanValue(): Boolean {
    return value
  }
}
