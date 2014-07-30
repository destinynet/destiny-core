package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * 中國干支組合表示法，0[甲子] ~ 59[癸亥]
 */
public class StemBranch implements Comparable<StemBranch> , Serializable
{
  @Nullable
  private final HeavenlyStems   stem;   //天干
  @Nullable
  private final EarthlyBranches branch; //地支
  
  // 0[甲子] ~ 59[癸亥]
  @NotNull
  private transient static StemBranch[] StemBranchArray = new StemBranch[60];
  static
  {
    int n=0;
    do
    {
      StemBranchArray[n]= new StemBranch (HeavenlyStems.getHeavenlyStems( n % 10 ) , 
                                          EarthlyBranches.getEarthlyBranches( n % 12 ) );
      n++;
    }
    while(n<60);
  }
  
  private StemBranch(@Nullable HeavenlyStems 天干 , @Nullable EarthlyBranches 地支)
  {
    this.stem = 天干;
    this.branch = 地支;
    if (天干 != null && 地支 != null)
      if ( (HeavenlyStems.getIndex(天干) % 2 )  != (EarthlyBranches.getIndex(地支) %2 ) )
        throw new RuntimeException("Stem/Branch combination illegal ! " + 天干 + " cannot be combined with " + 地支 );
  }
  
  /*
  public StemBranch(char 天干 , char 地支) 
  {
    this.stem = HeavenlyStems.getHeavenlyStems(天干);
    this.branch = EarthlyBranches.getEarthlyBranches(地支);

    if ( (HeavenlyStems.getIndex(this.stem) % 2 )  != (EarthlyBranches.getIndex(this.branch) %2 ) )
          throw new RuntimeException("Stem/Branch combination illegal ! " + stem + " cannot be combined with " + branch );    
  }
  */
  
  /**
   * 0[甲子] ~ 59[癸亥]
   * @param index
   * @return
   */
  public static StemBranch get(int index)
  {
    return StemBranchArray[normalize(index)];
  }
  
  @Nullable
  public static StemBranch get(@Nullable HeavenlyStems 天干 , @Nullable EarthlyBranches 地支)
  {
    if (天干 != null && 地支 != null)
      if ( (HeavenlyStems.getIndex(天干) % 2 )  != (EarthlyBranches.getIndex(地支) %2 ) )
        throw new RuntimeException("Stem/Branch combination illegal ! " + 天干 + " cannot be combined with " + 地支 );
      else
      {
        int Hindex = HeavenlyStems.getIndex(天干);
        int Eindex = EarthlyBranches.getIndex(地支);
        switch (Hindex - Eindex)
        {
          case 0 : case -10 : return StemBranchArray[Eindex];
          case 2 : case  -8 : return StemBranchArray[Eindex+12];
          case 4 : case  -6 : return StemBranchArray[Eindex+24];
          case 6 : case  -4 : return StemBranchArray[Eindex+36];
          case 8 : case  -2 : return StemBranchArray[Eindex+48];
          default : throw new RuntimeException("Invalid HeavenlyStems EarthlyBranches Combination!");
        }
      }
    return new StemBranch(天干 , 地支);
  }
  
  @Nullable
  public static StemBranch get(char heavenlyStems , char earthlyBranches)
  {
    return get(HeavenlyStems.getHeavenlyStems(heavenlyStems) , EarthlyBranches.getEarthlyBranches(earthlyBranches));
  }
  
  @Nullable
  public static StemBranch get(@NotNull String stemBranch)
  {
    if (stemBranch.length() != 2)
      throw new RuntimeException("The length of " + stemBranch + " must equal to 2 !");
    else
      return get(stemBranch.charAt(0) , stemBranch.charAt(1));
  }
  
  private static int normalize(int index)
  {
    if (index >= 60)
      return (normalize(index-60));
    else if (index < 0)
      return (normalize(index+60));
    else
      return index;
  }
  
  /** 取得干支的差距，例如 "乙丑" 距離 "甲子" 的差距為 "1" , 通常是用於計算「需歲」 (尚需加一) */
  public int differs(@NotNull StemBranch sb)
  {
    return getIndex(this) - sb.getIndex();
  }
  
  public boolean equals(@Nullable Object o)
  {
    if ((o != null) && (o.getClass().equals(this.getClass())))
    {
      StemBranch sb = (StemBranch) o;
      return (this.stem == sb.stem && this.branch == sb.branch );
    }
    else return false;
  }//equals()

  public int hashCode()
  {
    int stemCode   = (stem == null ? 0 : stem.hashCode() );
    int branchCode = (branch == null ? 0 : branch.hashCode() );
    int hash = 7;
    hash = hash * 11 + stemCode;
    hash = hash * 11 + branchCode;
    return hash;
  }
  
  /**
   * 0[甲子] ~ 59[癸亥]
   * @param sb 取得某組干支的順序
   * @return 0[甲子] ~ 59[癸亥]
   */
  public static int getIndex(@NotNull StemBranch sb)
  {
    int index=-1;
    for (int i = 0 ; i < StemBranchArray.length ; i ++)
    {
      if (sb.equals(StemBranchArray[i]) )
        index = i;
    }
    return index;
  }//getIndex()
  
  /**
   * @return 0[甲子] ~ 59[癸亥]
   */
  public int getIndex()
  {
    return getIndex(this);
  }
  
  @NotNull
  public String toString()
  {
    return stem.toString()+branch.toString();
  }
   /**
   * 實作 Comparable 的 compareTo()
   */
  public int compareTo(StemBranch o)
  {
    StemBranch sb = (StemBranch) o ;
    return (getIndex(this) - getIndex(sb));
    
    /**
    StemBranch sb = (StemBranch)o;
    if ( getIndex(this) < StemBranch.getIndex(sb) )
      return -1;
    else if ( getIndex(this) == StemBranch.getIndex(sb))
      return 0;
    else
      return 1;
    */
  }//compareTo()
 
  /**
   * @return 天干
   */
  @Nullable
  public HeavenlyStems getStem()
  {
    return stem;
  }
  
  /**
   * @return 地支
   */
  @Nullable
  public EarthlyBranches getBranch()
  {
    return branch;
  }

  /** 取得下一組干支 , 甲子 傳回 乙丑 */
  public StemBranch getNext()
  {
    return get(getIndex(this)+1);
  }
  
  /** 取得上一組干支 , 甲子 傳回 癸亥 */
  public StemBranch getPrevious()
  {
    return get(getIndex(this)-1);
  }
  
}
