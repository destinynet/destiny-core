/**
 * Created by smallufo on 2017-11-18.
 */
package destiny.astrology.eclipse;

import destiny.astrology.Azimuth;

public class LunarEclipsePenumbraObservation extends AbstractLunarEclipseObservation {

  private final boolean penumbraBeginVisible;

  private final boolean penumbraEndVisible;

  public LunarEclipsePenumbraObservation(double gmtJulDay, double lng, double lat, double alt, Azimuth azimuth, AbstractLunarEclipse.LunarType lunarType, double magUmbra, double magPenumbra, boolean maxVisible, boolean penumbraBeginVisible, boolean penumbraEndVisible) {
    super(gmtJulDay, lng, lat, alt, azimuth, lunarType, magUmbra, magPenumbra, maxVisible);
    this.penumbraBeginVisible = penumbraBeginVisible;
    this.penumbraEndVisible = penumbraEndVisible;
  }

  public boolean isPenumbraBeginVisible() {
    return penumbraBeginVisible;
  }

  public boolean isPenumbraEndVisible() {
    return penumbraEndVisible;
  }
}
