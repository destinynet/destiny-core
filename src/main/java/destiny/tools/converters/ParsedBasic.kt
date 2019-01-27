/**
 * Created by smallufo on 2018-01-16.
 */
package destiny.tools.converters

import destiny.core.BirthData
import destiny.core.IBirthData
import java.io.Serializable


data class ParsedBasic(
  val successful: Boolean,
  private val birthData: IBirthData) : Serializable, IBirthData by birthData
