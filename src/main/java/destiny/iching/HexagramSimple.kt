/**
 * @author smallufo
 * Created on 2011/4/18 at 下午12:23:56
 */
package destiny.iching

import java.io.Serializable
import java.util.*

class HexagramSimple : Serializable {
  companion object {
    private val map = mapOf(
      arrayOf(true, true, true, true, true, true) to 1,
      arrayOf(false, false, false, false, false, false) to 2,
      arrayOf(true, false, false, false, true, false) to 3,
      arrayOf(false, true, false, false, false, true) to 4,
      arrayOf(true, true, true, false, true, false) to 5,
      arrayOf(false, true, false, true, true, true) to 6,
      arrayOf(false, true, false, false, false, false) to 7,
      arrayOf(false, false, false, false, true, false) to 8,
      arrayOf(true, true, true, false, true, true) to 9,
      arrayOf(true, true, false, true, true, true) to 10,
      arrayOf(true, true, true, false, false, false) to 11,
      arrayOf(false, false, false, true, true, true) to 12,
      arrayOf(true, false, true, true, true, true) to 13,
      arrayOf(true, true, true, true, false, true) to 14,
      arrayOf(false, false, true, false, false, false) to 15,
      arrayOf(false, false, false, true, false, false) to 16,
      arrayOf(true, false, false, true, true, false) to 17,
      arrayOf(false, true, true, false, false, true) to 18,
      arrayOf(true, true, false, false, false, false) to 19,
      arrayOf(false, false, false, false, true, true) to 20,
      arrayOf(true, false, false, true, false, true) to 21,
      arrayOf(true, false, true, false, false, true) to 22,
      arrayOf(false, false, false, false, false, true) to 23,
      arrayOf(true, false, false, false, false, false) to 24,
      arrayOf(true, false, false, true, true, true) to 25,
      arrayOf(true, true, true, false, false, true) to 26,
      arrayOf(true, false, false, false, false, true) to 27,
      arrayOf(false, true, true, true, true, false) to 28,
      arrayOf(false, true, false, false, true, false) to 29,
      arrayOf(true, false, true, true, false, true) to 30,
      arrayOf(false, false, true, true, true, false) to 31,
      arrayOf(false, true, true, true, false, false) to 32,
      arrayOf(false, false, true, true, true, true) to 33,
      arrayOf(true, true, true, true, false, false) to 34,
      arrayOf(false, false, false, true, false, true) to 35,
      arrayOf(true, false, true, false, false, false) to 36,
      arrayOf(true, false, true, false, true, true) to 37,
      arrayOf(true, true, false, true, false, true) to 38,
      arrayOf(false, false, true, false, true, false) to 39,
      arrayOf(false, true, false, true, false, false) to 40,
      arrayOf(true, true, false, false, false, true) to 41,
      arrayOf(true, false, false, false, true, true) to 42,
      arrayOf(true, true, true, true, true, false) to 43,
      arrayOf(false, true, true, true, true, true) to 44,
      arrayOf(false, false, false, true, true, false) to 45,
      arrayOf(false, true, true, false, false, false) to 46,
      arrayOf(false, true, false, true, true, false) to 47,
      arrayOf(false, true, true, false, true, false) to 48,
      arrayOf(true, false, true, true, true, false) to 49,
      arrayOf(false, true, true, true, false, true) to 50,
      arrayOf(true, false, false, true, false, false) to 51,
      arrayOf(false, false, true, false, false, true) to 52,
      arrayOf(false, false, true, false, true, true) to 53,
      arrayOf(true, true, false, true, false, false) to 54,
      arrayOf(true, false, true, true, false, false) to 55,
      arrayOf(false, false, true, true, false, true) to 56,
      arrayOf(false, true, true, false, true, true) to 57,
      arrayOf(true, true, false, true, true, false) to 58,
      arrayOf(false, true, false, false, true, true) to 59,
      arrayOf(true, true, false, false, true, false) to 60,
      arrayOf(true, true, false, false, true, true) to 61,
      arrayOf(false, false, true, true, false, false) to 62,
      arrayOf(true, false, true, false, true, false) to 63,
      arrayOf(false, true, false, true, false, true) to 64)


    fun getIndex(lines: Array<Boolean>): Int {
      for ((key, value) in map) {
        if (Arrays.equals(lines, key))
          return value
      }
      return 0
    }
  }
}
