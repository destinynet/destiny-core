/**
 * @author smallufo
 * @date 2002/8/12
 * @time 上午 02:57:12
 */
package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** 實作 五行 getFiveElement() 以及 陰陽 getYinYang() 以及取得地支順序 getIndex() 的地支 */
public enum SimpleBranch implements BranchIF, FiveElementIF , YinYangIF
{
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
  
  
  @Nullable
  private Branch eb = null;
  
  private SimpleBranch(Branch eb)
  {
    this.eb = eb;
  }
  
  @NotNull
  public FiveElement getFiveElement()
  {
    switch (eb)
    {
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
    throw new Error("Error while calling EarthlyBranches.getFiveElement() , EarthlyBranches = " + eb.toString());
  }
  
  /**
   * 是否還要再做一個 static FiveElement getFiveElement(SimpleEarthlyBranches eb) ... ??
   * Java 5 中的 enum 無法被繼承 , 真是麻煩... 
   */
  @NotNull
  public static FiveElement getFiveElement(@NotNull Branch eb)
  {
    switch (eb)
    {
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
    throw new Error("Error while calling EarthlyBranches.getFiveElement() , EarthlyBranches = " + eb.toString());
  }

  @Override
  public boolean getBooleanValue() {
    return ( Branch.getIndex(eb) % 2 == 0);
  }


  /**
   * 實作 EarthlyBranchesIF
   */
  public int getIndex()
  {
    return eb.getIndex();
  }

}
