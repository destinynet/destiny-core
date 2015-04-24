/**
 * @author smallufo 
 * Created on 2008/2/20 at 上午 12:35:35
 */ 
package destiny.utils;

public class AlignUtil
{
  /**
   * 將 int 轉成 String , 前面塞入適當的 fill 字元，使其寬度變為 w ,
   * 如果 int 比 w 長，則從最前面摘掉字元
   */
  public static String alignRight(int value , int width , char fill)
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append(String.valueOf(Math.abs(value)));
    int valueLength;
    if (value < 0)
      sb.insert(0, "-");
    valueLength = sb.length();
    
    if (valueLength == width)
      return sb.toString();
    else if (valueLength < width)
    {
      int whiteSpaces =  width-valueLength ;
      
      for (int i=0 ; i < whiteSpaces ; i++)
      {
        sb.insert(0, fill);
      }
      return sb.toString();
    }
    else
    {
      //sb.length() > w
      return sb.substring(valueLength-width);
    }
  } //alignRight

  /** 
   * 將 double 轉成 String , 前面塞入適當的 fill 字元，使其寬度變為 w
   * 如果 double 比 w 長，則從最「後面」摘掉字元 (因為這是小數點)
   */
  public static String alignRight(double value , int width , char fill)
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append(String.valueOf(Math.abs(value)));
    int valueLength;
    if (value < 0)
      sb.insert(0, "-");
    valueLength = sb.length();
    
    if (valueLength == width)
      return sb.toString();
    else if (valueLength < width)
    {
      int whiteSpaces =  width-valueLength ;
      
      for (int i=0 ; i < whiteSpaces ; i++)
      {
        sb.insert(0, fill);
      }
      return sb.toString();
    }
    else
    {
      //sb.length() > w
      return sb.substring(0 , width);
    }
  }
}
