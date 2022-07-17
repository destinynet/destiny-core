/**
 * Created by smallufo on 2022-07-18.
 */
package destiny.core.chinese.eightwords.hazards

import destiny.core.Gender
import destiny.core.calendar.eightwords.IEightWords
import java.util.*

interface IHazardService {

  fun getChildHazardNotes(eightWords: IEightWords, gender: Gender?, locale: Locale): List<HazardItem>

}
