/**
 * @author smallufo
 * Created on 2010/6/23 at 下午5:17:33
 */
package destiny.iching;

import destiny.core.chinese.YinYangIF;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public enum Hexagram implements HexagramIF , Serializable
{
  乾  (Symbol.乾 , Symbol.乾),
  坤  (Symbol.坤 , Symbol.坤),
  屯  (Symbol.坎 , Symbol.震),
  蒙  (Symbol.艮 , Symbol.坎),
  需  (Symbol.坎 , Symbol.乾),
  訟  (Symbol.乾 , Symbol.坎),
  師  (Symbol.坤 , Symbol.坎),

  比  (Symbol.坎 , Symbol.坤),
  小畜(Symbol.巽 , Symbol.乾),
  履  (Symbol.乾 , Symbol.兌),
  泰  (Symbol.坤 , Symbol.乾),
  否  (Symbol.乾 , Symbol.坤),
  
  同人(Symbol.乾 , Symbol.離),
  大有(Symbol.離 , Symbol.乾),
  謙  (Symbol.坤 , Symbol.艮),
  豫  (Symbol.震 , Symbol.坤),
  隨  (Symbol.兌 , Symbol.震),
  
  蠱  (Symbol.艮 , Symbol.巽),
  臨  (Symbol.坤 , Symbol.兌),
  觀  (Symbol.巽 , Symbol.坤),
  噬嗑(Symbol.離 , Symbol.震),
  賁  (Symbol.艮 , Symbol.離),
  
  剝  (Symbol.艮 , Symbol.坤),
  復  (Symbol.坤 , Symbol.震),
  無妄(Symbol.乾 , Symbol.震),
  大畜(Symbol.艮 , Symbol.乾),
  頤  (Symbol.艮 , Symbol.震),
  
  大過(Symbol.兌 , Symbol.巽),
  坎  (Symbol.坎 , Symbol.坎),
  離  (Symbol.離 , Symbol.離),
      
  咸  (Symbol.兌 , Symbol.艮),
  恆  (Symbol.震 , Symbol.巽),
  遯  (Symbol.乾 , Symbol.艮),
  大壯(Symbol.震 , Symbol.乾),
  
  晉  (Symbol.離 , Symbol.坤),
  明夷(Symbol.坤 , Symbol.離),
  家人(Symbol.巽 , Symbol.離),
  睽  (Symbol.離 , Symbol.兌),
      
  蹇  (Symbol.坎 , Symbol.艮),
  解  (Symbol.震 , Symbol.坎),
  損  (Symbol.艮 , Symbol.兌),
  益  (Symbol.巽 , Symbol.震),
  夬  (Symbol.兌 , Symbol.乾),
  姤  (Symbol.乾 , Symbol.巽),
  萃  (Symbol.兌 , Symbol.坤),
      
  升  (Symbol.坤 , Symbol.巽),
  困  (Symbol.兌 , Symbol.坎),
  井  (Symbol.坎 , Symbol.巽),
  革  (Symbol.兌 , Symbol.離),
  鼎  (Symbol.離 , Symbol.巽),
  震  (Symbol.震 , Symbol.震),
      
  艮  (Symbol.艮 , Symbol.艮),
  漸  (Symbol.巽 , Symbol.艮),
  歸妹(Symbol.震 , Symbol.兌),
  豐  (Symbol.震 , Symbol.離),
  旅  (Symbol.離 , Symbol.艮),
  巽  (Symbol.巽 , Symbol.巽),
      
  兌  (Symbol.兌 , Symbol.兌),
  渙  (Symbol.巽 , Symbol.坎),
  節  (Symbol.坎 , Symbol.兌),
  中孚(Symbol.巽 , Symbol.兌),
  
  小過(Symbol.震 , Symbol.艮),
  既濟(Symbol.坎 , Symbol.離),
  未濟(Symbol.離 , Symbol.坎);
  
  private Symbol upper;
  private Symbol lower;

  Hexagram(Symbol upper, Symbol lower) {
    this.upper = upper;
    this.lower = lower;
  }

  @NotNull
  public static Hexagram getHexagram(Symbol upper, Symbol lower) {
    for (Hexagram h : values()) {
      if (h.getUpperSymbol() == upper && h.getLowerSymbol() == lower)
        return h;
    }
    throw new RuntimeException("Cannot find Hexagram from upper : " + upper + " and lower : " + lower);
  }
  

  /**
   * 由卦序傳回卦
   * @param index 1 <= 卦序 <= 64
   * @param sequence 實作 getIndex(Hexagram) 的介面
   */
  public static Hexagram getHexagram(int index, @NotNull HexagramSequenceIF sequence) {
    if (index > 64)
      return getHexagram(index % 64, sequence);
    if (index <= 0)
      return getHexagram(index + 64, sequence);

    return sequence.getHexagram(index);
  }
  
  /** 從 陰陽 YinYang 實體的 array 傳回 HexagramIF */
  @NotNull
  public static Hexagram getHexagram(YinYangIF[] yinyangs) {
    if (yinyangs == null)
      throw new RuntimeException("yinyangs is NULL !");
    if (yinyangs.length != 6)
      throw new RuntimeException("yinyangs length not equal 6 !");

    Symbol upper = Symbol.getSymbol(yinyangs[3].getBooleanValue(), yinyangs[4].getBooleanValue(), yinyangs[5].getBooleanValue());
    Symbol lower = Symbol.getSymbol(yinyangs[0].getBooleanValue(), yinyangs[1].getBooleanValue(), yinyangs[2].getBooleanValue());
    return getHexagram(upper, lower);
  }

  /**
   * 由六爻的 boolean array 尋找卦象
   *
   * @param booleans [0~5]
   *          六爻陰陽，由初爻至上爻
   * @return 卦的實體(Hexagram)
   */
  @NotNull
  public static Hexagram getHexagram(boolean[] booleans) {
    if (booleans == null)
      throw new RuntimeException("null array !");
    if (booleans.length != 6)
      throw new RuntimeException("booleans length is not 6 , the length is " + booleans.length);
    Symbol lower = Symbol.getSymbol(booleans[0], booleans[1], booleans[2]);
    Symbol upper = Symbol.getSymbol(booleans[3], booleans[4], booleans[5]);

    return Hexagram.getHexagram(upper, lower);
  }

  @NotNull
  public static Hexagram getHexagram(@NotNull List<Boolean> booleans) {
    if (booleans.size() != 6)
      throw new AssertionError("booleans length is not 6 . content : " + booleans);
    Symbol lower = Symbol.getSymbol(booleans.get(0) , booleans.get(1) , booleans.get(2));
    Symbol upper = Symbol.getSymbol(booleans.get(3) , booleans.get(4) , booleans.get(5));
    return Hexagram.getHexagram(upper , lower);
  }

  /**
   * @return 從 六爻 (6,7,8 or 9) 取得本卦以及變卦
   */
  @NotNull
  public static Pair<HexagramIF , HexagramIF> getHexagrams(@NotNull List<Integer> lines) {
    List<Boolean> src = lines.stream().map(i -> i % 2 == 1).collect(Collectors.toList());
    List<Boolean> dst = lines.stream().map(i -> (i == 6 || i == 7)).collect(Collectors.toList());
    return ImmutablePair.of(getHexagram(src), getHexagram(dst));
  }
  
  /** 取得第幾爻的陰陽 , 為了方便起見，index 為 1 至 6 */
  @Override
  public boolean getLine(int index)
  {
    switch(index)
    {
      case 1 : return lower.getBooleanValue(1);
      case 2 : return lower.getBooleanValue(2);
      case 3 : return lower.getBooleanValue(3);
      case 4 : return upper.getBooleanValue(1);
      case 5 : return upper.getBooleanValue(2);
      case 6 : return upper.getBooleanValue(3);
    }
    throw new RuntimeException("index out of range , 1 <= index <= 6 ");
  }

  @NotNull
  @Override
  public boolean[] getYinYangs()
  {
    boolean[] yy = new boolean[6];
    yy[0] = lower.getBooleanValue(1);
    yy[1] = lower.getBooleanValue(2);
    yy[2] = lower.getBooleanValue(3);
    yy[3] = upper.getBooleanValue(1);
    yy[4] = upper.getBooleanValue(2);
    yy[5] = upper.getBooleanValue(3);
    return yy;
  }

  @Override
  public Symbol getUpperSymbol()
  {
    return upper;
  }

  @Override
  public Symbol getLowerSymbol()
  {
    return lower;
  }
  
  /**
   * 第 line 爻動的話，變卦是什麼
   * @param lines [1~6]
   */
  @NotNull
  @Override
  public HexagramIF getHexagram(@NotNull int... lines)
  {
    boolean[] booleans = getYinYangs();
    for (int index : lines) {
      if (index >=1 && index <=6)
        booleans[index-1] = !booleans[index-1];
    }
    return Hexagram.getHexagram(booleans);
  }

  /** @return 互卦 , 去掉初爻、上爻，中間四爻延展出去，故用 Middle Span Hexagram 為名 */
  @NotNull
  public HexagramIF getMiddleSpanHexagram()
  {
    return Hexagram.getHexagram(new boolean[] {lower.getBooleanValue(2) , lower.getBooleanValue(3) , upper.getBooleanValue(1) , lower.getBooleanValue(3) , upper.getBooleanValue(1) , upper.getBooleanValue(2)});
  }

  /** @return 錯卦 , 一卦六爻全變 , 交錯之意 , 故取名 Interlaced Hexagram */
  @NotNull
  public HexagramIF getInterlacedHexagram()
  {
    return Hexagram.getHexagram(new boolean[] {
      !lower.getBooleanValue(1) , !lower.getBooleanValue(2) , !lower.getBooleanValue(3) ,
      !upper.getBooleanValue(1) , !upper.getBooleanValue(2) , !upper.getBooleanValue(3)} );
  }

  /** @return 綜卦 , 上下顛倒 , 故取名 Reversed Hexagram */
  @NotNull
  public HexagramIF getReversedHexagram()
  {
    return Hexagram.getHexagram(new boolean[] {
      upper.getBooleanValue(3) , upper.getBooleanValue(2) , upper.getBooleanValue(1) ,
      lower.getBooleanValue(3) , lower.getBooleanValue(2) , lower.getBooleanValue(1)});
  }

  @NotNull
  @Override
  public String getBinaryCode()
  {
    StringBuilder sb = new StringBuilder();
    for(boolean yy : getYinYangs())
      sb.append(yy ? '1' : '0');
    return sb.toString();
  }
}
