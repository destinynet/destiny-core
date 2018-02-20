/**
 * @author smallufo
 * @date 2005/4/4
 * @time 下午 03:05:36
 */
package destiny.tools.canvas

import java.awt.Font
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorByteTest {
  @Test
  fun testColorByte() {
    var cb: ColorByte

    //不變
    cb = ColorByte(1.toByte(), "#FFFFFF", "red", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertEquals(cb.foreColor, "#FFFFFF")

    //不變
    cb = ColorByte(1.toByte(), "#FFF", "red", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertEquals(cb.foreColor, "#FFF")

    //小寫轉大寫
    cb = ColorByte(1.toByte(), "#FFFFFF", "red", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertEquals(cb.foreColor, "#FFFFFF")

    //小寫轉成大寫，並在前加上 '#'
    cb = ColorByte(1.toByte(), "ffffff", "red", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertEquals(cb.foreColor, "#FFFFFF")

    //小寫轉成大寫
    cb = ColorByte(1.toByte(), "#FFF", "red", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertEquals(cb.foreColor, "#FFF")

    //小寫轉成大寫，並在前加上 '#'
    cb = ColorByte(1.toByte(), "fff", "red", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertEquals(cb.foreColor, "#FFF")

    //錯誤的色碼
    cb = ColorByte(1.toByte(), "#ffffff", "red", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertEquals(cb.foreColor, "#FFFFFF")

    //錯誤的色碼
    cb = ColorByte(1.toByte(), "#fffff", "red", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertEquals("#FFFFF" , cb.foreColor)

  }

  @Test
  fun testIsSameProperties() {
    var cb1: ColorByte
    var cb2: ColorByte
    //應該相同
    cb1 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertTrue(cb1.isSameProperties(cb2))

    //應該相同
    cb1 = ColorByte(1.toByte(), "ffffff", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "ffffff", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertTrue(cb1.isSameProperties(cb2))

    //應該相同
    cb1 = ColorByte(1.toByte(), "abc", "DEF", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "ABC", "def", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertTrue(cb1.isSameProperties(cb2))

    //前景不同
    cb1 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "green", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //背景不同
    cb1 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "red", "black", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //字體family不同
    cb1 = ColorByte(1.toByte(), "red", "blue", Font("標楷體", Font.PLAIN, 16), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //字體型態不同
    cb1 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.ITALIC, 16), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //字體大小不同
    cb1 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 14), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //網址不同 , 但是網址不納入比對 (耗時)，因此會視為相同
    cb1 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to", null)
    cb2 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to/", null)
    assertTrue(!cb1.isSameProperties(cb2))

    //title 不同
    cb1 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to/", "AAA")
    cb2 = ColorByte(1.toByte(), "red", "blue", Font("細明體", Font.PLAIN, 16), "http://destiny.to/", "BBB")
    assertTrue(!cb1.isSameProperties(cb2))
  }

}
