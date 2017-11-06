/**
 * Created by smallufo on 2017-11-06.
 */
package destiny.astrology;

public class EclipsePartial extends AbstractEclipse {

  public EclipsePartial(double max, double begin, double end) {
    super(max, begin, end);
  }

  @Override
  public Type getType() {
    return Type.PARTIAL;
  }


}
