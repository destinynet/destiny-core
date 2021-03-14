/**
 * Created by smallufo on 2021-03-15.
 */
package destiny.core.chinese

import destiny.core.chinese.Animal.values
import destiny.core.chinese.Animal.貓
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class AnimalTest {


  @Test
  fun testToString() {
    values().forEach { animal ->
      assertTrue(animal.toString(Locale.TAIWAN).length == 1)
      assertTrue(animal.toString(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(animal.toString(Locale.ENGLISH))
    }

    assertEquals("貓" , 貓.toString(Locale.TAIWAN))
    assertEquals("猫" , 貓.toString(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Cat" , 貓.toString(Locale.ENGLISH))
  }
}
