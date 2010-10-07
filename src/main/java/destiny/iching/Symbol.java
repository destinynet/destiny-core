/**
 * @author smallufo
 * Created on 2002/8/13 at 下午 01:08:17
 */
package destiny.iching;

import java.io.Serializable;

import destiny.core.chinese.FiveElement;
import destiny.core.chinese.FiveElementIF;
import destiny.core.chinese.YinYang;
import destiny.core.chinese.YinYangIF;


/**
 * 八卦基本符號以及其資料
 */
public enum Symbol implements Serializable , SymbolIF , FiveElementIF
{
  乾('乾', new YinYang[] {YinYang.陽 , YinYang.陽 , YinYang.陽 } ),
  兌('兌', new YinYang[] {YinYang.陽 , YinYang.陽 , YinYang.陰 } ),
  離('離', new YinYang[] {YinYang.陽 , YinYang.陰 , YinYang.陽 } ),
  震('震', new YinYang[] {YinYang.陽 , YinYang.陰 , YinYang.陰 } ),
  巽('巽', new YinYang[] {YinYang.陰 , YinYang.陽 , YinYang.陽 } ),
  坎('坎', new YinYang[] {YinYang.陰 , YinYang.陽 , YinYang.陰 } ),
  艮('艮', new YinYang[] {YinYang.陰 , YinYang.陰 , YinYang.陽 } ),
  坤('坤', new YinYang[] {YinYang.陰 , YinYang.陰 , YinYang.陰 } );
  
  private char name;
  private YinYang[] yinYangs = new YinYang[3];
  
  private static final Symbol[] symbolArray = {乾 , 兌 , 離 , 震 , 巽 , 坎 , 艮 , 坤};
  
  private Symbol(char name , YinYang[] yinYangs)
  {
    this.name = name;
    this.yinYangs = yinYangs;
  }
  
  /**
   * 取得八個卦
   */
  public static Symbol[] getSymbols()
  {
    return symbolArray;
  }
  
  public String toString()
  {
    return String.valueOf(name);
  }
  
  /**
   * 實作 SymbolIF
   * 取得八卦名
   */
  public char getName()
  {
    return name;
  }
  
  /**
   * 實作 SymbolIF
   * 取得一個卦的第幾爻
   * index 值為 1,2,或3
   */
  public YinYang getYinYang(int index)
  {
    return yinYangs[index-1];
  }
  
  /**
   * 「由下而上」 三個陰陽 , 查詢卦象為何
   */
  public static Symbol getSymbol(YinYangIF[] line)
  {
    for (int i=0 ; i< symbolArray.length ; i++)
    {
      if ( (line[0].getYinYang() == symbolArray[i].yinYangs[0] ) && 
           (line[1].getYinYang() == symbolArray[i].yinYangs[1] ) &&
           (line[2].getYinYang() == symbolArray[i].yinYangs[2] ) )
      {
        return symbolArray[i];
      }
    }
    throw new RuntimeException("Cannot find Symbol for " + line[0] + " , " + line[1] + " , " + line[2]);
  }
  
  /**
   * 「由下而上」 三個陰陽 , 查詢卦象為何
   */
  public static Symbol getSymbol(boolean[] lines)
  {
    for (int i=0 ; i< symbolArray.length ; i++)
    {
      if ( (YinYang.getYinYang(lines[0]) == symbolArray[i].yinYangs[0] ) && 
           (YinYang.getYinYang(lines[1]) == symbolArray[i].yinYangs[1] ) &&
           (YinYang.getYinYang(lines[2]) == symbolArray[i].yinYangs[2] ) )
      {
        return symbolArray[i];
      }
    }
    throw new RuntimeException("Cannot find Symbol for " + lines[0] + " , " + lines[1] + " , " + lines[2]);
  }

  /**
   * 由 三個陰陽 , 查詢卦象為何 , 比較標準的命名方式
   */
  public static Symbol valueOf(YinYangIF[] yinYangs)
  {
    return getSymbol(yinYangs);
  }
  
  /**
   * implements FiveElementIF 
   */
  public FiveElement getFiveElement()
  {
    switch ( this )
    {
      case 乾 : case 兌 : return FiveElement.金 ;
      case 離 :          return FiveElement.火 ;
      case 震 : case 巽 : return FiveElement.木 ;
      case 坎 :          return FiveElement.水 ;
      case 艮 : case 坤 : return FiveElement.土 ;
      default   : return null;
    }
  }
}
