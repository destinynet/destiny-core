/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.tools.ArrayTools
import org.slf4j.LoggerFactory
import java.io.Serializable
import java.util.*

abstract class HouseSeqAbstractImpl : IHouseSeq, Serializable {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun next(from: House, n: Int): House {
    return get(getIndex(from) + n)
  }

  override fun getAheadOf(h1: House, h2: House): Int {
    val index1 = getIndex(h1)
    val index2 = getIndex(h2)
    logger.trace("index1({}) = {} , index2({}) = {}", h1, index1, h2, index2)
    if (index1 < 0 || index2 < 0)
      return -1
    val steps = index1 - index2
    return if (steps >= 0) steps else steps + 12
  }

  override fun getTitle(locale: Locale): String {
    return try {
      ResourceBundle.getBundle(HouseSeqAbstractImpl::class.java.name, locale).getString(javaClass.simpleName)
    } catch (e: MissingResourceException) {
      javaClass.simpleName
    }

  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }

  private operator fun get(index: Int): House {
    return ArrayTools[houses, index]
  }

  private fun getIndex(h: House): Int {
    return listOf(*houses).indexOf(h)
  }
}
