/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology.eclipse;

public class LunarEclipseTotal extends LunarEclipsePartial {

  /** 月亮全部進入地球本影 剛開始 (U2) */
  private final double totalBegin;

  /** 月亮全部進入地球本影 剛結束 (U3) */
  private final double totalEnd;

  public LunarEclipseTotal(double penumbraBegin, double partialBegin, double totalBegin, double max, double totalEnd, double partialEnd, double penumbraEnd) {
    super(penumbraBegin , partialBegin , max , partialEnd , penumbraEnd);
    this.totalBegin = totalBegin;
    this.totalEnd = totalEnd;
  }

  public double getTotalBegin() {
    return totalBegin;
  }

  public double getTotalEnd() {
    return totalEnd;
  }

  @Override
  public LunarType getLunarType() {
    return LunarType.TOTAL;
  }

}
