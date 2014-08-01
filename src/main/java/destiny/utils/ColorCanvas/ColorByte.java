/*
 * 彩色的一個字 (byte)
 * 包含 字體、Color、URL 等資訊
 * @author smallufo
 * @date 2004/8/13
 * @time 下午 06:08:03
 */
package destiny.utils.ColorCanvas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Font;
import java.io.Serializable;
import java.net.URL;


class ColorByte implements Serializable
{
  private byte b;

  @Nullable
  private String foreColor; //前景色

  @Nullable
  private String backColor; //背景色

  private Font font;
  private URL url;
  private String title;
  
  /**
   * @return Returns the backColor.
   */
  @Nullable
  public String getBackColor()
  {
    return backColor;
  }
    
  public void setBackColor(String color)
  {
    this.backColor = this.validateColor(color);
  }
  
  /**
   * @return Returns the font.
   */
  public Font getFont()
  {
    return font;
  }
  
  /**
   * @return Returns the foreColor.
   */
  @Nullable
  public String getForeColor()
  {
    return foreColor;
  }
  
  /**
   * @return Returns the url.
   */
  public URL getUrl()
  {
    return url;
  }
  
  /**
   * @return Returns the title
   */
  public String getTitle()
  {
    return title;
  }

  public ColorByte() {}
  
  public ColorByte(byte b , String foreColor , String backColor , Font font , URL url , String title)
  {
    this.b = b;
    this.foreColor = validateColor(foreColor);
    this.backColor = validateColor(backColor);
    this.font = font;
    this.url = url;
    this.title = title;
  }
  
  /**
   * 檢查 color 字串，將其轉成正確的表示語法
   * 檢查 color 的每個 byte 是否介於 0~9 , A~F , a~f 之間
   * 0 ~ 9 = (ASCII) 48 ~ 57
   * A ~ F = (ASCII) 65 ~ 70
   * a ~ f = (ASCII) 97 ~ 102
   * 如果檢查正確的話，代表使用者所填的 color 是 "000000" 到 "FFFFFF" 之間（或是 3-byte 的表示法 000 到 FFF ） , 
   * 但是沒有加上 "#" , 所以會在前面加上 "#"
   * @param color
   * @return
   */
  @Nullable
  private String validateColor(@Nullable String color)
  {
    if (color != null &&  
        (
            (color.getBytes().length == 3 || color.getBytes().length == 6 ) ||
            (color.startsWith("#") && (color.getBytes().length == 4 || color.getBytes().length == 7))
        )
       )
    {
      byte[] colorBytes = color.getBytes();
      boolean isRGB = true;
      int start = color.startsWith("#") ? 1 : 0;
      for ( int i = start ; i < colorBytes.length ; i++)
      {
        if (!(  ( (colorBytes[i] >= 48 ) && ( colorBytes[i] <= 57  ) )
             || ( (colorBytes[i] >= 65 ) && ( colorBytes[i] <= 70  ) ) 
             || ( (colorBytes[i] >= 97 ) && ( colorBytes[i] <= 102 ) )  ))
        {
          isRGB = false;
          break;
        }
      }//loop 6 bytes
      if (isRGB)
      {
        color = color.toUpperCase();
        if (start == 0)
          color = "#".concat(color);  
      }        
    }        
    return color;
  }//validateColor
  
  public ColorByte(char bgChar)
  {
    this.b = (byte) bgChar;
  }
  
  public ColorByte(byte b)
  {
    this.b = b;
  }
  
  public byte getByte()
  {
    return this.b;
  }
  
  /**
   * 檢查這個 ColoyByte 是否與另一個 ColorByte 除了 byte 不同外，其他都相同
   * @param cb
   * @return
   */
  public boolean isSameProperties(@NotNull ColorByte cb)
  {
    //url 的 equals 要 resolve domain name , 改以 url.toExternalForm() 來比對
    if (
        ( ( (this.foreColor == null) && (cb.foreColor == null)) || ( (this.foreColor != null ) && this.foreColor.equals(cb.foreColor)) ) &&
        ( ( (this.backColor == null) && (cb.backColor == null)) || ( (this.backColor != null ) && this.backColor.equals(cb.backColor)) ) && 
        ( ( (this.font      == null) && (cb.font      == null)) || ( (this.font      != null ) && this.font.equals(cb.font)          ) ) && 
        ( ( (this.url       == null) && (cb.url       == null)) || ( (this.url       != null ) && (cb.url       != null ) && this.url.toString().equals(cb.url.toExternalForm())            ) ) &&
        ( ( (this.title     == null) && (cb.title     == null)) || ( (this.title     != null ) && this.title.equals(cb.title)        ) ) 
       )
      return true;
    else
      return false;
  }

  @NotNull
  @Override
  public String toString()
  {
    return "ColorByte [b=" + b + "]";
  }

}//Class
