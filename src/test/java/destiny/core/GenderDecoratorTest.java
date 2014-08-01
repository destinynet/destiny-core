package destiny.core;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;


public class GenderDecoratorTest {

  @Test
  public void testDecorator() {

    assertEquals("男" , GenderDecorator.getOutputString(Gender.男 , Locale.TRADITIONAL_CHINESE));
    assertEquals("女" , GenderDecorator.getOutputString(Gender.女 , Locale.TRADITIONAL_CHINESE));

    assertEquals("男" , GenderDecorator.getOutputString(Gender.男 , Locale.SIMPLIFIED_CHINESE));
    assertEquals("女" , GenderDecorator.getOutputString(Gender.女 , Locale.SIMPLIFIED_CHINESE));

    assertEquals("Male"   , GenderDecorator.getOutputString(Gender.男 , Locale.ENGLISH));
    assertEquals("Female" , GenderDecorator.getOutputString(Gender.女 , Locale.ENGLISH));

    assertEquals("男" , GenderDecorator.getOutputString(Gender.男 , Locale.FRANCE));
    assertEquals("女" , GenderDecorator.getOutputString(Gender.女 , Locale.FRANCE));
  }

}