/**
 * Created by smallufo on 2019-05-23.
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame


class RsmiTest {

  @Test
  fun testString() {

    println(ResourceBundle.getBundle("destiny.astrology.Rsmi", Locale("en" , "US")).getString("Rsmi.RISING"))
    assertEquals("Rising" , Rsmi.RISING.toString(Locale.ENGLISH))
    assertEquals("AC" , Rsmi.RISING.getAbbreviation(Locale.ENGLISH))
    assertEquals("東昇" , Rsmi.RISING.toString(Locale.TRADITIONAL_CHINESE))
    assertEquals("東昇" , Rsmi.RISING.toString(Locale.TAIWAN))
    assertEquals("昇" , Rsmi.RISING.getAbbreviation(Locale.TAIWAN))
    assertEquals("昇" , Rsmi.RISING.abbreviation)


    Rsmi.array.forEach { p ->
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