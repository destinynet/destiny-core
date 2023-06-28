/**
 * Created by smallufo on 2022-07-14.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.EnumTest
import kotlin.test.Test

internal class BookTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Book::class , false)
  }
}
