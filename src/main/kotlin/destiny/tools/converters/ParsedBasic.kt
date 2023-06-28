/**
 * Created by smallufo on 2018-01-16.
 */
package destiny.tools.converters

import destiny.core.IBirthData
import java.io.Serializable


data class ParsedBasic(
  val successful: Boolean,
  val birthData: IBirthData) : Serializable, IBirthData by birthData
