/**
 * Created by smallufo on 2023-03-31.
 */
package destiny.core.astrology

import destiny.core.astrology.classical.rules.IPlanetPatternFactory
import java.io.Serializable
import java.util.*

data class HoroscopeClassicalConfig(val locale: Locale,
                                    val horoConfig: HoroscopeConfig = HoroscopeConfig(),
                                    val factories: List<IPlanetPatternFactory>) : Serializable
