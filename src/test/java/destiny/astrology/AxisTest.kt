/**
 * Created by smallufo on 2019-05-23.
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame


class AxisTest {

  @Test
  fun testString() {

    println(ResourceBundle.getBundle("destiny.astrology.Axis", Locale("en" , "US")).getString("Axis.RISING"))
    assertEquals("Rising" , Axis.RISING.toString(Locale.ENGLISH))
    assertEquals("AC" , Axis.RISING.getAbbreviation(Locale.ENGLISH))
    assertEquals("東昇" , Axis.RISING.toString(Locale.TRADITIONAL_CHINESE))
    assertEquals("東昇" , Axis.RISING.toString(Locale.TAIWAN))
    assertEquals("昇" , Axis.RISING.getAbbreviation(Locale.TAIWAN))
    assertEquals("昇" , Axis.RISING.getAbbreviation(Locale.TAIWAN))


    Axis.array.forEach { p ->
      p.toString().also {
        assertNotNull(it)
        println(it + "->" + p.getAbbreviation(Locale.getDefault()))
        assertNotSame('!', it[0])
      }


      p.toString(Locale.ENGLISH).also {
        assertNotNull(it)
        println(it + "->" + p.getAbbreviation(Locale.ENGLISH))
        assertNotSame('!', it[0])
      }
    }
  }
}