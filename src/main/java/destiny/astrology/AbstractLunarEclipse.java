/**
 * Created by smallufo on 2017-11-12.
 */
package destiny.astrology;

public abstract class AbstractLunarEclipse extends AbstractEclipse {

  public enum LunarType {
    TOTAL,
    PARTIAL,
    PENUMBRA  // 半影月食
  }

  /** 半影開始 (P1) , 最早 , 可視為整個 eclipse 的 begin */
  protected final double penumbraEnd;

  /** 半影結束 (P4) , 最遲 , 可視為整個 eclipse 的 end   */
  protected final double penumbraBegin;

  public AbstractLunarEclipse(double penumbraBegin, double max, double penumbraEnd) {
    super(penumbraBegin, max, penumbraEnd);
    this.penumbraEnd = penumbraEnd;
    this.penumbraBegin = penumbraBegin;
  }


  public double getPenumbraBegin() {
    return penumbraBegin;
  }

  public double getPenumbraEnd() {
    return penumbraEnd;
  }


  public abstract LunarType getLunarType();
}
