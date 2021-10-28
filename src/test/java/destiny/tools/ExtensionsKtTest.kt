/**
 * Created by smallufo on 2018-11-13.
 */
package destiny.tools

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExtensionsKtTest {

  @Test
  fun firstNotNullResult_iterable() {
    listOf(null, 'A', 'B').also { list ->
      assertEquals('A', list.firstNotNullResult { it })
      assertEquals('a', list.firstNotNullResult { it?.toLowerCase() })
    }

    listOf<Char?>(null, null).also { list ->
      assertNull(list.firstNotNullResult { it })
      assertNull(list.firstNotNullResult { it?.toLowerCase() })
    }
  }

  @Test
  fun firstNotNullResult_sequence() {
    sequenceOf(null, 'A', 'B').also { seq ->
      assertEquals('A', seq.firstNotNullResult { it })
      assertEquals('a', seq.firstNotNullResult { it?.toLowerCase() })
    }

    sequenceOf<String?>(null, null).also { seq ->
      assertNull(seq.firstNotNullResult { it })
      assertNull(seq.map { it }.firstNotNullResult { it?.lowercase(Locale.getDefault()) })
    }
  }
}
