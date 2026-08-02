/**
 * Created by smallufo on 2018-01-16.
 */
package destiny.tools.converters

import destiny.core.IBirthData
import destiny.tools.JSerializable


data class ParsedBasic(
  val successful: Boolean,
  val birthData: IBirthData) : JSerializable, IBirthData by birthData
