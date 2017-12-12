/**
 * @author smallufo Created on 2011/3/11 at 上午4:08:05
 */
package destiny.tools

import java.io.Serializable

/**
 * http://java.dzone.com/articles/create-your-own-bitly-using
 */
class Base58 : Serializable {
  companion object {

    private val BASE58_CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()

    fun numberToAlpha(number: Long): String {
      var number = number
      val buffer = CharArray(20)
      var index = 0
      do {
        buffer[index++] = BASE58_CHARS[(number % BASE58_CHARS.size).toInt()]
        number /= BASE58_CHARS.size
      } while (number > 0)
      return String(buffer, 0, index)
    }

    fun alphaToNumber(text: String): Long {
      val chars = text.toCharArray()
      var result: Long = 0
      var multiplier: Long = 1
      for (c in chars) {
        val digit: Int = when (c) {
          in '1'..'9' -> c - '1'
          in 'A'..'H' -> c - 'A' + 9
          in 'J'..'N' -> c - 'J' + 17
          in 'P'..'Z' -> c - 'P' + 22
          in 'a'..'k' -> c - 'a' + 33
          in 'm'..'z' -> c - 'l' + 43
          else -> throw IllegalArgumentException("Illegal character found: '$c'")
        }

        result += digit * multiplier
        multiplier *= BASE58_CHARS.size
      }
      return result
    }
  }

}
