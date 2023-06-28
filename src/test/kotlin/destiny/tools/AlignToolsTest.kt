/**
 * @author smallufo
 * Created on 2008/2/20 at 上午 12:40:10
 */
package destiny.tools

import org.apache.commons.lang3.StringUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class AlignToolsTest {
  
  @Test
  fun testAlignCenter() {
    var value= 12
    assertEquals("12", AlignTools.alignCenter(value, 2))
    assertEquals("12", StringUtils.center("12", 2, ' '))

    assertEquals(" 12 ", AlignTools.alignCenter(value, 4))
    assertEquals(" 12 ", StringUtils.center("12", 4, ' '))

    assertEquals("　12　", AlignTools.alignCenter(value, 6))
    assertEquals("  12  ", StringUtils.center("12", 6, ' '))

    assertEquals("　 12　 ", AlignTools.alignCenter(value, 8))
    assertEquals("   12   ", StringUtils.center("12", 8, ' '))

    assertEquals("　　12　　", AlignTools.alignCenter(value, 10))
    assertEquals("    12    ", StringUtils.center("12", 10, ' '))

    //assertEquals(" 12", StringUtils.center("12", 3, ' ')); // failed , 此演算法與 apache 不同
    assertEquals(" 12", AlignTools.alignCenter(value, 3))
    assertEquals("　12 ", AlignTools.alignCenter(value, 5))
    assertEquals("　 12　", AlignTools.alignCenter(value, 7))
    assertEquals("　　12　 ", AlignTools.alignCenter(value, 9))


    assertEquals("　　12　　", AlignTools.alignCenter(value, 10))
    value = 123
    assertEquals("　 123　　", AlignTools.alignCenter(value, 10))
    value = 1234
    assertEquals("　 1234　 ", AlignTools.alignCenter(value, 10))

    value = 12
    assertEquals("　　 12　　", AlignTools.alignCenter(value, 11))
    value = 123
    assertEquals("　　123　　", AlignTools.alignCenter(value, 11))
    value = 1234
    assertEquals("　　1234　 ", AlignTools.alignCenter(value, 11))
    value = 12345
    assertEquals("　 12345　 ", AlignTools.alignCenter(value, 11))
  }


  @Test
  fun testAlignRightInt() {
    assertEquals("2018", AlignTools.alignRight(2018, 4 , true))
    assertEquals(" 2018", AlignTools.alignRight(2018, 5 , true))
    assertEquals("　2018", AlignTools.alignRight(2018, 6, true))
    assertEquals("　 2018", AlignTools.alignRight(2018, 7 , true))
    assertEquals("　　2018", AlignTools.alignRight(2018, 8, true))
    assertEquals("　　　 1", AlignTools.alignRight(1, 8 , true))
  }

  @Test
  fun testAlignRightDouble() {
    assertEquals("0.12", AlignTools.alignRight(0.123456, 4, ' '))
    assertEquals("0.12", StringUtils.left("0.123456", 4))

    assertEquals("  0.12", AlignTools.alignRight(0.12, 6, ' '))
    assertEquals("  0.12", StringUtils.leftPad("0.12", 6, ' '))


    assertEquals("0.123456" , AlignTools.alignRight(0.123456, 8))

    assertEquals("0.1234" , AlignTools.alignRight(0.123456, 6))
    assertEquals("0.1234" , AlignTools.alignRight(0.1234, 6))
    assertEquals("  0.12" , AlignTools.alignRight(0.12, 6))
    assertEquals("000.12" , AlignTools.alignRight(0.12, 6, '0'))


    assertEquals("-0.123" , AlignTools.alignRight(-0.123456, 6))
    assertEquals("-0.123" , AlignTools.alignRight(-0.123, 6))
    assertEquals("  -0.1" , AlignTools.alignRight(-0.1, 6))

  }

  @Test
  fun testApacheStringUtilsLeftPad() {
    assertEquals("1234", StringUtils.leftPad("1234", 4, ' '))
    assertEquals("1234", AlignTools.leftPad("1234", 4, ' '))

    assertEquals("  1234", StringUtils.leftPad("1234", 6, ' '))
    assertEquals("  1234", AlignTools.leftPad("1234", 6, ' '))

    assertEquals("xx1234", StringUtils.leftPad("1234", 6, 'x'))
    assertEquals("xx1234", AlignTools.leftPad("1234", 6, 'x'))

    assertEquals("   1", StringUtils.leftPad("1", 4, ' '))
    assertEquals("   1", AlignTools.leftPad("1", 4, ' '))

    assertEquals("   0", StringUtils.leftPad("0", 4, ' '))
    assertEquals("   0", AlignTools.leftPad("0", 4, ' '))

    assertEquals("-123", StringUtils.leftPad("-123", 4, ' '))
    assertEquals("-123", AlignTools.leftPad("-123", 4, ' '))
  }
}
