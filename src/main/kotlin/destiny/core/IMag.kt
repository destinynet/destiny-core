/**
 * Created by smallufo on 2020-07-30.
 */
package destiny.core

import destiny.core.calendar.Lat
import destiny.core.calendar.Lng
import java.time.LocalDate

/**
 * 地磁資訊
 */
data class Mag(val declination: Double,
               val dipAngle: Double,
               val northIntensity: Double,
               val eastIntensity: Double,
               val verticalIntensity: Double,
               val horizontalIntensity: Double
)

interface IMag {
  fun getMag(lat: Lat, lng: Lng, alt: Double = 0.0, date: LocalDate): Mag
}
