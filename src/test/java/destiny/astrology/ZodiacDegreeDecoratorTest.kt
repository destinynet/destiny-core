/**
 * Created by smallufo on 2017-12-13.
 */
package destiny.astrology

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ZodiacDegreeDecoratorTest {

  @Test
  fun getOutputString() {
    assertEquals("牡羊 00度 00分 00秒", ZodiacDegreeDecorator.getOutputString(0.0  , Locale.TAIWAN))
    assertEquals("牡羊 01度 00分 00秒", ZodiacDegreeDecorator.getOutputString(1.0  , Locale.TAIWAN))
    assertEquals("牡羊 29度 00分 00秒", ZodiacDegreeDecorator.getOutputString(29.0 , Locale.TAIWAN))
    assertEquals("天秤 00度 00分 00秒", ZodiacDegreeDecorator.getOutputString(180.0, Locale.TAIWAN))
    assertEquals("雙魚 29度 00分 00秒", ZodiacDegreeDecorator.getOutputString(359.0, Locale.TAIWAN))

    assertEquals("00牡00" , ZodiacDegreeDecorator.getSimpOutputString(0.0  , Locale.JAPANESE))
    assertEquals("01牡00" , ZodiacDegreeDecorator.getSimpOutputString(1.0  , Locale.JAPANESE))
    assertEquals("29牡00" , ZodiacDegreeDecorator.getSimpOutputString(29.0 , Locale.JAPANESE))
    assertEquals("00秤00" , ZodiacDegreeDecorator.getSimpOutputString(180.0, Locale.JAPANESE))
    assertEquals("29魚00" , ZodiacDegreeDecorator.getSimpOutputString(359.0, Locale.JAPANESE))

    assertEquals("Aries 0Deg 0Min 0.0Sec" , ZodiacDegreeDecorator.getOutputString(0.0, Locale.ENGLISH))
    assertEquals("Aries 1Deg 0Min 0.0Sec" , ZodiacDegreeDecorator.getOutputString(1.0, Locale.ENGLISH))
    assertEquals("Aries 29Deg 0Min 0.0Sec" , ZodiacDegreeDecorator.getOutputString(29.0, Locale.ENGLISH))
    assertEquals("Libra 0Deg 0Min 0.0Sec" , ZodiacDegreeDecorator.getOutputString(180.0, Locale.ENGLISH))
    assertEquals("Pisces 29Deg 0Min 0.0Sec" , ZodiacDegreeDecorator.getOutputString(359.0, Locale.ENGLISH))
  }
}