/**
 * Created by smallufo on 2022-07-14.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.tools.getTitle
import mu.KotlinLogging
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream
import kotlin.reflect.full.isSubclassOf
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ChildHazardTest {

  private val logger = KotlinLogging.logger { }

  private fun hazards() : Stream<Triple<ChildHazard, Book?, Locale>> {
    val locales = listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE)

    return ChildHazard::class.nestedClasses
      .filter { it.isFinal && it.isSubclassOf(ChildHazard::class) }
      .mapNotNull { it.objectInstance }
      .map { it as ChildHazard }
      .flatMap { childHazard ->
        listOf(null, *Book.values()).flatMap { book ->
          locales.map { locale ->
            Triple(childHazard, book, locale)
          }
        }
      }.stream()
  }

  @ParameterizedTest
  @MethodSource("hazards")
  fun testHazardNotes(row : Triple<ChildHazard, Book?, Locale>) {
    val (childHazard, book, locale) = row
    val notes = childHazard.getNotes(locale, book)
    assertNotNull(notes)
    if (notes != childHazard.getName()) {
      logger.info { "${childHazard.getName(locale)} :《${book?.getTitle(locale)}》 $locale = $notes" }
    }
  }
}
