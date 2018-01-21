/**
 * @author smallufo
 * @date 2002/8/12
 * @time 上午 02:57:12
 */
package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

/** 實作 五行 getFiveElement() 以及 陰陽 getYinYang() 以及取得地支順序 getIndex() 的地支 */
public enum SimpleBranch implements IBranch<SimpleBranch>, IFiveElement, IYinYang {
  子(Branch.子),
  丑(Branch.丑),
  寅(Branch.寅),
  卯(Branch.卯),
  辰(Branch.辰),
  巳(Branch.巳),
  午(Branch.午),
  未(Branch.未),
  申(Branch.申),
  酉(Branch.酉),
  戌(Branch.戌),
  亥(Branch.亥);
  
  
  @NotNull
  private final Branch eb;

  SimpleBranch(@NotNull Branch eb) {
    this.eb = eb;
  }

  public static SimpleBranch get(Branch b) {
    return SimpleBranch.valueOf(b.name());
  }
  
  @NotNull
  public FiveElement getFiveElement() {
    return getFiveElement(eb);
  }
  
  /**
   * 是否還要再做一個 static FiveElement getFiveElement(SimpleEarthlyBranches eb) ... ??
   * Java 5 中的 enum 無法被繼承 , 真是麻煩... 
   */
  @NotNull
  public static FiveElement getFiveElement(@NotNull Branch eb) {
    switch (eb) {
      case 亥:
      case 子:
        return FiveElement.水;
      case 丑:
      case 辰:
      case 未:
      case 戌:
        return FiveElement.土;
      case 寅:
      case 卯:
        return FiveElement.木;
      case 巳:
      case 午:
        return FiveElement.火;
      case 申:
      case 酉:
        return FiveElement.金;
    }
    throw new AssertionError("getFiveElement() of " + eb);
  }

  @Override
  public boolean getBooleanValue() {
    return getBooleanValue(this.getBranch());
  }

  public final static boolean getBooleanValue(Branch branch) {
    return (Branch.Companion.getIndex(branch) % 2 == 0);
  }


  @Override
  public int getAheadOf(SimpleBranch other) {
    return eb.getAheadOf(other.eb);
  }

  @Override
  public Branch getBranch() {
    return eb;
  }
}
