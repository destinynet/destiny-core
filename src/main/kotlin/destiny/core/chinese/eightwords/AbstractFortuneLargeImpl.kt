package destiny.core.chinese.eightwords

import destiny.core.IIntAgeNote
import destiny.core.IntAgeNote
import destiny.tools.JSerializable

abstract class AbstractFortuneLargeImpl(private val ageNoteImplMap: Map<IntAgeNote, IIntAgeNote>) : IPersonFortuneLarge, JSerializable {

  fun getAgeNoteImpls(intAgeNotes: List<IntAgeNote>): List<IIntAgeNote> {
    return intAgeNotes.map { impl: IntAgeNote ->
      ageNoteImplMap[impl]!!
    }.toList()
  }
}
