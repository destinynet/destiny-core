package destiny.core.chinese;

import destiny.tools.ArrayTools;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** 天干系統 */
public enum Stem implements Comparable<Stem> , FiveElementIF , YinYangIF {

  甲('甲'),
  乙('乙'),
  丙('丙'),
  丁('丁'),
  戊('戊'),
  己('己'),
  庚('庚'),
  辛('辛'),
  壬('壬'),
  癸('癸');
  
  private final char name;

  private final static Stem[] ARRAY = {甲, 乙, 丙, 丁, 戊, 己, 庚, 辛, 壬, 癸};
  
  private final static List<Stem> STEM_LIST = Arrays.asList(ARRAY);

  Stem(char c)
  {
    this.name = c;
  }
  
  /** 從五行 以及 陰陽 建立天干 */
  @NotNull
  public static Stem getHeavenlyStems(FiveElement fiveElement , boolean yinYang) {
    switch (fiveElement) {
      case 木 : return yinYang ? 甲 : 乙;
      case 火 : return yinYang ? 丙 : 丁;
      case 土 : return yinYang ? 戊 : 己;
      case 金 : return yinYang ? 庚 : 辛;
      case 水 : return yinYang ? 壬 : 癸;
      default: throw new AssertionError(fiveElement + "+" + yinYang);
    }
  }

  /**
   * 抓取天干的 index , 為 0-based <BR>
   * 0 為 甲 <BR>
   * 1 為 乙 <BR>
   * ...     <BR>
   * 9 為 癸 <BR> 
   * @param index
   * @return
   */
  public static Stem get(int index) {
    return ArrayTools.get(ARRAY , index);
  }


  /**
   * 取得下 n 個天干為何
   * n = 0 : 傳回自己
   */
  public Stem next(int n) {
    return get(getIndex(this) + n);
  }

  /**
   * 取得前 n 個天干為何
   * n = 0 : 傳回自己
   */
  public Stem prev(int n) {
    return next(0-n);
  }




  public static Optional<Stem> get(char c)
  {
    Stem result = null;
    for (Stem aStemArray : ARRAY) {
      if (aStemArray.name == c) {
        result = aStemArray;
        break;
      }
    }
    if (result != null)
      return Optional.of(result);
    else
      return Optional.empty();
  }
  
  /** 甲[0] ... 癸[9] */
  public static int getIndex(Stem hs) {
    return Arrays.binarySearch(ARRAY , hs);
  }

  /**
   * 取得此天干，領先另一個天干，多少距離. 其值一定為正值
   * 甲 領先 甲 0 步
   * 甲 領先 乙 9 步
   * ...
   * 甲 領先 癸 1 步
   */
  public int getAheadOf(Stem other) {
    int steps = getIndex() - other.getIndex();
    return (steps >=0 ? steps : steps+10);
  }


  
  /** 甲[0] ... 癸[9] */
  public int getIndex()
  {
    return getIndex(this);
  }

  /** 甲[1] ... 癸[10] */
  public int getIndexFromOne() {
    return getIndex(this)+1;
  }
  
  /**
   * 實作 Comparable
   * */
  /*
  public int compareTo(Object o)
  {
    HeavenlyStems h = (HeavenlyStems) o;
    if ( getIndex(this) < getIndex(h) )
      return -1;
    else if ( getIndex(this) == getIndex(h) )
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
  
  /**
   * 實作 FiveElementsIF 的 getFiveElements()
   */
  @NotNull
  public FiveElement getFiveElement()
  {
    switch(getIndex(this))
    {
      case 0:      case 1:
        return FiveElement.木;
      case 2:      case 3:
        return FiveElement.火;
      case 4:      case 5:
        return FiveElement.土;
      case 6:      case 7:
        return FiveElement.金;
      case 8:      case 9:
        return FiveElement.水;
      default:
        throw new AssertionError("HeavenlyStems Error : cannot getFiveElements() : " + toString());
    }
  }//getFiveElements()

  @Override
  public boolean getBooleanValue() {
    return (getIndex(this) % 2 == 0);
  }

  public static Iterable<Stem> iterable() {
    return STEM_LIST;
  }

}
