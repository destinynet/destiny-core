/**
 * @author smallufo
 * Created on 2010/12/15 at 下午4:08:15
 */
package destiny.tools

import destiny.tools.ClassUtils.PERMISSION
import destiny.tools.canvas.ColorCanvas
import kotlin.test.Test
import kotlin.test.assertTrue

class ClassUtilsTest {

  @Test
  fun testGetProperties() {
    ClassUtils.getProperties(ColorCanvas::class.java, PERMISSION.READABLE).forEach {
      println(it)
    }
  }

  @Test
  fun testIsWritable() {
    assertTrue(!ClassUtils.isWritable(ColorCanvas::class.java, "w"))
  }
}
