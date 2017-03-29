/**
 * @author smallufo
 * @date 2004/8/13
 * @time 下午 06:01:39
 */
package destiny.tools.ColorCanvas;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 具備彩色/字型的 Canvas , 能夠輸出成 HTML , 座標系統為 1-based，如下： <BR>
 * <BR> x1y1 x1y2 x1y3  
 * <BR> x2y1 x2y2 x2y3 
 * <BR> x3y1 x3y2 x3y3 
 * <BR>
 */
public class ColorCanvas implements Serializable
{
  private Optional<ColorCanvas> parent = Optional.empty();
  
  private int height; // x
  private int width;  // y
  //private boolean extensible; //是否可以拉長：即 x 軸 (row) 是否可以自動增加 , 對於 addLine() 很好用
  
  @NotNull
  private ColorByte[] content;
  
  @NotNull
  private List<Child> children = new ArrayList<>();
  
  
  /**
   * 產生新的畫布，內定背景以空白鍵塗滿
   * @param height  高度
   * @param width   寬度
   */
  public ColorCanvas(int height , int width)
  {
    this.height = height;
    this.width = width;
    content = new ColorByte[width*height];
    for (int i=0 ; i < content.length ; i++)
    {
      content[i] = new ColorByte(' ');
    }    
  }
  
  
  /**
   * 產生新的畫布，內定以 bgChar 字元填滿整個畫面
   * @param height  高度
   * @param width   寬度
   * @param bgChar  內定以 bgChar 字元填滿整個畫面
   */
  public ColorCanvas(int height , int width , char bgChar)
  {
    this.height = height;
    this.width = width;
    
    content = new ColorByte[width*height];
    for (int i=0 ; i < content.length ; i++)
      content[i] = new ColorByte(bgChar);
  }//constructor
  
  /**
   * 產生新的畫布，內定以 fill ColorByte 填滿整個畫面
   * @param h
   * @param w
   * @param fill
   * @param extensible 是否可以 自動拉長：即 x 軸 (row) 是否可以自動增加 , 對於 addLine() 很好用
   */
  /* @Deprecated
  public ColorCanvas(int h , int w , ColorByte fill, boolean extensible)
  {
    this.h = h;
    this.w = w;
    this.extensible = extensible;
    
    content = new ColorByte[w*h];
    for (int i=0 ; i < content.length ; i++)
    {
      content[i] = fill;
    }
  }
  */
  
  /** 生新的畫布，以 fill 'String' 填滿整個畫面 ，不指定前景背景顏色 */
  public ColorCanvas(int height , int width , @NotNull String fill )
  {
    this.height = height;
    this.width = width;
    content = new ColorByte[width*height];
    
    this.fillContent(fill , Optional.empty() , Optional.empty());
  }
  
  /** 生新的畫布，以 fill 'String' 填滿整個畫面 ，指定前景及背景顏色 */
  public ColorCanvas(int height , int width , @NotNull String fill , Optional<String> foreColor , Optional<String> backColor)
  {
    this.height = height;
    this.width = width;
    content = new ColorByte[width*height];
    
    this.fillContent(fill , foreColor , backColor);
    
  }
  
  private void fillContent(@NotNull String fill , Optional<String> foreColor , Optional<String> backColor)
  {
    char[] strChars = fill.toCharArray();
    
    int totalBytesLength = 0; // fill 的 byte 長度
    for (char strChar : strChars) {
      byte[] byteArray = null;
      try {
        byteArray = String.valueOf(strChar).getBytes("Big5");
      } catch (UnsupportedEncodingException ignored) {
      }
      if (byteArray != null) {
        totalBytesLength += byteArray.length;
      }
    }
    
    // 將 fill 轉成 bytes array
    byte[] bytes = new byte[totalBytesLength]; 
    int index=0;
    for (char strChar : strChars) {
      byte[] byteArray = null;
      try {
        byteArray = String.valueOf(strChar).getBytes("Big5");
      } catch (UnsupportedEncodingException ignored) {
      }
      if (byteArray != null) {
        System.arraycopy(byteArray, 0, bytes, index, byteArray.length);
        index = index + byteArray.length;
      }
    }

    for (int i=1 ; i <= height ; i++)
    {
      for (int j=1 ; j <= width ; j++)
      {
        index = (i-1)*width+ j-1;
        content[index] = new ColorByte( bytes[(j-1)%bytes.length], foreColor , backColor, Optional.empty() , null , Optional.empty());
      }
    }
  }
  
  /**
   * 在第 x row , 第 y column , 開始，寫入 Text 純文字 , 內定不換行（字會被切掉）
   * @param str
   * @param x
   * @param y
   */
  public void setText(@NotNull String str , int x , int y )
  {
    this.setText(str , x , y
      , Optional.empty()
      , Optional.empty()
      , Optional.empty()
      , null
      , Optional.empty()
      , false);
  }
  
  /**
   * 在第 x row , 第 y column , 開始，寫入 Text 純文字
   * @param str
   * @param x
   * @param y
   * @wrap 是否換行 , 如果不換行，後面的字會被切掉
   */
  public void setText(@NotNull String str , int x , int y , boolean wrap)
  {
    this.setText(str , x , y , Optional.empty() , Optional.empty() , Optional.empty() , null , Optional.empty() , wrap);
  }//setText
  

  /**
   * 在第 x row , 第 y column , 開始，寫入 Text 純文字 , 有設定前景色
   */
  public void setText(@NotNull String str , int x , int y , @NotNull String foreColor)
  {
    this.setText(str , x , y
      , Optional.of(foreColor)
      , Optional.empty()
      , Optional.empty()
      , null
      , Optional.empty()
      , false);
  }

  /**
   * 在第 x row , 第 y column , 開始，寫入 Text , 有設定前景色，背景色，以及 title
   */
  public void setText(@NotNull String str , int x , int y ,
                      @NotNull String foreColor , @Nullable String backColor , @Nullable String title) {
    this.setText(str , x , y
      , Optional.of(foreColor)
      , Optional.ofNullable(backColor)
      , Optional.empty()
      , null
      , Optional.ofNullable(title)
      , false);
  }


  public void setText(@NotNull String str , int x , int y , @Nullable String foreColor , @Nullable String backColor ,
                      @Nullable Font font , @Nullable URL url , @Nullable String title , boolean wrap) {
    setText(str , x , y
      , Optional.ofNullable(foreColor)
      , Optional.ofNullable(backColor)
      , Optional.ofNullable(font)
      , url == null ? null : url.toExternalForm()
      , Optional.ofNullable(title) , wrap);
  }

  /**
   * 在第 x row , 第 y column , 開始，寫入 彩色文字
   * @param str	       要輸入的字串  
   * @param x         row index
   * @param y         column index
   * @param foreColor 前景顏色字串，以 16 進位表示，例如 "FFFFCC"
   * @param backColor 背景顏色字串，以 16 進位表示，例如 "FFFFCC"
   * @param font      字型，例如： new Font("細明體" , Font.PLAIN , 16)
   * @param url       網址物件 , 例如 "http://www.google.com.tw"
   * @param title     Title
   * @param wrap      是否換行
   */
  public void setText(@NotNull String str , int x , int y , Optional<String> foreColor , Optional<String> backColor
    ,  Optional<Font> font , @Nullable String url , Optional<String> title , boolean wrap)
  {    
    int index = (x-1) * width + (y-1);
    int strWidth=0;
    try
    {
      //以 big5 編碼取出 bytes , 一個中文字佔兩個 bytes , 剛好就是英文字母的兩倍 , 可以拿來當作字元寬度
      //byte[] bytes = str.getBytes("Big5");      
      strWidth = str.getBytes("Big5").length;
    }
    catch (UnsupportedEncodingException ignored)
    {}
    if (wrap)
    {
      //如果要換行 , 就檢查是否太長
      //檢查是否會太長 , 如果太長，丟出錯誤訊息
      if (index+strWidth-1 >= this.content.length)
        throw new RuntimeException("setText() 超出 Canvas 長度");
      else
      {
        //TODO : 太長，處理換行問題
      }
    }
    else
    {
      //如果不換行（切字）
      //int a = y+strWidth-1;
      //System.out.println("a = " + a + " , w = " + w);
      if ( (y+strWidth-1) > (this.width))
      {
        //超出，切字
        //byte[] byteArray = str.getBytes();        
        //str = new String(byteArray , 0 , (this.w-y+1));
        str = getStringFromBytes(str , (this.width-y+1));
      }
    }
    
    
    //處理中文字切字的問題 , 判斷每個 char 「在 Big5 編碼下」佔用多少 bytes 的空間
    //因為如果在 UTF8 的環境下，每個中文字就佔 3-bytes , 這不是我要的
    char[] strChars = str.toCharArray(); //取得傳入字串的 char array

    for (char strChar : strChars) {
      byte[] bytes = null;
      try {
        bytes = String.valueOf(strChar).getBytes("Big5");
      } catch (UnsupportedEncodingException ignored) {
      }

      int indexLine = ((index / this.width)) + 1;
      int precursorLine = 0;

      if (bytes != null) {
        if ((index + bytes.length) % this.width == 0) //剛好到行尾
        {
          precursorLine = indexLine;
        }
        else {
          precursorLine = (((index + bytes.length) / this.width)) + 1;
        }
      }

      if (indexLine != precursorLine) {
        //代表有切行的可能，必須把這個 Char 移到下一行，而目前這行的結尾，以空白鍵填入
        //先求出，目前 index 到行尾，要塞入幾個空白鍵（中文字為 1 個）
        int spaces = width - index % this.width;
        for (int j = index; j < index + spaces; j++) {
          content[j] = new ColorByte((byte) ' ', foreColor, backColor, font, url, title);
        }//填空白
        index = index + spaces;
      }
      for (int j = index; j < index + bytes.length; j++) {
        //如果新加入的背景色為空，檢查原字元的背景色
        if (!backColor.isPresent()) {
          if (content[j].getBackColor() != null) {
            backColor = content[j].getBackColor();
          }
        }
        //如果新加入的前景色為空，檢查原字元的前景色
        if (!foreColor.isPresent()) {
          if (content[j].getForeColor() != null) {
            foreColor = content[j].getForeColor();
          }
        }
        //如果新加入的字型為空，則檢查原字元的字型
        if (!font.isPresent()) {
          if (content[j].getFont() != null) {
            font = content[j].getFont();
          }
        }
        //如果新加入的 URL 為空，則檢查原字元的網址
        if (url == null) {
          if (content[j].getUrl() != null) {
            url = content[j].getUrl().orElse(null);
          }
        }

        content[j] = new ColorByte(bytes[j - index], foreColor, backColor, font, url, title);
      }
      index = index + bytes.length;
      if (index >= content.length) {
        break;
      }
    }
  }//setText()


  /**
   * <pre>
   * 2010/08/13 Added :
   * 要從 str 中製造出 寬度 為 w 的字串
   * 假設 str = "一二三四五aabbcc" , 強迫寬度為 10 , 就要取前五個字
   * 原始字串的 bytearray 為：
   * [-28, -72, -128, -28, -70, -116, -28, -72, -119, -27, -101, -101, -28, -70, -108, 97, 97, 98, 98, 99, 99]
   *  (    一      ）（     二      ）（    三      ）（    四       ）（    五     ） a   a   b   b   c   c
   *  舊的演算法 :
   *  byte[] byteArray = str.getBytes();
   *  str = new String(byteArray , 0 , (this.w-y+1));
   *  如果按照此演算法，只取前 10 bytes , 則只會取到「四」的第一個 byte (-27) , 並不完整！
   *  必須想辦法，讓他取到「五」的第三個 byte (-108)
   *
   *  新的演算法：把 str 拆成 char[] , 一個一個累加
   *  </pre>
   */
  @NotNull
  private String getStringFromBytes(@NotNull String str , int width)
  {
    char[] chars = str.toCharArray();
    int nowWidth=0;
    StringBuffer sb = new StringBuffer();
    for (char aChar : chars) {
      Character c = aChar;
      String s = c.toString();
      if (s.getBytes().length == 3) {
        //中文字

        //如果加上字元所佔寬度(2)大於 w , 就不 append 了
        if (nowWidth + 2 > width) {
          break;
        }
        sb = sb.append(s);
        //雖然 bytes 長度是 3 , 但是寬度只佔 2
        nowWidth += 2;
      }
      else {
        if (nowWidth + 1 > width) {
          break;
        }
        sb = sb.append(s);
        nowWidth++;
      }
    }
    return sb.toString();
  }
  
  public int getWidth() { return this.width;}
  
  int getHeight() { return this.height; }
  
  void setParent(@NotNull ColorCanvas cc)
  {
    this.parent = Optional.of(cc);
  }
  
  private Optional<ColorCanvas> getParent()
  {
    return this.parent;
  }
  
  /**
   * 將某個子 ColorCanvas 加到這個畫布中
   * @param cc
   * @param x row index , 1-based
   * @param y column index , 1-based
   */
  public void add(@NotNull ColorCanvas cc , int x , int y)
  {
    if (cc.getWidth() > this.width || cc.getHeight() > this.height)
      throw new RuntimeException("不能把大的 Canvas 塞入小的 Canvas 中 ! 父 Canvas 寬 = " + this.width + " , 高 = " + this.height + " ; 子 Canvas 寬 = " + cc.getWidth() + " , 高 = " + cc.getHeight());    
    if ( (y+cc.getWidth()-1) >= (this.width+1) )
      throw new RuntimeException("寬度超過 ! 父 Canvas 寬度為 " + this.width  + " , 起始加入 y 座標 = " + y + " , 子 Canvas 寬度為 " + cc.getWidth()  + " , 超出！");    
    if (x+cc.getHeight()-1 > this.getHeight())
      throw new RuntimeException("高度超過 ! 父 Canvas 高度為 " + this.height + " , 起始加入 x 座標 = " + x + " , 子 Canvas 高度為 " + cc.getHeight() + " , 超出！");
    
    cc.setParent(this);
    children.add(new Child(cc , x , y));
    
    //這個步驟，代表把 Child ColorCanvas 的內容，貼到自己的 content 中，取代自己的 content
    this.content = this.getContentWithChildren();
  }
  
  /**
   * 新增一行字，到本 Canvas 「有字的最底端」
   */
  public void addLine(@NotNull String str , @Nullable String foreColor , @Nullable String backColor ,
                      @Nullable Font font , @Nullable URL url , boolean wrap)
  {
    /*
     * 必須先取出來，第幾行開始為空
     * 演算法：由底層數上來，遇到有字，則停止
     */
    ColorByte[] cbs = this.getContentWithChildren(); 
    int targetLine = this.height+1;
    for (int i= this.height ; i>=1 ;  i--)
    {
      int firstByte = (i-1) * this.width ;
      int lastByte  = i * width -1;
      ColorByte firstCB = cbs[firstByte];
      
      boolean foundDifference = false;
      for (int j = firstByte+1 ; j <= lastByte ; j++)
      {
        ColorByte cb = cbs[j];
        //走訪此行每個 ColorByte 是否一樣，如果一樣，代表此行為空行
        //FIXME : 這無法處理「全形中文空白背景」，因為一個全形中文被拆成兩個 ColorByte , 其 properties 一定不一樣！
        if (!(firstCB.isSameProperties(cb) && firstCB.getByte()==cb.getByte()))
        {
          targetLine = i+1; //此行(row)找到不一樣的字元了，所以要加入 Line，得從下一行開始
          foundDifference = true;
          break;
        }
      }//走訪此行每個 ColorByte
      if (foundDifference)
        break;
      targetLine = i;
    }
    //System.out.println("targetLine = " + targetLine);
    if ( targetLine > this.height)
      throw new RuntimeException("錯誤，欲新加入一行，但是最後一行已經有資料了，無法再往下加一行了");
        
    this.setText(str , targetLine , 1 , Optional.ofNullable(foreColor) , Optional.ofNullable(backColor)
      , Optional.ofNullable(font) , null , Optional.empty() , wrap);
  }//addLine
  
  /**
   * 附加一串字，到 content 的尾端「之後」，亦即，加高 content 
   */
  public void appendLine(@NotNull String str , @Nullable String foreColor , @Nullable String backColor , @NotNull String fill
    , @Nullable Font font , @Nullable URL url )
  {
    int strWidth=0;
    try
    {
      //以 big5 編碼取出 bytes , 一個中文字佔兩個 bytes , 剛好就是英文字母的兩倍 , 可以拿來當作字元寬度
      //byte[] bytes = str.getBytes("Big5");      
      strWidth = str.getBytes("Big5").length;
    }
    catch (UnsupportedEncodingException ignored)
    {}
    
    //ColorByte[] content 必須要加長 (高度, h 增加)
    
    //需要多少 Rows
    int additionalRows = strWidth / this.width +1;
    
    ColorCanvas appendedCanvas = new ColorCanvas(additionalRows , width , fill , Optional.ofNullable(foreColor) , Optional.ofNullable(backColor));
    appendedCanvas.addLine(str, foreColor, backColor, font, url, true);

    this.height += additionalRows;
    ColorByte[] newContent = new ColorByte[content.length + additionalRows*width];
    System.arraycopy(content, 0, newContent, 0, content.length);
    System.arraycopy(appendedCanvas.getContent() , 0 , newContent , content.length , appendedCanvas.getContent().length);
    content = newContent;
  }
  
  /** 附加一行「空行」到 content 尾端「之後」，亦即，加高 content */
  public void appendEmptyLine()
  {
    ColorCanvas appendedCanvas = new ColorCanvas(1 , width , " " , Optional.empty() , Optional.empty());
    this.height ++;
    ColorByte[] newContent = new ColorByte[content.length + width];
    System.arraycopy(content, 0, newContent, 0, content.length);
    System.arraycopy(appendedCanvas.getContent() , 0 , newContent , content.length , appendedCanvas.getContent().length);
    content = newContent;
  }
  
  /**
   * @return 讀取這個 ColorCanvas 的 content 資料
   */
  @NotNull
  private ColorByte[] getContent() {
    return this.content;
  }
  
  /**
   * 讀取這個 ColorCanvas 的 content 資料
   * 「以及」其子ColorCanvas 的 content 資料
   * 並且，將子 ColorCanvas 的 content 覆蓋過本 content
   */
  @NotNull
  private ColorByte[] getContentWithChildren()
  {
    if (this.children.size() == 0)
    {
      return this.content;
    }      
    else
    {
      //有 children      
      ColorByte[] result = null;
      for (Child child : children) {
        ColorByte[] childContent = child.getCanvasCanvas().getContent();
        for (int j = 0; j < childContent.length; j++) {
          //走訪childContent , 必須計算 childContent 每個 ColorByte 在 parent 的座標 , 並且複製過去
          int childX = (j / child.getCanvasCanvas().getWidth()) + 1;
          int childY = (j + 1) % child.getCanvasCanvas().getWidth();
          if (childY == 0) {
            childY = child.getCanvasCanvas().getWidth();
          }

          //檢查 '子' content 是否有背景色，如果背景色是 null , 則 '父'content 必須保留其背景色
          if (childContent[j].getBackColor().isPresent()) {
            this.content[(child.getX() + (childX - 1) - 1) * this.getWidth() + (child.getY() + (childY - 1)) - 1] = childContent[j];
          }
          else {
            Optional<String> tempBgColor = this.content[(child.getX() + (childX - 1) - 1) * this.getWidth() + (child.getY() + (childY - 1)) - 1].getBackColor();
            this.content[(child.getX() + (childX - 1) - 1) * this.getWidth() + (child.getY() + (childY - 1)) - 1] = childContent[j];
            this.content[(child.getX() + (childX - 1) - 1) * this.getWidth() + (child.getY() + (childY - 1)) - 1].setBackColor(tempBgColor);
          }

        }
        result = this.content;
      }//for children
      return result;
    }
  }
  
  /**
   * 最重大的工程，輸出 HTML 檔案
   * @return HTML 表示
   */
  @NotNull
  public String getHtmlOutput()
  {
    int index = 0;      //目前走到的索引    
    int precursor = 0;  //先鋒
    StringBuilder sb = new StringBuilder(); //存放目前的結果
    sb.append("<div style=\"");
    sb.append("white-space: pre; ");
    sb.append("\">");
    //sb.append("<pre>");
    /**
     * 之前做法：會在這裡塞一行
     * ColorByte[] content = this.getContent();
     * 但是，後來的做法
     * add(ColorCanvas cc , int x , int y) 底下已經呼叫了一次 this.content = this.getContent()
     * 也就是，add Child 之後，會自動 update Parent Canvas 的 content
     * 所以，這裡就不需要再去 getContent() 一次
     */
    
    List<ColorByte> list = new ArrayList<>();
    while (precursor < content.length )
    {      
      if (content[index].isSameProperties(content[precursor]))
      {
        //properties 一樣 , 檢查是否到達邊界
        if( (precursor +1) % this.width == 0 && (precursor != content.length-1))
        {
          //如果到達了邊界 , 強制段行
          list.add(content[precursor]);
          sb.append(buildHtml(list));
          sb.append("<br />");
          list.clear();
          precursor++;
          index = precursor;
          //list.add(content[index]);
        }
        else
        {
          // properties 一樣，還未到達邊界，直接放入
          list.add(content[precursor]);
          precursor++;
        }
      }        
      else
      {
        //發現不一樣的 ColorByte properties，開始讀取 list內容，將 list 內容讀出來，建造出一個 HTML String        
        sb.append(buildHtml(list));
        list.clear();        
        index = precursor;
        list.add(content[index]);
        
        //檢查是否到達邊界
        if( (index +1) % this.width == 0 && (index != content.length-1))
        {
          //如果到達邊界 , 則，將「一個」 ColorByte 送入製作成 HTML
          sb.append(buildHtml(list));
          sb.append("<br />");
          list.clear();
          index ++;
          precursor++;
          
        }
        else
        {
          //尚未到達邊界
          precursor++;
        }
        
      }//else
      
    }
    //把最後的 cursor 走完
    sb.append(buildHtml(list));
    list.clear();
    //sb.append("</pre>");
    sb.append("</div>");
    return sb.toString();
  }//getHtmlOutput
  
  
  @NotNull
  private String buildHtml(@NotNull List<ColorByte> list)
  {
    StringBuilder tempSb = new StringBuilder();
    byte[] byteArray = new byte[list.size()];
    for (int i=0 ; i < list.size() ; i++)
    {
      ColorByte cb = list.get(i); 
      byteArray[i] = cb.getByte();
    }//走訪每個 list 內的 ColorByte
    ColorByte cb = list.get(0);
    
    boolean hasUrl;
    hasUrl = cb.getUrl().isPresent();
 
    boolean hasFont;
    hasFont = (cb.getFont().isPresent()) || (cb.getForeColor().isPresent()) || (cb.getBackColor().isPresent()) || (cb.getTitle().isPresent());
    
    if (hasUrl && !hasFont) //只有網址
    {
      try
      {
        tempSb.append("<a href=\"").append(cb.getUrl().get()).append("\" target=\"_blank\">").append(new String(byteArray, "Big5")).append("</a>");
      }
      catch (UnsupportedEncodingException ignored)
      {
      }
    }
    else if (!hasUrl && hasFont) //只有字型
    {
      tempSb.append(buildFontHtml(cb, byteArray));
    }
    else if (hasUrl && hasFont) //有網址也有字型
    {
      tempSb.append("<a href=\"").append(cb.getUrl().get()).append("\" target=\"_blank\">");
      tempSb.append(buildFontHtml(cb, byteArray));
      tempSb.append("</a>");
    }
    else
    {
      //沒有網址，也沒有字型變化
      try
      {
        tempSb.append(new String(byteArray , "Big5"));
      }
      catch (UnsupportedEncodingException ignored)
      {
      }
    }
    return tempSb.toString();
  }
  
  @NotNull
  private StringBuffer buildFontHtml(@NotNull ColorByte cb, @NotNull byte[] byteArray) {
    StringBuffer sb = new StringBuffer();
    sb.append("<span ");
    sb.append("style=\"");
//    sb.append("white-space: pre; ");

    cb.getForeColor().ifPresent(foreColor -> sb.append("color:").append(foreColor).append("; "));
    cb.getBackColor().ifPresent( backColor -> sb.append("background-color:").append(backColor).append("; "));
    cb.getFont().ifPresent( font -> sb.append("font-family:").append(font.getFamily()).append("; "));

    sb.append("\"");
    
    //檢查是否有 title
    cb.getTitle().ifPresent( title -> {
      sb.append(" title=\"");
      sb.append(cb.getTitle().get());
      sb.append("\"");
    });

    sb.append(">");
    try {
      sb.append(new String(byteArray, "Big5"));
    } catch (UnsupportedEncodingException ignored) {
    }
    sb.append("</span>");
    return sb;
  }//buildFontHtml()
  
  
    
  /**
   * 省略所有 Color / Font / URL
   * 純粹輸出 byte 內容
   */  
  @NotNull
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    ColorByte[] cbs = this.getContentWithChildren(); 
    for (int i=1 ; i<=height ; i++)
    {
      byte[] byteArray = new byte[width];
      for (int j=0 ; j < width ; j++)
      {
        byteArray[j] = cbs[(i-1)*width+(j)].getByte();
      }
      String s = null;
      try
      {
        s = new String(byteArray, 0 , width , "Big5");
      }
      catch (UnsupportedEncodingException ignored)
      {}
      sb.append(s);
      sb.append('\n');
    }
    return sb.toString();
  }

}//class
