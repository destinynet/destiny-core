/**
 * @author smallufo 
 * Created on 2007/3/23 at 下午 11:21:25
 */ 
package destiny.utils.ColorCanvas;

import org.jetbrains.annotations.NotNull;

/**
 * 對齊的工具箱, 如果小於 1 , 則塞「前」
 */
public class AlignUtil
{
  /**
   * 將 int 轉成 String , 前後 塞入適當的空白字元，使其寬度變為 width
   * 如果 int 比 width 長，則從最前面摘掉字元
   */
  public static String alignCenter(int value , int width)
  {
    StringBuffer sb = new StringBuffer();
    if (value < 0) 
      sb.append("前");
    
    sb.append(String.valueOf(Math.abs(value)));
    int valueLength;
    if (value > 0)
      valueLength = sb.length();
    else
      valueLength = sb.length() + 1; // 加上一個「前」 的 2-bytes
     
    if (valueLength == width)
      return sb.toString();
    else if (valueLength < width)
    {
      int leftSpaces ; //左邊的空格
      int rightSpaces; //右邊的空格
      if (valueLength % 2 ==0)
      {
        //長度是偶數
        if (width % 2 == 0)
        {
          //寬度是偶數
          //左右空格同寬
          leftSpaces = (width - valueLength)/2;
          rightSpaces = leftSpaces;
        }
        else
        {
          //寬度是奇數 
          leftSpaces = (width - valueLength)/2+1 ;
          rightSpaces = leftSpaces -1;
        }
      }
      else
      {
        //長度是奇數
        if (width % 2 == 0)
        {
          //寬度是偶數 
          leftSpaces = (width - valueLength)/2 ;
          rightSpaces = leftSpaces +1;
        }
        else
        {
          //寬度是奇數
          //左右空格同寬
          leftSpaces = (width - valueLength)/2;
          rightSpaces = leftSpaces;
        }
      }
      
      sb.insert(0,generateDoubleSpace(leftSpaces));
      sb.append(generateDoubleSpace(rightSpaces));
      
      return sb.toString();
    }
    else
    {
      //sb.length() > width
      return sb.substring(valueLength-width);
    }
  } //alignCenter
  
  /**
   * 將 int 轉成 String , 前面塞入適當的空白字元，使其寬度變為 width
   * 如果 int 比 width 長，則從最前面摘掉字元
   */
  public static String alignRight(int value , int width)
  {
    StringBuffer sb = new StringBuffer();
    if (value < 0) 
      sb.append("前");
    
    sb.append(String.valueOf(Math.abs(value)));
    int valueLength;
    if (value >= 0)
      valueLength = sb.length();
    else
      valueLength = sb.length() + 1; // 加上一個「前」 的 2-bytes
     
    
    if (valueLength == width)
      return sb.toString();
    else if (valueLength < width)
    {
      int doubleByteSpaces =  ( width - valueLength ) /2;
      
      for (int i=0 ; i < doubleByteSpaces ; i++)
      {
        sb.insert(0, "　");
      }
      
      if ((width-valueLength) % 2 == 1)
        sb.insert(doubleByteSpaces , ' ');
      
      return sb.toString();
    }
    else
    {
      //sb.length() > width
      return sb.substring(valueLength-width);
    }
  } //alignRight
  
  
  /**
   * 產生全形空白
   */
  @NotNull
  private static String generateDoubleSpace(int length)
  {
    StringBuffer sb = new StringBuffer();
    int doubleByteSpaces = length / 2;
    for (int i=0 ; i < doubleByteSpaces ; i++)
    {
      sb.insert(0, "　");
    }
    if (length % 2 == 1)
      sb.insert(doubleByteSpaces , ' ');
    
    return sb.toString();
  }
  
  

  
  /**
   * 將 double 轉成 String , 前面塞入適當的空白字元，使其寬度變為 width
   * 如果 double 比 width 長，則從最後面摘掉字元
   */
  public static String alignRight(double value , int width)
  {
    StringBuffer sb = new StringBuffer(String.valueOf(value));
    int valueLength = sb.length();
    
    if (valueLength == 4)
      return sb.toString();
    else if (valueLength < 4)
    {
      int doubleByteSpaces =  ( 4 - valueLength ) /2;
      
      for (int i=0 ; i < doubleByteSpaces ; i++)
      {
        sb.insert(0, "　");
      }
      
      if ((4 -valueLength) % 2 == 1)
        sb.insert(doubleByteSpaces , ' ');
      
      return sb.toString();
    }
    else
    {
      //sb.length() > width
      return sb.substring(0, 4);
    }
  }
  

}
