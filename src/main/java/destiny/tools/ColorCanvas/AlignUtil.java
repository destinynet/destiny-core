/**
 * @author smallufo
 * Created on 2007/3/23 at 下午 11:21:25
 */
package destiny.tools.ColorCanvas;


/**
 * 對齊的工具箱, 如果小於 1 , 則塞「前」
 */
public class AlignUtil {


  /**
   * 將 int 轉成 String , 前面塞入適當的空白字元，使其寬度變為 w
   * 如果 int 比 w 長，則從最前面摘掉字元
   */
  public static String alignRight(int value, int width) {
    StringBuffer sb = new StringBuffer();
    if (value < 0)
      sb.append("前");

    sb.append(String.valueOf(Math.abs(value)));
    int valueLength;
    if (value >= 0)
      valueLength = sb.length();
    else
      valueLength = sb.length() + 1; // 加上一個「前」 的 2-bytes

    return outputStringBuffer(valueLength, width, sb);

  } //alignRight

  public static String outputStringBuffer(int valueLength, int width, StringBuffer sb) {
    if (valueLength == width)
      return sb.toString();
    else if (valueLength < width) {
      int doubleByteSpaces = (width - valueLength) / 2;

      for (int i = 0; i < doubleByteSpaces; i++) {
        sb.insert(0, "　");
      }

      if ((width - valueLength) % 2 == 1)
        sb.insert(doubleByteSpaces, ' ');

      return sb.toString();
    }
    else {
      //sb.length() > w
      return sb.substring(valueLength - width);
    }
  }



  /**
   * 將 double 轉成 String , 前面塞入適當的空白字元，使其寬度變為 w
   * 如果 double 比 w 長，則從最後面摘掉字元
   */
  public static String alignRight(double value, int width) {
    StringBuilder sb = new StringBuilder(String.valueOf(value));
    int valueLength = sb.length();

    if (valueLength == 4)
      return sb.toString();
    else if (valueLength < 4) {
      int doubleByteSpaces = (4 - valueLength) / 2;

      for (int i = 0; i < doubleByteSpaces; i++) {
        sb.insert(0, "　");
      }

      if ((4 - valueLength) % 2 == 1)
        sb.insert(doubleByteSpaces, ' ');

      return sb.toString();
    }
    else {
      //sb.length() > w
      return sb.substring(0, 4);
    }
  }


}
