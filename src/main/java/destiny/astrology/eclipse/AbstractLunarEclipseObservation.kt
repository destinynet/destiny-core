/**
 * Created by smallufo on 2017-11-15.
 */
package destiny.astrology.eclipse

import destiny.astrology.Azimuth

abstract class AbstractLunarEclipseObservation(
  gmtJulDay: Double, lng: Double, lat: Double, alt: Double, azimuth: Azimuth,
  val type: AbstractLunarEclipse.LunarType,

  /** 本影強度 , magnitude of Umbra  */
  val magUmbra: Double,

  /** 半影強度 , magnitude of Penumbra  */
  val magPenumbra: Double,

  /** 食甚可見否?  */
  val isMaxVisible: Boolean) : AbstractEclipseObservation(gmtJulDay, lng, lat, alt, azimuth) {

  fun getLunarType(): AbstractLunarEclipse.LunarType {
    return type
  }

  override fun toString(): String {
    return "[LunarEclipseObservation  type = $type , magUmbra=$magUmbra, magPenumbra=$magPenumbra, azimuth=$azimuth]"
  }
}
