package destiny.tools.canvas

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorCanvasTest {

  @Test
  fun testCanvas() {
    val cc = ColorCanvas(9, 50, "." , "#ffffcc" , "000")
    cc.add(ColorCanvas(7 , 5 , "X" , "red" , "white") , 2 , 4)
    cc.add(ColorCanvas(2 , 5 , "ABC" , "red" , "white") , 4 , 6)
    cc.add(ColorCanvas(3 , 8 , "哈囉" , "red" , "white") , 4 , 40)
    println(cc)
  }

  @Test
  fun testColorCanvas() {
    val cc = ColorCanvas(1, 10, "　")
    cc.setText("一二三四五", 1, 1)
    assertEquals("一二三四五", cc.toString().trim { it <= ' ' })
    cc.setText("一二a三aabbcc", 1, 1)
    assertEquals("一二a三aab", cc.toString().trim { it <= ' ' })

    cc.setText("aabbccdd一二", 1, 1)
    assertEquals("aabbccdd一", cc.toString().trim { it <= ' ' })
  }

  @Test
  fun testAddLine() {
    val cc = ColorCanvas(2, 10, " ") //FIXME addLine() 只能測試背景是「非中文」的字元
    cc.setText("一二三四五", 1, 1)
    cc.addLine("六七八九十", false)
    assertEquals("一二三四五\n六七八九十", cc.toString())
  }

  @Test
  fun testAppendLine() {
    val cc = ColorCanvas(2, 10, " ")
    cc.setText("一二三四五", 1, 1)
    cc.setText("六七八九十", 2, 1)
    cc.appendLine("許功蓋許功蓋許功蓋", null, null, " ")
    cc.appendLine("许功盖许功盖许功盖", null, null, " ")
    println(cc.toString())
  }
}
