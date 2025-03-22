/**
 * Created by smallufo on 2025-01-27.
 */
package destiny.core.astrology

import kotlinx.serialization.Serializable

@Serializable
data class RulingHouse(val house: Int, val sign: ZodiacSign) : java.io.Serializable
