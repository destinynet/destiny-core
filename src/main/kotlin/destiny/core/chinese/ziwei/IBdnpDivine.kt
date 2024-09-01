/**
 * Created by smallufo on 2024-09-01.
 */
package destiny.core.chinese.ziwei

import destiny.core.IBirthDataNamePlace

sealed interface IBdnpDivine {
  val divineBdnp: IBirthDataNamePlace
  val question: String
}
