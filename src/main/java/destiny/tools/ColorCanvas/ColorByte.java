/*
 * 彩色的一個字 (byte)
 * 包含 字體、Color、URL 等資訊
 * @author smallufo
 * @date 2004/8/13
 * @time 下午 06:08:03
 */
package destiny.tools.ColorCanvas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;


class ColorByte implements Serializable {
  private byte b;

  private Optional<String> foreColor = Optional.empty(); //前景色

  private Optional<String> backColor = Optional.empty(); //背景色

  private Optional<Font> font = Optional.empty();

  @Nullable
  private String url = null;

  private Optional<String> title = Optional.empty();
  
  /**
   * @return Returns the backColor.
   */
  public Optional<String> getBackColor()
  {
    return backColor;
  }
    
  public void setBackColor(Optional<String> color)
  {
    this.backColor = this.validateColor(color);
  }
  
  /**
   * @return Returns the font.
   */
  public Optional<Font> getFont()
  {
    return font;
  }
  
  /**
   * @return Returns the foreColor.
   */
  public Optional<String> getForeColor()
  {
    return foreColor;
  }
  
  /**
   * @return Returns the url.
   */
  public Optional<String> getUrl()
  {
    return Optional.ofNullable(url);
  }
  
  /**
   * @return Returns the title
   */
  public Optional<String> getTitle()
  {
    return title;
  }

  public ColorByte() {}
  
  public ColorByte(byte b , Optional<String> foreColor , Optional<String> backColor
    , Optional<Font> font , @Nullable String url , Optional<String> title)
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
  private Optional<String> validateColor(Optional<String> color)
  {
    if (color.isPresent() &&
        (
            (color.get().getBytes().length == 3 || color.get().getBytes().length == 6 ) ||
            (color.get().startsWith("#") && (color.get().getBytes().length == 4 || color.get().getBytes().length == 7))
        )
       )
    {
      byte[] colorBytes = color.get().getBytes();
      boolean isRGB = true;
      int start = color.get().startsWith("#") ? 1 : 0;
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
        color = Optional.of(color.get().toUpperCase());
        if (start == 0)
          color = Optional.of("#".concat(color.get()));
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
    //System.out.println("font = " + font + " , cb.font = " + cb.font);
    //url 的 equals 要 resolve domain name , 改以 url.toExternalForm() 來比對
    if (
        ( ( (!this.foreColor.isPresent()) && (!cb.foreColor.isPresent())) || ( (this.foreColor.isPresent() ) && this.foreColor.equals(cb.foreColor)) )
          && ( ( (!this.backColor.isPresent()) && (!cb.backColor.isPresent())) || ( (this.backColor.isPresent() ) && this.backColor.orElse(null).equals(cb.backColor.orElse(null))) )
          && ( ( (!this.font.isPresent()     ) && (!cb.font.isPresent())     ) || ( (this.font.isPresent()      ) && this.font.equals(cb.font)          ) )
          && (Objects.equals(this.url , cb.url))
          //&& ( ( (!this.url.isPresent()      ) && (!cb.url.isPresent())      ) || ( (this.url.isPresent()       ) && (cb.url.isPresent() ) && this.url.get().toString().equals(cb.url.get().toExternalForm())) )
          && ( ( (!this.title.isPresent()    ) && (!cb.title.isPresent()     ) || ( (this.title.isPresent()     ) && this.title.get().equals(cb.title.get()))) )
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
