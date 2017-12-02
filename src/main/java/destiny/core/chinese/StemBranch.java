package destiny.core.chinese;

import destiny.tools.ArrayTools;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

import static destiny.core.chinese.Branch.*;
import static destiny.core.chinese.Stem.*;

/**
 * 中國干支組合表示法，0[甲子] ~ 59[癸亥]
 */
public class StemBranch extends StemBranchOptional implements Comparable<StemBranch> , Serializable {

  public final static StemBranch 甲子 = new StemBranch(甲 , 子);
  public final static StemBranch 乙丑 = new StemBranch(乙 , 丑);
  public final static StemBranch 丙寅 = new StemBranch(丙 , 寅);
  public final static StemBranch 丁卯 = new StemBranch(丁 , 卯);
  public final static StemBranch 戊辰 = new StemBranch(戊 , 辰);
  public final static StemBranch 己巳 = new StemBranch(己 , 巳);
  public final static StemBranch 庚午 = new StemBranch(庚 , 午);
  public final static StemBranch 辛未 = new StemBranch(辛 , 未);
  public final static StemBranch 壬申 = new StemBranch(壬 , 申);
  public final static StemBranch 癸酉 = new StemBranch(癸 , 酉);
  public final static StemBranch 甲戌 = new StemBranch(甲 , 戌);
  public final static StemBranch 乙亥 = new StemBranch(乙 , 亥);
  public final static StemBranch 丙子 = new StemBranch(丙 , 子);
  public final static StemBranch 丁丑 = new StemBranch(丁 , 丑);
  public final static StemBranch 戊寅 = new StemBranch(戊 , 寅);
  public final static StemBranch 己卯 = new StemBranch(己 , 卯);
  public final static StemBranch 庚辰 = new StemBranch(庚 , 辰);
  public final static StemBranch 辛巳 = new StemBranch(辛 , 巳);
  public final static StemBranch 壬午 = new StemBranch(壬 , 午);
  public final static StemBranch 癸未 = new StemBranch(癸 , 未);
  public final static StemBranch 甲申 = new StemBranch(甲 , 申);
  public final static StemBranch 乙酉 = new StemBranch(乙 , 酉);
  public final static StemBranch 丙戌 = new StemBranch(丙 , 戌);
  public final static StemBranch 丁亥 = new StemBranch(丁 , 亥);
  public final static StemBranch 戊子 = new StemBranch(戊 , 子);
  public final static StemBranch 己丑 = new StemBranch(己 , 丑);
  public final static StemBranch 庚寅 = new StemBranch(庚 , 寅);
  public final static StemBranch 辛卯 = new StemBranch(辛 , 卯);
  public final static StemBranch 壬辰 = new StemBranch(壬 , 辰);
  public final static StemBranch 癸巳 = new StemBranch(癸 , 巳);
  public final static StemBranch 甲午 = new StemBranch(甲 , 午);
  public final static StemBranch 乙未 = new StemBranch(乙 , 未);
  public final static StemBranch 丙申 = new StemBranch(丙 , 申);
  public final static StemBranch 丁酉 = new StemBranch(丁 , 酉);
  public final static StemBranch 戊戌 = new StemBranch(戊 , 戌);
  public final static StemBranch 己亥 = new StemBranch(己 , 亥);
  public final static StemBranch 庚子 = new StemBranch(庚 , 子);
  public final static StemBranch 辛丑 = new StemBranch(辛 , 丑);
  public final static StemBranch 壬寅 = new StemBranch(壬 , 寅);
  public final static StemBranch 癸卯 = new StemBranch(癸 , 卯);
  public final static StemBranch 甲辰 = new StemBranch(甲 , 辰);
  public final static StemBranch 乙巳 = new StemBranch(乙 , 巳);
  public final static StemBranch 丙午 = new StemBranch(丙 , 午);
  public final static StemBranch 丁未 = new StemBranch(丁 , 未);
  public final static StemBranch 戊申 = new StemBranch(戊 , 申);
  public final static StemBranch 己酉 = new StemBranch(己 , 酉);
  public final static StemBranch 庚戌 = new StemBranch(庚 , 戌);
  public final static StemBranch 辛亥 = new StemBranch(辛 , 亥);
  public final static StemBranch 壬子 = new StemBranch(壬 , 子);
  public final static StemBranch 癸丑 = new StemBranch(癸 , 丑);
  public final static StemBranch 甲寅 = new StemBranch(甲 , 寅);
  public final static StemBranch 乙卯 = new StemBranch(乙 , 卯);
  public final static StemBranch 丙辰 = new StemBranch(丙 , 辰);
  public final static StemBranch 丁巳 = new StemBranch(丁 , 巳);
  public final static StemBranch 戊午 = new StemBranch(戊 , 午);
  public final static StemBranch 己未 = new StemBranch(己 , 未);
  public final static StemBranch 庚申 = new StemBranch(庚 , 申);
  public final static StemBranch 辛酉 = new StemBranch(辛 , 酉);
  public final static StemBranch 壬戌 = new StemBranch(壬 , 戌);
  public final static StemBranch 癸亥 = new StemBranch(癸 , 亥);

  // 0[甲子] ~ 59[癸亥]
  @NotNull
  private final static StemBranch[] ARRAY = {甲子,乙丑,丙寅,丁卯,戊辰,己巳,庚午,辛未,壬申,癸酉,甲戌,乙亥,丙子,丁丑,戊寅,己卯,庚辰,辛巳,壬午,癸未,甲申,乙酉,丙戌,丁亥,戊子,己丑,庚寅,辛卯,壬辰,癸巳,甲午,乙未,丙申,丁酉,戊戌,己亥,庚子,辛丑,壬寅,癸卯,甲辰,乙巳,丙午,丁未,戊申,己酉,庚戌,辛亥,壬子,癸丑,甲寅,乙卯,丙辰,丁巳,戊午,己未,庚申,辛酉,壬戌,癸亥};

  private final static List<StemBranch> list = Arrays.asList(ARRAY);

  private StemBranch(@NotNull Stem stem, @NotNull Branch branch) {
    super(Optional.of(stem), Optional.of(branch));
  }
  
  /**
   * @param index 0[甲子] ~ 59[癸亥]
   */
  public static StemBranch get(int index) {
    return ArrayTools.INSTANCE.get(ARRAY , index);
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

  public static StemBranch get(char stemChar , char branchChar) {
    Optional<Stem> stemOptional = Stem.get(stemChar);
    Optional<Branch> branchOptional = Branch.get(branchChar);

    return stemOptional.map(stem1 -> StemBranch.get(stem1 , branchOptional.orElseThrow(() -> new RuntimeException("Cannot find Branch: " + branchChar))))
      .orElseThrow(() -> new RuntimeException("Cannot get StemBranch("+stemChar+" , "+branchChar+")"));

  }
  
  public static StemBranch get(@NotNull String stemBranch)
  {
    if (stemBranch.length() != 2)
      throw new RuntimeException("The length of " + stemBranch + " must equal to 2 !");
    else
      return get(stemBranch.charAt(0) , stemBranch.charAt(1));
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
  private static int getIndex(@NotNull StemBranch sb) {
    //return Arrays.binarySearch(ARRAY , sb);
    return list.indexOf(sb);
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
  public int compareTo(@NotNull StemBranch o) {
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
