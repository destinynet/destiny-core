/**
 * Created by smallufo on 2017-12-05.
 */
package destiny.bugs

import kotlin.test.Test

class AlphabetTest {

  val m = AlphabetMap()

  @Test
  fun printValues() {
    Alphabet.array.forEach { print("$it ") };println()
    Alphabet.list.forEach { print("$it ") };println()
  }
}