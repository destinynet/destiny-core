/**
 * Created by smallufo on 2017-11-18.
 */
package destiny.astrology.eclipse

import destiny.astrology.Azimuth

/**
 * 月全食
 * [ILunarEclipseTotalObservation]
 * */
class LunarEclipseTotalObservation(gmtJulDay: Double, lng: Double, lat: Double, alt: Double, azimuth: Azimuth,
                                   lunarType: ILunarEclipse.LunarType,
                                   magUmbra: Double,
                                   magPenumbra: Double,
                                   maxVisible: Boolean,
                                   penumbraBeginVisible: Boolean,
                                   penumbraEndVisible: Boolean,
                                   partialBeginVisible: Boolean,
                                   partialEndVisible: Boolean,
                                   val totalBeginVisible: Boolean,
                                   val totalEndVisible: Boolean) :
  LunarEclipsePartialObservation(gmtJulDay, lng, lat, alt, azimuth, lunarType,
    magUmbra, magPenumbra, maxVisible, penumbraBeginVisible, penumbraEndVisible, partialBeginVisible, partialEndVisible)
