/**
 * Created by smallufo on 2017-11-15.
 */
package destiny.astrology.eclipse;

import destiny.astrology.Azimuth;

public abstract class AbstractLunarEclipseObservation extends AbstractEclipseObservation {

  private final AbstractLunarEclipse.LunarType lunarType;

  /** 本影強度 , magnitude of Umbra */
  private final double magUmbra;

  /** 半影強度 , magnitude of Penumbra */
  private final double magPenumbra;

  /** 食甚可見否? */
  private final boolean maxVisible;

  public AbstractLunarEclipseObservation(double gmtJulDay, double lng, double lat, double alt, Azimuth azimuth, AbstractLunarEclipse.LunarType lunarType, double magUmbra, double magPenumbra, boolean maxVisible) {
    super(gmtJulDay, lng, lat, alt, azimuth);
    this.lunarType = lunarType;
    this.magUmbra = magUmbra;
    this.magPenumbra = magPenumbra;
    this.maxVisible = maxVisible;
  }

  public AbstractLunarEclipse.LunarType getType() {
    return lunarType;
  }

  public double getMagUmbra() {
    return magUmbra;
  }

  public double getMagPenumbra() {
    return magPenumbra;
  }

  public AbstractLunarEclipse.LunarType getLunarType() {
    return lunarType;
  }

  public boolean isMaxVisible() {
    return maxVisible;
  }

  @Override
  public String toString() {
    return "[LunarEclipseObservation " + " type = " + lunarType  + " , magUmbra=" + magUmbra + ", magPenumbra=" + magPenumbra + ", azimuth=" + azimuth + ']';
  }
}
