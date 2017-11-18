/**
 * Created by smallufo on 2017-11-18.
 */
package destiny.astrology.eclipse;

import destiny.astrology.Azimuth;

public class LunarEclipsePartialObservation extends LunarEclipsePenumbraObservation {

  private final boolean partialBeginVisible;

  private final boolean partialEndVisible;

  public LunarEclipsePartialObservation(double gmtJulDay, double lng, double lat, double alt, Azimuth azimuth, AbstractLunarEclipse.LunarType lunarType, double magUmbra, double magPenumbra, boolean maxVisible, boolean penumbraBeginVisible, boolean penumbraEndVisible, boolean partialBeginVisible, boolean partialEndVisible) {
    super(gmtJulDay, lng, lat, alt, azimuth, lunarType, magUmbra, magPenumbra, maxVisible, penumbraBeginVisible, penumbraEndVisible);
    this.partialBeginVisible = partialBeginVisible;
    this.partialEndVisible = partialEndVisible;
  }

  public boolean isPartialBeginVisible() {
    return partialBeginVisible;
  }

  public boolean isPartialEndVisible() {
    return partialEndVisible;
  }
}
