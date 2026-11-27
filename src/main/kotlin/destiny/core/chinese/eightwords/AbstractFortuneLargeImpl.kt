package destiny.core.chinese.eightwords

import destiny.core.IIntAgeNote
import destiny.core.IntAgeNote
import destiny.tools.JSerializable
import destiny.tools.requireAllEnumKeys

abstract class AbstractFortuneLargeImpl(private val ageNoteImplMap: Map<IntAgeNote, IIntAgeNote>) : IPersonFortuneLarge, JSerializable {

  init {
    ageNoteImplMap.requireAllEnumKeys("ageNoteImplMap")
  }

  fun getAgeNoteImpls(intAgeNotes: List<IntAgeNote>): List<IIntAgeNote> {
    return intAgeNotes.map { impl: IntAgeNote ->
      ageNoteImplMap.getValue(impl)
    }.toList()
  }
}
