/**
 * Created by smallufo on 2022-07-17.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.tools.AbstractPropertyBasedPatternDescriptor
import destiny.tools.getTitle
import destiny.tools.Lang


class ChildHazardDescriptor(hazard: ChildHazard) : AbstractPropertyBasedPatternDescriptor(hazard, "NOT_APPLICABLE", emptyList()) {

  override val resource: String = ChildHazard::class.qualifiedName!!

  override fun getDescription(lang: Lang): String {

    val hazard = pattern as ChildHazard

    return Book.entries.map { book ->
      book to hazard.getBookNote(lang, book)
    }.filter { (_, note) ->
      note != null
    }.joinToString("\n") { (book, note) ->
      "《${book.getTitle(lang)}》：${note}"
    }
  }
}
