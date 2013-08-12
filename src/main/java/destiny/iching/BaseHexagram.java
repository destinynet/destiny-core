/**
 * @author smallufo
 * Created on 2010/6/24 at 上午4:38:39
 */
package destiny.iching;

import java.io.Serializable;

import destiny.core.chinese.YinYang;
import destiny.core.chinese.YinYangIF;

/** 因為 enum Hexagram 無法被繼承 , 所以才做出這個中介/adapter class , 提供給其他 class 繼承 */
public class BaseHexagram implements HexagramIF , Serializable
{
  private Hexagram hexagram;
  
  public BaseHexagram(Hexagram hexagram)
  {
    this.hexagram = hexagram;
  }
  
  public BaseHexagram(YinYangIF[] yinyangs)
  {
    if(yinyangs.length != 6)
      throw new RuntimeException("BaseHexagram yinyangs length not equal 6!");
    this.hexagram = Hexagram.getHexagram(yinyangs);
  }

  @Override
  public YinYangIF getLine(int index)
  {
    return hexagram.getLine(index);
  }

  @Override
  public Symbol getLowerSymbol()
  {
    return hexagram.getLowerSymbol();
  }

  @Override
  public Symbol getUpperSymbol()
  {
    return hexagram.getUpperSymbol();
  }

  @Override
  public YinYang[] getYinYangs()
  {
    return hexagram.getYinYangs();
  }
  
  @Override
  public String getBinaryCode()
  {
    return hexagram.getBinaryCode();
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((hexagram == null) ? 0 : hexagram.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BaseHexagram other = (BaseHexagram) obj;
    if (hexagram == null)
    {
      if (other.hexagram != null)
        return false;
    }
    else if (!hexagram.equals(other.hexagram))
      return false;
    return true;
  }

  /** 從 "010101" 取得一個卦 */
  public static HexagramIF getFromBinaryString(String code)
  {
    if (code == null || code.length() < 6)
      return Hexagram.乾;
    
    boolean[] bools = new boolean[6];
    try
    {
      for(int i=0 ; i<6 ; i++)
      {
        char c = code.toCharArray()[i];
        if (c == '0')
          bools[i] = false;
        else
          bools[i] = true;
      }
      return Hexagram.getHexagram(bools);  
    }
    catch(Exception e)
    {
      return Hexagram.乾;
    }
  }

  @Override
  public Hexagram getHexagram(int... lines)
  {
    return hexagram.getHexagram(lines);
  }

}
