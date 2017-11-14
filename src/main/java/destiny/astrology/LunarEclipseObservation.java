/**
 * Created by smallufo on 2017-11-15.
 */
package destiny.astrology;

public class LunarEclipseObservation extends AbstractEclipseObservation {

  /** 本影強度 , magnitude of Umbra */
  private final double magUmbra;

  /** 半影強度 , magnitude of Penumbra */
  private final double magPenumbra;

  public LunarEclipseObservation(double gmtJulDay, double lng, double lat, double alt, Azimuth azimuth, double magUmbra, double magPenumbra) {
    super(gmtJulDay, lng, lat, alt, azimuth);
    this.magUmbra = magUmbra;
    this.magPenumbra = magPenumbra;
  }

  public double getMagUmbra() {
    return magUmbra;
  }

  public double getMagPenumbra() {
    return magPenumbra;
  }


  @Override
  public String toString() {
    return "[LunarEclipseObservation " + "magUmbra=" + magUmbra + ", magPenumbra=" + magPenumbra + ", azimuth=" + azimuth + ']';
  }
}
