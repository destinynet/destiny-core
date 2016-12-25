package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

import static destiny.core.chinese.Branch.*;

/**
 * 中國干支組合表示法，0[甲子] ~ 59[癸亥]
 */
public class StemBranch extends StemBranchOptional implements Comparable<StemBranch> , Serializable {
  // 0[甲子] ~ 59[癸亥]
  @NotNull
  private transient static StemBranch[] ARRAY = new StemBranch[60];

  static {
    int n = 0;
    do {
      ARRAY[n] = new StemBranch(Stem.get(n % 10), Branch.get(n % 12));
      n++;
    } while (n < 60);
  }

  private StemBranch(@NotNull Stem stem, @NotNull Branch branch) {
    super(Optional.of(stem), Optional.of(branch));
  }
  
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

  @Override
  public Optional<? extends StemBranchOptional> nextOpt(int n) {
    return Optional.of(next(n));
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

    int sIndex = Stem.getIndex(stem);
    int bIndex = Branch.getIndex(branch);
    switch (sIndex - bIndex) {
      case 0:
      case -10:
        return get(bIndex);
      case 2:
      case -8:
        return get(bIndex + 12);
      case 4:
      case -6:
        return get(bIndex + 24);
      case 6:
      case -4:
        return get(bIndex + 36);
      case 8:
      case -2:
        return get(bIndex + 48);
      default:
        throw new AssertionError("Invalid stem/branch Combination!");
    }
  }
  
  public static StemBranch get(char heavenlyStems , char earthlyBranches)
  {
    return get(Stem.getHeavenlyStems(heavenlyStems).get() , Branch.get(earthlyBranches).get());
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
    assert (stem != null && branch != null);
    return stem.toString()+branch.toString();
  }

  /**
   * 實作 Comparable 的 compareTo()
   */
  public int compareTo(StemBranch o) {
    return (getIndex(this) - getIndex(o));
  }//compareTo()
 
  /**
   * @return stem
   */
  @NotNull
  public Stem getStem()
  {
    assert stem != null;
    return stem;
  }
  
  /**
   * @return branch
   */
  @NotNull
  public Branch getBranch()
  {
    assert branch != null;
    return branch;
  }

  public static List<StemBranch> getList() {
    return Arrays.asList(ARRAY);
  }

  public static Iterable<StemBranch> iterable() {
    return Arrays.asList(ARRAY);
  }

  /** 取得「空亡」的兩個地支 */
  public static Collection<Branch> getEmpties(StemBranch sb) {
    int shift = sb.getStem().getIndex() - sb.getBranch().getIndex();
    switch (shift) {
      case 0 :
        return Arrays.asList(戌 , 亥);
      case 2 :
      case -10 :
        return Arrays.asList(申 , 酉);
      case 4 :
      case -8 :
        return Arrays.asList(午 , 未);
      case 6 :
      case -6 :
        return Arrays.asList(辰 , 巳);
      case 8 :
      case -4 :
        return Arrays.asList(寅 , 卯);
      case 10 :
      case -2:
        return Arrays.asList(子 , 丑);
      default: throw new AssertionError("Cannot find 空亡 from " + sb);
    }
  }

  /** 取得「空亡」的兩個地支 */
  public Collection<Branch> getEmpties() {
    return StemBranch.getEmpties(this);
  }

}
