/**
 * Created by smallufo on 2017-11-18.
 */
package destiny.astrology.eclipse

import destiny.astrology.Azimuth

/** 半影月食 */
open class LunarEclipsePenumbraObservation(gmtJulDay: Double, lng: Double, lat: Double, alt: Double, azimuth: Azimuth,
                                           lunarType: ILunarEclipse.LunarType,
                                           magUmbra: Double,
                                           magPenumbra: Double,
                                           maxVisible: Boolean,
                                           val isPenumbraBeginVisible: Boolean,
                                           val isPenumbraEndVisible: Boolean) :
  AbstractLunarEclipseObservation(gmtJulDay, lng, lat, alt, azimuth, lunarType, magUmbra, magPenumbra, maxVisible)
