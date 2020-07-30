/**
 * Created by smallufo on 2020-07-30.
 */
package destiny.core

/**
 * 地磁資訊
 */
data class Mag(val declination: Double,
               val dipAngle: Double,
               val eastIntensity: Double,
               val horizontalIntensity: Double)

interface IMag {
  fun getMag(lat: Double, lng: Double)
}