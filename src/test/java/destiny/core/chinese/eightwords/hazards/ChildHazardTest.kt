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
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ChildHazardTest {

  private val logger = KotlinLogging.logger { }

  private fun hazards(): Stream<Triple<ChildHazard, Book, Locale>> {
    val locales = listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE)

    return ChildHazard::class.nestedClasses
      .filter { it.isFinal && it.isSubclassOf(ChildHazard::class) }
      .mapNotNull { it.objectInstance }
      .map { it as ChildHazard }
      .flatMap { childHazard ->
        Book.values().flatMap { book ->
          locales.map { locale ->
            Triple(childHazard, book, locale)
          }
        }
      }.stream()
  }

  @ParameterizedTest
  @MethodSource("hazards")
  fun testBookNote(row: Triple<ChildHazard, Book, Locale>) {
    val (childHazard, book, locale) = row
    val note = childHazard.getBookNote(locale, book)
    logger.info { "${childHazard.getName(locale)} :《${book.getTitle(locale)}》 $locale = $note" }

    if (note != null) {
      assertNotEquals(childHazard.getName(locale), note)
    }
  }

  /**
   * [ChildHazard.getNotes] delegated to [ChildHazardDescriptorTest.testGetDescription]
   */
  @Test
  fun getNotesNull() {
    val locales = listOf(Locale.TRADITIONAL_CHINESE, Locale.SIMPLIFIED_CHINESE)

    ChildHazard::class.nestedClasses
      .filter { it.isFinal && it.isSubclassOf(ChildHazard::class) }
      .mapNotNull { it.objectInstance }
      .map { it as ChildHazard }
      .forEach { childHazard ->
        locales.forEach { locale ->
          val notes = childHazard.getNotes(locale)
          assertNull(notes)
//          val lines = notes!!.split("\n")
//          assertTrue { lines.isNotEmpty() }
//          logger.info { "${childHazard.getName(locale)} $locale : ${lines.size} " }
//          lines.forEach { line ->
//            assertTrue { line.isNotBlank() }
//            assertTrue { line.startsWith("《") }
//            assertTrue { line.contains("》") }
//            logger.info { "\t$line" }
//          }

        }
      }

  }
}
