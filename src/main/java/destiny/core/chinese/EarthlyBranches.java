package destiny.core.chinese;


import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 地支系統
 * */
public enum EarthlyBranches implements EarthlyBranchesIF
{
  子('子'),
  丑('丑'),
  寅('寅'),
  卯('卯'),
  辰('辰'),
  巳('巳'),
  午('午'),
  未('未'),
  申('申'),
  酉('酉'),
  戌('戌'),
  亥('亥');
  
  
  private char name;
  
  private final static EarthlyBranches[] EarthlyBranchesArray =
    new EarthlyBranches[] { 子 , 丑 , 寅 , 卯 , 辰 , 巳 , 午 , 未 , 申 , 酉 , 戌 , 亥};
  
  EarthlyBranches(char c)
  {
    this.name = c;
  }

  /**
   * 抓取地支的 index , 為 0-based <BR>
   * 0 為 子  <BR>
   * 1 為 丑  <BR>
   * ...      <BR>
   * 11 為 亥 <BR>
   * 
   * @param index
   * @return
   */
  public static EarthlyBranches getEarthlyBranches(int index)
  {
    /**
     * 如果 index < 0  , 則 加 12 , recursive 再傳一次<BR>
     * 如果 index >=12 , 則 減 12 , recursive 再傳一次<BR> 
     */
    if (index < 0 )
      return getEarthlyBranches(index+12);
    else if (index >=12)
      return getEarthlyBranches(index-12);
    return EarthlyBranchesArray[index];
  }
  
  public static Optional<EarthlyBranches> getEarthlyBranches(char c)
  {
    EarthlyBranches result = null;
    for (EarthlyBranches aEarthlyBranchesArray : EarthlyBranchesArray) {
      if (aEarthlyBranchesArray.name == c) {
        result = aEarthlyBranchesArray;
        break;
      }
    }
    if (result != null)
      return Optional.of(result);
    else
      return Optional.empty();
  }
  
  /** 取得對沖 的地支 */
  public EarthlyBranches getOpposite()
  {
    return EarthlyBranches.getEarthlyBranches(getIndex()+6);
  }
  
  /**
   * 子[0] ~ 亥[11]
   */
  public static int getIndex(@NotNull EarthlyBranches eb)
  {
    int index = -1;
    for (int i=0 ; i < EarthlyBranchesArray.length ; i++)
    {
      if ( eb.equals(EarthlyBranchesArray[i]) )
        index = i;
    }
    return index;
  }

  /**
   * 子[0] ~ 亥[11]
   */
  public int getIndex()
  {
    return getIndex(this);
  }

  
  /**
   * 實作 Comparable
   * */
  /*
  public int compareTo(Object o)
  {
    EarthlyBranches e = (EarthlyBranches) o;
    if ( getIndex(this) < getIndex(e) )
      return -1;
    else if ( getIndex(this) == getIndex(e) )
      return 0;
    else
      return 1;
  }
  */
  
  @NotNull
  @Override
  public String toString()
  {
    return String.valueOf(name);
  }
}
