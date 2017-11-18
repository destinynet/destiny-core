/**
 * Created by smallufo on 2017-11-18.
 */
package destiny.astrology.eclipse;

import destiny.astrology.Azimuth;

public class LunarEclipseTotalObservation extends LunarEclipsePartialObservation {

  private final boolean totalBeginVisible;
  private final boolean totalEndVisible;

  public LunarEclipseTotalObservation(double gmtJulDay, double lng, double lat, double alt, Azimuth azimuth, AbstractLunarEclipse.LunarType lunarType, double magUmbra, double magPenumbra, boolean maxVisible, boolean penumbraBeginVisible, boolean penumbraEndVisible, boolean partialBeginVisible, boolean partialEndVisible, boolean totalBeginVisible, boolean totalEndVisible) {
    super(gmtJulDay, lng, lat, alt, azimuth, lunarType, magUmbra, magPenumbra, maxVisible, penumbraBeginVisible, penumbraEndVisible, partialBeginVisible, partialEndVisible);
    this.totalBeginVisible = totalBeginVisible;
    this.totalEndVisible = totalEndVisible;
  }

  public boolean isTotalBeginVisible() {
    return totalBeginVisible;
  }

  public boolean isTotalEndVisible() {
    return totalEndVisible;
  }
}
