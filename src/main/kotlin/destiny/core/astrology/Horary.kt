/**
 * Created by smallufo on 2025-03-14.
 */
package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import kotlinx.serialization.Serializable


@Serializable
data class Horary(
  val type: Type,
  val bdnp: IBirthDataNamePlace,
  val question: String,
) {
  enum class Type {
    RANDOM,
    NOW
  }
}
