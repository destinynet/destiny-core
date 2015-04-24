/**
 * @author smallufo 
 * Created on 2007/3/19 at 下午 2:14:34
 */ 
package destiny.core.calendar;

import java.io.Serializable;

import destiny.utils.Decorator;
import org.jetbrains.annotations.NotNull;

/**
 * 簡單的中文輸出 , 總共輸出 38位元 <BR/>
 * <pre>
 *西元　2000年01月01日　00時00分 00.00秒
 *西元前2000年12月31日　23時59分 59.99秒
 * </pre>
 */
public class TimeDecoratorChinese implements Decorator<Time> , Serializable
{
  public TimeDecoratorChinese()
  {
  }
  
  @NotNull
  public String getOutputString(@NotNull Time time)
  {
    StringBuffer sb = new StringBuffer();
    sb.append("西元");
    if (!time.isAd())
      sb.append("前" );
    else
      sb.append("　");
    sb.append(alignRight(time.getYear(), 4)).append("年");
    sb.append(time.getMonth() < 10 ? "0" : "").append(time.getMonth()).append("月");
    sb.append(time.getDay() < 10 ? "0" : "").append(time.getDay()).append("日");
    sb.append("　");
    sb.append(time.getHour() < 10 ? "0" : "").append(time.getHour()).append("時");
    sb.append(time.getMinute() < 10 ? "0" : "").append(time.getMinute()).append("分");
    if (time.getSecond() - (int)time.getSecond() ==0)
    {
      //整數
      if (time.getSecond() < 10)
        sb.append(" 0").append(String.valueOf(time.getSecond()).substring(0, 1)).append(".00");
      else
        sb.append(" ").append(String.valueOf(time.getSecond()).substring(0, 2)).append(".00");
    }
    else
    {
      //有小數
      if (time.getSecond() < 10)
      {
        if (String.valueOf(time.getSecond()).length() >= 4)
          sb.append(" 0").append(String.valueOf(time.getSecond()).substring(0, 4));
        else
          sb.append(" 0").append(String.valueOf(time.getSecond())).append("0"); //長度一定等於3
      }
      else
      {
        if (String.valueOf(time.getSecond()).length() >= 5)
          sb.append(" ").append(String.valueOf(time.getSecond()).substring(0, 5));
        else
          sb.append(" ").append(String.valueOf(time.getSecond())).append("0"); //長度一定等於4
      }
    }
    sb.append("秒");
    return sb.toString();
  }
  
  public static String alignRight(int value , int width)
  {
    StringBuffer sb = new StringBuffer(String.valueOf(value));
    int valueLength = sb.length();
    
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
      //sb.length() > w
      return sb.substring(valueLength-width);
    }
  }

}
