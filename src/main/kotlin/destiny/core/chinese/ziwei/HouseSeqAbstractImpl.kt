/**
 * Created by smallufo on 2017-04-23.
 */
package destiny.core.chinese.ziwei

import destiny.tools.KotlinLogging
import destiny.tools.getTitle
import java.io.Serializable
import java.util.*

abstract class HouseSeqAbstractImpl : IHouseSeq, Serializable {
  // 建立 House 到索引的映射（延遲初始化，提升查找性能）
  private val houseToIndexMap: Map<House, Int> by lazy {
    houses.withIndex().associate { (index, house) -> house to index }
  }


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
    return if (steps >= 0) steps else steps + houses.size
  }

  override fun getTitle(locale: Locale): String {
    return houseSeq.getTitle(locale)
  }

  override fun getDescription(locale: Locale): String {
    return getTitle(locale)
  }

  private operator fun get(index: Int): House {
    return houses[index.mod(houses.size)]
  }

  private fun getIndex(h: House): Int {
    return houseToIndexMap[h] ?: -1
  }

  companion object {
    val logger = KotlinLogging.logger { }
  }
}
