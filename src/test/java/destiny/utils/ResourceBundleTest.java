/**
 * @author smallufo
 * Created on 2011/5/3 at 上午7:49:41
 */
package destiny.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Test;

public class ResourceBundleTest
{

  @Test
  public void testResource()
  {
    ResourceBundle rb = ResourceBundle.getBundle("destiny.utils.testBundle" , Locale.CANADA_FRENCH);
    String pattern = rb.getString("hello");
    String result = MessageFormat.format(pattern, "smallufo" , "test");
    System.out.println("result = " + result);
        
  }
}
