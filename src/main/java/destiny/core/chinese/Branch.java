package destiny.core.chinese;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * 地支系統
 * */
public enum Branch implements BranchIF<Branch>
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
  
  
  private final char name;
  
  private final static Branch[] ARRAY =
    new Branch[] { 子 , 丑 , 寅 , 卯 , 辰 , 巳 , 午 , 未 , 申 , 酉 , 戌 , 亥};
  
  Branch(char c)
  {
    this.name = c;
  }

  /**
   * 抓取地支的 index , 為 0-based <BR>
   * 0 為 子  <BR>
   * 1 為 丑  <BR>
   * ...      <BR>
   * 11 為 亥 <BR>
   */
  public static Branch get(int index)
  {
    /**
     * 如果 index < 0  , 則 加 12 , recursive 再傳一次<BR>
     * 如果 index >=12 , 則 減 12 , recursive 再傳一次<BR> 
     */
    if (index < 0 )
      return get(index + 12);
    else if (index >=12)
      return get(index - 12);
    return ARRAY[index];
  }

  /**
   * 取得下 n 個地支為何
   * n = 0 : 傳回自己
   */
  public Branch next(int n) {
    return get(getIndex(this) + n);
  }

  /**
   * 取得前 n 個地支為何
   * n = 0 : 傳回自己
   */
  public Branch prev(int n) {
    return next(0-n);
  }

  
  public static Optional<Branch> get(char c)
  {
    Branch result = null;
    for (Branch aBranchArray : ARRAY) {
      if (aBranchArray.name == c) {
        result = aBranchArray;
        break;
      }
    }
    if (result != null)
      return Optional.of(result);
    else
      return Optional.empty();
  }
  
  /** 取得對沖 的地支 */
  public Branch getOpposite()
  {
    return Branch.get(getIndex() + 6);
  }

  /** 取得 六合 的地支 */
  public Branch getCombined() {
    switch (this) {
      case 子 : return 丑;
      case 丑 : return 子;
      case 寅 : return 亥;
      case 卯 : return 戌;
      case 辰 : return 酉;
      case 巳 : return 申;

      case 午 : return 未;
      case 未 : return 午;
      case 申 : return 巳;
      case 酉 : return 辰;
      case 戌 : return 卯;
      case 亥 : return 寅;
      default: throw new AssertionError();
    }
  }
  
  /**
   * 子[0] ~ 亥[11]
   */
  public static int getIndex(@NotNull Branch eb)
  {
    int index = -1;
    for (int i = 0; i < ARRAY.length; i++) {
      if (eb.equals(ARRAY[i]))
        index = i;
    }
    return index;
  }




  @Override
  public Branch getBranch() {
    return this;
  }

  /**
   * 子[0] ~ 亥[11]
   */
  public int getIndex() {
    return getIndex(this);
  }

/**
   * 子[1] ~ 亥[12]
   */
  public int getIndexFromOne() {
    return getIndex() +1;
  }


  /**
   * 此地支「領先」另一個地支多少距離. 其值一定為正值
   * 子領先子 0
   * 子領先丑 11
   * ...
   * 子領先亥 1
   * */
  @Override
  public int getAheadOf(Branch other) {
    int steps = getIndex() - other.getIndex();
    return (steps >=0 ? steps : steps + 12);
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

  public static Iterable<Branch> iterable() {
    return Arrays.asList(ARRAY);
  }


}
