/**
 * @author smallufo
 * Created on 2010/6/24 at 上午4:38:39
 */
package destiny.iching;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

/** 因為 enum Hexagram 無法被繼承 , 所以才做出這個中介/adapter class , 提供給其他 class 繼承
 * FIXME :  2018-05-11 考慮此 class 之存廢 */
public class BaseHexagram implements IHexagram, Serializable {

  private Hexagram hexagram;
  
  public BaseHexagram(Hexagram hexagram)
  {
    this.hexagram = hexagram;
  }
  
  public BaseHexagram(@NotNull boolean[] yinyangs)
  {
    if(yinyangs.length != 6)
      throw new RuntimeException("BaseHexagram yinyangs length not equal 6!");
    this.hexagram = Hexagram.Companion.getHexagram(yinyangs);
  }

  @Override
  public boolean getLine(int index)
  {
    return hexagram.getLine(index);
  }

  @NotNull
  @Override
  public Symbol getLowerSymbol()
  {
    return hexagram.getLowerSymbol();
  }

  @NotNull
  @Override
  public Symbol getUpperSymbol()
  {
    return hexagram.getUpperSymbol();
  }

  @NotNull
  @Override
  public List<Boolean> getYinYangs() {
    return hexagram.getYinYangs();
  }

  @NotNull
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
  public boolean equals(@Nullable Object obj)
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
      return other.hexagram == null;
    }
    else
      return hexagram.equals(other.hexagram);
  }

  /** 從 "010101" 取得一個卦 */
  @NotNull
  public static IHexagram getFromBinaryString(@Nullable String code)
  {
    if (code == null || code.length() < 6)
      return Hexagram.乾;
    
    boolean[] bools = new boolean[6];
    try
    {
      for(int i=0 ; i<6 ; i++)
      {
        char c = code.toCharArray()[i];
        bools[i] = (c != '0');
      }
      return Hexagram.Companion.getHexagram(bools);
    }
    catch(Exception e)
    {
      return Hexagram.乾;
    }
  }

  @NotNull
  @Override
  public IHexagram getHexagram(@NotNull int... lines)
  {
    return hexagram.getHexagram(lines);
  }

  @NotNull
  @Override
  public IHexagram getMiddleSpanHexagram() {
    // TODO
    return null;
  }

  @NotNull
  @Override
  public IHexagram getInterlacedHexagram() {
    // TODO
    return null;
  }

  @NotNull
  @Override
  public IHexagram getReversedHexagram() {
    // TODO
    return null;
  }
}
