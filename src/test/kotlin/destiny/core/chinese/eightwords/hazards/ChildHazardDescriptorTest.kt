/**
 * Created by smallufo on 2022-07-17.
 */
package destiny.core.chinese.eightwords.hazards

import mu.KotlinLogging
import java.util.*
import kotlin.reflect.full.isSubclassOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

internal class ChildHazardDescriptorTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testGetDescription() {

    val locales = listOf(Locale.TAIWAN , Locale.SIMPLIFIED_CHINESE)


    ChildHazard::class.nestedClasses
      .filter { it.isFinal && it.isSubclassOf(ChildHazard::class) }
      .mapNotNull { it.objectInstance }
      .map { it as ChildHazard }
      .forEach { childHazard ->
        val descriptor = ChildHazardDescriptor(childHazard)

        locales.forEach { locale ->

          val title = descriptor.getTitle(locale)
          val desc = descriptor.getDescription(locale)

          if (locale != Locale.TAIWAN) {
            assertNotEquals(childHazard::class.simpleName , title)
          } else {
            assertEquals(childHazard::class.simpleName, title)
          }


          val lines= desc.split("\n")
          assertTrue { lines.isNotEmpty() }

          logger.info { "$title : $locale" }

          lines.forEach { line ->
            assertTrue { line.isNotBlank() }
            assertTrue { line.startsWith("《") }
            assertTrue { line.contains("》") }
            logger.info { "\t$line" }
          }

        }
      }
  }
}
