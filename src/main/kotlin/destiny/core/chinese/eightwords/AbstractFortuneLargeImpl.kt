package destiny.core.chinese.eightwords

import destiny.core.IIntAgeNote
import destiny.core.IntAgeNote
import java.io.Serializable

abstract class AbstractFortuneLargeImpl(private val ageNoteImplMap: Map<IntAgeNote, IIntAgeNote>) : IPersonFortuneLarge, Serializable {

  fun getAgeNoteImpls(intAgeNotes: List<IntAgeNote>): List<IIntAgeNote> {
    return intAgeNotes.map { impl: IntAgeNote ->
      ageNoteImplMap[impl]!!
    }.toList()
  }
}
