/**
 * Created by smallufo on 2022-07-18.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.Gender
import destiny.core.calendar.eightwords.IEightWords
import java.util.*

data class HazardItem(
  val hazard: ChildHazard,
  val title: String,
  /** bookName , bookNote */
  val bookNotes: List<Pair<String, String>>
)

interface IHazardService {

  fun getChildHazardNotes(eightWords: IEightWords, gender: Gender?, locale: Locale): List<HazardItem>

}
