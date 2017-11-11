/**
 * Created by smallufo on 2017-11-11.
 */
package destiny.astrology;

/** 全環食 */
public class EclipseHybrid extends EclipseAnnularCentered {

  public EclipseHybrid(double max, double partialBegin, double partialEnd, double totalBegin, double totalEnd, double centerBegin, double centerEnd, double annularBegin, double annularEnd) {
    super(max, partialBegin, partialEnd, totalBegin, totalEnd, centerBegin, centerEnd, annularBegin, annularEnd);
  }

  @Override
  public Type getType() {
    return Type.HYBRID;
  }
}
