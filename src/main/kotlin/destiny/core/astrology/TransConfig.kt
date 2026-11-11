/**
 * Created by smallufo on 2021-08-15.
 */
package destiny.core.astrology

import destiny.core.calendar.eightwords.ITransConfig
import kotlinx.serialization.Serializable


@Serializable
data class TransConfig(override var discCenter: Boolean = false,
                       override var refraction: Boolean = true,
                       override var temperature: Double = 0.0,
                       override var pressure: Double = 1013.25,
                       override var starTypeOptions: StarTypeOptions = StarTypeOptions.MEAN): ITransConfig
