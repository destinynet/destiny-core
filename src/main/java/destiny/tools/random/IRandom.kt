/**
 * @author smallufo
 * Created on 2008/5/2 at 上午 1:22:15
 */
package destiny.tools.random

import java.io.IOException

interface IRandom {

  /**
   * @param count 要傳回的整數數量
   * @param min   最小的整數值 (包含)
   * @param max   最大的整數值 (包含)
   * @return count 數量個整數
   * @throws IOException
   */
  fun getIntegers(count: Int, min: Int, max: Int): IntArray

  fun getIntegerList(count: Int, min: Int, max: Int): List<Int> {
    return getIntegers(count, min, max).toList()
  }

  fun <T : Enum<*>> randomEnum(clazz: Class<T>): T {
    val length = clazz.enumConstants.size
    val r = getIntegers(length, 0, length - 1)[0]
    return clazz.enumConstants[r]
  }

  /**
   * 取得 count 個陰陽
   *
   * @throws IOException
   */
  fun getYinYangs(count: Int): BooleanArray
}
