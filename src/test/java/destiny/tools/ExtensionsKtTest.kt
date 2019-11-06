/**
 * Created by smallufo on 2018-11-13.
 */
package destiny.tools

import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionsKtTest {

  @Test
  fun firstNotNullResult() {
    val list = listOf(null, 'A', 'B')
    assertEquals('A', list.firstNotNullResult { it })
    assertEquals('a', list.firstNotNullResult { it?.toLowerCase() })
  }
}
