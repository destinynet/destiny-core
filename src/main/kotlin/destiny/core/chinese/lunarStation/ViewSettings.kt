/**
 * Created by smallufo on 2021-04-30.
 */
package destiny.core.chinese.lunarStation

import destiny.core.calendar.eightwords.Direction
import destiny.tools.JSerializable

data class ViewSettings(val direction: Direction = Direction.R2L) : JSerializable
