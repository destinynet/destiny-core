package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

/**
 * 中國干支組合表示法，0[甲子] ~ 59[癸亥]
 */
public class StemBranch extends StemBranchOptional implements Comparable<StemBranch> , Serializable
{
//  @Nullable
//  private final HeavenlyStems   stem;   //stem
//
//  @Nullable
//  private final EarthlyBranches branch; //branch
  
  // 0[甲子] ~ 59[癸亥]
  @NotNull
  private transient static StemBranch[] ARRAY = new StemBranch[60];

  static {
    int n = 0;
    do {
      ARRAY[n] = new StemBranch(Stem.getHeavenlyStems(n % 10), Branch.getEarthlyBranches(n % 12));
      n++;
    } while (n < 60);
  }

  StemBranch(@NotNull Stem stem, @NotNull Branch branch) {
    super(Optional.of(stem), Optional.of(branch));
  }
  
  /*
  public StemBranch(char stem , char branch)
  {
    this.stem = HeavenlyStems.getHeavenlyStems(stem);
    this.branch = EarthlyBranches.getEarthlyBranches(branch);

    if ( (HeavenlyStems.getIndex(this.stem) % 2 )  != (EarthlyBranches.getIndex(this.branch) %2 ) )
          throw new RuntimeException("Stem/Branch combination illegal ! " + stem + " cannot be combined with " + branch );    
  }
  */
  
  /**
   * @param index 0[甲子] ~ 59[癸亥]
   */
  public static StemBranch get(int index)
  {
    return ARRAY[normalize(index)];
  }

  /**
   * 取得下 n 組干支組合
   * n = 0 : 傳回自己
   */
  public StemBranch next(int n) {
    return get(getIndex(this) + n);
  }

  /**
   * 取得前 n 組干支組合
   * n = 0 : 傳回自己
   */
  public StemBranch prev(int n) {
    return next(0-n);
  }

  /** 取得下一組干支 , 甲子 傳回 乙丑 */
  public StemBranch getNext() {
    return next(1);
  }

  /** 取得上一組干支 , 甲子 傳回 癸亥 */
  public StemBranch getPrevious() {
    return prev(1);
  }

  @NotNull
  public static StemBranch get(@NotNull Stem stem, @NotNull Branch branch)
  {
    if ( (Stem.getIndex(stem) % 2 )  != (Branch.getIndex(branch) %2 ) )
        throw new RuntimeException("Stem/Branch combination illegal ! " + stem + " cannot be combined with " + branch);

    int hIndex = Stem.getIndex(stem);
    int eIndex = Branch.getIndex(branch);
    switch (hIndex - eIndex) {
      case 0:
      case -10:
        return get(eIndex);
      case 2:
      case -8:
        return get(eIndex + 12);
      case 4:
      case -6:
        return get(eIndex + 24);
      case 6:
      case -4:
        return get(eIndex + 36);
      case 8:
      case -2:
        return get(eIndex + 48);
      default:
        throw new AssertionError("Invalid stem/branch Combination!");
    }
  }
  
  @Nullable
  public static StemBranch get(char heavenlyStems , char earthlyBranches)
  {
    return get(Stem.getHeavenlyStems(heavenlyStems).get() , Branch.getEarthlyBranches(earthlyBranches).get());
  }
  
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
  
  /** 取得干支的差距，例如 "乙丑" 距離 "甲子" 的差距為 "1" , 通常是用於計算「虛歲」 (尚需加一) */
  public int differs(@NotNull StemBranch sb)
  {
    return getIndex(this) - sb.getIndex();
  }

  /**
   * 取得此干支，領先另一組，多少步. 其值一定為正值
   *
   * 「甲子」領先「癸亥」 1
   * 「甲子」領先「乙丑」59
   */
  public int getAheadOf(StemBranch other) {
    int steps = getIndex() - other.getIndex();
    return (steps >=0 ? steps : steps+60);
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
  private static int getIndex(@NotNull StemBranch sb)
  {
    int index = -1;
    for (int i = 0; i < ARRAY.length; i++) {
      if (sb.equals(ARRAY[i]))
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

  public static Iterator<StemBranch> iterator() {
    return Arrays.stream(ARRAY).iterator();
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
    return (getIndex(this) - getIndex(o));
    
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
   * @return stem
   */
  @NotNull
  public Stem getStem()
  {
    return stem.get();
  }
  
  /**
   * @return branch
   */
  @NotNull
  public Branch getBranch()
  {
    return branch.get();
  }



  public static Iterable<StemBranch> iterable() {
    return Arrays.asList(ARRAY);
  }
}
