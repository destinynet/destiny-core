package destiny.core

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals


class GenderDecoratorTest {

  @Test
  fun testDecorator() {

    assertEquals("男", GenderDecorator.getOutputString(Gender.男, Locale.TRADITIONAL_CHINESE))
    assertEquals("女", GenderDecorator.getOutputString(Gender.女, Locale.TRADITIONAL_CHINESE))

    assertEquals("男", GenderDecorator.getOutputString(Gender.男, Locale.SIMPLIFIED_CHINESE))
    assertEquals("女", GenderDecorator.getOutputString(Gender.女, Locale.SIMPLIFIED_CHINESE))

    assertEquals("Male", GenderDecorator.getOutputString(Gender.男, Locale.ENGLISH))
    assertEquals("Female", GenderDecorator.getOutputString(Gender.女, Locale.ENGLISH))

    assertEquals("男", GenderDecorator.getOutputString(Gender.男, Locale.FRANCE))
    assertEquals("女", GenderDecorator.getOutputString(Gender.女, Locale.FRANCE))
  }

}