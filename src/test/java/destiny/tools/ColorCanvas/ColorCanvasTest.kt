package destiny.tools.ColorCanvas

import kotlin.test.Test
import kotlin.test.assertEquals

class ColorCanvasTest {

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
    cc.addLine("六七八九十", null, null, null, null, false)
    assertEquals("一二三四五\n六七八九十\n", cc.toString())
  }

  @Test
  fun testAppendLine() {
    val cc = ColorCanvas(2, 10, " ")
    cc.setText("一二三四五", 1, 1)
    cc.setText("六七八九十", 2, 1)
    cc.appendLine("測試隨便亂加行12rewerwrd哈哈", null, null, " ", null, null)
    println(cc.toString())
  }
}
