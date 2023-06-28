/**
 * Created by smallufo on 2022-07-17.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.tools.AbstractPropertyBasedPatternDescriptor
import destiny.tools.getTitle
import java.util.*


class ChildHazardDescriptor(hazard: ChildHazard) : AbstractPropertyBasedPatternDescriptor(hazard, "NOT_APPLICABLE", emptyList()) {

  override val resource: String = ChildHazard::class.qualifiedName!!

  override fun getDescription(locale: Locale): String {

    val hazard = pattern as ChildHazard

    return Book.values().map { book ->
      book to hazard.getBookNote(locale, book)
    }.filter { (_, note) ->
      note != null
    }.joinToString("\n") { (book, note) ->
      "《${book.getTitle(locale)}》：${note}"
    }
  }
}
