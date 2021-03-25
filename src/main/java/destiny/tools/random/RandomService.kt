/**
 * @author smallufo
 * Created on 2011/4/10 at 下午11:21:13
 */
package destiny.tools.random

interface RandomService {

  /**
   * @param count 要傳回的整數數量
   * @param min   最小的整數值 (包含)
   * @param max   最大的整數值 (包含)
   */
  fun getIntegers(count: Int, min: Int, max: Int): IntArray

  fun getIntegerList(count: Int, min: Int, max: Int): List<Int> {
    return getIntegers(count, min, max).toList()
  }

  fun getLongs(count: Int, min: Long, max: Long): LongArray

  fun getLong(min: Long, max: Long): Long {
    return getLongs(1, min, max)[0]
  }

  fun <T : Enum<*>> randomEnum(clazz: Class<T>): T {
    val length = clazz.enumConstants.size
    val r = getIntegers(length, 0, length - 1)[0]
    return clazz.enumConstants[r]
  }

  fun getYinYangs(count: Int): BooleanArray
}
