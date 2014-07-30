/**
 * @author smallufo
 * @date 2002/8/12
 * @time 上午 02:57:12
 */
package destiny.core.chinese;

import org.jetbrains.annotations.Nullable;

/** 實作 五行 getFiveElement() 以及 陰陽 getYinYang() 以及取得地支順序 getIndex() 的地支 */
public enum SimpleEarthlyBranches implements EarthlyBranchesIF , FiveElementIF , YinYangIF
{
  子(EarthlyBranches.子),
  丑(EarthlyBranches.丑),
  寅(EarthlyBranches.寅),
  卯(EarthlyBranches.卯),
  辰(EarthlyBranches.辰),
  巳(EarthlyBranches.巳),
  午(EarthlyBranches.午),
  未(EarthlyBranches.未),
  申(EarthlyBranches.申),
  酉(EarthlyBranches.酉),
  戌(EarthlyBranches.戌),
  亥(EarthlyBranches.亥);
  
  
  @Nullable
  private EarthlyBranches eb = null;
  
  private SimpleEarthlyBranches(EarthlyBranches eb)
  {
    this.eb = eb;
  }
  
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
  public static FiveElement getFiveElement(EarthlyBranches eb)
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
    return ( EarthlyBranches.getIndex(eb) % 2 == 0);
  }


  /**
   * 實作 EarthlyBranchesIF
   */
  public int getIndex()
  {
    return eb.getIndex();
  }

}
