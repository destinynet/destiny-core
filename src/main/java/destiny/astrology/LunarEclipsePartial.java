/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

public class LunarEclipsePartial extends AbstractLunarEclipse {

  public LunarEclipsePartial(double max, double begin, double end) {
    super(max, begin, end);
  }

  @Override
  public LunarType getLunarType() {
    return LunarType.PARTIAL;
  }

}
