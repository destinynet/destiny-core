/**
 * @author smallufo 
 * Created on 2007/3/8 at 上午 3:44:36
 */ 
package destiny.iching;

import org.jetbrains.annotations.NotNull;

/**
 * 一個最基本的「卦」的資料，只有 取得 各爻陰陽 getLine(int index) / 取得六爻陰陽 getLines()  / 上卦 getUpperSymbol() / 下卦 getLowerSymbol() / 等介面
 */
public interface HexagramIF
{
  /** 取得第幾爻的陰陽 , 為了方便起見，index 為 1 至 6 */
  //public <T extends BaseLine> BaseLine getLine(int index);
  
  /** 取得第幾爻的陰陽 , 為了方便起見，index 為 1 至 6 */
  public boolean getLine(int index);
  
  /** 取得全部的陰陽 */
  @NotNull
  public boolean[] getYinYangs();
  
  /** 取得上卦 */
  public Symbol getUpperSymbol();
  
  /** 取得下卦 */
  public Symbol getLowerSymbol();

  /**
   * 第 line 爻動的話，變卦是什麼
   * @param line [1~6]
   */
  @NotNull
  public HexagramIF getHexagram(int... line);
  
  /** 取得 010101 的表示法 */
  @NotNull
  public String getBinaryCode();
  
  /** 以下三個 method , 屬於「梅花易」常用的詞彙，經仔細思考，還是決定不放入最基本的 methods 中 */
  //互卦 , 去掉初爻、上爻，中間四爻延展出去，故用 Middle Span Hexagram 為名 */
  //public HexagramIF getMiddleSpanHexagram();
  //綜卦 , 上下顛倒 , 故取名 Reversed Hexagram */
  //public HexagramIF getReversedHexagram();
  //錯卦 , 一卦六爻全變 , 交錯之意 , 故取名 Interlaced Hexagram */
  //public HexagramIF getInterlacedHexagram();
}
