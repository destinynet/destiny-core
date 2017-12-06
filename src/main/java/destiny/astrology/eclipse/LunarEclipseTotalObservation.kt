/**
 * Created by smallufo on 2017-11-18.
 */
package destiny.astrology.eclipse

import destiny.astrology.Azimuth

/** 月全食 */
class LunarEclipseTotalObservation(gmtJulDay: Double, lng: Double, lat: Double, alt: Double, azimuth: Azimuth,
                                   lunarType: AbstractLunarEclipse.LunarType,
                                   magUmbra: Double,
                                   magPenumbra: Double,
                                   maxVisible: Boolean,
                                   penumbraBeginVisible: Boolean,
                                   penumbraEndVisible: Boolean,
                                   partialBeginVisible: Boolean,
                                   partialEndVisible: Boolean,
                                   val isTotalBeginVisible: Boolean,
                                   val isTotalEndVisible: Boolean) :
  LunarEclipsePartialObservation(gmtJulDay, lng, lat, alt, azimuth, lunarType,
    magUmbra, magPenumbra, maxVisible, penumbraBeginVisible, penumbraEndVisible, partialBeginVisible, partialEndVisible)
