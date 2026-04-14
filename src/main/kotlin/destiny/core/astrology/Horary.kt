/**
 * Created by smallufo on 2025-03-14.
 */
package destiny.core.astrology

import destiny.core.IBirthDataNamePlace
import destiny.tools.serializers.IBirthDataNamePlaceSerializer
import kotlinx.serialization.Serializable


@Serializable
data class Horary(
  val type: Type,
  @Serializable(with = IBirthDataNamePlaceSerializer::class)
  val bdnp: IBirthDataNamePlace,
  val question: String,
) {
  enum class Type {
    RANDOM,
    NOW
  }
}
