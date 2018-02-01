package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Branch.巳;

/**
 * @author smallufo
 * @date 2002/8/26
 * @time 下午 01:19:37
 */
public class Characteristics {

  private Stem 日干;

  @NotNull
  private final Branch[] 天乙貴人 = new Branch[2];

  public Characteristics(Stem 日干) {
    this.日干 = 日干;
  }

  /**
   * 被 {@link ITianyi} 所取代
   * 指向 {@link destiny.core.chinese.impls.TianyiAuthorizedImpl}
   */
  @NotNull
  public Branch[] get天乙貴人() {
    if (日干 == Stem.甲 || 日干 == Stem.戊 || 日干 == Stem.庚) {
      天乙貴人[0] = 丑;
      天乙貴人[1] = 未;
    }
    else if (日干 == Stem.乙 || 日干 == Stem.己) {
      天乙貴人[0] = 子;
      天乙貴人[1] = 申;
    }
    else if (日干 == Stem.丙 || 日干 == Stem.丁) {
      天乙貴人[0] = 酉;
      天乙貴人[1] = 亥;
    }
    else if (日干 == Stem.辛) {
      天乙貴人[0] = 寅;
      天乙貴人[1] = 午;
    }
    else if (日干 == Stem.壬 || 日干 == Stem.癸) {
      天乙貴人[0] = 卯;
      天乙貴人[1] = 巳;
    }
    return 天乙貴人;
  }


}
