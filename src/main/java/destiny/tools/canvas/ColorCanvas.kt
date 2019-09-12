/**
 * @author smallufo
 * @date 2004/8/13
 * @time 下午 06:01:39
 */
package destiny.tools.canvas

import java.awt.Font
import java.io.Serializable
import java.net.URL
import java.nio.charset.Charset

/**
 * 具備彩色/字型的 Canvas , 能夠輸出成 HTML , 座標系統為 1-based，如下：
 * <pre>
 * x1y1 x1y2 x1y3
 * x2y1 x2y2 x2y3
 * x3y1 x3y2 x3y3
 * </pre>
 */
open class ColorCanvas : Serializable {

  var parent: ColorCanvas? = null
    internal set

  var height: Int = 0 // x

  val width: Int  // y

  /**
   * @return 讀取這個 ColorCanvas 的 content 資料
   */
  private var content: Array<ColorByte>

  private val children = mutableListOf<Child>()


  /** 輸出模式  */
  enum class OutputMode {
    HTML, TEXT
  }

  /**
   * 讀取這個 ColorCanvas 的 content 資料
   * 「以及」其子ColorCanvas 的 content 資料
   * 並且，將子 ColorCanvas 的 content 覆蓋過本 content
   */
  private val contentWithChildren: Array<ColorByte>
    get() {
      if (this.children.size == 0) {
        return this.content
      } else {
        //有 children

        //var result: Array<ColorByte>? = null
        for (child in children) {
          val childContent = child.canvasCanvas.content
          for (j in childContent.indices) {
            //走訪childContent , 必須計算 childContent 每個 ColorByte 在 parent 的座標 , 並且複製過去
            val childX = j / child.canvasCanvas.width + 1
            var childY = (j + 1) % child.canvasCanvas.width
            if (childY == 0
            ) {
              childY = child.canvasCanvas.width
            }
            //檢查 '子' content 是否有背景色，如果背景色是 null , 則 '父'content 必須保留其背景色
            if (childContent[j].getBackColor() != null
            ) {

              this.content[(child.x + (childX - 1) - 1) * this.width + (child.y + (childY - 1)) - 1] = childContent[j]
            } else {
              val tempBgColor =
                this.content[(child.x + (childX - 1) - 1) * this.width + (child.y + (childY - 1)) - 1].getBackColor()
              this.content[(child.x + (childX - 1) - 1) * this.width + (child.y + (childY - 1)) - 1] = childContent[j]
              this.content[(child.x + (childX - 1) - 1) * this.width + (child.y + (childY - 1)) - 1].setBackColor(tempBgColor)
            }

          }
          //result = this.content
        } //for children
        return this.content
      }
    }

  /**
   * 最重大的工程，輸出 HTML 檔案
   *
   * @return HTML 表示
   */
  val htmlOutput: String
    get() {
      //目前走到的索引
      var index = 0
      //先鋒
      var precursor = 0
      //存放目前的結果
      val sb = StringBuilder()
      sb.append("<div style=\"")
      sb.append("white-space: pre; ")
      sb.append("\">")

      /**
       * 之前做法：會在這裡塞一行
       * ColorByte[] content = this.getContent();
       * 但是，後來的做法
       * add(ColorCanvas cc , int x , int y) 底下已經呼叫了一次 this.content = this.getContent()
       * 也就是，add Child 之後，會自動 update Parent Canvas 的 content
       * 所以，這裡就不需要再去 getContent() 一次
       */

      val list = mutableListOf<ColorByte>()
      while (precursor < content.size) {
        if (content[index].isSameProperties(content[precursor])) {
          if ((precursor + 1) % this.width == 0 && precursor != content.size - 1) {
            //如果到達了邊界 , 強制段行
            list.add(content[precursor])
            sb.append(buildHtml(list))
            sb.append("<br />")
            list.clear()
            precursor++
            index = precursor
            //list.add(content[index]);
          } else {
            // properties 一樣，還未到達邊界，直接放入
            list.add(content[precursor])
            precursor++
          }
        } else {
          //發現不一樣的 ColorByte properties，開始讀取 list內容，將 list 內容讀出來，建造出一個 HTML String
          sb.append(buildHtml(list))
          list.clear()
          index = precursor
          list.add(content[index])
          //檢查是否到達邊界
          if ((index + 1) % this.width == 0 && index != content.size - 1) {
            //如果到達邊界 , 則，將「一個」 ColorByte 送入製作成 HTML
            sb.append(buildHtml(list))
            sb.append("<br />")
            list.clear()
            index++
            precursor++

          } else {
            //尚未到達邊界
            precursor++
          }
        }
      }
      //把最後的 cursor 走完
      sb.append(buildHtml(list))
      list.clear()
      sb.append("</div>")
      return sb.toString()
    } //getHtmlOutput


  /**
   * 產生新的畫布，內定背景以空白鍵塗滿
   *
   * @param height 高度
   * @param width  寬度
   */
  constructor(height: Int, width: Int) {
    this.height = height
    this.width = width
    content = Array(width * height) { ColorByte(' ') }
  }


  /**
   * 產生新的畫布，內定以 bgChar 字元填滿整個畫面
   *
   * @param height 高度
   * @param width  寬度
   * @param bgChar 內定以 bgChar 字元填滿整個畫面
   */
  constructor(height: Int, width: Int, bgChar: Char) {
    this.height = height
    this.width = width

    content = Array(width * height) { ColorByte(bgChar) }
  } //constructor


  /** 生新的畫布，以 fill 'String' 填滿整個畫面 ，不指定前景背景顏色  */
  constructor(height: Int, width: Int, fill: String) {
    this.height = height
    this.width = width

    content = fillContent(fill, null, null, height, width)
  }

  /** 生新的畫布，以 fill 'String' 填滿整個畫面 ，指定前景及背景顏色  */
  constructor(height: Int, width: Int, fill: String, foreColor: String?, backColor: String?) {
    this.height = height
    this.width = width

    content = fillContent(fill, foreColor, backColor, height, width)
  }

  private fun fillContent(fill: String,
                          foreColor: String?,
                          backColor: String?,
                          height: Int,
                          width: Int): Array<ColorByte> {
    val bytes: ByteArray = fillingStringToBytes(fill)

    return Array(width * height) {
      val h = it / width
      val w = it % width
      ColorByte(bytes[w % bytes.size], foreColor, backColor, null, null, null)
    }
  }

  private fun fillingStringToBytes(fill: String): ByteArray {
    val strChars = fill.toCharArray()

    return strChars.flatMap { strChar ->
      val byteArray: ByteArray = strChar.toString().toByteArray(charsetBig5)
      byteArray.asIterable()
    }.toByteArray()
  }


  /**
   * 在第 x row , 第 y column , 開始，寫入 彩色文字
   *
   * @param text       要輸入的字串
   * @param x         row index
   * @param y         column index
   * @param foreColor 前景顏色字串，以 16 進位表示，例如 "FFFFCC"
   * @param backColor 背景顏色字串，以 16 進位表示，例如 "FFFFCC"
   * @param txtUrl    網址物件 , 例如 "http://www.google.com.tw"
   * @param title     Title
   * @param wrap      是否換行
   * @param txtFont   字型，例如： new Font("細明體" , Font.PLAIN , 16)
   */
  fun setText(text: String,
              x: Int,
              y: Int,
              foreColor: String? = null,
              backColor: String? = null,
              txtUrl: String? = null,
              title: String? = null,
              wrap: Boolean = false,
              txtFont: Font? = null) {
    var str = text
    var fore = foreColor
    var back = backColor
    var font = txtFont
    var url = txtUrl
    var index = (x - 1) * width + (y - 1)
    var strWidth = 0
    //以 big5 編碼取出 bytes , 一個中文字佔兩個 bytes , 剛好就是英文字母的兩倍 , 可以拿來當作字元寬度
    strWidth = str.toByteArray(charset("Big5")).size

    if (wrap) {
      //如果要換行 , 就檢查是否太長
      //檢查是否會太長 , 如果太長，丟出錯誤訊息
      if (index + strWidth - 1 >= this.content.size
      ) throw RuntimeException("setText() 超出 Canvas 長度")
      else {
        //TODO : 太長，處理換行問題
      }
    } else {
      //如果不換行（切字）
      //int a = y+strWidth-1;
      //System.out.println("a = " + a + " , w = " + w);
      if (y + strWidth - 1 > this.width) {
        //超出，切字
        //byte[] byteArray = str.getBytes();
        //str = new String(byteArray , 0 , (this.w-y+1));
        str = getStringFromBytes(str, this.width - y + 1)
      }
    }


    //處理中文字切字的問題 , 判斷每個 char 「在 Big5 編碼下」佔用多少 bytes 的空間
    //因為如果在 UTF8 的環境下，每個中文字就佔 3-bytes , 這不是我要的
    val strChars = str.toCharArray() //取得傳入字串的 char array

    for (strChar in strChars) {
      val bytes = strChar.toString().toByteArray(charset("Big5"))
      val indexLine = index / this.width + 1
      var precursorLine = 0


      precursorLine = if ((index + bytes.size) % this.width == 0) {
        //剛好到行尾
        indexLine
      } else {
        (index + bytes.size) / this.width + 1
      }

      if (indexLine != precursorLine) {
        //代表有切行的可能，必須把這個 Char 移到下一行，而目前這行的結尾，以空白鍵填入
        //先求出，目前 index 到行尾，要塞入幾個空白鍵（中文字為 1 個）
        val spaces = width - index % this.width
        for (j in index until index + spaces) {
          content[j] = ColorByte(' '.toByte(), fore, back, font, url, title)
        } //填空白
        index += spaces
      }
      for (j in index until index + bytes.size) {
        //如果新加入的背景色為空，檢查原字元的背景色
        if (back == null) {
          back = content[j].getBackColor()
        }
        //如果新加入的前景色為空，檢查原字元的前景色
        if (fore == null) {
          fore = content[j].foreColor
        }
        //如果新加入的字型為空，則檢查原字元的字型
        if (font == null) {
          font = content[j].font
        }
        //如果新加入的 URL 為空，則檢查原字元的網址
        if (url == null) {
          url = content[j].url
        }

        content[j] = ColorByte(bytes[j - index], fore, back, font, url, title)
      }
      index += bytes.size
      if (index >= content.size) {
        break
      }
    }
  } //setText()


  /**
   * <pre>
   * 2010/08/13 Added :
   * 要從 str 中製造出 寬度 為 w 的字串
   * 假設 str = "一二三四五aabbcc" , 強迫寬度為 10 , 就要取前五個字
   * 原始字串的 byteArray 為：
   * [-28, -72, -128, -28, -70, -116, -28, -72, -119, -27, -101, -101, -28, -70, -108, 97, 97, 98, 98, 99, 99]
   * (     一      ） （     二      ）  （    三     ）  （     四     ） （    五     ）   a   a   b   b   c   c
   * 舊的演算法 :
   * byte[] byteArray = str.getBytes();
   * str = new String(byteArray , 0 , (this.w-y+1));
   * 如果按照此演算法，只取前 10 bytes , 則只會取到「四」的第一個 byte (-27) , 並不完整！
   * 必須想辦法，讓他取到「五」的第三個 byte (-108)
   *
   * 新的演算法：把 str 拆成 char[] , 一個一個累加
  </pre> *
   */
  private fun getStringFromBytes(str: String, width: Int): String {
    val chars = str.toCharArray()
    var nowWidth = 0
    var sb = StringBuffer()
    for (aChar in chars) {
      val s = aChar.toString()
      if (s.toByteArray().size == 3
      ) {
        //中文字

        //如果加上字元所佔寬度(2)大於 w , 就不 append 了
        if (nowWidth + 2 > width
        ) {
          break
        }
        sb = sb.append(s)
        //雖然 bytes 長度是 3 , 但是寬度只佔 2
        nowWidth += 2
      } else {
        if (nowWidth + 1 > width
        ) {
          break
        }
        sb = sb.append(s)
        nowWidth++
      }
    }
    return sb.toString()
  }

  /**
   * 將某個子 ColorCanvas 加到這個畫布中
   *
   * @param cc
   * @param x  row index , 1-based
   * @param y  column index , 1-based
   */
  fun add(cc: ColorCanvas, x: Int, y: Int) {
    if (cc.width > this.width || cc.height > this.height
    ) throw RuntimeException(
      "不能把大的 Canvas 塞入小的 Canvas 中 ! 父 Canvas 寬 = " + this.width + " , 高 = " + this.height + " ; 子 Canvas 寬 = " + cc.width + " , 高 = " + cc.height)
    if (y + cc.width - 1 >= this.width + 1
    ) throw RuntimeException(
      "寬度超過 ! 父 Canvas 寬度為 " + this.width + " , 起始加入 y 座標 = " + y + " , 子 Canvas 寬度為 " + cc.width + " , 超出！")
    if (x + cc.height - 1 > this.height
    ) throw RuntimeException(
      "高度超過 ! 父 Canvas 高度為 " + this.height + " , 起始加入 x 座標 = " + x + " , 子 Canvas 高度為 " + cc.height + " , 超出！")

    cc.parent = this
    children.add(Child(cc, x, y))

    //這個步驟，代表把 Child ColorCanvas 的內容，貼到自己的 content 中，取代自己的 content
    this.content = this.contentWithChildren
  }

  /**
   * 新增一行字，到本 Canvas 「有字的最底端」
   */
  fun addLine(str: String,
              wrap: Boolean,
              foreColor: String? = null,
              backColor: String? = null,
              font: Font? = null,
              url: URL? = null) {
    /**
     * 必須先取出來，第幾行開始為空
     * 演算法：由底層數上來，遇到有字，則停止
     */
    val cbs = this.contentWithChildren
    var targetLine = this.height + 1
    for (i in this.height downTo 1) {
      val firstByte = (i - 1) * this.width
      val lastByte = i * width - 1
      val firstCB = cbs[firstByte]

      var foundDifference = false
      for (j in firstByte + 1..lastByte) {
        val cb = cbs[j]
        //走訪此行每個 ColorByte 是否一樣，如果一樣，代表此行為空行
        //FIXME : 這無法處理「全形中文空白背景」，因為一個全形中文被拆成兩個 ColorByte , 其 properties 一定不一樣！
        if (!(firstCB.isSameProperties(cb) && firstCB.byte == cb.byte)) {
          targetLine = i + 1 //此行(row)找到不一樣的字元了，所以要加入 Line，得從下一行開始
          foundDifference = true
          break
        }
      } //走訪此行每個 ColorByte
      if (foundDifference) break
      targetLine = i
    }
    //System.out.println("targetLine = " + targetLine);
    if (targetLine > this.height
    ) throw RuntimeException("錯誤，欲新加入一行，但是最後一行已經有資料了，無法再往下加一行了")

    this.setText(str, targetLine, 1, foreColor, backColor, null, null, wrap, font)
  } //addLine

  /**
   * 附加一串字，到 content 的尾端「之後」，亦即，加高 content
   */
  fun appendLine(str: String,
                 foreColor: String?,
                 backColor: String?,
                 fill: String,
                 font: Font? = null,
                 url: URL? = null) {
    val strWidth: Int = str.toByteArray(charsetBig5).size
    //以 big5 編碼取出 bytes , 一個中文字佔兩個 bytes , 剛好就是英文字母的兩倍 , 可以拿來當作字元寬度

    //ColorByte[] content 必須要加長 (高度, h 增加)

    //需要多少 Rows
    val additionalRows = strWidth / this.width + 1

    val appendedCanvas = ColorCanvas(additionalRows, width, fill, foreColor, backColor)
    appendedCanvas.addLine(str, true, foreColor, backColor, font, url)

    this.height += additionalRows
    content += appendedCanvas.content
  }

  /** 附加一行「空行」到 content 尾端「之後」，亦即，加高 content  */
  fun appendEmptyLine() {
    val appendedCanvas = ColorCanvas(1, width, " ", null, null)
    this.height++
    val newContent = content + appendedCanvas.content
    content = newContent
  }


  private fun buildHtml(list: List<ColorByte>): String {
    val tempSb = StringBuilder()

    val byteArray = list.map { it.byte }.toByteArray()

    val cb = list[0]

    val hasUrl = cb.url != null

    val hasFont: Boolean = cb.font != null || cb.foreColor != null || cb.getBackColor() != null || cb.title != null

    if (hasUrl && !hasFont) {
      //只有網址
      tempSb.append("<a href=\"").append(cb.url).append("\" target=\"_blank\">").append(String(byteArray, charsetBig5)).append("</a>")
    } else if (!hasUrl && hasFont) {
      //只有字型
      tempSb.append(buildFontHtml(cb, byteArray))
    } else if (hasUrl && hasFont) {
      //有網址也有字型
      tempSb.append("<a href=\"").append(cb.url).append("\" target=\"_blank\">")
      tempSb.append(buildFontHtml(cb, byteArray))
      tempSb.append("</a>")
    } else {
      //沒有網址，也沒有字型變化
      tempSb.append(String(byteArray, Charset.forName("Big5")))
    }
    return tempSb.toString()
  }

  private fun buildFontHtml(cb: ColorByte, byteArray: ByteArray): StringBuffer {
    val sb = StringBuffer()
    sb.append("<span ")
    sb.append("style=\"")
    //    sb.append("white-space: pre; ");

    if (cb.foreColor != null) {
      sb.append("color:").append(cb.foreColor).append("; ")
    }
    if (cb.getBackColor() != null) {
      sb.append("background-color:").append(cb.getBackColor()).append("; ")
    }
    if (cb.font != null) {
      sb.append("font-family:").append(cb.font.family).append("; ")
    }

    sb.append("\"")

    //檢查是否有 title
    if (cb.title != null) {
      sb.append(" title=\"")
      sb.append(cb.title)
      sb.append("\"")
    }

    sb.append(">")
    sb.append(String(byteArray, charsetBig5))
    sb.append("</span>")
    return sb
  } //buildFontHtml()


  /**
   * 省略所有 Color / Font / URL
   * 純粹輸出 byte 內容
   */
  fun getTextOutput(): String {
    val cbs = this.contentWithChildren

    return (1..height).joinToString("\n") { i ->
      val byteArray = (0 until width).map { j ->
        cbs[(i - 1) * width + j].byte
      }.toByteArray()
      String(byteArray, 0, width, charsetBig5)
    }

  }

  /**
   * 省略所有 Color / Font / URL
   * 純粹輸出 byte 內容
   */
  override fun toString(): String {
    return getTextOutput()
  }

  companion object {
    val charsetBig5: Charset = Charset.forName("Big5")
  }
} //class
