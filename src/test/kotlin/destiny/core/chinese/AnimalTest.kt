/**
 * Created by smallufo on 2021-03-15.
 */
package destiny.core.chinese

import destiny.core.EnumTest
import destiny.core.chinese.Animal.*
import destiny.tools.getTitle
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class AnimalTest : EnumTest() {

  @Test
  fun testString() {
    testEnums(Animal::class, false)
  }

  @Test
  fun testToString() {
    entries.forEach { animal ->
      assertTrue(animal.getTitle(Locale.TAIWAN).length == 1)
      assertTrue(animal.getTitle(Locale.SIMPLIFIED_CHINESE).length == 1)
      assertNotNull(animal.getTitle(Locale.ENGLISH))
    }

    assertEquals("貓" , 貓.getTitle(Locale.TAIWAN))
    assertEquals("猫" , 貓.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Cat" , 貓.getTitle(Locale.ENGLISH))

    assertEquals("犺" , 犺.getTitle(Locale.TAIWAN))
    assertEquals("犺" , 犺.getTitle(Locale.SIMPLIFIED_CHINESE))
    assertEquals("Kang" , 犺.getTitle(Locale.ENGLISH))
  }
}
