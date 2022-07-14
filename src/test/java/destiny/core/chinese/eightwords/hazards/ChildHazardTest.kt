/**
 * Created by smallufo on 2022-07-14.
 */
package destiny.core.chinese.eightwords.hazards

import mu.KotlinLogging
import java.util.*
import kotlin.reflect.full.isSubclassOf
import kotlin.test.Test
import kotlin.test.assertNotNull

internal class ChildHazardTest {

  private val logger = KotlinLogging.logger { }

  @Test
  fun testNotesOfBook() {

    val locales = listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE)

    ChildHazard::class.nestedClasses
      .filter { it.isFinal && it.isSubclassOf(ChildHazard::class) }
      .mapNotNull { it.objectInstance }
      .forEach {
        val childHazard = it as ChildHazard

        logger.info { childHazard.getName() }

        listOf(null, *Book.values()).forEach { book ->
          logger.info { "\t $book" }

          locales.forEach { locale ->

            val notes = childHazard.getNotes(locale, book)
            assertNotNull(notes)
            logger.info { "\t\t $locale : $notes" }
          }
        }
      }
  }
}
