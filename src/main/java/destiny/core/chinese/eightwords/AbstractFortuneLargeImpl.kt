package destiny.core.chinese.eightwords

import destiny.core.IntAgeNote
import destiny.core.IntAgeNoteImpl
import java.io.Serializable

abstract class AbstractFortuneLargeImpl(private val ageNoteImplMap: Map<IntAgeNoteImpl, IntAgeNote>) : IPersonFortuneLarge, Serializable {

  fun getAgeNoteImpls(intAgeNotes: List<IntAgeNoteImpl>): List<IntAgeNote> {
    return intAgeNotes.map { impl: IntAgeNoteImpl ->
      ageNoteImplMap[impl]!!
    }.toList()
  }
}
