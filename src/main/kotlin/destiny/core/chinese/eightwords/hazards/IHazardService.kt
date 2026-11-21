/**
 * Created by smallufo on 2022-07-18.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.tools.Lang
import destiny.core.Gender
import destiny.core.calendar.eightwords.IEightWords
import destiny.tools.JSerializable

data class HazardItem(
  val hazard: ChildHazard,
  val title: String,
  /** bookName , bookNote */
  val bookNotes: List<Pair<String, String>>
) : JSerializable

interface IHazardService {

  fun getChildHazardNotes(eightWords: IEightWords, gender: Gender?, lang: Lang): List<HazardItem>

  val hazardFactories : List<IHazardFactory>

}
