/**
 * @author smallufo
 * Created on 2011/3/11 at 上午4:09:32
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals


class Base58Test {

  @Test
  fun testBase58() {
    assertEquals("abc123ABC", Base58.numberToAlpha(1431117682956369L))

    assertEquals(1431117682956369L, Base58.alphaToNumber("abc123ABC"))
  }
}
