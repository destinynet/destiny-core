package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/** 天干系統 */
public enum Stem implements Comparable<Stem> , FiveElementIF , YinYangIF
{
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
  
  private char name;
  
  private final static Stem[] STEM_ARRAY =
    { 甲 , 乙 , 丙 , 丁 , 戊 ,
      己 , 庚 , 辛 , 壬 , 癸 };
  
  private final static List<Stem> STEM_LIST = Arrays.asList(STEM_ARRAY);

  Stem(char c)
  {
    this.name = c;
  }
  
  /** 從五行 以及 陰陽 建立天干 */
  @NotNull
  public static Stem getHeavenlyStems(FiveElement fiveElement , boolean yinYang) {
    if (fiveElement == FiveElement.木)
    {
      if (yinYang)
        return 甲;
      else
        return 乙;
    }
    else if (fiveElement == FiveElement.火)
    {
      if (yinYang)
        return 丙;
      else
        return 丁;
    }
    else if (fiveElement == FiveElement.土)
    {
      if (yinYang)
        return 戊;
      else
        return 己;
    }
    else if (fiveElement == FiveElement.金)
    {
      if (yinYang)
        return 庚;
      else
        return 辛;
    }
    else
    {
      if (yinYang)
        return 壬;
      else
        return 癸;
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
  public static Stem getHeavenlyStems(int index)
  {
    /**
     * 如果 index < 0  , 則 加 10 , recursive 再傳一次<BR>
     * 如果 index >=10 , 則 減 10 , recursive 再傳一次<BR> 
     */
    if (index < 0)
      return getHeavenlyStems(index+10);
    else if (index >=10 )
      return (getHeavenlyStems(index-10));
    return STEM_ARRAY[index];
  }


  /**
   * 取得下 n 個天干為何
   * n = 0 : 傳回自己
   */
  public Stem next(int n) {
    return getHeavenlyStems(getIndex(this) + n);
  }

  /**
   * 取得前 n 個天干為何
   * n = 0 : 傳回自己
   */
  public Stem prev(int n) {
    return next(0-n);
  }


  public static Optional<Stem> getHeavenlyStems(char c)
  {
    Stem result = null;
    for (Stem aStemArray : STEM_ARRAY) {
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
  public static int getIndex(Stem hs)
  {
    return STEM_LIST.indexOf(hs);
    /*
    int result = heavenlyStemsList.indexOf(hs);
    if (result != -1)
      return result;
    else
      throw new RuntimeException("Cannot find HeavenlyStems : " + hs + " in HeavenlyStems .");
    */
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
    return Arrays.asList(STEM_ARRAY);
  }

}
