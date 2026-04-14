/**
 * Created by smallufo on 2024-08-31.
 */
package destiny.core.chinese.ziwei

import destiny.core.IBirthDataNamePlace
import destiny.tools.serializers.IBirthDataNamePlaceSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ZiweiDivine(
  @Serializable(with = IBirthDataNamePlaceSerializer::class)
  override val divineBdnp: IBirthDataNamePlace,
  override val question: String
) : IBdnpDivine
