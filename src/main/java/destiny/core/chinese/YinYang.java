/**
 * @author smallufo
 * Created on 2002/9/24 at 上午 02:07:55
 * 2007/03/10 改為 class
 */
package destiny.core.chinese;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

@Deprecated
public enum YinYang implements YinYangIF
{
  陽(true , '陽'),
  陰(false, '陰');
  
  private boolean booleanValue;
  private char    c;
  
  
  private YinYang(boolean booleanValue , char c)
  {
    this.booleanValue = booleanValue;
    this.c     = c;
  }

  @NotNull
  public static YinYang getYinYang(boolean value)
  {
    if (value)
      return 陽;
    else
      return 陰;
  }
  
  @NotNull
  public static YinYang getYinYang(char c)
  {
    return (c =='0' || c == '陰') ? 陰 : 陽;
  }
  
  /** 若陰，則傳回陽 ; 若陽，則傳回陰 */
  @NotNull
  public YinYang getOpposite()
  {
    if (this == 陽)
      return 陰;
    else 
      return 陽;
  }
  
  /** 亂數取得 陰或陽 */
  @NotNull
  public static YinYang getRandomValue()
  {
    Random random = new Random();
    if (random.nextInt(2)==0)
      return 陰;
    else
      return 陽;
  }

  @Override
  public boolean getBooleanValue()
  {
    return this.booleanValue;
  }
  
  public char charValue()
  {
    return this.c;
  }
  
  @NotNull
  @Override
  public String toString()
  {
    return String.valueOf(c);
  }
  
}
